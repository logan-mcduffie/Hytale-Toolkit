package org.bouncycastle.pqc.jcajce.provider.picnic;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicSigner;

public class SignatureSpi extends Signature {
   private SecureRandom random;
   private Digest digest;
   private PicnicSigner signer;

   protected SignatureSpi(Digest var1, PicnicSigner var2) {
      super("Picnic");
      this.digest = var1;
      this.signer = var2;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCPicnicPublicKey) {
         BCPicnicPublicKey var2 = (BCPicnicPublicKey)var1;
         PicnicPublicKeyParameters var3 = var2.getKeyParams();
         this.digest.reset();
         this.signer.init(false, var3);
      } else {
         throw new InvalidKeyException("unknown public key passed to Picnic");
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCPicnicPrivateKey) {
         BCPicnicPrivateKey var2 = (BCPicnicPrivateKey)var1;
         PicnicPrivateKeyParameters var3 = var2.getKeyParams();
         this.digest.reset();
         this.signer.init(true, var3);
      } else {
         throw new InvalidKeyException("unknown private key passed to Picnic");
      }
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.digest.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.digest.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);

      try {
         return this.signer.generateSignature(var1);
      } catch (Exception var3) {
         throw new SignatureException(var3.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);
      return this.signer.verifySignature(var2, var1);
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected void engineSetParameter(String var1, Object var2) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   /** @deprecated */
   @Override
   protected Object engineGetParameter(String var1) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   public static class Base extends SignatureSpi {
      public Base() {
         super(new NullDigest(), new PicnicSigner());
      }
   }

   public static class withSha3512 extends SignatureSpi {
      public withSha3512() {
         super(new SHA3Digest(512), new PicnicSigner());
      }
   }

   public static class withSha512 extends SignatureSpi {
      public withSha512() {
         super(new SHA512Digest(), new PicnicSigner());
      }
   }

   public static class withShake256 extends SignatureSpi {
      public withShake256() {
         super(new SHAKEDigest(256), new PicnicSigner());
      }
   }
}
