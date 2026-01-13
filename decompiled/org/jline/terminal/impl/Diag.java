package org.jline.terminal.impl;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

public class Diag {
   private final PrintStream out;
   private final boolean verbose;

   public static void main(String[] args) {
      diag(System.out, Arrays.asList(args).contains("--verbose"));
   }

   public static void diag(PrintStream out) {
      diag(out, true);
   }

   public static void diag(PrintStream out, boolean verbose) {
      new Diag(out, verbose).run();
   }

   public Diag(PrintStream out, boolean verbose) {
      this.out = out;
      this.verbose = verbose;
   }

   public void run() {
      this.out.println("System properties");
      this.out.println("=================");
      this.out.println("os.name =         " + System.getProperty("os.name"));
      this.out.println("OSTYPE =          " + System.getenv("OSTYPE"));
      this.out.println("MSYSTEM =         " + System.getenv("MSYSTEM"));
      this.out.println("PWD =             " + System.getenv("PWD"));
      this.out.println("ConEmuPID =       " + System.getenv("ConEmuPID"));
      this.out.println("WSL_DISTRO_NAME = " + System.getenv("WSL_DISTRO_NAME"));
      this.out.println("WSL_INTEROP =     " + System.getenv("WSL_INTEROP"));
      this.out.println();
      this.out.println("OSUtils");
      this.out.println("=================");
      this.out.println("IS_WINDOWS = " + OSUtils.IS_WINDOWS);
      this.out.println("IS_CYGWIN =  " + OSUtils.IS_CYGWIN);
      this.out.println("IS_MSYSTEM = " + OSUtils.IS_MSYSTEM);
      this.out.println("IS_WSL =     " + OSUtils.IS_WSL);
      this.out.println("IS_WSL1 =    " + OSUtils.IS_WSL1);
      this.out.println("IS_WSL2 =    " + OSUtils.IS_WSL2);
      this.out.println("IS_CONEMU =  " + OSUtils.IS_CONEMU);
      this.out.println("IS_OSX =     " + OSUtils.IS_OSX);
      this.out.println();
      this.out.println("FFM Support");
      this.out.println("=================");

      try {
         TerminalProvider provider = TerminalProvider.load("ffm");
         this.testProvider(provider);
      } catch (Throwable var6) {
         this.error("FFM support not available", var6);
      }

      this.out.println();
      this.out.println("JnaSupport");
      this.out.println("=================");

      try {
         TerminalProvider provider = TerminalProvider.load("jna");
         this.testProvider(provider);
      } catch (Throwable var5) {
         this.error("JNA support not available", var5);
      }

      this.out.println();
      this.out.println("Jansi2Support");
      this.out.println("=================");

      try {
         TerminalProvider provider = TerminalProvider.load("jansi");
         this.testProvider(provider);
      } catch (Throwable var4) {
         this.error("Jansi 2 support not available", var4);
      }

      this.out.println();
      this.out.println("JniSupport");
      this.out.println("=================");

      try {
         TerminalProvider provider = TerminalProvider.load("jni");
         this.testProvider(provider);
      } catch (Throwable var3) {
         this.error("JNI support not available", var3);
      }

      this.out.println();
      this.out.println("Exec Support");
      this.out.println("=================");

      try {
         TerminalProvider provider = TerminalProvider.load("exec");
         this.testProvider(provider);
      } catch (Throwable var2) {
         this.error("Exec support not available", var2);
      }

      if (!this.verbose) {
         this.out.println();
         this.out.println("Run with --verbose argument to print stack traces");
      }
   }

   private void testProvider(TerminalProvider provider) {
      try {
         this.out.println("StdIn stream =    " + provider.isSystemStream(SystemStream.Input));
         this.out.println("StdOut stream =   " + provider.isSystemStream(SystemStream.Output));
         this.out.println("StdErr stream =   " + provider.isSystemStream(SystemStream.Error));
      } catch (Throwable var29) {
         this.error("Unable to check stream", var29);
      }

      try {
         this.out.println("StdIn stream name =     " + provider.systemStreamName(SystemStream.Input));
         this.out.println("StdOut stream name =    " + provider.systemStreamName(SystemStream.Output));
         this.out.println("StdErr stream name =    " + provider.systemStreamName(SystemStream.Error));
      } catch (Throwable var28) {
         this.error("Unable to check stream names", var28);
      }

      try {
         Terminal terminal = provider.sysTerminal(
            "diag",
            "xterm",
            false,
            StandardCharsets.UTF_8,
            StandardCharsets.UTF_8,
            StandardCharsets.UTF_8,
            false,
            Terminal.SignalHandler.SIG_DFL,
            false,
            SystemStream.Output
         );

         try {
            if (terminal != null) {
               Attributes attr = terminal.enterRawMode();

               try {
                  this.out.println("Terminal size: " + terminal.getSize());
                  ForkJoinPool forkJoinPool = new ForkJoinPool(1);

                  try {
                     ForkJoinTask<Integer> t = forkJoinPool.submit(() -> terminal.reader().read(1L));
                     t.get(1000L, TimeUnit.MILLISECONDS);
                  } finally {
                     forkJoinPool.shutdown();
                  }

                  StringBuilder sb = new StringBuilder();
                  sb.append("The terminal seems to work: ");
                  sb.append("terminal ").append(terminal.getClass().getName());
                  if (terminal instanceof AbstractPosixTerminal) {
                     sb.append(" with pty ").append(((AbstractPosixTerminal)terminal).getPty().getClass().getName());
                  }

                  this.out.println(sb);
               } catch (Throwable var26) {
                  this.error("Unable to read from terminal", var26);
               } finally {
                  terminal.setAttributes(attr);
               }
            } else {
               this.out.println("Not supported by provider");
            }
         } catch (Throwable var30) {
            if (terminal != null) {
               try {
                  terminal.close();
               } catch (Throwable var24) {
                  var30.addSuppressed(var24);
               }
            }

            throw var30;
         }

         if (terminal != null) {
            terminal.close();
         }
      } catch (Throwable var31) {
         this.error("Unable to open terminal", var31);
      }
   }

   private void error(String message, Throwable cause) {
      if (this.verbose) {
         this.out.println(message);
         cause.printStackTrace(this.out);
      } else {
         this.out.println(message + ": " + cause);
      }
   }
}
