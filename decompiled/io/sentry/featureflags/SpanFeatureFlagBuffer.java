package io.sentry.featureflags;

import io.sentry.ISentryLifecycleToken;
import io.sentry.protocol.FeatureFlag;
import io.sentry.protocol.FeatureFlags;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SpanFeatureFlagBuffer implements IFeatureFlagBuffer {
   private static final int MAX_SIZE = 10;
   @Nullable
   private Map<String, Boolean> flags = null;
   @NotNull
   private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

   private SpanFeatureFlagBuffer() {
   }

   @Override
   public void add(@Nullable String flag, @Nullable Boolean result) {
      if (flag != null && result != null) {
         ISentryLifecycleToken ignored = this.lock.acquire();

         try {
            if (this.flags == null) {
               this.flags = new LinkedHashMap<>(10);
            }

            if (this.flags.size() < 10 || this.flags.containsKey(flag)) {
               this.flags.put(flag, result);
            }
         } catch (Throwable var7) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      FeatureFlags var8;
      label55: {
         List<FeatureFlag> featureFlags;
         try {
            if (this.flags != null && !this.flags.isEmpty()) {
               featureFlags = new ArrayList<>(this.flags.size());

               for (Entry<String, Boolean> entry : this.flags.entrySet()) {
                  featureFlags.add(new FeatureFlag(entry.getKey(), entry.getValue()));
               }

               var8 = new FeatureFlags(featureFlags);
               break label55;
            }

            featureFlags = null;
         } catch (Throwable var6) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (ignored != null) {
            ignored.close();
         }

         return featureFlags;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var8;
   }

   @NotNull
   @Override
   public IFeatureFlagBuffer clone() {
      return create();
   }

   @NotNull
   public static IFeatureFlagBuffer create() {
      return new SpanFeatureFlagBuffer();
   }
}
