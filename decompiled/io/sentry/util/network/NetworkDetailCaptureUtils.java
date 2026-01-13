package io.sentry.util.network;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public final class NetworkDetailCaptureUtils {
   private NetworkDetailCaptureUtils() {
   }

   @Nullable
   public static NetworkRequestData initializeForUrl(
      @NotNull String url, @Nullable String method, @Nullable List<String> networkDetailAllowUrls, @Nullable List<String> networkDetailDenyUrls
   ) {
      return !shouldCaptureUrl(url, networkDetailAllowUrls, networkDetailDenyUrls) ? null : new NetworkRequestData(method);
   }

   @NotNull
   public static <T> ReplayNetworkRequestOrResponse createRequest(
      @NotNull T httpObject,
      @Nullable Long bodySize,
      boolean networkCaptureBodies,
      @NotNull NetworkDetailCaptureUtils.NetworkBodyExtractor<T> bodyExtractor,
      @NotNull List<String> networkRequestHeaders,
      @NotNull NetworkDetailCaptureUtils.NetworkHeaderExtractor<T> headerExtractor
   ) {
      return createRequestOrResponseInternal(httpObject, bodySize, networkCaptureBodies, bodyExtractor, networkRequestHeaders, headerExtractor);
   }

   @NotNull
   public static <T> ReplayNetworkRequestOrResponse createResponse(
      @NotNull T httpObject,
      @Nullable Long bodySize,
      boolean networkCaptureBodies,
      @NotNull NetworkDetailCaptureUtils.NetworkBodyExtractor<T> bodyExtractor,
      @NotNull List<String> networkResponseHeaders,
      @NotNull NetworkDetailCaptureUtils.NetworkHeaderExtractor<T> headerExtractor
   ) {
      return createRequestOrResponseInternal(httpObject, bodySize, networkCaptureBodies, bodyExtractor, networkResponseHeaders, headerExtractor);
   }

   private static boolean shouldCaptureUrl(@NotNull String url, @Nullable List<String> networkDetailAllowUrls, @Nullable List<String> networkDetailDenyUrls) {
      if (networkDetailDenyUrls != null) {
         for (String pattern : networkDetailDenyUrls) {
            if (pattern != null && url.matches(pattern)) {
               return false;
            }
         }
      }

      if (networkDetailAllowUrls == null) {
         return false;
      } else {
         for (String patternx : networkDetailAllowUrls) {
            if (patternx != null && url.matches(patternx)) {
               return true;
            }
         }

         return false;
      }
   }

   @VisibleForTesting
   @NotNull
   static Map<String, String> getCaptureHeaders(@Nullable Map<String, String> allHeaders, @NotNull List<String> allowedHeaders) {
      Map<String, String> capturedHeaders = new LinkedHashMap<>();
      if (allHeaders == null) {
         return capturedHeaders;
      } else {
         Set<String> normalizedAllowed = new HashSet<>();

         for (String header : allowedHeaders) {
            if (header != null) {
               normalizedAllowed.add(header.toLowerCase(Locale.ROOT));
            }
         }

         for (Entry<String, String> entry : allHeaders.entrySet()) {
            if (normalizedAllowed.contains(entry.getKey().toLowerCase(Locale.ROOT))) {
               capturedHeaders.put(entry.getKey(), entry.getValue());
            }
         }

         return capturedHeaders;
      }
   }

   @NotNull
   private static <T> ReplayNetworkRequestOrResponse createRequestOrResponseInternal(
      @NotNull T httpObject,
      @Nullable Long bodySize,
      boolean networkCaptureBodies,
      @NotNull NetworkDetailCaptureUtils.NetworkBodyExtractor<T> bodyExtractor,
      @NotNull List<String> allowedHeaders,
      @NotNull NetworkDetailCaptureUtils.NetworkHeaderExtractor<T> headerExtractor
   ) {
      NetworkBody body = null;
      if (networkCaptureBodies) {
         body = bodyExtractor.extract(httpObject);
      }

      Map<String, String> headers = getCaptureHeaders(headerExtractor.extract(httpObject), allowedHeaders);
      return new ReplayNetworkRequestOrResponse(bodySize, body, headers);
   }

   public interface NetworkBodyExtractor<T> {
      @Nullable
      NetworkBody extract(@NotNull T var1);
   }

   public interface NetworkHeaderExtractor<T> {
      @NotNull
      Map<String, String> extract(@NotNull T var1);
   }
}
