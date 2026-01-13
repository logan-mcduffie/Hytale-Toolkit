package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.Recycler;
import io.netty.util.internal.ObjectPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledSlicedByteBuf extends AbstractPooledDerivedByteBuf {
   private static final Recycler<PooledSlicedByteBuf> RECYCLER = new Recycler<PooledSlicedByteBuf>() {
      protected PooledSlicedByteBuf newObject(Recycler.Handle<PooledSlicedByteBuf> handle) {
         return new PooledSlicedByteBuf(handle);
      }
   };
   int adjustment;

   static PooledSlicedByteBuf newInstance(AbstractByteBuf unwrapped, ByteBuf wrapped, int index, int length) {
      AbstractUnpooledSlicedByteBuf.checkSliceOutOfBounds(index, length, unwrapped);
      return newInstance0(unwrapped, wrapped, index, length);
   }

   private static PooledSlicedByteBuf newInstance0(AbstractByteBuf unwrapped, ByteBuf wrapped, int adjustment, int length) {
      PooledSlicedByteBuf slice = RECYCLER.get();
      slice.init(unwrapped, wrapped, 0, length, length);
      slice.discardMarks();
      slice.adjustment = adjustment;
      return slice;
   }

   private PooledSlicedByteBuf(ObjectPool.Handle<PooledSlicedByteBuf> handle) {
      super(handle);
   }

   @Override
   public int capacity() {
      return this.maxCapacity();
   }

   @Override
   public ByteBuf capacity(int newCapacity) {
      throw new UnsupportedOperationException("sliced buffer");
   }

   @Override
   public int arrayOffset() {
      return this.idx(this.unwrap().arrayOffset());
   }

   @Override
   public long memoryAddress() {
      return this.unwrap().memoryAddress() + this.adjustment;
   }

   @Override
   public ByteBuffer nioBuffer(int index, int length) {
      this.checkIndex0(index, length);
      return this.unwrap().nioBuffer(this.idx(index), length);
   }

   @Override
   public ByteBuffer[] nioBuffers(int index, int length) {
      this.checkIndex0(index, length);
      return this.unwrap().nioBuffers(this.idx(index), length);
   }

   @Override
   public ByteBuf copy(int index, int length) {
      this.checkIndex0(index, length);
      return this.unwrap().copy(this.idx(index), length);
   }

   @Override
   public ByteBuf slice(int index, int length) {
      this.checkIndex0(index, length);
      return super.slice(this.idx(index), length);
   }

   @Override
   public ByteBuf retainedSlice(int index, int length) {
      this.checkIndex0(index, length);
      return newInstance0(this.unwrap(), this, this.idx(index), length);
   }

   @Override
   public ByteBuf duplicate() {
      return this.slice(0, this.capacity()).setIndex(this.readerIndex(), this.writerIndex());
   }

   @Override
   public ByteBuf retainedDuplicate() {
      return this.retainedSlice(0, this.capacity()).setIndex(this.readerIndex(), this.writerIndex());
   }

   @Override
   public byte getByte(int index) {
      this.checkIndex(index, 1);
      return this._getByte(index);
   }

   @Override
   protected byte _getByte(int index) {
      return this.unwrap()._getByte(this.idx(index));
   }

   @Override
   public short getShort(int index) {
      this.checkIndex(index, 2);
      return this._getShort(index);
   }

   @Override
   protected short _getShort(int index) {
      return this.unwrap()._getShort(this.idx(index));
   }

   @Override
   public short getShortLE(int index) {
      this.checkIndex(index, 2);
      return this._getShortLE(index);
   }

   @Override
   protected short _getShortLE(int index) {
      return this.unwrap()._getShortLE(this.idx(index));
   }

   @Override
   public int getUnsignedMedium(int index) {
      this.checkIndex(index, 3);
      return this._getUnsignedMedium(index);
   }

   @Override
   protected int _getUnsignedMedium(int index) {
      return this.unwrap()._getUnsignedMedium(this.idx(index));
   }

   @Override
   public int getUnsignedMediumLE(int index) {
      this.checkIndex(index, 3);
      return this._getUnsignedMediumLE(index);
   }

   @Override
   protected int _getUnsignedMediumLE(int index) {
      return this.unwrap()._getUnsignedMediumLE(this.idx(index));
   }

   @Override
   public int getInt(int index) {
      this.checkIndex(index, 4);
      return this._getInt(index);
   }

   @Override
   protected int _getInt(int index) {
      return this.unwrap()._getInt(this.idx(index));
   }

   @Override
   public int getIntLE(int index) {
      this.checkIndex(index, 4);
      return this._getIntLE(index);
   }

   @Override
   protected int _getIntLE(int index) {
      return this.unwrap()._getIntLE(this.idx(index));
   }

   @Override
   public long getLong(int index) {
      this.checkIndex(index, 8);
      return this._getLong(index);
   }

   @Override
   protected long _getLong(int index) {
      return this.unwrap()._getLong(this.idx(index));
   }

   @Override
   public long getLongLE(int index) {
      this.checkIndex(index, 8);
      return this._getLongLE(index);
   }

   @Override
   protected long _getLongLE(int index) {
      return this.unwrap()._getLongLE(this.idx(index));
   }

   @Override
   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.checkIndex0(index, length);
      this.unwrap().getBytes(this.idx(index), dst, dstIndex, length);
      return this;
   }

   @Override
   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.checkIndex0(index, length);
      this.unwrap().getBytes(this.idx(index), dst, dstIndex, length);
      return this;
   }

   @Override
   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.checkIndex0(index, dst.remaining());
      this.unwrap().getBytes(this.idx(index), dst);
      return this;
   }

   @Override
   public ByteBuf setByte(int index, int value) {
      this.checkIndex(index, 1);
      this._setByte(index, value);
      return this;
   }

   @Override
   protected void _setByte(int index, int value) {
      this.unwrap()._setByte(this.idx(index), value);
   }

   @Override
   public ByteBuf setShort(int index, int value) {
      this.checkIndex(index, 2);
      this._setShort(index, value);
      return this;
   }

   @Override
   protected void _setShort(int index, int value) {
      this.unwrap()._setShort(this.idx(index), value);
   }

   @Override
   public ByteBuf setShortLE(int index, int value) {
      this.checkIndex(index, 2);
      this._setShortLE(index, value);
      return this;
   }

   @Override
   protected void _setShortLE(int index, int value) {
      this.unwrap()._setShortLE(this.idx(index), value);
   }

   @Override
   public ByteBuf setMedium(int index, int value) {
      this.checkIndex(index, 3);
      this._setMedium(index, value);
      return this;
   }

   @Override
   protected void _setMedium(int index, int value) {
      this.unwrap()._setMedium(this.idx(index), value);
   }

   @Override
   public ByteBuf setMediumLE(int index, int value) {
      this.checkIndex(index, 3);
      this._setMediumLE(index, value);
      return this;
   }

   @Override
   protected void _setMediumLE(int index, int value) {
      this.unwrap()._setMediumLE(this.idx(index), value);
   }

   @Override
   public ByteBuf setInt(int index, int value) {
      this.checkIndex(index, 4);
      this._setInt(index, value);
      return this;
   }

   @Override
   protected void _setInt(int index, int value) {
      this.unwrap()._setInt(this.idx(index), value);
   }

   @Override
   public ByteBuf setIntLE(int index, int value) {
      this.checkIndex(index, 4);
      this._setIntLE(index, value);
      return this;
   }

   @Override
   protected void _setIntLE(int index, int value) {
      this.unwrap()._setIntLE(this.idx(index), value);
   }

   @Override
   public ByteBuf setLong(int index, long value) {
      this.checkIndex(index, 8);
      this._setLong(index, value);
      return this;
   }

   @Override
   protected void _setLong(int index, long value) {
      this.unwrap()._setLong(this.idx(index), value);
   }

   @Override
   public ByteBuf setLongLE(int index, long value) {
      this.checkIndex(index, 8);
      this._setLongLE(index, value);
      return this;
   }

   @Override
   protected void _setLongLE(int index, long value) {
      this.unwrap()._setLongLE(this.idx(index), value);
   }

   @Override
   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.checkIndex0(index, length);
      this.unwrap().setBytes(this.idx(index), src, srcIndex, length);
      return this;
   }

   @Override
   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.checkIndex0(index, length);
      this.unwrap().setBytes(this.idx(index), src, srcIndex, length);
      return this;
   }

   @Override
   public ByteBuf setBytes(int index, ByteBuffer src) {
      this.checkIndex0(index, src.remaining());
      this.unwrap().setBytes(this.idx(index), src);
      return this;
   }

   @Override
   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.checkIndex0(index, length);
      this.unwrap().getBytes(this.idx(index), out, length);
      return this;
   }

   @Override
   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      this.checkIndex0(index, length);
      return this.unwrap().getBytes(this.idx(index), out, length);
   }

   @Override
   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
      this.checkIndex0(index, length);
      return this.unwrap().getBytes(this.idx(index), out, position, length);
   }

   @Override
   public int setBytes(int index, InputStream in, int length) throws IOException {
      this.checkIndex0(index, length);
      return this.unwrap().setBytes(this.idx(index), in, length);
   }

   @Override
   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.checkIndex0(index, length);
      return this.unwrap().setBytes(this.idx(index), in, length);
   }

   @Override
   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
      this.checkIndex0(index, length);
      return this.unwrap().setBytes(this.idx(index), in, position, length);
   }

   @Override
   public int forEachByte(int index, int length, ByteProcessor processor) {
      this.checkIndex0(index, length);
      int ret = this.unwrap().forEachByte(this.idx(index), length, processor);
      return ret < this.adjustment ? -1 : ret - this.adjustment;
   }

   @Override
   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
      this.checkIndex0(index, length);
      int ret = this.unwrap().forEachByteDesc(this.idx(index), length, processor);
      return ret < this.adjustment ? -1 : ret - this.adjustment;
   }

   private int idx(int index) {
      return index + this.adjustment;
   }
}
