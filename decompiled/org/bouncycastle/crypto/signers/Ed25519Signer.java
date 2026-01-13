package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class Ed25519Signer implements Signer {
   private final Ed25519Signer.Buffer buffer = new Ed25519Signer.Buffer();
   private boolean forSigning;
   private Ed25519PrivateKeyParameters privateKey;
   private Ed25519PublicKeyParameters publicKey;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forSigning = var1;
      if (var2 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var2).getParameters();
      }

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
      this.buffer.write(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.buffer.write(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() {
      if (this.forSigning && null != this.privateKey) {
         return this.buffer.generateSignature(this.privateKey);
      } else {
         throw new IllegalStateException("Ed25519Signer not initialised for signature generation.");
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (!this.forSigning && null != this.publicKey) {
         return this.buffer.verifySignature(this.publicKey, var1);
      } else {
         throw new IllegalStateException("Ed25519Signer not initialised for verification");
      }
   }

   @Override
   public void reset() {
      this.buffer.reset();
   }

   private static final class Buffer extends ByteArrayOutputStream {
      private Buffer() {
      }

      synchronized byte[] generateSignature(Ed25519PrivateKeyParameters var1) {
         byte[] var2 = new byte[64];
         var1.sign(0, null, this.buf, 0, this.count, var2, 0);
         this.reset();
         return var2;
      }

      synchronized boolean verifySignature(Ed25519PublicKeyParameters var1, byte[] var2) {
         if (64 != var2.length) {
            this.reset();
            return false;
         } else {
            boolean var3 = var1.verify(0, null, this.buf, 0, this.count, var2, 0);
            this.reset();
            return var3;
         }
      }

      @Override
      public synchronized void reset() {
         Arrays.fill(this.buf, 0, this.count, (byte)0);
         this.count = 0;
      }
   }
}
