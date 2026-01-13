package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class GMSignatureSpi extends java.security.SignatureSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private AlgorithmParameters engineParams;
   private SM2ParameterSpec paramSpec;
   private final SM2Signer signer;

   protected GMSignatureSpi(SM2Signer var1) {
      this.signer = var1;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      Object var2 = ECUtils.generatePublicKeyParameter(var1);
      if (this.paramSpec != null) {
         var2 = new ParametersWithID((CipherParameters)var2, this.paramSpec.getID());
      }

      this.signer.init(false, (CipherParameters)var2);
   }

   @Override
   protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
      Object var2 = ECUtil.generatePrivateKeyParameter(var1);
      if (this.appRandom != null) {
         var2 = new ParametersWithRandom((CipherParameters)var2, this.appRandom);
      }

      if (this.paramSpec != null) {
         this.signer.init(true, new ParametersWithID((CipherParameters)var2, this.paramSpec.getID()));
      } else {
         this.signer.init(true, (CipherParameters)var2);
      }
   }

   @Override
   protected void engineUpdate(byte var1) throws SignatureException {
      this.signer.update(var1);
   }

   @Override
   protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
      this.signer.update(var1, var2, var3);
   }

   @Override
   protected byte[] engineSign() throws SignatureException {
      try {
         return this.signer.generateSignature();
      } catch (CryptoException var2) {
         throw new SignatureException("unable to create signature: " + var2.getMessage());
      }
   }

   @Override
   protected boolean engineVerify(byte[] var1) throws SignatureException {
      return this.signer.verifySignature(var1);
   }

   @Override
   protected void engineSetParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      if (var1 instanceof SM2ParameterSpec) {
         this.paramSpec = (SM2ParameterSpec)var1;
      } else {
         throw new InvalidAlgorithmParameterException("only SM2ParameterSpec supported");
      }
   }

   @Override
   protected AlgorithmParameters engineGetParameters() {
      if (this.engineParams == null && this.paramSpec != null) {
         try {
            this.engineParams = this.helper.createAlgorithmParameters("PSS");
            this.engineParams.init(this.paramSpec);
         } catch (Exception var2) {
            throw new RuntimeException(var2.toString());
         }
      }

      return this.engineParams;
   }

   @Override
   protected void engineSetParameter(String var1, Object var2) {
      throw new UnsupportedOperationException("engineSetParameter unsupported");
   }

   @Override
   protected Object engineGetParameter(String var1) {
      throw new UnsupportedOperationException("engineGetParameter unsupported");
   }

   public static class sha256WithSM2 extends GMSignatureSpi {
      public sha256WithSM2() {
         super(new SM2Signer(SHA256Digest.newInstance()));
      }
   }

   public static class sm3WithSM2 extends GMSignatureSpi {
      public sm3WithSM2() {
         super(new SM2Signer());
      }
   }
}
