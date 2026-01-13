package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

public abstract class XMSSAddress {
   private final int layerAddress;
   private final long treeAddress;
   private final int type;
   private final int keyAndMask;

   protected XMSSAddress(XMSSAddress.Builder var1) {
      this.layerAddress = var1.layerAddress;
      this.treeAddress = var1.treeAddress;
      this.type = var1.type;
      this.keyAndMask = var1.keyAndMask;
   }

   protected byte[] toByteArray() {
      byte[] var1 = new byte[32];
      Pack.intToBigEndian(this.layerAddress, var1, 0);
      Pack.longToBigEndian(this.treeAddress, var1, 4);
      Pack.intToBigEndian(this.type, var1, 12);
      Pack.intToBigEndian(this.keyAndMask, var1, 28);
      return var1;
   }

   protected final int getLayerAddress() {
      return this.layerAddress;
   }

   protected final long getTreeAddress() {
      return this.treeAddress;
   }

   public final int getType() {
      return this.type;
   }

   public final int getKeyAndMask() {
      return this.keyAndMask;
   }

   protected abstract static class Builder<T extends XMSSAddress.Builder> {
      private final int type;
      private int layerAddress = 0;
      private long treeAddress = 0L;
      private int keyAndMask = 0;

      protected Builder(int var1) {
         this.type = var1;
      }

      protected T withLayerAddress(int var1) {
         this.layerAddress = var1;
         return this.getThis();
      }

      protected T withTreeAddress(long var1) {
         this.treeAddress = var1;
         return this.getThis();
      }

      protected T withKeyAndMask(int var1) {
         this.keyAndMask = var1;
         return this.getThis();
      }

      protected abstract XMSSAddress build();

      protected abstract T getThis();
   }
}
