package org.bouncycastle.pqc.jcajce.provider.snova;

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
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaSigner;
import org.bouncycastle.util.Strings;

public class SignatureSpi extends Signature {
   private final ByteArrayOutputStream bOut;
   private final SnovaSigner signer;
   private SecureRandom random;
   private final SnovaParameters parameters;

   protected SignatureSpi(SnovaSigner var1) {
      super("Snova");
      this.bOut = new ByteArrayOutputStream();
      this.signer = var1;
      this.parameters = null;
   }

   protected SignatureSpi(SnovaSigner var1, SnovaParameters var2) {
      super(Strings.toUpperCase(var2.getName()));
      this.parameters = var2;
      this.bOut = new ByteArrayOutputStream();
      this.signer = var1;
   }

   @Override
   protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
      if (!(var1 instanceof BCSnovaPublicKey)) {
         try {
            var1 = new BCSnovaPublicKey(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
         } catch (Exception var4) {
            throw new InvalidKeyException("unknown public key passed to Snova: " + var4.getMessage());
         }
      }

      BCSnovaPublicKey var2 = (BCSnovaPublicKey)var1;
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
      if (var1 instanceof BCSnovaPrivateKey) {
         BCSnovaPrivateKey var2 = (BCSnovaPrivateKey)var1;
         SnovaPrivateKeyParameters var3 = var2.getKeyParams();
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
         throw new InvalidKeyException("unknown private key passed to Snova");
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
         super(new SnovaSigner());
      }
   }

