package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.util.LazyEvaluator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
final class AtomicClientReportStorage implements IClientReportStorage {
   @NotNull
   private final LazyEvaluator<Map<ClientReportKey, AtomicLong>> lostEventCounts = new LazyEvaluator<>(() -> {
      Map<ClientReportKey, AtomicLong> modifyableEventCountsForInit = new ConcurrentHashMap<>();

      for (DiscardReason discardReason : DiscardReason.values()) {
         for (DataCategory category : DataCategory.values()) {
            modifyableEventCountsForInit.put(new ClientReportKey(discardReason.getReason(), category.getCategory()), new AtomicLong(0L));
         }
      }

      return Collections.unmodifiableMap(modifyableEventCountsForInit);
   });

   public AtomicClientReportStorage() {
   }

   @Override
   public void addCount(ClientReportKey key, Long count) {
      AtomicLong quantity = this.lostEventCounts.getValue().get(key);
      if (quantity != null) {
         quantity.addAndGet(count);
      }
   }

   @Override
   public List<DiscardedEvent> resetCountsAndGet() {
      List<DiscardedEvent> discardedEvents = new ArrayList<>();

      for (Entry<ClientReportKey, AtomicLong> entry : this.lostEventCounts.getValue().entrySet()) {
         Long quantity = entry.getValue().getAndSet(0L);
         if (quantity > 0L) {
            discardedEvents.add(new DiscardedEvent(entry.getKey().getReason(), entry.getKey().getCategory(), quantity));
         }
      }

      return discardedEvents;
   }
}
