package org.jline.terminal.impl.ffm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.Linker.Option;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

class CLibrary {
   private static final Logger logger = Logger.getLogger("org.jline");
   static final MethodHandle ioctl;
   static final MethodHandle isatty;
   static final MethodHandle openpty;
   static final MethodHandle tcsetattr;
   static final MethodHandle tcgetattr;
   static final MethodHandle ttyname_r;
   static LinkageError openptyError;
   private static final int TIOCGWINSZ;
   private static final int TIOCSWINSZ;
   private static final int TCSANOW;
   private static int TCSADRAIN;
   private static int TCSAFLUSH;
   private static final int VEOF;
   private static final int VEOL;
   private static final int VEOL2;
   private static final int VERASE;
   private static final int VWERASE;
   private static final int VKILL;
   private static final int VREPRINT;
   private static final int VERASE2;
   private static final int VINTR;
   private static final int VQUIT;
   private static final int VSUSP;
   private static final int VDSUSP;
   private static final int VSTART;
   private static final int VSTOP;
   private static final int VLNEXT;
   private static final int VDISCARD;
   private static final int VMIN;
   private static final int VSWTC;
   private static final int VTIME;
   private static final int VSTATUS;
   private static final int IGNBRK;
   private static final int BRKINT;
   private static final int IGNPAR;
   private static final int PARMRK;
   private static final int INPCK;
   private static final int ISTRIP;
   private static final int INLCR;
   private static final int IGNCR;
   private static final int ICRNL;
   private static int IUCLC;
   private static final int IXON;
   private static final int IXOFF;
   private static final int IXANY;
   private static final int IMAXBEL;
   private static int IUTF8;
   private static final int OPOST;
   private static int OLCUC;
   private static final int ONLCR;
   private static int OXTABS;
   private static int NLDLY;
   private static int NL0;
   private static int NL1;
   private static final int TABDLY;
   private static int TAB0;
   private static int TAB1;
   private static int TAB2;
   private static int TAB3;
   private static int CRDLY;
   private static int CR0;
   private static int CR1;
   private static int CR2;
   private static int CR3;
   private static int FFDLY;
   private static int FF0;
   private static int FF1;
   private static int XTABS;
   private static int BSDLY;
   private static int BS0;
   private static int BS1;
   private static int VTDLY;
   private static int VT0;
   private static int VT1;
   private static int CBAUD;
   private static int B0;
   private static int B50;
   private static int B75;
   private static int B110;
   private static int B134;
   private static int B150;
   private static int B200;
   private static int B300;
   private static int B600;
   private static int B1200;
   private static int B1800;
   private static int B2400;
   private static int B4800;
   private static int B9600;
   private static int B19200;
   private static int B38400;
   private static int EXTA;
   private static int EXTB;
   private static int OFDEL;
   private static int ONOEOT;
   private static final int OCRNL;
   private static int ONOCR;
   private static final int ONLRET;
   private static int OFILL;
   private static int CIGNORE;
   private static int CSIZE;
   private static final int CS5;
   private static final int CS6;
   private static final int CS7;
   private static final int CS8;
   private static final int CSTOPB;
   private static final int CREAD;
   private static final int PARENB;
   private static final int PARODD;
   private static final int HUPCL;
   private static final int CLOCAL;
   private static int CCTS_OFLOW;
   private static int CRTS_IFLOW;
   private static int CDTR_IFLOW;
   private static int CDSR_OFLOW;
   private static int CCAR_OFLOW;
   private static final int ECHOKE;
   private static final int ECHOE;
   private static final int ECHOK;
   private static final int ECHO;
   private static final int ECHONL;
   private static final int ECHOPRT;
   private static final int ECHOCTL;
   private static final int ISIG;
   private static final int ICANON;
   private static int XCASE;
   private static int ALTWERASE;
   private static final int IEXTEN;
   private static final int EXTPROC;
   private static final int TOSTOP;
   private static final int FLUSHO;
   private static int NOKERNINFO;
   private static final int PENDIN;
   private static final int NOFLSH;

   private static String readFully(InputStream in) throws IOException {
      int readLen = 0;
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      byte[] buf = new byte[32];

      while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
         b.write(buf, 0, readLen);
      }

