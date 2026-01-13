package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class VMPCMac implements Mac {
   private byte g;
   private byte n = 0;
   private byte[] P = null;
   private byte s = 0;
   private byte[] T;
   private byte[] workingIV;
   private byte[] workingKey;
   private byte x1;
   private byte x2;
   private byte x3;
   private byte x4;

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      for (int var3 = 1; var3 < 25; var3++) {
         this.s = this.P[this.s + this.P[this.n & 255] & 0xFF];
         this.x4 = this.P[this.x4 + this.x3 + var3 & 0xFF];
         this.x3 = this.P[this.x3 + this.x2 + var3 & 0xFF];
         this.x2 = this.P[this.x2 + this.x1 + var3 & 0xFF];
         this.x1 = this.P[this.x1 + this.s + var3 & 0xFF];
         this.T[this.g & 31] = (byte)(this.T[this.g & 31] ^ this.x1);
         this.T[this.g + 1 & 31] = (byte)(this.T[this.g + 1 & 31] ^ this.x2);
         this.T[this.g + 2 & 31] = (byte)(this.T[this.g + 2 & 31] ^ this.x3);
         this.T[this.g + 3 & 31] = (byte)(this.T[this.g + 3 & 31] ^ this.x4);
         this.g = (byte)(this.g + 4 & 31);
         byte var4 = this.P[this.n & 255];
         this.P[this.n & 255] = this.P[this.s & 255];
         this.P[this.s & 255] = var4;
         this.n = (byte)(this.n + 1 & 0xFF);
      }

      for (int var6 = 0; var6 < 768; var6++) {
         this.s = this.P[this.s + this.P[var6 & 0xFF] + this.T[var6 & 31] & 0xFF];
         byte var8 = this.P[var6 & 0xFF];
         this.P[var6 & 0xFF] = this.P[this.s & 255];
         this.P[this.s & 255] = var8;
      }

      byte[] var7 = new byte[20];

      for (int var9 = 0; var9 < 20; var9++) {
         this.s = this.P[this.s + this.P[var9 & 0xFF] & 0xFF];
         var7[var9] = this.P[this.P[this.P[this.s & 255] & 255] + 1 & 0xFF];
         byte var5 = this.P[var9 & 0xFF];
         this.P[var9 & 0xFF] = this.P[this.s & 255];
         this.P[this.s & 255] = var5;
      }

      System.arraycopy(var7, 0, var1, var2, var7.length);
      this.reset();
      return var7.length;
   }

   @Override
   public String getAlgorithmName() {
      return "VMPC-MAC";
   }

   @Override
   public int getMacSize() {
      return 20;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (!(var1 instanceof ParametersWithIV)) {
         throw new IllegalArgumentException("VMPC-MAC Init parameters must include an IV");
      } else {
         ParametersWithIV var2 = (ParametersWithIV)var1;
         KeyParameter var3 = (KeyParameter)var2.getParameters();
         if (!(var2.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include a key");
         } else {
            this.workingIV = var2.getIV();
            if (this.workingIV != null && this.workingIV.length >= 1 && this.workingIV.length <= 768) {
               this.workingKey = var3.getKey();
               this.reset();
            } else {
               throw new IllegalArgumentException("VMPC-MAC requires 1 to 768 bytes of IV");
            }
         }
      }
   }

   private void initKey(byte[] var1, byte[] var2) {
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
   public void reset() {
      this.initKey(this.workingKey, this.workingIV);
      this.g = this.x1 = this.x2 = this.x3 = this.x4 = this.n = 0;
      this.T = new byte[32];

      for (int var1 = 0; var1 < 32; var1++) {
         this.T[var1] = 0;
      }
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      this.s = this.P[this.s + this.P[this.n & 255] & 0xFF];
      byte var2 = (byte)(var1 ^ this.P[this.P[this.P[this.s & 255] & 255] + 1 & 0xFF]);
      this.x4 = this.P[this.x4 + this.x3 & 0xFF];
      this.x3 = this.P[this.x3 + this.x2 & 0xFF];
      this.x2 = this.P[this.x2 + this.x1 & 0xFF];
      this.x1 = this.P[this.x1 + this.s + var2 & 0xFF];
      this.T[this.g & 31] = (byte)(this.T[this.g & 31] ^ this.x1);
      this.T[this.g + 1 & 31] = (byte)(this.T[this.g + 1 & 31] ^ this.x2);
      this.T[this.g + 2 & 31] = (byte)(this.T[this.g + 2 & 31] ^ this.x3);
      this.T[this.g + 3 & 31] = (byte)(this.T[this.g + 3 & 31] ^ this.x4);
      this.g = (byte)(this.g + 4 & 31);
      byte var3 = this.P[this.n & 255];
      this.P[this.n & 255] = this.P[this.s & 255];
      this.P[this.s & 255] = var3;
      this.n = (byte)(this.n + 1 & 0xFF);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else {
         for (int var4 = 0; var4 < var3; var4++) {
            this.update(var1[var2 + var4]);
         }
      }
   }
}
