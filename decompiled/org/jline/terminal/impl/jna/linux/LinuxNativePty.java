package org.jline.terminal.impl.jna.linux;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.FileDescriptor;
import java.io.IOException;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.jna.JnaNativePty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public class LinuxNativePty extends JnaNativePty {
   private static final CLibrary C_LIBRARY = (CLibrary)Native.load(Platform.C_LIBRARY_NAME, CLibrary.class);

   public static LinuxNativePty current(TerminalProvider provider, SystemStream systemStream) throws IOException {
      switch (systemStream) {
         case Output:
            return new LinuxNativePty(provider, systemStream, -1, null, 0, FileDescriptor.in, 1, FileDescriptor.out, ttyname(0));
         case Error:
            return new LinuxNativePty(provider, systemStream, -1, null, 0, FileDescriptor.in, 2, FileDescriptor.err, ttyname(0));
         default:
            throw new IllegalArgumentException("Unsupported stream for console: " + systemStream);
      }
   }

   public static LinuxNativePty open(TerminalProvider provider, Attributes attr, Size size) throws IOException {
      int[] master = new int[1];
      int[] slave = new int[1];
      byte[] buf = new byte[64];
      LinuxNativePty.UtilLibrary.INSTANCE
         .openpty(master, slave, buf, attr != null ? new CLibrary.termios(attr) : null, size != null ? new CLibrary.winsize(size) : null);
      int len = 0;

      while (buf[len] != 0) {
         len++;
      }

      String name = new String(buf, 0, len);
      return new LinuxNativePty(provider, null, master[0], newDescriptor(master[0]), slave[0], newDescriptor(slave[0]), name);
   }

   public LinuxNativePty(
      TerminalProvider provider, SystemStream systemStream, int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, String name
   ) {
      super(provider, systemStream, master, masterFD, slave, slaveFD, name);
   }

   public LinuxNativePty(
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
      super(provider, systemStream, master, masterFD, slave, slaveFD, slaveOut, slaveOutFD, name);
   }

   @Override
   public Attributes getAttr() throws IOException {
      CLibrary.termios termios = new CLibrary.termios();
      C_LIBRARY.tcgetattr(this.getSlave(), termios);
      return termios.toAttributes();
   }

   @Override
   protected void doSetAttr(Attributes attr) throws IOException {
      CLibrary.termios termios = new CLibrary.termios(attr);
      CLibrary.termios org = new CLibrary.termios();
      C_LIBRARY.tcgetattr(this.getSlave(), org);
      org.c_iflag = termios.c_iflag;
      org.c_oflag = termios.c_oflag;
      org.c_lflag = termios.c_lflag;
      System.arraycopy(termios.c_cc, 0, org.c_cc, 0, termios.c_cc.length);
      C_LIBRARY.tcsetattr(this.getSlave(), 1, org);
   }

   @Override
   public Size getSize() throws IOException {
      CLibrary.winsize sz = new CLibrary.winsize();
      C_LIBRARY.ioctl(this.getSlave(), CLibrary.TIOCGWINSZ, sz);
      return sz.toSize();
   }

   @Override
   public void setSize(Size size) throws IOException {
      CLibrary.winsize sz = new CLibrary.winsize(size);
      C_LIBRARY.ioctl(this.getSlave(), CLibrary.TIOCSWINSZ, sz);
   }

   public static int isatty(int fd) {
      return C_LIBRARY.isatty(fd);
   }

   public static String ttyname(int slave) {
      byte[] buf = new byte[64];
      C_LIBRARY.ttyname_r(slave, buf, buf.length);
      int len = 0;

      while (buf[len] != 0) {
         len++;
      }

      return new String(buf, 0, len);
   }

   public interface UtilLibrary extends Library {
      LinuxNativePty.UtilLibrary INSTANCE = (LinuxNativePty.UtilLibrary)Native.load("util", LinuxNativePty.UtilLibrary.class);

      void openpty(int[] var1, int[] var2, byte[] var3, CLibrary.termios var4, CLibrary.winsize var5) throws LastErrorException;
   }
}
