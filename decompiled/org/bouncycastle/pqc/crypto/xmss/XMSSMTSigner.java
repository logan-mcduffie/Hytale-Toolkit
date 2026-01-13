package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Arrays;

public class XMSSMTSigner implements StateAwareMessageSigner {
   private XMSSMTPrivateKeyParameters privateKey;
   private XMSSMTPublicKeyParameters publicKey;
   private XMSSMTParameters params;
   private XMSSParameters xmssParams;
   private WOTSPlus wotsPlus;
   private boolean hasGenerated;
   private boolean initSign;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         this.initSign = true;
         this.hasGenerated = false;
         this.privateKey = (XMSSMTPrivateKeyParameters)var2;
         this.params = this.privateKey.getParameters();
         this.xmssParams = this.params.getXMSSParameters();
      } else {
         this.initSign = false;
         this.publicKey = (XMSSMTPublicKeyParameters)var2;
         this.params = this.publicKey.getParameters();
         this.xmssParams = this.params.getXMSSParameters();
      }

      this.wotsPlus = this.params.getWOTSPlus();
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("message == null");
      } else if (!this.initSign) {
         throw new IllegalStateException("signer not initialized for signature generation");
      } else if (this.privateKey == null) {
         throw new IllegalStateException("signing key no longer usable");
      } else {
         synchronized (this.privateKey) {
            if (this.privateKey.getUsagesRemaining() <= 0L) {
               throw new IllegalStateException("no usages of private key remaining");
            } else if (this.privateKey.getBDSState().isEmpty()) {
               throw new IllegalStateException("not initialized");
            } else {
               byte[] var30;
               try {
                  BDSStateMap var3 = this.privateKey.getBDSState();
                  long var4 = this.privateKey.getIndex();
                  int var6 = this.params.getHeight();
                  int var7 = this.xmssParams.getHeight();
                  if (this.privateKey.getUsagesRemaining() <= 0L) {
                     throw new IllegalStateException("index out of bounds");
                  }

                  byte[] var8 = this.wotsPlus.getKhf().PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(var4, 32));
                  byte[] var9 = Arrays.concatenate(var8, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(var4, this.params.getTreeDigestSize()));
                  byte[] var10 = this.wotsPlus.getKhf().HMsg(var9, var1);
                  this.hasGenerated = true;
                  XMSSMTSignature var11 = new XMSSMTSignature.Builder(this.params).withIndex(var4).withRandom(var8).build();
                  long var12 = XMSSUtil.getTreeIndex(var4, var7);
                  int var14 = XMSSUtil.getLeafIndex(var4, var7);
                  this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
                  OTSHashAddress var15 = (OTSHashAddress)new OTSHashAddress.Builder().withTreeAddress(var12).withOTSAddress(var14).build();
                  if (var3.get(0) == null || var14 == 0) {
                     var3.put(0, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), var15));
                  }

                  WOTSPlusSignature var16 = this.wotsSign(var10, var15);
                  XMSSReducedSignature var17 = new XMSSReducedSignature.Builder(this.xmssParams)
                     .withWOTSPlusSignature(var16)
                     .withAuthPath(var3.get(0).getAuthenticationPath())
                     .build();
                  var11.getReducedSignatures().add(var17);

                  for (int var18 = 1; var18 < this.params.getLayers(); var18++) {
                     XMSSNode var19 = var3.get(var18 - 1).getRoot();
                     var14 = XMSSUtil.getLeafIndex(var12, var7);
                     var12 = XMSSUtil.getTreeIndex(var12, var7);
                     var15 = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(var18).withTreeAddress(var12).withOTSAddress(var14).build();
                     var16 = this.wotsSign(var19.getValue(), var15);
                     if (var3.get(var18) == null || XMSSUtil.isNewBDSInitNeeded(var4, var7, var18)) {
                        var3.put(var18, new BDS(this.xmssParams, this.privateKey.getPublicSeed(), this.privateKey.getSecretKeySeed(), var15));
                     }

                     var17 = new XMSSReducedSignature.Builder(this.xmssParams)
                        .withWOTSPlusSignature(var16)
                        .withAuthPath(var3.get(var18).getAuthenticationPath())
                        .build();
                     var11.getReducedSignatures().add(var17);
                  }

                  var30 = var11.toByteArray();
               } finally {
                  this.privateKey.rollKey();
               }

               return var30;
            }
         }
      }
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      if (var1 == null) {
         throw new NullPointerException("message == null");
      } else if (var2 == null) {
         throw new NullPointerException("signature == null");
      } else if (this.publicKey == null) {
         throw new NullPointerException("publicKey == null");
      } else {
         XMSSMTSignature var3 = new XMSSMTSignature.Builder(this.params).withSignature(var2).build();
         byte[] var4 = Arrays.concatenate(
            var3.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(var3.getIndex(), this.params.getTreeDigestSize())
         );
         byte[] var5 = this.wotsPlus.getKhf().HMsg(var4, var1);
         long var6 = var3.getIndex();
         int var8 = this.xmssParams.getHeight();
         long var9 = XMSSUtil.getTreeIndex(var6, var8);
         int var11 = XMSSUtil.getLeafIndex(var6, var8);
         this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.publicKey.getPublicSeed());
         OTSHashAddress var12 = (OTSHashAddress)new OTSHashAddress.Builder().withTreeAddress(var9).withOTSAddress(var11).build();
         XMSSReducedSignature var13 = var3.getReducedSignatures().get(0);
         XMSSNode var14 = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, var8, var5, var13, var12, var11);

         for (int var15 = 1; var15 < this.params.getLayers(); var15++) {
            var13 = var3.getReducedSignatures().get(var15);
            var11 = XMSSUtil.getLeafIndex(var9, var8);
            var9 = XMSSUtil.getTreeIndex(var9, var8);
            var12 = (OTSHashAddress)new OTSHashAddress.Builder().withLayerAddress(var15).withTreeAddress(var9).withOTSAddress(var11).build();
            var14 = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, var8, var14.getValue(), var13, var12, var11);
         }

         return Arrays.constantTimeAreEqual(var14.getValue(), this.publicKey.getRoot());
      }
   }

   private WOTSPlusSignature wotsSign(byte[] var1, OTSHashAddress var2) {
      if (var1.length != this.params.getTreeDigestSize()) {
         throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
      } else if (var2 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), var2), this.privateKey.getPublicSeed());
         return this.wotsPlus.sign(var1, var2);
      }
   }

   public long getUsagesRemaining() {
      return this.privateKey.getUsagesRemaining();
   }

   @Override
   public AsymmetricKeyParameter getUpdatedPrivateKey() {
      if (this.hasGenerated) {
         XMSSMTPrivateKeyParameters var2 = this.privateKey;
         this.privateKey = null;
         return var2;
      } else {
         XMSSMTPrivateKeyParameters var1 = this.privateKey;
         if (var1 != null) {
            this.privateKey = this.privateKey.getNextKey();
         }

         return var1;
      }
   }
}
