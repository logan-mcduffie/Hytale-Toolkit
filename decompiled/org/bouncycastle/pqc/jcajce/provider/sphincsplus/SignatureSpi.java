package org.bouncycastle.pqc.jcajce.provider.sphincsplus;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusSigner;

public class SignatureSpi extends java.security.SignatureSpi {
   private final Digest digest;
   private final SPHINCSPlusSigner signer;

   protected SignatureSpi(Digest var1, SPHINCSPlusSigner var2) {
      this.digest = var1;
      this.signer = var2;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCSPHINCSPlusPublicKey) {
         BCSPHINCSPlusPublicKey var2 = (BCSPHINCSPlusPublicKey)var1;
         CipherParameters var3 = var2.getKeyParams();
         this.signer.init(false, var3);
      } else {
         throw new InvalidKeyException("unknown public key passed to SPHINCS+");
      }
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.appRandom = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCSPHINCSPlusPrivateKey) {
         BCSPHINCSPlusPrivateKey var2 = (BCSPHINCSPlusPrivateKey)var1;
         SPHINCSPlusPrivateKeyParameters var3 = var2.getKeyParams();
         if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(var3, this.appRandom));
         } else {
            this.signer.init(true, var3);
         }
      } else {
         throw new InvalidKeyException("unknown private key passed to SPHINCS+");
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

   public static class Direct extends SignatureSpi {
      public Direct() {
         super(new NullDigest(), new SPHINCSPlusSigner());
      }
   }
}
