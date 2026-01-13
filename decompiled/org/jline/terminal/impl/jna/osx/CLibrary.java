package org.jline.terminal.impl.jna.osx;

import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;

public interface CLibrary extends Library {
   long TIOCGWINSZ = 1074295912L;
   long TIOCSWINSZ = 2148037735L;
   int TCSANOW = 0;
   int VEOF = 0;
   int VEOL = 1;
   int VEOL2 = 2;
   int VERASE = 3;
   int VWERASE = 4;
   int VKILL = 5;
   int VREPRINT = 6;
   int VINTR = 8;
   int VQUIT = 9;
   int VSUSP = 10;
   int VDSUSP = 11;
   int VSTART = 12;
   int VSTOP = 13;
   int VLNEXT = 14;
   int VDISCARD = 15;
   int VMIN = 16;
   int VTIME = 17;
   int VSTATUS = 18;
   int IGNBRK = 1;
   int BRKINT = 2;
   int IGNPAR = 4;
   int PARMRK = 8;
   int INPCK = 16;
   int ISTRIP = 32;
   int INLCR = 64;
   int IGNCR = 128;
   int ICRNL = 256;
   int IXON = 512;
   int IXOFF = 1024;
   int IXANY = 2048;
   int IMAXBEL = 8192;
   int IUTF8 = 16384;
   int OPOST = 1;
   int ONLCR = 2;
   int OXTABS = 4;
   int ONOEOT = 8;
   int OCRNL = 16;
   int ONOCR = 32;
   int ONLRET = 64;
   int OFILL = 128;
   int NLDLY = 768;
   int TABDLY = 3076;
   int CRDLY = 12288;
   int FFDLY = 16384;
   int BSDLY = 32768;
   int VTDLY = 65536;
   int OFDEL = 131072;
   int CIGNORE = 1;
   int CS5 = 0;
   int CS6 = 256;
   int CS7 = 512;
   int CS8 = 768;
   int CSTOPB = 1024;
   int CREAD = 2048;
   int PARENB = 4096;
   int PARODD = 8192;
   int HUPCL = 16384;
   int CLOCAL = 32768;
   int CCTS_OFLOW = 65536;
   int CRTS_IFLOW = 131072;
   int CDTR_IFLOW = 262144;
   int CDSR_OFLOW = 524288;
   int CCAR_OFLOW = 1048576;
   int ECHOKE = 1;
   int ECHOE = 2;
   int ECHOK = 4;
   int ECHO = 8;
   int ECHONL = 16;
   int ECHOPRT = 32;
   int ECHOCTL = 64;
   int ISIG = 128;
   int ICANON = 256;
   int ALTWERASE = 512;
   int IEXTEN = 1024;
   int EXTPROC = 2048;
   int TOSTOP = 4194304;
   int FLUSHO = 8388608;
   int NOKERNINFO = 33554432;
   int PENDIN = 536870912;
   int NOFLSH = Integer.MIN_VALUE;

   void tcgetattr(int var1, CLibrary.termios var2) throws LastErrorException;

   void tcsetattr(int var1, int var2, CLibrary.termios var3) throws LastErrorException;

   void ioctl(int var1, NativeLong var2, CLibrary.winsize var3) throws LastErrorException;

   int isatty(int var1);

   void ttyname_r(int var1, byte[] var2, int var3) throws LastErrorException;

   void openpty(int[] var1, int[] var2, byte[] var3, CLibrary.termios var4, CLibrary.winsize var5) throws LastErrorException;

   public static class termios extends Structure {
      public NativeLong c_iflag;
      public NativeLong c_oflag;
      public NativeLong c_cflag;
      public NativeLong c_lflag;
      public byte[] c_cc = new byte[20];
      public NativeLong c_ispeed;
      public NativeLong c_ospeed;

      protected List<String> getFieldOrder() {
         return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_cc", "c_ispeed", "c_ospeed");
      }

      public termios() {
      }

