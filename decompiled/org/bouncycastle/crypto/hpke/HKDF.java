package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

class HKDF {
   private static final byte[] VERSION_LABEL = getBytes("HPKE-v1");
   private final HKDFBytesGenerator kdf;
   private final int hashLength;

   HKDF(short var1) {
      Object var2;
      switch (var1) {
         case 1:
            var2 = new SHA256Digest();
            break;
         case 2:
            var2 = new SHA384Digest();
            break;
         case 3:
            var2 = new SHA512Digest();
            break;
         default:
            throw new IllegalArgumentException("invalid kdf id");
      }

      this.kdf = new HKDFBytesGenerator((Digest)var2);
      this.hashLength = ((Digest)var2).getDigestSize();
   }

   int getHashSize() {
      return this.hashLength;
   }

   protected byte[] LabeledExtract(byte[] var1, byte[] var2, String var3, byte[] var4) {
      if (var1 == null) {
         var1 = new byte[this.hashLength];
      }

      byte[] var5 = Arrays.concatenate(VERSION_LABEL, var2, getBytes(var3), var4);
      return this.kdf.extractPRK(var1, var5);
   }

   protected byte[] LabeledExpand(byte[] var1, byte[] var2, String var3, byte[] var4, int var5) {
      if (var5 > 65536) {
         throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
      } else {
         byte[] var6 = Arrays.concatenate(Pack.shortToBigEndian((short)var5), VERSION_LABEL, var2, getBytes(var3));
         this.kdf.init(HKDFParameters.skipExtractParameters(var1, Arrays.concatenate(var6, var4)));
         byte[] var7 = new byte[var5];
         this.kdf.generateBytes(var7, 0, var7.length);
         return var7;
      }
   }

   protected byte[] Extract(byte[] var1, byte[] var2) {
      if (var1 == null) {
         var1 = new byte[this.hashLength];
      }

      return this.kdf.extractPRK(var1, var2);
   }

   protected byte[] Expand(byte[] var1, byte[] var2, int var3) {
      if (var3 > 65536) {
         throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
      } else {
         this.kdf.init(HKDFParameters.skipExtractParameters(var1, var2));
         byte[] var4 = new byte[var3];
         this.kdf.generateBytes(var4, 0, var4.length);
         return var4;
      }
   }

   private static byte[] getBytes(String var0) {
      return Strings.toByteArray(var0);
   }
}
