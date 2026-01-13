package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import java.text.ParseException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.util.Arrays;

public final class XMSSMT {
   private XMSSMTParameters params;
   private XMSSParameters xmssParams;
   private SecureRandom prng;
   private XMSSMTPrivateKeyParameters privateKey;
   private XMSSMTPublicKeyParameters publicKey;

   public XMSSMT(XMSSMTParameters var1, SecureRandom var2) {
      if (var1 == null) {
         throw new NullPointerException("params == null");
      } else {
         this.params = var1;
         this.xmssParams = var1.getXMSSParameters();
         this.prng = var2;
         this.privateKey = new XMSSMTPrivateKeyParameters.Builder(var1).build();
         this.publicKey = new XMSSMTPublicKeyParameters.Builder(var1).build();
      }
   }

   public void generateKeys() {
      XMSSMTKeyPairGenerator var1 = new XMSSMTKeyPairGenerator();
      var1.init(new XMSSMTKeyGenerationParameters(this.getParams(), this.prng));
      AsymmetricCipherKeyPair var2 = var1.generateKeyPair();
      this.privateKey = (XMSSMTPrivateKeyParameters)var2.getPrivate();
      this.publicKey = (XMSSMTPublicKeyParameters)var2.getPublic();
      this.importState(this.privateKey, this.publicKey);
   }

   private void importState(XMSSMTPrivateKeyParameters var1, XMSSMTPublicKeyParameters var2) {
      this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getTreeDigestSize()], this.privateKey.getPublicSeed());
      this.privateKey = var1;
      this.publicKey = var2;
   }

   public void importState(byte[] var1, byte[] var2) {
      if (var1 == null) {
         throw new NullPointerException("privateKey == null");
      } else if (var2 == null) {
         throw new NullPointerException("publicKey == null");
      } else {
         XMSSMTPrivateKeyParameters var3 = new XMSSMTPrivateKeyParameters.Builder(this.params).withPrivateKey(var1).build();
         XMSSMTPublicKeyParameters var4 = new XMSSMTPublicKeyParameters.Builder(this.params).withPublicKey(var2).build();
         if (!Arrays.areEqual(var3.getRoot(), var4.getRoot())) {
            throw new IllegalStateException("root of private key and public key do not match");
         } else if (!Arrays.areEqual(var3.getPublicSeed(), var4.getPublicSeed())) {
            throw new IllegalStateException("public seed of private key and public key do not match");
         } else {
            this.xmssParams.getWOTSPlus().importKeys(new byte[this.params.getTreeDigestSize()], var3.getPublicSeed());
            this.privateKey = var3;
            this.publicKey = var4;
         }
      }
   }

   public byte[] sign(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("message == null");
      } else {
         XMSSMTSigner var2 = new XMSSMTSigner();
         var2.init(true, this.privateKey);
         byte[] var3 = var2.generateSignature(var1);
         this.privateKey = (XMSSMTPrivateKeyParameters)var2.getUpdatedPrivateKey();
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
         XMSSMTSigner var4 = new XMSSMTSigner();
         var4.init(false, new XMSSMTPublicKeyParameters.Builder(this.getParams()).withPublicKey(var3).build());
         return var4.verifySignature(var1, var2);
      }
   }

   public byte[] exportPrivateKey() {
      return this.privateKey.toByteArray();
   }

   public byte[] exportPublicKey() {
      return this.publicKey.toByteArray();
   }

   public XMSSMTParameters getParams() {
      return this.params;
   }

   public byte[] getPublicSeed() {
      return this.privateKey.getPublicSeed();
   }

   protected XMSSParameters getXMSS() {
      return this.xmssParams;
   }
}
