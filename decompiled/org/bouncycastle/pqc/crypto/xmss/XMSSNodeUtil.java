package org.bouncycastle.pqc.crypto.xmss;

class XMSSNodeUtil {
   static XMSSNode lTree(WOTSPlus var0, WOTSPlusPublicKeyParameters var1, LTreeAddress var2) {
      if (var1 == null) {
         throw new NullPointerException("publicKey == null");
      } else if (var2 == null) {
         throw new NullPointerException("address == null");
      } else {
         int var3 = var0.getParams().getLen();
         byte[][] var4 = var1.toByteArray();
         XMSSNode[] var5 = new XMSSNode[var4.length];

         for (int var6 = 0; var6 < var4.length; var6++) {
            var5[var6] = new XMSSNode(0, var4[var6]);
         }

         for (LTreeAddress var7 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var2.getLayerAddress())
               .withTreeAddress(var2.getTreeAddress())
               .withLTreeAddress(var2.getLTreeAddress())
               .withTreeHeight(0)
               .withTreeIndex(var2.getTreeIndex())
               .withKeyAndMask(var2.getKeyAndMask())
               .build();
            var3 > 1;
            var7 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withLTreeAddress(var7.getLTreeAddress())
               .withTreeHeight(var7.getTreeHeight() + 1)
               .withTreeIndex(var7.getTreeIndex())
               .withKeyAndMask(var7.getKeyAndMask())
               .build()
         ) {
            for (int var8 = 0; var8 < (int)Math.floor(var3 / 2); var8++) {
               var7 = (LTreeAddress)new LTreeAddress.Builder()
                  .withLayerAddress(var7.getLayerAddress())
                  .withTreeAddress(var7.getTreeAddress())
                  .withLTreeAddress(var7.getLTreeAddress())
                  .withTreeHeight(var7.getTreeHeight())
                  .withTreeIndex(var8)
                  .withKeyAndMask(var7.getKeyAndMask())
                  .build();
               var5[var8] = randomizeHash(var0, var5[2 * var8], var5[2 * var8 + 1], var7);
            }

            if (var3 % 2 == 1) {
               var5[(int)Math.floor(var3 / 2)] = var5[var3 - 1];
            }

            var3 = (int)Math.ceil(var3 / 2.0);
         }

         return var5[0];
      }
   }

   static XMSSNode randomizeHash(WOTSPlus var0, XMSSNode var1, XMSSNode var2, XMSSAddress var3) {
      if (var1 == null) {
         throw new NullPointerException("left == null");
      } else if (var2 == null) {
         throw new NullPointerException("right == null");
      } else if (var1.getHeight() != var2.getHeight()) {
         throw new IllegalStateException("height of both nodes must be equal");
      } else if (var3 == null) {
         throw new NullPointerException("address == null");
      } else {
         byte[] var4 = var0.getPublicSeed();
         if (var3 instanceof LTreeAddress) {
            LTreeAddress var5 = (LTreeAddress)var3;
            var3 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var5.getLayerAddress())
               .withTreeAddress(var5.getTreeAddress())
               .withLTreeAddress(var5.getLTreeAddress())
               .withTreeHeight(var5.getTreeHeight())
               .withTreeIndex(var5.getTreeIndex())
               .withKeyAndMask(0)
               .build();
         } else if (var3 instanceof HashTreeAddress) {
            HashTreeAddress var11 = (HashTreeAddress)var3;
            var3 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var11.getLayerAddress())
               .withTreeAddress(var11.getTreeAddress())
               .withTreeHeight(var11.getTreeHeight())
               .withTreeIndex(var11.getTreeIndex())
               .withKeyAndMask(0)
               .build();
         }

         byte[] var12 = var0.getKhf().PRF(var4, ((XMSSAddress)var3).toByteArray());
         if (var3 instanceof LTreeAddress) {
            LTreeAddress var6 = (LTreeAddress)var3;
            var3 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var6.getLayerAddress())
               .withTreeAddress(var6.getTreeAddress())
               .withLTreeAddress(var6.getLTreeAddress())
               .withTreeHeight(var6.getTreeHeight())
               .withTreeIndex(var6.getTreeIndex())
               .withKeyAndMask(1)
               .build();
         } else if (var3 instanceof HashTreeAddress) {
            HashTreeAddress var13 = (HashTreeAddress)var3;
            var3 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var13.getLayerAddress())
               .withTreeAddress(var13.getTreeAddress())
               .withTreeHeight(var13.getTreeHeight())
               .withTreeIndex(var13.getTreeIndex())
               .withKeyAndMask(1)
               .build();
         }

         byte[] var14 = var0.getKhf().PRF(var4, ((XMSSAddress)var3).toByteArray());
         if (var3 instanceof LTreeAddress) {
            LTreeAddress var7 = (LTreeAddress)var3;
            var3 = (LTreeAddress)new LTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withLTreeAddress(var7.getLTreeAddress())
               .withTreeHeight(var7.getTreeHeight())
               .withTreeIndex(var7.getTreeIndex())
               .withKeyAndMask(2)
               .build();
         } else if (var3 instanceof HashTreeAddress) {
            HashTreeAddress var15 = (HashTreeAddress)var3;
            var3 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var15.getLayerAddress())
               .withTreeAddress(var15.getTreeAddress())
               .withTreeHeight(var15.getTreeHeight())
               .withTreeIndex(var15.getTreeIndex())
               .withKeyAndMask(2)
               .build();
         }

         byte[] var16 = var0.getKhf().PRF(var4, ((XMSSAddress)var3).toByteArray());
         int var8 = var0.getParams().getTreeDigestSize();
         byte[] var9 = new byte[2 * var8];

         for (int var10 = 0; var10 < var8; var10++) {
            var9[var10] = (byte)(var1.getValue()[var10] ^ var14[var10]);
         }

         for (int var17 = 0; var17 < var8; var17++) {
            var9[var17 + var8] = (byte)(var2.getValue()[var17] ^ var16[var17]);
         }

         byte[] var18 = var0.getKhf().H(var12, var9);
         return new XMSSNode(var1.getHeight(), var18);
      }
   }
}
