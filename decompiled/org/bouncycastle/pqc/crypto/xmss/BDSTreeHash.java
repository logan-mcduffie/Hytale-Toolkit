package org.bouncycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.Stack;

class BDSTreeHash implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private XMSSNode tailNode;
   private final int initialHeight;
   private int height;
   private int nextIndex;
   private boolean initialized;
   private boolean finished;

   BDSTreeHash(int var1) {
      this.initialHeight = var1;
      this.initialized = false;
      this.finished = false;
   }

   void initialize(int var1) {
      this.tailNode = null;
      this.height = this.initialHeight;
      this.nextIndex = var1;
      this.initialized = true;
      this.finished = false;
   }

   void update(Stack<XMSSNode> var1, WOTSPlus var2, byte[] var3, byte[] var4, OTSHashAddress var5) {
      if (var5 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else if (!this.finished && this.initialized) {
         var5 = (OTSHashAddress)new OTSHashAddress.Builder()
            .withLayerAddress(var5.getLayerAddress())
            .withTreeAddress(var5.getTreeAddress())
            .withOTSAddress(this.nextIndex)
            .withChainAddress(var5.getChainAddress())
            .withHashAddress(var5.getHashAddress())
            .withKeyAndMask(var5.getKeyAndMask())
            .build();
         LTreeAddress var6 = (LTreeAddress)new LTreeAddress.Builder()
            .withLayerAddress(var5.getLayerAddress())
            .withTreeAddress(var5.getTreeAddress())
            .withLTreeAddress(this.nextIndex)
            .build();
         HashTreeAddress var7 = (HashTreeAddress)new HashTreeAddress.Builder()
            .withLayerAddress(var5.getLayerAddress())
            .withTreeAddress(var5.getTreeAddress())
            .withTreeIndex(this.nextIndex)
            .build();
         var2.importKeys(var2.getWOTSPlusSecretKey(var4, var5), var3);
         WOTSPlusPublicKeyParameters var8 = var2.getPublicKey(var5);
         XMSSNode var9 = XMSSNodeUtil.lTree(var2, var8, var6);

         while (!var1.isEmpty() && ((XMSSNode)var1.peek()).getHeight() == var9.getHeight() && ((XMSSNode)var1.peek()).getHeight() != this.initialHeight) {
            var7 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withTreeHeight(var7.getTreeHeight())
               .withTreeIndex((var7.getTreeIndex() - 1) / 2)
               .withKeyAndMask(var7.getKeyAndMask())
               .build();
            var9 = XMSSNodeUtil.randomizeHash(var2, (XMSSNode)var1.pop(), var9, var7);
            var9 = new XMSSNode(var9.getHeight() + 1, var9.getValue());
            var7 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withTreeHeight(var7.getTreeHeight() + 1)
               .withTreeIndex(var7.getTreeIndex())
               .withKeyAndMask(var7.getKeyAndMask())
               .build();
         }

         if (this.tailNode == null) {
            this.tailNode = var9;
         } else if (this.tailNode.getHeight() == var9.getHeight()) {
            var7 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withTreeHeight(var7.getTreeHeight())
               .withTreeIndex((var7.getTreeIndex() - 1) / 2)
               .withKeyAndMask(var7.getKeyAndMask())
               .build();
            XMSSNode var15 = XMSSNodeUtil.randomizeHash(var2, this.tailNode, var9, var7);
            var9 = new XMSSNode(this.tailNode.getHeight() + 1, var15.getValue());
            this.tailNode = var9;
            var7 = (HashTreeAddress)new HashTreeAddress.Builder()
               .withLayerAddress(var7.getLayerAddress())
               .withTreeAddress(var7.getTreeAddress())
               .withTreeHeight(var7.getTreeHeight() + 1)
               .withTreeIndex(var7.getTreeIndex())
               .withKeyAndMask(var7.getKeyAndMask())
               .build();
         } else {
            var1.push(var9);
         }

         if (this.tailNode.getHeight() == this.initialHeight) {
            this.finished = true;
         } else {
            this.height = var9.getHeight();
            this.nextIndex++;
         }
      } else {
         throw new IllegalStateException("finished or not initialized");
      }
   }

   int getHeight() {
      return this.initialized && !this.finished ? this.height : Integer.MAX_VALUE;
   }

   int getIndexLeaf() {
      return this.nextIndex;
   }

   void setNode(XMSSNode var1) {
      this.tailNode = var1;
      this.height = var1.getHeight();
      if (this.height == this.initialHeight) {
         this.finished = true;
      }
   }

   boolean isFinished() {
      return this.finished;
   }

   boolean isInitialized() {
      return this.initialized;
   }

   public XMSSNode getTailNode() {
      return this.tailNode;
   }

   protected BDSTreeHash clone() {
      BDSTreeHash var1 = new BDSTreeHash(this.initialHeight);
      var1.tailNode = this.tailNode;
      var1.height = this.height;
      var1.nextIndex = this.nextIndex;
      var1.initialized = this.initialized;
      var1.finished = this.finished;
      return var1;
   }
}