   public static class SNOVA_24_5_4_ESK extends SignatureSpi {
      public SNOVA_24_5_4_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_ESK);
      }
   }

   public static class SNOVA_24_5_4_SHAKE_ESK extends SignatureSpi {
      public SNOVA_24_5_4_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_SHAKE_ESK);
      }
   }

   public static class SNOVA_24_5_4_SHAKE_SSK extends SignatureSpi {
      public SNOVA_24_5_4_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_SHAKE_SSK);
      }
   }

   public static class SNOVA_24_5_4_SSK extends SignatureSpi {
      public SNOVA_24_5_4_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_4_SSK);
      }
   }

   public static class SNOVA_24_5_5_ESK extends SignatureSpi {
      public SNOVA_24_5_5_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_ESK);
      }
   }

   public static class SNOVA_24_5_5_SHAKE_ESK extends SignatureSpi {
      public SNOVA_24_5_5_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_SHAKE_ESK);
      }
   }

   public static class SNOVA_24_5_5_SHAKE_SSK extends SignatureSpi {
      public SNOVA_24_5_5_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_SHAKE_SSK);
      }
   }

   public static class SNOVA_24_5_5_SSK extends SignatureSpi {
      public SNOVA_24_5_5_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_24_5_5_SSK);
      }
   }

   public static class SNOVA_25_8_3_ESK extends SignatureSpi {
      public SNOVA_25_8_3_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_ESK);
      }
   }

   public static class SNOVA_25_8_3_SHAKE_ESK extends SignatureSpi {
      public SNOVA_25_8_3_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_SHAKE_ESK);
      }
   }

   public static class SNOVA_25_8_3_SHAKE_SSK extends SignatureSpi {
      public SNOVA_25_8_3_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_SHAKE_SSK);
      }
   }

   public static class SNOVA_25_8_3_SSK extends SignatureSpi {
      public SNOVA_25_8_3_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_25_8_3_SSK);
      }
   }

   public static class SNOVA_29_6_5_ESK extends SignatureSpi {
      public SNOVA_29_6_5_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_ESK);
      }
   }

   public static class SNOVA_29_6_5_SHAKE_ESK extends SignatureSpi {
      public SNOVA_29_6_5_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_SHAKE_ESK);
      }
   }

   public static class SNOVA_29_6_5_SHAKE_SSK extends SignatureSpi {
      public SNOVA_29_6_5_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_SHAKE_SSK);
      }
   }

   public static class SNOVA_29_6_5_SSK extends SignatureSpi {
      public SNOVA_29_6_5_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_29_6_5_SSK);
      }
   }

   public static class SNOVA_37_17_2_ESK extends SignatureSpi {
      public SNOVA_37_17_2_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_ESK);
      }
   }

   public static class SNOVA_37_17_2_SHAKE_ESK extends SignatureSpi {
      public SNOVA_37_17_2_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_SHAKE_ESK);
      }
   }

   public static class SNOVA_37_17_2_SHAKE_SSK extends SignatureSpi {
      public SNOVA_37_17_2_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_SHAKE_SSK);
      }
   }

   public static class SNOVA_37_17_2_SSK extends SignatureSpi {
      public SNOVA_37_17_2_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_17_2_SSK);
      }
   }

   public static class SNOVA_37_8_4_ESK extends SignatureSpi {
      public SNOVA_37_8_4_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_ESK);
      }
   }

   public static class SNOVA_37_8_4_SHAKE_ESK extends SignatureSpi {
      public SNOVA_37_8_4_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_SHAKE_ESK);
      }
   }

   public static class SNOVA_37_8_4_SHAKE_SSK extends SignatureSpi {
      public SNOVA_37_8_4_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_SHAKE_SSK);
      }
   }

   public static class SNOVA_37_8_4_SSK extends SignatureSpi {
      public SNOVA_37_8_4_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_37_8_4_SSK);
      }
   }

   public static class SNOVA_49_11_3_ESK extends SignatureSpi {
      public SNOVA_49_11_3_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_ESK);
      }
   }

   public static class SNOVA_49_11_3_SHAKE_ESK extends SignatureSpi {
      public SNOVA_49_11_3_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_SHAKE_ESK);
      }
   }

   public static class SNOVA_49_11_3_SHAKE_SSK extends SignatureSpi {
      public SNOVA_49_11_3_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_SHAKE_SSK);
      }
   }

   public static class SNOVA_49_11_3_SSK extends SignatureSpi {
      public SNOVA_49_11_3_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_49_11_3_SSK);
      }
   }

   public static class SNOVA_56_25_2_ESK extends SignatureSpi {
      public SNOVA_56_25_2_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_ESK);
      }
   }

   public static class SNOVA_56_25_2_SHAKE_ESK extends SignatureSpi {
      public SNOVA_56_25_2_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_SHAKE_ESK);
      }
   }

   public static class SNOVA_56_25_2_SHAKE_SSK extends SignatureSpi {
      public SNOVA_56_25_2_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_SHAKE_SSK);
      }
   }

   public static class SNOVA_56_25_2_SSK extends SignatureSpi {
      public SNOVA_56_25_2_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_56_25_2_SSK);
      }
   }

   public static class SNOVA_60_10_4_ESK extends SignatureSpi {
      public SNOVA_60_10_4_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_ESK);
      }
   }

   public static class SNOVA_60_10_4_SHAKE_ESK extends SignatureSpi {
      public SNOVA_60_10_4_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_SHAKE_ESK);
      }
   }

   public static class SNOVA_60_10_4_SHAKE_SSK extends SignatureSpi {
      public SNOVA_60_10_4_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_SHAKE_SSK);
      }
   }

   public static class SNOVA_60_10_4_SSK extends SignatureSpi {
      public SNOVA_60_10_4_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_60_10_4_SSK);
      }
   }

   public static class SNOVA_66_15_3_ESK extends SignatureSpi {
      public SNOVA_66_15_3_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_ESK);
      }
   }

   public static class SNOVA_66_15_3_SHAKE_ESK extends SignatureSpi {
      public SNOVA_66_15_3_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_SHAKE_ESK);
      }
   }

   public static class SNOVA_66_15_3_SHAKE_SSK extends SignatureSpi {
      public SNOVA_66_15_3_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_SHAKE_SSK);
      }
   }

   public static class SNOVA_66_15_3_SSK extends SignatureSpi {
      public SNOVA_66_15_3_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_66_15_3_SSK);
      }
   }

   public static class SNOVA_75_33_2_ESK extends SignatureSpi {
      public SNOVA_75_33_2_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_ESK);
      }
   }

   public static class SNOVA_75_33_2_SHAKE_ESK extends SignatureSpi {
      public SNOVA_75_33_2_SHAKE_ESK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_SHAKE_ESK);
      }
   }

   public static class SNOVA_75_33_2_SHAKE_SSK extends SignatureSpi {
      public SNOVA_75_33_2_SHAKE_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_SHAKE_SSK);
      }
   }

   public static class SNOVA_75_33_2_SSK extends SignatureSpi {
      public SNOVA_75_33_2_SSK() {
         super(new SnovaSigner(), SnovaParameters.SNOVA_75_33_2_SSK);
      }
   }
}
