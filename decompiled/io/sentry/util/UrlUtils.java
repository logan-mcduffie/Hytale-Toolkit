package io.sentry.util;

import io.sentry.ISpan;
import io.sentry.protocol.Request;
import java.net.URI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class UrlUtils {
   @NotNull
   public static final String SENSITIVE_DATA_SUBSTITUTE = "[Filtered]";

   @Nullable
   public static UrlUtils.UrlDetails parseNullable(@Nullable String url) {
      return url == null ? null : parse(url);
   }

   @NotNull
   public static UrlUtils.UrlDetails parse(@NotNull String url) {
      try {
         URI uri = new URI(url);
         if (uri.isAbsolute() && !isValidAbsoluteUrl(uri)) {
            return new UrlUtils.UrlDetails(null, null, null);
         } else {
            String schemeAndSeparator = uri.getScheme() == null ? "" : uri.getScheme() + "://";
            String authority = uri.getRawAuthority() == null ? "" : uri.getRawAuthority();
            String path = uri.getRawPath() == null ? "" : uri.getRawPath();
            String query = uri.getRawQuery();
            String fragment = uri.getRawFragment();
            String filteredUrl = schemeAndSeparator + filterUserInfo(authority) + path;
            return new UrlUtils.UrlDetails(filteredUrl, query, fragment);
         }
      } catch (Exception var8) {
         return new UrlUtils.UrlDetails(null, null, null);
      }
   }

   private static boolean isValidAbsoluteUrl(@NotNull URI uri) {
      try {
         uri.toURL();
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   @NotNull
   private static String filterUserInfo(@NotNull String url) {
      if (!url.contains("@")) {
         return url;
      } else if (url.startsWith("@")) {
         return "[Filtered]" + url;
      } else {
         String userInfo = url.substring(0, url.indexOf(64));
         String filteredUserInfo = userInfo.contains(":") ? "[Filtered]:[Filtered]" : "[Filtered]";
         return filteredUserInfo + url.substring(url.indexOf(64));
      }
   }

   public static final class UrlDetails {
      @Nullable
      private final String url;
      @Nullable
      private final String query;
      @Nullable
      private final String fragment;

      public UrlDetails(@Nullable String url, @Nullable String query, @Nullable String fragment) {
         this.url = url;
         this.query = query;
         this.fragment = fragment;
      }

      @Nullable
      public String getUrl() {
         return this.url;
      }

      @NotNull
      public String getUrlOrFallback() {
         return this.url == null ? "unknown" : this.url;
      }

      @Nullable
      public String getQuery() {
         return this.query;
      }

      @Nullable
      public String getFragment() {
         return this.fragment;
      }

      public void applyToRequest(@Nullable Request request) {
         if (request != null) {
            request.setUrl(this.url);
            request.setQueryString(this.query);
            request.setFragment(this.fragment);
         }
      }

      public void applyToSpan(@Nullable ISpan span) {
         if (span != null) {
            if (this.query != null) {
               span.setData("http.query", this.query);
            }

            if (this.fragment != null) {
               span.setData("http.fragment", this.fragment);
            }
         }
      }
   }
}
