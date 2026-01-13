package io.netty.util.concurrent;

import java.util.List;

public interface EventExecutorChooserFactory {
   EventExecutorChooserFactory.EventExecutorChooser newChooser(EventExecutor[] var1);

   public interface EventExecutorChooser {
      EventExecutor next();
   }

   public interface ObservableEventExecutorChooser extends EventExecutorChooserFactory.EventExecutorChooser {
      int activeExecutorCount();

      List<AutoScalingEventExecutorChooserFactory.AutoScalingUtilizationMetric> executorUtilizations();
   }
}
