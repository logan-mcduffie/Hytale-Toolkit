package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class VMPCEngine implements StreamCipher {
   protected byte n = 0;
   protected byte[] P = null;
   protected byte s = 0;
   protected byte[] workingIV;
   protected byte[] workingKey;

   @Override
   public String getAlgorithmName() {
      return "VMPC";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof ParametersWithIV)) {
         throw new IllegalArgumentException("VMPC init parameters must include an IV");
      } else {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         if (!(var3.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC init parameters must include a key");
         } else {
            KeyParameter var4 = (KeyParameter)var3.getParameters();
            this.workingIV = var3.getIV();
            if (this.workingIV != null && this.workingIV.length >= 1 && this.workingIV.length <= 768) {
               this.workingKey = var4.getKey();
               this.initKey(this.workingKey, this.workingIV);
               CryptoServicesRegistrar.checkConstraints(
                  new DefaultServiceProperties(
                     this.getAlgorithmName(), this.workingKey.length >= 32 ? 256 : this.workingKey.length * 8, var2, Utils.getPurpose(var1)
                  )
               );
            } else {
               throw new IllegalArgumentException("VMPC requires 1 to 768 bytes of IV");
            }
         }
      }
   }

   protected void initKey(byte[] var1, byte[] var2) {
      this.s = 0;
      this.P = new byte[256];

      for (int var3 = 0; var3 < 256; var3++) {
         this.P[var3] = (byte)var3;
      }

      for (int var5 = 0; var5 < 768; var5++) {
         this.s = this.P[this.s + this.P[var5 & 0xFF] + var1[var5 % var1.length] & 0xFF];
         byte var4 = this.P[var5 & 0xFF];
         this.P[var5 & 0xFF] = this.P[this.s & 255];
         this.P[this.s & 255] = var4;
      }

      for (int var6 = 0; var6 < 768; var6++) {
         this.s = this.P[this.s + this.P[var6 & 0xFF] + var2[var6 % var2.length] & 0xFF];
         byte var7 = this.P[var6 & 0xFF];
         this.P[var6 & 0xFF] = this.P[this.s & 255];
         this.P[this.s & 255] = var7;
      }

      this.n = 0;
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            this.s = this.P[this.s + this.P[this.n & 255] & 0xFF];
            byte var7 = this.P[this.P[this.P[this.s & 255] & 255] + 1 & 0xFF];
            byte var8 = this.P[this.n & 255];
            this.P[this.n & 255] = this.P[this.s & 255];
            this.P[this.s & 255] = var8;
            this.n = (byte)(this.n + 1 & 0xFF);
            var4[var6 + var5] = (byte)(var1[var6 + var2] ^ var7);
         }

         return var3;
      }
   }

   @Override
   public void reset() {
      this.initKey(this.workingKey, this.workingIV);
   }

   @Override
   public byte returnByte(byte var1) {
      this.s = this.P[this.s + this.P[this.n & 255] & 0xFF];
      byte var2 = this.P[this.P[this.P[this.s & 255] & 255] + 1 & 0xFF];
      byte var3 = this.P[this.n & 255];
      this.P[this.n & 255] = this.P[this.s & 255];
      this.P[this.s & 255] = var3;
      this.n = (byte)(this.n + 1 & 0xFF);
      return (byte)(var1 ^ var2);
   }
}
