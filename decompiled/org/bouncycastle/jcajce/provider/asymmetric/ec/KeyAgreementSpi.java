package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCUnifiedAgreement;
import org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.interfaces.MQVPrivateKey;
import org.bouncycastle.jce.interfaces.MQVPublicKey;
import org.bouncycastle.util.Arrays;

public class KeyAgreementSpi extends BaseAgreementSpi {
   private static final X9IntegerConverter converter = new X9IntegerConverter();
   private String kaAlgorithm;
   private ECDomainParameters parameters;
   private Object agreement;
   private MQVParameterSpec mqvParameters;
   private DHUParameterSpec dheParameters;
   private byte[] result;

   protected KeyAgreementSpi(String var1, BasicAgreement var2, DerivationFunction var3) {
      super(var1, var3);
      this.kaAlgorithm = var1;
      this.agreement = var2;
   }

   protected KeyAgreementSpi(String var1, ECDHCUnifiedAgreement var2, DerivationFunction var3) {
      super(var1, var3);
      this.kaAlgorithm = var1;
      this.agreement = var2;
   }

   protected byte[] bigIntToBytes(BigInteger var1) {
      return converter.integerToBytes(var1, converter.getByteLength(this.parameters.getCurve()));
   }

   @Override
   protected Key engineDoPhase(Key var1, boolean var2) throws InvalidKeyException, IllegalStateException {
      if (this.parameters == null) {
         throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
      } else if (!var2) {
         throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
      } else {
         Object var3;
         if (this.agreement instanceof ECMQVBasicAgreement) {
            if (!(var1 instanceof MQVPublicKey)) {
               ECPublicKeyParameters var4 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter((PublicKey)var1);
               ECPublicKeyParameters var5 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(this.mqvParameters.getOtherPartyEphemeralKey());
               var3 = new MQVPublicParameters(var4, var5);
            } else {
               MQVPublicKey var8 = (MQVPublicKey)var1;
               ECPublicKeyParameters var10 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(var8.getStaticKey());
               ECPublicKeyParameters var6 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(var8.getEphemeralKey());
               var3 = new MQVPublicParameters(var10, var6);
            }
         } else if (this.agreement instanceof ECDHCUnifiedAgreement) {
            ECPublicKeyParameters var9 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter((PublicKey)var1);
            ECPublicKeyParameters var11 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(this.dheParameters.getOtherPartyEphemeralKey());
            var3 = new ECDHUPublicParameters(var9, var11);
         } else {
            if (!(var1 instanceof PublicKey)) {
               throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase");
            }

            var3 = ECUtils.generatePublicKeyParameter((PublicKey)var1);
         }

         try {
            if (this.agreement instanceof BasicAgreement) {
               this.result = this.bigIntToBytes(((BasicAgreement)this.agreement).calculateAgreement((CipherParameters)var3));
            } else {
               this.result = ((ECDHCUnifiedAgreement)this.agreement).calculateAgreement((CipherParameters)var3);
            }

            return null;
         } catch (final Exception var7) {
            throw new InvalidKeyException("calculation failed: " + var7.getMessage()) {
               @Override
               public Throwable getCause() {
                  return var7;
               }
            };
         }
      }
   }

