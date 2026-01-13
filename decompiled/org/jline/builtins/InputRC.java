package org.jline.builtins;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jline.reader.LineReader;

public final class InputRC {
   public static void configure(LineReader reader, URL url) throws IOException {
      org.jline.reader.impl.InputRC.configure(reader, url);
   }

   public static void configure(LineReader reader, InputStream is) throws IOException {
      org.jline.reader.impl.InputRC.configure(reader, is);
   }

   public static void configure(LineReader reader, Reader r) throws IOException {
      org.jline.reader.impl.InputRC.configure(reader, r);
   }

   public static void configure(LineReader lineReader, Path path) throws IOException {
      if (Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path)) {
         Reader reader = Files.newBufferedReader(path);

         try {
            configure(lineReader, reader);
         } catch (Throwable var6) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (reader != null) {
            reader.close();
         }
      }
   }

   public static void configure(LineReader lineReader) throws IOException {
      String userHome = System.getProperty("user.home");
      if (userHome != null) {
         configure(lineReader, Paths.get(userHome, ".inputrc"));
      }

      configure(lineReader, Paths.get("/etc/inputrc"));
   }
}
