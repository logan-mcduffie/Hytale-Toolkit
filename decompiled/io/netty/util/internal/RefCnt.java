package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public final class RefCnt {
   private static final int UNSAFE = 0;
   private static final int VAR_HANDLE = 1;
   private static final int ATOMIC_UPDATER = 2;
   private static final int REF_CNT_IMPL;
   volatile int value;

   public RefCnt() {
      switch (REF_CNT_IMPL) {
         case 0:
            RefCnt.UnsafeRefCnt.init(this);
            break;
         case 1:
            RefCnt.VarHandleRefCnt.init(this);
            break;
         case 2:
         default:
            RefCnt.AtomicRefCnt.init(this);
      }
   }

   public static int refCnt(RefCnt ref) {
      switch (REF_CNT_IMPL) {
         case 0:
            return RefCnt.UnsafeRefCnt.refCnt(ref);
         case 1:
            return RefCnt.VarHandleRefCnt.refCnt(ref);
         case 2:
         default:
            return RefCnt.AtomicRefCnt.refCnt(ref);
      }
   }

   public static void retain(RefCnt ref) {
      switch (REF_CNT_IMPL) {
         case 0:
            RefCnt.UnsafeRefCnt.retain(ref);
            break;
         case 1:
            RefCnt.VarHandleRefCnt.retain(ref);
            break;
         case 2:
         default:
            RefCnt.AtomicRefCnt.retain(ref);
      }
   }

   public static void retain(RefCnt ref, int increment) {
      switch (REF_CNT_IMPL) {
         case 0:
            RefCnt.UnsafeRefCnt.retain(ref, increment);
            break;
         case 1:
            RefCnt.VarHandleRefCnt.retain(ref, increment);
            break;
         case 2:
         default:
            RefCnt.AtomicRefCnt.retain(ref, increment);
      }
   }

   public static boolean release(RefCnt ref) {
      switch (REF_CNT_IMPL) {
         case 0:
            return RefCnt.UnsafeRefCnt.release(ref);
         case 1:
            return RefCnt.VarHandleRefCnt.release(ref);
         case 2:
         default:
            return RefCnt.AtomicRefCnt.release(ref);
      }
   }

   public static boolean release(RefCnt ref, int decrement) {
      switch (REF_CNT_IMPL) {
         case 0:
            return RefCnt.UnsafeRefCnt.release(ref, decrement);
         case 1:
            return RefCnt.VarHandleRefCnt.release(ref, decrement);
         case 2:
         default:
            return RefCnt.AtomicRefCnt.release(ref, decrement);
      }
   }

   public static boolean isLiveNonVolatile(RefCnt ref) {
      switch (REF_CNT_IMPL) {
         case 0:
            return RefCnt.UnsafeRefCnt.isLiveNonVolatile(ref);
         case 1:
            return RefCnt.VarHandleRefCnt.isLiveNonVolatile(ref);
         case 2:
         default:
            return RefCnt.AtomicRefCnt.isLiveNonVolatile(ref);
      }
   }

   public static void setRefCnt(RefCnt ref, int refCnt) {
      switch (REF_CNT_IMPL) {
         case 0:
            RefCnt.UnsafeRefCnt.setRefCnt(ref, refCnt);
            break;
         case 1:
            RefCnt.VarHandleRefCnt.setRefCnt(ref, refCnt);
            break;
         case 2:
         default:
            RefCnt.AtomicRefCnt.setRefCnt(ref, refCnt);
      }
   }

   public static void resetRefCnt(RefCnt ref) {
      switch (REF_CNT_IMPL) {
         case 0:
            RefCnt.UnsafeRefCnt.resetRefCnt(ref);
            break;
         case 1:
            RefCnt.VarHandleRefCnt.resetRefCnt(ref);
            break;
         case 2:
         default:
            RefCnt.AtomicRefCnt.resetRefCnt(ref);
      }
   }

   static void throwIllegalRefCountOnRelease(int decrement, int curr) {
      throw new IllegalReferenceCountException(curr >>> 1, -(decrement >>> 1));
   }

   static {
      if (PlatformDependent.hasUnsafe()) {
         REF_CNT_IMPL = 0;
      } else if (PlatformDependent.hasVarHandle()) {
         REF_CNT_IMPL = 1;
      } else {
         REF_CNT_IMPL = 2;
      }
   }

   private static final class AtomicRefCnt {
      private static final AtomicIntegerFieldUpdater<RefCnt> UPDATER = AtomicIntegerFieldUpdater.newUpdater(RefCnt.class, "value");

      static void init(RefCnt instance) {
         UPDATER.set(instance, 2);
      }

      static int refCnt(RefCnt instance) {
         return UPDATER.get(instance) >>> 1;
      }

      static void retain(RefCnt instance) {
         retain0(instance, 2);
      }

      static void retain(RefCnt instance, int increment) {
         retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
      }

      private static void retain0(RefCnt instance, int increment) {
         int oldRef = UPDATER.getAndAdd(instance, increment);
         if ((oldRef & -2147483647) != 0 || oldRef > Integer.MAX_VALUE - increment) {
            UPDATER.getAndAdd(instance, -increment);
            throw new IllegalReferenceCountException(0, increment >>> 1);
         }
      }

      static boolean release(RefCnt instance) {
         return release0(instance, 2);
      }

      static boolean release(RefCnt instance, int decrement) {
         return release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
      }

      private static boolean release0(RefCnt instance, int decrement) {
         int curr;
         int next;
         do {
            curr = instance.value;
            if (curr == decrement) {
               next = 1;
            } else {
               if (curr < decrement || (curr & 1) == 1) {
                  RefCnt.throwIllegalRefCountOnRelease(decrement, curr);
               }

               next = curr - decrement;
            }
         } while (!UPDATER.compareAndSet(instance, curr, next));

         return (next & 1) == 1;
      }

      static void setRefCnt(RefCnt instance, int refCnt) {
         int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
         UPDATER.lazySet(instance, rawRefCnt);
      }

      static void resetRefCnt(RefCnt instance) {
         UPDATER.lazySet(instance, 2);
      }

      static boolean isLiveNonVolatile(RefCnt instance) {
         int rawCnt = instance.value;
         return rawCnt == 2 ? true : (rawCnt & 1) == 0;
      }
   }

   private static final class UnsafeRefCnt {
      private static final long VALUE_OFFSET = getUnsafeOffset(RefCnt.class, "value");

      private static long getUnsafeOffset(Class<?> clz, String fieldName) {
         try {
            if (PlatformDependent.hasUnsafe()) {
               return PlatformDependent.objectFieldOffset(clz.getDeclaredField(fieldName));
            }
         } catch (Throwable var3) {
         }

         return -1L;
      }

      static void init(RefCnt instance) {
         PlatformDependent.safeConstructPutInt(instance, VALUE_OFFSET, 2);
      }

      static int refCnt(RefCnt instance) {
         return PlatformDependent.getVolatileInt(instance, VALUE_OFFSET) >>> 1;
      }

      static void retain(RefCnt instance) {
         retain0(instance, 2);
      }

      static void retain(RefCnt instance, int increment) {
         retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
      }

      private static void retain0(RefCnt instance, int increment) {
         int oldRef = PlatformDependent.getAndAddInt(instance, VALUE_OFFSET, increment);
         if ((oldRef & -2147483647) != 0 || oldRef > Integer.MAX_VALUE - increment) {
            PlatformDependent.getAndAddInt(instance, VALUE_OFFSET, -increment);
            throw new IllegalReferenceCountException(0, increment >>> 1);
         }
      }

      static boolean release(RefCnt instance) {
         return release0(instance, 2);
      }

      static boolean release(RefCnt instance, int decrement) {
         return release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
      }

      private static boolean release0(RefCnt instance, int decrement) {
         int curr;
         int next;
         do {
            curr = PlatformDependent.getInt(instance, VALUE_OFFSET);
            if (curr == decrement) {
               next = 1;
            } else {
               if (curr < decrement || (curr & 1) == 1) {
                  RefCnt.throwIllegalRefCountOnRelease(decrement, curr);
               }

               next = curr - decrement;
            }
         } while (!PlatformDependent.compareAndSwapInt(instance, VALUE_OFFSET, curr, next));

         return (next & 1) == 1;
      }

      static void setRefCnt(RefCnt instance, int refCnt) {
         int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
         PlatformDependent.putOrderedInt(instance, VALUE_OFFSET, rawRefCnt);
      }

      static void resetRefCnt(RefCnt instance) {
         PlatformDependent.putOrderedInt(instance, VALUE_OFFSET, 2);
      }

      static boolean isLiveNonVolatile(RefCnt instance) {
         int rawCnt = PlatformDependent.getInt(instance, VALUE_OFFSET);
         return rawCnt == 2 ? true : (rawCnt & 1) == 0;
      }
   }

   private static final class VarHandleRefCnt {
      private static final VarHandle VH = PlatformDependent.findVarHandleOfIntField(MethodHandles.lookup(), RefCnt.class, "value");

      static void init(RefCnt instance) {
         VH.set((RefCnt)instance, (int)2);
         VarHandle.storeStoreFence();
      }

      static int refCnt(RefCnt instance) {
         return (int)VH.getAcquire((RefCnt)instance) >>> 1;
      }

      static void retain(RefCnt instance) {
         retain0(instance, 2);
      }

      static void retain(RefCnt instance, int increment) {
         retain0(instance, ObjectUtil.checkPositive(increment, "increment") << 1);
      }

      private static void retain0(RefCnt instance, int increment) {
         int oldRef = (int)VH.getAndAdd((RefCnt)instance, (int)increment);
         if ((oldRef & -2147483647) != 0 || oldRef > Integer.MAX_VALUE - increment) {
            VH.getAndAdd((RefCnt)instance, (int)(-increment));
            throw new IllegalReferenceCountException(0, increment >>> 1);
         }
      }

      static boolean release(RefCnt instance) {
         return release0(instance, 2);
      }

      static boolean release(RefCnt instance, int decrement) {
         return release0(instance, ObjectUtil.checkPositive(decrement, "decrement") << 1);
      }

      private static boolean release0(RefCnt instance, int decrement) {
         int curr;
         int next;
         do {
            curr = (int)VH.get((RefCnt)instance);
            if (curr == decrement) {
               next = 1;
            } else {
               if (curr < decrement || (curr & 1) == 1) {
                  RefCnt.throwIllegalRefCountOnRelease(decrement, curr);
               }

               next = curr - decrement;
            }
         } while (!VH.compareAndSet((RefCnt)instance, (int)curr, (int)next));

         return (next & 1) == 1;
      }

      static void setRefCnt(RefCnt instance, int refCnt) {
         int rawRefCnt = refCnt > 0 ? refCnt << 1 : 1;
         VH.setRelease((RefCnt)instance, (int)rawRefCnt);
      }

      static void resetRefCnt(RefCnt instance) {
         VH.setRelease((RefCnt)instance, (int)2);
      }

      static boolean isLiveNonVolatile(RefCnt instance) {
         int rawCnt = (int)VH.get((RefCnt)instance);
         return rawCnt == 2 ? true : (rawCnt & 1) == 0;
      }
   }
}
