package org.bouncycastle.pqc.crypto.sphincs;

class Tree {
   static void l_tree(HashFunctions var0, byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6) {
      int var7 = 67;
      boolean var9 = false;

      for (int var8 = 0; var8 < 7; var8++) {
         for (int var10 = 0; var10 < var7 >>> 1; var10++) {
            var0.hash_2n_n_mask(var3, var4 + var10 * 32, var3, var4 + var10 * 2 * 32, var5, var6 + var8 * 2 * 32);
         }

         if ((var7 & 1) != 0) {
            System.arraycopy(var3, var4 + (var7 - 1) * 32, var3, var4 + (var7 >>> 1) * 32, 32);
            var7 = (var7 >>> 1) + 1;
         } else {
            var7 >>>= 1;
         }
      }

      System.arraycopy(var3, var4, var1, var2, 32);
   }

   static void treehash(HashFunctions var0, byte[] var1, int var2, int var3, byte[] var4, Tree.leafaddr var5, byte[] var6, int var7) {
      Tree.leafaddr var8 = new Tree.leafaddr(var5);
      byte[] var11 = new byte[(var3 + 1) * 32];
      int[] var12 = new int[var3 + 1];
      int var13 = 0;

      for (int var9 = (int)(var8.subleaf + (1 << var3)); var8.subleaf < var9; var8.subleaf++) {
         gen_leaf_wots(var0, var11, var13 * 32, var6, var7, var4, var8);
         var12[var13] = 0;
         var13++;

         while (var13 > 1 && var12[var13 - 1] == var12[var13 - 2]) {
            int var14 = 2 * (var12[var13 - 1] + 7) * 32;
            var0.hash_2n_n_mask(var11, (var13 - 2) * 32, var11, (var13 - 2) * 32, var6, var7 + var14);
            var12[var13 - 2]++;
            var13--;
         }
      }

      for (int var10 = 0; var10 < 32; var10++) {
         var1[var2 + var10] = var11[var10];
      }
   }

   static void gen_leaf_wots(HashFunctions var0, byte[] var1, int var2, byte[] var3, int var4, byte[] var5, Tree.leafaddr var6) {
      byte[] var7 = new byte[32];
      byte[] var8 = new byte[2144];
      Wots var9 = new Wots();
      Seed.get_seed(var0, var7, 0, var5, var6);
      var9.wots_pkgen(var0, var8, 0, var7, 0, var3, var4);
      l_tree(var0, var1, var2, var8, 0, var3, var4);
   }

   static class leafaddr {
      int level;
      long subtree;
      long subleaf;

      public leafaddr() {
      }

      public leafaddr(Tree.leafaddr var1) {
         this.level = var1.level;
         this.subtree = var1.subtree;
         this.subleaf = var1.subleaf;
      }
   }
}
