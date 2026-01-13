package org.bouncycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

public class XMSSReducedSignature implements XMSSStoreableObjectInterface {
   private final XMSSParameters params;
   private final WOTSPlusSignature wotsPlusSignature;
   private final List<XMSSNode> authPath;

   protected XMSSReducedSignature(XMSSReducedSignature.Builder var1) {
      this.params = var1.params;
      if (this.params == null) {
         throw new NullPointerException("params == null");
      } else {
         int var2 = this.params.getTreeDigestSize();
         int var3 = this.params.getWOTSPlus().getParams().getLen();
         int var4 = this.params.getHeight();
         byte[] var5 = var1.reducedSignature;
         if (var5 != null) {
            int var6 = var3 * var2;
            int var7 = var4 * var2;
            int var8 = var6 + var7;
            if (var5.length != var8) {
               throw new IllegalArgumentException("signature has wrong size");
            }

            int var9 = 0;
            byte[][] var10 = new byte[var3][];

            for (int var11 = 0; var11 < var10.length; var11++) {
               var10[var11] = XMSSUtil.extractBytesAtOffset(var5, var9, var2);
               var9 += var2;
            }

            this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), var10);
            ArrayList var15 = new ArrayList();

            for (int var12 = 0; var12 < var4; var12++) {
               var15.add(new XMSSNode(var12, XMSSUtil.extractBytesAtOffset(var5, var9, var2)));
               var9 += var2;
            }

            this.authPath = var15;
         } else {
            WOTSPlusSignature var13 = var1.wotsPlusSignature;
            if (var13 != null) {
               this.wotsPlusSignature = var13;
            } else {
               this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), new byte[var3][var2]);
            }

            List var14 = var1.authPath;
            if (var14 != null) {
               if (var14.size() != var4) {
                  throw new IllegalArgumentException("size of authPath needs to be equal to height of tree");
               }

               this.authPath = var14;
            } else {
               this.authPath = new ArrayList<>();
            }
         }
      }
   }

   @Override
   public byte[] toByteArray() {
      int var1 = this.params.getTreeDigestSize();
      int var2 = this.params.getWOTSPlus().getParams().getLen() * var1;
      int var3 = this.params.getHeight() * var1;
      int var4 = var2 + var3;
      byte[] var5 = new byte[var4];
      int var6 = 0;
      byte[][] var7 = this.wotsPlusSignature.toByteArray();

      for (int var8 = 0; var8 < var7.length; var8++) {
         XMSSUtil.copyBytesAtOffset(var5, var7[var8], var6);
         var6 += var1;
      }

      for (int var10 = 0; var10 < this.authPath.size(); var10++) {
         byte[] var9 = this.authPath.get(var10).getValue();
         XMSSUtil.copyBytesAtOffset(var5, var9, var6);
         var6 += var1;
      }

      return var5;
   }

   public XMSSParameters getParams() {
      return this.params;
   }

   public WOTSPlusSignature getWOTSPlusSignature() {
      return this.wotsPlusSignature;
   }

   public List<XMSSNode> getAuthPath() {
      return this.authPath;
   }

   public static class Builder {
      private final XMSSParameters params;
      private WOTSPlusSignature wotsPlusSignature = null;
      private List<XMSSNode> authPath = null;
      private byte[] reducedSignature = null;

      public Builder(XMSSParameters var1) {
         this.params = var1;
      }

      public XMSSReducedSignature.Builder withWOTSPlusSignature(WOTSPlusSignature var1) {
         this.wotsPlusSignature = var1;
         return this;
      }

      public XMSSReducedSignature.Builder withAuthPath(List<XMSSNode> var1) {
         this.authPath = var1;
         return this;
      }

      public XMSSReducedSignature.Builder withReducedSignature(byte[] var1) {
         this.reducedSignature = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSReducedSignature build() {
         return new XMSSReducedSignature(this);
      }
   }
}
