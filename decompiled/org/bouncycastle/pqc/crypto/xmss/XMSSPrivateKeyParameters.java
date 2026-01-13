package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Pack;

public final class XMSSPrivateKeyParameters extends XMSSKeyParameters implements XMSSStoreableObjectInterface, Encodable {
   private final XMSSParameters params;
   private final byte[] secretKeySeed;
   private final byte[] secretKeyPRF;
   private final byte[] publicSeed;
   private final byte[] root;
   private volatile BDS bdsState;

   private XMSSPrivateKeyParameters(XMSSPrivateKeyParameters.Builder var1) {
      super(true, var1.params.getTreeDigest());
      this.params = var1.params;
      if (this.params == null) {
         throw new NullPointerException("params == null");
      } else {
         int var2 = this.params.getTreeDigestSize();
         byte[] var3 = var1.privateKey;
         if (var3 != null) {
            int var4 = this.params.getHeight();
            byte var5 = 4;
            int var10 = 0;
            int var11 = Pack.bigEndianToInt(var3, var10);
            if (!XMSSUtil.isIndexValid(var4, var11)) {
               throw new IllegalArgumentException("index out of bounds");
            }

            var10 += var5;
            this.secretKeySeed = XMSSUtil.extractBytesAtOffset(var3, var10, var2);
            var10 += var2;
            this.secretKeyPRF = XMSSUtil.extractBytesAtOffset(var3, var10, var2);
            var10 += var2;
            this.publicSeed = XMSSUtil.extractBytesAtOffset(var3, var10, var2);
            var10 += var2;
            this.root = XMSSUtil.extractBytesAtOffset(var3, var10, var2);
            var10 += var2;
            byte[] var12 = XMSSUtil.extractBytesAtOffset(var3, var10, var3.length - var10);

            try {
               BDS var13 = (BDS)XMSSUtil.deserialize(var12, BDS.class);
               if (var13.getIndex() != var11) {
                  throw new IllegalStateException("serialized BDS has wrong index");
               }

               this.bdsState = var13.withWOTSDigest(var1.params.getTreeDigestOID());
            } catch (IOException var14) {
               throw new IllegalArgumentException(var14.getMessage(), var14);
            } catch (ClassNotFoundException var15) {
               throw new IllegalArgumentException(var15.getMessage(), var15);
            }
         } else {
            byte[] var16 = var1.secretKeySeed;
            if (var16 != null) {
               if (var16.length != var2) {
                  throw new IllegalArgumentException("size of secretKeySeed needs to be equal size of digest");
               }

               this.secretKeySeed = var16;
            } else {
               this.secretKeySeed = new byte[var2];
            }

            byte[] var17 = var1.secretKeyPRF;
            if (var17 != null) {
               if (var17.length != var2) {
                  throw new IllegalArgumentException("size of secretKeyPRF needs to be equal size of digest");
               }

               this.secretKeyPRF = var17;
            } else {
               this.secretKeyPRF = new byte[var2];
            }

            byte[] var6 = var1.publicSeed;
            if (var6 != null) {
               if (var6.length != var2) {
                  throw new IllegalArgumentException("size of publicSeed needs to be equal size of digest");
               }

               this.publicSeed = var6;
            } else {
               this.publicSeed = new byte[var2];
            }

            byte[] var7 = var1.root;
            if (var7 != null) {
               if (var7.length != var2) {
                  throw new IllegalArgumentException("size of root needs to be equal size of digest");
               }

               this.root = var7;
            } else {
               this.root = new byte[var2];
            }

            BDS var8 = var1.bdsState;
            if (var8 != null) {
               this.bdsState = var8;
            } else if (var1.index < (1 << this.params.getHeight()) - 2 && var6 != null && var16 != null) {
               this.bdsState = new BDS(this.params, var6, var16, (OTSHashAddress)new OTSHashAddress.Builder().build(), var1.index);
            } else {
               this.bdsState = new BDS(this.params, (1 << this.params.getHeight()) - 1, var1.index);
            }

            if (var1.maxIndex >= 0 && var1.maxIndex != this.bdsState.getMaxIndex()) {
               throw new IllegalArgumentException("maxIndex set but not reflected in state");
            }
         }
      }
   }

   public long getUsagesRemaining() {
      synchronized (this) {
         return this.bdsState.getMaxIndex() - this.getIndex() + 1;
      }
   }

   @Override
   public byte[] getEncoded() throws IOException {
      synchronized (this) {
         return this.toByteArray();
      }
   }

   XMSSPrivateKeyParameters rollKey() {
      synchronized (this) {
         if (this.bdsState.getIndex() < this.bdsState.getMaxIndex()) {
            this.bdsState = this.bdsState.getNextState(this.publicSeed, this.secretKeySeed, (OTSHashAddress)new OTSHashAddress.Builder().build());
         } else {
            this.bdsState = new BDS(this.params, this.bdsState.getMaxIndex(), this.bdsState.getMaxIndex() + 1);
         }

         return this;
      }
   }

