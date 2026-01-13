package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public final class CorsConfig {
   private final Set<String> origins;
   private final boolean anyOrigin;
   private final boolean enabled;
   private final Set<String> exposeHeaders;
   private final boolean allowCredentials;
   private final long maxAge;
   private final Set<HttpMethod> allowedRequestMethods;
   private final Set<String> allowedRequestHeaders;
   private final boolean allowNullOrigin;
   private final Map<CharSequence, Callable<?>> preflightHeaders;
   private final boolean shortCircuit;
   private final boolean allowPrivateNetwork;

   CorsConfig(CorsConfigBuilder builder) {
      this.origins = new LinkedHashSet<>(builder.origins);
      this.anyOrigin = builder.anyOrigin;
      this.enabled = builder.enabled;
      this.exposeHeaders = builder.exposeHeaders;
      this.allowCredentials = builder.allowCredentials;
      this.maxAge = builder.maxAge;
      this.allowedRequestMethods = builder.requestMethods;
      this.allowedRequestHeaders = builder.requestHeaders;
      this.allowNullOrigin = builder.allowNullOrigin;
      this.preflightHeaders = builder.preflightHeaders;
      this.shortCircuit = builder.shortCircuit;
      this.allowPrivateNetwork = builder.allowPrivateNetwork;
   }

   public boolean isCorsSupportEnabled() {
      return this.enabled;
   }

   public boolean isAnyOriginSupported() {
      return this.anyOrigin;
   }

   public String origin() {
      return this.origins.isEmpty() ? "*" : this.origins.iterator().next();
   }

   public Set<String> origins() {
      return this.origins;
   }

   public boolean isNullOriginAllowed() {
      return this.allowNullOrigin;
   }

   public boolean isPrivateNetworkAllowed() {
      return this.allowPrivateNetwork;
   }

   public Set<String> exposedHeaders() {
      return Collections.unmodifiableSet(this.exposeHeaders);
   }

   public boolean isCredentialsAllowed() {
      return this.allowCredentials;
   }

   public long maxAge() {
      return this.maxAge;
   }

   public Set<HttpMethod> allowedRequestMethods() {
      return Collections.unmodifiableSet(this.allowedRequestMethods);
   }

   public Set<String> allowedRequestHeaders() {
      return Collections.unmodifiableSet(this.allowedRequestHeaders);
   }

   public HttpHeaders preflightResponseHeaders() {
      if (this.preflightHeaders.isEmpty()) {
         return EmptyHttpHeaders.INSTANCE;
      } else {
         HttpHeaders preflightHeaders = new DefaultHttpHeaders();

         for (Entry<CharSequence, Callable<?>> entry : this.preflightHeaders.entrySet()) {
            Object value = getValue(entry.getValue());
            if (value instanceof Iterable) {
               preflightHeaders.add(entry.getKey(), (Iterable<?>)value);
            } else {
               preflightHeaders.add(entry.getKey(), value);
            }
         }

         return preflightHeaders;
      }
   }

   public boolean isShortCircuit() {
      return this.shortCircuit;
   }

   @Deprecated
   public boolean isShortCurcuit() {
      return this.isShortCircuit();
   }

   private static <T> T getValue(Callable<T> callable) {
      try {
         return callable.call();
      } catch (Exception var2) {
         throw new IllegalStateException("Could not generate value for callable [" + callable + ']', var2);
      }
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + "[enabled="
         + this.enabled
         + ", origins="
         + this.origins
         + ", anyOrigin="
         + this.anyOrigin
         + ", exposedHeaders="
         + this.exposeHeaders
         + ", isCredentialsAllowed="
         + this.allowCredentials
         + ", maxAge="
         + this.maxAge
         + ", allowedRequestMethods="
         + this.allowedRequestMethods
         + ", allowedRequestHeaders="
         + this.allowedRequestHeaders
         + ", preflightHeaders="
         + this.preflightHeaders
         + ", isPrivateNetworkAllowed="
         + this.allowPrivateNetwork
         + ']';
   }

   @Deprecated
   public static CorsConfig.Builder withAnyOrigin() {
      return new CorsConfig.Builder();
   }

   @Deprecated
   public static CorsConfig.Builder withOrigin(String origin) {
      return "*".equals(origin) ? new CorsConfig.Builder() : new CorsConfig.Builder(origin);
   }

   @Deprecated
   public static CorsConfig.Builder withOrigins(String... origins) {
      return new CorsConfig.Builder(origins);
   }

   @Deprecated
   public static class Builder {
      private final CorsConfigBuilder builder;

      @Deprecated
      public Builder(String... origins) {
         this.builder = new CorsConfigBuilder(origins);
      }

      @Deprecated
      public Builder() {
         this.builder = new CorsConfigBuilder();
      }

      @Deprecated
      public CorsConfig.Builder allowNullOrigin() {
         this.builder.allowNullOrigin();
         return this;
      }

      @Deprecated
      public CorsConfig.Builder disable() {
         this.builder.disable();
         return this;
      }

      @Deprecated
      public CorsConfig.Builder exposeHeaders(String... headers) {
         this.builder.exposeHeaders(headers);
         return this;
      }

      @Deprecated
      public CorsConfig.Builder allowCredentials() {
         this.builder.allowCredentials();
         return this;
      }

      @Deprecated
      public CorsConfig.Builder maxAge(long max) {
         this.builder.maxAge(max);
         return this;
      }

      @Deprecated
      public CorsConfig.Builder allowedRequestMethods(HttpMethod... methods) {
         this.builder.allowedRequestMethods(methods);
         return this;
      }

      @Deprecated
      public CorsConfig.Builder allowedRequestHeaders(String... headers) {
         this.builder.allowedRequestHeaders(headers);
         return this;
      }

      @Deprecated
      public CorsConfig.Builder preflightResponseHeader(CharSequence name, Object... values) {
         this.builder.preflightResponseHeader(name, values);
         return this;
      }

      @Deprecated
      public <T> CorsConfig.Builder preflightResponseHeader(CharSequence name, Iterable<T> value) {
         this.builder.preflightResponseHeader(name, value);
         return this;
      }

      @Deprecated
      public <T> CorsConfig.Builder preflightResponseHeader(String name, Callable<T> valueGenerator) {
         this.builder.preflightResponseHeader(name, valueGenerator);
         return this;
      }

      @Deprecated
      public CorsConfig.Builder noPreflightResponseHeaders() {
         this.builder.noPreflightResponseHeaders();
         return this;
      }

      @Deprecated
      public CorsConfig build() {
         return this.builder.build();
      }

      @Deprecated
      public CorsConfig.Builder shortCurcuit() {
         this.builder.shortCircuit();
         return this;
      }
   }

   @Deprecated
   public static final class DateValueGenerator implements Callable<Date> {
      public Date call() throws Exception {
         return new Date();
      }
   }
}
