package org.jline.terminal.impl.jna.linux;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;

public interface CLibrary extends Library {
   int TIOCGWINSZ = !Platform.isMIPS() && !Platform.isPPC() && !Platform.isSPARC() ? 21523 : 1074295912;
   int TIOCSWINSZ = !Platform.isMIPS() && !Platform.isPPC() && !Platform.isSPARC() ? 21524 : -2146929561;
   int VINTR = 0;
   int VQUIT = 1;
   int VERASE = 2;
   int VKILL = 3;
   int VEOF = 4;
   int VTIME = 5;
   int VMIN = 6;
   int VSWTC = 7;
   int VSTART = 8;
   int VSTOP = 9;
   int VSUSP = 10;
   int VEOL = 11;
   int VREPRINT = 12;
   int VDISCARD = 13;
   int VWERASE = 14;
   int VLNEXT = 15;
   int VEOL2 = 16;
   int IGNBRK = 1;
   int BRKINT = 2;
   int IGNPAR = 4;
   int PARMRK = 8;
   int INPCK = 16;
   int ISTRIP = 32;
   int INLCR = 64;
   int IGNCR = 128;
   int ICRNL = 256;
   int IUCLC = 512;
   int IXON = 1024;
   int IXANY = 2048;
   int IXOFF = 4096;
   int IMAXBEL = 8192;
   int IUTF8 = 16384;
   int OPOST = 1;
   int OLCUC = 2;
   int ONLCR = 4;
   int OCRNL = 8;
   int ONOCR = 16;
   int ONLRET = 32;
   int OFILL = 64;
   int OFDEL = 128;
   int NLDLY = 256;
   int NL0 = 0;
   int NL1 = 256;
   int CRDLY = 1536;
   int CR0 = 0;
   int CR1 = 512;
   int CR2 = 1024;
   int CR3 = 1536;
   int TABDLY = 6144;
   int TAB0 = 0;
   int TAB1 = 2048;
   int TAB2 = 4096;
   int TAB3 = 6144;
   int XTABS = 6144;
   int BSDLY = 8192;
   int BS0 = 0;
   int BS1 = 8192;
   int VTDLY = 16384;
   int VT0 = 0;
   int VT1 = 16384;
   int FFDLY = 32768;
   int FF0 = 0;
   int FF1 = 32768;
   int CBAUD = 4111;
   int B0 = 0;
   int B50 = 1;
   int B75 = 2;
   int B110 = 3;
   int B134 = 4;
   int B150 = 5;
   int B200 = 6;
   int B300 = 7;
   int B600 = 8;
   int B1200 = 9;
   int B1800 = 10;
   int B2400 = 11;
   int B4800 = 12;
   int B9600 = 13;
   int B19200 = 14;
   int B38400 = 15;
   int EXTA = 14;
   int EXTB = 15;
   int CSIZE = 48;
   int CS5 = 0;
   int CS6 = 16;
   int CS7 = 32;
   int CS8 = 48;
   int CSTOPB = 64;
   int CREAD = 128;
   int PARENB = 256;
   int PARODD = 512;
   int HUPCL = 1024;
   int CLOCAL = 2048;
   int ISIG = 1;
   int ICANON = 2;
   int XCASE = 4;
   int ECHO = 8;
   int ECHOE = 16;
   int ECHOK = 32;
   int ECHONL = 64;
   int NOFLSH = 128;
   int TOSTOP = 256;
   int ECHOCTL = 512;
   int ECHOPRT = 1024;
   int ECHOKE = 2048;
   int FLUSHO = 4096;
   int PENDIN = 8192;
   int IEXTEN = 32768;
   int EXTPROC = 65536;
   int TCSANOW = 0;
   int TCSADRAIN = 1;
   int TCSAFLUSH = 2;

   void tcgetattr(int var1, CLibrary.termios var2) throws LastErrorException;

   void tcsetattr(int var1, int var2, CLibrary.termios var3) throws LastErrorException;

   void ioctl(int var1, int var2, CLibrary.winsize var3) throws LastErrorException;

   int isatty(int var1);

   void ttyname_r(int var1, byte[] var2, int var3) throws LastErrorException;

