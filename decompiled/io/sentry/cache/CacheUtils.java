package io.sentry.cache;

import io.sentry.JsonDeserializer;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CacheUtils {
   private static final Charset UTF_8 = Charset.forName("UTF-8");

   static <T> void store(@NotNull SentryOptions options, @NotNull T entity, @NotNull String dirName, @NotNull String fileName) {
      File cacheDir = ensureCacheDir(options, dirName);
      if (cacheDir == null) {
         options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot store in scope cache");
      } else {
         File file = new File(cacheDir, fileName);

         try {
            OutputStream outputStream = new FileOutputStream(file);

            try {
               Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));

               try {
                  options.getSerializer().serialize(entity, writer);
               } catch (Throwable var12) {
                  try {
                     writer.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }

                  throw var12;
               }

               writer.close();
            } catch (Throwable var13) {
               try {
                  outputStream.close();
               } catch (Throwable var10) {
                  var13.addSuppressed(var10);
               }

               throw var13;
            }

            outputStream.close();
         } catch (Throwable var14) {
            options.getLogger().log(SentryLevel.ERROR, var14, "Error persisting entity: %s", fileName);
         }
      }
   }

   static void delete(@NotNull SentryOptions options, @NotNull String dirName, @NotNull String fileName) {
      File cacheDir = ensureCacheDir(options, dirName);
      if (cacheDir == null) {
         options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot delete from scope cache");
      } else {
         File file = new File(cacheDir, fileName);
         options.getLogger().log(SentryLevel.DEBUG, "Deleting %s from scope cache", fileName);
         if (!file.delete()) {
            options.getLogger().log(SentryLevel.INFO, "Failed to delete: %s", file.getAbsolutePath());
         }
      }
   }

   @Nullable
   static <T, R> T read(
      @NotNull SentryOptions options,
      @NotNull String dirName,
      @NotNull String fileName,
      @NotNull Class<T> clazz,
      @Nullable JsonDeserializer<R> elementDeserializer
   ) {
      File cacheDir = ensureCacheDir(options, dirName);
      if (cacheDir == null) {
         options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot read from scope cache");
         return null;
      } else {
         File file = new File(cacheDir, fileName);
         if (file.exists()) {
            try {
               Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));

               Object var13;
               label41: {
                  try {
                     if (elementDeserializer == null) {
                        var13 = options.getSerializer().deserialize(reader, clazz);
                        break label41;
                     }

                     var13 = options.getSerializer().deserializeCollection(reader, clazz, elementDeserializer);
                  } catch (Throwable var11) {
                     try {
                        reader.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }

                     throw var11;
                  }

                  reader.close();
                  return (T)var13;
               }

               reader.close();
               return (T)var13;
            } catch (Throwable var12) {
               options.getLogger().log(SentryLevel.ERROR, var12, "Error reading entity from scope cache: %s", fileName);
            }
         } else {
            options.getLogger().log(SentryLevel.DEBUG, "No entry stored for %s", fileName);
         }

         return null;
      }
   }

   @Nullable
   static File ensureCacheDir(@NotNull SentryOptions options, @NotNull String cacheDirName) {
      String cacheDir = options.getCacheDirPath();
      if (cacheDir == null) {
         return null;
      } else {
         File dir = new File(cacheDir, cacheDirName);
         dir.mkdirs();
         return dir;
      }
   }
}
