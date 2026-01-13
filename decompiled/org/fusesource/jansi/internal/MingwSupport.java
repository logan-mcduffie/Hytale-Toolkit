package org.fusesource.jansi.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MingwSupport {
   private final String sttyCommand;
   private final String ttyCommand;
   private final Pattern columnsPatterns;

   public MingwSupport() {
      String tty = null;
      String stty = null;
      String path = System.getenv("PATH");
      if (path != null) {
         String[] paths = path.split(File.pathSeparator);

         for (String p : paths) {
            File ttyFile = new File(p, "tty.exe");
            if (tty == null && ttyFile.canExecute()) {
               tty = ttyFile.getAbsolutePath();
            }

            File sttyFile = new File(p, "stty.exe");
            if (stty == null && sttyFile.canExecute()) {
               stty = sttyFile.getAbsolutePath();
            }
         }
      }

      if (tty == null) {
         tty = "tty.exe";
      }

      if (stty == null) {
         stty = "stty.exe";
      }

      this.ttyCommand = tty;
      this.sttyCommand = stty;
      this.columnsPatterns = Pattern.compile("\\bcolumns\\s+(\\d+)\\b");
   }

   public String getConsoleName(boolean stdout) {
      try {
         Process p = new ProcessBuilder(this.ttyCommand).redirectInput(this.getRedirect(stdout ? FileDescriptor.out : FileDescriptor.err)).start();
         String result = waitAndCapture(p);
         if (p.exitValue() == 0) {
            return result.trim();
         }
      } catch (Throwable var4) {
         if ("java.lang.reflect.InaccessibleObjectException".equals(var4.getClass().getName())) {
            System.err.println("MINGW support requires --add-opens java.base/java.lang=ALL-UNNAMED");
         }
      }

      return null;
   }

   public int getTerminalWidth(String name) {
      try {
         Process p = new ProcessBuilder(this.sttyCommand, "-F", name, "-a").start();
         String result = waitAndCapture(p);
         if (p.exitValue() != 0) {
            throw new IOException("Error executing '" + this.sttyCommand + "': " + result);
         } else {
            Matcher matcher = this.columnsPatterns.matcher(result);
            if (matcher.find()) {
               return Integer.parseInt(matcher.group(1));
            } else {
               throw new IOException("Unable to parse columns");
            }
         }
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   private static String waitAndCapture(Process p) throws IOException, InterruptedException {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      InputStream in = p.getInputStream();

      try {
         InputStream err = p.getErrorStream();

         try {
            int c;
            while ((c = in.read()) != -1) {
               bout.write(c);
            }

            while ((c = err.read()) != -1) {
               bout.write(c);
            }

            p.waitFor();
         } catch (Throwable var8) {
            if (err != null) {
               try {
                  err.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (err != null) {
            err.close();
         }
      } catch (Throwable var9) {
         if (in != null) {
            try {
               in.close();
            } catch (Throwable var6) {
               var9.addSuppressed(var6);
            }
         }

         throw var9;
      }

      if (in != null) {
         in.close();
      }

      return bout.toString();
   }

   private Redirect getRedirect(FileDescriptor fd) throws ReflectiveOperationException {
      Class<?> rpi = Class.forName("java.lang.ProcessBuilder$RedirectPipeImpl");
      Constructor<?> cns = rpi.getDeclaredConstructor();
      cns.setAccessible(true);
      Redirect input = (Redirect)cns.newInstance();
      Field f = rpi.getDeclaredField("fd");
      f.setAccessible(true);
      f.set(input, fd);
      return input;
   }
}
