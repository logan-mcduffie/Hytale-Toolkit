package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Grain128AEADEngine extends AEADBaseEngine {
   private static final int STATE_SIZE = 4;
   private byte[] workingKey;
   private byte[] workingIV;
   private final int[] lfsr;
   private final int[] nfsr;
   private final int[] authAcc;
   private final int[] authSr;

   public Grain128AEADEngine() {
      this.algorithmName = "Grain-128 AEAD";
      this.KEY_SIZE = 16;
      this.IV_SIZE = 12;
      this.MAC_SIZE = 8;
      this.lfsr = new int[4];
      this.nfsr = new int[4];
      this.authAcc = new int[2];
      this.authSr = new int[2];
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Stream, AEADBaseEngine.DataOperatorType.StreamCipher);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.workingIV = new byte[16];
      this.workingKey = var1;
      System.arraycopy(var2, 0, this.workingIV, 0, this.IV_SIZE);
      this.workingIV[12] = -1;
      this.workingIV[13] = -1;
      this.workingIV[14] = -1;
      this.workingIV[15] = 127;
   }

   private void initGrain(int[] var1) {
      for (int var2 = 0; var2 < 2; var2++) {
         for (int var3 = 0; var3 < 32; var3++) {
            var1[var2] |= this.getByteKeyStream() << var3;
         }
      }
   }

   private int getOutputNFSR() {
      int var1 = this.nfsr[0];
      int var2 = this.nfsr[0] >>> 3;
      int var3 = this.nfsr[0] >>> 11;
      int var4 = this.nfsr[0] >>> 13;
      int var5 = this.nfsr[0] >>> 17;
      int var6 = this.nfsr[0] >>> 18;
      int var7 = this.nfsr[0] >>> 22;
      int var8 = this.nfsr[0] >>> 24;
      int var9 = this.nfsr[0] >>> 25;
      int var10 = this.nfsr[0] >>> 26;
      int var11 = this.nfsr[0] >>> 27;
      int var12 = this.nfsr[1] >>> 8;
      int var13 = this.nfsr[1] >>> 16;
      int var14 = this.nfsr[1] >>> 24;
      int var15 = this.nfsr[1] >>> 27;
      int var16 = this.nfsr[1] >>> 29;
      int var17 = this.nfsr[2] >>> 1;
      int var18 = this.nfsr[2] >>> 3;
      int var19 = this.nfsr[2] >>> 4;
      int var20 = this.nfsr[2] >>> 6;
      int var21 = this.nfsr[2] >>> 14;
      int var22 = this.nfsr[2] >>> 18;
      int var23 = this.nfsr[2] >>> 20;
      int var24 = this.nfsr[2] >>> 24;
      int var25 = this.nfsr[2] >>> 27;
      int var26 = this.nfsr[2] >>> 28;
      int var27 = this.nfsr[2] >>> 29;
      int var28 = this.nfsr[2] >>> 31;
      int var29 = this.nfsr[3];
      return (
            var1
               ^ var10
               ^ var14
               ^ var25
               ^ var29
               ^ var2 & var18
               ^ var3 & var4
               ^ var5 & var6
               ^ var11 & var15
               ^ var12 & var13
               ^ var16 & var17
               ^ var19 & var23
               ^ var7 & var8 & var9
               ^ var20 & var21 & var22
               ^ var24 & var26 & var27 & var28
         )
         & 1;
   }

   private int getOutputLFSR() {
      int var1 = this.lfsr[0];
      int var2 = this.lfsr[0] >>> 7;
      int var3 = this.lfsr[1] >>> 6;
      int var4 = this.lfsr[2] >>> 6;
      int var5 = this.lfsr[2] >>> 17;
      int var6 = this.lfsr[3];
      return (var1 ^ var2 ^ var3 ^ var4 ^ var5 ^ var6) & 1;
   }

   private int getOutput() {
      int var1 = this.nfsr[0] >>> 2;
      int var2 = this.nfsr[0] >>> 12;
      int var3 = this.nfsr[0] >>> 15;
      int var4 = this.nfsr[1] >>> 4;
      int var5 = this.nfsr[1] >>> 13;
      int var6 = this.nfsr[2];
      int var7 = this.nfsr[2] >>> 9;
      int var8 = this.nfsr[2] >>> 25;
      int var9 = this.nfsr[2] >>> 31;
      int var10 = this.lfsr[0] >>> 8;
      int var11 = this.lfsr[0] >>> 13;
      int var12 = this.lfsr[0] >>> 20;
      int var13 = this.lfsr[1] >>> 10;
      int var14 = this.lfsr[1] >>> 28;
      int var15 = this.lfsr[2] >>> 15;
      int var16 = this.lfsr[2] >>> 29;
      int var17 = this.lfsr[2] >>> 30;
      return (var2 & var10 ^ var11 & var12 ^ var9 & var13 ^ var14 & var15 ^ var2 & var9 & var17 ^ var16 ^ var1 ^ var3 ^ var4 ^ var5 ^ var6 ^ var7 ^ var8) & 1;
   }

   private void shift(int[] var1, int var2) {
      var1[0] = var1[0] >>> 1 | var1[1] << 31;
      var1[1] = var1[1] >>> 1 | var1[2] << 31;
      var1[2] = var1[2] >>> 1 | var1[3] << 31;
      var1[3] = var1[3] >>> 1 | var2 << 31;
   }

   private void shift() {
      this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0]) & 1);
      this.shift(this.lfsr, this.getOutputLFSR() & 1);
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      Pack.littleEndianToInt(this.workingKey, 0, this.nfsr);
      Pack.littleEndianToInt(this.workingIV, 0, this.lfsr);
      Arrays.clear(this.authAcc);
      Arrays.clear(this.authSr);

      for (int var3 = 0; var3 < 320; var3++) {
         int var2 = this.getOutput();
         this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0] ^ var2) & 1);
         this.shift(this.lfsr, (this.getOutputLFSR() ^ var2) & 1);
      }

      for (int var6 = 0; var6 < 8; var6++) {
         for (int var4 = 0; var4 < 8; var4++) {
            int var5 = this.getOutput();
            this.shift(this.nfsr, (this.getOutputNFSR() ^ this.lfsr[0] ^ var5 ^ this.workingKey[var6] >> var4) & 1);
            this.shift(this.lfsr, (this.getOutputLFSR() ^ var5 ^ this.workingKey[var6 + 8] >> var4) & 1);
         }
      }

      this.initGrain(this.authAcc);
      this.initGrain(this.authSr);
   }

   private void updateInternalState(int var1) {
      var1 = -var1;
      this.authAcc[0] = this.authAcc[0] ^ this.authSr[0] & var1;
      this.authAcc[1] = this.authAcc[1] ^ this.authSr[1] & var1;
      var1 = this.getByteKeyStream();
      this.authSr[0] = this.authSr[0] >>> 1 | this.authSr[1] << 31;
      this.authSr[1] = this.authSr[1] >>> 1 | var1 << 31;
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      return this.getTotalBytesForUpdate(var1);
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD1(var1);
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      this.authAcc[0] = this.authAcc[0] ^ this.authSr[0];
      this.authAcc[1] = this.authAcc[1] ^ this.authSr[1];
      Pack.intToLittleEndian(this.authAcc, this.mac, 0);
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
   }

   @Override
   protected void processFinalAAD() {
      int var1 = this.aadOperator.getLen();
      byte[] var2 = ((AEADBaseEngine.StreamAADOperator)this.aadOperator).getBytes();
      byte[] var3 = new byte[5];
      int var7;
      if (var1 < 128) {
         var7 = var3.length - 1;
         var3[var7] = (byte)var1;
      } else {
         var7 = var3.length;
         int var5 = var1;

         do {
            var3[--var7] = (byte)var5;
            var5 >>>= 8;
         } while (var5 != 0);

         int var6 = var3.length - var7;
         var3[--var7] = (byte)(128 | var6);
      }

      this.absorbAadData(var3, var7, var3.length - var7);
      this.absorbAadData(var2, 0, var1);
   }

   private void absorbAadData(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         byte var5 = var1[var2 + var4];

         for (int var6 = 0; var6 < 8; var6++) {
            this.shift();
            this.updateInternalState(var5 >> var6 & 1);
         }
      }
   }

   private int getByteKeyStream() {
      int var1 = this.getOutput();
      this.shift();
      return var1;
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.dataOperator.getLen();

      for (int var6 = 0; var6 < var5; var6++) {
         byte var7 = 0;
         byte var8 = var1[var2 + var6];

         for (int var9 = 0; var9 < 8; var9++) {
            int var10 = var8 >> var9 & 1;
            var7 = (byte)(var7 | (var10 ^ this.getByteKeyStream()) << var9);
            this.updateInternalState(var10);
         }

         var3[var4 + var6] = var7;
      }
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.dataOperator.getLen();

      for (int var6 = 0; var6 < var5; var6++) {
         byte var7 = 0;
         byte var8 = var1[var2 + var6];

         for (int var9 = 0; var9 < 8; var9++) {
            var7 = (byte)(var7 | (var8 >> var9 & 1 ^ this.getByteKeyStream()) << var9);
            this.updateInternalState(var7 >> var9 & 1);
         }

         var3[var4 + var6] = var7;
      }
   }
}
