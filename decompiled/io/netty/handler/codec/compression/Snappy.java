package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.Arrays;

public final class Snappy {
   private static final int MAX_HT_SIZE = 16384;
   private static final int MIN_COMPRESSIBLE_BYTES = 15;
   private static final int PREAMBLE_NOT_FULL = -1;
   private static final int NOT_ENOUGH_INPUT = -1;
   private static final int LITERAL = 0;
   private static final int COPY_1_BYTE_OFFSET = 1;
   private static final int COPY_2_BYTE_OFFSET = 2;
   private static final int COPY_4_BYTE_OFFSET = 3;
   private static final FastThreadLocal<short[]> HASH_TABLE = new FastThreadLocal<>();
   private static final boolean DEFAULT_REUSE_HASHTABLE = SystemPropertyUtil.getBoolean("io.netty.handler.codec.compression.snappy.reuseHashTable", false);
   private final boolean reuseHashtable;
   private Snappy.State state = Snappy.State.READING_PREAMBLE;
   private byte tag;
   private int written;

   public Snappy() {
      this(DEFAULT_REUSE_HASHTABLE);
   }

   Snappy(boolean reuseHashtable) {
      this.reuseHashtable = reuseHashtable;
   }

   public static Snappy withHashTableReuse() {
      return new Snappy(true);
   }

   public void reset() {
      this.state = Snappy.State.READING_PREAMBLE;
      this.tag = 0;
      this.written = 0;
   }

   public void encode(ByteBuf in, ByteBuf out, int length) {
      int i = 0;

      while (true) {
         int b = length >>> i * 7;
         if ((b & -128) == 0) {
            out.writeByte(b);
            i = in.readerIndex();
            b = i;
            int hashTableSize = MathUtil.findNextPositivePowerOfTwo(length);
            hashTableSize = Math.min(hashTableSize, 16384);
            short[] table = this.getHashTable(hashTableSize);
            int shift = Integer.numberOfLeadingZeros(hashTableSize) + 1;
            int nextEmit = i;
            if (length - i >= 15) {
               int nextHash = hash(in, ++i, shift);

               label38:
               while (true) {
                  int skip = 32;
                  int nextIndex = i;

                  int candidate;
                  do {
                     i = nextIndex;
                     int hash = nextHash;
                     int bytesBetweenHashLookups = skip++ >> 5;
                     nextIndex += bytesBetweenHashLookups;
                     if (nextIndex > length - 4) {
                        break label38;
                     }

                     nextHash = hash(in, nextIndex, shift);
                     candidate = b + (table[hash] & '\uffff');
                     table[hash] = (short)(i - b);
                  } while (in.getInt(i) != in.getInt(candidate));

                  encodeLiteral(in, out, i - nextEmit);

                  int insertTail;
                  do {
                     int base = i;
                     int matched = 4 + findMatchingLength(in, candidate + 4, i + 4, length);
                     i += matched;
                     int offset = base - candidate;
                     encodeCopy(out, offset, matched);
                     in.readerIndex(in.readerIndex() + matched);
                     insertTail = i - 1;
                     nextEmit = i;
                     if (i >= length - 4) {
                        break label38;
                     }

                     int prevHash = hash(in, insertTail, shift);
                     table[prevHash] = (short)(i - b - 1);
                     int currentHash = hash(in, insertTail + 1, shift);
                     candidate = b + (table[currentHash] & '\uffff');
                     table[currentHash] = (short)(i - b);
                  } while (in.getInt(insertTail + 1) == in.getInt(candidate));

                  nextHash = hash(in, insertTail + 2, shift);
                  i++;
               }
            }

            if (nextEmit < length) {
               encodeLiteral(in, out, length - nextEmit);
            }

            return;
         }

         out.writeByte(b & 127 | 128);
         i++;
      }
   }

   private static int hash(ByteBuf in, int index, int shift) {
      return in.getInt(index) * 506832829 >>> shift;
   }

   private short[] getHashTable(int hashTableSize) {
      return this.reuseHashtable ? getHashTableFastThreadLocalArrayFill(hashTableSize) : new short[hashTableSize];
   }

   public static short[] getHashTableFastThreadLocalArrayFill(int hashTableSize) {
      short[] hashTable = HASH_TABLE.get();
      if (hashTable != null && hashTable.length >= hashTableSize) {
         Arrays.fill(hashTable, 0, hashTableSize, (short)0);
         return hashTable;
      } else {
         hashTable = new short[hashTableSize];
         HASH_TABLE.set(hashTable);
         return hashTable;
      }
   }

