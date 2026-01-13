package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Grain128Engine implements StreamCipher {
   private static final int STATE_SIZE = 4;
   private byte[] workingKey;
   private byte[] workingIV;
   private byte[] out;
   private int[] lfsr;
   private int[] nfsr;
   private int output;
   private int index = 4;
   private boolean initialised = false;

   @Override
   public String getAlgorithmName() {
      return "Grain-128";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (!(var2 instanceof ParametersWithIV)) {
         throw new IllegalArgumentException("Grain-128 Init parameters must include an IV");
      } else {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4 == null || var4.length != 12) {
            throw new IllegalArgumentException("Grain-128 requires exactly 12 bytes of IV");
         } else if (!(var3.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("Grain-128 init parameters must include a key");
         } else {
            KeyParameter var5 = (KeyParameter)var3.getParameters();
            byte[] var6 = var5.getKey();
            if (var6.length != 16) {
               throw new IllegalArgumentException("Grain-128 key must be 128 bits long");
            } else {
               CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
               this.workingIV = new byte[var6.length];
               this.workingKey = new byte[var6.length];
               this.lfsr = new int[4];
               this.nfsr = new int[4];
               this.out = new byte[4];
               System.arraycopy(var4, 0, this.workingIV, 0, var4.length);
               System.arraycopy(var6, 0, this.workingKey, 0, var6.length);
               this.reset();
            }
         }
      }
   }

   private void initGrain() {
      for (int var1 = 0; var1 < 8; var1++) {
         this.output = this.getOutput();
         this.nfsr = this.shift(this.nfsr, this.getOutputNFSR() ^ this.lfsr[0] ^ this.output);
         this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() ^ this.output);
      }

      this.initialised = true;
   }

   private int getOutputNFSR() {
      int var1 = this.nfsr[0];
      int var2 = this.nfsr[0] >>> 3 | this.nfsr[1] << 29;
      int var3 = this.nfsr[0] >>> 11 | this.nfsr[1] << 21;
      int var4 = this.nfsr[0] >>> 13 | this.nfsr[1] << 19;
      int var5 = this.nfsr[0] >>> 17 | this.nfsr[1] << 15;
      int var6 = this.nfsr[0] >>> 18 | this.nfsr[1] << 14;
      int var7 = this.nfsr[0] >>> 26 | this.nfsr[1] << 6;
      int var8 = this.nfsr[0] >>> 27 | this.nfsr[1] << 5;
      int var9 = this.nfsr[1] >>> 8 | this.nfsr[2] << 24;
      int var10 = this.nfsr[1] >>> 16 | this.nfsr[2] << 16;
      int var11 = this.nfsr[1] >>> 24 | this.nfsr[2] << 8;
      int var12 = this.nfsr[1] >>> 27 | this.nfsr[2] << 5;
      int var13 = this.nfsr[1] >>> 29 | this.nfsr[2] << 3;
      int var14 = this.nfsr[2] >>> 1 | this.nfsr[3] << 31;
      int var15 = this.nfsr[2] >>> 3 | this.nfsr[3] << 29;
      int var16 = this.nfsr[2] >>> 4 | this.nfsr[3] << 28;
      int var17 = this.nfsr[2] >>> 20 | this.nfsr[3] << 12;
      int var18 = this.nfsr[2] >>> 27 | this.nfsr[3] << 5;
      int var19 = this.nfsr[3];
      return var1 ^ var7 ^ var11 ^ var18 ^ var19 ^ var2 & var15 ^ var3 & var4 ^ var5 & var6 ^ var8 & var12 ^ var9 & var10 ^ var13 & var14 ^ var16 & var17;
   }

   private int getOutputLFSR() {
      int var1 = this.lfsr[0];
      int var2 = this.lfsr[0] >>> 7 | this.lfsr[1] << 25;
      int var3 = this.lfsr[1] >>> 6 | this.lfsr[2] << 26;
      int var4 = this.lfsr[2] >>> 6 | this.lfsr[3] << 26;
      int var5 = this.lfsr[2] >>> 17 | this.lfsr[3] << 15;
      int var6 = this.lfsr[3];
      return var1 ^ var2 ^ var3 ^ var4 ^ var5 ^ var6;
   }

   private int getOutput() {
      int var1 = this.nfsr[0] >>> 2 | this.nfsr[1] << 30;
      int var2 = this.nfsr[0] >>> 12 | this.nfsr[1] << 20;
      int var3 = this.nfsr[0] >>> 15 | this.nfsr[1] << 17;
      int var4 = this.nfsr[1] >>> 4 | this.nfsr[2] << 28;
      int var5 = this.nfsr[1] >>> 13 | this.nfsr[2] << 19;
      int var6 = this.nfsr[2];
      int var7 = this.nfsr[2] >>> 9 | this.nfsr[3] << 23;
      int var8 = this.nfsr[2] >>> 25 | this.nfsr[3] << 7;
      int var9 = this.nfsr[2] >>> 31 | this.nfsr[3] << 1;
      int var10 = this.lfsr[0] >>> 8 | this.lfsr[1] << 24;
      int var11 = this.lfsr[0] >>> 13 | this.lfsr[1] << 19;
      int var12 = this.lfsr[0] >>> 20 | this.lfsr[1] << 12;
      int var13 = this.lfsr[1] >>> 10 | this.lfsr[2] << 22;
      int var14 = this.lfsr[1] >>> 28 | this.lfsr[2] << 4;
      int var15 = this.lfsr[2] >>> 15 | this.lfsr[3] << 17;
      int var16 = this.lfsr[2] >>> 29 | this.lfsr[3] << 3;
      int var17 = this.lfsr[2] >>> 31 | this.lfsr[3] << 1;
      return var2 & var10 ^ var11 & var12 ^ var9 & var13 ^ var14 & var15 ^ var2 & var9 & var17 ^ var16 ^ var1 ^ var3 ^ var4 ^ var5 ^ var6 ^ var7 ^ var8;
   }

   private int[] shift(int[] var1, int var2) {
      var1[0] = var1[1];
      var1[1] = var1[2];
      var1[2] = var1[3];
      var1[3] = var2;
      return var1;
   }

   private void setKey(byte[] var1, byte[] var2) {
      var2[12] = -1;
      var2[13] = -1;
      var2[14] = -1;
      var2[15] = -1;
      this.workingKey = var1;
      this.workingIV = var2;
      byte var3 = 0;

      for (int var4 = 0; var4 < this.nfsr.length; var4++) {
         this.nfsr[var4] = this.workingKey[var3 + 3] << 24
            | this.workingKey[var3 + 2] << 16 & 0xFF0000
            | this.workingKey[var3 + 1] << 8 & 0xFF00
            | this.workingKey[var3] & 255;
         this.lfsr[var4] = this.workingIV[var3 + 3] << 24
            | this.workingIV[var3 + 2] << 16 & 0xFF0000
            | this.workingIV[var3 + 1] << 8 & 0xFF00
            | this.workingIV[var3] & 255;
         var3 += 4;
      }
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      if (!this.initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            var4[var5 + var6] = (byte)(var1[var2 + var6] ^ this.getKeyStream());
         }

         return var3;
      }
   }

   @Override
   public void reset() {
      this.index = 4;
      this.setKey(this.workingKey, this.workingIV);
      this.initGrain();
   }

   private void oneRound() {
      this.output = this.getOutput();
      this.out[0] = (byte)this.output;
      this.out[1] = (byte)(this.output >> 8);
      this.out[2] = (byte)(this.output >> 16);
      this.out[3] = (byte)(this.output >> 24);
      this.nfsr = this.shift(this.nfsr, this.getOutputNFSR() ^ this.lfsr[0]);
      this.lfsr = this.shift(this.lfsr, this.getOutputLFSR());
   }

   @Override
   public byte returnByte(byte var1) {
      if (!this.initialised) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else {
         return (byte)(var1 ^ this.getKeyStream());
      }
   }

   private byte getKeyStream() {
      if (this.index > 3) {
         this.oneRound();
         this.index = 0;
      }

      return this.out[this.index++];
   }
}
