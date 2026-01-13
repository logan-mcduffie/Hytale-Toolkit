package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

public class LMSContext implements Digest {
   private final byte[] C;
   private final LMOtsPrivateKey key;
   private final LMSigParameters sigParams;
   private final byte[][] path;
   private final LMOtsPublicKey publicKey;
   private final Object signature;
   private LMSSignedPubKey[] signedPubKeys;
   private volatile Digest digest;

   LMSContext(LMOtsPrivateKey var1, LMSigParameters var2, Digest var3, byte[] var4, byte[][] var5) {
      this.key = var1;
      this.sigParams = var2;
      this.digest = var3;
      this.C = var4;
      this.path = var5;
      this.publicKey = null;
      this.signature = null;
   }

   LMSContext(LMOtsPublicKey var1, Object var2, Digest var3) {
      this.publicKey = var1;
      this.signature = var2;
      this.digest = var3;
      this.C = null;
      this.key = null;
      this.sigParams = null;
      this.path = null;
   }

   byte[] getC() {
      return this.C;
   }

   byte[] getQ() {
      byte[] var1 = new byte[34];
      this.digest.doFinal(var1, 0);
      this.digest = null;
      return var1;
   }

   byte[][] getPath() {
      return this.path;
   }

   LMOtsPrivateKey getPrivateKey() {
      return this.key;
   }

   LMOtsPublicKey getPublicKey() {
      return this.publicKey;
   }

   LMSigParameters getSigParams() {
      return this.sigParams;
   }

   public Object getSignature() {
      return this.signature;
   }

   LMSSignedPubKey[] getSignedPubKeys() {
      return this.signedPubKeys;
   }

   LMSContext withSignedPublicKeys(LMSSignedPubKey[] var1) {
      this.signedPubKeys = var1;
      return this;
   }

   @Override
   public String getAlgorithmName() {
      return this.digest.getAlgorithmName();
   }

   @Override
   public int getDigestSize() {
      return this.digest.getDigestSize();
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.digest.doFinal(var1, var2);
   }

   @Override
   public void reset() {
      this.digest.reset();
   }
}
