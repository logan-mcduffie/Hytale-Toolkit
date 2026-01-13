package org.jline.terminal.impl.jni.solaris;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import org.jline.nativ.CLibrary;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.jni.JniNativePty;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public class SolarisNativePty extends JniNativePty {
   private static final int VINTR = 0;
   private static final int VQUIT = 1;
   private static final int VERASE = 2;
   private static final int VKILL = 3;
   private static final int VEOF = 4;
   private static final int VTIME = 5;
   private static final int VMIN = 6;
   private static final int VSWTC = 7;
   private static final int VSTART = 8;
   private static final int VSTOP = 9;
   private static final int VSUSP = 10;
   private static final int VEOL = 11;
   private static final int VREPRINT = 12;
   private static final int VDISCARD = 13;
   private static final int VWERASE = 14;
   private static final int VLNEXT = 15;
   private static final int VEOL2 = 16;
   private static final int IGNBRK = 1;
   private static final int BRKINT = 2;
   private static final int IGNPAR = 4;
   private static final int PARMRK = 16;
   private static final int INPCK = 32;
   private static final int ISTRIP = 64;
   private static final int INLCR = 256;
   private static final int IGNCR = 512;
   private static final int ICRNL = 1024;
   private static final int IUCLC = 4096;
   private static final int IXON = 8192;
   private static final int IXANY = 16384;
   private static final int IXOFF = 65536;
   private static final int IMAXBEL = 131072;
   private static final int IUTF8 = 262144;
   private static final int OPOST = 1;
   private static final int OLCUC = 2;
   private static final int ONLCR = 4;
   private static final int OCRNL = 16;
   private static final int ONOCR = 32;
   private static final int ONLRET = 64;
   private static final int OFILL = 256;
   private static final int OFDEL = 512;
   private static final int NLDLY = 1024;
   private static final int NL0 = 0;
   private static final int NL1 = 1024;
   private static final int CRDLY = 12288;
   private static final int CR0 = 0;
   private static final int CR1 = 4096;
   private static final int CR2 = 8192;
   private static final int CR3 = 12288;
   private static final int TABDLY = 81920;
   private static final int TAB0 = 0;
   private static final int TAB1 = 16384;
   private static final int TAB2 = 65536;
   private static final int TAB3 = 81920;
   private static final int XTABS = 81920;
   private static final int BSDLY = 131072;
   private static final int BS0 = 0;
   private static final int BS1 = 131072;
   private static final int VTDLY = 262144;
   private static final int VT0 = 0;
   private static final int VT1 = 262144;
   private static final int FFDLY = 1048576;
   private static final int FF0 = 0;
   private static final int FF1 = 1048576;
   private static final int CBAUD = 65559;
   private static final int B0 = 0;
   private static final int B50 = 1;
   private static final int B75 = 2;
   private static final int B110 = 3;
   private static final int B134 = 4;
   private static final int B150 = 5;
   private static final int B200 = 6;
   private static final int B300 = 7;
   private static final int B600 = 16;
   private static final int B1200 = 17;
   private static final int B1800 = 18;
   private static final int B2400 = 19;
   private static final int B4800 = 20;
   private static final int B9600 = 21;
   private static final int B19200 = 22;
   private static final int B38400 = 23;
   private static final int EXTA = 11637248;
   private static final int EXTB = 11764736;
   private static final int CSIZE = 96;
   private static final int CS5 = 0;
   private static final int CS6 = 32;
   private static final int CS7 = 64;
   private static final int CS8 = 96;
   private static final int CSTOPB = 256;
   private static final int CREAD = 512;
   private static final int PARENB = 1024;
   private static final int PARODD = 4096;
   private static final int HUPCL = 8192;
   private static final int CLOCAL = 16384;
   private static final int ISIG = 1;
   private static final int ICANON = 2;
   private static final int XCASE = 4;
   private static final int ECHO = 16;
   private static final int ECHOE = 32;
   private static final int ECHOK = 64;
   private static final int ECHONL = 256;
   private static final int NOFLSH = 512;
   private static final int TOSTOP = 1024;
   private static final int ECHOCTL = 4096;
   private static final int ECHOPRT = 8192;
   private static final int ECHOKE = 16384;
   private static final int FLUSHO = 65536;
   private static final int PENDIN = 262144;
   private static final int IEXTEN = 1048576;
   private static final int EXTPROC = 2097152;

   public static SolarisNativePty current(TerminalProvider provider, SystemStream systemStream) throws IOException {
      try {
         switch (systemStream) {
            case Output:
               return new SolarisNativePty(provider, systemStream, -1, null, 0, FileDescriptor.in, 1, FileDescriptor.out, ttyname(1));
            case Error:
               return new SolarisNativePty(provider, systemStream, -1, null, 0, FileDescriptor.in, 2, FileDescriptor.err, ttyname(2));
            default:
               throw new IllegalArgumentException("Unsupported stream for console: " + systemStream);
         }
      } catch (IOException var3) {
         throw new IOException("Not a tty", var3);
      }
   }

   public static SolarisNativePty open(TerminalProvider provider, Attributes attr, Size size) throws IOException {
      int[] master = new int[1];
      int[] slave = new int[1];
      byte[] buf = new byte[64];
      CLibrary.openpty(
         master, slave, buf, attr != null ? termios(attr) : null, size != null ? new CLibrary.WinSize((short)size.getRows(), (short)size.getColumns()) : null
      );
      int len = 0;

      while (buf[len] != 0) {
         len++;
      }

      String name = new String(buf, 0, len);
      return new SolarisNativePty(provider, null, master[0], newDescriptor(master[0]), slave[0], newDescriptor(slave[0]), name);
   }

   public SolarisNativePty(
      TerminalProvider provider, SystemStream systemStream, int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, String name
   ) {
      super(provider, systemStream, master, masterFD, slave, slaveFD, name);
   }

   public SolarisNativePty(
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
   protected CLibrary.Termios toTermios(Attributes t) {
      return termios(t);
   }

   static CLibrary.Termios termios(Attributes t) {
      CLibrary.Termios tio = new CLibrary.Termios();
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNBRK), 1L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.BRKINT), 2L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNPAR), 4L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.PARMRK), 16L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.INPCK), 32L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.ISTRIP), 64L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.INLCR), 256L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNCR), 512L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.ICRNL), 1024L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXON), 8192L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXOFF), 65536L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXANY), 16384L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IMAXBEL), 131072L, tio.c_iflag);
      tio.c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IUTF8), 262144L, tio.c_iflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OPOST), 1L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLCR), 4L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OCRNL), 16L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOCR), 32L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLRET), 64L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OFILL), 256L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.NLDLY), 1024L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.TABDLY), 81920L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.CRDLY), 12288L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.FFDLY), 1048576L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.BSDLY), 131072L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.VTDLY), 262144L, tio.c_oflag);
      tio.c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OFDEL), 512L, tio.c_oflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS5), 0L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS6), 32L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS7), 64L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS8), 96L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CSTOPB), 256L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CREAD), 512L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.PARENB), 1024L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.PARODD), 4096L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.HUPCL), 8192L, tio.c_cflag);
      tio.c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CLOCAL), 16384L, tio.c_cflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOKE), 16384L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOE), 32L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOK), 64L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHO), 16L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHONL), 256L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOPRT), 8192L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOCTL), 4096L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ISIG), 1L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ICANON), 2L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.IEXTEN), 1048576L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.EXTPROC), 2097152L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.TOSTOP), 1024L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.FLUSHO), 65536L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.PENDIN), 262144L, tio.c_lflag);
      tio.c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.NOFLSH), 512L, tio.c_lflag);
      tio.c_cc[4] = (byte)t.getControlChar(Attributes.ControlChar.VEOF);
      tio.c_cc[11] = (byte)t.getControlChar(Attributes.ControlChar.VEOL);
      tio.c_cc[16] = (byte)t.getControlChar(Attributes.ControlChar.VEOL2);
      tio.c_cc[2] = (byte)t.getControlChar(Attributes.ControlChar.VERASE);
      tio.c_cc[14] = (byte)t.getControlChar(Attributes.ControlChar.VWERASE);
      tio.c_cc[3] = (byte)t.getControlChar(Attributes.ControlChar.VKILL);
      tio.c_cc[12] = (byte)t.getControlChar(Attributes.ControlChar.VREPRINT);
      tio.c_cc[0] = (byte)t.getControlChar(Attributes.ControlChar.VINTR);
      tio.c_cc[1] = (byte)t.getControlChar(Attributes.ControlChar.VQUIT);
      tio.c_cc[10] = (byte)t.getControlChar(Attributes.ControlChar.VSUSP);
      tio.c_cc[8] = (byte)t.getControlChar(Attributes.ControlChar.VSTART);
      tio.c_cc[9] = (byte)t.getControlChar(Attributes.ControlChar.VSTOP);
      tio.c_cc[15] = (byte)t.getControlChar(Attributes.ControlChar.VLNEXT);
      tio.c_cc[13] = (byte)t.getControlChar(Attributes.ControlChar.VDISCARD);
      tio.c_cc[6] = (byte)t.getControlChar(Attributes.ControlChar.VMIN);
      tio.c_cc[5] = (byte)t.getControlChar(Attributes.ControlChar.VTIME);
      return tio;
   }

   @Override
   protected Attributes toAttributes(CLibrary.Termios tio) {
      Attributes attr = new Attributes();
      EnumSet<Attributes.InputFlag> iflag = attr.getInputFlags();
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNBRK, 1);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNBRK, 1);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.BRKINT, 2);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNPAR, 4);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.PARMRK, 16);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.INPCK, 32);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.ISTRIP, 64);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.INLCR, 256);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IGNCR, 512);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.ICRNL, 1024);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IXON, 8192);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IXOFF, 65536);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IXANY, 16384);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IMAXBEL, 131072);
      addFlag(tio.c_iflag, iflag, Attributes.InputFlag.IUTF8, 262144);
      EnumSet<Attributes.OutputFlag> oflag = attr.getOutputFlags();
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.OPOST, 1);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.ONLCR, 4);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.OCRNL, 16);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.ONOCR, 32);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.ONLRET, 64);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.OFILL, 256);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.NLDLY, 1024);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.TABDLY, 81920);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.CRDLY, 12288);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.FFDLY, 1048576);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.BSDLY, 131072);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.VTDLY, 262144);
      addFlag(tio.c_oflag, oflag, Attributes.OutputFlag.OFDEL, 512);
      EnumSet<Attributes.ControlFlag> cflag = attr.getControlFlags();
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS5, 0);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS6, 32);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS7, 64);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CS8, 96);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CSTOPB, 256);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CREAD, 512);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.PARENB, 1024);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.PARODD, 4096);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.HUPCL, 8192);
      addFlag(tio.c_cflag, cflag, Attributes.ControlFlag.CLOCAL, 16384);
      EnumSet<Attributes.LocalFlag> lflag = attr.getLocalFlags();
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOKE, 16384);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOE, 32);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOK, 64);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHO, 16);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHONL, 256);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOPRT, 8192);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ECHOCTL, 4096);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ISIG, 1);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.ICANON, 2);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.IEXTEN, 1048576);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.EXTPROC, 2097152);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.TOSTOP, 1024);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.FLUSHO, 65536);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.PENDIN, 262144);
      addFlag(tio.c_lflag, lflag, Attributes.LocalFlag.NOFLSH, 512);
      EnumMap<Attributes.ControlChar, Integer> cc = attr.getControlChars();
      cc.put(Attributes.ControlChar.VEOF, Integer.valueOf(tio.c_cc[4]));
      cc.put(Attributes.ControlChar.VEOL, Integer.valueOf(tio.c_cc[11]));
      cc.put(Attributes.ControlChar.VEOL2, Integer.valueOf(tio.c_cc[16]));
      cc.put(Attributes.ControlChar.VERASE, Integer.valueOf(tio.c_cc[2]));
      cc.put(Attributes.ControlChar.VWERASE, Integer.valueOf(tio.c_cc[14]));
      cc.put(Attributes.ControlChar.VKILL, Integer.valueOf(tio.c_cc[3]));
      cc.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(tio.c_cc[12]));
      cc.put(Attributes.ControlChar.VINTR, Integer.valueOf(tio.c_cc[0]));
      cc.put(Attributes.ControlChar.VQUIT, Integer.valueOf(tio.c_cc[1]));
      cc.put(Attributes.ControlChar.VSUSP, Integer.valueOf(tio.c_cc[10]));
      cc.put(Attributes.ControlChar.VSTART, Integer.valueOf(tio.c_cc[8]));
      cc.put(Attributes.ControlChar.VSTOP, Integer.valueOf(tio.c_cc[9]));
      cc.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(tio.c_cc[15]));
      cc.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(tio.c_cc[13]));
      cc.put(Attributes.ControlChar.VMIN, Integer.valueOf(tio.c_cc[6]));
      cc.put(Attributes.ControlChar.VTIME, Integer.valueOf(tio.c_cc[5]));
      return attr;
   }

   private static long setFlag(boolean flag, long value, long org) {
      return flag ? org | value : org;
   }

   private static <T extends Enum<T>> void addFlag(long value, EnumSet<T> flags, T flag, int v) {
      if ((value & v) != 0L) {
         flags.add(flag);
      }
   }
}
