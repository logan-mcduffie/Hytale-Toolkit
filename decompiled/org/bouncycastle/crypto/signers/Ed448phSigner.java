package org.bouncycastle.crypto.signers;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.Arrays;

public class Ed448phSigner implements Signer {
   private final Xof prehash = Ed448.createPrehash();
   private final byte[] context;
   private boolean forSigning;
   private Ed448PrivateKeyParameters privateKey;
   private Ed448PublicKeyParameters publicKey;

   public Ed448phSigner(byte[] var1) {
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
         this.privateKey = (Ed448PrivateKeyParameters)var2;
         this.publicKey = null;
      } else {
         this.privateKey = null;
         this.publicKey = (Ed448PublicKeyParameters)var2;
      }

      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("Ed448", 224, var2, var1));
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
         if (64 != this.prehash.doFinal(var1, 0, 64)) {
            throw new IllegalStateException("Prehash digest failed");
         } else {
            byte[] var2 = new byte[114];
            this.privateKey.sign(1, this.context, var1, 0, 64, var2, 0);
            return var2;
         }
      } else {
         throw new IllegalStateException("Ed448phSigner not initialised for signature generation.");
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (this.forSigning || null == this.publicKey) {
         throw new IllegalStateException("Ed448phSigner not initialised for verification");
      } else if (114 != var1.length) {
         this.prehash.reset();
         return false;
      } else {
         byte[] var2 = new byte[64];
         if (64 != this.prehash.doFinal(var2, 0, 64)) {
            throw new IllegalStateException("Prehash digest failed");
         } else {
            return this.publicKey.verify(1, this.context, var2, 0, 64, var1, 0);
         }
      }
   }

   @Override
   public void reset() {
      this.prehash.reset();
   }
}
