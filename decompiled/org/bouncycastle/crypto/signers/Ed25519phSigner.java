package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;

public class Ed25519phSigner implements Signer {
   private final Digest prehash = Ed25519.createPrehash();
   private final byte[] context;
   private boolean forSigning;
   private Ed25519PrivateKeyParameters privateKey;
   private Ed25519PublicKeyParameters publicKey;

   public Ed25519phSigner(byte[] var1) {
      if (null == var1) {
         throw new NullPointerException("'context' cannot be null");
      } else {
         this.context = Arrays.clone(var1);
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forSigning = var1;
      if (var1) {
         this.privateKey = (Ed25519PrivateKeyParameters)var2;
         this.publicKey = null;
      } else {
         this.privateKey = null;
         this.publicKey = (Ed25519PublicKeyParameters)var2;
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("Ed25519", 128, var2, var1));
      this.reset();
   }

   @Override
   public void update(byte var1) {
      this.prehash.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.prehash.update(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() {
      if (this.forSigning && null != this.privateKey) {
         byte[] var1 = new byte[64];
         if (64 != this.prehash.doFinal(var1, 0)) {
            throw new IllegalStateException("Prehash digest failed");
         } else {
            byte[] var2 = new byte[64];
            this.privateKey.sign(2, this.context, var1, 0, 64, var2, 0);
            return var2;
         }
      } else {
         throw new IllegalStateException("Ed25519phSigner not initialised for signature generation.");
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (this.forSigning || null == this.publicKey) {
         throw new IllegalStateException("Ed25519phSigner not initialised for verification");
      } else if (64 != var1.length) {
         this.prehash.reset();
         return false;
      } else {
         byte[] var2 = new byte[64];
         if (64 != this.prehash.doFinal(var2, 0)) {
            throw new IllegalStateException("Prehash digest failed");
         } else {
            return this.publicKey.verify(2, this.context, var2, 0, 64, var1, 0);
         }
      }
   }

   @Override
   public void reset() {
      this.prehash.reset();
   }
}
