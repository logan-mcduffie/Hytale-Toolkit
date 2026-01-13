package com.google.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CodedOutputStream extends ByteOutput {
   private static final Logger logger = Logger.getLogger(CodedOutputStream.class.getName());
   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = UnsafeUtil.hasUnsafeArrayOperations();
   Object wrapper;
   @Deprecated
   public static final int LITTLE_ENDIAN_32_SIZE = 4;
   public static final int DEFAULT_BUFFER_SIZE = 4096;
   private boolean serializationDeterministic;

   static int computePreferredBufferSize(int dataLength) {
      return dataLength > 4096 ? 4096 : dataLength;
   }

   public static CodedOutputStream newInstance(final OutputStream output) {
      return newInstance(output, 4096);
   }

   public static CodedOutputStream newInstance(final OutputStream output, final int bufferSize) {
      return new CodedOutputStream.OutputStreamEncoder(output, bufferSize);
   }

   public static CodedOutputStream newInstance(final byte[] flatArray) {
      return newInstance(flatArray, 0, flatArray.length);
   }

   public static CodedOutputStream newInstance(final byte[] flatArray, final int offset, final int length) {
      return new CodedOutputStream.ArrayEncoder(flatArray, offset, length);
   }

   public static CodedOutputStream newInstance(ByteBuffer buffer) {
      if (buffer.hasArray()) {
         return new CodedOutputStream.HeapNioEncoder(buffer);
      } else if (buffer.isDirect() && !buffer.isReadOnly()) {
         return CodedOutputStream.UnsafeDirectNioEncoder.isSupported() ? newUnsafeInstance(buffer) : newSafeInstance(buffer);
      } else {
         throw new IllegalArgumentException("ByteBuffer is read-only");
      }
   }

   static CodedOutputStream newUnsafeInstance(ByteBuffer buffer) {
      return new CodedOutputStream.UnsafeDirectNioEncoder(buffer);
   }

   static CodedOutputStream newSafeInstance(ByteBuffer buffer) {
      return new CodedOutputStream.SafeDirectNioEncoder(buffer);
   }

   public void useDeterministicSerialization() {
      this.serializationDeterministic = true;
   }

   boolean isSerializationDeterministic() {
      return this.serializationDeterministic;
   }

   @Deprecated
   public static CodedOutputStream newInstance(ByteBuffer byteBuffer, int unused) {
      return newInstance(byteBuffer);
   }

   static CodedOutputStream newInstance(ByteOutput byteOutput, int bufferSize) {
      if (bufferSize < 0) {
         throw new IllegalArgumentException("bufferSize must be positive");
      } else {
         return new CodedOutputStream.ByteOutputEncoder(byteOutput, bufferSize);
      }
   }

   private CodedOutputStream() {
   }

   public abstract void writeTag(int fieldNumber, int wireType) throws IOException;

   public abstract void writeInt32(int fieldNumber, int value) throws IOException;

   public abstract void writeUInt32(int fieldNumber, int value) throws IOException;

   public final void writeSInt32(final int fieldNumber, final int value) throws IOException {
      this.writeUInt32(fieldNumber, encodeZigZag32(value));
   }

   public abstract void writeFixed32(int fieldNumber, int value) throws IOException;

   public final void writeSFixed32(final int fieldNumber, final int value) throws IOException {
      this.writeFixed32(fieldNumber, value);
   }

   public final void writeInt64(final int fieldNumber, final long value) throws IOException {
      this.writeUInt64(fieldNumber, value);
   }

   public abstract void writeUInt64(int fieldNumber, long value) throws IOException;

   public final void writeSInt64(final int fieldNumber, final long value) throws IOException {
      this.writeUInt64(fieldNumber, encodeZigZag64(value));
   }

   public abstract void writeFixed64(int fieldNumber, long value) throws IOException;

   public final void writeSFixed64(final int fieldNumber, final long value) throws IOException {
      this.writeFixed64(fieldNumber, value);
   }

   public final void writeFloat(final int fieldNumber, final float value) throws IOException {
      this.writeFixed32(fieldNumber, Float.floatToRawIntBits(value));
   }

   public final void writeDouble(final int fieldNumber, final double value) throws IOException {
      this.writeFixed64(fieldNumber, Double.doubleToRawLongBits(value));
   }

   public abstract void writeBool(int fieldNumber, boolean value) throws IOException;

   public final void writeEnum(final int fieldNumber, final int value) throws IOException {
      this.writeInt32(fieldNumber, value);
   }

   public abstract void writeString(int fieldNumber, String value) throws IOException;

   public abstract void writeBytes(int fieldNumber, ByteString value) throws IOException;

   public abstract void writeByteArray(int fieldNumber, byte[] value) throws IOException;

   public abstract void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException;

   public abstract void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException;

   public final void writeRawByte(final byte value) throws IOException {
      this.write(value);
   }

   public final void writeRawByte(final int value) throws IOException {
      this.write((byte)value);
   }

   public final void writeRawBytes(final byte[] value) throws IOException {
      this.write(value, 0, value.length);
   }

   public final void writeRawBytes(final byte[] value, int offset, int length) throws IOException {
      this.write(value, offset, length);
   }

   public final void writeRawBytes(final ByteString value) throws IOException {
      value.writeTo(this);
   }

   public abstract void writeRawBytes(final ByteBuffer value) throws IOException;

   public abstract void writeMessage(final int fieldNumber, final MessageLite value) throws IOException;

   public abstract void writeMessageSetExtension(final int fieldNumber, final MessageLite value) throws IOException;

   public abstract void writeRawMessageSetExtension(final int fieldNumber, final ByteString value) throws IOException;

   public abstract void writeInt32NoTag(final int value) throws IOException;

   public abstract void writeUInt32NoTag(int value) throws IOException;

   public final void writeSInt32NoTag(final int value) throws IOException {
      this.writeUInt32NoTag(encodeZigZag32(value));
   }

   public abstract void writeFixed32NoTag(int value) throws IOException;

   public final void writeSFixed32NoTag(final int value) throws IOException {
      this.writeFixed32NoTag(value);
   }

   public final void writeInt64NoTag(final long value) throws IOException {
      this.writeUInt64NoTag(value);
   }

   public abstract void writeUInt64NoTag(long value) throws IOException;

   public final void writeSInt64NoTag(final long value) throws IOException {
      this.writeUInt64NoTag(encodeZigZag64(value));
   }

   public abstract void writeFixed64NoTag(long value) throws IOException;

   public final void writeSFixed64NoTag(final long value) throws IOException {
      this.writeFixed64NoTag(value);
   }

   public final void writeFloatNoTag(final float value) throws IOException {
      this.writeFixed32NoTag(Float.floatToRawIntBits(value));
   }

   public final void writeDoubleNoTag(final double value) throws IOException {
      this.writeFixed64NoTag(Double.doubleToRawLongBits(value));
   }

   public final void writeBoolNoTag(final boolean value) throws IOException {
      this.write((byte)(value ? 1 : 0));
   }

   public final void writeEnumNoTag(final int value) throws IOException {
      this.writeInt32NoTag(value);
   }

   public abstract void writeStringNoTag(String value) throws IOException;

   public abstract void writeBytesNoTag(final ByteString value) throws IOException;

   public final void writeByteArrayNoTag(final byte[] value) throws IOException {
      this.writeByteArrayNoTag(value, 0, value.length);
   }

   public abstract void writeMessageNoTag(final MessageLite value) throws IOException;

   @Override
   public abstract void write(byte value) throws IOException;

   @Override
   public abstract void write(byte[] value, int offset, int length) throws IOException;

   @Override
   public abstract void writeLazy(byte[] value, int offset, int length) throws IOException;

   @Override
   public abstract void write(ByteBuffer value) throws IOException;

   @Override
   public abstract void writeLazy(ByteBuffer value) throws IOException;

   public static int computeInt32Size(final int fieldNumber, final int value) {
      return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
   }

   public static int computeUInt32Size(final int fieldNumber, final int value) {
      return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
   }

   public static int computeSInt32Size(final int fieldNumber, final int value) {
      return computeTagSize(fieldNumber) + computeSInt32SizeNoTag(value);
   }

   public static int computeFixed32Size(final int fieldNumber, final int value) {
      return computeTagSize(fieldNumber) + computeFixed32SizeNoTag(value);
   }

   public static int computeSFixed32Size(final int fieldNumber, final int value) {
      return computeTagSize(fieldNumber) + computeSFixed32SizeNoTag(value);
   }

   public static int computeInt64Size(final int fieldNumber, final long value) {
      return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
   }

   public static int computeUInt64Size(final int fieldNumber, final long value) {
      return computeTagSize(fieldNumber) + computeUInt64SizeNoTag(value);
   }

   public static int computeSInt64Size(final int fieldNumber, final long value) {
      return computeTagSize(fieldNumber) + computeSInt64SizeNoTag(value);
   }

   public static int computeFixed64Size(final int fieldNumber, final long value) {
      return computeTagSize(fieldNumber) + computeFixed64SizeNoTag(value);
   }

   public static int computeSFixed64Size(final int fieldNumber, final long value) {
      return computeTagSize(fieldNumber) + computeSFixed64SizeNoTag(value);
   }

   public static int computeFloatSize(final int fieldNumber, final float value) {
      return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
   }

   public static int computeDoubleSize(final int fieldNumber, final double value) {
      return computeTagSize(fieldNumber) + computeDoubleSizeNoTag(value);
   }

   public static int computeBoolSize(final int fieldNumber, final boolean value) {
      return computeTagSize(fieldNumber) + computeBoolSizeNoTag(value);
   }

   public static int computeEnumSize(final int fieldNumber, final int value) {
      return computeTagSize(fieldNumber) + computeEnumSizeNoTag(value);
   }

   public static int computeStringSize(final int fieldNumber, final String value) {
      return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
   }

   public static int computeBytesSize(final int fieldNumber, final ByteString value) {
      return computeTagSize(fieldNumber) + computeBytesSizeNoTag(value);
   }

   public static int computeByteArraySize(final int fieldNumber, final byte[] value) {
      return computeTagSize(fieldNumber) + computeByteArraySizeNoTag(value);
   }

   public static int computeByteBufferSize(final int fieldNumber, final ByteBuffer value) {
      return computeTagSize(fieldNumber) + computeByteBufferSizeNoTag(value);
   }

   @Deprecated
   @InlineMe(replacement = "value.computeSize(fieldNumber)")
   public static int computeLazyFieldSize(final int fieldNumber, final LazyFieldLite value) {
      return value.computeSize(fieldNumber);
   }

   public static int computeMessageSize(final int fieldNumber, final MessageLite value) {
      return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
   }

   public static int computeMessageSetExtensionSize(final int fieldNumber, final MessageLite value) {
      return computeTagSize(1) * 2 + computeUInt32Size(2, fieldNumber) + computeMessageSize(3, value);
   }

   public static int computeRawMessageSetExtensionSize(final int fieldNumber, final ByteString value) {
      return computeTagSize(1) * 2 + computeUInt32Size(2, fieldNumber) + computeBytesSize(3, value);
   }

   @Deprecated
   @InlineMe(replacement = "value.computeMessageSetExtensionSize(fieldNumber)")
   public static int computeLazyFieldMessageSetExtensionSize(final int fieldNumber, final LazyFieldLite value) {
      return value.computeMessageSetExtensionSize(fieldNumber);
   }

   public static int computeTagSize(final int fieldNumber) {
      return computeUInt32SizeNoTag(WireFormat.makeTag(fieldNumber, 0));
   }

   public static int computeInt32SizeNoTag(final int value) {
      return computeUInt64SizeNoTag(value);
   }

   public static int computeUInt32SizeNoTag(final int value) {
      int clz = Integer.numberOfLeadingZeros(value);
      return 352 - clz * 9 >>> 6;
   }

   public static int computeSInt32SizeNoTag(final int value) {
      return computeUInt32SizeNoTag(encodeZigZag32(value));
   }

   public static int computeFixed32SizeNoTag(final int unused) {
      return 4;
   }

   public static int computeSFixed32SizeNoTag(final int unused) {
      return 4;
   }

   public static int computeInt64SizeNoTag(final long value) {
      return computeUInt64SizeNoTag(value);
   }

   public static int computeUInt64SizeNoTag(long value) {
      int clz = Long.numberOfLeadingZeros(value);
      return 640 - clz * 9 >>> 6;
   }

   public static int computeSInt64SizeNoTag(final long value) {
      return computeUInt64SizeNoTag(encodeZigZag64(value));
   }

   public static int computeFixed64SizeNoTag(final long unused) {
      return 8;
   }

   public static int computeSFixed64SizeNoTag(final long unused) {
      return 8;
   }

   public static int computeFloatSizeNoTag(final float unused) {
      return 4;
   }

   public static int computeDoubleSizeNoTag(final double unused) {
      return 8;
   }

   public static int computeBoolSizeNoTag(final boolean unused) {
      return 1;
   }

   public static int computeEnumSizeNoTag(final int value) {
      return computeInt32SizeNoTag(value);
   }

   public static int computeStringSizeNoTag(final String value) {
      int length;
      try {
         length = Utf8.encodedLength(value);
      } catch (Utf8.UnpairedSurrogateException var4) {
         byte[] bytes = value.getBytes(Internal.UTF_8);
         length = bytes.length;
      }

      return computeLengthDelimitedFieldSize(length);
   }

   @Deprecated
   @InlineMe(replacement = "value.computeSizeNoTag()")
   public static int computeLazyFieldSizeNoTag(final LazyFieldLite value) {
      return value.computeSizeNoTag();
   }

   public static int computeBytesSizeNoTag(final ByteString value) {
      return computeLengthDelimitedFieldSize(value.size());
   }

   public static int computeByteArraySizeNoTag(final byte[] value) {
      return computeLengthDelimitedFieldSize(value.length);
   }

   public static int computeByteBufferSizeNoTag(final ByteBuffer value) {
      return computeLengthDelimitedFieldSize(value.capacity());
   }

   public static int computeMessageSizeNoTag(final MessageLite value) {
      return computeLengthDelimitedFieldSize(value.getSerializedSize());
   }

   static int computeLengthDelimitedFieldSize(int fieldLength) {
      return computeUInt32SizeNoTag(fieldLength) + fieldLength;
   }

   public static int encodeZigZag32(final int n) {
      return n << 1 ^ n >> 31;
   }

   public static long encodeZigZag64(final long n) {
      return n << 1 ^ n >> 63;
   }

   public abstract void flush() throws IOException;

   public abstract int spaceLeft();

   public final void checkNoSpaceLeft() {
      if (this.spaceLeft() != 0) {
         throw new IllegalStateException("Did not write as much data as expected.");
      }
   }

   public abstract int getTotalBytesWritten();

   abstract void writeByteArrayNoTag(final byte[] value, final int offset, final int length) throws IOException;

   final void inefficientWriteStringNoTag(String value, Utf8.UnpairedSurrogateException cause) throws IOException {
      logger.log(Level.WARNING, "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", (Throwable)cause);
      byte[] bytes = value.getBytes(Internal.UTF_8);

      try {
         this.writeUInt32NoTag(bytes.length);
         this.writeLazy(bytes, 0, bytes.length);
      } catch (IndexOutOfBoundsException var5) {
         throw new CodedOutputStream.OutOfSpaceException(var5);
      }
   }

   @Deprecated
   public final void writeGroup(final int fieldNumber, final MessageLite value) throws IOException {
      this.writeTag(fieldNumber, 3);
      this.writeGroupNoTag(value);
      this.writeTag(fieldNumber, 4);
   }

   @Deprecated
   public final void writeGroupNoTag(final MessageLite value) throws IOException {
      value.writeTo(this);
   }

   @Deprecated
   public static int computeGroupSize(final int fieldNumber, final MessageLite value) {
      return computeTagSize(fieldNumber) * 2 + value.getSerializedSize();
   }

   @Deprecated
   @InlineMe(replacement = "value.getSerializedSize()")
   public static int computeGroupSizeNoTag(final MessageLite value) {
      return value.getSerializedSize();
   }

   @Deprecated
   @InlineMe(replacement = "this.writeUInt32NoTag(value)")
   public final void writeRawVarint32(int value) throws IOException {
      this.writeUInt32NoTag(value);
   }

   @Deprecated
   @InlineMe(replacement = "this.writeUInt64NoTag(value)")
   public final void writeRawVarint64(long value) throws IOException {
      this.writeUInt64NoTag(value);
   }

   @Deprecated
   @InlineMe(replacement = "CodedOutputStream.computeUInt32SizeNoTag(value)", imports = "com.google.protobuf.CodedOutputStream")
   public static int computeRawVarint32Size(final int value) {
      return computeUInt32SizeNoTag(value);
   }

   @Deprecated
   @InlineMe(replacement = "CodedOutputStream.computeUInt64SizeNoTag(value)", imports = "com.google.protobuf.CodedOutputStream")
   public static int computeRawVarint64Size(long value) {
      return computeUInt64SizeNoTag(value);
   }

   @Deprecated
   @InlineMe(replacement = "this.writeFixed32NoTag(value)")
   public final void writeRawLittleEndian32(final int value) throws IOException {
      this.writeFixed32NoTag(value);
   }

   @Deprecated
   @InlineMe(replacement = "this.writeFixed64NoTag(value)")
   public final void writeRawLittleEndian64(final long value) throws IOException {
      this.writeFixed64NoTag(value);
   }

   private abstract static class AbstractBufferedEncoder extends CodedOutputStream {
      final byte[] buffer;
      final int limit;
      int position;
      int totalBytesWritten;

      AbstractBufferedEncoder(int bufferSize) {
         if (bufferSize < 0) {
            throw new IllegalArgumentException("bufferSize must be >= 0");
         } else {
            this.buffer = new byte[Math.max(bufferSize, 20)];
            this.limit = this.buffer.length;
         }
      }

      @Override
      public final int spaceLeft() {
         throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array or ByteBuffer.");
      }

      @Override
      public final int getTotalBytesWritten() {
         return this.totalBytesWritten;
      }

      final void buffer(byte value) {
         int position = this.position;
         this.buffer[position] = value;
         this.position = position + 1;
         this.totalBytesWritten++;
      }

      final void bufferTag(final int fieldNumber, final int wireType) {
         this.bufferUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
      }

      final void bufferInt32NoTag(final int value) {
         if (value >= 0) {
            this.bufferUInt32NoTag(value);
         } else {
            this.bufferUInt64NoTag(value);
         }
      }

      final void bufferUInt32NoTag(int value) {
         if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
            long originalPos = this.position;

            while ((value & -128) != 0) {
               UnsafeUtil.putByte(this.buffer, this.position++, (byte)(value | 128));
               value >>>= 7;
            }

            UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
            int delta = (int)(this.position - originalPos);
            this.totalBytesWritten += delta;
         } else {
            while ((value & -128) != 0) {
               this.buffer[this.position++] = (byte)(value | 128);
               this.totalBytesWritten++;
               value >>>= 7;
            }

            this.buffer[this.position++] = (byte)value;
            this.totalBytesWritten++;
         }
      }

      final void bufferUInt64NoTag(long value) {
         if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
            long originalPos = this.position;

            while ((value & -128L) != 0L) {
               UnsafeUtil.putByte(this.buffer, this.position++, (byte)((int)value | 128));
               value >>>= 7;
            }

            UnsafeUtil.putByte(this.buffer, this.position++, (byte)value);
            int delta = (int)(this.position - originalPos);
            this.totalBytesWritten += delta;
         } else {
            while ((value & -128L) != 0L) {
               this.buffer[this.position++] = (byte)((int)value | 128);
               this.totalBytesWritten++;
               value >>>= 7;
            }

            this.buffer[this.position++] = (byte)value;
            this.totalBytesWritten++;
         }
      }

      final void bufferFixed32NoTag(int value) {
         int position = this.position;
         this.buffer[position++] = (byte)value;
         this.buffer[position++] = (byte)(value >> 8);
         this.buffer[position++] = (byte)(value >> 16);
         this.buffer[position++] = (byte)(value >> 24);
         this.position = position;
         this.totalBytesWritten += 4;
      }

      final void bufferFixed64NoTag(long value) {
         int position = this.position;
         this.buffer[position++] = (byte)value;
         this.buffer[position++] = (byte)(value >> 8);
         this.buffer[position++] = (byte)(value >> 16);
         this.buffer[position++] = (byte)(value >> 24);
         this.buffer[position++] = (byte)(value >> 32);
         this.buffer[position++] = (byte)(value >> 40);
         this.buffer[position++] = (byte)(value >> 48);
         this.buffer[position++] = (byte)(value >> 56);
         this.position = position;
         this.totalBytesWritten += 8;
      }
   }

   private static class ArrayEncoder extends CodedOutputStream {
      private final byte[] buffer;
      private final int offset;
      private final int limit;
      private int position;

      ArrayEncoder(byte[] buffer, int offset, int length) {
         if (buffer == null) {
            throw new NullPointerException("buffer");
         } else if ((offset | length | buffer.length - (offset + length)) < 0) {
            throw new IllegalArgumentException(
               String.format(Locale.US, "Array range is invalid. Buffer.length=%d, offset=%d, length=%d", buffer.length, offset, length)
            );
         } else {
            this.buffer = buffer;
            this.offset = offset;
            this.position = offset;
            this.limit = offset + length;
         }
      }

      @Override
      public final void writeTag(final int fieldNumber, final int wireType) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
      }

      @Override
      public final void writeInt32(final int fieldNumber, final int value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeInt32NoTag(value);
      }

      @Override
      public final void writeUInt32(final int fieldNumber, final int value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeUInt32NoTag(value);
      }

      @Override
      public final void writeFixed32(final int fieldNumber, final int value) throws IOException {
         this.writeTag(fieldNumber, 5);
         this.writeFixed32NoTag(value);
      }

      @Override
      public final void writeUInt64(final int fieldNumber, final long value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeUInt64NoTag(value);
      }

      @Override
      public final void writeFixed64(final int fieldNumber, final long value) throws IOException {
         this.writeTag(fieldNumber, 1);
         this.writeFixed64NoTag(value);
      }

      @Override
      public final void writeBool(final int fieldNumber, final boolean value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.write((byte)(value ? 1 : 0));
      }

      @Override
      public final void writeString(final int fieldNumber, final String value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeStringNoTag(value);
      }

      @Override
      public final void writeBytes(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeBytesNoTag(value);
      }

      @Override
      public final void writeByteArray(final int fieldNumber, final byte[] value) throws IOException {
         this.writeByteArray(fieldNumber, value, 0, value.length);
      }

      @Override
      public final void writeByteArray(final int fieldNumber, final byte[] value, final int offset, final int length) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeByteArrayNoTag(value, offset, length);
      }

      @Override
      public final void writeByteBuffer(final int fieldNumber, final ByteBuffer value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeUInt32NoTag(value.capacity());
         this.writeRawBytes(value);
      }

      @Override
      public final void writeBytesNoTag(final ByteString value) throws IOException {
         this.writeUInt32NoTag(value.size());
         value.writeTo(this);
      }

      @Override
      public final void writeByteArrayNoTag(final byte[] value, int offset, int length) throws IOException {
         this.writeUInt32NoTag(length);
         this.write(value, offset, length);
      }

      @Override
      public final void writeRawBytes(final ByteBuffer value) throws IOException {
         if (value.hasArray()) {
            this.write(value.array(), value.arrayOffset(), value.capacity());
         } else {
            ByteBuffer duplicated = value.duplicate();
            Java8Compatibility.clear(duplicated);
            this.write(duplicated);
         }
      }

      @Override
      public final void writeMessage(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeMessageNoTag(value);
      }

      @Override
      public final void writeMessageSetExtension(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeMessage(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public final void writeRawMessageSetExtension(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeBytes(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public final void writeMessageNoTag(final MessageLite value) throws IOException {
         this.writeUInt32NoTag(value.getSerializedSize());
         value.writeTo(this);
      }

      @Override
      public final void write(byte value) throws IOException {
         int position = this.position;

         try {
            this.buffer[position++] = value;
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(position, this.limit, 1, var4);
         }

         this.position = position;
      }

      @Override
      public final void writeInt32NoTag(int value) throws IOException {
         if (value >= 0) {
            this.writeUInt32NoTag(value);
         } else {
            this.writeUInt64NoTag(value);
         }
      }

      @Override
      public final void writeUInt32NoTag(int value) throws IOException {
         int position = this.position;

         try {
            while ((value & -128) != 0) {
               this.buffer[position++] = (byte)(value | 128);
               value >>>= 7;
            }

            this.buffer[position++] = (byte)value;
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(position, this.limit, 1, var4);
         }

         this.position = position;
      }

      @Override
      public final void writeFixed32NoTag(int value) throws IOException {
         int position = this.position;

         try {
            this.buffer[position] = (byte)value;
            this.buffer[position + 1] = (byte)(value >> 8);
            this.buffer[position + 2] = (byte)(value >> 16);
            this.buffer[position + 3] = (byte)(value >> 24);
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(position, this.limit, 4, var4);
         }

         this.position = position + 4;
      }

      @Override
      public final void writeUInt64NoTag(long value) throws IOException {
         int position = this.position;
         if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS && this.spaceLeft() >= 10) {
            while (true) {
               if ((value & -128L) == 0L) {
                  UnsafeUtil.putByte(this.buffer, position++, (byte)value);
                  break;
               }

               UnsafeUtil.putByte(this.buffer, position++, (byte)((int)value | 128));
               value >>>= 7;
            }
         } else {
            try {
               while ((value & -128L) != 0L) {
                  this.buffer[position++] = (byte)((int)value | 128);
                  value >>>= 7;
               }

               this.buffer[position++] = (byte)value;
            } catch (IndexOutOfBoundsException var5) {
               throw new CodedOutputStream.OutOfSpaceException(position, this.limit, 1, var5);
            }
         }

         this.position = position;
      }

      @Override
      public final void writeFixed64NoTag(long value) throws IOException {
         int position = this.position;

         try {
            this.buffer[position] = (byte)value;
            this.buffer[position + 1] = (byte)(value >> 8);
            this.buffer[position + 2] = (byte)(value >> 16);
            this.buffer[position + 3] = (byte)(value >> 24);
            this.buffer[position + 4] = (byte)(value >> 32);
            this.buffer[position + 5] = (byte)(value >> 40);
            this.buffer[position + 6] = (byte)(value >> 48);
            this.buffer[position + 7] = (byte)(value >> 56);
         } catch (IndexOutOfBoundsException var5) {
            throw new CodedOutputStream.OutOfSpaceException(position, this.limit, 8, var5);
         }

         this.position = position + 8;
      }

      @Override
      public final void write(byte[] value, int offset, int length) throws IOException {
         try {
            System.arraycopy(value, offset, this.buffer, this.position, length);
         } catch (IndexOutOfBoundsException var5) {
            throw new CodedOutputStream.OutOfSpaceException(this.position, this.limit, length, var5);
         }

         this.position += length;
      }

      @Override
      public final void writeLazy(byte[] value, int offset, int length) throws IOException {
         this.write(value, offset, length);
      }

      @Override
      public final void write(ByteBuffer value) throws IOException {
         int length = value.remaining();

         try {
            value.get(this.buffer, this.position, length);
            this.position += length;
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(this.position, this.limit, length, var4);
         }
      }

      @Override
      public final void writeLazy(ByteBuffer value) throws IOException {
         this.write(value);
      }

      @Override
      public final void writeStringNoTag(String value) throws IOException {
         int oldPosition = this.position;

         try {
            int maxLength = value.length() * 3;
            int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
            int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
            if (minLengthVarIntSize == maxLengthVarIntSize) {
               this.position = oldPosition + minLengthVarIntSize;
               int newPosition = Utf8.encode(value, this.buffer, this.position, this.spaceLeft());
               this.position = oldPosition;
               int length = newPosition - oldPosition - minLengthVarIntSize;
               this.writeUInt32NoTag(length);
               this.position = newPosition;
            } else {
               int length = Utf8.encodedLength(value);
               this.writeUInt32NoTag(length);
               this.position = Utf8.encode(value, this.buffer, this.position, this.spaceLeft());
            }
         } catch (Utf8.UnpairedSurrogateException var8) {
            this.position = oldPosition;
            this.inefficientWriteStringNoTag(value, var8);
         } catch (IndexOutOfBoundsException var9) {
            throw new CodedOutputStream.OutOfSpaceException(var9);
         }
      }

      @Override
      public void flush() {
      }

      @Override
      public final int spaceLeft() {
         return this.limit - this.position;
      }

      @Override
      public final int getTotalBytesWritten() {
         return this.position - this.offset;
      }
   }

   private static final class ByteOutputEncoder extends CodedOutputStream.AbstractBufferedEncoder {
      private final ByteOutput out;

      ByteOutputEncoder(ByteOutput out, int bufferSize) {
         super(bufferSize);
         if (out == null) {
            throw new NullPointerException("out");
         } else {
            this.out = out;
         }
      }

      @Override
      public void writeTag(final int fieldNumber, final int wireType) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
      }

      @Override
      public void writeInt32(final int fieldNumber, final int value) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(fieldNumber, 0);
         this.bufferInt32NoTag(value);
      }

      @Override
      public void writeUInt32(final int fieldNumber, final int value) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(fieldNumber, 0);
         this.bufferUInt32NoTag(value);
      }

      @Override
      public void writeFixed32(final int fieldNumber, final int value) throws IOException {
         this.flushIfNotAvailable(14);
         this.bufferTag(fieldNumber, 5);
         this.bufferFixed32NoTag(value);
      }

      @Override
      public void writeUInt64(final int fieldNumber, final long value) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(fieldNumber, 0);
         this.bufferUInt64NoTag(value);
      }

      @Override
      public void writeFixed64(final int fieldNumber, final long value) throws IOException {
         this.flushIfNotAvailable(18);
         this.bufferTag(fieldNumber, 1);
         this.bufferFixed64NoTag(value);
      }

      @Override
      public void writeBool(final int fieldNumber, final boolean value) throws IOException {
         this.flushIfNotAvailable(11);
         this.bufferTag(fieldNumber, 0);
         this.buffer((byte)(value ? 1 : 0));
      }

      @Override
      public void writeString(final int fieldNumber, final String value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeStringNoTag(value);
      }

      @Override
      public void writeBytes(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeBytesNoTag(value);
      }

      @Override
      public void writeByteArray(final int fieldNumber, final byte[] value) throws IOException {
         this.writeByteArray(fieldNumber, value, 0, value.length);
      }

      @Override
      public void writeByteArray(final int fieldNumber, final byte[] value, final int offset, final int length) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeByteArrayNoTag(value, offset, length);
      }

      @Override
      public void writeByteBuffer(final int fieldNumber, final ByteBuffer value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeUInt32NoTag(value.capacity());
         this.writeRawBytes(value);
      }

      @Override
      public void writeBytesNoTag(final ByteString value) throws IOException {
         this.writeUInt32NoTag(value.size());
         value.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(final byte[] value, int offset, int length) throws IOException {
         this.writeUInt32NoTag(length);
         this.write(value, offset, length);
      }

      @Override
      public void writeRawBytes(final ByteBuffer value) throws IOException {
         if (value.hasArray()) {
            this.write(value.array(), value.arrayOffset(), value.capacity());
         } else {
            ByteBuffer duplicated = value.duplicate();
            Java8Compatibility.clear(duplicated);
            this.write(duplicated);
         }
      }

      @Override
      public void writeMessage(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeMessageNoTag(value);
      }

      @Override
      public void writeMessageSetExtension(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeMessage(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeBytes(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(final MessageLite value) throws IOException {
         this.writeUInt32NoTag(value.getSerializedSize());
         value.writeTo(this);
      }

      @Override
      public void write(byte value) throws IOException {
         if (this.position == this.limit) {
            this.doFlush();
         }

         this.buffer(value);
      }

      @Override
      public void writeInt32NoTag(int value) throws IOException {
         if (value >= 0) {
            this.writeUInt32NoTag(value);
         } else {
            this.writeUInt64NoTag(value);
         }
      }

      @Override
      public void writeUInt32NoTag(int value) throws IOException {
         this.flushIfNotAvailable(5);
         this.bufferUInt32NoTag(value);
      }

      @Override
      public void writeFixed32NoTag(final int value) throws IOException {
         this.flushIfNotAvailable(4);
         this.bufferFixed32NoTag(value);
      }

      @Override
      public void writeUInt64NoTag(long value) throws IOException {
         this.flushIfNotAvailable(10);
         this.bufferUInt64NoTag(value);
      }

      @Override
      public void writeFixed64NoTag(final long value) throws IOException {
         this.flushIfNotAvailable(8);
         this.bufferFixed64NoTag(value);
      }

      @Override
      public void writeStringNoTag(String value) throws IOException {
         int maxLength = value.length() * 3;
         int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
         if (maxLengthVarIntSize + maxLength > this.limit) {
            byte[] encodedBytes = new byte[maxLength];
            int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
            this.writeUInt32NoTag(actualLength);
            this.writeLazy(encodedBytes, 0, actualLength);
         } else {
            if (maxLengthVarIntSize + maxLength > this.limit - this.position) {
               this.doFlush();
            }

            int oldPosition = this.position;

            try {
               int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
               if (minLengthVarIntSize == maxLengthVarIntSize) {
                  this.position = oldPosition + minLengthVarIntSize;
                  int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
                  this.position = oldPosition;
                  int length = newPosition - oldPosition - minLengthVarIntSize;
                  this.bufferUInt32NoTag(length);
                  this.position = newPosition;
                  this.totalBytesWritten += length;
               } else {
                  int length = Utf8.encodedLength(value);
                  this.bufferUInt32NoTag(length);
                  this.position = Utf8.encode(value, this.buffer, this.position, length);
                  this.totalBytesWritten += length;
               }
            } catch (Utf8.UnpairedSurrogateException var8) {
               this.totalBytesWritten = this.totalBytesWritten - (this.position - oldPosition);
               this.position = oldPosition;
               this.inefficientWriteStringNoTag(value, var8);
            } catch (IndexOutOfBoundsException var9) {
               throw new CodedOutputStream.OutOfSpaceException(var9);
            }
         }
      }

      @Override
      public void flush() throws IOException {
         if (this.position > 0) {
            this.doFlush();
         }
      }

      @Override
      public void write(byte[] value, int offset, int length) throws IOException {
         this.flush();
         this.out.write(value, offset, length);
         this.totalBytesWritten += length;
      }

      @Override
      public void writeLazy(byte[] value, int offset, int length) throws IOException {
         this.flush();
         this.out.writeLazy(value, offset, length);
         this.totalBytesWritten += length;
      }

      @Override
      public void write(ByteBuffer value) throws IOException {
         this.flush();
         int length = value.remaining();
         this.out.write(value);
         this.totalBytesWritten += length;
      }

      @Override
      public void writeLazy(ByteBuffer value) throws IOException {
         this.flush();
         int length = value.remaining();
         this.out.writeLazy(value);
         this.totalBytesWritten += length;
      }

      private void flushIfNotAvailable(int requiredSize) throws IOException {
         if (this.limit - this.position < requiredSize) {
            this.doFlush();
         }
      }

      private void doFlush() throws IOException {
         this.out.write(this.buffer, 0, this.position);
         this.position = 0;
      }
   }

   private static final class HeapNioEncoder extends CodedOutputStream.ArrayEncoder {
      private final ByteBuffer byteBuffer;
      private int initialPosition;

      HeapNioEncoder(ByteBuffer byteBuffer) {
         super(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
         this.byteBuffer = byteBuffer;
         this.initialPosition = byteBuffer.position();
      }

      @Override
      public void flush() {
         Java8Compatibility.position(this.byteBuffer, this.initialPosition + this.getTotalBytesWritten());
      }
   }

   public static class OutOfSpaceException extends IOException {
      private static final long serialVersionUID = -6947486886997889499L;
      private static final String MESSAGE = "CodedOutputStream was writing to a flat byte array and ran out of space.";

      OutOfSpaceException() {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.");
      }

      OutOfSpaceException(String explanationMessage) {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage);
      }

      OutOfSpaceException(Throwable cause) {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.", cause);
      }

      OutOfSpaceException(String explanationMessage, Throwable cause) {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + explanationMessage, cause);
      }

      OutOfSpaceException(int position, int limit, int length) {
         this(position, limit, length, null);
      }

      OutOfSpaceException(int position, int limit, int length, Throwable cause) {
         this((long)position, (long)limit, length, cause);
      }

      OutOfSpaceException(long position, long limit, int length) {
         this(position, limit, length, null);
      }

      OutOfSpaceException(long position, long limit, int length, Throwable cause) {
         this(String.format(Locale.US, "Pos: %d, limit: %d, len: %d", position, limit, length), cause);
      }
   }

   private static final class OutputStreamEncoder extends CodedOutputStream.AbstractBufferedEncoder {
      private final OutputStream out;

      OutputStreamEncoder(OutputStream out, int bufferSize) {
         super(bufferSize);
         if (out == null) {
            throw new NullPointerException("out");
         } else {
            this.out = out;
         }
      }

      @Override
      public void writeTag(final int fieldNumber, final int wireType) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
      }

      @Override
      public void writeInt32(final int fieldNumber, final int value) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(fieldNumber, 0);
         this.bufferInt32NoTag(value);
      }

      @Override
      public void writeUInt32(final int fieldNumber, final int value) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(fieldNumber, 0);
         this.bufferUInt32NoTag(value);
      }

      @Override
      public void writeFixed32(final int fieldNumber, final int value) throws IOException {
         this.flushIfNotAvailable(14);
         this.bufferTag(fieldNumber, 5);
         this.bufferFixed32NoTag(value);
      }

      @Override
      public void writeUInt64(final int fieldNumber, final long value) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(fieldNumber, 0);
         this.bufferUInt64NoTag(value);
      }

      @Override
      public void writeFixed64(final int fieldNumber, final long value) throws IOException {
         this.flushIfNotAvailable(18);
         this.bufferTag(fieldNumber, 1);
         this.bufferFixed64NoTag(value);
      }

      @Override
      public void writeBool(final int fieldNumber, final boolean value) throws IOException {
         this.flushIfNotAvailable(11);
         this.bufferTag(fieldNumber, 0);
         this.buffer((byte)(value ? 1 : 0));
      }

      @Override
      public void writeString(final int fieldNumber, final String value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeStringNoTag(value);
      }

      @Override
      public void writeBytes(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeBytesNoTag(value);
      }

      @Override
      public void writeByteArray(final int fieldNumber, final byte[] value) throws IOException {
         this.writeByteArray(fieldNumber, value, 0, value.length);
      }

      @Override
      public void writeByteArray(final int fieldNumber, final byte[] value, final int offset, final int length) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeByteArrayNoTag(value, offset, length);
      }

      @Override
      public void writeByteBuffer(final int fieldNumber, final ByteBuffer value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeUInt32NoTag(value.capacity());
         this.writeRawBytes(value);
      }

      @Override
      public void writeBytesNoTag(final ByteString value) throws IOException {
         this.writeUInt32NoTag(value.size());
         value.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(final byte[] value, int offset, int length) throws IOException {
         this.writeUInt32NoTag(length);
         this.write(value, offset, length);
      }

      @Override
      public void writeRawBytes(final ByteBuffer value) throws IOException {
         if (value.hasArray()) {
            this.write(value.array(), value.arrayOffset(), value.capacity());
         } else {
            ByteBuffer duplicated = value.duplicate();
            Java8Compatibility.clear(duplicated);
            this.write(duplicated);
         }
      }

      @Override
      public void writeMessage(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeMessageNoTag(value);
      }

      @Override
      public void writeMessageSetExtension(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeMessage(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeBytes(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(final MessageLite value) throws IOException {
         this.writeUInt32NoTag(value.getSerializedSize());
         value.writeTo(this);
      }

      @Override
      public void write(byte value) throws IOException {
         if (this.position == this.limit) {
            this.doFlush();
         }

         this.buffer(value);
      }

      @Override
      public void writeInt32NoTag(int value) throws IOException {
         if (value >= 0) {
            this.writeUInt32NoTag(value);
         } else {
            this.writeUInt64NoTag(value);
         }
      }

      @Override
      public void writeUInt32NoTag(int value) throws IOException {
         this.flushIfNotAvailable(5);
         this.bufferUInt32NoTag(value);
      }

      @Override
      public void writeFixed32NoTag(final int value) throws IOException {
         this.flushIfNotAvailable(4);
         this.bufferFixed32NoTag(value);
      }

      @Override
      public void writeUInt64NoTag(long value) throws IOException {
         this.flushIfNotAvailable(10);
         this.bufferUInt64NoTag(value);
      }

      @Override
      public void writeFixed64NoTag(final long value) throws IOException {
         this.flushIfNotAvailable(8);
         this.bufferFixed64NoTag(value);
      }

      @Override
      public void writeStringNoTag(String value) throws IOException {
         try {
            int maxLength = value.length() * 3;
            int maxLengthVarIntSize = computeUInt32SizeNoTag(maxLength);
            if (maxLengthVarIntSize + maxLength > this.limit) {
               byte[] encodedBytes = new byte[maxLength];
               int actualLength = Utf8.encode(value, encodedBytes, 0, maxLength);
               this.writeUInt32NoTag(actualLength);
               this.writeLazy(encodedBytes, 0, actualLength);
               return;
            }

            if (maxLengthVarIntSize + maxLength > this.limit - this.position) {
               this.doFlush();
            }

            int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
            int oldPosition = this.position;

            try {
               int length;
               if (minLengthVarIntSize == maxLengthVarIntSize) {
                  this.position = oldPosition + minLengthVarIntSize;
                  int newPosition = Utf8.encode(value, this.buffer, this.position, this.limit - this.position);
                  this.position = oldPosition;
                  length = newPosition - oldPosition - minLengthVarIntSize;
                  this.bufferUInt32NoTag(length);
                  this.position = newPosition;
               } else {
                  length = Utf8.encodedLength(value);
                  this.bufferUInt32NoTag(length);
                  this.position = Utf8.encode(value, this.buffer, this.position, length);
               }

               this.totalBytesWritten += length;
            } catch (Utf8.UnpairedSurrogateException var8) {
               this.totalBytesWritten = this.totalBytesWritten - (this.position - oldPosition);
               this.position = oldPosition;
               throw var8;
            } catch (ArrayIndexOutOfBoundsException var9) {
               throw new CodedOutputStream.OutOfSpaceException(var9);
            }
         } catch (Utf8.UnpairedSurrogateException var10) {
            this.inefficientWriteStringNoTag(value, var10);
         }
      }

      @Override
      public void flush() throws IOException {
         if (this.position > 0) {
            this.doFlush();
         }
      }

      @Override
      public void write(byte[] value, int offset, int length) throws IOException {
         if (this.limit - this.position >= length) {
            System.arraycopy(value, offset, this.buffer, this.position, length);
            this.position += length;
            this.totalBytesWritten += length;
         } else {
            int bytesWritten = this.limit - this.position;
            System.arraycopy(value, offset, this.buffer, this.position, bytesWritten);
            offset += bytesWritten;
            length -= bytesWritten;
            this.position = this.limit;
            this.totalBytesWritten += bytesWritten;
            this.doFlush();
            if (length <= this.limit) {
               System.arraycopy(value, offset, this.buffer, 0, length);
               this.position = length;
            } else {
               this.out.write(value, offset, length);
            }

            this.totalBytesWritten += length;
         }
      }

      @Override
      public void writeLazy(byte[] value, int offset, int length) throws IOException {
         this.write(value, offset, length);
      }

      @Override
      public void write(ByteBuffer value) throws IOException {
         int length = value.remaining();
         if (this.limit - this.position >= length) {
            value.get(this.buffer, this.position, length);
            this.position += length;
            this.totalBytesWritten += length;
         } else {
            int bytesWritten = this.limit - this.position;
            value.get(this.buffer, this.position, bytesWritten);
            length -= bytesWritten;
            this.position = this.limit;
            this.totalBytesWritten += bytesWritten;
            this.doFlush();

            while (length > this.limit) {
               value.get(this.buffer, 0, this.limit);
               this.out.write(this.buffer, 0, this.limit);
               length -= this.limit;
               this.totalBytesWritten = this.totalBytesWritten + this.limit;
            }

            value.get(this.buffer, 0, length);
            this.position = length;
            this.totalBytesWritten += length;
         }
      }

      @Override
      public void writeLazy(ByteBuffer value) throws IOException {
         this.write(value);
      }

      private void flushIfNotAvailable(int requiredSize) throws IOException {
         if (this.limit - this.position < requiredSize) {
            this.doFlush();
         }
      }

      private void doFlush() throws IOException {
         this.out.write(this.buffer, 0, this.position);
         this.position = 0;
      }
   }

   private static final class SafeDirectNioEncoder extends CodedOutputStream {
      private final ByteBuffer originalBuffer;
      private final ByteBuffer buffer;
      private final int initialPosition;

      SafeDirectNioEncoder(ByteBuffer buffer) {
         this.originalBuffer = buffer;
         this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
         this.initialPosition = buffer.position();
      }

      @Override
      public void writeTag(final int fieldNumber, final int wireType) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
      }

      @Override
      public void writeInt32(final int fieldNumber, final int value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeInt32NoTag(value);
      }

      @Override
      public void writeUInt32(final int fieldNumber, final int value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeUInt32NoTag(value);
      }

      @Override
      public void writeFixed32(final int fieldNumber, final int value) throws IOException {
         this.writeTag(fieldNumber, 5);
         this.writeFixed32NoTag(value);
      }

      @Override
      public void writeUInt64(final int fieldNumber, final long value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeUInt64NoTag(value);
      }

      @Override
      public void writeFixed64(final int fieldNumber, final long value) throws IOException {
         this.writeTag(fieldNumber, 1);
         this.writeFixed64NoTag(value);
      }

      @Override
      public void writeBool(final int fieldNumber, final boolean value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.write((byte)(value ? 1 : 0));
      }

      @Override
      public void writeString(final int fieldNumber, final String value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeStringNoTag(value);
      }

      @Override
      public void writeBytes(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeBytesNoTag(value);
      }

      @Override
      public void writeByteArray(final int fieldNumber, final byte[] value) throws IOException {
         this.writeByteArray(fieldNumber, value, 0, value.length);
      }

      @Override
      public void writeByteArray(final int fieldNumber, final byte[] value, final int offset, final int length) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeByteArrayNoTag(value, offset, length);
      }

      @Override
      public void writeByteBuffer(final int fieldNumber, final ByteBuffer value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeUInt32NoTag(value.capacity());
         this.writeRawBytes(value);
      }

      @Override
      public void writeMessage(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeMessageNoTag(value);
      }

      @Override
      public void writeMessageSetExtension(final int fieldNumber, final MessageLite value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeMessage(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(final int fieldNumber, final ByteString value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeBytes(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(final MessageLite value) throws IOException {
         this.writeUInt32NoTag(value.getSerializedSize());
         value.writeTo(this);
      }

      @Override
      public void write(byte value) throws IOException {
         try {
            this.buffer.put(value);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(this.buffer.position(), this.buffer.limit(), 1, var3);
         }
      }

      @Override
      public void writeBytesNoTag(final ByteString value) throws IOException {
         this.writeUInt32NoTag(value.size());
         value.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(final byte[] value, int offset, int length) throws IOException {
         this.writeUInt32NoTag(length);
         this.write(value, offset, length);
      }

      @Override
      public void writeRawBytes(final ByteBuffer value) throws IOException {
         if (value.hasArray()) {
            this.write(value.array(), value.arrayOffset(), value.capacity());
         } else {
            ByteBuffer duplicated = value.duplicate();
            Java8Compatibility.clear(duplicated);
            this.write(duplicated);
         }
      }

      @Override
      public void writeInt32NoTag(int value) throws IOException {
         if (value >= 0) {
            this.writeUInt32NoTag(value);
         } else {
            this.writeUInt64NoTag(value);
         }
      }

      @Override
      public void writeUInt32NoTag(int value) throws IOException {
         try {
            while ((value & -128) != 0) {
               this.buffer.put((byte)(value | 128));
               value >>>= 7;
            }

            this.buffer.put((byte)value);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeFixed32NoTag(int value) throws IOException {
         try {
            this.buffer.putInt(value);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(this.buffer.position(), this.buffer.limit(), 4, var3);
         }
      }

      @Override
      public void writeUInt64NoTag(long value) throws IOException {
         try {
            while ((value & -128L) != 0L) {
               this.buffer.put((byte)((int)value | 128));
               value >>>= 7;
            }

            this.buffer.put((byte)value);
         } catch (BufferOverflowException var4) {
            throw new CodedOutputStream.OutOfSpaceException(var4);
         }
      }

      @Override
      public void writeFixed64NoTag(long value) throws IOException {
         try {
            this.buffer.putLong(value);
         } catch (BufferOverflowException var4) {
            throw new CodedOutputStream.OutOfSpaceException(this.buffer.position(), this.buffer.limit(), 8, var4);
         }
      }

      @Override
      public void write(byte[] value, int offset, int length) throws IOException {
         try {
            this.buffer.put(value, offset, length);
         } catch (IndexOutOfBoundsException var5) {
            throw new CodedOutputStream.OutOfSpaceException(var5);
         } catch (BufferOverflowException var6) {
            throw new CodedOutputStream.OutOfSpaceException(var6);
         }
      }

      @Override
      public void writeLazy(byte[] value, int offset, int length) throws IOException {
         this.write(value, offset, length);
      }

      @Override
      public void write(ByteBuffer value) throws IOException {
         try {
            this.buffer.put(value);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeLazy(ByteBuffer value) throws IOException {
         this.write(value);
      }

      @Override
      public void writeStringNoTag(String value) throws IOException {
         int startPos = this.buffer.position();

         try {
            int maxEncodedSize = value.length() * 3;
            int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
            int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
            if (minLengthVarIntSize == maxLengthVarIntSize) {
               int startOfBytes = this.buffer.position() + minLengthVarIntSize;
               Java8Compatibility.position(this.buffer, startOfBytes);
               this.encode(value);
               int endOfBytes = this.buffer.position();
               Java8Compatibility.position(this.buffer, startPos);
               this.writeUInt32NoTag(endOfBytes - startOfBytes);
               Java8Compatibility.position(this.buffer, endOfBytes);
            } else {
               int length = Utf8.encodedLength(value);
               this.writeUInt32NoTag(length);
               this.encode(value);
            }
         } catch (Utf8.UnpairedSurrogateException var8) {
            Java8Compatibility.position(this.buffer, startPos);
            this.inefficientWriteStringNoTag(value, var8);
         } catch (IllegalArgumentException var9) {
            throw new CodedOutputStream.OutOfSpaceException(var9);
         }
      }

      @Override
      public void flush() {
         Java8Compatibility.position(this.originalBuffer, this.buffer.position());
      }

      @Override
      public int spaceLeft() {
         return this.buffer.remaining();
      }

      @Override
      public int getTotalBytesWritten() {
         return this.buffer.position() - this.initialPosition;
      }

      private void encode(String value) throws IOException {
         try {
            Utf8.encodeUtf8(value, this.buffer);
         } catch (IndexOutOfBoundsException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }
   }

   private static final class UnsafeDirectNioEncoder extends CodedOutputStream {
      private final ByteBuffer originalBuffer;
      private final ByteBuffer buffer;
      private final long address;
      private final long initialPosition;
      private final long limit;
      private final long oneVarintLimit;
      private long position;

      UnsafeDirectNioEncoder(ByteBuffer buffer) {
         this.originalBuffer = buffer;
         this.buffer = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
         this.address = UnsafeUtil.addressOffset(buffer);
         this.initialPosition = this.address + buffer.position();
         this.limit = this.address + buffer.limit();
         this.oneVarintLimit = this.limit - 10L;
         this.position = this.initialPosition;
      }

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      @Override
      public void writeTag(int fieldNumber, int wireType) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(fieldNumber, wireType));
      }

      @Override
      public void writeInt32(int fieldNumber, int value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeInt32NoTag(value);
      }

      @Override
      public void writeUInt32(int fieldNumber, int value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeUInt32NoTag(value);
      }

      @Override
      public void writeFixed32(int fieldNumber, int value) throws IOException {
         this.writeTag(fieldNumber, 5);
         this.writeFixed32NoTag(value);
      }

      @Override
      public void writeUInt64(int fieldNumber, long value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.writeUInt64NoTag(value);
      }

      @Override
      public void writeFixed64(int fieldNumber, long value) throws IOException {
         this.writeTag(fieldNumber, 1);
         this.writeFixed64NoTag(value);
      }

      @Override
      public void writeBool(int fieldNumber, boolean value) throws IOException {
         this.writeTag(fieldNumber, 0);
         this.write((byte)(value ? 1 : 0));
      }

      @Override
      public void writeString(int fieldNumber, String value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeStringNoTag(value);
      }

      @Override
      public void writeBytes(int fieldNumber, ByteString value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeBytesNoTag(value);
      }

      @Override
      public void writeByteArray(int fieldNumber, byte[] value) throws IOException {
         this.writeByteArray(fieldNumber, value, 0, value.length);
      }

      @Override
      public void writeByteArray(int fieldNumber, byte[] value, int offset, int length) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeByteArrayNoTag(value, offset, length);
      }

      @Override
      public void writeByteBuffer(int fieldNumber, ByteBuffer value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeUInt32NoTag(value.capacity());
         this.writeRawBytes(value);
      }

      @Override
      public void writeMessage(int fieldNumber, MessageLite value) throws IOException {
         this.writeTag(fieldNumber, 2);
         this.writeMessageNoTag(value);
      }

      @Override
      public void writeMessageSetExtension(int fieldNumber, MessageLite value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeMessage(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(int fieldNumber, ByteString value) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, fieldNumber);
         this.writeBytes(3, value);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(MessageLite value) throws IOException {
         this.writeUInt32NoTag(value.getSerializedSize());
         value.writeTo(this);
      }

      @Override
      public void write(byte value) throws IOException {
         if (this.position >= this.limit) {
            throw new CodedOutputStream.OutOfSpaceException(this.position, this.limit, 1);
         } else {
            UnsafeUtil.putByte(this.position++, value);
         }
      }

      @Override
      public void writeBytesNoTag(ByteString value) throws IOException {
         this.writeUInt32NoTag(value.size());
         value.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(byte[] value, int offset, int length) throws IOException {
         this.writeUInt32NoTag(length);
         this.write(value, offset, length);
      }

      @Override
      public void writeRawBytes(ByteBuffer value) throws IOException {
         if (value.hasArray()) {
            this.write(value.array(), value.arrayOffset(), value.capacity());
         } else {
            ByteBuffer duplicated = value.duplicate();
            Java8Compatibility.clear(duplicated);
            this.write(duplicated);
         }
      }

      @Override
      public void writeInt32NoTag(int value) throws IOException {
         if (value >= 0) {
            this.writeUInt32NoTag(value);
         } else {
            this.writeUInt64NoTag(value);
         }
      }

      @Override
      public void writeUInt32NoTag(int value) throws IOException {
         long position = this.position;
         if (position > this.oneVarintLimit) {
            while (true) {
               if (position >= this.limit) {
                  throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", position, this.limit, 1));
               }

               if ((value & -128) == 0) {
                  UnsafeUtil.putByte(position++, (byte)value);
                  break;
               }

               UnsafeUtil.putByte(position++, (byte)(value | 128));
               value >>>= 7;
            }
         } else {
            while ((value & -128) != 0) {
               UnsafeUtil.putByte(position++, (byte)(value | 128));
               value >>>= 7;
            }

            UnsafeUtil.putByte(position++, (byte)value);
         }

         this.position = position;
      }

      @Override
      public void writeFixed32NoTag(int value) throws IOException {
         try {
            this.buffer.putInt(this.bufferPos(this.position), value);
         } catch (IndexOutOfBoundsException var3) {
            throw new CodedOutputStream.OutOfSpaceException(this.position, this.limit, 4, var3);
         }

         this.position += 4L;
      }

      @Override
      public void writeUInt64NoTag(long value) throws IOException {
         long position = this.position;
         if (position > this.oneVarintLimit) {
            while (true) {
               if (position >= this.limit) {
                  throw new CodedOutputStream.OutOfSpaceException(position, this.limit, 1);
               }

               if ((value & -128L) == 0L) {
                  UnsafeUtil.putByte(position++, (byte)value);
                  break;
               }

               UnsafeUtil.putByte(position++, (byte)((int)value | 128));
               value >>>= 7;
            }
         } else {
            while ((value & -128L) != 0L) {
               UnsafeUtil.putByte(position++, (byte)((int)value | 128));
               value >>>= 7;
            }

            UnsafeUtil.putByte(position++, (byte)value);
         }

         this.position = position;
      }

      @Override
      public void writeFixed64NoTag(long value) throws IOException {
         try {
            this.buffer.putLong(this.bufferPos(this.position), value);
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(this.position, this.limit, 8, var4);
         }

         this.position += 8L;
      }

      @Override
      public void write(byte[] value, int offset, int length) throws IOException {
         if (value != null && offset >= 0 && length >= 0 && value.length - length >= offset && this.limit - length >= this.position) {
            UnsafeUtil.copyMemory(value, offset, this.position, length);
            this.position += length;
         } else if (value == null) {
            throw new NullPointerException("value");
         } else {
            throw new CodedOutputStream.OutOfSpaceException(this.position, this.limit, length);
         }
      }

      @Override
      public void writeLazy(byte[] value, int offset, int length) throws IOException {
         this.write(value, offset, length);
      }

      @Override
      public void write(ByteBuffer value) throws IOException {
         try {
            int length = value.remaining();
            this.repositionBuffer(this.position);
            this.buffer.put(value);
            this.position += length;
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeLazy(ByteBuffer value) throws IOException {
         this.write(value);
      }

      @Override
      public void writeStringNoTag(String value) throws IOException {
         long prevPos = this.position;

         try {
            int maxEncodedSize = value.length() * 3;
            int maxLengthVarIntSize = computeUInt32SizeNoTag(maxEncodedSize);
            int minLengthVarIntSize = computeUInt32SizeNoTag(value.length());
            if (minLengthVarIntSize == maxLengthVarIntSize) {
               int stringStart = this.bufferPos(this.position) + minLengthVarIntSize;
               Java8Compatibility.position(this.buffer, stringStart);
               Utf8.encodeUtf8(value, this.buffer);
               int length = this.buffer.position() - stringStart;
               this.writeUInt32NoTag(length);
               this.position += length;
            } else {
               int length = Utf8.encodedLength(value);
               this.writeUInt32NoTag(length);
               this.repositionBuffer(this.position);
               Utf8.encodeUtf8(value, this.buffer);
               this.position += length;
            }
         } catch (Utf8.UnpairedSurrogateException var9) {
            this.position = prevPos;
            this.repositionBuffer(this.position);
            this.inefficientWriteStringNoTag(value, var9);
         } catch (IllegalArgumentException var10) {
            throw new CodedOutputStream.OutOfSpaceException(var10);
         } catch (IndexOutOfBoundsException var11) {
            throw new CodedOutputStream.OutOfSpaceException(var11);
         }
      }

      @Override
      public void flush() {
         Java8Compatibility.position(this.originalBuffer, this.bufferPos(this.position));
      }

      @Override
      public int spaceLeft() {
         return (int)(this.limit - this.position);
      }

      @Override
      public int getTotalBytesWritten() {
         return (int)(this.position - this.initialPosition);
      }

      private void repositionBuffer(long pos) {
         Java8Compatibility.position(this.buffer, this.bufferPos(pos));
      }

      private int bufferPos(long pos) {
         return (int)(pos - this.address);
      }
   }
}