   @Override
   protected void doInitFromKey(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var2 != null && !(var2 instanceof MQVParameterSpec) && !(var2 instanceof UserKeyingMaterialSpec) && !(var2 instanceof DHUParameterSpec)) {
         throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
      } else {
         if (this.agreement instanceof ECMQVBasicAgreement) {
            this.mqvParameters = null;
            if (!(var1 instanceof MQVPrivateKey) && !(var2 instanceof MQVParameterSpec)) {
               throw new InvalidAlgorithmParameterException(
                  this.kaAlgorithm + " key agreement requires " + getSimpleName(MQVParameterSpec.class) + " for initialisation"
               );
            }

            ECPrivateKeyParameters var4;
            ECPrivateKeyParameters var5;
            ECPublicKeyParameters var6;
            if (var1 instanceof MQVPrivateKey) {
               MQVPrivateKey var7 = (MQVPrivateKey)var1;
               var4 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter(var7.getStaticPrivateKey());
               var5 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter(var7.getEphemeralPrivateKey());
               var6 = null;
               if (var7.getEphemeralPublicKey() != null) {
                  var6 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(var7.getEphemeralPublicKey());
               }
            } else {
               MQVParameterSpec var13 = (MQVParameterSpec)var2;
               var4 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter((PrivateKey)var1);
               var5 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter(var13.getEphemeralPrivateKey());
               var6 = null;
               if (var13.getEphemeralPublicKey() != null) {
                  var6 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(var13.getEphemeralPublicKey());
               }

               this.mqvParameters = var13;
               this.ukmParameters = var13.getUserKeyingMaterial();
            }

            MQVPrivateParameters var14 = new MQVPrivateParameters(var4, var5, var6);
            this.parameters = var4.getParameters();
            ((ECMQVBasicAgreement)this.agreement).init(var14);
         } else if (var2 instanceof DHUParameterSpec) {
            if (!(this.agreement instanceof ECDHCUnifiedAgreement)) {
               throw new InvalidAlgorithmParameterException(this.kaAlgorithm + " key agreement cannot be used with " + getSimpleName(DHUParameterSpec.class));
            }

            DHUParameterSpec var9 = (DHUParameterSpec)var2;
            ECPrivateKeyParameters var11 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter((PrivateKey)var1);
            ECPrivateKeyParameters var12 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter(var9.getEphemeralPrivateKey());
            ECPublicKeyParameters var15 = null;
            if (var9.getEphemeralPublicKey() != null) {
               var15 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(var9.getEphemeralPublicKey());
            }

            this.dheParameters = var9;
            this.ukmParameters = var9.getUserKeyingMaterial();
            ECDHUPrivateParameters var8 = new ECDHUPrivateParameters(var11, var12, var15);
            this.parameters = var11.getParameters();
            ((ECDHCUnifiedAgreement)this.agreement).init(var8);
         } else {
            if (!(var1 instanceof PrivateKey)) {
               throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation");
            }

            if (this.kdf == null && var2 instanceof UserKeyingMaterialSpec) {
               throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
            }

            ECPrivateKeyParameters var10 = (ECPrivateKeyParameters)ECUtils.generatePrivateKeyParameter((PrivateKey)var1);
            this.parameters = var10.getParameters();
            this.ukmParameters = var2 instanceof UserKeyingMaterialSpec ? ((UserKeyingMaterialSpec)var2).getUserKeyingMaterial() : null;
            ((BasicAgreement)this.agreement).init(var10);
         }
      }
   }

   private static String getSimpleName(Class var0) {
      String var1 = var0.getName();
      return var1.substring(var1.lastIndexOf(46) + 1);
   }

   @Override
   protected byte[] doCalcSecret() {
      return Arrays.clone(this.result);
   }

   public static class CDHwithSHA1KDFAndSharedInfo extends KeyAgreementSpi {
      public CDHwithSHA1KDFAndSharedInfo() {
         super("ECCDHwithSHA1KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class CDHwithSHA224KDFAndSharedInfo extends KeyAgreementSpi {
      public CDHwithSHA224KDFAndSharedInfo() {
         super("ECCDHwithSHA224KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class CDHwithSHA256KDFAndSharedInfo extends KeyAgreementSpi {
      public CDHwithSHA256KDFAndSharedInfo() {
         super("ECCDHwithSHA256KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class CDHwithSHA384KDFAndSharedInfo extends KeyAgreementSpi {
      public CDHwithSHA384KDFAndSharedInfo() {
         super("ECCDHwithSHA384KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class CDHwithSHA512KDFAndSharedInfo extends KeyAgreementSpi {
      public CDHwithSHA512KDFAndSharedInfo() {
         super("ECCDHwithSHA512KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DH extends KeyAgreementSpi {
      public DH() {
         super("ECDH", new ECDHBasicAgreement(), null);
      }
   }

   public static class DHC extends KeyAgreementSpi {
      public DHC() {
         super("ECDHC", new ECDHCBasicAgreement(), null);
      }
   }

   public static class DHUC extends KeyAgreementSpi {
      public DHUC() {
         super("ECCDHU", new ECDHCUnifiedAgreement(), null);
      }
   }

   public static class DHUwithSHA1CKDF extends KeyAgreementSpi {
      public DHUwithSHA1CKDF() {
         super("ECCDHUwithSHA1CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHUwithSHA1KDF extends KeyAgreementSpi {
      public DHUwithSHA1KDF() {
         super("ECCDHUwithSHA1KDF", new ECDHCUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHUwithSHA224CKDF extends KeyAgreementSpi {
      public DHUwithSHA224CKDF() {
         super("ECCDHUwithSHA224CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHUwithSHA224KDF extends KeyAgreementSpi {
      public DHUwithSHA224KDF() {
         super("ECCDHUwithSHA224KDF", new ECDHCUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHUwithSHA256CKDF extends KeyAgreementSpi {
      public DHUwithSHA256CKDF() {
         super("ECCDHUwithSHA256CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHUwithSHA256KDF extends KeyAgreementSpi {
      public DHUwithSHA256KDF() {
         super("ECCDHUwithSHA256KDF", new ECDHCUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHUwithSHA384CKDF extends KeyAgreementSpi {
      public DHUwithSHA384CKDF() {
         super("ECCDHUwithSHA384CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHUwithSHA384KDF extends KeyAgreementSpi {
      public DHUwithSHA384KDF() {
         super("ECCDHUwithSHA384KDF", new ECDHCUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHUwithSHA512CKDF extends KeyAgreementSpi {
      public DHUwithSHA512CKDF() {
         super("ECCDHUwithSHA512CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DHUwithSHA512KDF extends KeyAgreementSpi {
      public DHUwithSHA512KDF() {
         super("ECCDHUwithSHA512KDF", new ECDHCUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DHwithSHA1CKDF extends KeyAgreementSpi {
      public DHwithSHA1CKDF() {
         super("ECDHwithSHA1CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHwithSHA1KDF extends KeyAgreementSpi {
      public DHwithSHA1KDF() {
         super("ECDHwithSHA1KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHwithSHA1KDFAndSharedInfo extends KeyAgreementSpi {
      public DHwithSHA1KDFAndSharedInfo() {
         super("ECDHwithSHA1KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHwithSHA224KDFAndSharedInfo extends KeyAgreementSpi {
      public DHwithSHA224KDFAndSharedInfo() {
         super("ECDHwithSHA224KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHwithSHA256CKDF extends KeyAgreementSpi {
      public DHwithSHA256CKDF() {
         super("ECDHwithSHA256CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHwithSHA256KDFAndSharedInfo extends KeyAgreementSpi {
      public DHwithSHA256KDFAndSharedInfo() {
         super("ECDHwithSHA256KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHwithSHA384CKDF extends KeyAgreementSpi {
      public DHwithSHA384CKDF() {
         super("ECDHwithSHA384CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHwithSHA384KDFAndSharedInfo extends KeyAgreementSpi {
      public DHwithSHA384KDFAndSharedInfo() {
         super("ECDHwithSHA384KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHwithSHA512CKDF extends KeyAgreementSpi {
      public DHwithSHA512CKDF() {
         super("ECDHwithSHA512CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DHwithSHA512KDFAndSharedInfo extends KeyAgreementSpi {
      public DHwithSHA512KDFAndSharedInfo() {
         super("ECDHwithSHA512KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class ECKAEGwithRIPEMD160KDF extends KeyAgreementSpi {
      public ECKAEGwithRIPEMD160KDF() {
         super("ECKAEGwithRIPEMD160KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(new RIPEMD160Digest()));
      }
   }

   public static class ECKAEGwithSHA1KDF extends KeyAgreementSpi {
      public ECKAEGwithSHA1KDF() {
         super("ECKAEGwithSHA1KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class ECKAEGwithSHA224KDF extends KeyAgreementSpi {
      public ECKAEGwithSHA224KDF() {
         super("ECKAEGwithSHA224KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class ECKAEGwithSHA256KDF extends KeyAgreementSpi {
      public ECKAEGwithSHA256KDF() {
         super("ECKAEGwithSHA256KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class ECKAEGwithSHA384KDF extends KeyAgreementSpi {
      public ECKAEGwithSHA384KDF() {
         super("ECKAEGwithSHA384KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class ECKAEGwithSHA512KDF extends KeyAgreementSpi {
      public ECKAEGwithSHA512KDF() {
         super("ECKAEGwithSHA512KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class MQV extends KeyAgreementSpi {
      public MQV() {
         super("ECMQV", new ECMQVBasicAgreement(), null);
      }
   }

   public static class MQVwithSHA1CKDF extends KeyAgreementSpi {
      public MQVwithSHA1CKDF() {
         super("ECMQVwithSHA1CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class MQVwithSHA1KDF extends KeyAgreementSpi {
      public MQVwithSHA1KDF() {
         super("ECMQVwithSHA1KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class MQVwithSHA1KDFAndSharedInfo extends KeyAgreementSpi {
      public MQVwithSHA1KDFAndSharedInfo() {
         super("ECMQVwithSHA1KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class MQVwithSHA224CKDF extends KeyAgreementSpi {
      public MQVwithSHA224CKDF() {
         super("ECMQVwithSHA224CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class MQVwithSHA224KDF extends KeyAgreementSpi {
      public MQVwithSHA224KDF() {
         super("ECMQVwithSHA224KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class MQVwithSHA224KDFAndSharedInfo extends KeyAgreementSpi {
      public MQVwithSHA224KDFAndSharedInfo() {
         super("ECMQVwithSHA224KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class MQVwithSHA256CKDF extends KeyAgreementSpi {
      public MQVwithSHA256CKDF() {
         super("ECMQVwithSHA256CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class MQVwithSHA256KDF extends KeyAgreementSpi {
      public MQVwithSHA256KDF() {
         super("ECMQVwithSHA256KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class MQVwithSHA256KDFAndSharedInfo extends KeyAgreementSpi {
      public MQVwithSHA256KDFAndSharedInfo() {
         super("ECMQVwithSHA256KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class MQVwithSHA384CKDF extends KeyAgreementSpi {
      public MQVwithSHA384CKDF() {
         super("ECMQVwithSHA384CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class MQVwithSHA384KDF extends KeyAgreementSpi {
      public MQVwithSHA384KDF() {
         super("ECMQVwithSHA384KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class MQVwithSHA384KDFAndSharedInfo extends KeyAgreementSpi {
      public MQVwithSHA384KDFAndSharedInfo() {
         super("ECMQVwithSHA384KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class MQVwithSHA512CKDF extends KeyAgreementSpi {
      public MQVwithSHA512CKDF() {
         super("ECMQVwithSHA512CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class MQVwithSHA512KDF extends KeyAgreementSpi {
      public MQVwithSHA512KDF() {
         super("ECMQVwithSHA512KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class MQVwithSHA512KDFAndSharedInfo extends KeyAgreementSpi {
      public MQVwithSHA512KDFAndSharedInfo() {
         super("ECMQVwithSHA512KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }
}
