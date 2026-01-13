package com.nimbusds.jose.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class IOUtils {
   public static String readInputStreamToString(InputStream stream) throws IOException {
      return readInputStreamToString(stream, StandardCharset.UTF_8);
   }

   public static String readInputStreamToString(InputStream stream, Charset charset) throws IOException {
      int bufferSize = 1024;
      char[] buffer = new char[1024];
      StringBuilder out = new StringBuilder();
      Reader in = new InputStreamReader(stream, charset);

      String var10;
      try {
         while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0) {
               var10 = out.toString();
               break;
            }

            out.append(buffer, 0, rsz);
         }
      } catch (Throwable var9) {
         try {
            in.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      in.close();
      return var10;
   }

   public static String readFileToString(File file) throws IOException {
      return readInputStreamToString(new FileInputStream(file));
   }

   public static String readFileToString(File file, Charset charset) throws IOException {
      return readInputStreamToString(new FileInputStream(file), charset);
   }

   public static void closeSilently(Closeable closeable) {
      try {
         closeable.close();
      } catch (IOException var2) {
      }
   }

   private IOUtils() {
   }
}
