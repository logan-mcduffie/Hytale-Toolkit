package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.util.Pack;

class Tape {
   byte[][] tapes;
   int pos;
   int nTapes;
   private PicnicEngine engine;

   public Tape(PicnicEngine var1) {
      this.engine = var1;
      this.tapes = new byte[var1.numMPCParties][2 * var1.andSizeBytes];
      this.pos = 0;
      this.nTapes = var1.numMPCParties;
   }

   protected void setAuxBits(byte[] var1) {
      int var2 = this.engine.numMPCParties - 1;
      int var3 = 0;
      int var4 = this.engine.stateSizeBits;

      for (int var5 = 0; var5 < this.engine.numRounds; var5++) {
         for (int var6 = 0; var6 < var4; var6++) {
            Utils.setBit(this.tapes[var2], var4 + var4 * 2 * var5 + var6, Utils.getBit(var1, var3++));
         }
      }
   }

   protected void computeAuxTape(byte[] var1) {
      int[] var2 = new int[16];
      int[] var3 = new int[16];
      int[] var4 = new int[16];
      int[] var5 = new int[16];
      int[] var6 = new int[16];
      var6[this.engine.stateSizeWords - 1] = 0;
      this.tapesToParityBits(var6, this.engine.stateSizeBits);
      KMatricesWithPointer var7 = this.engine.lowmcConstants.KMatrixInv(this.engine);
      this.engine.matrix_mul(var5, var6, var7.getData(), var7.getMatrixPointer());
      if (var1 != null) {
         Pack.intToLittleEndian(var5, 0, this.engine.stateSizeWords, var1, 0);
      }

      for (int var8 = this.engine.numRounds; var8 > 0; var8--) {
         var7 = this.engine.lowmcConstants.KMatrix(this.engine, var8);
         this.engine.matrix_mul(var2, var5, var7.getData(), var7.getMatrixPointer());
         this.engine.xor_array(var3, var3, var2, 0);
         var7 = this.engine.lowmcConstants.LMatrixInv(this.engine, var8 - 1);
         this.engine.matrix_mul(var4, var3, var7.getData(), var7.getMatrixPointer());
         if (var8 == 1) {
            System.arraycopy(var6, 0, var3, 0, var6.length);
         } else {
            this.pos = this.engine.stateSizeBits * 2 * (var8 - 1);
            this.tapesToParityBits(var3, this.engine.stateSizeBits);
         }

         this.pos = this.engine.stateSizeBits * 2 * (var8 - 1) + this.engine.stateSizeBits;
         this.engine.aux_mpc_sbox(var3, var4, this);
      }

      this.pos = 0;
   }

   private void tapesToParityBits(int[] var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         Utils.setBitInWordArray(var1, var3, Utils.parity16(this.tapesToWord()));
      }
   }

   protected int tapesToWord() {
      int var1 = 0;
      int var2 = this.pos >>> 3;
      int var3 = this.pos & 7 ^ 7;
      int var4 = 1 << var3;
      var1 |= (this.tapes[0][var2] & var4) << 7;
      var1 |= (this.tapes[1][var2] & var4) << 6;
      var1 |= (this.tapes[2][var2] & var4) << 5;
      var1 |= (this.tapes[3][var2] & var4) << 4;
      var1 |= (this.tapes[4][var2] & var4) << 3;
      var1 |= (this.tapes[5][var2] & var4) << 2;
      var1 |= (this.tapes[6][var2] & var4) << 1;
      var1 |= (this.tapes[7][var2] & var4) << 0;
      var1 |= (this.tapes[8][var2] & var4) << 15;
      var1 |= (this.tapes[9][var2] & var4) << 14;
      var1 |= (this.tapes[10][var2] & var4) << 13;
      var1 |= (this.tapes[11][var2] & var4) << 12;
      var1 |= (this.tapes[12][var2] & var4) << 11;
      var1 |= (this.tapes[13][var2] & var4) << 10;
      var1 |= (this.tapes[14][var2] & var4) << 9;
      var1 |= (this.tapes[15][var2] & var4) << 8;
      this.pos++;
      return var1 >>> var3;
   }
}