      return b.toString();
   }

   static Size getTerminalSize(int fd) {
      try {
         CLibrary.winsize ws = new CLibrary.winsize();
         int res = (int)ioctl.invoke((int)fd, (long)TIOCGWINSZ, (MemorySegment)ws.segment());
         return new Size(ws.ws_col(), ws.ws_row());
      } catch (Throwable var3) {
         throw new RuntimeException("Unable to call ioctl(TIOCGWINSZ)", var3);
      }
   }

   static void setTerminalSize(int fd, Size size) {
      try {
         CLibrary.winsize ws = new CLibrary.winsize();
         ws.ws_row((short)size.getRows());
         ws.ws_col((short)size.getColumns());
         int var3 = (int)ioctl.invoke((int)fd, (int)TIOCSWINSZ, (MemorySegment)ws.segment());
      } catch (Throwable var4) {
         throw new RuntimeException("Unable to call ioctl(TIOCSWINSZ)", var4);
      }
   }

   static Attributes getAttributes(int fd) {
      try {
         CLibrary.termios t = new CLibrary.termios();
         int res = (int)tcgetattr.invoke((int)fd, (MemorySegment)t.segment());
         return t.asAttributes();
      } catch (Throwable var3) {
         throw new RuntimeException("Unable to call tcgetattr()", var3);
      }
   }

   static void setAttributes(int fd, Attributes attr) {
      try {
         CLibrary.termios t = new CLibrary.termios(attr);
         int var3 = (int)tcsetattr.invoke((int)fd, (int)TCSANOW, (MemorySegment)t.segment());
      } catch (Throwable var4) {
         throw new RuntimeException("Unable to call tcsetattr()", var4);
      }
   }

   static boolean isTty(int fd) {
      try {
         return (int)isatty.invoke((int)fd) == 1;
      } catch (Throwable var2) {
         throw new RuntimeException("Unable to call isatty()", var2);
      }
   }

   static String ttyName(int fd) {
      try {
         MemorySegment buf = Arena.ofAuto().allocate(64L);
         int res = (int)ttyname_r.invoke((int)fd, (MemorySegment)buf, (long)buf.byteSize());
         byte[] data = buf.toArray(ValueLayout.JAVA_BYTE);
         int len = 0;

         while (data[len] != 0) {
            len++;
         }

         return new String(data, 0, len);
      } catch (Throwable var5) {
         throw new RuntimeException("Unable to call ttyname_r()", var5);
      }
   }

   static Pty openpty(TerminalProvider provider, Attributes attr, Size size) {
      if (openptyError != null) {
         throw openptyError;
      } else {
         try {
            MemorySegment buf = Arena.ofAuto().allocate(64L);
            MemorySegment master = Arena.ofAuto().allocate(ValueLayout.JAVA_INT);
            MemorySegment slave = Arena.ofAuto().allocate(ValueLayout.JAVA_INT);
            int res = (int)openpty.invoke(
               (MemorySegment)master,
               (MemorySegment)slave,
               (MemorySegment)buf,
               (Object)(attr != null ? new CLibrary.termios(attr).segment() : MemorySegment.NULL),
               (Object)(size != null ? new CLibrary.winsize((short)size.getRows(), (short)size.getColumns()).segment() : MemorySegment.NULL)
            );
            byte[] str = buf.toArray(ValueLayout.JAVA_BYTE);
            int len = 0;

            while (str[len] != 0) {
               len++;
            }

            String device = new String(str, 0, len);
            return new FfmNativePty(provider, null, master.get(ValueLayout.JAVA_INT, 0L), slave.get(ValueLayout.JAVA_INT, 0L), device);
         } catch (Throwable var10) {
            throw new RuntimeException("Unable to call openpty()", var10);
         }
      }
   }

   static {
      Linker linker = Linker.nativeLinker();
      SymbolLookup lookup = SymbolLookup.loaderLookup().or(linker.defaultLookup());
      ioctl = linker.downcallHandle(
         lookup.find("ioctl").get(),
         FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
         Option.firstVariadicArg(2)
      );
      isatty = linker.downcallHandle(lookup.find("isatty").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
      tcsetattr = linker.downcallHandle(
         lookup.find("tcsetattr").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
      );
      tcgetattr = linker.downcallHandle(lookup.find("tcgetattr").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
      ttyname_r = linker.downcallHandle(
         lookup.find("ttyname_r").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG)
      );
      LinkageError error = null;
      Optional<MemorySegment> openPtyAddr = lookup.find("openpty");
      if (openPtyAddr.isEmpty()) {
         StringBuilder sb = new StringBuilder();
         sb.append("Unable to find openpty native method in static libraries and unable to load the util library.");
         List<Throwable> suppressed = new ArrayList<>();

         try {
            System.loadLibrary("util");
            openPtyAddr = lookup.find("openpty");
         } catch (Throwable var18) {
            suppressed.add(var18);
         }

         if (openPtyAddr.isEmpty()) {
            String libUtilPath = System.getProperty("org.jline.ffm.libutil");
            if (libUtilPath != null && !libUtilPath.isEmpty()) {
               try {
                  System.load(libUtilPath);
                  openPtyAddr = lookup.find("openpty");
               } catch (Throwable var17) {
                  suppressed.add(var17);
               }
            }
         }

         if (openPtyAddr.isEmpty() && OSUtils.IS_LINUX) {
            try {
               Process p = Runtime.getRuntime().exec(new String[]{"uname", "-m"});
               p.waitFor();

               try (InputStream in = p.getInputStream()) {
                  String hwName = readFully(in).trim();
                  Path libDir = Paths.get("/usr/lib", hwName + "-linux-gnu");

                  try (Stream<Path> stream = Files.list(libDir)) {
                     for (Path lib : stream.filter(l -> l.getFileName().toString().startsWith("libutil.so.")).collect(Collectors.toList())) {
                        try {
                           System.load(lib.toString());
                           openPtyAddr = lookup.find("openpty");
                           if (openPtyAddr.isPresent()) {
                              break;
                           }
                        } catch (Throwable var19) {
                           suppressed.add(var19);
                        }
                     }
                  }
               }
            } catch (Throwable var22) {
               suppressed.add(var22);
            }
         }

         if (openPtyAddr.isEmpty()) {
            for (Throwable t : suppressed) {
               sb.append("\n\t- ").append(t.toString());
            }

            error = new LinkageError(sb.toString());
            suppressed.forEach(error::addSuppressed);
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.WARNING, error.getMessage(), (Throwable)error);
            } else {
               logger.log(Level.WARNING, error.getMessage());
            }
         }
      }

      if (openPtyAddr.isPresent()) {
         openpty = linker.downcallHandle(
            openPtyAddr.get(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS)
         );
         openptyError = null;
      } else {
         openpty = null;
         openptyError = error;
      }

      String osName = System.getProperty("os.name");
      if (osName.startsWith("Linux")) {
         String arch = System.getProperty("os.arch");
         boolean isMipsPpcOrSparc = arch.equals("mips")
            || arch.equals("mips64")
            || arch.equals("mipsel")
            || arch.equals("mips64el")
            || arch.startsWith("ppc")
            || arch.startsWith("sparc");
         TIOCGWINSZ = isMipsPpcOrSparc ? 1074295912 : 21523;
         TIOCSWINSZ = isMipsPpcOrSparc ? -2146929561 : 21524;
         TCSANOW = 0;
         TCSADRAIN = 1;
         TCSAFLUSH = 2;
         VINTR = 0;
         VQUIT = 1;
         VERASE = 2;
         VKILL = 3;
         VEOF = 4;
         VTIME = 5;
         VMIN = 6;
         VSWTC = 7;
         VSTART = 8;
         VSTOP = 9;
         VSUSP = 10;
         VEOL = 11;
         VREPRINT = 12;
         VDISCARD = 13;
         VWERASE = 14;
         VLNEXT = 15;
         VEOL2 = 16;
         VERASE2 = -1;
         VDSUSP = -1;
         VSTATUS = -1;
         IGNBRK = 1;
         BRKINT = 2;
         IGNPAR = 4;
         PARMRK = 8;
         INPCK = 16;
         ISTRIP = 32;
         INLCR = 64;
         IGNCR = 128;
         ICRNL = 256;
         IUCLC = 512;
         IXON = 1024;
         IXANY = 2048;
         IXOFF = 4096;
         IMAXBEL = 8192;
         IUTF8 = 16384;
         OPOST = 1;
         OLCUC = 2;
         ONLCR = 4;
         OCRNL = 8;
         ONOCR = 16;
         ONLRET = 32;
         OFILL = 64;
         OFDEL = 128;
         NLDLY = 256;
         NL0 = 0;
         NL1 = 256;
         CRDLY = 1536;
         CR0 = 0;
         CR1 = 512;
         CR2 = 1024;
         CR3 = 1536;
         TABDLY = 6144;
         TAB0 = 0;
         TAB1 = 2048;
         TAB2 = 4096;
         TAB3 = 6144;
         XTABS = 6144;
         BSDLY = 8192;
         BS0 = 0;
         BS1 = 8192;
         VTDLY = 16384;
         VT0 = 0;
         VT1 = 16384;
         FFDLY = 32768;
         FF0 = 0;
         FF1 = 32768;
         CBAUD = 4111;
         B0 = 0;
         B50 = 1;
         B75 = 2;
         B110 = 3;
         B134 = 4;
         B150 = 5;
         B200 = 6;
         B300 = 7;
         B600 = 8;
         B1200 = 9;
         B1800 = 10;
         B2400 = 11;
         B4800 = 12;
         B9600 = 13;
         B19200 = 14;
         B38400 = 15;
         EXTA = B19200;
         EXTB = B38400;
         CSIZE = 48;
         CS5 = 0;
         CS6 = 16;
         CS7 = 32;
         CS8 = 48;
         CSTOPB = 64;
         CREAD = 128;
         PARENB = 256;
         PARODD = 512;
         HUPCL = 1024;
         CLOCAL = 2048;
         ISIG = 1;
         ICANON = 2;
         XCASE = 4;
         ECHO = 8;
         ECHOE = 16;
         ECHOK = 32;
         ECHONL = 64;
         NOFLSH = 128;
         TOSTOP = 256;
         ECHOCTL = 512;
         ECHOPRT = 1024;
         ECHOKE = 2048;
         FLUSHO = 4096;
         PENDIN = 8192;
         IEXTEN = 32768;
         EXTPROC = 65536;
      } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
         int _TIOC = 21504;
         TIOCGWINSZ = _TIOC | 104;
         TIOCSWINSZ = _TIOC | 103;
         TCSANOW = 0;
         TCSADRAIN = 1;
         TCSAFLUSH = 2;
         VINTR = 0;
         VQUIT = 1;
         VERASE = 2;
         VKILL = 3;
         VEOF = 4;
         VTIME = 5;
         VMIN = 6;
         VSWTC = 7;
         VSTART = 8;
         VSTOP = 9;
         VSUSP = 10;
         VEOL = 11;
         VREPRINT = 12;
         VDISCARD = 13;
         VWERASE = 14;
         VLNEXT = 15;
         VEOL2 = 16;
         VERASE2 = -1;
         VDSUSP = -1;
         VSTATUS = -1;
         IGNBRK = 1;
         BRKINT = 2;
         IGNPAR = 4;
         PARMRK = 16;
         INPCK = 32;
         ISTRIP = 64;
         INLCR = 256;
         IGNCR = 512;
         ICRNL = 1024;
         IUCLC = 4096;
         IXON = 8192;
         IXANY = 16384;
         IXOFF = 65536;
         IMAXBEL = 131072;
         IUTF8 = 262144;
         OPOST = 1;
         OLCUC = 2;
         ONLCR = 4;
         OCRNL = 16;
         ONOCR = 32;
         ONLRET = 64;
         OFILL = 256;
         OFDEL = 512;
         NLDLY = 1024;
         NL0 = 0;
         NL1 = 1024;
         CRDLY = 12288;
         CR0 = 0;
         CR1 = 4096;
         CR2 = 8192;
         CR3 = 12288;
         TABDLY = 81920;
         TAB0 = 0;
         TAB1 = 16384;
         TAB2 = 65536;
         TAB3 = 81920;
         XTABS = 81920;
         BSDLY = 131072;
         BS0 = 0;
         BS1 = 131072;
         VTDLY = 262144;
         VT0 = 0;
         VT1 = 262144;
         FFDLY = 1048576;
         FF0 = 0;
         FF1 = 1048576;
         CBAUD = 65559;
         B0 = 0;
         B50 = 1;
         B75 = 2;
         B110 = 3;
         B134 = 4;
         B150 = 5;
         B200 = 6;
         B300 = 7;
         B600 = 16;
         B1200 = 17;
         B1800 = 18;
         B2400 = 19;
         B4800 = 20;
         B9600 = 21;
         B19200 = 22;
         B38400 = 23;
         EXTA = 11637248;
         EXTB = 11764736;
         CSIZE = 96;
         CS5 = 0;
         CS6 = 32;
         CS7 = 64;
         CS8 = 96;
         CSTOPB = 256;
         CREAD = 512;
         PARENB = 1024;
         PARODD = 4096;
         HUPCL = 8192;
         CLOCAL = 16384;
         ISIG = 1;
         ICANON = 2;
         XCASE = 4;
         ECHO = 16;
         ECHOE = 32;
         ECHOK = 64;
         ECHONL = 256;
         NOFLSH = 512;
         TOSTOP = 1024;
         ECHOCTL = 4096;
         ECHOPRT = 8192;
         ECHOKE = 16384;
         FLUSHO = 65536;
         PENDIN = 262144;
         IEXTEN = 1048576;
         EXTPROC = 2097152;
      } else if (!osName.startsWith("Mac") && !osName.startsWith("Darwin")) {
         if (!osName.startsWith("FreeBSD")) {
            throw new UnsupportedOperationException();
         }

         TIOCGWINSZ = 1074295912;
         TIOCSWINSZ = -2146929561;
         TCSANOW = 0;
         TCSADRAIN = 1;
         TCSAFLUSH = 2;
         VEOF = 0;
         VEOL = 1;
         VEOL2 = 2;
         VERASE = 3;
         VWERASE = 4;
         VKILL = 5;
         VREPRINT = 6;
         VERASE2 = 7;
         VINTR = 8;
         VQUIT = 9;
         VSUSP = 10;
         VDSUSP = 11;
         VSTART = 12;
         VSTOP = 13;
         VLNEXT = 14;
         VDISCARD = 15;
         VMIN = 16;
         VTIME = 17;
         VSTATUS = 18;
         VSWTC = -1;
         IGNBRK = 1;
         BRKINT = 2;
         IGNPAR = 4;
         PARMRK = 8;
         INPCK = 16;
         ISTRIP = 32;
         INLCR = 64;
         IGNCR = 128;
         ICRNL = 256;
         IXON = 512;
         IXOFF = 1024;
         IXANY = 2048;
         IMAXBEL = 8192;
         OPOST = 1;
         ONLCR = 2;
         TABDLY = 4;
         TAB0 = 0;
         TAB3 = 4;
         ONOEOT = 8;
         OCRNL = 16;
         ONLRET = 64;
         CIGNORE = 1;
         CSIZE = 768;
         CS5 = 0;
         CS6 = 256;
         CS7 = 512;
         CS8 = 768;
         CSTOPB = 1024;
         CREAD = 2048;
         PARENB = 4096;
         PARODD = 8192;
         HUPCL = 16384;
         CLOCAL = 32768;
         ECHOKE = 1;
         ECHOE = 2;
         ECHOK = 4;
         ECHO = 8;
         ECHONL = 16;
         ECHOPRT = 32;
         ECHOCTL = 64;
         ISIG = 128;
         ICANON = 256;
         ALTWERASE = 512;
         IEXTEN = 1024;
         EXTPROC = 2048;
         TOSTOP = 4194304;
         FLUSHO = 8388608;
         PENDIN = 33554432;
         NOFLSH = 134217728;
      } else {
         TIOCGWINSZ = 1074295912;
         TIOCSWINSZ = -2146929561;
         TCSANOW = 0;
         VEOF = 0;
         VEOL = 1;
         VEOL2 = 2;
         VERASE = 3;
         VWERASE = 4;
         VKILL = 5;
         VREPRINT = 6;
         VINTR = 8;
         VQUIT = 9;
         VSUSP = 10;
         VDSUSP = 11;
         VSTART = 12;
         VSTOP = 13;
         VLNEXT = 14;
         VDISCARD = 15;
         VMIN = 16;
         VTIME = 17;
         VSTATUS = 18;
         VERASE2 = -1;
         VSWTC = -1;
         IGNBRK = 1;
         BRKINT = 2;
         IGNPAR = 4;
         PARMRK = 8;
         INPCK = 16;
         ISTRIP = 32;
         INLCR = 64;
         IGNCR = 128;
         ICRNL = 256;
         IXON = 512;
         IXOFF = 1024;
         IXANY = 2048;
         IMAXBEL = 8192;
         IUTF8 = 16384;
         OPOST = 1;
         ONLCR = 2;
         OXTABS = 4;
         ONOEOT = 8;
         OCRNL = 16;
         ONOCR = 32;
         ONLRET = 64;
         OFILL = 128;
         NLDLY = 768;
         TABDLY = 3076;
         CRDLY = 12288;
         FFDLY = 16384;
         BSDLY = 32768;
         VTDLY = 65536;
         OFDEL = 131072;
         CIGNORE = 1;
         CS5 = 0;
         CS6 = 256;
         CS7 = 512;
         CS8 = 768;
         CSTOPB = 1024;
         CREAD = 2048;
         PARENB = 4096;
         PARODD = 8192;
         HUPCL = 16384;
         CLOCAL = 32768;
         CCTS_OFLOW = 65536;
         CRTS_IFLOW = 131072;
         CDTR_IFLOW = 262144;
         CDSR_OFLOW = 524288;
         CCAR_OFLOW = 1048576;
         ECHOKE = 1;
         ECHOE = 2;
         ECHOK = 4;
         ECHO = 8;
         ECHONL = 16;
         ECHOPRT = 32;
         ECHOCTL = 64;
         ISIG = 128;
         ICANON = 256;
         ALTWERASE = 512;
         IEXTEN = 1024;
         EXTPROC = 2048;
         TOSTOP = 4194304;
         FLUSHO = 8388608;
         NOKERNINFO = 33554432;
         PENDIN = 536870912;
         NOFLSH = Integer.MIN_VALUE;
      }
   }

   static class termios {
      static final GroupLayout LAYOUT;
      private static final VarHandle c_iflag;
      private static final VarHandle c_oflag;
      private static final VarHandle c_cflag;
      private static final VarHandle c_lflag;
      private static final long c_cc_offset;
      private static final VarHandle c_ispeed;
      private static final VarHandle c_ospeed;
      private final MemorySegment seg;

      private static VarHandle adjust2LinuxHandle(VarHandle v) {
         if (OSUtils.IS_LINUX) {
            MethodHandle id = MethodHandles.identity(int.class);
            v = MethodHandles.filterValue(
               v,
               MethodHandles.explicitCastArguments(id, MethodType.methodType(int.class, long.class)),
               MethodHandles.explicitCastArguments(id, MethodType.methodType(long.class, int.class))
            );
         }

         return v;
      }

      termios() {
         this.seg = Arena.ofAuto().allocate(LAYOUT);
      }

      termios(Attributes t) {
         this();
         long c_iflag = 0L;
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNBRK), CLibrary.IGNBRK, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.BRKINT), CLibrary.BRKINT, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNPAR), CLibrary.IGNPAR, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.PARMRK), CLibrary.PARMRK, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.INPCK), CLibrary.INPCK, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.ISTRIP), CLibrary.ISTRIP, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.INLCR), CLibrary.INLCR, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IGNCR), CLibrary.IGNCR, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.ICRNL), CLibrary.ICRNL, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXON), CLibrary.IXON, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXOFF), CLibrary.IXOFF, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IXANY), CLibrary.IXANY, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IMAXBEL), CLibrary.IMAXBEL, c_iflag);
         c_iflag = setFlag(t.getInputFlag(Attributes.InputFlag.IUTF8), CLibrary.IUTF8, c_iflag);
         this.c_iflag(c_iflag);
         long c_oflag = 0L;
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OPOST), CLibrary.OPOST, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLCR), CLibrary.ONLCR, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OXTABS), CLibrary.OXTABS, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOEOT), CLibrary.ONOEOT, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OCRNL), CLibrary.OCRNL, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOCR), CLibrary.ONOCR, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLRET), CLibrary.ONLRET, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OFILL), CLibrary.OFILL, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.NLDLY), CLibrary.NLDLY, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.TABDLY), CLibrary.TABDLY, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.CRDLY), CLibrary.CRDLY, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.FFDLY), CLibrary.FFDLY, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.BSDLY), CLibrary.BSDLY, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.VTDLY), CLibrary.VTDLY, c_oflag);
         c_oflag = setFlag(t.getOutputFlag(Attributes.OutputFlag.OFDEL), CLibrary.OFDEL, c_oflag);
         this.c_oflag(c_oflag);
         long c_cflag = 0L;
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CIGNORE), CLibrary.CIGNORE, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS5), CLibrary.CS5, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS6), CLibrary.CS6, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS7), CLibrary.CS7, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CS8), CLibrary.CS8, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CSTOPB), CLibrary.CSTOPB, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CREAD), CLibrary.CREAD, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.PARENB), CLibrary.PARENB, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.PARODD), CLibrary.PARODD, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.HUPCL), CLibrary.HUPCL, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CLOCAL), CLibrary.CLOCAL, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CCTS_OFLOW), CLibrary.CCTS_OFLOW, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CRTS_IFLOW), CLibrary.CRTS_IFLOW, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CDTR_IFLOW), CLibrary.CDTR_IFLOW, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CDSR_OFLOW), CLibrary.CDSR_OFLOW, c_cflag);
         c_cflag = setFlag(t.getControlFlag(Attributes.ControlFlag.CCAR_OFLOW), CLibrary.CCAR_OFLOW, c_cflag);
         this.c_cflag(c_cflag);
         long c_lflag = 0L;
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOKE), CLibrary.ECHOKE, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOE), CLibrary.ECHOE, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOK), CLibrary.ECHOK, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHO), CLibrary.ECHO, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHONL), CLibrary.ECHONL, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOPRT), CLibrary.ECHOPRT, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOCTL), CLibrary.ECHOCTL, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ISIG), CLibrary.ISIG, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ICANON), CLibrary.ICANON, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.ALTWERASE), CLibrary.ALTWERASE, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.IEXTEN), CLibrary.IEXTEN, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.EXTPROC), CLibrary.EXTPROC, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.TOSTOP), CLibrary.TOSTOP, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.FLUSHO), CLibrary.FLUSHO, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.NOKERNINFO), CLibrary.NOKERNINFO, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.PENDIN), CLibrary.PENDIN, c_lflag);
         c_lflag = setFlag(t.getLocalFlag(Attributes.LocalFlag.NOFLSH), CLibrary.NOFLSH, c_lflag);
         this.c_lflag(c_lflag);
         byte[] c_cc = new byte[20];
         c_cc[CLibrary.VEOF] = (byte)t.getControlChar(Attributes.ControlChar.VEOF);
         c_cc[CLibrary.VEOL] = (byte)t.getControlChar(Attributes.ControlChar.VEOL);
         c_cc[CLibrary.VEOL2] = (byte)t.getControlChar(Attributes.ControlChar.VEOL2);
         c_cc[CLibrary.VERASE] = (byte)t.getControlChar(Attributes.ControlChar.VERASE);
         c_cc[CLibrary.VWERASE] = (byte)t.getControlChar(Attributes.ControlChar.VWERASE);
         c_cc[CLibrary.VKILL] = (byte)t.getControlChar(Attributes.ControlChar.VKILL);
         c_cc[CLibrary.VREPRINT] = (byte)t.getControlChar(Attributes.ControlChar.VREPRINT);
         c_cc[CLibrary.VINTR] = (byte)t.getControlChar(Attributes.ControlChar.VINTR);
         c_cc[CLibrary.VQUIT] = (byte)t.getControlChar(Attributes.ControlChar.VQUIT);
         c_cc[CLibrary.VSUSP] = (byte)t.getControlChar(Attributes.ControlChar.VSUSP);
         if (CLibrary.VDSUSP != -1) {
            c_cc[CLibrary.VDSUSP] = (byte)t.getControlChar(Attributes.ControlChar.VDSUSP);
         }

         c_cc[CLibrary.VSTART] = (byte)t.getControlChar(Attributes.ControlChar.VSTART);
         c_cc[CLibrary.VSTOP] = (byte)t.getControlChar(Attributes.ControlChar.VSTOP);
         c_cc[CLibrary.VLNEXT] = (byte)t.getControlChar(Attributes.ControlChar.VLNEXT);
         c_cc[CLibrary.VDISCARD] = (byte)t.getControlChar(Attributes.ControlChar.VDISCARD);
         c_cc[CLibrary.VMIN] = (byte)t.getControlChar(Attributes.ControlChar.VMIN);
         c_cc[CLibrary.VTIME] = (byte)t.getControlChar(Attributes.ControlChar.VTIME);
         if (CLibrary.VSTATUS != -1) {
            c_cc[CLibrary.VSTATUS] = (byte)t.getControlChar(Attributes.ControlChar.VSTATUS);
         }

         this.c_cc().copyFrom(MemorySegment.ofArray(c_cc));
      }

      MemorySegment segment() {
         return this.seg;
      }

      long c_iflag() {
         return (long)c_iflag.get((MemorySegment)this.seg);
      }

      void c_iflag(long f) {
         c_iflag.set((MemorySegment)this.seg, (long)f);
      }

      long c_oflag() {
         return (long)c_oflag.get((MemorySegment)this.seg);
      }

      void c_oflag(long f) {
         c_oflag.set((MemorySegment)this.seg, (long)f);
      }

      long c_cflag() {
         return (long)c_cflag.get((MemorySegment)this.seg);
      }

      void c_cflag(long f) {
         c_cflag.set((MemorySegment)this.seg, (long)f);
      }

      long c_lflag() {
         return (long)c_lflag.get((MemorySegment)this.seg);
      }

      void c_lflag(long f) {
         c_lflag.set((MemorySegment)this.seg, (long)f);
      }

      MemorySegment c_cc() {
         return this.seg.asSlice(c_cc_offset, 20L);
      }

      long c_ispeed() {
         return (long)c_ispeed.get((MemorySegment)this.seg);
      }

      void c_ispeed(long f) {
         c_ispeed.set((MemorySegment)this.seg, (long)f);
      }

      long c_ospeed() {
         return (long)c_ospeed.get((MemorySegment)this.seg);
      }

      void c_ospeed(long f) {
         c_ospeed.set((MemorySegment)this.seg, (long)f);
      }

      private static long setFlag(boolean flag, long value, long org) {
         return flag ? org | value : org;
      }

      private static <T extends Enum<T>> void addFlag(long value, EnumSet<T> flags, T flag, int v) {
         if ((value & v) != 0L) {
            flags.add(flag);
         }
      }

      public Attributes asAttributes() {
         Attributes attr = new Attributes();
         long c_iflag = this.c_iflag();
         EnumSet<Attributes.InputFlag> iflag = attr.getInputFlags();
         addFlag(c_iflag, iflag, Attributes.InputFlag.IGNBRK, CLibrary.IGNBRK);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IGNBRK, CLibrary.IGNBRK);
         addFlag(c_iflag, iflag, Attributes.InputFlag.BRKINT, CLibrary.BRKINT);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IGNPAR, CLibrary.IGNPAR);
         addFlag(c_iflag, iflag, Attributes.InputFlag.PARMRK, CLibrary.PARMRK);
         addFlag(c_iflag, iflag, Attributes.InputFlag.INPCK, CLibrary.INPCK);
         addFlag(c_iflag, iflag, Attributes.InputFlag.ISTRIP, CLibrary.ISTRIP);
         addFlag(c_iflag, iflag, Attributes.InputFlag.INLCR, CLibrary.INLCR);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IGNCR, CLibrary.IGNCR);
         addFlag(c_iflag, iflag, Attributes.InputFlag.ICRNL, CLibrary.ICRNL);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IXON, CLibrary.IXON);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IXOFF, CLibrary.IXOFF);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IXANY, CLibrary.IXANY);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IMAXBEL, CLibrary.IMAXBEL);
         addFlag(c_iflag, iflag, Attributes.InputFlag.IUTF8, CLibrary.IUTF8);
         long c_oflag = this.c_oflag();
         EnumSet<Attributes.OutputFlag> oflag = attr.getOutputFlags();
         addFlag(c_oflag, oflag, Attributes.OutputFlag.OPOST, CLibrary.OPOST);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.ONLCR, CLibrary.ONLCR);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.OXTABS, CLibrary.OXTABS);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.ONOEOT, CLibrary.ONOEOT);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.OCRNL, CLibrary.OCRNL);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.ONOCR, CLibrary.ONOCR);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.ONLRET, CLibrary.ONLRET);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.OFILL, CLibrary.OFILL);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.NLDLY, CLibrary.NLDLY);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.TABDLY, CLibrary.TABDLY);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.CRDLY, CLibrary.CRDLY);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.FFDLY, CLibrary.FFDLY);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.BSDLY, CLibrary.BSDLY);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.VTDLY, CLibrary.VTDLY);
         addFlag(c_oflag, oflag, Attributes.OutputFlag.OFDEL, CLibrary.OFDEL);
         long c_cflag = this.c_cflag();
         EnumSet<Attributes.ControlFlag> cflag = attr.getControlFlags();
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CIGNORE, CLibrary.CIGNORE);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CS5, CLibrary.CS5);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CS6, CLibrary.CS6);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CS7, CLibrary.CS7);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CS8, CLibrary.CS8);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CSTOPB, CLibrary.CSTOPB);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CREAD, CLibrary.CREAD);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.PARENB, CLibrary.PARENB);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.PARODD, CLibrary.PARODD);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.HUPCL, CLibrary.HUPCL);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CLOCAL, CLibrary.CLOCAL);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CCTS_OFLOW, CLibrary.CCTS_OFLOW);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CRTS_IFLOW, CLibrary.CRTS_IFLOW);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CDSR_OFLOW, CLibrary.CDSR_OFLOW);
         addFlag(c_cflag, cflag, Attributes.ControlFlag.CCAR_OFLOW, CLibrary.CCAR_OFLOW);
         long c_lflag = this.c_lflag();
         EnumSet<Attributes.LocalFlag> lflag = attr.getLocalFlags();
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOKE, CLibrary.ECHOKE);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOE, CLibrary.ECHOE);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOK, CLibrary.ECHOK);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHO, CLibrary.ECHO);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHONL, CLibrary.ECHONL);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOPRT, CLibrary.ECHOPRT);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOCTL, CLibrary.ECHOCTL);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ISIG, CLibrary.ISIG);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ICANON, CLibrary.ICANON);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.ALTWERASE, CLibrary.ALTWERASE);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.IEXTEN, CLibrary.IEXTEN);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.EXTPROC, CLibrary.EXTPROC);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.TOSTOP, CLibrary.TOSTOP);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.FLUSHO, CLibrary.FLUSHO);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.NOKERNINFO, CLibrary.NOKERNINFO);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.PENDIN, CLibrary.PENDIN);
         addFlag(c_lflag, lflag, Attributes.LocalFlag.NOFLSH, CLibrary.NOFLSH);
         byte[] c_cc = this.c_cc().toArray(ValueLayout.JAVA_BYTE);
         EnumMap<Attributes.ControlChar, Integer> cc = attr.getControlChars();
         cc.put(Attributes.ControlChar.VEOF, Integer.valueOf(c_cc[CLibrary.VEOF]));
         cc.put(Attributes.ControlChar.VEOL, Integer.valueOf(c_cc[CLibrary.VEOL]));
         cc.put(Attributes.ControlChar.VEOL2, Integer.valueOf(c_cc[CLibrary.VEOL2]));
         cc.put(Attributes.ControlChar.VERASE, Integer.valueOf(c_cc[CLibrary.VERASE]));
         cc.put(Attributes.ControlChar.VWERASE, Integer.valueOf(c_cc[CLibrary.VWERASE]));
         cc.put(Attributes.ControlChar.VKILL, Integer.valueOf(c_cc[CLibrary.VKILL]));
         cc.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(c_cc[CLibrary.VREPRINT]));
         cc.put(Attributes.ControlChar.VINTR, Integer.valueOf(c_cc[CLibrary.VINTR]));
         cc.put(Attributes.ControlChar.VQUIT, Integer.valueOf(c_cc[CLibrary.VQUIT]));
         cc.put(Attributes.ControlChar.VSUSP, Integer.valueOf(c_cc[CLibrary.VSUSP]));
         if (CLibrary.VDSUSP != -1) {
            cc.put(Attributes.ControlChar.VDSUSP, Integer.valueOf(c_cc[CLibrary.VDSUSP]));
         }

         cc.put(Attributes.ControlChar.VSTART, Integer.valueOf(c_cc[CLibrary.VSTART]));
         cc.put(Attributes.ControlChar.VSTOP, Integer.valueOf(c_cc[CLibrary.VSTOP]));
         cc.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(c_cc[CLibrary.VLNEXT]));
         cc.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(c_cc[CLibrary.VDISCARD]));
         cc.put(Attributes.ControlChar.VMIN, Integer.valueOf(c_cc[CLibrary.VMIN]));
         cc.put(Attributes.ControlChar.VTIME, Integer.valueOf(c_cc[CLibrary.VTIME]));
         if (CLibrary.VSTATUS != -1) {
            cc.put(Attributes.ControlChar.VSTATUS, Integer.valueOf(c_cc[CLibrary.VSTATUS]));
         }

         return attr;
      }

      static {
         if (OSUtils.IS_OSX) {
            LAYOUT = MemoryLayout.structLayout(
               ValueLayout.JAVA_LONG.withName("c_iflag"),
               ValueLayout.JAVA_LONG.withName("c_oflag"),
               ValueLayout.JAVA_LONG.withName("c_cflag"),
               ValueLayout.JAVA_LONG.withName("c_lflag"),
               MemoryLayout.sequenceLayout(32L, ValueLayout.JAVA_BYTE).withName("c_cc"),
               ValueLayout.JAVA_LONG.withName("c_ispeed"),
               ValueLayout.JAVA_LONG.withName("c_ospeed")
            );
         } else {
            if (!OSUtils.IS_LINUX) {
               throw new IllegalStateException("Unsupported system!");
            }

            LAYOUT = MemoryLayout.structLayout(
               ValueLayout.JAVA_INT.withName("c_iflag"),
               ValueLayout.JAVA_INT.withName("c_oflag"),
               ValueLayout.JAVA_INT.withName("c_cflag"),
               ValueLayout.JAVA_INT.withName("c_lflag"),
               ValueLayout.JAVA_BYTE.withName("c_line"),
               MemoryLayout.sequenceLayout(32L, ValueLayout.JAVA_BYTE).withName("c_cc"),
               MemoryLayout.paddingLayout(3L),
               ValueLayout.JAVA_INT.withName("c_ispeed"),
               ValueLayout.JAVA_INT.withName("c_ospeed")
            );
         }

         c_iflag = adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("c_iflag")));
         c_oflag = adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("c_oflag")));
         c_cflag = adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("c_cflag")));
         c_lflag = adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("c_lflag")));
         c_cc_offset = LAYOUT.byteOffset(PathElement.groupElement("c_cc"));
         c_ispeed = adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("c_ispeed")));
         c_ospeed = adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("c_ospeed")));
      }
   }

   static class winsize {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(
         ValueLayout.JAVA_SHORT.withName("ws_row"), ValueLayout.JAVA_SHORT.withName("ws_col"), ValueLayout.JAVA_SHORT, ValueLayout.JAVA_SHORT
      );
      private static final VarHandle ws_col = FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("ws_col"));
      private static final VarHandle ws_row = FfmTerminalProvider.lookupVarHandle(LAYOUT, PathElement.groupElement("ws_row"));
      private final MemorySegment seg = Arena.ofAuto().allocate(LAYOUT);

      winsize() {
      }

      winsize(short ws_col, short ws_row) {
         this();
         this.ws_col(ws_col);
         this.ws_row(ws_row);
      }

      MemorySegment segment() {
         return this.seg;
      }

      short ws_col() {
         return (short)ws_col.get((MemorySegment)this.seg);
      }

      void ws_col(short col) {
         ws_col.set((MemorySegment)this.seg, (short)col);
      }

      short ws_row() {
         return (short)ws_row.get((MemorySegment)this.seg);
      }

      void ws_row(short row) {
         ws_row.set((MemorySegment)this.seg, (short)row);
      }
   }
}
