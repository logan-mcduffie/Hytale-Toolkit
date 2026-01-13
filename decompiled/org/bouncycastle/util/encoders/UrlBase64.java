package org.bouncycastle.util.encoders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UrlBase64 {
   private static final Encoder encoder = new UrlBase64Encoder();

   public static byte[] encode(byte[] var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      try {
         encoder.encode(var0, 0, var0.length, var1);
      } catch (Exception var3) {
         throw new EncoderException("exception encoding URL safe base64 data: " + var3.getMessage(), var3);
      }

      return var1.toByteArray();
   }

   public static int encode(byte[] var0, OutputStream var1) throws IOException {
      return encoder.encode(var0, 0, var0.length, var1);
   }

   public static byte[] decode(byte[] var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      try {
         encoder.decode(var0, 0, var0.length, var1);
      } catch (Exception var3) {
         throw new DecoderException("exception decoding URL safe base64 string: " + var3.getMessage(), var3);
      }

      return var1.toByteArray();
   }

   public static int decode(byte[] var0, OutputStream var1) throws IOException {
      return encoder.decode(var0, 0, var0.length, var1);
   }

   public static byte[] decode(String var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      try {
         encoder.decode(var0, var1);
      } catch (Exception var3) {
         throw new DecoderException("exception decoding URL safe base64 string: " + var3.getMessage(), var3);
      }

      return var1.toByteArray();
   }

   public static int decode(String var0, OutputStream var1) throws IOException {
      return encoder.decode(var0, var1);
   }
}
