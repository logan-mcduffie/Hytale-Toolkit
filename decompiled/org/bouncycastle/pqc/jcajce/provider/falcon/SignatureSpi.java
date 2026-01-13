package org.bouncycastle.pqc.jcajce.provider.falcon;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconSigner;
import org.bouncycastle.util.Strings;

public class SignatureSpi extends Signature {
   private ByteArrayOutputStream bOut;
   private FalconSigner signer;
   private SecureRandom random;
   private FalconParameters parameters;

   protected SignatureSpi(FalconSigner var1) {
      super("FALCON");
      this.bOut = new ByteArrayOutputStream();
      this.signer = var1;
      this.parameters = null;
   }

   protected SignatureSpi(FalconSigner var1, FalconParameters var2) {
      super(Strings.toUpperCase(var2.getName()));
      this.parameters = var2;
      this.bOut = new ByteArrayOutputStream();
      this.signer = var1;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof BCFalconPublicKey)) {
         try {
            var1 = new BCFalconPublicKey(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
         } catch (Exception var4) {
            throw new InvalidKeyException("unknown public key passed to Falcon: " + var4.getMessage(), var4);
         }
      }

      BCFalconPublicKey var2 = (BCFalconPublicKey)var1;
      if (this.parameters != null) {
         String var3 = Strings.toUpperCase(this.parameters.getName());
         if (!var3.equals(var2.getAlgorithm())) {
            throw new InvalidKeyException("signature configured for " + var3);
         }
      }

      this.signer.init(false, var2.getKeyParams());
   }

   @Override
   protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.random = var2;
      this.engineInitSign(var1);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof BCFalconPrivateKey) {
         BCFalconPrivateKey var2 = (BCFalconPrivateKey)var1;
         FalconPrivateKeyParameters var3 = var2.getKeyParams();
         if (this.parameters != null) {
            String var4 = Strings.toUpperCase(this.parameters.getName());
            if (!var4.equals(var2.getAlgorithm())) {
               throw new InvalidKeyException("signature configured for " + var4);
            }
         }

         if (this.random != null) {
            this.signer.init(true, new ParametersWithRandom(var3, this.random));
         } else {
            this.signer.init(true, var3);
         }
      } else {
         throw new InvalidKeyException("unknown private key passed to Falcon");
      }
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.bOut.write(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.bOut.write(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      try {
         byte[] var1 = this.bOut.toByteArray();
         this.bOut.reset();
         return this.signer.generateSignature(var1);
      } catch (Exception var2) {
         throw new SignatureException(var2.toString());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      byte[] var2 = this.bOut.toByteArray();
      this.bOut.reset();
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
         super(new FalconSigner());
      }
   }

   public static class Falcon1024 extends SignatureSpi {
      public Falcon1024() {
         super(new FalconSigner(), FalconParameters.falcon_1024);
      }
   }

   public static class Falcon512 extends SignatureSpi {
      public Falcon512() {
         super(new FalconSigner(), FalconParameters.falcon_512);
      }
   }
}
