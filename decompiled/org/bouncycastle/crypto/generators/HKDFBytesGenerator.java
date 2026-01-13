package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class HKDFBytesGenerator implements DerivationFunction {
   private HMac hMacHash;
   private int hashLen;
   private byte[] info;
   private byte[] currentT;
   private int generatedBytes;

   public HKDFBytesGenerator(Digest var1) {
      this.hMacHash = new HMac(var1);
      this.hashLen = var1.getDigestSize();
   }

   @Override
   public void init(DerivationParameters var1) {
      if (!(var1 instanceof HKDFParameters)) {
         throw new IllegalArgumentException("HKDF parameters required for HKDFBytesGenerator");
      } else {
         HKDFParameters var2 = (HKDFParameters)var1;
         if (var2.skipExtract()) {
            this.hMacHash.init(new KeyParameter(var2.getIKM()));
         } else {
            this.hMacHash.init(new KeyParameter(this.extractPRK(var2.getSalt(), var2.getIKM())));
         }

         this.info = var2.getInfo();
         this.generatedBytes = 0;
         this.currentT = new byte[this.hashLen];
      }
   }

   public byte[] extractPRK(byte[] var1, byte[] var2) {
      if (var1 == null) {
         this.hMacHash.init(new KeyParameter(new byte[this.hashLen]));
      } else {
         this.hMacHash.init(new KeyParameter(var1));
      }

      this.hMacHash.update(var2, 0, var2.length);
      byte[] var3 = new byte[this.hashLen];
      this.hMacHash.doFinal(var3, 0);
      return var3;
   }

   private void expandNext() throws DataLengthException {
      int var1 = this.generatedBytes / this.hashLen + 1;
      if (var1 >= 256) {
         throw new DataLengthException("HKDF cannot generate more than 255 blocks of HashLen size");
      } else {
         if (this.generatedBytes != 0) {
            this.hMacHash.update(this.currentT, 0, this.hashLen);
         }

         this.hMacHash.update(this.info, 0, this.info.length);
         this.hMacHash.update((byte)var1);
         this.hMacHash.doFinal(this.currentT, 0);
      }
   }

   public Digest getDigest() {
      return this.hMacHash.getUnderlyingDigest();
   }

   @Override
   public int generateBytes(byte[] var1, int var2, int var3) throws DataLengthException, IllegalArgumentException {
      if (this.generatedBytes + var3 > 255 * this.hashLen) {
         throw new DataLengthException("HKDF may only be used for 255 * HashLen bytes of output");
      } else {
         if (this.generatedBytes % this.hashLen == 0) {
            this.expandNext();
         }

         int var5 = this.generatedBytes % this.hashLen;
         int var6 = this.hashLen - this.generatedBytes % this.hashLen;
         int var7 = Math.min(var6, var3);
         System.arraycopy(this.currentT, var5, var1, var2, var7);
         this.generatedBytes += var7;
         int var4 = var3 - var7;
         var2 += var7;

         while (var4 > 0) {
            this.expandNext();
            var7 = Math.min(this.hashLen, var4);
            System.arraycopy(this.currentT, 0, var1, var2, var7);
            this.generatedBytes += var7;
            var4 -= var7;
            var2 += var7;
         }

         return var3;
      }
   }
}
