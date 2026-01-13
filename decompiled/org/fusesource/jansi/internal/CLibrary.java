package org.fusesource.jansi.internal;

public class CLibrary {
   public static final boolean LOADED = JansiLoader.initialize();
   public static int STDOUT_FILENO;
   public static int STDERR_FILENO;
   public static boolean HAVE_ISATTY;
   public static boolean HAVE_TTYNAME;
   public static int TCSANOW;
   public static int TCSADRAIN;
   public static int TCSAFLUSH;
   public static long TIOCGETA;
   public static long TIOCSETA;
   public static long TIOCGETD;
   public static long TIOCSETD;
   public static long TIOCGWINSZ;
   public static long TIOCSWINSZ;

   private static native void init();

   public static native int isatty(int var0);

   public static native String ttyname(int var0);

   public static native int openpty(int[] var0, int[] var1, byte[] var2, CLibrary.Termios var3, CLibrary.WinSize var4);

   public static native int tcgetattr(int var0, CLibrary.Termios var1);

   public static native int tcsetattr(int var0, int var1, CLibrary.Termios var2);

   public static native int ioctl(int var0, long var1, int[] var3);

   public static native int ioctl(int var0, long var1, CLibrary.WinSize var3);

   static {
      if (LOADED) {
         init();
      }

      STDOUT_FILENO = 1;
      STDERR_FILENO = 2;
   }

   public static class Termios {
      public static int SIZEOF;
      public long c_iflag;
      public long c_oflag;
      public long c_cflag;
      public long c_lflag;
      public byte[] c_cc = new byte[32];
      public long c_ispeed;
      public long c_ospeed;

      private static native void init();

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class WinSize {
      public static int SIZEOF;
      public short ws_row;
      public short ws_col;
      public short ws_xpixel;
      public short ws_ypixel;

      private static native void init();

      public WinSize() {
      }

      public WinSize(short ws_row, short ws_col) {
         this.ws_row = ws_row;
         this.ws_col = ws_col;
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }
}
