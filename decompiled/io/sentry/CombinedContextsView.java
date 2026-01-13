package io.sentry;

import io.sentry.protocol.App;
import io.sentry.protocol.Browser;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Device;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Gpu;
import io.sentry.protocol.OperatingSystem;
import io.sentry.protocol.Response;
import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.Spring;
import io.sentry.util.HintUtils;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class CombinedContextsView extends Contexts {
   private static final long serialVersionUID = 3585992094653318439L;
   @NotNull
   private final Contexts globalContexts;
   @NotNull
   private final Contexts isolationContexts;
   @NotNull
   private final Contexts currentContexts;
   @NotNull
   private final ScopeType defaultScopeType;

   public CombinedContextsView(
      @NotNull Contexts globalContexts, @NotNull Contexts isolationContexts, @NotNull Contexts currentContexts, @NotNull ScopeType defaultScopeType
   ) {
      this.globalContexts = globalContexts;
      this.isolationContexts = isolationContexts;
      this.currentContexts = currentContexts;
      this.defaultScopeType = defaultScopeType;
   }

   @Nullable
   @Override
   public SpanContext getTrace() {
      SpanContext current = this.currentContexts.getTrace();
      if (current != null) {
         return current;
      } else {
         SpanContext isolation = this.isolationContexts.getTrace();
         return isolation != null ? isolation : this.globalContexts.getTrace();
      }
   }

   @Override
   public void setTrace(@NotNull SpanContext traceContext) {
      this.getDefaultContexts().setTrace(traceContext);
   }

   @NotNull
   private Contexts getDefaultContexts() {
      switch (this.defaultScopeType) {
         case CURRENT:
            return this.currentContexts;
         case ISOLATION:
            return this.isolationContexts;
         case GLOBAL:
            return this.globalContexts;
         default:
            return this.currentContexts;
      }
   }

   @Nullable
   @Override
   public App getApp() {
      App current = this.currentContexts.getApp();
      if (current != null) {
         return current;
      } else {
         App isolation = this.isolationContexts.getApp();
         return isolation != null ? isolation : this.globalContexts.getApp();
      }
   }

   @Override
   public void setApp(@NotNull App app) {
      this.getDefaultContexts().setApp(app);
   }

   @Nullable
   @Override
   public Browser getBrowser() {
      Browser current = this.currentContexts.getBrowser();
      if (current != null) {
         return current;
      } else {
         Browser isolation = this.isolationContexts.getBrowser();
         return isolation != null ? isolation : this.globalContexts.getBrowser();
      }
   }

   @Override
   public void setBrowser(@NotNull Browser browser) {
      this.getDefaultContexts().setBrowser(browser);
   }

   @Nullable
   @Override
   public Device getDevice() {
      Device current = this.currentContexts.getDevice();
      if (current != null) {
         return current;
      } else {
         Device isolation = this.isolationContexts.getDevice();
         return isolation != null ? isolation : this.globalContexts.getDevice();
      }
   }

   @Override
   public void setDevice(@NotNull Device device) {
      this.getDefaultContexts().setDevice(device);
   }

   @Nullable
   @Override
   public OperatingSystem getOperatingSystem() {
      OperatingSystem current = this.currentContexts.getOperatingSystem();
      if (current != null) {
         return current;
      } else {
         OperatingSystem isolation = this.isolationContexts.getOperatingSystem();
         return isolation != null ? isolation : this.globalContexts.getOperatingSystem();
      }
   }

   @Override
   public void setOperatingSystem(@NotNull OperatingSystem operatingSystem) {
      this.getDefaultContexts().setOperatingSystem(operatingSystem);
   }

   @Nullable
   @Override
   public SentryRuntime getRuntime() {
      SentryRuntime current = this.currentContexts.getRuntime();
      if (current != null) {
         return current;
      } else {
         SentryRuntime isolation = this.isolationContexts.getRuntime();
         return isolation != null ? isolation : this.globalContexts.getRuntime();
      }
   }

   @Override
   public void setRuntime(@NotNull SentryRuntime runtime) {
      this.getDefaultContexts().setRuntime(runtime);
   }

   @Nullable
   @Override
   public Gpu getGpu() {
      Gpu current = this.currentContexts.getGpu();
      if (current != null) {
         return current;
      } else {
         Gpu isolation = this.isolationContexts.getGpu();
         return isolation != null ? isolation : this.globalContexts.getGpu();
      }
   }

   @Override
   public void setGpu(@NotNull Gpu gpu) {
      this.getDefaultContexts().setGpu(gpu);
   }

   @Nullable
   @Override
   public Response getResponse() {
      Response current = this.currentContexts.getResponse();
      if (current != null) {
         return current;
      } else {
         Response isolation = this.isolationContexts.getResponse();
         return isolation != null ? isolation : this.globalContexts.getResponse();
      }
   }

   @Override
   public void withResponse(HintUtils.SentryConsumer<Response> callback) {
      if (this.currentContexts.getResponse() != null) {
         this.currentContexts.withResponse(callback);
      } else if (this.isolationContexts.getResponse() != null) {
         this.isolationContexts.withResponse(callback);
      } else if (this.globalContexts.getResponse() != null) {
         this.globalContexts.withResponse(callback);
      } else {
         this.getDefaultContexts().withResponse(callback);
      }
   }

   @Override
   public void setResponse(@NotNull Response response) {
      this.getDefaultContexts().setResponse(response);
   }

   @Nullable
   @Override
   public Spring getSpring() {
      Spring current = this.currentContexts.getSpring();
      if (current != null) {
         return current;
      } else {
         Spring isolation = this.isolationContexts.getSpring();
         return isolation != null ? isolation : this.globalContexts.getSpring();
      }
   }

   @Override
   public void setSpring(@NotNull Spring spring) {
      this.getDefaultContexts().setSpring(spring);
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      FeatureFlags current = this.currentContexts.getFeatureFlags();
      if (current != null) {
         return current;
      } else {
         FeatureFlags isolation = this.isolationContexts.getFeatureFlags();
         return isolation != null ? isolation : this.globalContexts.getFeatureFlags();
      }
   }

   @Internal
   @Override
   public void setFeatureFlags(@NotNull FeatureFlags spring) {
      this.getDefaultContexts().setFeatureFlags(spring);
   }

   @Override
   public int size() {
      return this.mergeContexts().size();
   }

   @Override
   public int getSize() {
      return this.size();
   }

   @Override
   public boolean isEmpty() {
      return this.globalContexts.isEmpty() && this.isolationContexts.isEmpty() && this.currentContexts.isEmpty();
   }

   @Override
   public boolean containsKey(@Nullable Object key) {
      return this.globalContexts.containsKey(key) || this.isolationContexts.containsKey(key) || this.currentContexts.containsKey(key);
   }

   @Nullable
   @Override
   public Object get(@Nullable Object key) {
      Object current = this.currentContexts.get(key);
      if (current != null) {
         return current;
      } else {
         Object isolation = this.isolationContexts.get(key);
         return isolation != null ? isolation : this.globalContexts.get(key);
      }
   }

   @Nullable
   @Override
   public Object put(@Nullable String key, @Nullable Object value) {
      return this.getDefaultContexts().put(key, value);
   }

   @Nullable
   @Override
   public Object remove(@Nullable Object key) {
      return this.getDefaultContexts().remove(key);
   }

   @NotNull
   @Override
   public Enumeration<String> keys() {
      return this.mergeContexts().keys();
   }

   @NotNull
   @Override
   public Set<Entry<String, Object>> entrySet() {
      return this.mergeContexts().entrySet();
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      this.mergeContexts().serialize(writer, logger);
   }

   @Nullable
   @Override
   public Object set(@Nullable String key, @Nullable Object value) {
      return this.put(key, value);
   }

   @Override
   public void putAll(@Nullable Map<? extends String, ?> m) {
      this.getDefaultContexts().putAll((Map<? extends String, ? extends Object>)m);
   }

   @Override
   public void putAll(@Nullable Contexts contexts) {
      this.getDefaultContexts().putAll(contexts);
   }

   @NotNull
   private Contexts mergeContexts() {
      Contexts allContexts = new Contexts();
      allContexts.putAll(this.globalContexts);
      allContexts.putAll(this.isolationContexts);
      allContexts.putAll(this.currentContexts);
      return allContexts;
   }
}
