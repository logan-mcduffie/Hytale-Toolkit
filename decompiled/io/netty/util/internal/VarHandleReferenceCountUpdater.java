package io.netty.util.internal;

import io.netty.util.ReferenceCounted;
import java.lang.invoke.VarHandle;

public abstract class VarHandleReferenceCountUpdater<T extends ReferenceCounted> extends ReferenceCountUpdater<T> {
   protected VarHandleReferenceCountUpdater() {
   }

   protected abstract VarHandle varHandle();

   @Override
   protected final void safeInitializeRawRefCnt(T refCntObj, int value) {
      this.varHandle().set((ReferenceCounted)refCntObj, (int)value);
   }

   @Override
   protected final int getAndAddRawRefCnt(T refCntObj, int increment) {
      return (int)this.varHandle().getAndAdd((ReferenceCounted)refCntObj, (int)increment);
   }

   @Override
   protected final int getRawRefCnt(T refCnt) {
      return (int)this.varHandle().get((ReferenceCounted)refCnt);
   }

   @Override
   protected final int getAcquireRawRefCnt(T refCnt) {
      return (int)this.varHandle().getAcquire((ReferenceCounted)refCnt);
   }

   @Override
   protected final void setReleaseRawRefCnt(T refCnt, int value) {
      this.varHandle().setRelease((ReferenceCounted)refCnt, (int)value);
   }

   @Override
   protected final boolean casRawRefCnt(T refCnt, int expected, int value) {
      return this.varHandle().compareAndSet((ReferenceCounted)refCnt, (int)expected, (int)value);
   }
}
