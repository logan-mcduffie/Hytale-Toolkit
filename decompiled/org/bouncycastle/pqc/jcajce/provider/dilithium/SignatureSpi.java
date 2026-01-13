package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumSigner;
import org.bouncycastle.util.Strings;

public class SignatureSpi extends Signature {
   private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
   private DilithiumSigner signer;
   private SecureRandom random;
   private DilithiumParameters parameters;

   protected SignatureSpi(DilithiumSigner var1) {
      super("Dilithium");
      this.signer = var1;
      this.parameters = null;
   }

   protected SignatureSpi(DilithiumSigner var1, DilithiumParameters var2) {
      super(Strings.toUpperCase(var2.getName()));
      this.signer = var1;
      this.parameters = var2;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof BCDilithiumPublicKey)) {
         try {
            var1 = new BCDilithiumPublicKey(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
         } catch (Exception var4) {
            throw new InvalidKeyException("unknown public key passed to Dilithium: " + var4.getMessage(), var4);
         }
      }

      BCDilithiumPublicKey var2 = (BCDilithiumPublicKey)var1;
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
      if (var1 instanceof BCDilithiumPrivateKey) {
         BCDilithiumPrivateKey var2 = (BCDilithiumPrivateKey)var1;
         DilithiumPrivateKeyParameters var3 = var2.getKeyParams();
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
         throw new InvalidKeyException("unknown private key passed to Dilithium");
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
         super(new DilithiumSigner());
      }
   }

   public static class Base2 extends SignatureSpi {
      public Base2() {
         super(new DilithiumSigner(), DilithiumParameters.dilithium2);
      }
   }

   public static class Base3 extends SignatureSpi {
      public Base3() {
         super(new DilithiumSigner(), DilithiumParameters.dilithium3);
      }
   }

   public static class Base5 extends SignatureSpi {
      public Base5() throws NoSuchAlgorithmException {
         super(new DilithiumSigner(), DilithiumParameters.dilithium5);
      }
   }
}
