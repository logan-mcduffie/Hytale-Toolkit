package org.bouncycastle.pqc.crypto.xmss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class XMSSUtil {
   public static int log2(int var0) {
      int var1 = 0;

      while ((var0 >>= 1) != 0) {
         var1++;
      }

      return var1;
   }

   public static byte[] toBytesBigEndian(long var0, int var2) {
      byte[] var3 = new byte[var2];

      for (int var4 = var2 - 1; var4 >= 0; var4--) {
         var3[var4] = (byte)var0;
         var0 >>>= 8;
      }

      return var3;
   }

   public static void longToBigEndian(long var0, byte[] var2, int var3) {
      if (var2 == null) {
         throw new NullPointerException("in == null");
      } else if (var2.length - var3 < 8) {
         throw new IllegalArgumentException("not enough space in array");
      } else {
         var2[var3] = (byte)(var0 >> 56 & 255L);
         var2[var3 + 1] = (byte)(var0 >> 48 & 255L);
         var2[var3 + 2] = (byte)(var0 >> 40 & 255L);
         var2[var3 + 3] = (byte)(var0 >> 32 & 255L);
         var2[var3 + 4] = (byte)(var0 >> 24 & 255L);
         var2[var3 + 5] = (byte)(var0 >> 16 & 255L);
         var2[var3 + 6] = (byte)(var0 >> 8 & 255L);
         var2[var3 + 7] = (byte)(var0 & 255L);
      }
   }

   public static long bytesToXBigEndian(byte[] var0, int var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException("in == null");
      } else {
         long var3 = 0L;

         for (int var5 = var1; var5 < var1 + var2; var5++) {
            var3 = var3 << 8 | var0[var5] & 255;
         }

         return var3;
      }
   }

   public static byte[] cloneArray(byte[] var0) {
      if (var0 == null) {
         throw new NullPointerException("in == null");
      } else {
         byte[] var1 = new byte[var0.length];
         System.arraycopy(var0, 0, var1, 0, var0.length);
         return var1;
      }
   }

   public static byte[][] cloneArray(byte[][] var0) {
      if (hasNullPointer(var0)) {
         throw new NullPointerException("in has null pointers");
      } else {
         byte[][] var1 = new byte[var0.length][];

         for (int var2 = 0; var2 < var0.length; var2++) {
            var1[var2] = new byte[var0[var2].length];
            System.arraycopy(var0[var2], 0, var1[var2], 0, var0[var2].length);
         }

         return var1;
      }
   }

   public static boolean areEqual(byte[][] var0, byte[][] var1) {
      if (!hasNullPointer(var0) && !hasNullPointer(var1)) {
         for (int var2 = 0; var2 < var0.length; var2++) {
            if (!Arrays.areEqual(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      } else {
         throw new NullPointerException("a or b == null");
      }
   }

   public static void dumpByteArray(byte[][] var0) {
      if (hasNullPointer(var0)) {
         throw new NullPointerException("x has null pointers");
      } else {
         for (int var1 = 0; var1 < var0.length; var1++) {
            System.out.println(Hex.toHexString(var0[var1]));
         }
      }
   }

   public static boolean hasNullPointer(byte[][] var0) {
      if (var0 == null) {
         return true;
      } else {
         for (int var1 = 0; var1 < var0.length; var1++) {
            if (var0[var1] == null) {
               return true;
            }
         }

         return false;
      }
   }

   public static void copyBytesAtOffset(byte[] var0, byte[] var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException("dst == null");
      } else if (var1 == null) {
         throw new NullPointerException("src == null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("offset hast to be >= 0");
      } else if (var1.length + var2 > var0.length) {
         throw new IllegalArgumentException("src length + offset must not be greater than size of destination");
      } else {
         for (int var3 = 0; var3 < var1.length; var3++) {
            var0[var2 + var3] = var1[var3];
         }
      }
   }

   public static byte[] extractBytesAtOffset(byte[] var0, int var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException("src == null");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("offset hast to be >= 0");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("length hast to be >= 0");
      } else if (var1 + var2 > var0.length) {
         throw new IllegalArgumentException("offset + length must not be greater then size of source array");
      } else {
         byte[] var3 = new byte[var2];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var3[var4] = var0[var1 + var4];
         }

         return var3;
      }
   }

   public static boolean isIndexValid(int var0, long var1) {
      if (var1 < 0L) {
         throw new IllegalStateException("index must not be negative");
      } else {
         return var1 < 1L << var0;
      }
   }

   public static int getDigestSize(Digest var0) {
      if (var0 == null) {
         throw new NullPointerException("digest == null");
      } else {
         String var1 = var0.getAlgorithmName();
         if (var1.equals("SHAKE128")) {
            return 32;
         } else {
            return var1.equals("SHAKE256") ? 64 : var0.getDigestSize();
         }
      }
   }

   public static long getTreeIndex(long var0, int var2) {
      return var0 >> var2;
   }

   public static int getLeafIndex(long var0, int var2) {
      return (int)(var0 & (1L << var2) - 1L);
   }

   public static byte[] serialize(Object var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      ObjectOutputStream var2 = new ObjectOutputStream(var1);
      var2.writeObject(var0);
      var2.flush();
      return var1.toByteArray();
   }

   public static Object deserialize(byte[] var0, Class var1) throws IOException, ClassNotFoundException {
      ByteArrayInputStream var2 = new ByteArrayInputStream(var0);
      XMSSUtil.CheckingStream var3 = new XMSSUtil.CheckingStream(var1, var2);
      Object var4 = var3.readObject();
      if (var3.available() != 0) {
         throw new IOException("unexpected data found at end of ObjectInputStream");
      } else if (var1.isInstance(var4)) {
         return var4;
      } else {
         throw new IOException("unexpected class found in ObjectInputStream");
      }
   }

   public static int calculateTau(int var0, int var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < var1; var3++) {
         if ((var0 >> var3 & 1) == 0) {
            var2 = var3;
            break;
         }
      }

      return var2;
   }

   public static boolean isNewBDSInitNeeded(long var0, int var2, int var3) {
      return var0 == 0L ? false : var0 % (long)Math.pow(1 << var2, var3 + 1) == 0L;
   }

   public static boolean isNewAuthenticationPathNeeded(long var0, int var2, int var3) {
      return var0 == 0L ? false : (var0 + 1L) % (long)Math.pow(1 << var2, var3) == 0L;
   }

   private static class CheckingStream extends ObjectInputStream {
      private static final Set components = new HashSet();
      private final Class mainClass;
      private boolean found = false;

      CheckingStream(Class var1, InputStream var2) throws IOException {
         super(var2);
         this.mainClass = var1;
      }

      @Override
      protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
         if (!this.found) {
            if (!var1.getName().equals(this.mainClass.getName())) {
               throw new InvalidClassException("unexpected class: ", var1.getName());
            }

            this.found = true;
         } else if (!components.contains(var1.getName())) {
            throw new InvalidClassException("unexpected class: ", var1.getName());
         }

         return super.resolveClass(var1);
      }

      static {
         components.add("java.util.TreeMap");
         components.add("java.lang.Integer");
         components.add("java.lang.Number");
         components.add("org.bouncycastle.pqc.crypto.xmss.BDS");
         components.add("java.util.ArrayList");
         components.add("org.bouncycastle.pqc.crypto.xmss.XMSSNode");
         components.add("[B");
         components.add("java.util.LinkedList");
         components.add("java.util.Stack");
         components.add("java.util.Vector");
         components.add("[Ljava.lang.Object;");
         components.add("org.bouncycastle.pqc.crypto.xmss.BDSTreeHash");
      }
   }
}
