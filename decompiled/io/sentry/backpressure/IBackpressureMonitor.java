package io.sentry.backpressure;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IBackpressureMonitor {
   void start();

   int getDownsampleFactor();

   void close();
}
