package org.bouncycastle.util;

import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

public class Fingerprint {
   private static char[] encodingTable = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private final byte[] fingerprint;

   public Fingerprint(byte[] var1) {
      this(var1, 160);
   }

   public Fingerprint(byte[] var1, int var2) {
      this.fingerprint = calculateFingerprint(var1, var2);
   }

   @Deprecated
   public Fingerprint(byte[] var1, boolean var2) {
      if (var2) {
         this.fingerprint = calculateFingerprintSHA512_160(var1);
      } else {
         this.fingerprint = calculateFingerprint(var1);
      }
   }

   public byte[] getFingerprint() {
      return Arrays.clone(this.fingerprint);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 != this.fingerprint.length; var2++) {
         if (var2 > 0) {
            var1.append(":");
         }

         var1.append(encodingTable[this.fingerprint[var2] >>> 4 & 15]);
         var1.append(encodingTable[this.fingerprint[var2] & 15]);
      }

      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return var1 instanceof Fingerprint ? Arrays.areEqual(((Fingerprint)var1).fingerprint, this.fingerprint) : false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.fingerprint);
   }

   public static byte[] calculateFingerprint(byte[] var0) {
      return calculateFingerprint(var0, 160);
   }

   public static byte[] calculateFingerprint(byte[] var0, int var1) {
      if (var1 % 8 != 0) {
         throw new IllegalArgumentException("bitLength must be a multiple of 8");
      } else {
         SHAKEDigest var2 = new SHAKEDigest(256);
         var2.update(var0, 0, var0.length);
         byte[] var3 = new byte[var1 / 8];
         var2.doFinal(var3, 0, var1 / 8);
         return var3;
      }
   }

   @Deprecated
   public static byte[] calculateFingerprintSHA512_160(byte[] var0) {
      SHA512tDigest var1 = new SHA512tDigest(160);
      var1.update(var0, 0, var0.length);
      byte[] var2 = new byte[var1.getDigestSize()];
      var1.doFinal(var2, 0);
      return var2;
   }
}
