package io.sentry.util;

import io.sentry.HttpStatusCodeRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class HttpUtils {
   public static final String COOKIE_HEADER_NAME = "Cookie";
   private static final List<String> SENSITIVE_HEADERS = Arrays.asList(
      "X-FORWARDED-FOR",
      "AUTHORIZATION",
      "COOKIE",
      "SET-COOKIE",
      "X-API-KEY",
      "X-REAL-IP",
      "REMOTE-ADDR",
      "FORWARDED",
      "PROXY-AUTHORIZATION",
      "X-CSRF-TOKEN",
      "X-CSRFTOKEN",
      "X-XSRF-TOKEN"
   );
   private static final List<String> SECURITY_COOKIES = Arrays.asList(
      "JSESSIONID", "JSESSIONIDSSO", "JSSOSESSIONID", "SESSIONID", "SID", "CSRFTOKEN", "XSRF-TOKEN"
   );
   private static final HttpStatusCodeRange CLIENT_ERROR_STATUS_CODES = new HttpStatusCodeRange(400, 499);
   private static final HttpStatusCodeRange SEVER_ERROR_STATUS_CODES = new HttpStatusCodeRange(500, 599);

   public static boolean containsSensitiveHeader(@NotNull String header) {
      return SENSITIVE_HEADERS.contains(header.toUpperCase(Locale.ROOT));
   }

   @Nullable
   public static List<String> filterOutSecurityCookiesFromHeader(
      @Nullable Enumeration<String> headers, @Nullable String headerName, @Nullable List<String> additionalCookieNamesToFilter
   ) {
      return headers == null ? null : filterOutSecurityCookiesFromHeader(Collections.list(headers), headerName, additionalCookieNamesToFilter);
   }

   @Nullable
   public static List<String> filterOutSecurityCookiesFromHeader(
      @Nullable List<String> headers, @Nullable String headerName, @Nullable List<String> additionalCookieNamesToFilter
   ) {
      if (headers == null) {
         return null;
      } else if (headerName != null && !"Cookie".equalsIgnoreCase(headerName)) {
         return headers;
      } else {
         ArrayList<String> filteredHeaders = new ArrayList<>();

         for (String header : headers) {
            filteredHeaders.add(filterOutSecurityCookies(header, additionalCookieNamesToFilter));
         }

         return filteredHeaders;
      }
   }

   @Nullable
   public static String filterOutSecurityCookies(@Nullable String cookieString, @Nullable List<String> additionalCookieNamesToFilter) {
      if (cookieString == null) {
         return null;
      } else {
         try {
            String[] cookies = cookieString.split(";", -1);
            StringBuilder filteredCookieString = new StringBuilder();
            boolean isFirst = true;

            for (String cookie : cookies) {
               if (!isFirst) {
                  filteredCookieString.append(";");
               }

               String[] cookieParts = cookie.split("=", -1);
               String cookieName = cookieParts[0];
               if (isSecurityCookie(cookieName.trim(), additionalCookieNamesToFilter)) {
                  filteredCookieString.append(cookieName + "=" + "[Filtered]");
               } else {
                  filteredCookieString.append(cookie);
               }

               isFirst = false;
            }

            return filteredCookieString.toString();
         } catch (Throwable var11) {
            return null;
         }
      }
   }

   public static boolean isSecurityCookie(@NotNull String cookieName, @Nullable List<String> additionalCookieNamesToFilter) {
      String cookieNameToSearchFor = cookieName.toUpperCase(Locale.ROOT);
      if (SECURITY_COOKIES.contains(cookieNameToSearchFor)) {
         return true;
      } else {
         if (additionalCookieNamesToFilter != null) {
            for (String additionalCookieName : additionalCookieNamesToFilter) {
               if (additionalCookieName.toUpperCase(Locale.ROOT).equals(cookieNameToSearchFor)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isHttpClientError(int statusCode) {
      return CLIENT_ERROR_STATUS_CODES.isInRange(statusCode);
   }

   public static boolean isHttpServerError(int statusCode) {
      return SEVER_ERROR_STATUS_CODES.isInRange(statusCode);
   }
}
