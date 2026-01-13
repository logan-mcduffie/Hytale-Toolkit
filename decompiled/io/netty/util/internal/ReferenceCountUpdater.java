package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;

@Deprecated
public abstract class ReferenceCountUpdater<T extends ReferenceCounted> {
   protected ReferenceCountUpdater() {
   }

   protected abstract void safeInitializeRawRefCnt(T var1, int var2);

   protected abstract int getAndAddRawRefCnt(T var1, int var2);

   protected abstract int getRawRefCnt(T var1);

   protected abstract int getAcquireRawRefCnt(T var1);

   protected abstract void setReleaseRawRefCnt(T var1, int var2);

   protected abstract boolean casRawRefCnt(T var1, int var2, int var3);

   public final int initialValue() {
      return 2;
   }

   public final void setInitialValue(T instance) {
      this.safeInitializeRawRefCnt(instance, this.initialValue());
   }

   private static int realRefCnt(int rawCnt) {
      return rawCnt >>> 1;
   }

   public final int refCnt(T instance) {
      return realRefCnt(this.getAcquireRawRefCnt(instance));
   }

   public final boolean isLiveNonVolatile(T instance) {
      int rawCnt = this.getRawRefCnt(instance);
      return rawCnt == 2 ? true : (rawCnt & 1) == 0;
   }

   public final void setRefCnt(T instance, int refCnt) {
      int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
      this.setReleaseRawRefCnt(instance, rawRefCnt);
   }

   public final void resetRefCnt(T instance) {
      this.setReleaseRawRefCnt(instance, this.initialValue());
   }

   public final T retain(T instance) {
      return this.retain0(instance, 2);
   }

   public final T retain(T instance, int increment) {
      return this.retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
   }

   private T retain0(T instance, int increment) {
      int oldRef = this.getAndAddRawRefCnt(instance, increment);
      if ((oldRef & -2147483647) == 0 && oldRef <= Integer.MAX_VALUE - increment) {
         return instance;
      } else {
         this.getAndAddRawRefCnt(instance, -increment);
         throw new IllegalReferenceCountException(0, increment >>> 1);
      }
   }

   public final boolean release(T instance) {
      return this.release0(instance, 2);
   }

   public final boolean release(T instance, int decrement) {
      return this.release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
   }

   private boolean release0(T instance, int decrement) {
      int curr;
      int next;
      do {
         curr = this.getRawRefCnt(instance);
         if (curr == decrement) {
            next = 1;
         } else {
            if (curr < decrement || (curr & 1) == 1) {
               throwIllegalRefCountOnRelease(decrement, curr);
            }

            next = curr - decrement;
         }
      } while (!this.casRawRefCnt(instance, curr, next));

      return (next & 1) == 1;
   }

   private static void throwIllegalRefCountOnRelease(int decrement, int curr) {
      throw new IllegalReferenceCountException(curr >>> 1, -(decrement >>> 1));
   }

   public static <T extends ReferenceCounted> ReferenceCountUpdater.UpdaterType updaterTypeOf(Class<T> clz, String fieldName) {
      long fieldOffset = getUnsafeOffset(clz, fieldName);
      if (fieldOffset >= 0L) {
         return ReferenceCountUpdater.UpdaterType.Unsafe;
      } else {
         return PlatformDependent.hasVarHandle() ? ReferenceCountUpdater.UpdaterType.VarHandle : ReferenceCountUpdater.UpdaterType.Atomic;
      }
   }

   public static long getUnsafeOffset(Class<? extends ReferenceCounted> clz, String fieldName) {
      try {
         if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.objectFieldOffset(clz.getDeclaredField(fieldName));
         }
      } catch (Throwable var3) {
      }

      return -1L;
   }

   public static enum UpdaterType {
      Unsafe,
      VarHandle,
      Atomic;
   }
}
