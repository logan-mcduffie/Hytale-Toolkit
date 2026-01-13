package com.google.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CodedInputStream {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private static final int DEFAULT_SIZE_LIMIT = Integer.MAX_VALUE;
   private static volatile int defaultRecursionLimit = 100;
   int messageDepth;
   int groupDepth;
   int recursionLimit = defaultRecursionLimit;
   int sizeLimit = Integer.MAX_VALUE;
   Object wrapper;
   private boolean shouldDiscardUnknownFields = false;

   public static CodedInputStream newInstance(final InputStream input) {
      return newInstance(input, 4096);
   }

   public static CodedInputStream newInstance(final InputStream input, int bufferSize) {
      if (bufferSize <= 0) {
         throw new IllegalArgumentException("bufferSize must be > 0");
      } else {
         return (CodedInputStream)(input == null ? newInstance(Internal.EMPTY_BYTE_ARRAY) : new CodedInputStream.StreamDecoder(input, bufferSize));
      }
   }

   public static CodedInputStream newInstance(final Iterable<ByteBuffer> input) {
      return !CodedInputStream.UnsafeDirectNioDecoder.isSupported() ? newInstance(new IterableByteBufferInputStream(input)) : newInstance(input, false);
   }

   static CodedInputStream newInstance(final Iterable<ByteBuffer> bufs, final boolean bufferIsImmutable) {
      int flag = 0;
      int totalSize = 0;

      for (ByteBuffer buf : bufs) {
         totalSize += buf.remaining();
         if (buf.hasArray()) {
            flag |= 1;
         } else if (buf.isDirect()) {
            flag |= 2;
         } else {
            flag |= 4;
         }
      }

      return (CodedInputStream)(flag == 2
         ? new CodedInputStream.IterableDirectByteBufferDecoder(bufs, totalSize, bufferIsImmutable)
         : newInstance(new IterableByteBufferInputStream(bufs)));
   }

   public static CodedInputStream newInstance(final byte[] buf) {
      return newInstance(buf, 0, buf.length);
   }

   public static CodedInputStream newInstance(final byte[] buf, final int off, final int len) {
      return newInstance(buf, off, len, false);
   }

   static CodedInputStream newInstance(final byte[] buf, final int off, final int len, final boolean bufferIsImmutable) {
      CodedInputStream.ArrayDecoder result = new CodedInputStream.ArrayDecoder(buf, off, len, bufferIsImmutable);

      try {
         result.pushLimit(len);
         return result;
      } catch (InvalidProtocolBufferException var6) {
         throw new IllegalArgumentException(var6);
      }
   }

   public static CodedInputStream newInstance(ByteBuffer buf) {
      return newInstance(buf, false);
   }

   static CodedInputStream newInstance(ByteBuffer buf, boolean bufferIsImmutable) {
      if (buf.hasArray()) {
         return newInstance(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining(), bufferIsImmutable);
      } else if (buf.isDirect() && CodedInputStream.UnsafeDirectNioDecoder.isSupported()) {
         return new CodedInputStream.UnsafeDirectNioDecoder(buf, bufferIsImmutable);
      } else {
         byte[] buffer = new byte[buf.remaining()];
         buf.duplicate().get(buffer);
         return newInstance(buffer, 0, buffer.length, true);
      }
   }

   public void checkRecursionLimit() throws InvalidProtocolBufferException {
      if (this.messageDepth + this.groupDepth >= this.recursionLimit) {
         throw InvalidProtocolBufferException.recursionLimitExceeded();
      }
   }

   public void checkValidEndTag() throws InvalidProtocolBufferException {
      if (this.groupDepth == 0) {
         this.checkLastTagWas(0);
      }
   }

   private CodedInputStream() {
   }

   public abstract int readTag() throws IOException;

   public abstract void checkLastTagWas(final int value) throws InvalidProtocolBufferException;

   public abstract int getLastTag();

   public abstract boolean skipField(final int tag) throws IOException;

   @Deprecated
   public abstract boolean skipField(final int tag, final CodedOutputStream output) throws IOException;

   public void skipMessage() throws IOException {
      boolean fieldSkipped;
      do {
         int tag = this.readTag();
         if (tag == 0) {
            return;
         }

         this.checkRecursionLimit();
         this.groupDepth++;
         fieldSkipped = this.skipField(tag);
         this.groupDepth--;
      } while (fieldSkipped);
   }

   public void skipMessage(CodedOutputStream output) throws IOException {
      boolean fieldSkipped;
      do {
         int tag = this.readTag();
         if (tag == 0) {
            return;
         }

         this.checkRecursionLimit();
         this.groupDepth++;
         fieldSkipped = this.skipField(tag, output);
         this.groupDepth--;
      } while (fieldSkipped);
   }

   public abstract double readDouble() throws IOException;

   public abstract float readFloat() throws IOException;

   public abstract long readUInt64() throws IOException;

   public abstract long readInt64() throws IOException;

   public abstract int readInt32() throws IOException;

   public abstract long readFixed64() throws IOException;

   public abstract int readFixed32() throws IOException;

   public abstract boolean readBool() throws IOException;

   public abstract String readString() throws IOException;

   Object readString(WireFormat.Utf8Validation utf8Validation) throws IOException {
      switch (utf8Validation) {
         case LOOSE:
            return this.readString();
         case STRICT:
            return this.readStringRequireUtf8();
         case LAZY:
            return this.readBytes();
         default:
            throw new IllegalStateException("Unknown UTF8 validation: " + utf8Validation);
      }
   }

   Object readPrimitiveField(WireFormat.FieldType type, WireFormat.Utf8Validation utf8Validation) throws IOException {
      switch (type) {
         case DOUBLE:
            return this.readDouble();
         case FLOAT:
            return this.readFloat();
         case INT64:
            return this.readInt64();
         case UINT64:
            return this.readUInt64();
         case INT32:
            return this.readInt32();
         case FIXED64:
            return this.readFixed64();
         case FIXED32:
            return this.readFixed32();
         case BOOL:
            return this.readBool();
         case BYTES:
            return this.readBytes();
         case UINT32:
            return this.readUInt32();
         case SFIXED32:
            return this.readSFixed32();
         case SFIXED64:
            return this.readSFixed64();
         case SINT32:
            return this.readSInt32();
         case SINT64:
            return this.readSInt64();
         case STRING:
            return this.readString(utf8Validation);
         case GROUP:
            throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
         case MESSAGE:
            throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
         case ENUM:
            throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
         default:
            throw new IllegalStateException("Unknown field type: " + type);
      }
   }

   public abstract String readStringRequireUtf8() throws IOException;

   public abstract void readGroup(final int fieldNumber, final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException;

   public abstract <T extends MessageLite> T readGroup(final int fieldNumber, final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException;

   @Deprecated
   public abstract void readUnknownGroup(final int fieldNumber, final MessageLite.Builder builder) throws IOException;

   public abstract void readMessage(final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException;

   public abstract <T extends MessageLite> T readMessage(final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException;

   public abstract ByteString readBytes() throws IOException;

   public abstract byte[] readByteArray() throws IOException;

   public abstract ByteBuffer readByteBuffer() throws IOException;

   public abstract int readUInt32() throws IOException;

   public abstract int readEnum() throws IOException;

   public abstract int readSFixed32() throws IOException;

   public abstract long readSFixed64() throws IOException;

   public abstract int readSInt32() throws IOException;

   public abstract long readSInt64() throws IOException;

   public abstract int readRawVarint32() throws IOException;

   public abstract long readRawVarint64() throws IOException;

   abstract long readRawVarint64SlowPath() throws IOException;

   public abstract int readRawLittleEndian32() throws IOException;

   public abstract long readRawLittleEndian64() throws IOException;

   public abstract void enableAliasing(boolean enabled);

   public final int setRecursionLimit(final int limit) {
      if (limit < 0) {
         throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
      } else {
         int oldLimit = this.recursionLimit;
         this.recursionLimit = limit;
         return oldLimit;
      }
   }

   public final int setSizeLimit(final int limit) {
      if (limit < 0) {
         throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
      } else {
         int oldLimit = this.sizeLimit;
         this.sizeLimit = limit;
         return oldLimit;
      }
   }

   final void discardUnknownFields() {
      this.shouldDiscardUnknownFields = true;
   }

   final void unsetDiscardUnknownFields() {
      this.shouldDiscardUnknownFields = false;
   }

   final boolean shouldDiscardUnknownFields() {
      return this.shouldDiscardUnknownFields;
   }

   public abstract void resetSizeCounter();

   public abstract int pushLimit(int byteLimit) throws InvalidProtocolBufferException;

   public abstract void popLimit(final int oldLimit);

   public abstract int getBytesUntilLimit();

   public abstract boolean isAtEnd() throws IOException;

   public abstract int getTotalBytesRead();

   public abstract byte readRawByte() throws IOException;

   public abstract byte[] readRawBytes(final int size) throws IOException;

   public abstract void skipRawBytes(final int size) throws IOException;

   public static int decodeZigZag32(final int n) {
      return n >>> 1 ^ -(n & 1);
   }

   public static long decodeZigZag64(final long n) {
      return n >>> 1 ^ -(n & 1L);
   }

   public static int readRawVarint32(final int firstByte, final InputStream input) throws IOException {
      if ((firstByte & 128) == 0) {
         return firstByte;
      } else {
         int result = firstByte & 127;

         int offset;
         for (offset = 7; offset < 32; offset += 7) {
            int b = input.read();
            if (b == -1) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            result |= (b & 127) << offset;
            if ((b & 128) == 0) {
               return result;
            }
         }

         while (offset < 64) {
            int bx = input.read();
            if (bx == -1) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            if ((bx & 128) == 0) {
               return result;
            }

            offset += 7;
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }
   }

   static int readRawVarint32(final InputStream input) throws IOException {
      int firstByte = input.read();
      if (firstByte == -1) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return readRawVarint32(firstByte, input);
      }
   }

   private static final class ArrayDecoder extends CodedInputStream {
      private final byte[] buffer;
      private final boolean immutable;
      private int limit;
      private int bufferSizeAfterLimit;
      private int pos;
      private int startPos;
      private int lastTag;
      private boolean enableAliasing;
      private int currentLimit = Integer.MAX_VALUE;

      private ArrayDecoder(final byte[] buffer, final int offset, final int len, boolean immutable) {
         this.buffer = buffer;
         this.limit = offset + len;
         this.pos = offset;
         this.startPos = this.pos;
         this.immutable = immutable;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(final int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(final int tag) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(final int tag, final CodedOutputStream output) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0: {
               long value = this.readInt64();
               output.writeUInt32NoTag(tag);
               output.writeUInt64NoTag(value);
               return true;
            }
            case 1: {
               long value = this.readRawLittleEndian64();
               output.writeUInt32NoTag(tag);
               output.writeFixed64NoTag(value);
               return true;
            }
            case 2: {
               ByteString value = this.readBytes();
               output.writeUInt32NoTag(tag);
               output.writeBytesNoTag(value);
               return true;
            }
            case 3:
               output.writeUInt32NoTag(tag);
               this.skipMessage(output);
               int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
               this.checkLastTagWas(endtag);
               output.writeUInt32NoTag(endtag);
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5: {
               int value = this.readRawLittleEndian32();
               output.writeUInt32NoTag(tag);
               output.writeFixed32NoTag(value);
               return true;
            }
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            String result = Utf8.decodeUtf8(this.buffer, this.pos, size);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void readGroup(final int fieldNumber, final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
      }

      @Override
      public <T extends MessageLite> T readGroup(final int fieldNumber, final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
         return result;
      }

      @Deprecated
      @Override
      public void readUnknownGroup(final int fieldNumber, final MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
            return result;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            ByteString result = this.immutable && this.enableAliasing
               ? ByteString.wrap(this.buffer, this.pos, size)
               : ByteString.copyFrom(this.buffer, this.pos, size);
            this.pos += size;
            return result;
         } else {
            return size == 0 ? ByteString.EMPTY : ByteString.wrap(this.readRawBytes(size));
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         int size = this.readRawVarint32();
         return this.readRawBytes(size);
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.limit - this.pos) {
            ByteBuffer result = !this.immutable && this.enableAliasing
               ? ByteBuffer.wrap(this.buffer, this.pos, size).slice()
               : ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
            this.pos += size;
            return result;
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         int tempPos;
         int x;
         label47: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               byte[] buffer = this.buffer;
               if ((x = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return x;
               }

               if (this.limit - tempPos >= 9) {
                  if ((x = x ^ buffer[tempPos++] << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x = x ^ buffer[tempPos++] << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x = x ^ buffer[tempPos++] << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = buffer[tempPos++];
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      private void skipRawVarint() throws IOException {
         if (this.limit - this.pos >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }
      }

      private void skipRawVarintFastPath() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (this.buffer[this.pos++] >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public long readRawVarint64() throws IOException {
         int tempPos;
         long x;
         label51: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               byte[] buffer = this.buffer;
               int y;
               if ((y = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return y;
               }

               if (this.limit - tempPos >= 9) {
                  if ((y = y ^ buffer[tempPos++] << 7) < 0) {
                     x = y ^ -128;
                     break label51;
                  }

                  if ((y = y ^ buffer[tempPos++] << 14) >= 0) {
                     x = y ^ 16256;
                     break label51;
                  }

                  if ((y = y ^ buffer[tempPos++] << 21) < 0) {
                     x = y ^ -2080896;
                     break label51;
                  }

                  if ((x = y ^ (long)buffer[tempPos++] << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x = x ^ (long)buffer[tempPos++] << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x = x ^ (long)buffer[tempPos++] << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x = x ^ (long)buffer[tempPos++] << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)buffer[tempPos++] << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || buffer[tempPos++] >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for (int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         int tempPos = this.pos;
         if (this.limit - tempPos < 4) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] buffer = this.buffer;
            this.pos = tempPos + 4;
            return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
         }
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         int tempPos = this.pos;
         if (this.limit - tempPos < 8) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] buffer = this.buffer;
            this.pos = tempPos + 8;
            return buffer[tempPos] & 255L
               | (buffer[tempPos + 1] & 255L) << 8
               | (buffer[tempPos + 2] & 255L) << 16
               | (buffer[tempPos + 3] & 255L) << 24
               | (buffer[tempPos + 4] & 255L) << 32
               | (buffer[tempPos + 5] & 255L) << 40
               | (buffer[tempPos + 6] & 255L) << 48
               | (buffer[tempPos + 7] & 255L) << 56;
         }
      }

      @Override
      public void enableAliasing(boolean enabled) {
         this.enableAliasing = enabled;
      }

      @Override
      public void resetSizeCounter() {
         this.startPos = this.pos;
      }

      @Override
      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.getTotalBytesRead();
            if (byteLimit < 0) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else {
               int oldLimit = this.currentLimit;
               if (byteLimit > oldLimit) {
                  throw InvalidProtocolBufferException.truncatedMessage();
               } else {
                  this.currentLimit = byteLimit;
                  this.recomputeBufferSizeAfterLimit();
                  return oldLimit;
               }
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.limit = this.limit + this.bufferSizeAfterLimit;
         int bufferEnd = this.limit - this.startPos;
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.limit = this.limit - this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }
      }

      @Override
      public void popLimit(final int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.pos == this.limit;
      }

      @Override
      public int getTotalBytesRead() {
         return this.pos - this.startPos;
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return this.buffer[this.pos++];
         }
      }

      @Override
      public byte[] readRawBytes(final int length) throws IOException {
         if (length > 0 && length <= this.limit - this.pos) {
            int tempPos = this.pos;
            this.pos += length;
            return Arrays.copyOfRange(this.buffer, tempPos, this.pos);
         } else if (length <= 0) {
            if (length == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void skipRawBytes(final int length) throws IOException {
         if (length >= 0 && length <= this.limit - this.pos) {
            this.pos += length;
         } else if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }
   }

   private static final class IterableDirectByteBufferDecoder extends CodedInputStream {
      private final Iterable<ByteBuffer> input;
      private final Iterator<ByteBuffer> iterator;
      private ByteBuffer currentByteBuffer;
      private final boolean immutable;
      private boolean enableAliasing;
      private int totalBufferSize;
      private int bufferSizeAfterCurrentLimit;
      private int currentLimit = Integer.MAX_VALUE;
      private int lastTag;
      private int totalBytesRead;
      private int startOffset;
      private long currentByteBufferPos;
      private long currentByteBufferStartPos;
      private long currentAddress;
      private long currentByteBufferLimit;

      private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> inputBufs, int size, boolean immutableFlag) {
         this.totalBufferSize = size;
         this.input = inputBufs;
         this.iterator = this.input.iterator();
         this.immutable = immutableFlag;
         this.startOffset = this.totalBytesRead = 0;
         if (size == 0) {
            this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
            this.currentByteBufferPos = 0L;
            this.currentByteBufferStartPos = 0L;
            this.currentByteBufferLimit = 0L;
            this.currentAddress = 0L;
         } else {
            this.tryGetNextByteBuffer();
         }
      }

      private void getNextByteBuffer() throws InvalidProtocolBufferException {
         if (!this.iterator.hasNext()) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.tryGetNextByteBuffer();
         }
      }

      private void tryGetNextByteBuffer() {
         this.currentByteBuffer = this.iterator.next();
         this.totalBytesRead = this.totalBytesRead + (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
         this.currentByteBufferPos = this.currentByteBuffer.position();
         this.currentByteBufferStartPos = this.currentByteBufferPos;
         this.currentByteBufferLimit = this.currentByteBuffer.limit();
         this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
         this.currentByteBufferPos = this.currentByteBufferPos + this.currentAddress;
         this.currentByteBufferStartPos = this.currentByteBufferStartPos + this.currentAddress;
         this.currentByteBufferLimit = this.currentByteBufferLimit + this.currentAddress;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(final int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(final int tag) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(final int tag, final CodedOutputStream output) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0: {
               long value = this.readInt64();
               output.writeUInt32NoTag(tag);
               output.writeUInt64NoTag(value);
               return true;
            }
            case 1: {
               long value = this.readRawLittleEndian64();
               output.writeUInt32NoTag(tag);
               output.writeFixed64NoTag(value);
               return true;
            }
            case 2: {
               ByteString value = this.readBytes();
               output.writeUInt32NoTag(tag);
               output.writeBytesNoTag(value);
               return true;
            }
            case 3:
               output.writeUInt32NoTag(tag);
               this.skipMessage(output);
               int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
               this.checkLastTagWas(endtag);
               output.writeUInt32NoTag(endtag);
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5: {
               int value = this.readRawLittleEndian32();
               output.writeUInt32NoTag(tag);
               output.writeFixed32NoTag(value);
               return true;
            }
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            byte[] bytes = new byte[size];
            UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
            String result = new String(bytes, Internal.UTF_8);
            this.currentByteBufferPos += size;
            return result;
         } else if (size > 0 && size <= this.remaining()) {
            byte[] bytes = new byte[size];
            this.readRawBytesTo(bytes, 0, size);
            return new String(bytes, Internal.UTF_8);
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            int bufferPos = (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
            String result = Utf8.decodeUtf8(this.currentByteBuffer, bufferPos, size);
            this.currentByteBufferPos += size;
            return result;
         } else if (size >= 0 && size <= this.remaining()) {
            byte[] bytes = new byte[size];
            this.readRawBytesTo(bytes, 0, size);
            return Utf8.decodeUtf8(bytes, 0, size);
         } else if (size == 0) {
            return "";
         } else if (size <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void readGroup(final int fieldNumber, final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
      }

      @Override
      public <T extends MessageLite> T readGroup(final int fieldNumber, final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
         return result;
      }

      @Deprecated
      @Override
      public void readUnknownGroup(final int fieldNumber, final MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
            return result;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            if (this.immutable && this.enableAliasing) {
               int idx = (int)(this.currentByteBufferPos - this.currentAddress);
               ByteString result = ByteString.wrap(this.slice(idx, idx + size));
               this.currentByteBufferPos += size;
               return result;
            } else {
               byte[] bytes = new byte[size];
               UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
               this.currentByteBufferPos += size;
               return ByteString.wrap(bytes);
            }
         } else if (size > 0 && size <= this.remaining()) {
            if (this.immutable && this.enableAliasing) {
               ArrayList<ByteString> byteStrings = new ArrayList<>();
               int l = size;

               while (l > 0) {
                  if (this.currentRemaining() == 0L) {
                     this.getNextByteBuffer();
                  }

                  int bytesToCopy = Math.min(l, (int)this.currentRemaining());
                  int idx = (int)(this.currentByteBufferPos - this.currentAddress);
                  byteStrings.add(ByteString.wrap(this.slice(idx, idx + bytesToCopy)));
                  l -= bytesToCopy;
                  this.currentByteBufferPos += bytesToCopy;
               }

               return ByteString.copyFrom(byteStrings);
            } else {
               byte[] temp = new byte[size];
               this.readRawBytesTo(temp, 0, size);
               return ByteString.wrap(temp);
            }
         } else if (size == 0) {
            return ByteString.EMPTY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         return this.readRawBytes(this.readRawVarint32());
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.currentRemaining()) {
            if (!this.immutable && this.enableAliasing) {
               this.currentByteBufferPos += size;
               return this.slice((int)(this.currentByteBufferPos - this.currentAddress - size), (int)(this.currentByteBufferPos - this.currentAddress));
            } else {
               byte[] bytes = new byte[size];
               UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, size);
               this.currentByteBufferPos += size;
               return ByteBuffer.wrap(bytes);
            }
         } else if (size > 0 && size <= this.remaining()) {
            byte[] temp = new byte[size];
            this.readRawBytesTo(temp, 0, size);
            return ByteBuffer.wrap(temp);
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         long tempPos;
         int x;
         label47: {
            tempPos = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
               if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  this.currentByteBufferPos++;
                  return x;
               }

               if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
                  if ((x = x ^ UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x = x ^ UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x = x ^ UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = UnsafeUtil.getByte(tempPos++);
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.currentByteBufferPos = tempPos;
         return x;
      }

      @Override
      public long readRawVarint64() throws IOException {
         long tempPos;
         long x;
         label51: {
            tempPos = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
               int y;
               if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  this.currentByteBufferPos++;
                  return y;
               }

               if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
                  if ((y = y ^ UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x = y ^ -128;
                     break label51;
                  }

                  if ((y = y ^ UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x = y ^ 16256;
                     break label51;
                  }

                  if ((y = y ^ UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x = y ^ -2080896;
                     break label51;
                  }

                  if ((x = y ^ (long)UnsafeUtil.getByte(tempPos++) << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x = x ^ (long)UnsafeUtil.getByte(tempPos++) << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x = x ^ (long)UnsafeUtil.getByte(tempPos++) << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x = x ^ (long)UnsafeUtil.getByte(tempPos++) << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)UnsafeUtil.getByte(tempPos++) << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || UnsafeUtil.getByte(tempPos++) >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.currentByteBufferPos = tempPos;
         return x;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for (int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         if (this.currentRemaining() >= 4L) {
            long tempPos = this.currentByteBufferPos;
            this.currentByteBufferPos += 4L;
            return UnsafeUtil.getByte(tempPos) & 0xFF
               | (UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8
               | (UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16
               | (UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
         } else {
            return this.readRawByte() & 0xFF | (this.readRawByte() & 0xFF) << 8 | (this.readRawByte() & 0xFF) << 16 | (this.readRawByte() & 0xFF) << 24;
         }
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         if (this.currentRemaining() >= 8L) {
            long tempPos = this.currentByteBufferPos;
            this.currentByteBufferPos += 8L;
            return UnsafeUtil.getByte(tempPos) & 255L
               | (UnsafeUtil.getByte(tempPos + 1L) & 255L) << 8
               | (UnsafeUtil.getByte(tempPos + 2L) & 255L) << 16
               | (UnsafeUtil.getByte(tempPos + 3L) & 255L) << 24
               | (UnsafeUtil.getByte(tempPos + 4L) & 255L) << 32
               | (UnsafeUtil.getByte(tempPos + 5L) & 255L) << 40
               | (UnsafeUtil.getByte(tempPos + 6L) & 255L) << 48
               | (UnsafeUtil.getByte(tempPos + 7L) & 255L) << 56;
         } else {
            return this.readRawByte() & 255L
               | (this.readRawByte() & 255L) << 8
               | (this.readRawByte() & 255L) << 16
               | (this.readRawByte() & 255L) << 24
               | (this.readRawByte() & 255L) << 32
               | (this.readRawByte() & 255L) << 40
               | (this.readRawByte() & 255L) << 48
               | (this.readRawByte() & 255L) << 56;
         }
      }

      @Override
      public void enableAliasing(boolean enabled) {
         this.enableAliasing = enabled;
      }

      @Override
      public void resetSizeCounter() {
         this.startOffset = (int)(this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos);
      }

      @Override
      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = byteLimit;
               this.recomputeBufferSizeAfterLimit();
               return oldLimit;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.totalBufferSize = this.totalBufferSize + this.bufferSizeAfterCurrentLimit;
         int bufferEnd = this.totalBufferSize - this.startOffset;
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterCurrentLimit = bufferEnd - this.currentLimit;
            this.totalBufferSize = this.totalBufferSize - this.bufferSizeAfterCurrentLimit;
         } else {
            this.bufferSizeAfterCurrentLimit = 0;
         }
      }

      @Override
      public void popLimit(final int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos == this.totalBufferSize;
      }

      @Override
      public int getTotalBytesRead() {
         return (int)(this.totalBytesRead - this.startOffset + this.currentByteBufferPos - this.currentByteBufferStartPos);
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.currentRemaining() == 0L) {
            this.getNextByteBuffer();
         }

         return UnsafeUtil.getByte(this.currentByteBufferPos++);
      }

      @Override
      public byte[] readRawBytes(final int length) throws IOException {
         if (length >= 0 && length <= this.currentRemaining()) {
            byte[] bytes = new byte[length];
            UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, length);
            this.currentByteBufferPos += length;
            return bytes;
         } else if (length >= 0 && length <= this.remaining()) {
            byte[] bytes = new byte[length];
            this.readRawBytesTo(bytes, 0, length);
            return bytes;
         } else if (length <= 0) {
            if (length == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void readRawBytesTo(byte[] bytes, int offset, final int length) throws IOException {
         if (length >= 0 && length <= this.remaining()) {
            int l = length;

            while (l > 0) {
               if (this.currentRemaining() == 0L) {
                  this.getNextByteBuffer();
               }

               int bytesToCopy = Math.min(l, (int)this.currentRemaining());
               UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, length - l + offset, bytesToCopy);
               l -= bytesToCopy;
               this.currentByteBufferPos += bytesToCopy;
            }
         } else if (length <= 0) {
            if (length != 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void skipRawBytes(final int length) throws IOException {
         if (length >= 0 && length <= this.totalBufferSize - this.totalBytesRead - this.currentByteBufferPos + this.currentByteBufferStartPos) {
            int l = length;

            while (l > 0) {
               if (this.currentRemaining() == 0L) {
                  this.getNextByteBuffer();
               }

               int rl = Math.min(l, (int)this.currentRemaining());
               l -= rl;
               this.currentByteBufferPos += rl;
            }
         } else if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void skipRawVarint() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private int remaining() {
         return (int)(this.totalBufferSize - this.totalBytesRead - this.currentByteBufferPos + this.currentByteBufferStartPos);
      }

      private long currentRemaining() {
         return this.currentByteBufferLimit - this.currentByteBufferPos;
      }

      private ByteBuffer slice(int begin, int end) throws IOException {
         int prevPos = this.currentByteBuffer.position();
         int prevLimit = this.currentByteBuffer.limit();
         Buffer asBuffer = this.currentByteBuffer;

         ByteBuffer e;
         try {
            asBuffer.position(begin);
            asBuffer.limit(end);
            e = this.currentByteBuffer.slice();
         } catch (IllegalArgumentException var10) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } finally {
            asBuffer.position(prevPos);
            asBuffer.limit(prevLimit);
         }

         return e;
      }
   }

   private static final class StreamDecoder extends CodedInputStream {
      private final InputStream input;
      private final byte[] buffer;
      private int bufferSize;
      private int bufferSizeAfterLimit;
      private int pos;
      private int lastTag;
      private int totalBytesRetired;
      private int currentLimit = Integer.MAX_VALUE;
      private CodedInputStream.StreamDecoder.RefillCallback refillCallback = null;

      private StreamDecoder(final InputStream input, int bufferSize) {
         Internal.checkNotNull(input, "input");
         this.input = input;
         this.buffer = new byte[bufferSize];
         this.bufferSize = 0;
         this.pos = 0;
         this.totalBytesRetired = 0;
      }

      private static int read(InputStream input, byte[] data, int offset, int length) throws IOException {
         try {
            return input.read(data, offset, length);
         } catch (InvalidProtocolBufferException var5) {
            var5.setThrownFromInputStream();
            throw var5;
         }
      }

      private static long skip(InputStream input, long length) throws IOException {
         try {
            return input.skip(length);
         } catch (InvalidProtocolBufferException var4) {
            var4.setThrownFromInputStream();
            throw var4;
         }
      }

      private static int available(InputStream input) throws IOException {
         try {
            return input.available();
         } catch (InvalidProtocolBufferException var2) {
            var2.setThrownFromInputStream();
            throw var2;
         }
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(final int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(final int tag) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(final int tag, final CodedOutputStream output) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0: {
               long value = this.readInt64();
               output.writeUInt32NoTag(tag);
               output.writeUInt64NoTag(value);
               return true;
            }
            case 1: {
               long value = this.readRawLittleEndian64();
               output.writeUInt32NoTag(tag);
               output.writeFixed64NoTag(value);
               return true;
            }
            case 2: {
               ByteString value = this.readBytes();
               output.writeUInt32NoTag(tag);
               output.writeBytesNoTag(value);
               return true;
            }
            case 3:
               output.writeUInt32NoTag(tag);
               this.skipMessage(output);
               int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
               this.checkLastTagWas(endtag);
               output.writeUInt32NoTag(endtag);
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5: {
               int value = this.readRawLittleEndian32();
               output.writeUInt32NoTag(tag);
               output.writeFixed32NoTag(value);
               return true;
            }
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.bufferSize - this.pos) {
            String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else if (size <= this.bufferSize) {
            this.refillBuffer(size);
            String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
            this.pos += size;
            return result;
         } else {
            return new String(this.readRawBytesSlowPath(size, false), Internal.UTF_8);
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         int oldPos = this.pos;
         byte[] bytes;
         int tempPos;
         if (size <= this.bufferSize - oldPos && size > 0) {
            bytes = this.buffer;
            this.pos = oldPos + size;
            tempPos = oldPos;
         } else {
            if (size == 0) {
               return "";
            }

            if (size < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (size <= this.bufferSize) {
               this.refillBuffer(size);
               bytes = this.buffer;
               tempPos = 0;
               this.pos = tempPos + size;
            } else {
               bytes = this.readRawBytesSlowPath(size, false);
               tempPos = 0;
            }
         }

         return Utf8.decodeUtf8(bytes, tempPos, size);
      }

      @Override
      public void readGroup(final int fieldNumber, final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
      }

      @Override
      public <T extends MessageLite> T readGroup(final int fieldNumber, final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
         return result;
      }

      @Deprecated
      @Override
      public void readUnknownGroup(final int fieldNumber, final MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
            return result;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size <= this.bufferSize - this.pos && size > 0) {
            ByteString result = ByteString.copyFrom(this.buffer, this.pos, size);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return ByteString.EMPTY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            return this.readBytesSlowPath(size);
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         int size = this.readRawVarint32();
         if (size <= this.bufferSize - this.pos && size > 0) {
            byte[] result = Arrays.copyOfRange(this.buffer, this.pos, this.pos + size);
            this.pos += size;
            return result;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            return this.readRawBytesSlowPath(size, false);
         }
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size <= this.bufferSize - this.pos && size > 0) {
            ByteBuffer result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
            this.pos += size;
            return result;
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            return ByteBuffer.wrap(this.readRawBytesSlowPath(size, true));
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         int tempPos;
         int x;
         label47: {
            tempPos = this.pos;
            if (this.bufferSize != tempPos) {
               byte[] buffer = this.buffer;
               if ((x = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return x;
               }

               if (this.bufferSize - tempPos >= 9) {
                  if ((x = x ^ buffer[tempPos++] << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x = x ^ buffer[tempPos++] << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x = x ^ buffer[tempPos++] << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = buffer[tempPos++];
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0 || buffer[tempPos++] >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      private void skipRawVarint() throws IOException {
         if (this.bufferSize - this.pos >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }
      }

      private void skipRawVarintFastPath() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (this.buffer[this.pos++] >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public long readRawVarint64() throws IOException {
         int tempPos;
         long x;
         label51: {
            tempPos = this.pos;
            if (this.bufferSize != tempPos) {
               byte[] buffer = this.buffer;
               int y;
               if ((y = buffer[tempPos++]) >= 0) {
                  this.pos = tempPos;
                  return y;
               }

               if (this.bufferSize - tempPos >= 9) {
                  if ((y = y ^ buffer[tempPos++] << 7) < 0) {
                     x = y ^ -128;
                     break label51;
                  }

                  if ((y = y ^ buffer[tempPos++] << 14) >= 0) {
                     x = y ^ 16256;
                     break label51;
                  }

                  if ((y = y ^ buffer[tempPos++] << 21) < 0) {
                     x = y ^ -2080896;
                     break label51;
                  }

                  if ((x = y ^ (long)buffer[tempPos++] << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x = x ^ (long)buffer[tempPos++] << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x = x ^ (long)buffer[tempPos++] << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x = x ^ (long)buffer[tempPos++] << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)buffer[tempPos++] << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || buffer[tempPos++] >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for (int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         int tempPos = this.pos;
         if (this.bufferSize - tempPos < 4) {
            this.refillBuffer(4);
            tempPos = this.pos;
         }

         byte[] buffer = this.buffer;
         this.pos = tempPos + 4;
         return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         int tempPos = this.pos;
         if (this.bufferSize - tempPos < 8) {
            this.refillBuffer(8);
            tempPos = this.pos;
         }

         byte[] buffer = this.buffer;
         this.pos = tempPos + 8;
         return buffer[tempPos] & 255L
            | (buffer[tempPos + 1] & 255L) << 8
            | (buffer[tempPos + 2] & 255L) << 16
            | (buffer[tempPos + 3] & 255L) << 24
            | (buffer[tempPos + 4] & 255L) << 32
            | (buffer[tempPos + 5] & 255L) << 40
            | (buffer[tempPos + 6] & 255L) << 48
            | (buffer[tempPos + 7] & 255L) << 56;
      }

      @Override
      public void enableAliasing(boolean enabled) {
      }

      @Override
      public void resetSizeCounter() {
         this.totalBytesRetired = -this.pos;
      }

      @Override
      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.totalBytesRetired + this.pos;
            if (byteLimit < 0) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else {
               int oldLimit = this.currentLimit;
               if (byteLimit > oldLimit) {
                  throw InvalidProtocolBufferException.truncatedMessage();
               } else {
                  this.currentLimit = byteLimit;
                  this.recomputeBufferSizeAfterLimit();
                  return oldLimit;
               }
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.bufferSize = this.bufferSize + this.bufferSizeAfterLimit;
         int bufferEnd = this.totalBytesRetired + this.bufferSize;
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.bufferSize = this.bufferSize - this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }
      }

      @Override
      public void popLimit(final int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         if (this.currentLimit == Integer.MAX_VALUE) {
            return -1;
         } else {
            int currentAbsolutePosition = this.totalBytesRetired + this.pos;
            return this.currentLimit - currentAbsolutePosition;
         }
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.pos == this.bufferSize && !this.tryRefillBuffer(1);
      }

      @Override
      public int getTotalBytesRead() {
         return this.totalBytesRetired + this.pos;
      }

      private void refillBuffer(int n) throws IOException {
         if (!this.tryRefillBuffer(n)) {
            if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else {
               throw InvalidProtocolBufferException.truncatedMessage();
            }
         }
      }

      private boolean tryRefillBuffer(int n) throws IOException {
         if (this.pos + n <= this.bufferSize) {
            throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
         } else if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
            return false;
         } else if (this.totalBytesRetired + this.pos + n > this.currentLimit) {
            return false;
         } else {
            if (this.refillCallback != null) {
               this.refillCallback.onRefill();
            }

            int tempPos = this.pos;
            if (tempPos > 0) {
               if (this.bufferSize > tempPos) {
                  System.arraycopy(this.buffer, tempPos, this.buffer, 0, this.bufferSize - tempPos);
               }

               this.totalBytesRetired += tempPos;
               this.bufferSize -= tempPos;
               this.pos = 0;
            }

            int bytesRead = read(
               this.input,
               this.buffer,
               this.bufferSize,
               Math.min(this.buffer.length - this.bufferSize, this.sizeLimit - this.totalBytesRetired - this.bufferSize)
            );
            if (bytesRead == 0 || bytesRead < -1 || bytesRead > this.buffer.length) {
               throw new IllegalStateException(
                  this.input.getClass() + "#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy."
               );
            } else if (bytesRead <= 0) {
               return false;
            } else {
               this.bufferSize += bytesRead;
               this.recomputeBufferSizeAfterLimit();
               return this.bufferSize >= n || this.tryRefillBuffer(n);
            }
         }
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.pos == this.bufferSize) {
            this.refillBuffer(1);
         }

         return this.buffer[this.pos++];
      }

      @Override
      public byte[] readRawBytes(final int size) throws IOException {
         int tempPos = this.pos;
         if (size <= this.bufferSize - tempPos && size > 0) {
            this.pos = tempPos + size;
            return Arrays.copyOfRange(this.buffer, tempPos, tempPos + size);
         } else {
            return this.readRawBytesSlowPath(size, false);
         }
      }

      private byte[] readRawBytesSlowPath(final int size, boolean ensureNoLeakedReferences) throws IOException {
         byte[] result = this.readRawBytesSlowPathOneChunk(size);
         if (result != null) {
            return ensureNoLeakedReferences ? (byte[])result.clone() : result;
         } else {
            int originalBufferPos = this.pos;
            int bufferedBytes = this.bufferSize - this.pos;
            this.totalBytesRetired = this.totalBytesRetired + this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = this.readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;

            for (byte[] chunk : chunks) {
               System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
               tempPos += chunk.length;
            }

            return bytes;
         }
      }

      private byte[] readRawBytesSlowPathOneChunk(final int size) throws IOException {
         if (size == 0) {
            return Internal.EMPTY_BYTE_ARRAY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            int currentMessageSize = this.totalBytesRetired + this.pos + size;
            if (currentMessageSize - this.sizeLimit > 0) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else if (currentMessageSize > this.currentLimit) {
               this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               int bufferedBytes = this.bufferSize - this.pos;
               int sizeLeft = size - bufferedBytes;
               if (sizeLeft >= 4096 && sizeLeft > available(this.input)) {
                  return null;
               } else {
                  byte[] bytes = new byte[size];
                  System.arraycopy(this.buffer, this.pos, bytes, 0, bufferedBytes);
                  this.totalBytesRetired = this.totalBytesRetired + this.bufferSize;
                  this.pos = 0;
                  this.bufferSize = 0;
                  int tempPos = bufferedBytes;

                  while (tempPos < bytes.length) {
                     int n = read(this.input, bytes, tempPos, size - tempPos);
                     if (n == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                     }

                     this.totalBytesRetired += n;
                     tempPos += n;
                  }

                  return bytes;
               }
            }
         }
      }

      private List<byte[]> readRawBytesSlowPathRemainingChunks(int sizeLeft) throws IOException {
         List<byte[]> chunks = new ArrayList<>();

         while (sizeLeft > 0) {
            byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
            int tempPos = 0;

            while (tempPos < chunk.length) {
               int n = this.input.read(chunk, tempPos, chunk.length - tempPos);
               if (n == -1) {
                  throw InvalidProtocolBufferException.truncatedMessage();
               }

               this.totalBytesRetired += n;
               tempPos += n;
            }

            sizeLeft -= chunk.length;
            chunks.add(chunk);
         }

         return chunks;
      }

      private ByteString readBytesSlowPath(final int size) throws IOException {
         byte[] result = this.readRawBytesSlowPathOneChunk(size);
         if (result != null) {
            return ByteString.copyFrom(result);
         } else {
            int originalBufferPos = this.pos;
            int bufferedBytes = this.bufferSize - this.pos;
            this.totalBytesRetired = this.totalBytesRetired + this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = this.readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;

            for (byte[] chunk : chunks) {
               System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
               tempPos += chunk.length;
            }

            return ByteString.wrap(bytes);
         }
      }

      @Override
      public void skipRawBytes(final int size) throws IOException {
         if (size <= this.bufferSize - this.pos && size >= 0) {
            this.pos += size;
         } else {
            this.skipRawBytesSlowPath(size);
         }
      }

      private void skipRawBytesSlowPath(final int size) throws IOException {
         if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else if (this.totalBytesRetired + this.pos + size > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            int totalSkipped = 0;
            if (this.refillCallback == null) {
               this.totalBytesRetired = this.totalBytesRetired + this.pos;
               totalSkipped = this.bufferSize - this.pos;
               this.bufferSize = 0;
               this.pos = 0;

               try {
                  while (totalSkipped < size) {
                     int toSkip = size - totalSkipped;
                     long skipped = skip(this.input, toSkip);
                     if (skipped < 0L || skipped > toSkip) {
                        throw new IllegalStateException(
                           this.input.getClass() + "#skip returned invalid result: " + skipped + "\nThe InputStream implementation is buggy."
                        );
                     }

                     if (skipped == 0L) {
                        break;
                     }

                     totalSkipped += (int)skipped;
                  }
               } finally {
                  this.totalBytesRetired += totalSkipped;
                  this.recomputeBufferSizeAfterLimit();
               }
            }

            if (totalSkipped < size) {
               int tempPos = this.bufferSize - this.pos;
               this.pos = this.bufferSize;
               this.refillBuffer(1);

               while (size - tempPos > this.bufferSize) {
                  tempPos += this.bufferSize;
                  this.pos = this.bufferSize;
                  this.refillBuffer(1);
               }

               this.pos = size - tempPos;
            }
         }
      }

      private interface RefillCallback {
         void onRefill();
      }

      private class SkippedDataSink implements CodedInputStream.StreamDecoder.RefillCallback {
         private int lastPos = StreamDecoder.this.pos;
         private ByteArrayOutputStream byteArrayStream;

         @Override
         public void onRefill() {
            if (this.byteArrayStream == null) {
               this.byteArrayStream = new ByteArrayOutputStream();
            }

            this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
            this.lastPos = 0;
         }

         ByteBuffer getSkippedData() {
            if (this.byteArrayStream == null) {
               return ByteBuffer.wrap(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
            } else {
               this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos);
               return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
            }
         }
      }
   }

   private static final class UnsafeDirectNioDecoder extends CodedInputStream {
      private final ByteBuffer buffer;
      private final boolean immutable;
      private final long address;
      private long limit;
      private long pos;
      private long startPos;
      private int bufferSizeAfterLimit;
      private int lastTag;
      private boolean enableAliasing;
      private int currentLimit = Integer.MAX_VALUE;

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      private UnsafeDirectNioDecoder(ByteBuffer buffer, boolean immutable) {
         this.buffer = buffer.duplicate();
         this.address = UnsafeUtil.addressOffset(buffer);
         this.limit = this.address + buffer.limit();
         this.pos = this.address + buffer.position();
         this.startPos = this.pos;
         this.immutable = immutable;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(final int value) throws InvalidProtocolBufferException {
         if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(final int tag) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(final int tag, final CodedOutputStream output) throws IOException {
         switch (WireFormat.getTagWireType(tag)) {
            case 0: {
               long value = this.readInt64();
               output.writeUInt32NoTag(tag);
               output.writeUInt64NoTag(value);
               return true;
            }
            case 1: {
               long value = this.readRawLittleEndian64();
               output.writeUInt32NoTag(tag);
               output.writeFixed64NoTag(value);
               return true;
            }
            case 2: {
               ByteString value = this.readBytes();
               output.writeUInt32NoTag(tag);
               output.writeBytesNoTag(value);
               return true;
            }
            case 3:
               output.writeUInt32NoTag(tag);
               this.skipMessage(output);
               int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
               this.checkLastTagWas(endtag);
               output.writeUInt32NoTag(endtag);
               return true;
            case 4:
               this.checkValidEndTag();
               return false;
            case 5: {
               int value = this.readRawLittleEndian32();
               output.writeUInt32NoTag(tag);
               output.writeFixed32NoTag(value);
               return true;
            }
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            byte[] bytes = new byte[size];
            UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
            String result = new String(bytes, Internal.UTF_8);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            int bufferPos = this.bufferPos(this.pos);
            String result = Utf8.decodeUtf8(this.buffer, bufferPos, size);
            this.pos += size;
            return result;
         } else if (size == 0) {
            return "";
         } else if (size <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void readGroup(final int fieldNumber, final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
      }

      @Override
      public <T extends MessageLite> T readGroup(final int fieldNumber, final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         this.checkRecursionLimit();
         this.groupDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
         this.groupDepth--;
         return result;
      }

      @Deprecated
      @Override
      public void readUnknownGroup(final int fieldNumber, final MessageLite.Builder builder) throws IOException {
         this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         builder.mergeFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
         int length = this.readRawVarint32();
         this.checkRecursionLimit();
         int oldLimit = this.pushLimit(length);
         this.messageDepth++;
         T result = (T)parser.parsePartialFrom(this, extensionRegistry);
         this.checkLastTagWas(0);
         this.messageDepth--;
         if (this.getBytesUntilLimit() != 0) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.popLimit(oldLimit);
            return result;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            if (this.immutable && this.enableAliasing) {
               ByteBuffer result = this.slice(this.pos, this.pos + size);
               this.pos += size;
               return ByteString.wrap(result);
            } else {
               byte[] bytes = new byte[size];
               UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
               this.pos += size;
               return ByteString.wrap(bytes);
            }
         } else if (size == 0) {
            return ByteString.EMPTY;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         return this.readRawBytes(this.readRawVarint32());
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int size = this.readRawVarint32();
         if (size > 0 && size <= this.remaining()) {
            if (!this.immutable && this.enableAliasing) {
               ByteBuffer result = this.slice(this.pos, this.pos + size);
               this.pos += size;
               return result;
            } else {
               byte[] bytes = new byte[size];
               UnsafeUtil.copyMemory(this.pos, bytes, 0L, size);
               this.pos += size;
               return ByteBuffer.wrap(bytes);
            }
         } else if (size == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         long tempPos;
         int x;
         label47: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  this.pos = tempPos;
                  return x;
               }

               if (this.limit - tempPos >= 9L) {
                  if ((x = x ^ UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x ^= -128;
                     break label47;
                  }

                  if ((x = x ^ UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x ^= 16256;
                     break label47;
                  }

                  if ((x = x ^ UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x ^= -2080896;
                     break label47;
                  }

                  int y = UnsafeUtil.getByte(tempPos++);
                  x ^= y << 28;
                  x ^= 266354560;
                  if (y >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0
                     || UnsafeUtil.getByte(tempPos++) >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      private void skipRawVarint() throws IOException {
         if (this.remaining() >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }
      }

      private void skipRawVarintFastPath() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (UnsafeUtil.getByte(this.pos++) >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for (int i = 0; i < 10; i++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public long readRawVarint64() throws IOException {
         long tempPos;
         long x;
         label51: {
            tempPos = this.pos;
            if (this.limit != tempPos) {
               int y;
               if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
                  this.pos = tempPos;
                  return y;
               }

               if (this.limit - tempPos >= 9L) {
                  if ((y = y ^ UnsafeUtil.getByte(tempPos++) << 7) < 0) {
                     x = y ^ -128;
                     break label51;
                  }

                  if ((y = y ^ UnsafeUtil.getByte(tempPos++) << 14) >= 0) {
                     x = y ^ 16256;
                     break label51;
                  }

                  if ((y = y ^ UnsafeUtil.getByte(tempPos++) << 21) < 0) {
                     x = y ^ -2080896;
                     break label51;
                  }

                  if ((x = y ^ (long)UnsafeUtil.getByte(tempPos++) << 28) >= 0L) {
                     x ^= 266354560L;
                     break label51;
                  }

                  if ((x = x ^ (long)UnsafeUtil.getByte(tempPos++) << 35) < 0L) {
                     x ^= -34093383808L;
                     break label51;
                  }

                  if ((x = x ^ (long)UnsafeUtil.getByte(tempPos++) << 42) >= 0L) {
                     x ^= 4363953127296L;
                     break label51;
                  }

                  if ((x = x ^ (long)UnsafeUtil.getByte(tempPos++) << 49) < 0L) {
                     x ^= -558586000294016L;
                     break label51;
                  }

                  x ^= (long)UnsafeUtil.getByte(tempPos++) << 56;
                  x ^= 71499008037633920L;
                  if (x >= 0L || UnsafeUtil.getByte(tempPos++) >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = tempPos;
         return x;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long result = 0L;

         for (int shift = 0; shift < 64; shift += 7) {
            byte b = this.readRawByte();
            result |= (long)(b & 127) << shift;
            if ((b & 128) == 0) {
               return result;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         long tempPos = this.pos;
         if (this.limit - tempPos < 4L) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.pos = tempPos + 4L;
            return UnsafeUtil.getByte(tempPos) & 0xFF
               | (UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8
               | (UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16
               | (UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
         }
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         long tempPos = this.pos;
         if (this.limit - tempPos < 8L) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.pos = tempPos + 8L;
            return UnsafeUtil.getByte(tempPos) & 255L
               | (UnsafeUtil.getByte(tempPos + 1L) & 255L) << 8
               | (UnsafeUtil.getByte(tempPos + 2L) & 255L) << 16
               | (UnsafeUtil.getByte(tempPos + 3L) & 255L) << 24
               | (UnsafeUtil.getByte(tempPos + 4L) & 255L) << 32
               | (UnsafeUtil.getByte(tempPos + 5L) & 255L) << 40
               | (UnsafeUtil.getByte(tempPos + 6L) & 255L) << 48
               | (UnsafeUtil.getByte(tempPos + 7L) & 255L) << 56;
         }
      }

      @Override
      public void enableAliasing(boolean enabled) {
         this.enableAliasing = enabled;
      }

      @Override
      public void resetSizeCounter() {
         this.startPos = this.pos;
      }

      @Override
      public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
         if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            byteLimit += this.getTotalBytesRead();
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = byteLimit;
               this.recomputeBufferSizeAfterLimit();
               return oldLimit;
            }
         }
      }

      @Override
      public void popLimit(final int oldLimit) {
         this.currentLimit = oldLimit;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.pos == this.limit;
      }

      @Override
      public int getTotalBytesRead() {
         return (int)(this.pos - this.startPos);
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return UnsafeUtil.getByte(this.pos++);
         }
      }

      @Override
      public byte[] readRawBytes(final int length) throws IOException {
         if (length >= 0 && length <= this.remaining()) {
            byte[] bytes = new byte[length];
            this.slice(this.pos, this.pos + length).get(bytes);
            this.pos += length;
            return bytes;
         } else if (length <= 0) {
            if (length == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void skipRawBytes(final int length) throws IOException {
         if (length >= 0 && length <= this.remaining()) {
            this.pos += length;
         } else if (length < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.limit = this.limit + this.bufferSizeAfterLimit;
         int bufferEnd = (int)(this.limit - this.startPos);
         if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.limit = this.limit - this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }
      }

      private int remaining() {
         return (int)(this.limit - this.pos);
      }

      private int bufferPos(long pos) {
         return (int)(pos - this.address);
      }

      private ByteBuffer slice(long begin, long end) throws IOException {
         int prevPos = this.buffer.position();
         int prevLimit = this.buffer.limit();
         Buffer asBuffer = this.buffer;

         ByteBuffer e;
         try {
            asBuffer.position(this.bufferPos(begin));
            asBuffer.limit(this.bufferPos(end));
            e = this.buffer.slice();
         } catch (IllegalArgumentException var13) {
            InvalidProtocolBufferException ex = InvalidProtocolBufferException.truncatedMessage();
            ex.initCause(var13);
            throw ex;
         } finally {
            asBuffer.position(prevPos);
            asBuffer.limit(prevLimit);
         }

         return e;
      }
   }
}
