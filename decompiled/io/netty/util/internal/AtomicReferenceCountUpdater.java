package io.netty.util.internal;

import io.netty.util.ReferenceCounted;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AtomicReferenceCountUpdater<T extends ReferenceCounted> extends ReferenceCountUpdater<T> {
   protected AtomicReferenceCountUpdater() {
   }

   protected abstract AtomicIntegerFieldUpdater<T> updater();

   @Override
   protected final void safeInitializeRawRefCnt(T refCntObj, int value) {
      this.updater().set(refCntObj, value);
   }

   @Override
   protected final int getAndAddRawRefCnt(T refCntObj, int increment) {
      return this.updater().getAndAdd(refCntObj, increment);
   }

   @Override
   protected final int getRawRefCnt(T refCnt) {
      return this.updater().get(refCnt);
   }

   @Override
   protected final int getAcquireRawRefCnt(T refCnt) {
      return this.updater().get(refCnt);
   }

   @Override
   protected final void setReleaseRawRefCnt(T refCnt, int value) {
      this.updater().lazySet(refCnt, value);
   }

   @Override
   protected final boolean casRawRefCnt(T refCnt, int expected, int value) {
      return this.updater().compareAndSet(refCnt, expected, value);
   }
}
