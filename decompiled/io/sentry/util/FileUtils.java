package io.sentry.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class FileUtils {
   public static boolean deleteRecursively(@Nullable File file) {
      if (file != null && file.exists()) {
         if (file.isFile()) {
            return file.delete();
         } else {
            File[] children = file.listFiles();
            if (children == null) {
               return true;
            } else {
               for (File f : children) {
                  if (!deleteRecursively(f)) {
                     return false;
                  }
               }

               return file.delete();
            }
         }
      } else {
         return true;
      }
   }

   @Nullable
   public static String readText(@Nullable File file) throws IOException {
      if (file != null && file.exists() && file.isFile() && file.canRead()) {
         StringBuilder contentBuilder = new StringBuilder();
         BufferedReader br = new BufferedReader(new FileReader(file));

         try {
            String line;
            if ((line = br.readLine()) != null) {
               contentBuilder.append(line);
            }

            while ((line = br.readLine()) != null) {
               contentBuilder.append("\n").append(line);
            }
         } catch (Throwable var6) {
            try {
               br.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         br.close();
         return contentBuilder.toString();
      } else {
         return null;
      }
   }

   public static byte[] readBytesFromFile(String pathname, long maxFileLength) throws IOException, SecurityException {
      File file = new File(pathname);
      if (!file.exists()) {
         throw new IOException(String.format("File '%s' doesn't exists", file.getName()));
      } else if (!file.isFile()) {
         throw new IOException(String.format("Reading path %s failed, because it's not a file.", pathname));
      } else if (!file.canRead()) {
         throw new IOException(String.format("Reading the item %s failed, because can't read the file.", pathname));
      } else if (file.length() > maxFileLength) {
         throw new IOException(
            String.format(
               "Reading file failed, because size located at '%s' with %d bytes is bigger than the maximum allowed size of %d bytes.",
               pathname,
               file.length(),
               maxFileLength
            )
         );
      } else {
         FileInputStream fileInputStream = new FileInputStream(pathname);

         byte[] var10;
         try {
            BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);

            try {
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

               try {
                  byte[] bytes = new byte[1024];
                  int offset = 0;

                  int length;
                  while ((length = inputStream.read(bytes)) != -1) {
                     outputStream.write(bytes, offset, length);
                  }

                  var10 = outputStream.toByteArray();
               } catch (Throwable var14) {
                  try {
                     outputStream.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }

                  throw var14;
               }

               outputStream.close();
            } catch (Throwable var15) {
               try {
                  inputStream.close();
               } catch (Throwable var12) {
                  var15.addSuppressed(var12);
               }

               throw var15;
            }

            inputStream.close();
         } catch (Throwable var16) {
            try {
               fileInputStream.close();
            } catch (Throwable var11) {
               var16.addSuppressed(var11);
            }

            throw var16;
         }

         fileInputStream.close();
         return var10;
      }
   }
}
