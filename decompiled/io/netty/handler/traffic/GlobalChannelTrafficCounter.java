package io.netty.handler.traffic;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlobalChannelTrafficCounter extends TrafficCounter {
   public GlobalChannelTrafficCounter(
      GlobalChannelTrafficShapingHandler trafficShapingHandler, ScheduledExecutorService executor, String name, long checkInterval
   ) {
      super(trafficShapingHandler, executor, name, checkInterval);
      ObjectUtil.checkNotNullWithIAE(executor, "executor");
   }

   @Override
   public synchronized void start() {
      if (!this.monitorActive) {
         this.lastTime.set(milliSecondFromNano());
         long localCheckInterval = this.checkInterval.get();
         if (localCheckInterval > 0L) {
            this.monitorActive = true;
            this.monitor = new GlobalChannelTrafficCounter.MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler, this);
            this.scheduledFuture = this.executor.scheduleAtFixedRate(this.monitor, 0L, localCheckInterval, TimeUnit.MILLISECONDS);
         }
      }
   }

   @Override
   public synchronized void stop() {
      if (this.monitorActive) {
         this.monitorActive = false;
         this.resetAccounting(milliSecondFromNano());
         this.trafficShapingHandler.doAccounting(this);
         if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
         }
      }
   }

   @Override
   public void resetCumulativeTime() {
      for (GlobalChannelTrafficShapingHandler.PerChannel perChannel : ((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler).channelQueues.values()) {
         perChannel.channelTrafficCounter.resetCumulativeTime();
      }

      super.resetCumulativeTime();
   }

   private static class MixedTrafficMonitoringTask implements Runnable {
      private final GlobalChannelTrafficShapingHandler trafficShapingHandler1;
      private final TrafficCounter counter;

      MixedTrafficMonitoringTask(GlobalChannelTrafficShapingHandler trafficShapingHandler, TrafficCounter counter) {
         this.trafficShapingHandler1 = trafficShapingHandler;
         this.counter = counter;
      }

      @Override
      public void run() {
         if (this.counter.monitorActive) {
            long newLastTime = TrafficCounter.milliSecondFromNano();
            this.counter.resetAccounting(newLastTime);

            for (GlobalChannelTrafficShapingHandler.PerChannel perChannel : this.trafficShapingHandler1.channelQueues.values()) {
               perChannel.channelTrafficCounter.resetAccounting(newLastTime);
            }

            this.trafficShapingHandler1.doAccounting(this.counter);
         }
      }
   }
}
