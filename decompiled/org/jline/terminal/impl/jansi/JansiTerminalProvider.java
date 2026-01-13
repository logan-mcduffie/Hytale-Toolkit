package org.jline.terminal.impl.jansi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.internal.Kernel32;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.PosixPtyTerminal;
import org.jline.terminal.impl.PosixSysTerminal;
import org.jline.terminal.impl.jansi.freebsd.FreeBsdNativePty;
import org.jline.terminal.impl.jansi.linux.LinuxNativePty;
import org.jline.terminal.impl.jansi.osx.OsXNativePty;
import org.jline.terminal.impl.jansi.win.JansiWinSysTerminal;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

public class JansiTerminalProvider implements TerminalProvider {
   static final int JANSI_MAJOR_VERSION;
   static final int JANSI_MINOR_VERSION;

   public static int getJansiMajorVersion() {
      return JANSI_MAJOR_VERSION;
   }

   public static int getJansiMinorVersion() {
      return JANSI_MINOR_VERSION;
   }

   public static boolean isAtLeast(int major, int minor) {
      return JANSI_MAJOR_VERSION > major || JANSI_MAJOR_VERSION == major && JANSI_MINOR_VERSION >= minor;
   }

   public static void verifyAtLeast(int major, int minor) {
      if (!isAtLeast(major, minor)) {
         throw new UnsupportedOperationException(
            "An old version of Jansi is loaded from " + Kernel32.class.getClassLoader().getResource(Kernel32.class.getName().replace('.', '/') + ".class")
         );
      }
   }

   public JansiTerminalProvider() {
      verifyAtLeast(1, 17);
      this.checkIsSystemStream(SystemStream.Output);
   }

   @Override
   public String name() {
      return "jansi";
   }

   public Pty current(SystemStream systemStream) throws IOException {
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Linux")) {
         return LinuxNativePty.current(this, systemStream);
      } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
         return OsXNativePty.current(this, systemStream);
      } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
         throw new UnsupportedOperationException("Unsupported platform " + osName);
      } else if (osName.startsWith("FreeBSD")) {
         return FreeBsdNativePty.current(this, systemStream);
      } else {
         throw new UnsupportedOperationException("Unsupported platform " + osName);
      }
   }

   public Pty open(Attributes attributes, Size size) throws IOException {
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Linux")) {
         return LinuxNativePty.open(this, attributes, size);
      } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
         return OsXNativePty.open(this, attributes, size);
      } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
         throw new UnsupportedOperationException("Unsupported platform " + osName);
      } else if (osName.startsWith("FreeBSD")) {
         return FreeBsdNativePty.open(this, attributes, size);
      } else {
         throw new UnsupportedOperationException("Unsupported platform " + osName);
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
      JansiWinSysTerminal terminal = JansiWinSysTerminal.createTerminal(
         this, systemStream, name, type, ansiPassThrough, encoding, stdinEncoding, stdoutEncoding, stderrEncoding, nativeSignals, signalHandler, paused
      );
      terminal.disableScrolling();
      return terminal;
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
      Pty pty = this.open(attributes, size);
      return new PosixPtyTerminal(name, type, pty, in, out, encoding, inputEncoding, outputEncoding, signalHandler, paused);
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
      Pty pty = this.open(attributes, size);
      return new PosixPtyTerminal(name, type, pty, in, out, encoding, stdinEncoding, stdoutEncoding, signalHandler, paused);
   }

   @Override
   public boolean isSystemStream(SystemStream stream) {
      try {
         return this.checkIsSystemStream(stream);
      } catch (Throwable var3) {
         return false;
      }
   }

   private boolean checkIsSystemStream(SystemStream stream) {
      return OSUtils.IS_WINDOWS ? JansiWinSysTerminal.isWindowsSystemStream(stream) : JansiNativePty.isPosixSystemStream(stream);
   }

   @Override
   public String systemStreamName(SystemStream stream) {
      return JansiNativePty.posixSystemStreamName(stream);
   }

   @Override
   public int systemStreamWidth(SystemStream stream) {
      return JansiNativePty.systemStreamWidth(stream);
   }

   @Override
   public String toString() {
      return "TerminalProvider[" + this.name() + "]";
   }

   static {
      int major = 0;
      int minor = 0;

      try {
         String v = null;

         try {
            InputStream is = AnsiConsole.class.getResourceAsStream("jansi.properties");

            try {
               if (is != null) {
                  Properties props = new Properties();
                  props.load(is);
                  v = props.getProperty("version");
               }
            } catch (Throwable var7) {
               if (is != null) {
                  try {
                     is.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (is != null) {
               is.close();
            }
         } catch (IOException var8) {
         }

         if (v == null) {
            v = AnsiConsole.class.getPackage().getImplementationVersion();
         }

         if (v != null) {
            Matcher m = Pattern.compile("([0-9]+)\\.([0-9]+)([\\.-]\\S+)?").matcher(v);
            if (m.matches()) {
               major = Integer.parseInt(m.group(1));
               minor = Integer.parseInt(m.group(2));
            }
         }
      } catch (Throwable var9) {
      }

      JANSI_MAJOR_VERSION = major;
      JANSI_MINOR_VERSION = minor;
   }
}
