package org.bouncycastle.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;
import org.bouncycastle.util.encoders.UTF8;

public final class Strings {
   private static String LINE_SEPARATOR;

   public static String fromUTF8ByteArray(byte[] var0) {
      return fromUTF8ByteArray(var0, 0, var0.length);
   }

   public static String fromUTF8ByteArray(byte[] var0, int var1, int var2) {
      char[] var3 = new char[var2];
      int var4 = UTF8.transcodeToUTF16(var0, var1, var2, var3);
      if (var4 < 0) {
         throw new IllegalArgumentException("Invalid UTF-8 input");
      } else {
         return new String(var3, 0, var4);
      }
   }

   public static byte[] toUTF8ByteArray(String var0) {
      return toUTF8ByteArray(var0.toCharArray());
   }

   public static byte[] toUTF8ByteArray(char[] var0) {
      return toUTF8ByteArray(var0, 0, var0.length);
   }

   public static byte[] toUTF8ByteArray(char[] var0, int var1, int var2) {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();

      try {
         toUTF8ByteArray(var0, var1, var2, var3);
      } catch (IOException var5) {
         throw new IllegalStateException("cannot encode string to byte array!");
      }

      return var3.toByteArray();
   }

   public static void toUTF8ByteArray(char[] var0, OutputStream var1) throws IOException {
      toUTF8ByteArray(var0, 0, var0.length, var1);
   }

   public static void toUTF8ByteArray(char[] var0, int var1, int var2, OutputStream var3) throws IOException {
      if (var2 >= 1) {
         byte[] var4 = new byte[64];
         int var5 = 0;
         int var6 = 0;

         do {
            char var7 = var0[var1 + var6++];
            if (var7 < 128) {
               var4[var5++] = (byte)var7;
            } else if (var7 < 2048) {
               var4[var5++] = (byte)(192 | var7 >> 6);
               var4[var5++] = (byte)(128 | var7 & '?');
            } else if (var7 >= '\ud800' && var7 <= '\udfff') {
               if (var7 > '\udbff') {
                  throw new IllegalStateException("invalid UTF-16 high surrogate");
               }

               if (var6 >= var2) {
                  throw new IllegalStateException("invalid UTF-16 codepoint (truncated surrogate pair)");
               }

               char var9 = var0[var1 + var6++];
               if (var9 < '\udc00' || var9 > '\udfff') {
                  throw new IllegalStateException("invalid UTF-16 low surrogate");
               }

               int var10 = ((var7 & 1023) << 10 | var9 & 1023) + 65536;
               var4[var5++] = (byte)(240 | var10 >> 18);
               var4[var5++] = (byte)(128 | var10 >> 12 & 63);
               var4[var5++] = (byte)(128 | var10 >> 6 & 63);
               var4[var5++] = (byte)(128 | var10 & 63);
            } else {
               var4[var5++] = (byte)(224 | var7 >> '\f');
               var4[var5++] = (byte)(128 | var7 >> 6 & 63);
               var4[var5++] = (byte)(128 | var7 & '?');
            }

            if (var5 + 4 > var4.length) {
               var3.write(var4, 0, var5);
               var5 = 0;
            }
         } while (var6 < var2);

         if (var5 > 0) {
            var3.write(var4, 0, var5);
         }
      }
   }

   public static String toUpperCase(String var0) {
      boolean var1 = false;
      char[] var2 = var0.toCharArray();

      for (int var3 = 0; var3 != var2.length; var3++) {
         char var4 = var2[var3];
         if ('a' <= var4 && 'z' >= var4) {
            var1 = true;
            var2[var3] = (char)(var4 - 'a' + 65);
         }
      }

      return var1 ? new String(var2) : var0;
   }

   public static String toLowerCase(String var0) {
      boolean var1 = false;
      char[] var2 = var0.toCharArray();

      for (int var3 = 0; var3 != var2.length; var3++) {
         char var4 = var2[var3];
         if ('A' <= var4 && 'Z' >= var4) {
            var1 = true;
            var2[var3] = (char)(var4 - 'A' + 97);
         }
      }

      return var1 ? new String(var2) : var0;
   }

   public static byte[] toByteArray(char[] var0) {
      byte[] var1 = new byte[var0.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (byte)var0[var2];
      }

      return var1;
   }

   public static byte[] toByteArray(String var0) {
      byte[] var1 = new byte[var0.length()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         char var3 = var0.charAt(var2);
         var1[var2] = (byte)var3;
      }

      return var1;
   }

   public static int toByteArray(String var0, byte[] var1, int var2) {
      int var3 = var0.length();

      for (int var4 = 0; var4 < var3; var4++) {
         char var5 = var0.charAt(var4);
         var1[var2 + var4] = (byte)var5;
      }

      return var3;
   }

   public static boolean constantTimeAreEqual(String var0, String var1) {
      boolean var2 = var0.length() == var1.length();
      int var3 = var0.length();
      if (var2) {
         for (int var4 = 0; var4 != var3; var4++) {
            var2 &= var0.charAt(var4) == var1.charAt(var4);
         }
      } else {
         for (int var5 = 0; var5 != var3; var5++) {
            var2 &= var0.charAt(var5) == ' ';
         }
      }

      return var2;
   }

   public static String fromByteArray(byte[] var0) {
      return new String(asCharArray(var0));
   }

   public static char[] asCharArray(byte[] var0) {
      char[] var1 = new char[var0.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (char)(var0[var2] & 255);
      }

      return var1;
   }

   public static String[] split(String var0, char var1) {
      Vector var2 = new Vector();
      boolean var3 = true;

      while (var3) {
         int var5 = var0.indexOf(var1);
         if (var5 > 0) {
            String var4 = var0.substring(0, var5);
            var2.addElement(var4);
            var0 = var0.substring(var5 + 1);
         } else {
            var3 = false;
            var2.addElement(var0);
         }
      }

      String[] var7 = new String[var2.size()];

      for (int var6 = 0; var6 != var7.length; var6++) {
         var7[var6] = (String)var2.elementAt(var6);
      }

      return var7;
   }

   public static StringList newList() {
      return new Strings.StringListImpl();
   }

   public static String lineSeparator() {
      return LINE_SEPARATOR;
   }

   static {
      try {
         LINE_SEPARATOR = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty("line.separator");
            }
         });
      } catch (Exception var3) {
         try {
            LINE_SEPARATOR = String.format("%n");
         } catch (Exception var2) {
            LINE_SEPARATOR = "\n";
         }
      }
   }

   private static class StringListImpl extends ArrayList<String> implements StringList {
      private StringListImpl() {
      }

      @Override
      public boolean add(String var1) {
         return super.add(var1);
      }

      public String set(int var1, String var2) {
         return super.set(var1, var2);
      }

      public void add(int var1, String var2) {
         super.add(var1, var2);
      }

      @Override
      public String[] toStringArray() {
         String[] var1 = new String[this.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = this.get(var2);
         }

         return var1;
      }

      @Override
      public String[] toStringArray(int var1, int var2) {
         String[] var3 = new String[var2 - var1];

         for (int var4 = var1; var4 != this.size() && var4 != var2; var4++) {
            var3[var4 - var1] = this.get(var4);
         }

         return var3;
      }
   }
}
