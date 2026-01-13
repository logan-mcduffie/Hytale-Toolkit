package io.sentry.featureflags;

import io.sentry.ISentryLifecycleToken;
import io.sentry.ScopeType;
import io.sentry.SentryOptions;
import io.sentry.protocol.FeatureFlag;
import io.sentry.protocol.FeatureFlags;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class FeatureFlagBuffer implements IFeatureFlagBuffer {
   @NotNull
   private volatile CopyOnWriteArrayList<FeatureFlagBuffer.FeatureFlagEntry> flags;
   @NotNull
   private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
   private int maxSize;

   private FeatureFlagBuffer(int maxSize) {
      this.maxSize = maxSize;
      this.flags = new CopyOnWriteArrayList<>();
   }

   private FeatureFlagBuffer(int maxSize, @NotNull CopyOnWriteArrayList<FeatureFlagBuffer.FeatureFlagEntry> flags) {
      this.maxSize = maxSize;
      this.flags = flags;
   }

   private FeatureFlagBuffer(@NotNull FeatureFlagBuffer other) {
      this.maxSize = other.maxSize;
      this.flags = new CopyOnWriteArrayList<>(other.flags);
   }

   @Override
   public void add(@Nullable String flag, @Nullable Boolean result) {
      if (flag != null && result != null) {
         ISentryLifecycleToken ignored = this.lock.acquire();

         try {
            int size = this.flags.size();

            for (int i = 0; i < size; i++) {
               FeatureFlagBuffer.FeatureFlagEntry entry = this.flags.get(i);
               if (entry.flag.equals(flag)) {
                  this.flags.remove(i);
                  break;
               }
            }

            this.flags.add(new FeatureFlagBuffer.FeatureFlagEntry(flag, result, System.nanoTime()));
            if (this.flags.size() > this.maxSize) {
               this.flags.remove(0);
            }
         } catch (Throwable var8) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      List<FeatureFlag> featureFlags = new ArrayList<>();

      for (FeatureFlagBuffer.FeatureFlagEntry entry : this.flags) {
         featureFlags.add(entry.toFeatureFlag());
      }

      return new FeatureFlags(featureFlags);
   }

   @NotNull
   @Override
   public IFeatureFlagBuffer clone() {
      return new FeatureFlagBuffer(this);
   }

   @NotNull
   public static IFeatureFlagBuffer create(@NotNull SentryOptions options) {
      int maxFeatureFlags = options.getMaxFeatureFlags();
      return (IFeatureFlagBuffer)(maxFeatureFlags > 0 ? new FeatureFlagBuffer(maxFeatureFlags) : NoOpFeatureFlagBuffer.getInstance());
   }

   @NotNull
   public static IFeatureFlagBuffer merged(
      @NotNull SentryOptions options,
      @Nullable IFeatureFlagBuffer globalBuffer,
      @Nullable IFeatureFlagBuffer isolationBuffer,
      @Nullable IFeatureFlagBuffer currentBuffer
   ) {
      int maxSize = options.getMaxFeatureFlags();
      return (IFeatureFlagBuffer)(maxSize <= 0
         ? NoOpFeatureFlagBuffer.getInstance()
         : merged(
            maxSize,
            globalBuffer instanceof FeatureFlagBuffer ? (FeatureFlagBuffer)globalBuffer : null,
            isolationBuffer instanceof FeatureFlagBuffer ? (FeatureFlagBuffer)isolationBuffer : null,
            currentBuffer instanceof FeatureFlagBuffer ? (FeatureFlagBuffer)currentBuffer : null
         ));
   }

   @NotNull
   private static IFeatureFlagBuffer merged(
      int maxSize, @Nullable FeatureFlagBuffer globalBuffer, @Nullable FeatureFlagBuffer isolationBuffer, @Nullable FeatureFlagBuffer currentBuffer
   ) {
      CopyOnWriteArrayList<FeatureFlagBuffer.FeatureFlagEntry> globalFlags = globalBuffer == null ? null : globalBuffer.flags;
      CopyOnWriteArrayList<FeatureFlagBuffer.FeatureFlagEntry> isolationFlags = isolationBuffer == null ? null : isolationBuffer.flags;
      CopyOnWriteArrayList<FeatureFlagBuffer.FeatureFlagEntry> currentFlags = currentBuffer == null ? null : currentBuffer.flags;
      int globalSize = globalFlags == null ? 0 : globalFlags.size();
      int isolationSize = isolationFlags == null ? 0 : isolationFlags.size();
      int currentSize = currentFlags == null ? 0 : currentFlags.size();
      if (globalSize == 0 && isolationSize == 0 && currentSize == 0) {
         return NoOpFeatureFlagBuffer.getInstance();
      } else {
         int globalIndex = globalSize - 1;
         int isolationIndex = isolationSize - 1;
         int currentIndex = currentSize - 1;
         FeatureFlagBuffer.FeatureFlagEntry globalEntry = globalFlags != null && globalIndex >= 0 ? globalFlags.get(globalIndex) : null;
         FeatureFlagBuffer.FeatureFlagEntry isolationEntry = isolationFlags != null && isolationIndex >= 0 ? isolationFlags.get(isolationIndex) : null;
         FeatureFlagBuffer.FeatureFlagEntry currentEntry = currentFlags != null && currentIndex >= 0 ? currentFlags.get(currentIndex) : null;
         Map<String, FeatureFlagBuffer.FeatureFlagEntry> uniqueFlags = new LinkedHashMap<>(maxSize);

         while (uniqueFlags.size() < maxSize && (globalEntry != null || isolationEntry != null || currentEntry != null)) {
            FeatureFlagBuffer.FeatureFlagEntry entryToAdd = null;
            ScopeType selectedBuffer = null;
            if (globalEntry != null && (entryToAdd == null || globalEntry.nanos > entryToAdd.nanos)) {
               entryToAdd = globalEntry;
               selectedBuffer = ScopeType.GLOBAL;
            }

            if (isolationEntry != null && (entryToAdd == null || isolationEntry.nanos > entryToAdd.nanos)) {
               entryToAdd = isolationEntry;
               selectedBuffer = ScopeType.ISOLATION;
            }

            if (currentEntry != null && (entryToAdd == null || currentEntry.nanos > entryToAdd.nanos)) {
               entryToAdd = currentEntry;
               selectedBuffer = ScopeType.CURRENT;
            }

            if (entryToAdd == null) {
               break;
            }

            if (!uniqueFlags.containsKey(entryToAdd.flag)) {
               uniqueFlags.put(entryToAdd.flag, entryToAdd);
            }

            if (ScopeType.CURRENT.equals(selectedBuffer)) {
               currentIndex--;
               currentEntry = currentFlags != null && currentIndex >= 0 ? currentFlags.get(currentIndex) : null;
            } else if (ScopeType.ISOLATION.equals(selectedBuffer)) {
               isolationIndex--;
               isolationEntry = isolationFlags != null && isolationIndex >= 0 ? isolationFlags.get(isolationIndex) : null;
            } else if (ScopeType.GLOBAL.equals(selectedBuffer)) {
               globalIndex--;
               globalEntry = globalFlags != null && globalIndex >= 0 ? globalFlags.get(globalIndex) : null;
            }
         }

         List<FeatureFlagBuffer.FeatureFlagEntry> resultList = new ArrayList<>(uniqueFlags.values());
         Collections.reverse(resultList);
         return new FeatureFlagBuffer(maxSize, new CopyOnWriteArrayList<>(resultList));
      }
   }

   private static class FeatureFlagEntry {
      @NotNull
      private final String flag;
      private final boolean result;
      @NotNull
      private final Long nanos;

      public FeatureFlagEntry(@NotNull String flag, boolean result, @NotNull Long nanos) {
         this.flag = flag;
         this.result = result;
         this.nanos = nanos;
      }

      @NotNull
      public FeatureFlag toFeatureFlag() {
         return new FeatureFlag(this.flag, this.result);
      }
   }
}
