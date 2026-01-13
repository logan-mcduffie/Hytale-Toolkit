package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.pqc.crypto.StateAwareMessageSigner;
import org.bouncycastle.util.Arrays;

public class XMSSSigner implements StateAwareMessageSigner {
   private XMSSPrivateKeyParameters privateKey;
   private XMSSPublicKeyParameters publicKey;
   private XMSSParameters params;
   private WOTSPlus wotsPlus;
   private KeyedHashFunctions khf;
   private boolean initSign;
   private boolean hasGenerated;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         this.initSign = true;
         this.hasGenerated = false;
         this.privateKey = (XMSSPrivateKeyParameters)var2;
         this.params = this.privateKey.getParameters();
      } else {
         this.initSign = false;
         this.publicKey = (XMSSPublicKeyParameters)var2;
         this.params = this.publicKey.getParameters();
      }

      this.wotsPlus = this.params.getWOTSPlus();
      this.khf = this.wotsPlus.getKhf();
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("message == null");
      } else if (this.initSign) {
         if (this.privateKey == null) {
            throw new IllegalStateException("signing key no longer usable");
         } else {
            synchronized (this.privateKey) {
               if (this.privateKey.getUsagesRemaining() <= 0L) {
                  throw new ExhaustedPrivateKeyException("no usages of private key remaining");
               } else if (this.privateKey.getBDSState().getAuthenticationPath().isEmpty()) {
                  throw new IllegalStateException("not initialized");
               } else {
                  byte[] var9;
                  try {
                     int var3 = this.privateKey.getIndex();
                     this.hasGenerated = true;
                     byte[] var4 = this.khf.PRF(this.privateKey.getSecretKeyPRF(), XMSSUtil.toBytesBigEndian(var3, 32));
                     byte[] var5 = Arrays.concatenate(var4, this.privateKey.getRoot(), XMSSUtil.toBytesBigEndian(var3, this.params.getTreeDigestSize()));
                     byte[] var6 = this.khf.HMsg(var5, var1);
                     OTSHashAddress var7 = (OTSHashAddress)new OTSHashAddress.Builder().withOTSAddress(var3).build();
                     WOTSPlusSignature var8 = this.wotsSign(var6, var7);
                     var9 = new XMSSSignature.Builder(this.params)
                        .withIndex(var3)
                        .withRandom(var4)
                        .withWOTSPlusSignature(var8)
                        .withAuthPath(this.privateKey.getBDSState().getAuthenticationPath())
                        .build()
                        .toByteArray();
                  } finally {
                     this.privateKey.getBDSState().markUsed();
                     this.privateKey.rollKey();
                  }

                  return var9;
               }
            }
         }
      } else {
         throw new IllegalStateException("signer not initialized for signature generation");
      }
   }

   public long getUsagesRemaining() {
      return this.privateKey.getUsagesRemaining();
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      XMSSSignature var3 = new XMSSSignature.Builder(this.params).withSignature(var2).build();
      int var4 = var3.getIndex();
      this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.publicKey.getPublicSeed());
      byte[] var5 = Arrays.concatenate(var3.getRandom(), this.publicKey.getRoot(), XMSSUtil.toBytesBigEndian(var4, this.params.getTreeDigestSize()));
      byte[] var6 = this.khf.HMsg(var5, var1);
      int var7 = this.params.getHeight();
      int var8 = XMSSUtil.getLeafIndex(var4, var7);
      OTSHashAddress var9 = (OTSHashAddress)new OTSHashAddress.Builder().withOTSAddress(var4).build();
      XMSSNode var10 = XMSSVerifierUtil.getRootNodeFromSignature(this.wotsPlus, var7, var6, var3, var9, var8);
      return Arrays.constantTimeAreEqual(var10.getValue(), this.publicKey.getRoot());
   }

   @Override
   public AsymmetricKeyParameter getUpdatedPrivateKey() {
      synchronized (this.privateKey) {
         if (this.hasGenerated) {
            XMSSPrivateKeyParameters var5 = this.privateKey;
            this.privateKey = null;
            return var5;
         } else {
            XMSSPrivateKeyParameters var2 = this.privateKey;
            if (var2 != null) {
               this.privateKey = this.privateKey.getNextKey();
            }

            return var2;
         }
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
}