   private static int findMatchingLength(ByteBuf in, int minIndex, int inIndex, int maxIndex) {
      int matched;
      for (matched = 0; inIndex <= maxIndex - 4 && in.getInt(inIndex) == in.getInt(minIndex + matched); matched += 4) {
         inIndex += 4;
      }

      while (inIndex < maxIndex && in.getByte(minIndex + matched) == in.getByte(inIndex)) {
         inIndex++;
         matched++;
      }

      return matched;
   }

   private static int bitsToEncode(int value) {
      int highestOneBit = Integer.highestOneBit(value);
      int bitLength = 0;

      while ((highestOneBit >>= 1) != 0) {
         bitLength++;
      }

      return bitLength;
   }

   static void encodeLiteral(ByteBuf in, ByteBuf out, int length) {
      if (length < 61) {
         out.writeByte(length - 1 << 2);
      } else {
         int bitLength = bitsToEncode(length - 1);
         int bytesToEncode = 1 + bitLength / 8;
         out.writeByte(59 + bytesToEncode << 2);

         for (int i = 0; i < bytesToEncode; i++) {
            out.writeByte(length - 1 >> i * 8 & 0xFF);
         }
      }

      out.writeBytes(in, length);
   }

   private static void encodeCopyWithOffset(ByteBuf out, int offset, int length) {
      if (length < 12 && offset < 2048) {
         out.writeByte(1 | length - 4 << 2 | offset >> 8 << 5);
         out.writeByte(offset & 0xFF);
      } else {
         out.writeByte(2 | length - 1 << 2);
         out.writeByte(offset & 0xFF);
         out.writeByte(offset >> 8 & 0xFF);
      }
   }

   private static void encodeCopy(ByteBuf out, int offset, int length) {
      while (length >= 68) {
         encodeCopyWithOffset(out, offset, 64);
         length -= 64;
      }

      if (length > 64) {
         encodeCopyWithOffset(out, offset, 60);
         length -= 60;
      }

      encodeCopyWithOffset(out, offset, length);
   }

   public void decode(ByteBuf in, ByteBuf out) {
      while (in.isReadable()) {
         switch (this.state) {
            case READING_PREAMBLE:
               int uncompressedLength = readPreamble(in);
               if (uncompressedLength == -1) {
                  return;
               }

               if (uncompressedLength == 0) {
                  return;
               }

               out.ensureWritable(uncompressedLength);
               this.state = Snappy.State.READING_TAG;
            case READING_TAG:
               if (!in.isReadable()) {
                  return;
               }

               this.tag = in.readByte();
               switch (this.tag & 3) {
                  case 0:
                     this.state = Snappy.State.READING_LITERAL;
                     continue;
                  case 1:
                  case 2:
                  case 3:
                     this.state = Snappy.State.READING_COPY;
                  default:
                     continue;
               }
            case READING_LITERAL:
               int literalWritten = decodeLiteral(this.tag, in, out);
               if (literalWritten != -1) {
                  this.state = Snappy.State.READING_TAG;
                  this.written += literalWritten;
                  break;
               }

               return;
            case READING_COPY:
               switch (this.tag & 3) {
                  case 1:
                     int decodeWritten = decodeCopyWith1ByteOffset(this.tag, in, out, this.written);
                     if (decodeWritten != -1) {
                        this.state = Snappy.State.READING_TAG;
                        this.written += decodeWritten;
                        break;
                     }

                     return;
                  case 2:
                     int decodeWritten = decodeCopyWith2ByteOffset(this.tag, in, out, this.written);
                     if (decodeWritten != -1) {
                        this.state = Snappy.State.READING_TAG;
                        this.written += decodeWritten;
                        break;
                     }

                     return;
                  case 3:
                     int decodeWritten = decodeCopyWith4ByteOffset(this.tag, in, out, this.written);
                     if (decodeWritten == -1) {
                        return;
                     }

                     this.state = Snappy.State.READING_TAG;
                     this.written += decodeWritten;
               }
         }
      }
   }

   private static int readPreamble(ByteBuf in) {
      int length = 0;
      int byteIndex = 0;

      while (in.isReadable()) {
         int current = in.readUnsignedByte();
         length |= (current & 127) << byteIndex++ * 7;
         if ((current & 128) == 0) {
            return length;
         }

         if (byteIndex >= 4) {
            throw new DecompressionException("Preamble is greater than 4 bytes");
         }
      }

      return 0;
   }

