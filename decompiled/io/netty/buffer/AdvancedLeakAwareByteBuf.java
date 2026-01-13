package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class AdvancedLeakAwareByteBuf extends SimpleLeakAwareByteBuf {
   private static final String PROP_ACQUIRE_AND_RELEASE_ONLY = "io.netty.leakDetection.acquireAndReleaseOnly";
   private static final boolean ACQUIRE_AND_RELEASE_ONLY = SystemPropertyUtil.getBoolean("io.netty.leakDetection.acquireAndReleaseOnly", false);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AdvancedLeakAwareByteBuf.class);

   AdvancedLeakAwareByteBuf(ByteBuf buf, ResourceLeakTracker<ByteBuf> leak) {
      super(buf, leak);
   }

   AdvancedLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leak) {
      super(wrapped, trackedByteBuf, leak);
   }

   static void recordLeakNonRefCountingOperation(ResourceLeakTracker<ByteBuf> leak) {
      if (!ACQUIRE_AND_RELEASE_ONLY) {
         leak.record();
      }
   }

   @Override
   public ByteBuf order(ByteOrder endianness) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.order(endianness);
   }

   @Override
   public ByteBuf slice() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.slice();
   }

   @Override
   public ByteBuf slice(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.slice(index, length);
   }

   @Override
   public ByteBuf retainedSlice() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.retainedSlice();
   }

   @Override
   public ByteBuf retainedSlice(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.retainedSlice(index, length);
   }

   @Override
   public ByteBuf retainedDuplicate() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.retainedDuplicate();
   }

   @Override
   public ByteBuf readRetainedSlice(int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readRetainedSlice(length);
   }

   @Override
   public ByteBuf duplicate() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.duplicate();
   }

   @Override
   public ByteBuf readSlice(int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readSlice(length);
   }

   @Override
   public ByteBuf discardReadBytes() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.discardReadBytes();
   }

   @Override
   public ByteBuf discardSomeReadBytes() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.discardSomeReadBytes();
   }

   @Override
   public ByteBuf ensureWritable(int minWritableBytes) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.ensureWritable(minWritableBytes);
   }

   @Override
   public int ensureWritable(int minWritableBytes, boolean force) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.ensureWritable(minWritableBytes, force);
   }

   @Override
   public boolean getBoolean(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBoolean(index);
   }

   @Override
   public byte getByte(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getByte(index);
   }

   @Override
   public short getUnsignedByte(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedByte(index);
   }

   @Override
   public short getShort(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getShort(index);
   }

   @Override
   public int getUnsignedShort(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedShort(index);
   }

   @Override
   public int getMedium(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getMedium(index);
   }

   @Override
   public int getUnsignedMedium(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedMedium(index);
   }

   @Override
   public int getInt(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getInt(index);
   }

   @Override
   public long getUnsignedInt(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedInt(index);
   }

   @Override
   public long getLong(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getLong(index);
   }

   @Override
   public char getChar(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getChar(index);
   }

   @Override
   public float getFloat(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getFloat(index);
   }

   @Override
   public double getDouble(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getDouble(index);
   }

   @Override
   public ByteBuf getBytes(int index, ByteBuf dst) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, dst);
   }

   @Override
   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, dst, length);
   }

   @Override
   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, dst, dstIndex, length);
   }

   @Override
   public ByteBuf getBytes(int index, byte[] dst) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, dst);
   }

   @Override
   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, dst, dstIndex, length);
   }

   @Override
   public ByteBuf getBytes(int index, ByteBuffer dst) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, dst);
   }

   @Override
   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, out, length);
   }

   @Override
   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, out, length);
   }

   @Override
   public CharSequence getCharSequence(int index, int length, Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getCharSequence(index, length, charset);
   }

   @Override
   public ByteBuf setBoolean(int index, boolean value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBoolean(index, value);
   }

   @Override
   public ByteBuf setByte(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setByte(index, value);
   }

   @Override
   public ByteBuf setShort(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setShort(index, value);
   }

   @Override
   public ByteBuf setMedium(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setMedium(index, value);
   }

   @Override
   public ByteBuf setInt(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setInt(index, value);
   }

   @Override
   public ByteBuf setLong(int index, long value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setLong(index, value);
   }

   @Override
   public ByteBuf setChar(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setChar(index, value);
   }

   @Override
   public ByteBuf setFloat(int index, float value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setFloat(index, value);
   }

   @Override
   public ByteBuf setDouble(int index, double value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setDouble(index, value);
   }

   @Override
   public ByteBuf setBytes(int index, ByteBuf src) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, src);
   }

   @Override
   public ByteBuf setBytes(int index, ByteBuf src, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, src, length);
   }

   @Override
   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, src, srcIndex, length);
   }

   @Override
   public ByteBuf setBytes(int index, byte[] src) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, src);
   }

   @Override
   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, src, srcIndex, length);
   }

   @Override
   public ByteBuf setBytes(int index, ByteBuffer src) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, src);
   }

   @Override
   public int setBytes(int index, InputStream in, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, in, length);
   }

   @Override
   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, in, length);
   }

   @Override
   public ByteBuf setZero(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setZero(index, length);
   }

   @Override
   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setCharSequence(index, sequence, charset);
   }

   @Override
   public boolean readBoolean() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBoolean();
   }

   @Override
   public byte readByte() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readByte();
   }

   @Override
   public short readUnsignedByte() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedByte();
   }

   @Override
   public short readShort() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readShort();
   }

   @Override
   public int readUnsignedShort() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedShort();
   }

   @Override
   public int readMedium() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readMedium();
   }

   @Override
   public int readUnsignedMedium() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedMedium();
   }

   @Override
   public int readInt() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readInt();
   }

   @Override
   public long readUnsignedInt() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedInt();
   }

   @Override
   public long readLong() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readLong();
   }

   @Override
   public char readChar() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readChar();
   }

   @Override
   public float readFloat() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readFloat();
   }

   @Override
   public double readDouble() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readDouble();
   }

   @Override
   public ByteBuf readBytes(int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(length);
   }

   @Override
   public ByteBuf readBytes(ByteBuf dst) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(dst);
   }

   @Override
   public ByteBuf readBytes(ByteBuf dst, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(dst, length);
   }

   @Override
   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(dst, dstIndex, length);
   }

   @Override
   public ByteBuf readBytes(byte[] dst) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(dst);
   }

   @Override
   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(dst, dstIndex, length);
   }

   @Override
   public ByteBuf readBytes(ByteBuffer dst) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(dst);
   }

   @Override
   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(out, length);
   }

   @Override
   public int readBytes(GatheringByteChannel out, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(out, length);
   }

   @Override
   public CharSequence readCharSequence(int length, Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readCharSequence(length, charset);
   }

   @Override
   public String readString(int length, Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readString(length, charset);
   }

   @Override
   public ByteBuf skipBytes(int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.skipBytes(length);
   }

   @Override
   public ByteBuf writeBoolean(boolean value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBoolean(value);
   }

   @Override
   public ByteBuf writeByte(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeByte(value);
   }

   @Override
   public ByteBuf writeShort(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeShort(value);
   }

   @Override
   public ByteBuf writeMedium(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeMedium(value);
   }

   @Override
   public ByteBuf writeInt(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeInt(value);
   }

   @Override
   public ByteBuf writeLong(long value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeLong(value);
   }

   @Override
   public ByteBuf writeChar(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeChar(value);
   }

   @Override
   public ByteBuf writeFloat(float value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeFloat(value);
   }

   @Override
   public ByteBuf writeDouble(double value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeDouble(value);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf src) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(src);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf src, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(src, length);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(src, srcIndex, length);
   }

   @Override
   public ByteBuf writeBytes(byte[] src) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(src);
   }

   @Override
   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(src, srcIndex, length);
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer src) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(src);
   }

   @Override
   public int writeBytes(InputStream in, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(in, length);
   }

   @Override
   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(in, length);
   }

   @Override
   public ByteBuf writeZero(int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeZero(length);
   }

   @Override
   public int indexOf(int fromIndex, int toIndex, byte value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.indexOf(fromIndex, toIndex, value);
   }

   @Override
   public int bytesBefore(byte value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(value);
   }

   @Override
   public int bytesBefore(int length, byte value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(length, value);
   }

   @Override
   public int bytesBefore(int index, int length, byte value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(index, length, value);
   }

   @Override
   public int forEachByte(ByteProcessor processor) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByte(processor);
   }

   @Override
   public int forEachByte(int index, int length, ByteProcessor processor) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByte(index, length, processor);
   }

   @Override
   public int forEachByteDesc(ByteProcessor processor) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByteDesc(processor);
   }

   @Override
   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByteDesc(index, length, processor);
   }

   @Override
   public ByteBuf copy() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.copy();
   }

   @Override
   public ByteBuf copy(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.copy(index, length);
   }

   @Override
   public int nioBufferCount() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffer();
   }

   @Override
   public ByteBuffer nioBuffer(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffer(index, length);
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffers();
   }

   @Override
   public ByteBuffer[] nioBuffers(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffers(index, length);
   }

   @Override
   public ByteBuffer internalNioBuffer(int index, int length) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.internalNioBuffer(index, length);
   }

   @Override
   public String toString(Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.toString(charset);
   }

   @Override
   public String toString(int index, int length, Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.toString(index, length, charset);
   }

   @Override
   public ByteBuf capacity(int newCapacity) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.capacity(newCapacity);
   }

   @Override
   public short getShortLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getShortLE(index);
   }

   @Override
   public int getUnsignedShortLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedShortLE(index);
   }

   @Override
   public int getMediumLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getMediumLE(index);
   }

   @Override
   public int getUnsignedMediumLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedMediumLE(index);
   }

   @Override
   public int getIntLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getIntLE(index);
   }

   @Override
   public long getUnsignedIntLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedIntLE(index);
   }

   @Override
   public long getLongLE(int index) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getLongLE(index);
   }

   @Override
   public ByteBuf setShortLE(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setShortLE(index, value);
   }

   @Override
   public ByteBuf setIntLE(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setIntLE(index, value);
   }

   @Override
   public ByteBuf setMediumLE(int index, int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setMediumLE(index, value);
   }

   @Override
   public ByteBuf setLongLE(int index, long value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setLongLE(index, value);
   }

   @Override
   public short readShortLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readShortLE();
   }

   @Override
   public int readUnsignedShortLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedShortLE();
   }

   @Override
   public int readMediumLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readMediumLE();
   }

   @Override
   public int readUnsignedMediumLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedMediumLE();
   }

   @Override
   public int readIntLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readIntLE();
   }

   @Override
   public long readUnsignedIntLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedIntLE();
   }

   @Override
   public long readLongLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readLongLE();
   }

   @Override
   public ByteBuf writeShortLE(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeShortLE(value);
   }

   @Override
   public ByteBuf writeMediumLE(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeMediumLE(value);
   }

   @Override
   public ByteBuf writeIntLE(int value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeIntLE(value);
   }

   @Override
   public ByteBuf writeLongLE(long value) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeLongLE(value);
   }

   @Override
   public int writeCharSequence(CharSequence sequence, Charset charset) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeCharSequence(sequence, charset);
   }

   @Override
   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(index, out, position, length);
   }

   @Override
   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(index, in, position, length);
   }

   @Override
   public int readBytes(FileChannel out, long position, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(out, position, length);
   }

   @Override
   public int writeBytes(FileChannel in, long position, int length) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(in, position, length);
   }

   @Override
   public ByteBuf asReadOnly() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.asReadOnly();
   }

   @Override
   public ByteBuf retain() {
      this.leak.record();
      return super.retain();
   }

   @Override
   public ByteBuf retain(int increment) {
      this.leak.record();
      return super.retain(increment);
   }

   @Override
   public boolean release() {
      this.leak.record();
      return super.release();
   }

   @Override
   public boolean release(int decrement) {
      this.leak.record();
      return super.release(decrement);
   }

   @Override
   public ByteBuf touch() {
      this.leak.record();
      return this;
   }

   @Override
   public ByteBuf touch(Object hint) {
      this.leak.record(hint);
      return this;
   }

   protected AdvancedLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf buf, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
      return new AdvancedLeakAwareByteBuf(buf, trackedByteBuf, leakTracker);
   }

   static {
      if (logger.isDebugEnabled()) {
         logger.debug("-D{}: {}", "io.netty.leakDetection.acquireAndReleaseOnly", ACQUIRE_AND_RELEASE_ONLY);
      }

      ResourceLeakDetector.addExclusions(AdvancedLeakAwareByteBuf.class, "touch", "recordLeakNonRefCountingOperation");
   }
}