   public XMSSPrivateKeyParameters getNextKey() {
      synchronized (this) {
         return this.extractKeyShard(1);
      }
   }

   public XMSSPrivateKeyParameters extractKeyShard(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("cannot ask for a shard with 0 keys");
      } else {
         synchronized (this) {
            if (var1 > this.getUsagesRemaining()) {
               throw new IllegalArgumentException("usageCount exceeds usages remaining");
            } else {
               XMSSPrivateKeyParameters var3 = new XMSSPrivateKeyParameters.Builder(this.params)
                  .withSecretKeySeed(this.secretKeySeed)
                  .withSecretKeyPRF(this.secretKeyPRF)
                  .withPublicSeed(this.publicSeed)
                  .withRoot(this.root)
                  .withIndex(this.getIndex())
                  .withBDSState(this.bdsState.withMaxIndex(this.bdsState.getIndex() + var1 - 1, this.params.getTreeDigestOID()))
                  .build();
               if (var1 == this.getUsagesRemaining()) {
                  this.bdsState = new BDS(this.params, this.bdsState.getMaxIndex(), this.getIndex() + var1);
               } else {
                  OTSHashAddress var4 = (OTSHashAddress)new OTSHashAddress.Builder().build();

                  for (int var5 = 0; var5 != var1; var5++) {
                     this.bdsState = this.bdsState.getNextState(this.publicSeed, this.secretKeySeed, var4);
                  }
               }

               return var3;
            }
         }
      }
   }

   @Deprecated
   @Override
   public byte[] toByteArray() {
      synchronized (this) {
         int var2 = this.params.getTreeDigestSize();
         byte var3 = 4;
         int var8 = var3 + var2 + var2 + var2 + var2;
         byte[] var9 = new byte[var8];
         int var10 = 0;
         Pack.intToBigEndian(this.bdsState.getIndex(), var9, var10);
         var10 += var3;
         XMSSUtil.copyBytesAtOffset(var9, this.secretKeySeed, var10);
         var10 += var2;
         XMSSUtil.copyBytesAtOffset(var9, this.secretKeyPRF, var10);
         var10 += var2;
         XMSSUtil.copyBytesAtOffset(var9, this.publicSeed, var10);
         var10 += var2;
         XMSSUtil.copyBytesAtOffset(var9, this.root, var10);
         Object var11 = null;

         try {
            var11 = XMSSUtil.serialize(this.bdsState);
         } catch (IOException var14) {
            throw new RuntimeException("error serializing bds state: " + var14.getMessage());
         }

         return Arrays.concatenate(var9, (byte[])var11);
      }
   }

   public int getIndex() {
      return this.bdsState.getIndex();
   }

   public byte[] getSecretKeySeed() {
      return XMSSUtil.cloneArray(this.secretKeySeed);
   }

   public byte[] getSecretKeyPRF() {
      return XMSSUtil.cloneArray(this.secretKeyPRF);
   }

   public byte[] getPublicSeed() {
      return XMSSUtil.cloneArray(this.publicSeed);
   }

   public byte[] getRoot() {
      return XMSSUtil.cloneArray(this.root);
   }

   BDS getBDSState() {
      return this.bdsState;
   }

   public XMSSParameters getParameters() {
      return this.params;
   }

   public static class Builder {
      private final XMSSParameters params;
      private int index = 0;
      private int maxIndex = -1;
      private byte[] secretKeySeed = null;
      private byte[] secretKeyPRF = null;
      private byte[] publicSeed = null;
      private byte[] root = null;
      private BDS bdsState = null;
      private byte[] privateKey = null;

      public Builder(XMSSParameters var1) {
         this.params = var1;
      }

      public XMSSPrivateKeyParameters.Builder withIndex(int var1) {
         this.index = var1;
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withMaxIndex(int var1) {
         this.maxIndex = var1;
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withSecretKeySeed(byte[] var1) {
         this.secretKeySeed = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withSecretKeyPRF(byte[] var1) {
         this.secretKeyPRF = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withPublicSeed(byte[] var1) {
         this.publicSeed = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withRoot(byte[] var1) {
         this.root = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withBDSState(BDS var1) {
         this.bdsState = var1;
         return this;
      }

      public XMSSPrivateKeyParameters.Builder withPrivateKey(byte[] var1) {
         this.privateKey = XMSSUtil.cloneArray(var1);
         return this;
      }

      public XMSSPrivateKeyParameters build() {
         return new XMSSPrivateKeyParameters(this);
      }
   }
}