   public static class termios extends Structure {
      public int c_iflag;
      public int c_oflag;
      public int c_cflag;
      public int c_lflag;
      public byte c_line;
      public byte[] c_cc = new byte[32];
      public int c_ispeed;
      public int c_ospeed;

      protected List<String> getFieldOrder() {
         return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "c_cc", "c_ispeed", "c_ospeed");
      }

      public termios() {
      }

      public termios(Attributes t) {
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IGNBRK), 1, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.BRKINT), 2, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IGNPAR), 4, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.PARMRK), 8, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.INPCK), 16, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.ISTRIP), 32, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.INLCR), 64, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IGNCR), 128, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.ICRNL), 256, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IXON), 1024, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IXOFF), 4096, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IXANY), 2048, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IMAXBEL), 8192, this.c_iflag);
         this.c_iflag = this.setFlag(t.getInputFlag(Attributes.InputFlag.IUTF8), 16384, this.c_iflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OPOST), 1, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLCR), 4, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OCRNL), 8, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOCR), 16, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLRET), 32, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OFILL), 64, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.NLDLY), 256, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.TABDLY), 6144, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.CRDLY), 1536, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.FFDLY), 32768, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.BSDLY), 8192, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.VTDLY), 16384, this.c_oflag);
         this.c_oflag = this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OFDEL), 128, this.c_oflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS5), 0, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS6), 16, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS7), 32, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS8), 48, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CSTOPB), 64, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CREAD), 128, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.PARENB), 256, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.PARODD), 512, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.HUPCL), 1024, this.c_cflag);
         this.c_cflag = this.setFlag(t.getControlFlag(Attributes.ControlFlag.CLOCAL), 2048, this.c_cflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOKE), 2048, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOE), 16, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOK), 32, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHO), 8, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHONL), 64, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOPRT), 1024, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOCTL), 512, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ISIG), 1, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ICANON), 2, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.EXTPROC), 65536, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.TOSTOP), 256, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.FLUSHO), 4096, this.c_lflag);
         this.c_lflag = this.setFlag(t.getLocalFlag(Attributes.LocalFlag.NOFLSH), 128, this.c_lflag);
         this.c_cc[4] = (byte)t.getControlChar(Attributes.ControlChar.VEOF);
         this.c_cc[11] = (byte)t.getControlChar(Attributes.ControlChar.VEOL);
         this.c_cc[16] = (byte)t.getControlChar(Attributes.ControlChar.VEOL2);
         this.c_cc[2] = (byte)t.getControlChar(Attributes.ControlChar.VERASE);
         this.c_cc[14] = (byte)t.getControlChar(Attributes.ControlChar.VWERASE);
         this.c_cc[3] = (byte)t.getControlChar(Attributes.ControlChar.VKILL);
         this.c_cc[12] = (byte)t.getControlChar(Attributes.ControlChar.VREPRINT);
         this.c_cc[0] = (byte)t.getControlChar(Attributes.ControlChar.VINTR);
         this.c_cc[1] = (byte)t.getControlChar(Attributes.ControlChar.VQUIT);
         this.c_cc[10] = (byte)t.getControlChar(Attributes.ControlChar.VSUSP);
         this.c_cc[8] = (byte)t.getControlChar(Attributes.ControlChar.VSTART);
         this.c_cc[9] = (byte)t.getControlChar(Attributes.ControlChar.VSTOP);
         this.c_cc[15] = (byte)t.getControlChar(Attributes.ControlChar.VLNEXT);
         this.c_cc[13] = (byte)t.getControlChar(Attributes.ControlChar.VDISCARD);
         this.c_cc[6] = (byte)t.getControlChar(Attributes.ControlChar.VMIN);
         this.c_cc[5] = (byte)t.getControlChar(Attributes.ControlChar.VTIME);
      }

      private int setFlag(boolean flag, int value, int org) {
         return flag ? org | value : org;
      }

      public Attributes toAttributes() {
         Attributes attr = new Attributes();
         EnumSet<Attributes.InputFlag> iflag = attr.getInputFlags();
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IGNBRK, 1);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IGNBRK, 1);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.BRKINT, 2);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IGNPAR, 4);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.PARMRK, 8);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.INPCK, 16);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.ISTRIP, 32);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.INLCR, 64);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IGNCR, 128);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.ICRNL, 256);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IXON, 1024);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IXOFF, 4096);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IXANY, 2048);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IMAXBEL, 8192);
         this.addFlag(this.c_iflag, iflag, Attributes.InputFlag.IUTF8, 16384);
         EnumSet<Attributes.OutputFlag> oflag = attr.getOutputFlags();
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.OPOST, 1);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.ONLCR, 4);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.OCRNL, 8);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.ONOCR, 16);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.ONLRET, 32);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.OFILL, 64);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.NLDLY, 256);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.TABDLY, 6144);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.CRDLY, 1536);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.FFDLY, 32768);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.BSDLY, 8192);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.VTDLY, 16384);
         this.addFlag(this.c_oflag, oflag, Attributes.OutputFlag.OFDEL, 128);
         EnumSet<Attributes.ControlFlag> cflag = attr.getControlFlags();
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CS5, 0);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CS6, 16);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CS7, 32);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CS8, 48);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CSTOPB, 64);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CREAD, 128);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.PARENB, 256);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.PARODD, 512);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.HUPCL, 1024);
         this.addFlag(this.c_cflag, cflag, Attributes.ControlFlag.CLOCAL, 2048);
         EnumSet<Attributes.LocalFlag> lflag = attr.getLocalFlags();
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHOKE, 2048);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHOE, 16);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHOK, 32);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHO, 8);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHONL, 64);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHOPRT, 1024);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ECHOCTL, 512);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ISIG, 1);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.ICANON, 2);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.EXTPROC, 65536);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.TOSTOP, 256);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.FLUSHO, 4096);
         this.addFlag(this.c_lflag, lflag, Attributes.LocalFlag.NOFLSH, 128);
         EnumMap<Attributes.ControlChar, Integer> cc = attr.getControlChars();
         cc.put(Attributes.ControlChar.VEOF, Integer.valueOf(this.c_cc[4]));
         cc.put(Attributes.ControlChar.VEOL, Integer.valueOf(this.c_cc[11]));
         cc.put(Attributes.ControlChar.VEOL2, Integer.valueOf(this.c_cc[16]));
         cc.put(Attributes.ControlChar.VERASE, Integer.valueOf(this.c_cc[2]));
         cc.put(Attributes.ControlChar.VWERASE, Integer.valueOf(this.c_cc[14]));
         cc.put(Attributes.ControlChar.VKILL, Integer.valueOf(this.c_cc[3]));
         cc.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(this.c_cc[12]));
         cc.put(Attributes.ControlChar.VINTR, Integer.valueOf(this.c_cc[0]));
         cc.put(Attributes.ControlChar.VQUIT, Integer.valueOf(this.c_cc[1]));
         cc.put(Attributes.ControlChar.VSUSP, Integer.valueOf(this.c_cc[10]));
         cc.put(Attributes.ControlChar.VSTART, Integer.valueOf(this.c_cc[8]));
         cc.put(Attributes.ControlChar.VSTOP, Integer.valueOf(this.c_cc[9]));
         cc.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(this.c_cc[15]));
         cc.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(this.c_cc[13]));
         cc.put(Attributes.ControlChar.VMIN, Integer.valueOf(this.c_cc[6]));
         cc.put(Attributes.ControlChar.VTIME, Integer.valueOf(this.c_cc[5]));
         return attr;
      }

      private <T extends Enum<T>> void addFlag(int value, EnumSet<T> flags, T flag, int v) {
         if ((value & v) != 0) {
            flags.add(flag);
         }
      }
   }

   public static class winsize extends Structure {
      public short ws_row;
      public short ws_col;
      public short ws_xpixel;
      public short ws_ypixel;

      public winsize() {
      }

      public winsize(Size ws) {
         this.ws_row = (short)ws.getRows();
         this.ws_col = (short)ws.getColumns();
      }

      public Size toSize() {
         return new Size(this.ws_col, this.ws_row);
      }

      protected List<String> getFieldOrder() {
         return Arrays.asList("ws_row", "ws_col", "ws_xpixel", "ws_ypixel");
      }
   }
}
