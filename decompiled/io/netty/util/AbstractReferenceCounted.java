package io.netty.util;

import io.netty.util.internal.RefCnt;

public abstract class AbstractReferenceCounted implements ReferenceCounted {
   private final RefCnt refCnt = new RefCnt();

   @Override
   public int refCnt() {
      return RefCnt.refCnt(this.refCnt);
   }

   protected void setRefCnt(int refCnt) {
      RefCnt.setRefCnt(this.refCnt, refCnt);
   }

   @Override
   public ReferenceCounted retain() {
      RefCnt.retain(this.refCnt);
      return this;
   }

   @Override
   public ReferenceCounted retain(int increment) {
      RefCnt.retain(this.refCnt, increment);
      return this;
   }

   @Override
   public ReferenceCounted touch() {
      return this.touch(null);
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
