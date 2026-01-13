package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.HashMLDSASigner;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;

public class HashSignatureSpi extends BaseDeterministicOrRandomSignature {
   private HashMLDSASigner signer;
   private MLDSAParameters parameters;

   protected HashSignatureSpi(HashMLDSASigner var1) {
      super("HashMLDSA");
      this.signer = var1;
      this.parameters = null;
   }

   protected HashSignatureSpi(HashMLDSASigner var1, MLDSAParameters var2) {
      super(MLDSAParameterSpec.fromName(var2.getName()).getName());
      this.signer = var1;
      this.parameters = var2;
   }

   @Override
   protected void verifyInit(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCMLDSAPublicKey) {
         BCMLDSAPublicKey var2 = (BCMLDSAPublicKey)var1;
         this.keyParams = var2.getKeyParams();
         if (this.parameters != null) {
            String var3 = MLDSAParameterSpec.fromName(this.parameters.getName()).getName();
            if (!var3.equals(var2.getAlgorithm())) {
               throw new InvalidKeyException("signature configured for " + var3);
            }
         }
      } else {
         throw new InvalidKeyException("unknown public key passed to ML-DSA");
      }
   }

   @Override
   protected void signInit(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.appRandom = var2;
      if (var1 instanceof BCMLDSAPrivateKey) {
         BCMLDSAPrivateKey var3 = (BCMLDSAPrivateKey)var1;
         this.keyParams = var3.getKeyParams();
         if (this.parameters != null) {
            String var4 = MLDSAParameterSpec.fromName(this.parameters.getName()).getName();
            if (!var4.equals(var3.getAlgorithm())) {
               throw new InvalidKeyException("signature configured for " + var4);
            }
         }
      } else {
         throw new InvalidKeyException("unknown private key passed to ML-DSA");
      }
   }

   @Override
   protected void updateEngine(byte var1) throws SignatureException {
      this.signer.update(var1);
   }

   @Override
   protected void updateEngine(byte[] var1, int var2, int var3) throws SignatureException {
      this.signer.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      try {
         return this.signer.generateSignature();
      } catch (Exception var2) {
         throw new SignatureException(var2.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      return this.signer.verifySignature(var1);
   }

   @Override
   protected void reInitialize(boolean var1, CipherParameters var2) {
      this.signer.init(var1, var2);
   }

   public static class MLDSA extends HashSignatureSpi {
      public MLDSA() {
         super(new HashMLDSASigner());
      }
   }

   public static class MLDSA44 extends HashSignatureSpi {
      public MLDSA44() {
         super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_44_with_sha512);
      }
   }

   public static class MLDSA65 extends HashSignatureSpi {
      public MLDSA65() {
         super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_65_with_sha512);
      }
   }

   public static class MLDSA87 extends HashSignatureSpi {
      public MLDSA87() throws NoSuchAlgorithmException {
         super(new HashMLDSASigner(), MLDSAParameters.ml_dsa_87_with_sha512);
      }
   }
}
