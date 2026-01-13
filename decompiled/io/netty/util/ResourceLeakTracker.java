package io.netty.util;

import org.jetbrains.annotations.Nullable;

public interface ResourceLeakTracker<T> {
   void record();

   void record(Object var1);

   boolean close(T var1);

   @Nullable
   default Throwable getCloseStackTraceIfAny() {
      return null;
   }
}
