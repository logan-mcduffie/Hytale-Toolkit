package org.bouncycastle.pqc.crypto.xmss;

class XMSSVerifierUtil {
   static XMSSNode getRootNodeFromSignature(WOTSPlus var0, int var1, byte[] var2, XMSSReducedSignature var3, OTSHashAddress var4, int var5) {
      if (var2.length != var0.getParams().getTreeDigestSize()) {
         throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
      } else if (var3 == null) {
         throw new NullPointerException("signature == null");
      } else if (var4 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         LTreeAddress var6 = (LTreeAddress)new LTreeAddress.Builder()
            .withLayerAddress(var4.getLayerAddress())
            .withTreeAddress(var4.getTreeAddress())
            .withLTreeAddress(var4.getOTSAddress())
            .build();
         HashTreeAddress var7 = (HashTreeAddress)new HashTreeAddress.Builder()
            .withLayerAddress(var4.getLayerAddress())
            .withTreeAddress(var4.getTreeAddress())
            .withTreeIndex(var4.getOTSAddress())
            .build();
         WOTSPlusPublicKeyParameters var8 = var0.getPublicKeyFromSignature(var2, var3.getWOTSPlusSignature(), var4);
         XMSSNode[] var9 = new XMSSNode[]{XMSSNodeUtil.lTree(var0, var8, var6), null};

         for (int var10 = 0; var10 < var1; var10++) {
            var7 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withTreeHeight(var10)
               .withTreeIndex(var7.getTreeIndex())
               .withKeyAndMask(var7.getKeyAndMask())
               .build();
            if (Math.floor(var5 / (1 << var10)) % 2.0 == 0.0) {
               var7 = (HashTreeAddress)new HashTreeAddress.Builder()
                  .withLayerAddress(var7.getLayerAddress())
                  .withTreeAddress(var7.getTreeAddress())
                  .withTreeHeight(var7.getTreeHeight())
                  .withTreeIndex(var7.getTreeIndex() / 2)
                  .withKeyAndMask(var7.getKeyAndMask())
                  .build();
               var9[1] = XMSSNodeUtil.randomizeHash(var0, var9[0], var3.getAuthPath().get(var10), var7);
               var9[1] = new XMSSNode(var9[1].getHeight() + 1, var9[1].getValue());
            } else {
               var7 = (HashTreeAddress)new HashTreeAddress.Builder()
                  .withLayerAddress(var7.getLayerAddress())
                  .withTreeAddress(var7.getTreeAddress())
                  .withTreeHeight(var7.getTreeHeight())
                  .withTreeIndex((var7.getTreeIndex() - 1) / 2)
                  .withKeyAndMask(var7.getKeyAndMask())
                  .build();
               var9[1] = XMSSNodeUtil.randomizeHash(var0, var3.getAuthPath().get(var10), var9[0], var7);
               var9[1] = new XMSSNode(var9[1].getHeight() + 1, var9[1].getValue());
            }

            var9[0] = var9[1];
         }

         return var9[0];
      }
   }
}
