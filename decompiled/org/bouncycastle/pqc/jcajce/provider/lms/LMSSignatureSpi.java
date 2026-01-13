package org.bouncycastle.pqc.jcajce.provider.lms;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.pqc.crypto.ExhaustedPrivateKeyException;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.lms.LMSContext;
import org.bouncycastle.pqc.crypto.lms.LMSContextBasedSigner;
import org.bouncycastle.pqc.crypto.lms.LMSContextBasedVerifier;

public class LMSSignatureSpi extends Signature {
   private Digest digest;
   private MessageSigner signer;
   private SecureRandom random;
   private LMSContextBasedSigner lmOtsSigner;
   private LMSContextBasedVerifier lmOtsVerifier;

   protected LMSSignatureSpi(String var1) {
      super(var1);
   }

   protected LMSSignatureSpi(String var1, Digest var2) {
      super(var1);
      this.digest = var2;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCLMSPublicKey) {
         this.digest = new NullDigest();
         this.digest.reset();
         this.lmOtsVerifier = (LMSContextBasedVerifier)((BCLMSPublicKey)var1).getKeyParams();
      } else {
         throw new InvalidKeyException("unknown public key passed to LMS");
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCLMSPrivateKey) {
         this.lmOtsSigner = (LMSContextBasedSigner)((BCLMSPrivateKey)var1).getKeyParams();
         if (this.lmOtsSigner.getUsagesRemaining() == 0L) {
            throw new InvalidKeyException("private key exhausted");
         } else {
            this.digest = null;
         }
      } else {
         throw new InvalidKeyException("unknown private key passed to LMS");
      }
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      if (this.digest == null) {
         this.digest = this.getSigner();
      }

      this.digest.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      if (this.digest == null) {
         this.digest = this.getSigner();
      }

      this.digest.update(var1, var2, var3);
   }

   private Digest getSigner() throws SignatureException {
      try {
         return this.lmOtsSigner.generateLMSContext();
      } catch (ExhaustedPrivateKeyException var2) {
         throw new SignatureException(var2.getMessage(), var2);
      }
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      if (this.digest == null) {
         this.digest = this.getSigner();
      }

      try {
         byte[] var1 = this.lmOtsSigner.generateSignature((LMSContext)this.digest);
         this.digest = null;
         return var1;
      } catch (Exception var2) {
         if (var2 instanceof IllegalStateException) {
            throw new SignatureException(var2.getMessage(), var2);
         } else {
            throw new SignatureException(var2.toString(), var2);
         }
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      LMSContext var2 = this.lmOtsVerifier.generateLMSContext(var1);
      byte[] var3 = DigestUtil.getDigestResult(this.digest);
      var2.update(var3, 0, var3.length);
      return this.lmOtsVerifier.verify(var2);
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

   public static class generic extends LMSSignatureSpi {
      public generic() {
         super("LMS", new NullDigest());
      }
   }
}
