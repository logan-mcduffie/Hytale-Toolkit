package com.google.crypto.tink.internal;

import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class Util {
   public static final Charset UTF_8 = Charset.forName("UTF-8");

   public static int randKeyId() {
      int result = 0;

      while (result == 0) {
         byte[] rand = Random.randBytes(4);
         result = (rand[0] & 255) << 24 | (rand[1] & 255) << 16 | (rand[2] & 255) << 8 | rand[3] & 255;
      }

      return result;
   }

   private static final byte toByteFromPrintableAscii(char c) {
      if (c >= '!' && c <= '~') {
         return (byte)c;
      } else {
         throw new TinkBugException("Not a printable ASCII character: " + c);
      }
   }

   private static final byte checkedToByteFromPrintableAscii(char c) throws GeneralSecurityException {
      if (c >= '!' && c <= '~') {
         return (byte)c;
      } else {
         throw new GeneralSecurityException("Not a printable ASCII character: " + c);
      }
   }

   public static final Bytes toBytesFromPrintableAscii(String s) {
      byte[] result = new byte[s.length()];

      for (int i = 0; i < s.length(); i++) {
         result[i] = toByteFromPrintableAscii(s.charAt(i));
      }

      return Bytes.copyFrom(result);
   }

   public static final Bytes checkedToBytesFromPrintableAscii(String s) throws GeneralSecurityException {
      byte[] result = new byte[s.length()];

      for (int i = 0; i < s.length(); i++) {
         result[i] = checkedToByteFromPrintableAscii(s.charAt(i));
      }

      return Bytes.copyFrom(result);
   }

   public static boolean isAndroid() {
      return Objects.equals(System.getProperty("java.vendor"), "The Android Project");
   }

   @Nullable
   public static Integer getAndroidApiLevel() {
      return !isAndroid() ? null : BuildDispatchedCode.getApiLevel();
   }

   public static boolean isPrefix(byte[] prefix, byte[] complete) {
      if (complete.length < prefix.length) {
         return false;
      } else {
         for (int i = 0; i < prefix.length; i++) {
            if (complete[i] != prefix[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public static SecretBytes readIntoSecretBytes(InputStream input, int length, SecretKeyAccess access) throws GeneralSecurityException {
      byte[] output = new byte[length];

      try {
         int len = output.length;
         int readTotal = 0;

         while (readTotal < len) {
            int read = input.read(output, readTotal, len - readTotal);
            if (read == -1) {
               throw new GeneralSecurityException("Not enough pseudorandomness provided");
            }

            readTotal += read;
         }
      } catch (IOException var7) {
         throw new GeneralSecurityException("Reading pseudorandomness failed");
      }

      return SecretBytes.copyFrom(output, access);
   }

   private Util() {
   }
}
