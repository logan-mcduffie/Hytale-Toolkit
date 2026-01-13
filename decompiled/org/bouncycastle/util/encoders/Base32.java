package org.bouncycastle.util.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Strings;

public class Base32 {
   private static final Encoder encoder = new Base32Encoder();

   public static String toBase32String(byte[] var0) {
      return toBase32String(var0, 0, var0.length);
   }

   public static String toBase32String(byte[] var0, int var1, int var2) {
      byte[] var3 = encode(var0, var1, var2);
      return Strings.fromByteArray(var3);
   }

   public static byte[] encode(byte[] var0) {
      return encode(var0, 0, var0.length);
   }

   public static byte[] encode(byte[] var0, int var1, int var2) {
      int var3 = encoder.getEncodedLength(var2);
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(var3);

      try {
         encoder.encode(var0, var1, var2, var4);
      } catch (Exception var6) {
         throw new EncoderException("exception encoding base32 string: " + var6.getMessage(), var6);
      }

      return var4.toByteArray();
   }

   public static int encode(byte[] var0, OutputStream var1) throws IOException {
      return encoder.encode(var0, 0, var0.length, var1);
   }

   public static int encode(byte[] var0, int var1, int var2, OutputStream var3) throws IOException {
      return encoder.encode(var0, var1, var2, var3);
   }

   public static byte[] decode(byte[] var0) {
      int var1 = var0.length / 8 * 5;
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(var1);

      try {
         encoder.decode(var0, 0, var0.length, var2);
      } catch (Exception var4) {
         throw new DecoderException("unable to decode base32 data: " + var4.getMessage(), var4);
      }

      return var2.toByteArray();
   }

   public static byte[] decode(String var0) {
      int var1 = var0.length() / 8 * 5;
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(var1);

      try {
         encoder.decode(var0, var2);
      } catch (Exception var4) {
         throw new DecoderException("unable to decode base32 string: " + var4.getMessage(), var4);
      }

      return var2.toByteArray();
   }

   public static int decode(String var0, OutputStream var1) throws IOException {
      return encoder.decode(var0, var1);
   }

   public static int decode(byte[] var0, int var1, int var2, OutputStream var3) {
      try {
         return encoder.decode(var0, var1, var2, var3);
      } catch (Exception var5) {
         throw new DecoderException("unable to decode base32 data: " + var5.getMessage(), var5);
      }
   }
}
