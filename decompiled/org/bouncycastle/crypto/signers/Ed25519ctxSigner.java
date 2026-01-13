package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class Ed25519ctxSigner implements Signer {
   private final Ed25519ctxSigner.Buffer buffer = new Ed25519ctxSigner.Buffer();
   private final byte[] context;
   private boolean forSigning;
   private Ed25519PrivateKeyParameters privateKey;
   private Ed25519PublicKeyParameters publicKey;

   public Ed25519ctxSigner(byte[] var1) {
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
      this.buffer.write(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.buffer.write(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() {
      if (this.forSigning && null != this.privateKey) {
         return this.buffer.generateSignature(this.privateKey, this.context);
      } else {
         throw new IllegalStateException("Ed25519ctxSigner not initialised for signature generation.");
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (!this.forSigning && null != this.publicKey) {
         return this.buffer.verifySignature(this.publicKey, this.context, var1);
      } else {
         throw new IllegalStateException("Ed25519ctxSigner not initialised for verification");
      }
   }

   @Override
   public void reset() {
      this.buffer.reset();
   }

   private static final class Buffer extends ByteArrayOutputStream {
      private Buffer() {
      }

      synchronized byte[] generateSignature(Ed25519PrivateKeyParameters var1, byte[] var2) {
         byte[] var3 = new byte[64];
         var1.sign(1, var2, this.buf, 0, this.count, var3, 0);
         this.reset();
         return var3;
      }

      synchronized boolean verifySignature(Ed25519PublicKeyParameters var1, byte[] var2, byte[] var3) {
         if (64 != var3.length) {
            this.reset();
            return false;
         } else {
            boolean var4 = var1.verify(1, var2, this.buf, 0, this.count, var3, 0);
            this.reset();
            return var4;
         }
      }

      @Override
      public synchronized void reset() {
         Arrays.fill(this.buf, 0, this.count, (byte)0);
         this.count = 0;
      }
   }
}
