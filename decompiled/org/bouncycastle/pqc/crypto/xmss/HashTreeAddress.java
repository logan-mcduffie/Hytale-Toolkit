package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

final class HashTreeAddress extends XMSSAddress {
   private static final int TYPE = 2;
   private static final int PADDING = 0;
   private final int padding = 0;
   private final int treeHeight;
   private final int treeIndex;

   private HashTreeAddress(HashTreeAddress.Builder var1) {
      super(var1);
      this.treeHeight = var1.treeHeight;
      this.treeIndex = var1.treeIndex;
   }

   @Override
   protected byte[] toByteArray() {
      byte[] var1 = super.toByteArray();
      Pack.intToBigEndian(this.padding, var1, 16);
      Pack.intToBigEndian(this.treeHeight, var1, 20);
      Pack.intToBigEndian(this.treeIndex, var1, 24);
      return var1;
   }

   protected int getPadding() {
      return this.padding;
   }

   protected int getTreeHeight() {
      return this.treeHeight;
   }

   protected int getTreeIndex() {
      return this.treeIndex;
   }

   protected static class Builder extends XMSSAddress.Builder<HashTreeAddress.Builder> {
      private int treeHeight = 0;
      private int treeIndex = 0;

      protected Builder() {
         super(2);
      }

      protected HashTreeAddress.Builder withTreeHeight(int var1) {
         this.treeHeight = var1;
         return this;
      }

      protected HashTreeAddress.Builder withTreeIndex(int var1) {
         this.treeIndex = var1;
         return this;
      }

      @Override
      protected XMSSAddress build() {
         return new HashTreeAddress(this);
      }

      protected HashTreeAddress.Builder getThis() {
         return this;
      }
   }
}
