package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.jcajce.MLDSAProxyPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSASigner;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;

public class SignatureSpi extends BaseDeterministicOrRandomSignature {
   protected MLDSASigner signer;
   protected MLDSAParameters parameters;

   protected SignatureSpi(MLDSASigner var1) {
      super("MLDSA");
      this.signer = var1;
      this.parameters = null;
   }

   protected SignatureSpi(MLDSASigner var1, MLDSAParameters var2) {
      super(MLDSAParameterSpec.fromName(var2.getName()).getName());
      this.signer = var1;
      this.parameters = var2;
   }

   @Override
   protected void verifyInit(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCMLDSAPublicKey) {
         BCMLDSAPublicKey var2 = (BCMLDSAPublicKey)var1;
         this.keyParams = var2.getKeyParams();
      } else {
         try {
            SubjectPublicKeyInfo var4 = SubjectPublicKeyInfo.getInstance(var1.getEncoded());
            this.keyParams = PublicKeyFactory.createKey(var4);
            var1 = new BCMLDSAPublicKey((MLDSAPublicKeyParameters)this.keyParams);
         } catch (Exception var3) {
            throw new InvalidKeyException("unknown public key passed to ML-DSA");
         }
      }

      if (this.parameters != null) {
         String var5 = MLDSAParameterSpec.fromName(this.parameters.getName()).getName();
         if (!var5.equals(var1.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + var5);
         }
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
         if (!(var1 instanceof MLDSAProxyPrivateKey) || !(this instanceof SignatureSpi.MLDSACalcMu)) {
            throw new InvalidKeyException("unknown private key passed to ML-DSA");
         }

         MLDSAProxyPrivateKey var7 = (MLDSAProxyPrivateKey)var1;
         MLDSAPublicKey var8 = var7.getPublicKey();

         try {
            this.keyParams = PublicKeyFactory.createKey(var8.getEncoded());
         } catch (IOException var6) {
            throw new InvalidKeyException(var6.getMessage());
         }

         if (this.parameters != null) {
            String var5 = MLDSAParameterSpec.fromName(this.parameters.getName()).getName();
            if (!var5.equals(var8.getAlgorithm())) {
               throw new InvalidKeyException("signature configured for " + var5);
            }
         }
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

   public static class MLDSA extends SignatureSpi {
      public MLDSA() {
         super(new MLDSASigner());
      }
   }

   public static class MLDSA44 extends SignatureSpi {
      public MLDSA44() {
         super(new MLDSASigner(), MLDSAParameters.ml_dsa_44);
      }
   }

   public static class MLDSA65 extends SignatureSpi {
      public MLDSA65() {
         super(new MLDSASigner(), MLDSAParameters.ml_dsa_65);
      }
   }

   public static class MLDSA87 extends SignatureSpi {
      public MLDSA87() throws NoSuchAlgorithmException {
         super(new MLDSASigner(), MLDSAParameters.ml_dsa_87);
      }
   }

   public static class MLDSACalcMu extends SignatureSpi {
      public MLDSACalcMu() {
         super(new MLDSASigner());
      }

      @Override
      protected byte[] engineSign() throws SignatureException {
         try {
            return this.signer.generateMu();
         } catch (Exception var2) {
            throw new SignatureException(var2.toString());
         }
      }

      @Override
      protected boolean engineVerify(byte[] var1) throws SignatureException {
         return this.signer.verifyMu(var1);
      }
   }

   public static class MLDSAExtMu extends SignatureSpi {
      private ByteArrayOutputStream bOut = new ByteArrayOutputStream(64);

      public MLDSAExtMu() {
         super(new MLDSASigner());
      }

      @Override
      protected void updateEngine(byte var1) throws SignatureException {
         this.bOut.write(var1);
      }

      @Override
      protected void updateEngine(byte[] var1, int var2, int var3) throws SignatureException {
         this.bOut.write(var1, var2, var3);
      }

      @Override
      protected byte[] engineSign() throws SignatureException {
         try {
            byte[] var1 = this.bOut.toByteArray();
            this.bOut.reset();
            return this.signer.generateMuSignature(var1);
         } catch (DataLengthException var2) {
            throw new SignatureException(var2.getMessage());
         } catch (Exception var3) {
            throw new SignatureException(var3.toString());
         }
      }

      @Override
      protected boolean engineVerify(byte[] var1) throws SignatureException {
         byte[] var2 = this.bOut.toByteArray();
         this.bOut.reset();

         try {
            return this.signer.verifyMuSignature(var2, var1);
         } catch (DataLengthException var4) {
            throw new SignatureException(var4.getMessage());
         }
      }
   }
}
