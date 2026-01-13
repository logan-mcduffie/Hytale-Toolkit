package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public final class XMSSMTSignature implements XMSSStoreableObjectInterface, Encodable {
   private final XMSSMTParameters params;
   private final long index;
   private final byte[] random;
   private final List<XMSSReducedSignature> reducedSignatures;

   private XMSSMTSignature(XMSSMTSignature.Builder var1) {
      this.params = var1.params;
      if (this.params == null) {
         throw new NullPointerException("params == null");
      } else {
         int var2 = this.params.getTreeDigestSize();
         byte[] var3 = var1.signature;
         if (var3 != null) {
            int var4 = this.params.getWOTSPlus().getParams().getLen();
            int var5 = (int)Math.ceil(this.params.getHeight() / 8.0);
            int var7 = (this.params.getHeight() / this.params.getLayers() + var4) * var2;
            int var8 = var7 * this.params.getLayers();
            int var9 = var5 + var2 + var8;
            if (var3.length != var9) {
               throw new IllegalArgumentException("signature has wrong size");
            }

            int var10 = 0;
            this.index = XMSSUtil.bytesToXBigEndian(var3, var10, var5);
            if (!XMSSUtil.isIndexValid(this.params.getHeight(), this.index)) {
               throw new IllegalArgumentException("index out of bounds");
            }

            var10 += var5;
            this.random = XMSSUtil.extractBytesAtOffset(var3, var10, var2);
            var10 += var2;

            for (this.reducedSignatures = new ArrayList<>(); var10 < var3.length; var10 += var7) {
               XMSSReducedSignature var11 = new XMSSReducedSignature.Builder(this.params.getXMSSParameters())
                  .withReducedSignature(XMSSUtil.extractBytesAtOffset(var3, var10, var7))
                  .build();
               this.reducedSignatures.add(var11);
            }
         } else {
            this.index = var1.index;
            byte[] var12 = var1.random;
            if (var12 != null) {
               if (var12.length != var2) {
                  throw new IllegalArgumentException("size of random needs to be equal to size of digest");
               }

               this.random = var12;
            } else {
               this.random = new byte[var2];
            }

            List var13 = var1.reducedSignatures;
            if (var13 != null) {
               this.reducedSignatures = var13;
            } else {
               this.reducedSignatures = new ArrayList<>();
            }
         }
      }
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.toByteArray();
   }

   @Override
   public byte[] toByteArray() {
      int var1 = this.params.getTreeDigestSize();
      int var2 = this.params.getWOTSPlus().getParams().getLen();
      int var3 = (int)Math.ceil(this.params.getHeight() / 8.0);
      int var5 = (this.params.getHeight() / this.params.getLayers() + var2) * var1;
      int var6 = var5 * this.params.getLayers();
      int var7 = var3 + var1 + var6;
      byte[] var8 = new byte[var7];
      int var9 = 0;
      byte[] var10 = XMSSUtil.toBytesBigEndian(this.index, var3);
      XMSSUtil.copyBytesAtOffset(var8, var10, var9);
      var9 += var3;
      XMSSUtil.copyBytesAtOffset(var8, this.random, var9);
      var9 += var1;

      for (XMSSReducedSignature var12 : this.reducedSignatures) {
         byte[] var13 = var12.toByteArray();
         XMSSUtil.copyBytesAtOffset(var8, var13, var9);
         var9 += var5;
      }

      return var8;
   }

   public long getIndex() {
      return this.index;
   }

   public byte[] getRandom() {
      return XMSSUtil.cloneArray(this.random);
   }

   public List<XMSSReducedSignature> getReducedSignatures() {
      return this.reducedSignatures;
   }

   public static class Builder {
      private final XMSSMTParameters params;
      private long index = 0L;
      private byte[] random = null;
      private List<XMSSReducedSignature> reducedSignatures = null;
      private byte[] signature = null;

      public Builder(XMSSMTParameters var1) {
         this.params = var1;
      }

      public XMSSMTSignature.Builder withIndex(long var1) {
         this.index = var1;
         return this;
      }

      public XMSSMTSignature.Builder withRandom(byte[] var1) {
         this.random = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSMTSignature.Builder withReducedSignatures(List<XMSSReducedSignature> var1) {
         this.reducedSignatures = var1;
         return this;
      }

      public XMSSMTSignature.Builder withSignature(byte[] var1) {
         this.signature = Arrays.clone(var1);
         return this;
      }

      public XMSSMTSignature build() {
         return new XMSSMTSignature(this);
      }
   }
}
