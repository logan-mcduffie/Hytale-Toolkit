package org.bouncycastle.jcajce.provider.asymmetric.slhdsa;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseDeterministicOrRandomSignature;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSASigner;

public class SignatureSpi extends BaseDeterministicOrRandomSignature {
   private final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
   private final SLHDSASigner signer;

   protected SignatureSpi(SLHDSASigner var1) {
      super("SLH-DSA");
      this.signer = var1;
   }

   @Override
   protected void verifyInit(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof BCSLHDSAPublicKey) {
         BCSLHDSAPublicKey var2 = (BCSLHDSAPublicKey)var1;
         this.keyParams = var2.getKeyParams();
      } else {
         throw new InvalidKeyException("unknown public key passed to SLH-DSA");
      }
   }

   @Override
   protected void signInit(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.appRandom = var2;
      if (var1 instanceof BCSLHDSAPrivateKey) {
         BCSLHDSAPrivateKey var3 = (BCSLHDSAPrivateKey)var1;
         this.keyParams = var3.getKeyParams();
      } else {
         throw new InvalidKeyException("unknown private key passed to SLH-DSA");
      }
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
      AsymmetricKeyParameter var1 = this.keyParams;
      if (!(var1 instanceof SLHDSAPrivateKeyParameters)) {
         throw new SignatureException("engine initialized for verification");
      } else {
         byte[] var3;
         try {
            byte[] var2 = this.signer.generateSignature(this.bOut.toByteArray());
            var3 = var2;
         } catch (Exception var7) {
            throw new SignatureException(var7.toString());
         } finally {
            this.isInitState = true;
            this.bOut.reset();
         }

         return var3;
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      AsymmetricKeyParameter var2 = this.keyParams;
      if (!(var2 instanceof SLHDSAPublicKeyParameters)) {
         throw new SignatureException("engine initialized for signing");
      } else {
         boolean var3;
         try {
            var3 = this.signer.verifySignature(this.bOut.toByteArray(), var1);
         } finally {
            this.isInitState = true;
            this.bOut.reset();
         }

         return var3;
      }
   }

   @Override
   protected void reInitialize(boolean var1, CipherParameters var2) {
      this.signer.init(var1, var2);
      this.bOut.reset();
   }

   public static class Direct extends SignatureSpi {
      public Direct() {
         super(new SLHDSASigner());
      }
   }
}
