package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

final class DirectIoByteBufAllocator implements ByteBufAllocator {
   private final ByteBufAllocator wrapped;

   DirectIoByteBufAllocator(ByteBufAllocator wrapped) {
      if (wrapped instanceof DirectIoByteBufAllocator) {
         wrapped = ((DirectIoByteBufAllocator)wrapped).wrapped();
      }

      this.wrapped = wrapped;
   }

   ByteBufAllocator wrapped() {
      return this.wrapped;
   }

   @Override
   public ByteBuf buffer() {
      return this.wrapped.buffer();
   }

   @Override
   public ByteBuf buffer(int initialCapacity) {
      return this.wrapped.buffer(initialCapacity);
   }

   @Override
   public ByteBuf buffer(int initialCapacity, int maxCapacity) {
      return this.wrapped.buffer(initialCapacity, maxCapacity);
   }

   @Override
   public ByteBuf ioBuffer() {
      return this.directBuffer();
   }

   @Override
   public ByteBuf ioBuffer(int initialCapacity) {
      return this.directBuffer(initialCapacity);
   }

   @Override
   public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
      return this.directBuffer(initialCapacity, maxCapacity);
   }

   @Override
   public ByteBuf heapBuffer() {
      return this.wrapped.heapBuffer();
   }

   @Override
   public ByteBuf heapBuffer(int initialCapacity) {
      return this.wrapped.heapBuffer(initialCapacity);
   }

   @Override
   public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
      return this.wrapped.heapBuffer(initialCapacity, maxCapacity);
   }

   @Override
   public ByteBuf directBuffer() {
      return this.wrapped.directBuffer();
   }

   @Override
   public ByteBuf directBuffer(int initialCapacity) {
      return this.wrapped.directBuffer(initialCapacity);
   }

   @Override
   public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
      return this.wrapped.directBuffer(initialCapacity, maxCapacity);
   }

   @Override
   public CompositeByteBuf compositeBuffer() {
      return this.wrapped.compositeBuffer();
   }

   @Override
   public CompositeByteBuf compositeBuffer(int maxNumComponents) {
      return this.wrapped.compositeBuffer(maxNumComponents);
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer() {
      return this.wrapped.compositeHeapBuffer();
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
      return this.wrapped.compositeHeapBuffer(maxNumComponents);
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer() {
      return this.wrapped.compositeDirectBuffer();
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
      return this.wrapped.compositeDirectBuffer(maxNumComponents);
   }

   @Override
   public boolean isDirectBufferPooled() {
      return this.wrapped.isDirectBufferPooled();
   }

   @Override
   public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
      return this.wrapped.calculateNewCapacity(minNewCapacity, maxCapacity);
   }
}
