package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class LTreeAddress extends XMSSAddress {
   private static final int TYPE = 1;
   private final int lTreeAddress;
   private final int treeHeight;
   private final int treeIndex;

   private LTreeAddress(LTreeAddress.Builder var1) {
      super(var1);
      this.lTreeAddress = var1.lTreeAddress;
      this.treeHeight = var1.treeHeight;
      this.treeIndex = var1.treeIndex;
   }

   @Override
   protected byte[] toByteArray() {
      byte[] var1 = super.toByteArray();
      Pack.intToBigEndian(this.lTreeAddress, var1, 16);
      Pack.intToBigEndian(this.treeHeight, var1, 20);
      Pack.intToBigEndian(this.treeIndex, var1, 24);
      return var1;
   }

   protected int getLTreeAddress() {
      return this.lTreeAddress;
   }

   protected int getTreeHeight() {
      return this.treeHeight;
   }

   protected int getTreeIndex() {
      return this.treeIndex;
   }

   protected static class Builder extends XMSSAddress.Builder<LTreeAddress.Builder> {
      private int lTreeAddress = 0;
      private int treeHeight = 0;
      private int treeIndex = 0;

      protected Builder() {
         super(1);
      }

      protected LTreeAddress.Builder withLTreeAddress(int var1) {
         this.lTreeAddress = var1;
         return this;
      }

      protected LTreeAddress.Builder withTreeHeight(int var1) {
         this.treeHeight = var1;
         return this;
      }

      protected LTreeAddress.Builder withTreeIndex(int var1) {
         this.treeIndex = var1;
         return this;
      }

      @Override
      protected XMSSAddress build() {
         return new LTreeAddress(this);
      }

      protected LTreeAddress.Builder getThis() {
         return this;
      }
   }
}
