package org.fusesource.jansi;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;
import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.MingwSupport;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.fusesource.jansi.io.FastBufferedOutputStream;
import org.fusesource.jansi.io.WindowsAnsiProcessor;

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
   @Deprecated
   public static final String JANSI_PASSTHROUGH = "jansi.passthrough";
   @Deprecated
   public static final String JANSI_STRIP = "jansi.strip";
   @Deprecated
   public static final String JANSI_FORCE = "jansi.force";
   @Deprecated
   public static final String JANSI_EAGER = "jansi.eager";
   public static final String JANSI_NORESET = "jansi.noreset";
   public static final String JANSI_GRACEFUL = "jansi.graceful";
   @Deprecated
   public static PrintStream system_out = System.out;
   @Deprecated
   public static PrintStream out;
   @Deprecated
   public static PrintStream system_err = System.err;
   @Deprecated
   public static PrintStream err;
   static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
   static final boolean IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
   static final boolean IS_MSYSTEM = IS_WINDOWS
      && System.getenv("MSYSTEM") != null
      && (System.getenv("MSYSTEM").startsWith("MINGW") || System.getenv("MSYSTEM").equals("MSYS"));
   static final boolean IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;
   static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
   static int STDOUT_FILENO = 1;
   static int STDERR_FILENO = 2;
   private static boolean initialized;
   private static int installed;
   private static int virtualProcessing;

   public static int getTerminalWidth() {
      int w = out().getTerminalWidth();
      if (w <= 0) {
         w = err().getTerminalWidth();
      }

      return w;
   }

   private AnsiConsole() {
   }

   private static AnsiPrintStream ansiStream(boolean stdout) {
      FileDescriptor descriptor = stdout ? FileDescriptor.out : FileDescriptor.err;
      OutputStream out = new FastBufferedOutputStream(new FileOutputStream(descriptor));
      String enc = System.getProperty(stdout ? "stdout.encoding" : "stderr.encoding");
      if (enc == null) {
         enc = System.getProperty(stdout ? "sun.stdout.encoding" : "sun.stderr.encoding");
      }

      final int fd = stdout ? STDOUT_FILENO : STDERR_FILENO;

      boolean isAtty;
      boolean withException;
      try {
         isAtty = CLibrary.isatty(fd) != 0;
         String term = System.getenv("TERM");
         String emacs = System.getenv("INSIDE_EMACS");
         if (isAtty && "dumb".equals(term) && emacs != null && !emacs.contains("comint")) {
            isAtty = false;
         }

         withException = false;
      } catch (Throwable var24) {
         isAtty = false;
         withException = true;
      }

      AnsiType type;
      AnsiOutputStream.IoRunnable installer;
      AnsiOutputStream.IoRunnable uninstaller;
      AnsiOutputStream.WidthSupplier width;
      AnsiProcessor processor;
      if (!isAtty) {
         processor = null;
         type = withException ? AnsiType.Unsupported : AnsiType.Redirected;
         uninstaller = null;
         installer = null;
         width = new AnsiOutputStream.ZeroWidthSupplier();
      } else if (IS_WINDOWS) {
         final long console = Kernel32.GetStdHandle(stdout ? Kernel32.STD_OUTPUT_HANDLE : Kernel32.STD_ERROR_HANDLE);
         int[] mode = new int[1];
         boolean isConsole = Kernel32.GetConsoleMode(console, mode) != 0;
         AnsiOutputStream.WidthSupplier kernel32Width = new AnsiOutputStream.WidthSupplier() {
            @Override
            public int getTerminalWidth() {
               Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
               Kernel32.GetConsoleScreenBufferInfo(console, info);
               return info.windowWidth();
            }
         };
         if (isConsole && Kernel32.SetConsoleMode(console, mode[0] | 4) != 0) {
            Kernel32.SetConsoleMode(console, mode[0]);
            processor = null;
            type = AnsiType.VirtualTerminal;
            installer = () -> {
               synchronized (AnsiConsole.class) {
                  virtualProcessing++;
                  Kernel32.SetConsoleMode(console, mode[0] | 4);
               }
            };
            uninstaller = () -> {
               synchronized (AnsiConsole.class) {
                  if (--virtualProcessing == 0) {
                     Kernel32.SetConsoleMode(console, mode[0]);
                  }
               }
            };
            width = kernel32Width;
         } else if ((IS_CONEMU || IS_CYGWIN || IS_MSYSTEM) && !isConsole) {
            processor = null;
            type = AnsiType.Native;
            uninstaller = null;
            installer = null;
            MingwSupport mingw = new MingwSupport();
            String name = mingw.getConsoleName(stdout);
            if (name != null && !name.isEmpty()) {
               width = () -> mingw.getTerminalWidth(name);
            } else {
               width = () -> -1;
            }
         } else {
            AnsiProcessor proc;
            AnsiType ttype;
            try {
               proc = new WindowsAnsiProcessor(out, console);
               ttype = AnsiType.Emulation;
            } catch (Throwable var23) {
               proc = new AnsiProcessor(out);
               ttype = AnsiType.Unsupported;
            }

            processor = proc;
            type = ttype;
            uninstaller = null;
            installer = null;
            width = kernel32Width;
         }
      } else {
         processor = null;
         type = AnsiType.Native;
         uninstaller = null;
         installer = null;
         width = new AnsiOutputStream.WidthSupplier() {
            @Override
            public int getTerminalWidth() {
               CLibrary.WinSize sz = new CLibrary.WinSize();
               CLibrary.ioctl(fd, CLibrary.TIOCGWINSZ, sz);
               return sz.ws_col;
            }
         };
      }

      String jansiMode = System.getProperty(stdout ? "jansi.out.mode" : "jansi.err.mode", System.getProperty("jansi.mode"));
      AnsiMode mode;
      if ("force".equals(jansiMode)) {
         mode = AnsiMode.Force;
      } else if ("strip".equals(jansiMode)) {
         mode = AnsiMode.Strip;
      } else if (jansiMode != null) {
         mode = isAtty ? AnsiMode.Default : AnsiMode.Strip;
      } else if (getBoolean("jansi.passthrough")) {
         mode = AnsiMode.Force;
      } else if (getBoolean("jansi.strip")) {
         mode = AnsiMode.Strip;
      } else if (getBoolean("jansi.force")) {
         mode = AnsiMode.Force;
      } else {
         mode = isAtty ? AnsiMode.Default : AnsiMode.Strip;
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
      Charset cs = Charset.defaultCharset();
      if (enc != null) {
         try {
            cs = Charset.forName(enc);
         } catch (UnsupportedCharsetException var22) {
         }
      }

      return newPrintStream(new AnsiOutputStream(out, width, mode, processor, type, colors, cs, installer, uninstaller, resetAtUninstall), cs.name());
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
      initStreams();
      return (AnsiPrintStream)out;
   }

   public static PrintStream sysOut() {
      return system_out;
   }

   public static AnsiPrintStream err() {
      initStreams();
      return (AnsiPrintStream)err;
   }

   public static PrintStream sysErr() {
      return system_err;
   }

   public static synchronized void systemInstall() {
      if (installed == 0) {
         initStreams();

         try {
            ((AnsiPrintStream)out).install();
            ((AnsiPrintStream)err).install();
         } catch (IOException var1) {
            throw new IOError(var1);
         }

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
         try {
            ((AnsiPrintStream)out).uninstall();
            ((AnsiPrintStream)err).uninstall();
         } catch (IOException var1) {
            throw new IOError(var1);
         }

         initialized = false;
         System.setOut(system_out);
         System.setErr(system_err);
      }
   }

   static synchronized void initStreams() {
      if (!initialized) {
         out = ansiStream(true);
         err = ansiStream(false);
         initialized = true;
      }
   }

   static {
      if (getBoolean("jansi.eager")) {
         initStreams();
      }
   }
}
