package io.sentry.internal.eventprocessor;

import io.sentry.EventProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EventProcessorAndOrder implements Comparable<EventProcessorAndOrder> {
   @NotNull
   private final EventProcessor eventProcessor;
   @NotNull
   private final Long order;

   public EventProcessorAndOrder(@NotNull EventProcessor eventProcessor, @Nullable Long order) {
      this.eventProcessor = eventProcessor;
      if (order == null) {
         this.order = System.nanoTime();
      } else {
         this.order = order;
      }
   }

   @NotNull
   public EventProcessor getEventProcessor() {
      return this.eventProcessor;
   }

   @NotNull
   public Long getOrder() {
      return this.order;
   }

   public int compareTo(@NotNull EventProcessorAndOrder o) {
      return this.order.compareTo(o.order);
   }
}
