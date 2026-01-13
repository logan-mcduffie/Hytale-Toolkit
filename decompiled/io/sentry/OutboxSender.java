package io.sentry;

import io.sentry.hints.Flushable;
import io.sentry.hints.Resettable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.util.CollectionUtils;
import io.sentry.util.HintUtils;
import io.sentry.util.LogUtils;
import io.sentry.util.Objects;
import io.sentry.util.SampleRateUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class OutboxSender extends DirectoryProcessor implements IEnvelopeSender {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   @NotNull
   private final IScopes scopes;
   @NotNull
   private final IEnvelopeReader envelopeReader;
   @NotNull
   private final ISerializer serializer;
   @NotNull
   private final ILogger logger;

   public OutboxSender(
      @NotNull IScopes scopes,
      @NotNull IEnvelopeReader envelopeReader,
      @NotNull ISerializer serializer,
      @NotNull ILogger logger,
      long flushTimeoutMillis,
      int maxQueueSize
   ) {
      super(scopes, logger, flushTimeoutMillis, maxQueueSize);
      this.scopes = Objects.requireNonNull(scopes, "Scopes are required.");
      this.envelopeReader = Objects.requireNonNull(envelopeReader, "Envelope reader is required.");
      this.serializer = Objects.requireNonNull(serializer, "Serializer is required.");
      this.logger = Objects.requireNonNull(logger, "Logger is required.");
   }

   @Override
   protected void processFile(@NotNull File file, @NotNull Hint hint) {
      Objects.requireNonNull(file, "File is required.");
      if (!this.isRelevantFileName(file.getName())) {
         this.logger.log(SentryLevel.DEBUG, "File '%s' should be ignored.", file.getAbsolutePath());
      } else {
         try {
            InputStream stream = new BufferedInputStream(new FileInputStream(file));

            try {
               SentryEnvelope envelope = this.envelopeReader.read(stream);
               if (envelope == null) {
                  this.logger.log(SentryLevel.ERROR, "Stream from path %s resulted in a null envelope.", file.getAbsolutePath());
               } else {
                  this.processEnvelope(envelope, hint);
                  this.logger.log(SentryLevel.DEBUG, "File '%s' is done.", file.getAbsolutePath());
               }
            } catch (Throwable var12) {
               try {
                  stream.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }

               throw var12;
            }

            stream.close();
         } catch (IOException var13) {
            this.logger.log(SentryLevel.ERROR, "Error processing envelope.", var13);
         } finally {
            HintUtils.runIfHasTypeLogIfNot(hint, Retryable.class, this.logger, retryable -> {
               if (!retryable.isRetry()) {
                  try {
                     if (!file.delete()) {
                        this.logger.log(SentryLevel.ERROR, "Failed to delete: %s", file.getAbsolutePath());
                     }
                  } catch (RuntimeException var4x) {
                     this.logger.log(SentryLevel.ERROR, var4x, "Failed to delete: %s", file.getAbsolutePath());
                  }
               }
            });
         }
      }
   }

   @Override
   protected boolean isRelevantFileName(@Nullable String fileName) {
      return fileName != null && !fileName.startsWith("session") && !fileName.startsWith("previous_session") && !fileName.startsWith("startup_crash");
   }

   @Override
   public void processEnvelopeFile(@NotNull String path, @NotNull Hint hint) {
      Objects.requireNonNull(path, "Path is required.");
      this.processFile(new File(path), hint);
   }

   private void processEnvelope(@NotNull SentryEnvelope envelope, @NotNull Hint hint) throws IOException {
      this.logger.log(SentryLevel.DEBUG, "Processing Envelope with %d item(s)", CollectionUtils.size(envelope.getItems()));
      int currentItem = 0;

      for (SentryEnvelopeItem item : envelope.getItems()) {
         currentItem++;
         if (item.getHeader() == null) {
            this.logger.log(SentryLevel.ERROR, "Item %d has no header", currentItem);
         } else {
            if (SentryItemType.Event.equals(item.getHeader().getType())) {
               try {
                  label134: {
                     Reader eventReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(item.getData()), UTF_8));

                     label103: {
                        label102: {
                           try {
                              SentryEvent event = this.serializer.deserialize(eventReader, SentryEvent.class);
                              if (event == null) {
                                 this.logEnvelopeItemNull(item, currentItem);
                              } else {
                                 if (event.getSdk() != null) {
                                    HintUtils.setIsFromHybridSdk(hint, event.getSdk().getName());
                                 }

                                 if (envelope.getHeader().getEventId() != null && !envelope.getHeader().getEventId().equals(event.getEventId())) {
                                    this.logUnexpectedEventId(envelope, event.getEventId(), currentItem);
                                    break label103;
                                 }

                                 this.scopes.captureEvent(event, hint);
                                 this.logItemCaptured(currentItem);
                                 if (!this.waitFlush(hint)) {
                                    this.logTimeout(event.getEventId());
                                    break label102;
                                 }
                              }
                           } catch (Throwable var11) {
                              try {
                                 eventReader.close();
                              } catch (Throwable var10) {
                                 var11.addSuppressed(var10);
                              }

                              throw var11;
                           }

                           eventReader.close();
                           break label134;
                        }

                        eventReader.close();
                        break;
                     }

                     eventReader.close();
                     continue;
                  }
               } catch (Throwable var12) {
                  this.logger.log(SentryLevel.ERROR, "Item failed to process.", var12);
               }
            } else if (SentryItemType.Transaction.equals(item.getHeader().getType())) {
               try {
                  label137: {
                     Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(item.getData()), UTF_8));

                     label119: {
                        label118: {
                           try {
                              SentryTransaction transaction = this.serializer.deserialize(reader, SentryTransaction.class);
                              if (transaction == null) {
                                 this.logEnvelopeItemNull(item, currentItem);
                              } else {
                                 if (envelope.getHeader().getEventId() != null && !envelope.getHeader().getEventId().equals(transaction.getEventId())) {
                                    this.logUnexpectedEventId(envelope, transaction.getEventId(), currentItem);
                                    break label119;
                                 }

                                 TraceContext traceContext = envelope.getHeader().getTraceContext();
                                 if (transaction.getContexts().getTrace() != null) {
                                    transaction.getContexts().getTrace().setSamplingDecision(this.extractSamplingDecision(traceContext));
                                 }

                                 this.scopes.captureTransaction(transaction, traceContext, hint);
                                 this.logItemCaptured(currentItem);
                                 if (!this.waitFlush(hint)) {
                                    this.logTimeout(transaction.getEventId());
                                    break label118;
                                 }
                              }
                           } catch (Throwable var13) {
                              try {
                                 reader.close();
                              } catch (Throwable var9) {
                                 var13.addSuppressed(var9);
                              }

                              throw var13;
                           }

                           reader.close();
                           break label137;
                        }

                        reader.close();
                        break;
                     }

                     reader.close();
                     continue;
                  }
               } catch (Throwable var14) {
                  this.logger.log(SentryLevel.ERROR, "Item failed to process.", var14);
               }
            } else {
               SentryEnvelope newEnvelope = new SentryEnvelope(envelope.getHeader().getEventId(), envelope.getHeader().getSdkVersion(), item);
               this.scopes.captureEnvelope(newEnvelope, hint);
               this.logger.log(SentryLevel.DEBUG, "%s item %d is being captured.", item.getHeader().getType().getItemType(), currentItem);
               if (!this.waitFlush(hint)) {
                  this.logger.log(SentryLevel.WARNING, "Timed out waiting for item type submission: %s", item.getHeader().getType().getItemType());
                  break;
               }
            }

            Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
            if (sentrySdkHint instanceof SubmissionResult && !((SubmissionResult)sentrySdkHint).isSuccess()) {
               this.logger.log(SentryLevel.WARNING, "Envelope had a failed capture at item %d. No more items will be sent.", currentItem);
               break;
            }

            HintUtils.runIfHasType(hint, Resettable.class, resettable -> resettable.reset());
         }
      }
   }

   @NotNull
   private TracesSamplingDecision extractSamplingDecision(@Nullable TraceContext traceContext) {
      if (traceContext != null) {
         String sampleRateString = traceContext.getSampleRate();
         if (sampleRateString != null) {
            try {
               Double sampleRate = Double.parseDouble(sampleRateString);
               if (SampleRateUtils.isValidTracesSampleRate(sampleRate, false)) {
                  String sampleRandString = traceContext.getSampleRand();
                  if (sampleRandString != null) {
                     Double sampleRand = Double.parseDouble(sampleRandString);
                     if (SampleRateUtils.isValidTracesSampleRate(sampleRand, false)) {
                        return new TracesSamplingDecision(true, sampleRate, sampleRand);
                     }
                  }

                  return SampleRateUtils.backfilledSampleRand(new TracesSamplingDecision(true, sampleRate));
               }

               this.logger.log(SentryLevel.ERROR, "Invalid sample rate parsed from TraceContext: %s", sampleRateString);
            } catch (Exception var6) {
               this.logger.log(SentryLevel.ERROR, "Unable to parse sample rate from TraceContext: %s", sampleRateString);
            }
         }
      }

      return new TracesSamplingDecision(true);
   }

   private void logEnvelopeItemNull(@NotNull SentryEnvelopeItem item, int itemIndex) {
      this.logger.log(SentryLevel.ERROR, "Item %d of type %s returned null by the parser.", itemIndex, item.getHeader().getType());
   }

   private void logUnexpectedEventId(@NotNull SentryEnvelope envelope, @Nullable SentryId eventId, int itemIndex) {
      this.logger
         .log(SentryLevel.ERROR, "Item %d of has a different event id (%s) to the envelope header (%s)", itemIndex, envelope.getHeader().getEventId(), eventId);
   }

   private void logItemCaptured(int itemIndex) {
      this.logger.log(SentryLevel.DEBUG, "Item %d is being captured.", itemIndex);
   }

   private void logTimeout(@Nullable SentryId eventId) {
      this.logger.log(SentryLevel.WARNING, "Timed out waiting for event id submission: %s", eventId);
   }

   private boolean waitFlush(@NotNull Hint hint) {
      Object sentrySdkHint = HintUtils.getSentrySdkHint(hint);
      if (sentrySdkHint instanceof Flushable) {
         return ((Flushable)sentrySdkHint).waitFlush();
      } else {
         LogUtils.logNotInstanceOf(Flushable.class, sentrySdkHint, this.logger);
         return true;
      }
   }
}
