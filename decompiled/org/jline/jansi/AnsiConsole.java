package org.jline.jansi;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.jline.jansi.io.AnsiOutputStream;
import org.jline.jansi.io.AnsiProcessor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.terminal.spi.TerminalExt;
import org.jline.utils.OSUtils;

public class AnsiConsole {
   public static final String JANSI_MODE = "jansi.mode";
   public static final String JANSI_OUT_MODE = "jansi.out.mode";
   public static final String JANSI_ERR_MODE = "jansi.err.mode";
   public static final String JANSI_MODE_STRIP = "strip";
   public static final String JANSI_MODE_FORCE = "force";
   public static final String JANSI_MODE_DEFAULT = "default";
   public static final String JANSI_COLORS = "jansi.colors";
   public static final String JANSI_OUT_COLORS = "jansi.out.colors";
   public static final String JANSI_ERR_COLORS = "jansi.err.colors";
   public static final String JANSI_COLORS_16 = "16";
   public static final String JANSI_COLORS_256 = "256";
   public static final String JANSI_COLORS_TRUECOLOR = "truecolor";
   public static final String JANSI_NORESET = "jansi.noreset";
   public static final String JANSI_GRACEFUL = "jansi.graceful";
   public static final String JANSI_PROVIDERS = "jansi.providers";
   public static final String JANSI_PROVIDER_JNI = "jni";
   public static final String JANSI_PROVIDER_FFM = "ffm";
   public static final String JANSI_PROVIDER_NATIVE_IMAGE = "native-image";
   private static final PrintStream system_out = System.out;
   private static PrintStream out;
   private static final PrintStream system_err = System.err;
   private static PrintStream err;
   static final boolean IS_WINDOWS = OSUtils.IS_WINDOWS;
   static final boolean IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
   static final boolean IS_MSYSTEM = IS_WINDOWS
      && System.getenv("MSYSTEM") != null
      && (System.getenv("MSYSTEM").startsWith("MINGW") || System.getenv("MSYSTEM").equals("MSYS"));
   static final boolean IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;
   static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
   static int STDOUT_FILENO = 1;
   static int STDERR_FILENO = 2;
   private static int installed;
   static Terminal terminal;

   public static int getTerminalWidth() {
      int w = out().getTerminalWidth();
      if (w <= 0) {
         w = err().getTerminalWidth();
      }

      return w;
   }

   private AnsiConsole() {
   }

   public static Terminal getTerminal() {
      return terminal;
   }

   public static void setTerminal(Terminal terminal) {
      AnsiConsole.terminal = terminal;
   }

   static synchronized void doInstall() {
      try {
         if (terminal == null) {
            TerminalBuilder builder = TerminalBuilder.builder().system(true).name("jansi").providers(System.getProperty("jansi.providers"));
            String graceful = System.getProperty("jansi.graceful");
            if (graceful != null) {
               builder.dumb(Boolean.parseBoolean(graceful));
            }

            terminal = builder.build();
         }

         if (out == null) {
            out = ansiStream(true);
            err = ansiStream(false);
         }
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }

   static synchronized void doUninstall() {
      try {
         if (terminal != null) {
            terminal.close();
         }
      } catch (IOException var4) {
         throw new IOError(var4);
      } finally {
         terminal = null;
         out = null;
         err = null;
      }
   }

   private static AnsiPrintStream ansiStream(boolean stdout) throws IOException {
      AnsiProcessor processor = null;
      AnsiOutputStream.IoRunnable installer = null;
      AnsiOutputStream.IoRunnable uninstaller = null;
      OutputStream out = terminal.output();
      AnsiOutputStream.WidthSupplier width = terminal::getWidth;
      AnsiType type = terminal instanceof DumbTerminal
         ? AnsiType.Unsupported
         : (((TerminalExt)terminal).getSystemStream() != null ? AnsiType.Native : AnsiType.Redirected);
      String jansiMode = System.getProperty(stdout ? "jansi.out.mode" : "jansi.err.mode", System.getProperty("jansi.mode"));
      AnsiMode mode;
      if ("force".equals(jansiMode)) {
         mode = AnsiMode.Force;
      } else if ("strip".equals(jansiMode)) {
         mode = AnsiMode.Strip;
      } else {
         mode = type == AnsiType.Native ? AnsiMode.Default : AnsiMode.Strip;
      }

      String jansiColors = System.getProperty(stdout ? "jansi.out.colors" : "jansi.err.colors", System.getProperty("jansi.colors"));
      AnsiColors colors;
      if ("truecolor".equals(jansiColors)) {
         colors = AnsiColors.TrueColor;
      } else if ("256".equals(jansiColors)) {
         colors = AnsiColors.Colors256;
      } else if (jansiColors != null) {
         colors = AnsiColors.Colors16;
      } else {
         String colorterm;
         if ((colorterm = System.getenv("COLORTERM")) == null || !colorterm.contains("truecolor") && !colorterm.contains("24bit")) {
            String term;
            if ((term = System.getenv("TERM")) != null && term.contains("-direct")) {
               colors = AnsiColors.TrueColor;
            } else if (term != null && term.contains("-256color")) {
               colors = AnsiColors.Colors256;
            } else {
               colors = AnsiColors.Colors16;
            }
         } else {
            colors = AnsiColors.TrueColor;
         }
      }

      boolean resetAtUninstall = type != AnsiType.Unsupported && !getBoolean("jansi.noreset");
      return newPrintStream(
         new AnsiOutputStream(out, width, mode, processor, type, colors, terminal.encoding(), installer, uninstaller, resetAtUninstall),
         terminal.encoding().name()
      );
   }

   private static AnsiPrintStream newPrintStream(AnsiOutputStream out, String enc) {
      if (enc != null) {
         try {
            return new AnsiPrintStream(out, true, enc);
         } catch (UnsupportedEncodingException var3) {
         }
      }

      return new AnsiPrintStream(out, true);
   }

   static boolean getBoolean(String name) {
      boolean result = false;

      try {
         String val = System.getProperty(name);
         result = val.isEmpty() || Boolean.parseBoolean(val);
      } catch (NullPointerException | IllegalArgumentException var3) {
      }

      return result;
   }

   public static AnsiPrintStream out() {
      doInstall();
      return (AnsiPrintStream)out;
   }

   public static PrintStream sysOut() {
      return system_out;
   }

   public static AnsiPrintStream err() {
      doInstall();
      return (AnsiPrintStream)err;
   }

   public static PrintStream sysErr() {
      return system_err;
   }

   public static synchronized void systemInstall() {
      if (installed == 0) {
         doInstall();
         System.setOut(out);
         System.setErr(err);
      }

      installed++;
   }

   public static synchronized boolean isInstalled() {
      return installed > 0;
   }

   public static synchronized void systemUninstall() {
      installed--;
      if (installed == 0) {
         doUninstall();
         System.setOut(system_out);
         System.setErr(system_err);
      }
   }
}
