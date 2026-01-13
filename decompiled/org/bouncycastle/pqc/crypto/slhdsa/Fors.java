package org.bouncycastle.pqc.crypto.slhdsa;

import java.math.BigInteger;
import java.util.LinkedList;
import org.bouncycastle.util.Arrays;

class Fors {
   SLHDSAEngine engine;

   public Fors(SLHDSAEngine var1) {
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
      int[] var6 = base2B(var1, this.engine.A, this.engine.K);
      SIG_FORS[] var7 = new SIG_FORS[this.engine.K];

      for (int var8 = 0; var8 < this.engine.K; var8++) {
         int var9 = var6[var8];
         var5.setTypeAndClear(6);
         var5.setKeyPairAddress(var4.getKeyPairAddress());
         var5.setTreeHeight(0);
         var5.setTreeIndex((var8 << this.engine.A) + var9);
         byte[] var10 = this.engine.PRF(var3, var2, var5);
         var5.changeType(3);
         byte[][] var11 = new byte[this.engine.A][];

         for (int var12 = 0; var12 < this.engine.A; var12++) {
            int var13 = var9 >>> var12 ^ 1;
            var11[var12] = this.treehash(var2, (var8 << this.engine.A) + (var13 << var12), var12, var3, var5);
         }

         var7[var8] = new SIG_FORS(var10, var11);
      }

      return var7;
   }

   public byte[] pkFromSig(SIG_FORS[] var1, byte[] var2, byte[] var3, ADRS var4) {
      byte[][] var5 = new byte[2][];
      byte[][] var6 = new byte[this.engine.K][];
      int[] var7 = base2B(var2, this.engine.A, this.engine.K);

      for (int var8 = 0; var8 < this.engine.K; var8++) {
         int var9 = var7[var8];
         byte[] var10 = var1[var8].getSK();
         var4.setTreeHeight(0);
         var4.setTreeIndex((var8 << this.engine.A) + var9);
         var5[0] = this.engine.F(var3, var4, var10);
         byte[][] var11 = var1[var8].getAuthPath();
         var4.setTreeIndex((var8 << this.engine.A) + var9);

         for (int var12 = 0; var12 < this.engine.A; var12++) {
            var4.setTreeHeight(var12 + 1);
            if ((var9 & 1 << var12) == 0) {
               var4.setTreeIndex(var4.getTreeIndex() / 2);
               var5[1] = this.engine.H(var3, var4, var5[0], var11[var12]);
            } else {
               var4.setTreeIndex((var4.getTreeIndex() - 1) / 2);
               var5[1] = this.engine.H(var3, var4, var11[var12], var5[0]);
            }

            var5[0] = var5[1];
         }

         var6[var8] = var5[0];
      }

      ADRS var13 = new ADRS(var4);
      var13.setTypeAndClear(4);
      var13.setKeyPairAddress(var4.getKeyPairAddress());
      return this.engine.T_l(var3, var13, Arrays.concatenate(var6));
   }

   static int[] base2B(byte[] var0, int var1, int var2) {
      int[] var3 = new int[var2];
      int var4 = 0;
      int var5 = 0;
      BigInteger var6 = BigInteger.ZERO;

      for (int var7 = 0; var7 < var2; var7++) {
         while (var5 < var1) {
            var6 = var6.shiftLeft(8).add(BigInteger.valueOf(var0[var4] & 255));
            var4++;
            var5 += 8;
         }

         var5 -= var1;
         var3[var7] = var6.shiftRight(var5).mod(BigInteger.valueOf(2L).pow(var1)).intValue();
      }

      return var3;
   }
}
