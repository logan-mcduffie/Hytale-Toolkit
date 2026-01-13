package org.bouncycastle.pqc.crypto.sphincsplus;

import java.util.LinkedList;
import org.bouncycastle.util.Arrays;

class Fors {
   SPHINCSPlusEngine engine;

   public Fors(SPHINCSPlusEngine var1) {
      this.engine = var1;
   }

   byte[] treehash(byte[] var1, int var2, int var3, byte[] var4, ADRS var5) {
      if (var2 >>> var3 << var3 != var2) {
         return null;
      } else {
         LinkedList var6 = new LinkedList();
         ADRS var7 = new ADRS(var5);

         for (int var8 = 0; var8 < 1 << var3; var8++) {
            var7.setTypeAndClear(6);
            var7.setKeyPairAddress(var5.getKeyPairAddress());
            var7.setTreeHeight(0);
            var7.setTreeIndex(var2 + var8);
            byte[] var9 = this.engine.PRF(var4, var1, var7);
            var7.changeType(3);
            byte[] var10 = this.engine.F(var4, var7, var9);
            var7.setTreeHeight(1);
            int var11 = 1;
            int var12 = var2 + var8;

            while (!var6.isEmpty() && ((NodeEntry)var6.get(0)).nodeHeight == var11) {
               var12 = (var12 - 1) / 2;
               var7.setTreeIndex(var12);
               NodeEntry var13 = (NodeEntry)var6.remove(0);
               var10 = this.engine.H(var4, var7, var13.nodeValue, var10);
               var7.setTreeHeight(++var11);
            }

            var6.add(0, new NodeEntry(var10, var11));
         }

         return ((NodeEntry)var6.get(0)).nodeValue;
      }
   }

   public SIG_FORS[] sign(byte[] var1, byte[] var2, byte[] var3, ADRS var4) {
      ADRS var5 = new ADRS(var4);
      int[] var6 = message_to_idxs(var1, this.engine.K, this.engine.A);
      SIG_FORS[] var7 = new SIG_FORS[this.engine.K];
      int var8 = this.engine.T;

      for (int var9 = 0; var9 < this.engine.K; var9++) {
         int var10 = var6[var9];
         var5.setTypeAndClear(6);
         var5.setKeyPairAddress(var4.getKeyPairAddress());
         var5.setTreeHeight(0);
         var5.setTreeIndex(var9 * var8 + var10);
         byte[] var11 = this.engine.PRF(var3, var2, var5);
         var5.changeType(3);
         byte[][] var12 = new byte[this.engine.A][];

         for (int var13 = 0; var13 < this.engine.A; var13++) {
            int var14 = var10 / (1 << var13) ^ 1;
            var12[var13] = this.treehash(var2, var9 * var8 + var14 * (1 << var13), var13, var3, var5);
         }

         var7[var9] = new SIG_FORS(var11, var12);
      }

      return var7;
   }

   public byte[] pkFromSig(SIG_FORS[] var1, byte[] var2, byte[] var3, ADRS var4) {
      byte[][] var5 = new byte[2][];
      byte[][] var6 = new byte[this.engine.K][];
      int var7 = this.engine.T;
      int[] var8 = message_to_idxs(var2, this.engine.K, this.engine.A);

      for (int var9 = 0; var9 < this.engine.K; var9++) {
         int var10 = var8[var9];
         byte[] var11 = var1[var9].getSK();
         var4.setTreeHeight(0);
         var4.setTreeIndex(var9 * var7 + var10);
         var5[0] = this.engine.F(var3, var4, var11);
         byte[][] var12 = var1[var9].getAuthPath();
         var4.setTreeIndex(var9 * var7 + var10);

         for (int var13 = 0; var13 < this.engine.A; var13++) {
            var4.setTreeHeight(var13 + 1);
            if (var10 / (1 << var13) % 2 == 0) {
               var4.setTreeIndex(var4.getTreeIndex() / 2);
               var5[1] = this.engine.H(var3, var4, var5[0], var12[var13]);
            } else {
               var4.setTreeIndex((var4.getTreeIndex() - 1) / 2);
               var5[1] = this.engine.H(var3, var4, var12[var13], var5[0]);
            }

            var5[0] = var5[1];
         }

         var6[var9] = var5[0];
      }

      ADRS var14 = new ADRS(var4);
      var14.setTypeAndClear(4);
      var14.setKeyPairAddress(var4.getKeyPairAddress());
      return this.engine.T_l(var3, var14, Arrays.concatenate(var6));
   }

   static int[] message_to_idxs(byte[] var0, int var1, int var2) {
      int var3 = 0;
      int[] var4 = new int[var1];

      for (int var5 = 0; var5 < var1; var5++) {
         var4[var5] = 0;

         for (int var6 = 0; var6 < var2; var6++) {
            var4[var5] ^= (var0[var3 >> 3] >> (var3 & 7) & 1) << var6;
            var3++;
         }
      }

      return var4;
   }
}
