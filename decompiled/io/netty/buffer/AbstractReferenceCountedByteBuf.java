package io.netty.buffer;

import io.netty.util.internal.RefCnt;

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {
   private final RefCnt refCnt = new RefCnt();

   protected AbstractReferenceCountedByteBuf(int maxCapacity) {
      super(maxCapacity);
   }

   @Override
   boolean isAccessible() {
      return RefCnt.isLiveNonVolatile(this.refCnt);
   }

   @Override
   public int refCnt() {
      return RefCnt.refCnt(this.refCnt);
   }

   protected final void setRefCnt(int count) {
      RefCnt.setRefCnt(this.refCnt, count);
   }

   protected final void resetRefCnt() {
      RefCnt.resetRefCnt(this.refCnt);
   }

   @Override
   public ByteBuf retain() {
      RefCnt.retain(this.refCnt);
      return this;
   }

   @Override
   public ByteBuf retain(int increment) {
      RefCnt.retain(this.refCnt, increment);
      return this;
   }

   @Override
   public ByteBuf touch() {
      return this;
   }

   @Override
   public ByteBuf touch(Object hint) {
      return this;
   }

   @Override
   public boolean release() {
      return this.handleRelease(RefCnt.release(this.refCnt));
   }

   @Override
   public boolean release(int decrement) {
      return this.handleRelease(RefCnt.release(this.refCnt, decrement));
   }

   private boolean handleRelease(boolean result) {
      if (result) {
         this.deallocate();
      }

      return result;
   }

   protected abstract void deallocate();
}
