package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Grainv1Engine implements StreamCipher {
   private static final int STATE_SIZE = 5;
   private byte[] workingKey;
   private byte[] workingIV;
   private byte[] out;
   private int[] lfsr;
   private int[] nfsr;
   private int output;
   private int index = 2;
   private boolean initialised = false;

   @Override
   public String getAlgorithmName() {
      return "Grain v1";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (!(var2 instanceof ParametersWithIV)) {
         throw new IllegalArgumentException("Grain v1 init parameters must include an IV");
      } else {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4 == null || var4.length != 8) {
            throw new IllegalArgumentException("Grain v1 requires exactly 8 bytes of IV");
         } else if (!(var3.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("Grain v1 init parameters must include a key");
         } else {
            KeyParameter var5 = (KeyParameter)var3.getParameters();
            byte[] var6 = var5.getKey();
            if (var6.length != 10) {
               throw new IllegalArgumentException("Grain v1 key must be 80 bits long");
            } else {
               this.workingIV = new byte[var6.length];
               this.workingKey = new byte[var6.length];
               this.lfsr = new int[5];
               this.nfsr = new int[5];
               this.out = new byte[2];
               System.arraycopy(var4, 0, this.workingIV, 0, var4.length);
               System.arraycopy(var6, 0, this.workingKey, 0, var6.length);
               CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 80, var2, Utils.getPurpose(var1)));
               this.reset();
            }
         }
      }
   }

   private void initGrain() {
      for (int var1 = 0; var1 < 10; var1++) {
         this.output = this.getOutput();
         this.nfsr = this.shift(this.nfsr, this.getOutputNFSR() ^ this.lfsr[0] ^ this.output);
         this.lfsr = this.shift(this.lfsr, this.getOutputLFSR() ^ this.output);
      }

      this.initialised = true;
   }

   private int getOutputNFSR() {
      int var1 = this.nfsr[0];
      int var2 = this.nfsr[0] >>> 9 | this.nfsr[1] << 7;
      int var3 = this.nfsr[0] >>> 14 | this.nfsr[1] << 2;
      int var4 = this.nfsr[0] >>> 15 | this.nfsr[1] << 1;
      int var5 = this.nfsr[1] >>> 5 | this.nfsr[2] << 11;
      int var6 = this.nfsr[1] >>> 12 | this.nfsr[2] << 4;
      int var7 = this.nfsr[2] >>> 1 | this.nfsr[3] << 15;
      int var8 = this.nfsr[2] >>> 5 | this.nfsr[3] << 11;
      int var9 = this.nfsr[2] >>> 13 | this.nfsr[3] << 3;
      int var10 = this.nfsr[3] >>> 4 | this.nfsr[4] << 12;
      int var11 = this.nfsr[3] >>> 12 | this.nfsr[4] << 4;
      int var12 = this.nfsr[3] >>> 14 | this.nfsr[4] << 2;
      int var13 = this.nfsr[3] >>> 15 | this.nfsr[4] << 1;
      return (
            var12
               ^ var11
               ^ var10
               ^ var9
               ^ var8
               ^ var7
               ^ var6
               ^ var5
               ^ var3
               ^ var2
               ^ var1
               ^ var13 & var11
               ^ var8 & var7
               ^ var4 & var2
               ^ var11 & var10 & var9
               ^ var7 & var6 & var5
               ^ var13 & var9 & var6 & var2
               ^ var11 & var10 & var8 & var7
               ^ var13 & var11 & var5 & var4
               ^ var13 & var11 & var10 & var9 & var8
               ^ var7 & var6 & var5 & var4 & var2
               ^ var10 & var9 & var8 & var7 & var6 & var5
         )
         & 65535;
   }

   private int getOutputLFSR() {
      int var1 = this.lfsr[0];
      int var2 = this.lfsr[0] >>> 13 | this.lfsr[1] << 3;
      int var3 = this.lfsr[1] >>> 7 | this.lfsr[2] << 9;
      int var4 = this.lfsr[2] >>> 6 | this.lfsr[3] << 10;
      int var5 = this.lfsr[3] >>> 3 | this.lfsr[4] << 13;
      int var6 = this.lfsr[3] >>> 14 | this.lfsr[4] << 2;
      return (var1 ^ var2 ^ var3 ^ var4 ^ var5 ^ var6) & 65535;
   }

   private int getOutput() {
      int var1 = this.nfsr[0] >>> 1 | this.nfsr[1] << 15;
      int var2 = this.nfsr[0] >>> 2 | this.nfsr[1] << 14;
      int var3 = this.nfsr[0] >>> 4 | this.nfsr[1] << 12;
      int var4 = this.nfsr[0] >>> 10 | this.nfsr[1] << 6;
      int var5 = this.nfsr[1] >>> 15 | this.nfsr[2] << 1;
      int var6 = this.nfsr[2] >>> 11 | this.nfsr[3] << 5;
      int var7 = this.nfsr[3] >>> 8 | this.nfsr[4] << 8;
      int var8 = this.nfsr[3] >>> 15 | this.nfsr[4] << 1;
      int var9 = this.lfsr[0] >>> 3 | this.lfsr[1] << 13;
      int var10 = this.lfsr[1] >>> 9 | this.lfsr[2] << 7;
      int var11 = this.lfsr[2] >>> 14 | this.lfsr[3] << 2;
      int var12 = this.lfsr[4];
      return (
            var10
               ^ var8
               ^ var9 & var12
               ^ var11 & var12
               ^ var12 & var8
               ^ var9 & var10 & var11
               ^ var9 & var11 & var12
               ^ var9 & var11 & var8
               ^ var10 & var11 & var8
               ^ var11 & var12 & var8
               ^ var1
               ^ var2
               ^ var3
               ^ var4
               ^ var5
               ^ var6
               ^ var7
         )
         & 65535;
   }

   private int[] shift(int[] var1, int var2) {
      var1[0] = var1[1];
      var1[1] = var1[2];
      var1[2] = var1[3];
      var1[3] = var1[4];
      var1[4] = var2;
      return var1;
   }

   private void setKey(byte[] var1, byte[] var2) {
      var2[8] = -1;
      var2[9] = -1;
      this.workingKey = var1;
      this.workingIV = var2;
      byte var3 = 0;

      for (int var4 = 0; var4 < this.nfsr.length; var4++) {
         this.nfsr[var4] = (this.workingKey[var3 + 1] << 8 | this.workingKey[var3] & 255) & 65535;
         this.lfsr[var4] = (this.workingIV[var3 + 1] << 8 | this.workingIV[var3] & 255) & 65535;
         var3 += 2;
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
      this.index = 2;
      this.setKey(this.workingKey, this.workingIV);
      this.initGrain();
   }

   private void oneRound() {
      this.output = this.getOutput();
      this.out[0] = (byte)this.output;
      this.out[1] = (byte)(this.output >> 8);
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
      if (this.index > 1) {
         this.oneRound();
         this.index = 0;
      }

      return this.out[this.index++];
   }
}
