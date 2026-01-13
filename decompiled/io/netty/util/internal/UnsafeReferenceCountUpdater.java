package io.netty.util.internal;

import io.netty.util.ReferenceCounted;

public abstract class UnsafeReferenceCountUpdater<T extends ReferenceCounted> extends ReferenceCountUpdater<T> {
   protected UnsafeReferenceCountUpdater() {
   }

   protected abstract long refCntFieldOffset();

   @Override
   protected final void safeInitializeRawRefCnt(T refCntObj, int value) {
      PlatformDependent.safeConstructPutInt(refCntObj, this.refCntFieldOffset(), value);
   }

   @Override
   protected final int getAndAddRawRefCnt(T refCntObj, int increment) {
      return PlatformDependent.getAndAddInt(refCntObj, this.refCntFieldOffset(), increment);
   }

   @Override
   protected final int getRawRefCnt(T refCnt) {
      return PlatformDependent.getInt(refCnt, this.refCntFieldOffset());
   }

   @Override
   protected final int getAcquireRawRefCnt(T refCnt) {
      return PlatformDependent.getVolatileInt(refCnt, this.refCntFieldOffset());
   }

   @Override
   protected final void setReleaseRawRefCnt(T refCnt, int value) {
      PlatformDependent.putOrderedInt(refCnt, this.refCntFieldOffset(), value);
   }

   @Override
   protected final boolean casRawRefCnt(T refCnt, int expected, int value) {
      return PlatformDependent.compareAndSwapInt(refCnt, this.refCntFieldOffset(), expected, value);
   }
}
