package org.bouncycastle.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

public final class Streams {
   private static int BUFFER_SIZE = 4096;

   public static void drain(InputStream var0) throws IOException {
      byte[] var1 = new byte[BUFFER_SIZE];

      while (var0.read(var1, 0, var1.length) >= 0) {
      }
   }

   public static void pipeAll(InputStream var0, OutputStream var1) throws IOException {
      pipeAll(var0, var1, BUFFER_SIZE);
   }

   public static void pipeAll(InputStream var0, OutputStream var1, int var2) throws IOException {
      byte[] var3 = new byte[var2];

      int var4;
      while ((var4 = var0.read(var3, 0, var3.length)) >= 0) {
         var1.write(var3, 0, var4);
      }
   }

   public static long pipeAllLimited(InputStream var0, long var1, OutputStream var3) throws IOException {
      long var4 = 0L;
      byte[] var6 = new byte[BUFFER_SIZE];

      int var7;
      while ((var7 = var0.read(var6, 0, var6.length)) >= 0) {
         if (var1 - var4 < var7) {
            throw new StreamOverflowException("Data Overflow");
         }

         var4 += var7;
         var3.write(var6, 0, var7);
      }

      return var4;
   }

   public static byte[] readAll(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      pipeAll(var0, var1);
      return var1.toByteArray();
   }

   public static byte[] readAllLimited(InputStream var0, int var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      pipeAllLimited(var0, var1, var2);
      return var2.toByteArray();
   }

   public static int readFully(InputStream var0, byte[] var1) throws IOException {
      return readFully(var0, var1, 0, var1.length);
   }

   public static int readFully(InputStream var0, byte[] var1, int var2, int var3) throws IOException {
      int var4 = 0;

      while (var4 < var3) {
         int var5 = var0.read(var1, var2 + var4, var3 - var4);
         if (var5 < 0) {
            break;
         }

         var4 += var5;
      }

      return var4;
   }

   public static void validateBufferArguments(byte[] var0, int var1, int var2) {
      Arrays.validateSegment(var0, var1, var2);
   }

   public static void writeBufTo(ByteArrayOutputStream var0, OutputStream var1) throws IOException {
      var0.writeTo(var1);
   }
}