      public termios(Attributes t) {
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IGNBRK), 1L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.BRKINT), 2L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IGNPAR), 4L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.PARMRK), 8L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.INPCK), 16L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.ISTRIP), 32L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.INLCR), 64L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IGNCR), 128L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.ICRNL), 256L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IXON), 512L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IXOFF), 1024L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IXANY), 2048L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IMAXBEL), 8192L, this.c_iflag);
         this.setFlag(t.getInputFlag(Attributes.InputFlag.IUTF8), 16384L, this.c_iflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OPOST), 1L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLCR), 2L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OXTABS), 4L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOEOT), 8L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OCRNL), 16L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOCR), 32L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLRET), 64L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OFILL), 128L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.NLDLY), 768L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.TABDLY), 3076L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.CRDLY), 12288L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.FFDLY), 16384L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.BSDLY), 32768L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.VTDLY), 65536L, this.c_oflag);
         this.setFlag(t.getOutputFlag(Attributes.OutputFlag.OFDEL), 131072L, this.c_oflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CIGNORE), 1L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS5), 0L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS6), 256L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS7), 512L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CS8), 768L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CSTOPB), 1024L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CREAD), 2048L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.PARENB), 4096L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.PARODD), 8192L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.HUPCL), 16384L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CLOCAL), 32768L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CCTS_OFLOW), 65536L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CRTS_IFLOW), 131072L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CDTR_IFLOW), 262144L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CDSR_OFLOW), 524288L, this.c_cflag);
         this.setFlag(t.getControlFlag(Attributes.ControlFlag.CCAR_OFLOW), 1048576L, this.c_cflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOKE), 1L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOE), 2L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOK), 4L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHO), 8L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHONL), 16L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOPRT), 32L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOCTL), 64L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ISIG), 128L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ICANON), 256L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.ALTWERASE), 512L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.IEXTEN), 1024L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.EXTPROC), 2048L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.TOSTOP), 4194304L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.FLUSHO), 8388608L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.NOKERNINFO), 33554432L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.PENDIN), 536870912L, this.c_lflag);
         this.setFlag(t.getLocalFlag(Attributes.LocalFlag.NOFLSH), -2147483648L, this.c_lflag);
         this.c_cc[0] = (byte)t.getControlChar(Attributes.ControlChar.VEOF);
         this.c_cc[1] = (byte)t.getControlChar(Attributes.ControlChar.VEOL);
         this.c_cc[2] = (byte)t.getControlChar(Attributes.ControlChar.VEOL2);
         this.c_cc[3] = (byte)t.getControlChar(Attributes.ControlChar.VERASE);
         this.c_cc[4] = (byte)t.getControlChar(Attributes.ControlChar.VWERASE);
         this.c_cc[5] = (byte)t.getControlChar(Attributes.ControlChar.VKILL);
         this.c_cc[6] = (byte)t.getControlChar(Attributes.ControlChar.VREPRINT);
         this.c_cc[8] = (byte)t.getControlChar(Attributes.ControlChar.VINTR);
         this.c_cc[9] = (byte)t.getControlChar(Attributes.ControlChar.VQUIT);
         this.c_cc[10] = (byte)t.getControlChar(Attributes.ControlChar.VSUSP);
         this.c_cc[11] = (byte)t.getControlChar(Attributes.ControlChar.VDSUSP);
         this.c_cc[12] = (byte)t.getControlChar(Attributes.ControlChar.VSTART);
         this.c_cc[13] = (byte)t.getControlChar(Attributes.ControlChar.VSTOP);
         this.c_cc[14] = (byte)t.getControlChar(Attributes.ControlChar.VLNEXT);
         this.c_cc[15] = (byte)t.getControlChar(Attributes.ControlChar.VDISCARD);
         this.c_cc[16] = (byte)t.getControlChar(Attributes.ControlChar.VMIN);
         this.c_cc[17] = (byte)t.getControlChar(Attributes.ControlChar.VTIME);
         this.c_cc[18] = (byte)t.getControlChar(Attributes.ControlChar.VSTATUS);
      }

      private void setFlag(boolean flag, long value, NativeLong org) {
         org.setValue(flag ? org.longValue() | value : org.longValue());
      }

      public Attributes toAttributes() {
         Attributes attr = new Attributes();
         EnumSet<Attributes.InputFlag> iflag = attr.getInputFlags();
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IGNBRK, 1);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IGNBRK, 1);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.BRKINT, 2);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IGNPAR, 4);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.PARMRK, 8);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.INPCK, 16);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.ISTRIP, 32);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.INLCR, 64);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IGNCR, 128);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.ICRNL, 256);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IXON, 512);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IXOFF, 1024);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IXANY, 2048);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IMAXBEL, 8192);
         this.addFlag(this.c_iflag.longValue(), iflag, Attributes.InputFlag.IUTF8, 16384);
         EnumSet<Attributes.OutputFlag> oflag = attr.getOutputFlags();
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.OPOST, 1);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.ONLCR, 2);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.OXTABS, 4);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.ONOEOT, 8);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.OCRNL, 16);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.ONOCR, 32);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.ONLRET, 64);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.OFILL, 128);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.NLDLY, 768);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.TABDLY, 3076);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.CRDLY, 12288);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.FFDLY, 16384);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.BSDLY, 32768);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.VTDLY, 65536);
         this.addFlag(this.c_oflag.longValue(), oflag, Attributes.OutputFlag.OFDEL, 131072);
         EnumSet<Attributes.ControlFlag> cflag = attr.getControlFlags();
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CIGNORE, 1);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CS5, 0);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CS6, 256);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CS7, 512);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CS8, 768);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CSTOPB, 1024);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CREAD, 2048);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.PARENB, 4096);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.PARODD, 8192);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.HUPCL, 16384);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CLOCAL, 32768);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CCTS_OFLOW, 65536);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CRTS_IFLOW, 131072);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CDSR_OFLOW, 524288);
         this.addFlag(this.c_cflag.longValue(), cflag, Attributes.ControlFlag.CCAR_OFLOW, 1048576);
         EnumSet<Attributes.LocalFlag> lflag = attr.getLocalFlags();
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHOKE, 1);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHOE, 2);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHOK, 4);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHO, 8);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHONL, 16);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHOPRT, 32);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ECHOCTL, 64);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ISIG, 128);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ICANON, 256);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.ALTWERASE, 512);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.IEXTEN, 1024);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.EXTPROC, 2048);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.TOSTOP, 4194304);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.FLUSHO, 8388608);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.NOKERNINFO, 33554432);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.PENDIN, 536870912);
         this.addFlag(this.c_lflag.longValue(), lflag, Attributes.LocalFlag.NOFLSH, Integer.MIN_VALUE);
         EnumMap<Attributes.ControlChar, Integer> cc = attr.getControlChars();
         cc.put(Attributes.ControlChar.VEOF, Integer.valueOf(this.c_cc[0]));
         cc.put(Attributes.ControlChar.VEOL, Integer.valueOf(this.c_cc[1]));
         cc.put(Attributes.ControlChar.VEOL2, Integer.valueOf(this.c_cc[2]));
         cc.put(Attributes.ControlChar.VERASE, Integer.valueOf(this.c_cc[3]));
         cc.put(Attributes.ControlChar.VWERASE, Integer.valueOf(this.c_cc[4]));
         cc.put(Attributes.ControlChar.VKILL, Integer.valueOf(this.c_cc[5]));
         cc.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(this.c_cc[6]));
         cc.put(Attributes.ControlChar.VINTR, Integer.valueOf(this.c_cc[8]));
         cc.put(Attributes.ControlChar.VQUIT, Integer.valueOf(this.c_cc[9]));
         cc.put(Attributes.ControlChar.VSUSP, Integer.valueOf(this.c_cc[10]));
         cc.put(Attributes.ControlChar.VDSUSP, Integer.valueOf(this.c_cc[11]));
         cc.put(Attributes.ControlChar.VSTART, Integer.valueOf(this.c_cc[12]));
         cc.put(Attributes.ControlChar.VSTOP, Integer.valueOf(this.c_cc[13]));
         cc.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(this.c_cc[14]));
         cc.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(this.c_cc[15]));
         cc.put(Attributes.ControlChar.VMIN, Integer.valueOf(this.c_cc[16]));
         cc.put(Attributes.ControlChar.VTIME, Integer.valueOf(this.c_cc[17]));
         cc.put(Attributes.ControlChar.VSTATUS, Integer.valueOf(this.c_cc[18]));
         return attr;
      }

      private <T extends Enum<T>> void addFlag(long value, EnumSet<T> flags, T flag, int v) {
         if ((value & v) != 0L) {
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
