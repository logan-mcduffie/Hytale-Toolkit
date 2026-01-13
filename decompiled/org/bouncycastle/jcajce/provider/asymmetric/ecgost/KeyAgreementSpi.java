package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.ECVKOAgreement;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

public class KeyAgreementSpi extends BaseAgreementSpi {
   private static final X9IntegerConverter converter = new X9IntegerConverter();
   private String kaAlgorithm;
   private ECDomainParameters parameters;
   private ECVKOAgreement agreement;
   private byte[] result;

   protected KeyAgreementSpi(String var1, ECVKOAgreement var2, DerivationFunction var3) {
      super(var1, var3);
      this.kaAlgorithm = var1;
      this.agreement = var2;
   }

   @Override
   protected Key engineDoPhase(Key var1, boolean var2) throws InvalidKeyException, IllegalStateException {
      if (this.parameters == null) {
         throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
      } else if (!var2) {
         throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
      } else if (!(var1 instanceof PublicKey)) {
         throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase");
      } else {
         AsymmetricKeyParameter var3 = generatePublicKeyParameter((PublicKey)var1);

         try {
            this.result = this.agreement.calculateAgreement(var3);
            return null;
         } catch (final Exception var5) {
            throw new InvalidKeyException("calculation failed: " + var5.getMessage()) {
               @Override
               public Throwable getCause() {
                  return var5;
               }
            };
         }
      }
   }

   @Override
   protected void doInitFromKey(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (!(var1 instanceof PrivateKey)) {
         throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation");
      } else if (var2 != null && !(var2 instanceof UserKeyingMaterialSpec)) {
         throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
      } else {
         ECPrivateKeyParameters var4 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)var1);
         this.parameters = var4.getParameters();
         this.ukmParameters = var2 instanceof UserKeyingMaterialSpec ? ((UserKeyingMaterialSpec)var2).getUserKeyingMaterial() : null;
         this.agreement.init(new ParametersWithUKM(var4, this.ukmParameters));
      }
   }

   private static String getSimpleName(Class var0) {
      String var1 = var0.getName();
      return var1.substring(var1.lastIndexOf(46) + 1);
   }

   @Override
   protected byte[] doCalcSecret() {
      return this.result;
   }

   static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      return (AsymmetricKeyParameter)(var0 instanceof BCECPublicKey
         ? ((BCECGOST3410PublicKey)var0).engineGetKeyParameters()
         : ECUtil.generatePublicKeyParameter(var0));
   }

   public static class ECVKO extends KeyAgreementSpi {
      public ECVKO() {
         super("ECGOST3410", new ECVKOAgreement(new GOST3411Digest()), null);
      }
   }
}
