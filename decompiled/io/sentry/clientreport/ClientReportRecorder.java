package io.sentry.clientreport;

import io.sentry.DataCategory;
import io.sentry.DateUtils;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEvents;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentrySpan;
import io.sentry.protocol.SentryTransaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ClientReportRecorder implements IClientReportRecorder {
   @NotNull
   private final IClientReportStorage storage;
   @NotNull
   private final SentryOptions options;

   public ClientReportRecorder(@NotNull SentryOptions options) {
      this.options = options;
      this.storage = new AtomicClientReportStorage();
   }

   @NotNull
   @Override
   public SentryEnvelope attachReportToEnvelope(@NotNull SentryEnvelope envelope) {
      ClientReport clientReport = this.resetCountsAndGenerateClientReport();
      if (clientReport == null) {
         return envelope;
      } else {
         try {
            this.options.getLogger().log(SentryLevel.DEBUG, "Attaching client report to envelope.");
            List<SentryEnvelopeItem> items = new ArrayList<>();

            for (SentryEnvelopeItem item : envelope.getItems()) {
               items.add(item);
            }

            items.add(SentryEnvelopeItem.fromClientReport(this.options.getSerializer(), clientReport));
            return new SentryEnvelope(envelope.getHeader(), items);
         } catch (Throwable var6) {
            this.options.getLogger().log(SentryLevel.ERROR, var6, "Unable to attach client report to envelope.");
            return envelope;
         }
      }
   }

   @Override
   public void recordLostEnvelope(@NotNull DiscardReason reason, @Nullable SentryEnvelope envelope) {
      if (envelope != null) {
         try {
            for (SentryEnvelopeItem item : envelope.getItems()) {
               this.recordLostEnvelopeItem(reason, item);
            }
         } catch (Throwable var5) {
            this.options.getLogger().log(SentryLevel.ERROR, var5, "Unable to record lost envelope.");
         }
      }
   }

   @Override
   public void recordLostEnvelopeItem(@NotNull DiscardReason reason, @Nullable SentryEnvelopeItem envelopeItem) {
      if (envelopeItem != null) {
         try {
            SentryItemType itemType = envelopeItem.getHeader().getType();
            if (SentryItemType.ClientReport.equals(itemType)) {
               try {
                  ClientReport clientReport = envelopeItem.getClientReport(this.options.getSerializer());
                  this.restoreCountsFromClientReport(clientReport);
               } catch (Exception var11) {
                  this.options.getLogger().log(SentryLevel.ERROR, "Unable to restore counts from previous client report.");
               }
            } else {
               DataCategory itemCategory = this.categoryFromItemType(itemType);
               if (itemCategory.equals(DataCategory.Transaction)) {
                  SentryTransaction transaction = envelopeItem.getTransaction(this.options.getSerializer());
                  if (transaction != null) {
                     List<SentrySpan> spans = transaction.getSpans();
                     this.recordLostEventInternal(reason.getReason(), DataCategory.Span.getCategory(), spans.size() + 1L);
                     this.executeOnDiscard(reason, DataCategory.Span, spans.size() + 1L);
                  }

                  this.recordLostEventInternal(reason.getReason(), itemCategory.getCategory(), 1L);
                  this.executeOnDiscard(reason, itemCategory, 1L);
               } else if (itemCategory.equals(DataCategory.LogItem)) {
                  SentryLogEvents logs = envelopeItem.getLogs(this.options.getSerializer());
                  if (logs != null) {
                     List<SentryLogEvent> items = logs.getItems();
                     long count = items.size();
                     this.recordLostEventInternal(reason.getReason(), itemCategory.getCategory(), count);
                     long logBytes = envelopeItem.getData().length;
                     this.recordLostEventInternal(reason.getReason(), DataCategory.LogByte.getCategory(), logBytes);
                     this.executeOnDiscard(reason, itemCategory, count);
                  } else {
                     this.options.getLogger().log(SentryLevel.ERROR, "Unable to parse lost logs envelope item.");
                  }
               } else {
                  this.recordLostEventInternal(reason.getReason(), itemCategory.getCategory(), 1L);
                  this.executeOnDiscard(reason, itemCategory, 1L);
               }
            }
         } catch (Throwable var12) {
            this.options.getLogger().log(SentryLevel.ERROR, var12, "Unable to record lost envelope item.");
         }
      }
   }

   @Override
   public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category) {
      this.recordLostEvent(reason, category, 1L);
   }

   @Override
   public void recordLostEvent(@NotNull DiscardReason reason, @NotNull DataCategory category, long count) {
      try {
         this.recordLostEventInternal(reason.getReason(), category.getCategory(), count);
         this.executeOnDiscard(reason, category, count);
      } catch (Throwable var6) {
         this.options.getLogger().log(SentryLevel.ERROR, var6, "Unable to record lost event.");
      }
   }

   private void executeOnDiscard(@NotNull DiscardReason reason, @NotNull DataCategory category, @NotNull Long countToAdd) {
      if (this.options.getOnDiscard() != null) {
         try {
            this.options.getOnDiscard().execute(reason, category, countToAdd);
         } catch (Throwable var5) {
            this.options.getLogger().log(SentryLevel.ERROR, "The onDiscard callback threw an exception.", var5);
         }
      }
   }

   private void recordLostEventInternal(@NotNull String reason, @NotNull String category, @NotNull Long countToAdd) {
      ClientReportKey key = new ClientReportKey(reason, category);
      this.storage.addCount(key, countToAdd);
   }

   @Nullable
   ClientReport resetCountsAndGenerateClientReport() {
      Date currentDate = DateUtils.getCurrentDateTime();
      List<DiscardedEvent> discardedEvents = this.storage.resetCountsAndGet();
      return discardedEvents.isEmpty() ? null : new ClientReport(currentDate, discardedEvents);
   }

   private void restoreCountsFromClientReport(@Nullable ClientReport clientReport) {
      if (clientReport != null) {
         for (DiscardedEvent discardedEvent : clientReport.getDiscardedEvents()) {
            this.recordLostEventInternal(discardedEvent.getReason(), discardedEvent.getCategory(), discardedEvent.getQuantity());
         }
      }
   }

   private DataCategory categoryFromItemType(SentryItemType itemType) {
      if (SentryItemType.Event.equals(itemType)) {
         return DataCategory.Error;
      } else if (SentryItemType.Session.equals(itemType)) {
         return DataCategory.Session;
      } else if (SentryItemType.Transaction.equals(itemType)) {
         return DataCategory.Transaction;
      } else if (SentryItemType.UserFeedback.equals(itemType)) {
         return DataCategory.UserReport;
      } else if (SentryItemType.Feedback.equals(itemType)) {
         return DataCategory.Feedback;
      } else if (SentryItemType.Profile.equals(itemType)) {
         return DataCategory.Profile;
      } else if (SentryItemType.ProfileChunk.equals(itemType)) {
         return DataCategory.ProfileChunkUi;
      } else if (SentryItemType.Attachment.equals(itemType)) {
         return DataCategory.Attachment;
      } else if (SentryItemType.CheckIn.equals(itemType)) {
         return DataCategory.Monitor;
      } else if (SentryItemType.ReplayVideo.equals(itemType)) {
         return DataCategory.Replay;
      } else if (SentryItemType.Log.equals(itemType)) {
         return DataCategory.LogItem;
      } else if (SentryItemType.Span.equals(itemType)) {
         return DataCategory.Span;
      } else {
         return SentryItemType.TraceMetric.equals(itemType) ? DataCategory.TraceMetric : DataCategory.Default;
      }
   }
}
