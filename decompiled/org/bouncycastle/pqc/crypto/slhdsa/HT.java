package org.bouncycastle.pqc.crypto.slhdsa;

import java.util.LinkedList;
import org.bouncycastle.util.Arrays;

class HT {
   private final byte[] skSeed;
   private final byte[] pkSeed;
   SLHDSAEngine engine;
   WotsPlus wots;
   final byte[] htPubKey;

   public HT(SLHDSAEngine var1, byte[] var2, byte[] var3) {
      this.skSeed = var2;
      this.pkSeed = var3;
      this.engine = var1;
      this.wots = new WotsPlus(var1);
      ADRS var4 = new ADRS();
      var4.setLayerAddress(var1.D - 1);
      var4.setTreeAddress(0L);
      if (var2 != null) {
         this.htPubKey = this.xmss_PKgen(var2, var3, var4);
      } else {
         this.htPubKey = null;
      }
   }

   byte[] sign(byte[] var1, long var2, int var4) {
      ADRS var5 = new ADRS();
      var5.setLayerAddress(0);
      var5.setTreeAddress(var2);
      SIG_XMSS var6 = this.xmss_sign(var1, this.skSeed, var4, this.pkSeed, var5);
      SIG_XMSS[] var7 = new SIG_XMSS[this.engine.D];
      var7[0] = var6;
      var5.setLayerAddress(0);
      var5.setTreeAddress(var2);
      byte[] var8 = this.xmss_pkFromSig(var4, var6, var1, this.pkSeed, var5);

      for (int var9 = 1; var9 < this.engine.D; var9++) {
         var4 = (int)(var2 & (1 << this.engine.H_PRIME) - 1);
         var2 >>>= this.engine.H_PRIME;
         var5.setLayerAddress(var9);
         var5.setTreeAddress(var2);
         var6 = this.xmss_sign(var8, this.skSeed, var4, this.pkSeed, var5);
         var7[var9] = var6;
         if (var9 < this.engine.D - 1) {
            var8 = this.xmss_pkFromSig(var4, var6, var8, this.pkSeed, var5);
         }
      }

      byte[][] var13 = new byte[var7.length][];

      for (int var10 = 0; var10 != var13.length; var10++) {
         var13[var10] = Arrays.concatenate(var7[var10].sig, Arrays.concatenate(var7[var10].auth));
      }

      return Arrays.concatenate(var13);
   }

   byte[] xmss_PKgen(byte[] var1, byte[] var2, ADRS var3) {
      return this.treehash(var1, 0, this.engine.H_PRIME, var2, var3);
   }

   byte[] xmss_pkFromSig(int var1, SIG_XMSS var2, byte[] var3, byte[] var4, ADRS var5) {
      ADRS var6 = new ADRS(var5);
      var6.setTypeAndClear(0);
      var6.setKeyPairAddress(var1);
      byte[] var7 = var2.getWOTSSig();
      byte[][] var8 = var2.getXMSSAUTH();
      byte[] var9 = this.wots.pkFromSig(var7, var3, var4, var6);
      Object var10 = null;
      var6.setTypeAndClear(2);
      var6.setTreeIndex(var1);

      for (int var11 = 0; var11 < this.engine.H_PRIME; var11++) {
         var6.setTreeHeight(var11 + 1);
         if ((var1 & 1 << var11) == 0) {
            var6.setTreeIndex(var6.getTreeIndex() / 2);
            var10 = this.engine.H(var4, var6, var9, var8[var11]);
         } else {
            var6.setTreeIndex((var6.getTreeIndex() - 1) / 2);
            var10 = this.engine.H(var4, var6, var8[var11], var9);
         }

         var9 = (byte[])var10;
      }

      return var9;
   }

   SIG_XMSS xmss_sign(byte[] var1, byte[] var2, int var3, byte[] var4, ADRS var5) {
      byte[][] var6 = new byte[this.engine.H_PRIME][];
      ADRS var7 = new ADRS(var5);
      var7.setTypeAndClear(2);
      var7.setLayerAddress(var5.getLayerAddress());
      var7.setTreeAddress(var5.getTreeAddress());

      for (int var8 = 0; var8 < this.engine.H_PRIME; var8++) {
         int var9 = var3 >>> var8 ^ 1;
         var6[var8] = this.treehash(var2, var9 << var8, var8, var4, var7);
      }

      var7 = new ADRS(var5);
      var7.setTypeAndClear(0);
      var7.setKeyPairAddress(var3);
      byte[] var11 = this.wots.sign(var1, var2, var4, var7);
      return new SIG_XMSS(var11, var6);
   }

   byte[] treehash(byte[] var1, int var2, int var3, byte[] var4, ADRS var5) {
      if (var2 >>> var3 << var3 != var2) {
         return null;
      } else {
         LinkedList var6 = new LinkedList();
         ADRS var7 = new ADRS(var5);

         for (int var8 = 0; var8 < 1 << var3; var8++) {
            var7.setTypeAndClear(0);
            var7.setKeyPairAddress(var2 + var8);
            byte[] var9 = this.wots.pkGen(var1, var4, var7);
            var7.setTypeAndClear(2);
            var7.setTreeHeight(1);
            var7.setTreeIndex(var2 + var8);
            int var10 = 1;
            int var11 = var2 + var8;

            while (!var6.isEmpty() && ((NodeEntry)var6.get(0)).nodeHeight == var10) {
               var11 = (var11 - 1) / 2;
               var7.setTreeIndex(var11);
               NodeEntry var12 = (NodeEntry)var6.remove(0);
               var9 = this.engine.H(var4, var7, var12.nodeValue, var9);
               var7.setTreeHeight(++var10);
            }

            var6.add(0, new NodeEntry(var9, var10));
         }

         return ((NodeEntry)var6.get(0)).nodeValue;
      }
   }

   public boolean verify(byte[] var1, SIG_XMSS[] var2, byte[] var3, long var4, int var6, byte[] var7) {
      ADRS var8 = new ADRS();
      SIG_XMSS var9 = var2[0];
      var8.setLayerAddress(0);
      var8.setTreeAddress(var4);
      byte[] var10 = this.xmss_pkFromSig(var6, var9, var1, var3, var8);

      for (int var11 = 1; var11 < this.engine.D; var11++) {
         var6 = (int)(var4 & (1 << this.engine.H_PRIME) - 1);
         var4 >>>= this.engine.H_PRIME;
         var9 = var2[var11];
         var8.setLayerAddress(var11);
         var8.setTreeAddress(var4);
         var10 = this.xmss_pkFromSig(var6, var9, var10, var3, var8);
      }

      return Arrays.areEqual(var7, var10);
   }
}
