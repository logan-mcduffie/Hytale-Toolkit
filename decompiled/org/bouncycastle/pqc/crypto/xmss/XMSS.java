package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.util.Arrays;

public class XMSS {
   private final XMSSParameters params;
   private WOTSPlus wotsPlus;
   private SecureRandom prng;
   private XMSSPrivateKeyParameters privateKey;
   private XMSSPublicKeyParameters publicKey;

   public XMSS(XMSSParameters var1, SecureRandom var2) {
      if (var1 == null) {
         throw new NullPointerException("params == null");
      } else {
         this.params = var1;
         this.wotsPlus = var1.getWOTSPlus();
         this.prng = var2;
      }
   }

   public void generateKeys() {
      XMSSKeyPairGenerator var1 = new XMSSKeyPairGenerator();
      var1.init(new XMSSKeyGenerationParameters(this.getParams(), this.prng));
      AsymmetricCipherKeyPair var2 = var1.generateKeyPair();
      this.privateKey = (XMSSPrivateKeyParameters)var2.getPrivate();
      this.publicKey = (XMSSPublicKeyParameters)var2.getPublic();
      this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
   }

   public void importState(XMSSPrivateKeyParameters var1, XMSSPublicKeyParameters var2) {
      if (!Arrays.areEqual(var1.getRoot(), var2.getRoot())) {
         throw new IllegalStateException("root of private key and public key do not match");
      } else if (!Arrays.areEqual(var1.getPublicSeed(), var2.getPublicSeed())) {
         throw new IllegalStateException("public seed of private key and public key do not match");
      } else {
         this.privateKey = var1;
         this.publicKey = var2;
         this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
      }
   }

   public void importState(byte[] var1, byte[] var2) {
      if (var1 == null) {
         throw new NullPointerException("privateKey == null");
      } else if (var2 == null) {
         throw new NullPointerException("publicKey == null");
      } else {
         XMSSPrivateKeyParameters var3 = new XMSSPrivateKeyParameters.Builder(this.params).withPrivateKey(var1).build();
         XMSSPublicKeyParameters var4 = new XMSSPublicKeyParameters.Builder(this.params).withPublicKey(var2).build();
         if (!Arrays.areEqual(var3.getRoot(), var4.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
         } else if (!Arrays.areEqual(var3.getPublicSeed(), var4.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
         } else {
            this.privateKey = var3;
            this.publicKey = var4;
            this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
         }
      }
   }

   public byte[] sign(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("message == null");
      } else {
         XMSSSigner var2 = new XMSSSigner();
         var2.init(true, this.privateKey);
         byte[] var3 = var2.generateSignature(var1);
         this.privateKey = (XMSSPrivateKeyParameters)var2.getUpdatedPrivateKey();
         this.importState(this.privateKey, this.publicKey);
         return var3;
      }
   }

   public boolean verifySignature(byte[] var1, byte[] var2, byte[] var3) throws ParseException {
      if (var1 == null) {
         throw new NullPointerException("message == null");
      } else if (var2 == null) {
         throw new NullPointerException("signature == null");
      } else if (var3 == null) {
         throw new NullPointerException("publicKey == null");
      } else {
         XMSSSigner var4 = new XMSSSigner();
         var4.init(false, new XMSSPublicKeyParameters.Builder(this.getParams()).withPublicKey(var3).build());
         return var4.verifySignature(var1, var2);
      }
   }

   public XMSSPrivateKeyParameters exportPrivateKey() {
      return this.privateKey;
   }

   public XMSSPublicKeyParameters exportPublicKey() {
      return this.publicKey;
   }

   protected WOTSPlusSignature wotsSign(byte[] var1, OTSHashAddress var2) {
      if (var1.length != this.params.getTreeDigestSize()) {
         throw new IllegalArgumentException("size of messageDigest needs to be equal to size of digest");
      } else if (var2 == null) {
         throw new NullPointerException("otsHashAddress == null");
      } else {
         this.wotsPlus.importKeys(this.wotsPlus.getWOTSPlusSecretKey(this.privateKey.getSecretKeySeed(), var2), this.getPublicSeed());
         return this.wotsPlus.sign(var1, var2);
      }
   }

   public XMSSParameters getParams() {
      return this.params;
   }

   protected WOTSPlus getWOTSPlus() {
      return this.wotsPlus;
   }

   public byte[] getRoot() {
      return this.privateKey.getRoot();
   }

   protected void setRoot(byte[] var1) {
      this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params)
         .withSecretKeySeed(this.privateKey.getSecretKeySeed())
         .withSecretKeyPRF(this.privateKey.getSecretKeyPRF())
         .withPublicSeed(this.getPublicSeed())
         .withRoot(var1)
         .withBDSState(this.privateKey.getBDSState())
         .build();
      this.publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(var1).withPublicSeed(this.getPublicSeed()).build();
   }

   public int getIndex() {
      return this.privateKey.getIndex();
   }

   protected void setIndex(int var1) {
      this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params)
         .withSecretKeySeed(this.privateKey.getSecretKeySeed())
         .withSecretKeyPRF(this.privateKey.getSecretKeyPRF())
         .withPublicSeed(this.privateKey.getPublicSeed())
         .withRoot(this.privateKey.getRoot())
         .withBDSState(this.privateKey.getBDSState())
         .build();
   }

   public byte[] getPublicSeed() {
      return this.privateKey.getPublicSeed();
   }

   protected void setPublicSeed(byte[] var1) {
      this.privateKey = new XMSSPrivateKeyParameters.Builder(this.params)
         .withSecretKeySeed(this.privateKey.getSecretKeySeed())
         .withSecretKeyPRF(this.privateKey.getSecretKeyPRF())
         .withPublicSeed(var1)
         .withRoot(this.getRoot())
         .withBDSState(this.privateKey.getBDSState())
         .build();
      this.publicKey = new XMSSPublicKeyParameters.Builder(this.params).withRoot(this.getRoot()).withPublicSeed(var1).build();
      this.wotsPlus.importKeys(new byte[this.params.getTreeDigestSize()], var1);
   }

   public XMSSPrivateKeyParameters getPrivateKey() {
      return this.privateKey;
   }
}
