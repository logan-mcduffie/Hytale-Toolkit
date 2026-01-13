package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class AutoScalingEventExecutorChooserFactory implements EventExecutorChooserFactory {
   private static final Runnable NO_OOP_TASK = () -> {};
   private final int minChildren;
   private final int maxChildren;
   private final long utilizationCheckPeriodNanos;
   private final double scaleDownThreshold;
   private final double scaleUpThreshold;
   private final int maxRampUpStep;
   private final int maxRampDownStep;
   private final int scalingPatienceCycles;

   public AutoScalingEventExecutorChooserFactory(
      int minThreads,
      int maxThreads,
      long utilizationWindow,
      TimeUnit windowUnit,
      double scaleDownThreshold,
      double scaleUpThreshold,
      int maxRampUpStep,
      int maxRampDownStep,
      int scalingPatienceCycles
   ) {
      this.minChildren = ObjectUtil.checkPositiveOrZero(minThreads, "minThreads");
      this.maxChildren = ObjectUtil.checkPositive(maxThreads, "maxThreads");
      if (minThreads > maxThreads) {
         throw new IllegalArgumentException(String.format("minThreads: %d must not be greater than maxThreads: %d", minThreads, maxThreads));
      } else {
         this.utilizationCheckPeriodNanos = ObjectUtil.checkNotNull(windowUnit, "windowUnit")
            .toNanos(ObjectUtil.checkPositive(utilizationWindow, "utilizationWindow"));
         this.scaleDownThreshold = ObjectUtil.checkInRange(scaleDownThreshold, 0.0, 1.0, "scaleDownThreshold");
         this.scaleUpThreshold = ObjectUtil.checkInRange(scaleUpThreshold, 0.0, 1.0, "scaleUpThreshold");
         if (scaleDownThreshold >= scaleUpThreshold) {
            throw new IllegalArgumentException("scaleDownThreshold must be less than scaleUpThreshold: " + scaleDownThreshold + " >= " + scaleUpThreshold);
         } else {
            this.maxRampUpStep = ObjectUtil.checkPositive(maxRampUpStep, "maxRampUpStep");
            this.maxRampDownStep = ObjectUtil.checkPositive(maxRampDownStep, "maxRampDownStep");
            this.scalingPatienceCycles = ObjectUtil.checkPositiveOrZero(scalingPatienceCycles, "scalingPatienceCycles");
         }
      }
   }

   @Override
   public EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] executors) {
      return new AutoScalingEventExecutorChooserFactory.AutoScalingEventExecutorChooser(executors);
   }

   private final class AutoScalingEventExecutorChooser implements EventExecutorChooserFactory.ObservableEventExecutorChooser {
      private final EventExecutor[] executors;
      private final EventExecutorChooserFactory.EventExecutorChooser allExecutorsChooser;
      private final AtomicReference<AutoScalingEventExecutorChooserFactory.AutoScalingState> state;
      private final List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> utilizationMetrics;

      AutoScalingEventExecutorChooser(EventExecutor[] executors) {
         this.executors = executors;
         List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> metrics = new ArrayList<>(executors.length);

         for (EventExecutor executor : executors) {
            metrics.add(new AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric(executor));
         }

         this.utilizationMetrics = Collections.unmodifiableList(metrics);
         this.allExecutorsChooser = DefaultEventExecutorChooserFactory.INSTANCE.newChooser(executors);
         AutoScalingEventExecutorChooserFactory.AutoScalingState initialState = new AutoScalingEventExecutorChooserFactory.AutoScalingState(
            AutoScalingEventExecutorChooserFactory.this.maxChildren, 0L, executors
         );
         this.state = new AtomicReference<>(initialState);
         ScheduledFuture<?> utilizationMonitoringTask = GlobalEventExecutor.INSTANCE
            .scheduleAtFixedRate(
               new AutoScalingEventExecutorChooserFactory.AutoScalingEventExecutorChooser.UtilizationMonitor(),
               AutoScalingEventExecutorChooserFactory.this.utilizationCheckPeriodNanos,
               AutoScalingEventExecutorChooserFactory.this.utilizationCheckPeriodNanos,
               TimeUnit.NANOSECONDS
            );
         if (executors.length > 0) {
            executors[0].terminationFuture().addListener(future -> utilizationMonitoringTask.cancel(false));
         }
      }

      @Override
      public EventExecutor next() {
         AutoScalingEventExecutorChooserFactory.AutoScalingState currentState = this.state.get();
         if (currentState.activeExecutors.length == 0) {
            this.tryScaleUpBy(1);
            return this.allExecutorsChooser.next();
         } else {
            return currentState.activeExecutorsChooser.next();
         }
      }

      private void tryScaleUpBy(int amount) {
         if (amount > 0) {
            AutoScalingEventExecutorChooserFactory.AutoScalingState oldState;
            AutoScalingEventExecutorChooserFactory.AutoScalingState newState;
            do {
               oldState = this.state.get();
               if (oldState.activeChildrenCount >= AutoScalingEventExecutorChooserFactory.this.maxChildren) {
                  return;
               }

               int canAdd = Math.min(amount, AutoScalingEventExecutorChooserFactory.this.maxChildren - oldState.activeChildrenCount);
               List<EventExecutor> wokenUp = new ArrayList<>(canAdd);
               long startIndex = oldState.nextWakeUpIndex;

               for (int i = 0; i < this.executors.length; i++) {
                  EventExecutor child = this.executors[(int)Math.abs((startIndex + i) % this.executors.length)];
                  if (wokenUp.size() >= canAdd) {
                     break;
                  }

                  if (child instanceof SingleThreadEventExecutor) {
                     SingleThreadEventExecutor stee = (SingleThreadEventExecutor)child;
                     if (stee.isSuspended()) {
                        stee.execute(AutoScalingEventExecutorChooserFactory.NO_OOP_TASK);
                        wokenUp.add(stee);
                     }
                  }
               }

               if (wokenUp.isEmpty()) {
                  return;
               }

               List<EventExecutor> newActiveList = new ArrayList<>(oldState.activeExecutors.length + wokenUp.size());
               Collections.addAll(newActiveList, oldState.activeExecutors);
               newActiveList.addAll(wokenUp);
               newState = new AutoScalingEventExecutorChooserFactory.AutoScalingState(
                  oldState.activeChildrenCount + wokenUp.size(), startIndex + wokenUp.size(), newActiveList.toArray(new EventExecutor[0])
               );
            } while (!this.state.compareAndSet(oldState, newState));
         }
      }

      @Override
      public int activeExecutorCount() {
         return this.state.get().activeChildrenCount;
      }

      @Override
      public List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> executorUtilizations() {
         return this.utilizationMetrics;
      }

      private final class UtilizationMonitor implements Runnable {
         private final List<SingleThreadEventExecutor> consistentlyIdleChildren = new ArrayList<>(AutoScalingEventExecutorChooserFactory.this.maxChildren);
         private long lastCheckTimeNanos;

         private UtilizationMonitor() {
         }

         @Override
         public void run() {
            if (AutoScalingEventExecutorChooser.this.executors.length != 0 && !AutoScalingEventExecutorChooser.this.executors[0].isShuttingDown()) {
               long now = AutoScalingEventExecutorChooser.this.executors[0].ticker().nanoTime();
               long totalTime;
               if (this.lastCheckTimeNanos == 0L) {
                  totalTime = AutoScalingEventExecutorChooserFactory.this.utilizationCheckPeriodNanos;
               } else {
                  totalTime = now - this.lastCheckTimeNanos;
               }

               this.lastCheckTimeNanos = now;
               if (totalTime > 0L) {
                  int consistentlyBusyChildren = 0;
                  this.consistentlyIdleChildren.clear();
                  AutoScalingEventExecutorChooserFactory.AutoScalingState currentState = AutoScalingEventExecutorChooser.this.state.get();

                  for (int i = 0; i < AutoScalingEventExecutorChooser.this.executors.length; i++) {
                     EventExecutor child = AutoScalingEventExecutorChooser.this.executors[i];
                     if (child instanceof SingleThreadEventExecutor) {
                        SingleThreadEventExecutor eventExecutor = (SingleThreadEventExecutor)child;
                        double utilization = 0.0;
                        if (!eventExecutor.isSuspended()) {
                           long activeTime = eventExecutor.getAndResetAccumulatedActiveTimeNanos();
                           if (activeTime == 0L) {
                              long lastActivity = eventExecutor.getLastActivityTimeNanos();
                              long idleTime = now - lastActivity;
                              if (idleTime < totalTime) {
                                 activeTime = totalTime - idleTime;
                              }
                           }

                           utilization = Math.min(1.0, (double)activeTime / totalTime);
                           if (utilization < AutoScalingEventExecutorChooserFactory.this.scaleDownThreshold) {
                              int idleCycles = eventExecutor.getAndIncrementIdleCycles();
                              eventExecutor.resetBusyCycles();
                              if (idleCycles >= AutoScalingEventExecutorChooserFactory.this.scalingPatienceCycles
                                 && eventExecutor.getNumOfRegisteredChannels() <= 0) {
                                 this.consistentlyIdleChildren.add(eventExecutor);
                              }
                           } else if (utilization > AutoScalingEventExecutorChooserFactory.this.scaleUpThreshold) {
                              int busyCycles = eventExecutor.getAndIncrementBusyCycles();
                              eventExecutor.resetIdleCycles();
                              if (busyCycles >= AutoScalingEventExecutorChooserFactory.this.scalingPatienceCycles) {
                                 consistentlyBusyChildren++;
                              }
                           } else {
                              eventExecutor.resetIdleCycles();
                              eventExecutor.resetBusyCycles();
                           }
                        }

                        AutoScalingEventExecutorChooser.this.utilizationMetrics.get(i).setUtilization(utilization);
                     }
                  }

                  int currentActive = currentState.activeChildrenCount;
                  if (consistentlyBusyChildren > 0 && currentActive < AutoScalingEventExecutorChooserFactory.this.maxChildren) {
                     int threadsToAdd = Math.min(consistentlyBusyChildren, AutoScalingEventExecutorChooserFactory.this.maxRampUpStep);
                     threadsToAdd = Math.min(threadsToAdd, AutoScalingEventExecutorChooserFactory.this.maxChildren - currentActive);
                     if (threadsToAdd > 0) {
                        AutoScalingEventExecutorChooser.this.tryScaleUpBy(threadsToAdd);
                        return;
                     }
                  }

                  boolean changed = false;
                  if (!this.consistentlyIdleChildren.isEmpty() && currentActive > AutoScalingEventExecutorChooserFactory.this.minChildren) {
                     int threadsToRemove = Math.min(this.consistentlyIdleChildren.size(), AutoScalingEventExecutorChooserFactory.this.maxRampDownStep);
                     threadsToRemove = Math.min(threadsToRemove, currentActive - AutoScalingEventExecutorChooserFactory.this.minChildren);

                     for (int ix = 0; ix < threadsToRemove; ix++) {
                        SingleThreadEventExecutor childToSuspend = this.consistentlyIdleChildren.get(ix);
                        if (childToSuspend.trySuspend()) {
                           childToSuspend.resetBusyCycles();
                           childToSuspend.resetIdleCycles();
                           changed = true;
                        }
                     }
                  }

                  if (changed || currentActive != currentState.activeExecutors.length) {
                     this.rebuildActiveExecutors();
                  }
               }
            }
         }

         private void rebuildActiveExecutors() {
            AutoScalingEventExecutorChooserFactory.AutoScalingState oldState;
            AutoScalingEventExecutorChooserFactory.AutoScalingState newState;
            do {
               oldState = AutoScalingEventExecutorChooser.this.state.get();
               List<EventExecutor> active = new ArrayList<>(oldState.activeChildrenCount);

               for (EventExecutor executor : AutoScalingEventExecutorChooser.this.executors) {
                  if (!executor.isSuspended()) {
                     active.add(executor);
                  }
               }

               EventExecutor[] newActiveExecutors = active.toArray(new EventExecutor[0]);
               newState = new AutoScalingEventExecutorChooserFactory.AutoScalingState(newActiveExecutors.length, oldState.nextWakeUpIndex, newActiveExecutors);
            } while (!AutoScalingEventExecutorChooser.this.state.compareAndSet(oldState, newState));
         }
      }
   }

   private static final class AutoScalingState {
      final int activeChildrenCount;
      final long nextWakeUpIndex;
      final EventExecutor[] activeExecutors;
      final EventExecutorChooserFactory.EventExecutorChooser activeExecutorsChooser;

      AutoScalingState(int activeChildrenCount, long nextWakeUpIndex, EventExecutor[] activeExecutors) {
         this.activeChildrenCount = activeChildrenCount;
         this.nextWakeUpIndex = nextWakeUpIndex;
         this.activeExecutors = activeExecutors;
         this.activeExecutorsChooser = DefaultEventExecutorChooserFactory.INSTANCE.newChooser(activeExecutors);
      }
   }

   public static final class AutoScalingUtilizationMetric {
      private final EventExecutor executor;
      private final AtomicLong utilizationBits = new AtomicLong();

      AutoScalingUtilizationMetric(EventExecutor executor) {
         this.executor = executor;
      }

      public double utilization() {
         return Double.longBitsToDouble(this.utilizationBits.get());
      }

      public EventExecutor executor() {
         return this.executor;
      }

      void setUtilization(double utilization) {
         long bits = Double.doubleToRawLongBits(utilization);
         this.utilizationBits.lazySet(bits);
      }
   }
}
