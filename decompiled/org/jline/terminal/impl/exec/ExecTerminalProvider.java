package org.jline.terminal.impl.exec;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import org.jline.nativ.JLineLibrary;
import org.jline.nativ.JLineNativeLoader;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.ExternalTerminal;
import org.jline.terminal.impl.PosixSysTerminal;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.ExecHelper;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public class ExecTerminalProvider implements TerminalProvider {
   private static boolean warned;
   private static ExecTerminalProvider.RedirectPipeCreator redirectPipeCreator;

   @Override
   public String name() {
      return "exec";
   }

   public Pty current(SystemStream systemStream) throws IOException {
      if (!this.isSystemStream(systemStream)) {
         throw new IOException("Not a system stream: " + systemStream);
      } else {
         return ExecPty.current(this, systemStream);
      }
   }

   @Override
   public Terminal sysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      return OSUtils.IS_WINDOWS
         ? this.winSysTerminal(
            name, type, ansiPassThrough, encoding, inputEncoding, outputEncoding, outputEncoding, nativeSignals, signalHandler, paused, systemStream
         )
         : this.posixSysTerminal(
            name, type, ansiPassThrough, encoding, inputEncoding, outputEncoding, outputEncoding, nativeSignals, signalHandler, paused, systemStream
         );
   }

   @Deprecated
   @Override
   public Terminal sysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      return OSUtils.IS_WINDOWS
         ? this.winSysTerminal(
            name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused, systemStream
         )
         : this.posixSysTerminal(
            name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused, systemStream
         );
   }

   public Terminal winSysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      return this.winSysTerminal(name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused, systemStream);
   }

   public Terminal winSysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      if (!OSUtils.IS_CYGWIN && !OSUtils.IS_MSYSTEM) {
         return null;
      } else {
         Pty pty = this.current(systemStream);
         Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
         return new PosixSysTerminal(name, type, pty, encoding, stdinEncoding, outputEncoding, nativeSignals, signalHandler);
      }
   }

   public Terminal posixSysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      return this.posixSysTerminal(name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused, systemStream);
   }

   public Terminal posixSysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      Pty pty = this.current(systemStream);
      Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
      return new PosixSysTerminal(name, type, pty, encoding, stdinEncoding, outputEncoding, nativeSignals, signalHandler);
   }

   @Override
   public Terminal newTerminal(
      String name,
      String type,
      InputStream in,
      OutputStream out,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      Attributes attributes,
      Size size
   ) throws IOException {
      return new ExternalTerminal(this, name, type, in, out, encoding, inputEncoding, outputEncoding, signalHandler, paused, attributes, size);
   }

   @Deprecated
   @Override
   public Terminal newTerminal(
      String name,
      String type,
      InputStream in,
      OutputStream out,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      Attributes attributes,
      Size size
   ) throws IOException {
      return new ExternalTerminal(this, name, type, in, out, encoding, stdinEncoding, stdoutEncoding, signalHandler, paused, attributes, size);
   }

   @Override
   public boolean isSystemStream(SystemStream stream) {
      try {
         return this.isPosixSystemStream(stream) || this.isWindowsSystemStream(stream);
      } catch (Throwable var3) {
         return false;
      }
   }

   public boolean isWindowsSystemStream(SystemStream stream) {
      return this.systemStreamName(stream) != null;
   }

   public boolean isPosixSystemStream(SystemStream stream) {
      try {
         Process p = new ProcessBuilder(OSUtils.TEST_COMMAND, "-t", Integer.toString(stream.ordinal())).inheritIO().start();
         return p.waitFor() == 0;
      } catch (Throwable var3) {
         Log.debug("ExecTerminalProvider failed 'test -t' for " + stream, var3);
         return false;
      }
   }

   @Override
   public String systemStreamName(SystemStream stream) {
      try {
         Redirect input = stream == SystemStream.Input
            ? Redirect.INHERIT
            : newDescriptor(stream == SystemStream.Output ? FileDescriptor.out : FileDescriptor.err);
         Process p = new ProcessBuilder(OSUtils.TTY_COMMAND).redirectInput(input).start();
         String result = ExecHelper.waitAndCapture(p);
         if (p.exitValue() == 0) {
            return result.trim();
         }
      } catch (Throwable var5) {
         if ("java.lang.reflect.InaccessibleObjectException".equals(var5.getClass().getName()) && !warned) {
            Log.warn("The ExecTerminalProvider requires the JVM options: '--add-opens java.base/java.lang=ALL-UNNAMED'");
            warned = true;
         }
      }

      return null;
   }

   @Override
   public int systemStreamWidth(SystemStream stream) {
      try {
         ExecPty pty = new ExecPty(this, stream, null);

         int var3;
         try {
            var3 = pty.getSize().getColumns();
         } catch (Throwable var6) {
            try {
               pty.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         pty.close();
         return var3;
      } catch (Throwable var7) {
         return -1;
      }
   }

   protected static Redirect newDescriptor(FileDescriptor fd) {
      if (redirectPipeCreator == null) {
         String str = System.getProperty("org.jline.terminal.exec.redirectPipeCreationMode", TerminalBuilder.PROP_REDIRECT_PIPE_CREATION_MODE_DEFAULT);
         String[] modes = str.split(",");
         IllegalStateException ise = new IllegalStateException("Unable to create RedirectPipe");

         for (String mode : modes) {
            try {
               switch (mode) {
                  case "native":
                     redirectPipeCreator = new ExecTerminalProvider.NativeRedirectPipeCreator();
                     break;
                  case "reflection":
                     redirectPipeCreator = new ExecTerminalProvider.ReflectionRedirectPipeCreator();
               }
            } catch (Throwable var10) {
               ise.addSuppressed(var10);
            }

            if (redirectPipeCreator != null) {
               break;
            }
         }

         if (redirectPipeCreator == null) {
            throw ise;
         }
      }

      return redirectPipeCreator.newRedirectPipe(fd);
   }

   @Override
   public String toString() {
      return "TerminalProvider[" + this.name() + "]";
   }

   static class NativeRedirectPipeCreator implements ExecTerminalProvider.RedirectPipeCreator {
      public NativeRedirectPipeCreator() {
         JLineNativeLoader.initialize();
      }

      @Override
      public Redirect newRedirectPipe(FileDescriptor fd) {
         return JLineLibrary.newRedirectPipe(fd);
      }
   }

   interface RedirectPipeCreator {
      Redirect newRedirectPipe(FileDescriptor var1);
   }

   static class ReflectionRedirectPipeCreator implements ExecTerminalProvider.RedirectPipeCreator {
      private final Constructor<Redirect> constructor;
      private final Field fdField;

      ReflectionRedirectPipeCreator() throws Exception {
         Class<?> rpi = Class.forName("java.lang.ProcessBuilder$RedirectPipeImpl");
         this.constructor = (Constructor<Redirect>)rpi.getDeclaredConstructor();
         this.constructor.setAccessible(true);
         this.fdField = rpi.getDeclaredField("fd");
         this.fdField.setAccessible(true);
      }

      @Override
      public Redirect newRedirectPipe(FileDescriptor fd) {
         try {
            Redirect input = this.constructor.newInstance();
            this.fdField.set(input, fd);
            return input;
         } catch (ReflectiveOperationException var3) {
            throw new IllegalStateException(var3);
         }
      }
   }
}
