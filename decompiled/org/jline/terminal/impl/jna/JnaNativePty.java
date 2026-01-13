package org.jline.terminal.impl.jna;

import com.sun.jna.Platform;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.AbstractPty;
import org.jline.terminal.impl.jna.freebsd.FreeBsdNativePty;
import org.jline.terminal.impl.jna.linux.LinuxNativePty;
import org.jline.terminal.impl.jna.osx.OsXNativePty;
import org.jline.terminal.impl.jna.solaris.SolarisNativePty;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public abstract class JnaNativePty extends AbstractPty implements Pty {
   private final int master;
   private final int slave;
   private final int slaveOut;
   private final String name;
   private final FileDescriptor masterFD;
   private final FileDescriptor slaveFD;
   private final FileDescriptor slaveOutFD;

   public static JnaNativePty current(TerminalProvider provider, SystemStream systemStream) throws IOException {
      if (Platform.isMac()) {
         if (Platform.is64Bit() && Platform.isARM()) {
            throw new UnsupportedOperationException();
         } else {
            return OsXNativePty.current(provider, systemStream);
         }
      } else if (Platform.isLinux()) {
         return LinuxNativePty.current(provider, systemStream);
      } else if (Platform.isSolaris()) {
         return SolarisNativePty.current(provider, systemStream);
      } else if (Platform.isFreeBSD()) {
         return FreeBsdNativePty.current(provider, systemStream);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public static JnaNativePty open(TerminalProvider provider, Attributes attr, Size size) throws IOException {
      if (Platform.isMac()) {
         if (Platform.is64Bit() && Platform.isARM()) {
            throw new UnsupportedOperationException();
         } else {
            return OsXNativePty.open(provider, attr, size);
         }
      } else if (Platform.isLinux()) {
         return LinuxNativePty.open(provider, attr, size);
      } else if (Platform.isSolaris()) {
         return SolarisNativePty.open(provider, attr, size);
      } else if (Platform.isFreeBSD()) {
         return FreeBsdNativePty.open(provider, attr, size);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   protected JnaNativePty(
      TerminalProvider provider, SystemStream systemStream, int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, String name
   ) {
      this(provider, systemStream, master, masterFD, slave, slaveFD, slave, slaveFD, name);
   }

   protected JnaNativePty(
      TerminalProvider provider,
      SystemStream systemStream,
      int master,
      FileDescriptor masterFD,
      int slave,
      FileDescriptor slaveFD,
      int slaveOut,
      FileDescriptor slaveOutFD,
      String name
   ) {
      super(provider, systemStream);
      this.master = master;
      this.slave = slave;
      this.slaveOut = slaveOut;
      this.name = name;
      this.masterFD = masterFD;
      this.slaveFD = slaveFD;
      this.slaveOutFD = slaveOutFD;
   }

   @Override
   public void close() throws IOException {
      if (this.master > 0) {
         this.getMasterInput().close();
      }

      if (this.slave > 0) {
         this.getSlaveInput().close();
      }
   }

   public int getMaster() {
      return this.master;
   }

   public int getSlave() {
      return this.slave;
   }

   public int getSlaveOut() {
      return this.slaveOut;
   }

   public String getName() {
      return this.name;
   }

   public FileDescriptor getMasterFD() {
      return this.masterFD;
   }

   public FileDescriptor getSlaveFD() {
      return this.slaveFD;
   }

   public FileDescriptor getSlaveOutFD() {
      return this.slaveOutFD;
   }

   @Override
   public InputStream getMasterInput() {
      return new FileInputStream(this.getMasterFD());
   }

   @Override
   public OutputStream getMasterOutput() {
      return new FileOutputStream(this.getMasterFD());
   }

   @Override
   protected InputStream doGetSlaveInput() {
      return new FileInputStream(this.getSlaveFD());
   }

   @Override
   public OutputStream getSlaveOutput() {
      return new FileOutputStream(this.getSlaveOutFD());
   }

   @Override
   public String toString() {
      return "JnaNativePty[" + this.getName() + "]";
   }

   public static boolean isPosixSystemStream(SystemStream stream) {
      switch (stream) {
         case Input:
            return isatty(0);
         case Output:
            return isatty(1);
         case Error:
            return isatty(2);
         default:
            return false;
      }
   }

   public static String posixSystemStreamName(SystemStream stream) {
      switch (stream) {
         case Input:
            return ttyname(0);
         case Output:
            return ttyname(1);
         case Error:
            return ttyname(2);
         default:
            return null;
      }
   }

   private static boolean isatty(int fd) {
      if (Platform.isMac()) {
         if (Platform.is64Bit() && Platform.isARM()) {
            throw new UnsupportedOperationException("Unsupported platform mac-aarch64");
         } else {
            return OsXNativePty.isatty(fd) == 1;
         }
      } else if (Platform.isLinux()) {
         return LinuxNativePty.isatty(fd) == 1;
      } else if (Platform.isSolaris()) {
         return SolarisNativePty.isatty(fd) == 1;
      } else {
         return Platform.isFreeBSD() ? FreeBsdNativePty.isatty(fd) == 1 : false;
      }
   }

   private static String ttyname(int fd) {
      if (Platform.isMac()) {
         if (Platform.is64Bit() && Platform.isARM()) {
            throw new UnsupportedOperationException("Unsupported platform mac-aarch64");
         } else {
            return OsXNativePty.ttyname(fd);
         }
      } else if (Platform.isLinux()) {
         return LinuxNativePty.ttyname(fd);
      } else if (Platform.isSolaris()) {
         return SolarisNativePty.ttyname(fd);
      } else {
         return Platform.isFreeBSD() ? FreeBsdNativePty.ttyname(fd) : null;
      }
   }
}