   int getPreamble(ByteBuf in) {
      if (this.state == Snappy.State.READING_PREAMBLE) {
         int readerIndex = in.readerIndex();

         int var3;
         try {
            var3 = readPreamble(in);
         } finally {
            in.readerIndex(readerIndex);
         }

         return var3;
      } else {
         return 0;
      }
   }

   static int decodeLiteral(byte tag, ByteBuf in, ByteBuf out) {
      in.markReaderIndex();
      int length;
      switch (tag >> 2 & 63) {
         case 60:
            if (!in.isReadable()) {
               return -1;
            }

            length = in.readUnsignedByte();
            break;
         case 61:
            if (in.readableBytes() < 2) {
               return -1;
            }

            length = in.readUnsignedShortLE();
            break;
         case 62:
            if (in.readableBytes() < 3) {
               return -1;
            }

            length = in.readUnsignedMediumLE();
            break;
         case 63:
            if (in.readableBytes() < 4) {
               return -1;
            }

            length = in.readIntLE();
            break;
         default:
            length = tag >> 2 & 63;
      }

      if (in.readableBytes() < ++length) {
         in.resetReaderIndex();
         return -1;
      } else {
         out.writeBytes(in, length);
         return length;
      }
   }

   private static int decodeCopyWith1ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
      if (!in.isReadable()) {
         return -1;
      } else {
         int initialIndex = out.writerIndex();
         int length = 4 + ((tag & 28) >> 2);
         int offset = (tag & 224) << 8 >> 5 | in.readUnsignedByte();
         validateOffset(offset, writtenSoFar);
         out.markReaderIndex();
         if (offset < length) {
            for (int copies = length / offset; copies > 0; copies--) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, offset);
            }

            if (length % offset != 0) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, length % offset);
            }
         } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
         }

         out.resetReaderIndex();
         return length;
      }
   }

   private static int decodeCopyWith2ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
      if (in.readableBytes() < 2) {
         return -1;
      } else {
         int initialIndex = out.writerIndex();
         int length = 1 + (tag >> 2 & 63);
         int offset = in.readUnsignedShortLE();
         validateOffset(offset, writtenSoFar);
         out.markReaderIndex();
         if (offset < length) {
            for (int copies = length / offset; copies > 0; copies--) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, offset);
            }

            if (length % offset != 0) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, length % offset);
            }
         } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
         }

         out.resetReaderIndex();
         return length;
      }
   }

   private static int decodeCopyWith4ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
      if (in.readableBytes() < 4) {
         return -1;
      } else {
         int initialIndex = out.writerIndex();
         int length = 1 + (tag >> 2 & 63);
         int offset = in.readIntLE();
         validateOffset(offset, writtenSoFar);
         out.markReaderIndex();
         if (offset < length) {
            for (int copies = length / offset; copies > 0; copies--) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, offset);
            }

            if (length % offset != 0) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, length % offset);
            }
         } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
         }

         out.resetReaderIndex();
         return length;
      }
   }

   private static void validateOffset(int offset, int chunkSizeSoFar) {
      if (offset == 0) {
         throw new DecompressionException("Offset is less than minimum permissible value");
      } else if (offset < 0) {
         throw new DecompressionException("Offset is greater than maximum value supported by this implementation");
      } else if (offset > chunkSizeSoFar) {
         throw new DecompressionException("Offset exceeds size of chunk");
      }
   }

   static int calculateChecksum(ByteBuf data) {
      return calculateChecksum(data, data.readerIndex(), data.readableBytes());
   }

   static int calculateChecksum(ByteBuf data, int offset, int length) {
      Crc32c crc32 = new Crc32c();

      int var4;
      try {
         crc32.update(data, offset, length);
         var4 = maskChecksum(crc32.getValue());
      } finally {
         crc32.reset();
      }

      return var4;
   }

   static void validateChecksum(int expectedChecksum, ByteBuf data) {
      validateChecksum(expectedChecksum, data, data.readerIndex(), data.readableBytes());
   }

   static void validateChecksum(int expectedChecksum, ByteBuf data, int offset, int length) {
      int actualChecksum = calculateChecksum(data, offset, length);
      if (actualChecksum != expectedChecksum) {
         throw new DecompressionException(
            "mismatching checksum: " + Integer.toHexString(actualChecksum) + " (expected: " + Integer.toHexString(expectedChecksum) + ')'
         );
      }
   }

   static int maskChecksum(long checksum) {
      return (int)((checksum >> 15 | checksum << 17) + -1568478504L);
   }

   private static enum State {
      READING_PREAMBLE,
      READING_TAG,
      READING_LITERAL,
      READING_COPY;
   }
}
