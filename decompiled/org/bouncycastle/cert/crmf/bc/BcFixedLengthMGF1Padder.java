package org.bouncycastle.cert.crmf.bc;

import java.security.SecureRandom;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.params.MGFParameters;

public class BcFixedLengthMGF1Padder implements EncryptedValuePadder {
   private int length;
   private SecureRandom random;
   private Digest dig = new SHA1Digest();

   public BcFixedLengthMGF1Padder(int var1) {
      this(var1, null);
   }

   public BcFixedLengthMGF1Padder(int var1, SecureRandom var2) {
      this.length = var1;
      this.random = var2;
   }

   @Override
   public byte[] getPaddedData(byte[] var1) {
      byte[] var2 = new byte[this.length];
      byte[] var3 = new byte[this.dig.getDigestSize()];
      byte[] var4 = new byte[this.length - this.dig.getDigestSize()];
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      this.random.nextBytes(var3);
      MGF1BytesGenerator var5 = new MGF1BytesGenerator(this.dig);
      var5.init(new MGFParameters(var3));
      var5.generateBytes(var4, 0, var4.length);
      System.arraycopy(var3, 0, var2, 0, var3.length);
      System.arraycopy(var1, 0, var2, var3.length, var1.length);

      for (int var6 = var3.length + var1.length + 1; var6 != var2.length; var6++) {
         var2[var6] = (byte)(1 + this.random.nextInt(255));
      }

      for (int var7 = 0; var7 != var4.length; var7++) {
         var2[var7 + var3.length] = (byte)(var2[var7 + var3.length] ^ var4[var7]);
      }

      return var2;
   }

   @Override
   public byte[] getUnpaddedData(byte[] var1) {
      byte[] var2 = new byte[this.dig.getDigestSize()];
      byte[] var3 = new byte[this.length - this.dig.getDigestSize()];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      MGF1BytesGenerator var4 = new MGF1BytesGenerator(this.dig);
      var4.init(new MGFParameters(var2));
      var4.generateBytes(var3, 0, var3.length);

      for (int var5 = 0; var5 != var3.length; var5++) {
         var1[var5 + var2.length] = (byte)(var1[var5 + var2.length] ^ var3[var5]);
      }

      int var7 = 0;

      for (int var6 = var1.length - 1; var6 != var2.length; var6--) {
         if (var1[var6] == 0) {
            var7 = var6;
            break;
         }
      }

      if (var7 == 0) {
         throw new IllegalStateException("bad padding in encoding");
      } else {
         byte[] var8 = new byte[var7 - var2.length];
         System.arraycopy(var1, var2.length, var8, 0, var8.length);
         return var8;
      }
   }
}
