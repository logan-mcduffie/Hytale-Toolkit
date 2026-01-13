package com.google.crypto.tink.util;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.concurrent.GuardedBy;

@Deprecated
public class KeysDownloader {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   private static final NetHttpTransport DEFAULT_HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport.Builder().build();
   private static final Executor DEFAULT_BACKGROUND_EXECUTOR = Executors.newCachedThreadPool();
   private static final Pattern MAX_AGE_PATTERN = Pattern.compile("\\s*max-age\\s*=\\s*(\\d+)\\s*");
   private final Executor backgroundExecutor;
   private final HttpTransport httpTransport;
   private final Object fetchDataLock;
   private final Object instanceStateLock;
   private final String url;
   @GuardedBy("instanceStateLock")
   private Runnable pendingRefreshRunnable;
   @GuardedBy("instanceStateLock")
   private String cachedData;
   @GuardedBy("instanceStateLock")
   private long cachedTimeInMillis;
   @GuardedBy("instanceStateLock")
   private long cacheExpirationDurationInMillis;

   public KeysDownloader(Executor backgroundExecutor, HttpTransport httpTransport, String url) {
      validate(url);
      this.backgroundExecutor = backgroundExecutor;
      this.httpTransport = httpTransport;
      this.instanceStateLock = new Object();
      this.fetchDataLock = new Object();
      this.url = url;
      this.cachedTimeInMillis = Long.MIN_VALUE;
      this.cacheExpirationDurationInMillis = 0L;
   }

   public String download() throws IOException {
      synchronized (this.instanceStateLock) {
         if (this.hasNonExpiredDataCached()) {
            if (this.shouldProactivelyRefreshDataInBackground()) {
               this.refreshInBackground();
            }

            return this.cachedData;
         }
      }

      synchronized (this.fetchDataLock) {
         synchronized (this.instanceStateLock) {
            if (this.hasNonExpiredDataCached()) {
               return this.cachedData;
            }
         }

         return this.fetchAndCacheData();
      }
   }

   public HttpTransport getHttpTransport() {
      return this.httpTransport;
   }

   public String getUrl() {
      return this.url;
   }

   @GuardedBy("instanceStateLock")
   private boolean hasNonExpiredDataCached() {
      long currentTimeInMillis = this.getCurrentTimeInMillis();
      boolean cachedInFuture = this.cachedTimeInMillis > currentTimeInMillis;
      boolean cacheExpired = this.cachedTimeInMillis + this.cacheExpirationDurationInMillis <= currentTimeInMillis;
      return !cacheExpired && !cachedInFuture;
   }

   @GuardedBy("instanceStateLock")
   private boolean shouldProactivelyRefreshDataInBackground() {
      return this.cachedTimeInMillis + this.cacheExpirationDurationInMillis / 2L <= this.getCurrentTimeInMillis();
   }

   long getCurrentTimeInMillis() {
      return System.currentTimeMillis();
   }

   @GuardedBy("fetchDataLock")
   @CanIgnoreReturnValue
   private String fetchAndCacheData() throws IOException {
      long currentTimeInMillis = this.getCurrentTimeInMillis();
      HttpRequest httpRequest = this.httpTransport.createRequestFactory().buildGetRequest(new GenericUrl(this.url));
      HttpResponse httpResponse = httpRequest.execute();
      if (httpResponse.getStatusCode() != 200) {
         throw new IOException("Unexpected status code = " + httpResponse.getStatusCode());
      } else {
         InputStream contentStream = httpResponse.getContent();

         String data;
         try {
            InputStreamReader reader = new InputStreamReader(contentStream, UTF_8);
            data = readerToString(reader);
         } finally {
            contentStream.close();
         }

         synchronized (this.instanceStateLock) {
            this.cachedTimeInMillis = currentTimeInMillis;
            this.cacheExpirationDurationInMillis = this.getExpirationDurationInSeconds(httpResponse.getHeaders()) * 1000L;
            this.cachedData = data;
            return data;
         }
      }
   }

   private static String readerToString(Reader reader) throws IOException {
      Reader var3 = new BufferedReader(reader);
      StringBuilder stringBuilder = new StringBuilder();

      int c;
      while ((c = var3.read()) != -1) {
         stringBuilder.append((char)c);
      }

      return stringBuilder.toString();
   }

   long getExpirationDurationInSeconds(HttpHeaders httpHeaders) {
      long expirationDurationInSeconds = 0L;
      if (httpHeaders.getCacheControl() != null) {
         for (String arg : httpHeaders.getCacheControl().split(",")) {
            Matcher m = MAX_AGE_PATTERN.matcher(arg);
            if (m.matches()) {
               expirationDurationInSeconds = Long.valueOf(m.group(1));
               break;
            }
         }
      }

      if (httpHeaders.getAge() != null) {
         expirationDurationInSeconds -= httpHeaders.getAge();
      }

      return Math.max(0L, expirationDurationInSeconds);
   }

   public void refreshInBackground() {
      Runnable refreshRunnable = this.newRefreshRunnable();
      synchronized (this.instanceStateLock) {
         if (this.pendingRefreshRunnable != null) {
            return;
         }

         this.pendingRefreshRunnable = refreshRunnable;
      }

      try {
         this.backgroundExecutor.execute(refreshRunnable);
      } catch (Throwable var6) {
         synchronized (this.instanceStateLock) {
            if (this.pendingRefreshRunnable == refreshRunnable) {
               this.pendingRefreshRunnable = null;
            }
         }

         throw var6;
      }
   }

   private Runnable newRefreshRunnable() {
      return new Runnable() {
         @Override
         public void run() {
            synchronized (KeysDownloader.this.fetchDataLock) {
               try {
                  KeysDownloader.this.fetchAndCacheData();
               } catch (IOException var16) {
               } finally {
                  synchronized (KeysDownloader.this.instanceStateLock) {
                     if (KeysDownloader.this.pendingRefreshRunnable == this) {
                        KeysDownloader.this.pendingRefreshRunnable = null;
                     }
                  }
               }
            }
         }
      };
   }

   private static void validate(String url) {
      try {
         URL tmp = new URL(url);
         if (!tmp.getProtocol().toLowerCase(Locale.US).equals("https")) {
            throw new IllegalArgumentException("url must point to a HTTPS server");
         }
      } catch (MalformedURLException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public static class Builder {
      private HttpTransport httpTransport = KeysDownloader.DEFAULT_HTTP_TRANSPORT;
      private Executor executor = KeysDownloader.DEFAULT_BACKGROUND_EXECUTOR;
      private String url;

      @CanIgnoreReturnValue
      public KeysDownloader.Builder setUrl(String val) {
         this.url = val;
         return this;
      }

      @CanIgnoreReturnValue
      public KeysDownloader.Builder setExecutor(Executor val) {
         this.executor = val;
         return this;
      }

      @CanIgnoreReturnValue
      public KeysDownloader.Builder setHttpTransport(HttpTransport httpTransport) {
         this.httpTransport = httpTransport;
         return this;
      }

      public KeysDownloader build() {
         if (this.url == null) {
            throw new IllegalArgumentException("must provide a url with {#setUrl}");
         } else {
            return new KeysDownloader(this.executor, this.httpTransport, this.url);
         }
      }
   }
}
