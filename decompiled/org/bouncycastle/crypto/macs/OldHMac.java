package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;

public class OldHMac implements Mac {
   private static final int BLOCK_LENGTH = 64;
   private static final byte IPAD = 54;
   private static final byte OPAD = 92;
   private Digest digest;
   private int digestSize;
   private byte[] inputPad = new byte[64];
   private byte[] outputPad = new byte[64];

   /** @deprecated */
   public OldHMac(Digest var1) {
      this.digest = var1;
      this.digestSize = var1.getDigestSize();
   }

   @Override
   public String getAlgorithmName() {
      return this.digest.getAlgorithmName() + "/HMAC";
   }

   public Digest getUnderlyingDigest() {
      return this.digest;
   }

   @Override
   public void init(CipherParameters var1) {
      this.digest.reset();
      byte[] var2 = ((KeyParameter)var1).getKey();
      if (var2.length > 64) {
         this.digest.update(var2, 0, var2.length);
         this.digest.doFinal(this.inputPad, 0);

         for (int var3 = this.digestSize; var3 < this.inputPad.length; var3++) {
            this.inputPad[var3] = 0;
         }
      } else {
         System.arraycopy(var2, 0, this.inputPad, 0, var2.length);

         for (int var4 = var2.length; var4 < this.inputPad.length; var4++) {
            this.inputPad[var4] = 0;
         }
      }

      this.outputPad = new byte[this.inputPad.length];
      System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);

      for (int var5 = 0; var5 < this.inputPad.length; var5++) {
         this.inputPad[var5] = (byte)(this.inputPad[var5] ^ 54);
      }

      for (int var6 = 0; var6 < this.outputPad.length; var6++) {
         this.outputPad[var6] = (byte)(this.outputPad[var6] ^ 92);
      }

      this.digest.update(this.inputPad, 0, this.inputPad.length);
   }

   @Override
   public int getMacSize() {
      return this.digestSize;
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      byte[] var3 = new byte[this.digestSize];
      this.digest.doFinal(var3, 0);
      this.digest.update(this.outputPad, 0, this.outputPad.length);
      this.digest.update(var3, 0, var3.length);
      int var4 = this.digest.doFinal(var1, var2);
      this.reset();
      return var4;
   }

   @Override
   public void reset() {
      this.digest.reset();
      this.digest.update(this.inputPad, 0, this.inputPad.length);
   }
}
