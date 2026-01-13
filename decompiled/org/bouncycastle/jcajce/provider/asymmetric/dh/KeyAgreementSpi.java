package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.DHUnifiedAgreement;
import org.bouncycastle.crypto.agreement.MQVBasicAgreement;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.DHMQVPrivateParameters;
import org.bouncycastle.crypto.params.DHMQVPublicParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHUPrivateParameters;
import org.bouncycastle.crypto.params.DHUPublicParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.util.BigIntegers;

public class KeyAgreementSpi extends BaseAgreementSpi {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);
   private final DHUnifiedAgreement unifiedAgreement;
   private final BasicAgreement mqvAgreement;
   private DHUParameterSpec dheParameters;
   private MQVParameterSpec mqvParameters;
   private BigInteger x;
   private BigInteger p;
   private BigInteger g;
   private byte[] result;

   public KeyAgreementSpi() {
      this("Diffie-Hellman", null);
   }

   public KeyAgreementSpi(String var1, DerivationFunction var2) {
      super(var1, var2);
      this.unifiedAgreement = null;
      this.mqvAgreement = null;
   }

   public KeyAgreementSpi(String var1, DHUnifiedAgreement var2, DerivationFunction var3) {
      super(var1, var3);
      this.unifiedAgreement = var2;
      this.mqvAgreement = null;
   }

   public KeyAgreementSpi(String var1, BasicAgreement var2, DerivationFunction var3) {
      super(var1, var3);
      this.unifiedAgreement = null;
      this.mqvAgreement = var2;
   }

   protected byte[] bigIntToBytes(BigInteger var1) {
      int var2 = (this.p.bitLength() + 7) / 8;
      return BigIntegers.asUnsignedByteArray(var2, var1);
   }

   @Override
   protected Key engineDoPhase(Key var1, boolean var2) throws InvalidKeyException, IllegalStateException {
      if (this.x == null) {
         throw new IllegalStateException("Diffie-Hellman not initialised.");
      } else if (!(var1 instanceof DHPublicKey)) {
         throw new InvalidKeyException("DHKeyAgreement doPhase requires DHPublicKey");
      } else {
         DHPublicKey var3 = (DHPublicKey)var1;
         if (var3.getParams().getG().equals(this.g) && var3.getParams().getP().equals(this.p)) {
            BigInteger var4 = ((DHPublicKey)var1).getY();
            if (var4 == null || var4.compareTo(TWO) < 0 || var4.compareTo(this.p.subtract(ONE)) >= 0) {
               throw new InvalidKeyException("Invalid DH PublicKey");
            } else if (this.unifiedAgreement != null) {
               if (!var2) {
                  throw new IllegalStateException("unified Diffie-Hellman can use only two key pairs");
               } else {
                  DHPublicKeyParameters var9 = this.generatePublicKeyParameter((PublicKey)var1);
                  DHPublicKeyParameters var10 = this.generatePublicKeyParameter(this.dheParameters.getOtherPartyEphemeralKey());
                  DHUPublicParameters var11 = new DHUPublicParameters(var9, var10);
                  this.result = this.unifiedAgreement.calculateAgreement(var11);
                  return null;
               }
            } else if (this.mqvAgreement == null) {
               BigInteger var8 = var4.modPow(this.x, this.p);
               if (var8.compareTo(ONE) == 0) {
                  throw new InvalidKeyException("Shared key can't be 1");
               } else {
                  this.result = this.bigIntToBytes(var8);
                  return var2 ? null : new BCDHPublicKey(var8, var3.getParams());
               }
            } else if (!var2) {
               throw new IllegalStateException("MQV Diffie-Hellman can use only two key pairs");
            } else {
               DHPublicKeyParameters var5 = this.generatePublicKeyParameter((PublicKey)var1);
               DHPublicKeyParameters var6 = this.generatePublicKeyParameter(this.mqvParameters.getOtherPartyEphemeralKey());
               DHMQVPublicParameters var7 = new DHMQVPublicParameters(var5, var6);
               this.result = this.bigIntToBytes(this.mqvAgreement.calculateAgreement(var7));
               return null;
            }
         } else {
            throw new InvalidKeyException("DHPublicKey not for this KeyAgreement!");
         }
      }
   }

   @Override
   protected byte[] engineGenerateSecret() throws IllegalStateException {
      if (this.x == null) {
         throw new IllegalStateException("Diffie-Hellman not initialised.");
      } else {
         return super.engineGenerateSecret();
      }
   }

   @Override
   protected int engineGenerateSecret(byte[] var1, int var2) throws IllegalStateException, ShortBufferException {
      if (this.x == null) {
         throw new IllegalStateException("Diffie-Hellman not initialised.");
      } else {
         return super.engineGenerateSecret(var1, var2);
      }
   }

   @Override
   protected SecretKey engineGenerateSecret(String var1) throws NoSuchAlgorithmException {
      if (this.x == null) {
         throw new IllegalStateException("Diffie-Hellman not initialised.");
      } else {
         return (SecretKey)(var1.equals("TlsPremasterSecret") ? new SecretKeySpec(trimZeroes(this.result), var1) : super.engineGenerateSecret(var1));
      }
   }

   @Override
   protected void doInitFromKey(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (!(var1 instanceof DHPrivateKey)) {
         throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey for initialisation");
      } else {
         DHPrivateKey var4 = (DHPrivateKey)var1;
         if (var2 != null) {
            if (var2 instanceof DHParameterSpec) {
               DHParameterSpec var5 = (DHParameterSpec)var2;
               this.p = var5.getP();
               this.g = var5.getG();
               this.dheParameters = null;
               this.ukmParameters = null;
            } else if (var2 instanceof DHUParameterSpec) {
               if (this.unifiedAgreement == null) {
                  throw new InvalidAlgorithmParameterException("agreement algorithm not DHU based");
               }

               this.p = var4.getParams().getP();
               this.g = var4.getParams().getG();
               this.dheParameters = (DHUParameterSpec)var2;
               this.ukmParameters = ((DHUParameterSpec)var2).getUserKeyingMaterial();
               if (this.dheParameters.getEphemeralPublicKey() != null) {
                  this.unifiedAgreement
                     .init(
                        new DHUPrivateParameters(
                           this.generatePrivateKeyParameter(var4),
                           this.generatePrivateKeyParameter(this.dheParameters.getEphemeralPrivateKey()),
                           this.generatePublicKeyParameter(this.dheParameters.getEphemeralPublicKey())
                        )
                     );
               } else {
                  this.unifiedAgreement
                     .init(
                        new DHUPrivateParameters(
                           this.generatePrivateKeyParameter(var4), this.generatePrivateKeyParameter(this.dheParameters.getEphemeralPrivateKey())
                        )
                     );
               }
            } else if (var2 instanceof MQVParameterSpec) {
               if (this.mqvAgreement == null) {
                  throw new InvalidAlgorithmParameterException("agreement algorithm not MQV based");
               }

               this.p = var4.getParams().getP();
               this.g = var4.getParams().getG();
               this.mqvParameters = (MQVParameterSpec)var2;
               this.ukmParameters = ((MQVParameterSpec)var2).getUserKeyingMaterial();
               if (this.mqvParameters.getEphemeralPublicKey() != null) {
                  this.mqvAgreement
                     .init(
                        new DHMQVPrivateParameters(
                           this.generatePrivateKeyParameter(var4),
                           this.generatePrivateKeyParameter(this.mqvParameters.getEphemeralPrivateKey()),
                           this.generatePublicKeyParameter(this.mqvParameters.getEphemeralPublicKey())
                        )
                     );
               } else {
                  this.mqvAgreement
                     .init(
                        new DHMQVPrivateParameters(
                           this.generatePrivateKeyParameter(var4), this.generatePrivateKeyParameter(this.mqvParameters.getEphemeralPrivateKey())
                        )
                     );
               }
            } else {
               if (!(var2 instanceof UserKeyingMaterialSpec)) {
                  throw new InvalidAlgorithmParameterException("DHKeyAgreement only accepts DHParameterSpec");
               }

               if (this.kdf == null) {
                  throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
               }

               this.p = var4.getParams().getP();
               this.g = var4.getParams().getG();
               this.dheParameters = null;
               this.ukmParameters = ((UserKeyingMaterialSpec)var2).getUserKeyingMaterial();
            }
         } else {
            this.p = var4.getParams().getP();
            this.g = var4.getParams().getG();
         }

         this.x = var4.getX();
         this.result = this.bigIntToBytes(this.x);
      }
   }

   @Override
   protected void engineInit(Key var1, SecureRandom var2) throws InvalidKeyException {
      if (!(var1 instanceof DHPrivateKey)) {
         throw new InvalidKeyException("DHKeyAgreement requires DHPrivateKey");
      } else {
         DHPrivateKey var3 = (DHPrivateKey)var1;
         this.p = var3.getParams().getP();
         this.g = var3.getParams().getG();
         this.x = var3.getX();
         this.result = this.bigIntToBytes(this.x);
      }
   }

   @Override
   protected byte[] doCalcSecret() {
      return this.result;
   }

   private DHPrivateKeyParameters generatePrivateKeyParameter(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof DHPrivateKey) {
         if (var1 instanceof BCDHPrivateKey) {
            return ((BCDHPrivateKey)var1).engineGetKeyParameters();
         } else {
            DHPrivateKey var2 = (DHPrivateKey)var1;
            DHParameterSpec var3 = var2.getParams();
            return new DHPrivateKeyParameters(var2.getX(), new DHParameters(var3.getP(), var3.getG(), null, var3.getL()));
         }
      } else {
         throw new InvalidKeyException("private key not a DHPrivateKey");
      }
   }

   private DHPublicKeyParameters generatePublicKeyParameter(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof DHPublicKey) {
         if (var1 instanceof BCDHPublicKey) {
            return ((BCDHPublicKey)var1).engineGetKeyParameters();
         } else {
            DHPublicKey var2 = (DHPublicKey)var1;
            DHParameterSpec var3 = var2.getParams();
            return var3 instanceof DHDomainParameterSpec
               ? new DHPublicKeyParameters(var2.getY(), ((DHDomainParameterSpec)var3).getDomainParameters())
               : new DHPublicKeyParameters(var2.getY(), new DHParameters(var3.getP(), var3.getG(), null, var3.getL()));
         }
      } else {
         throw new InvalidKeyException("public key not a DHPublicKey");
      }
   }

   public static class DHUwithSHA1CKDF extends KeyAgreementSpi {
      public DHUwithSHA1CKDF() {
         super("DHUwithSHA1CKDF", new DHUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHUwithSHA1KDF extends KeyAgreementSpi {
      public DHUwithSHA1KDF() {
         super("DHUwithSHA1KDF", new DHUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHUwithSHA224CKDF extends KeyAgreementSpi {
      public DHUwithSHA224CKDF() {
         super("DHUwithSHA224CKDF", new DHUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHUwithSHA224KDF extends KeyAgreementSpi {
      public DHUwithSHA224KDF() {
         super("DHUwithSHA224KDF", new DHUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHUwithSHA256CKDF extends KeyAgreementSpi {
      public DHUwithSHA256CKDF() {
         super("DHUwithSHA256CKDF", new DHUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHUwithSHA256KDF extends KeyAgreementSpi {
      public DHUwithSHA256KDF() {
         super("DHUwithSHA256KDF", new DHUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHUwithSHA384CKDF extends KeyAgreementSpi {
      public DHUwithSHA384CKDF() {
         super("DHUwithSHA384CKDF", new DHUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHUwithSHA384KDF extends KeyAgreementSpi {
      public DHUwithSHA384KDF() {
         super("DHUwithSHA384KDF", new DHUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHUwithSHA512CKDF extends KeyAgreementSpi {
      public DHUwithSHA512CKDF() {
         super("DHUwithSHA512CKDF", new DHUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DHUwithSHA512KDF extends KeyAgreementSpi {
      public DHUwithSHA512KDF() {
         super("DHUwithSHA512KDF", new DHUnifiedAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DHwithRFC2631KDF extends KeyAgreementSpi {
      public DHwithRFC2631KDF() {
         super("DHwithRFC2631KDF", new DHKEKGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHwithSHA1CKDF extends KeyAgreementSpi {
      public DHwithSHA1CKDF() {
         super("DHwithSHA1CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHwithSHA1KDF extends KeyAgreementSpi {
      public DHwithSHA1KDF() {
         super("DHwithSHA1CKDF", new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class DHwithSHA224CKDF extends KeyAgreementSpi {
      public DHwithSHA224CKDF() {
         super("DHwithSHA224CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHwithSHA224KDF extends KeyAgreementSpi {
      public DHwithSHA224KDF() {
         super("DHwithSHA224CKDF", new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class DHwithSHA256CKDF extends KeyAgreementSpi {
      public DHwithSHA256CKDF() {
         super("DHwithSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHwithSHA256KDF extends KeyAgreementSpi {
      public DHwithSHA256KDF() {
         super("DHwithSHA256CKDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class DHwithSHA384CKDF extends KeyAgreementSpi {
      public DHwithSHA384CKDF() {
         super("DHwithSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHwithSHA384KDF extends KeyAgreementSpi {
      public DHwithSHA384KDF() {
         super("DHwithSHA384KDF", new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class DHwithSHA512CKDF extends KeyAgreementSpi {
      public DHwithSHA512CKDF() {
         super("DHwithSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class DHwithSHA512KDF extends KeyAgreementSpi {
      public DHwithSHA512KDF() {
         super("DHwithSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class MQVwithSHA1CKDF extends KeyAgreementSpi {
      public MQVwithSHA1CKDF() {
         super("MQVwithSHA1CKDF", new MQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class MQVwithSHA1KDF extends KeyAgreementSpi {
      public MQVwithSHA1KDF() {
         super("MQVwithSHA1KDF", new MQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
      }
   }

   public static class MQVwithSHA224CKDF extends KeyAgreementSpi {
      public MQVwithSHA224CKDF() {
         super("MQVwithSHA224CKDF", new MQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class MQVwithSHA224KDF extends KeyAgreementSpi {
      public MQVwithSHA224KDF() {
         super("MQVwithSHA224KDF", new MQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
      }
   }

   public static class MQVwithSHA256CKDF extends KeyAgreementSpi {
      public MQVwithSHA256CKDF() {
         super("MQVwithSHA256CKDF", new MQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class MQVwithSHA256KDF extends KeyAgreementSpi {
      public MQVwithSHA256KDF() {
         super("MQVwithSHA256KDF", new MQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
      }
   }

   public static class MQVwithSHA384CKDF extends KeyAgreementSpi {
      public MQVwithSHA384CKDF() {
         super("MQVwithSHA384CKDF", new MQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class MQVwithSHA384KDF extends KeyAgreementSpi {
      public MQVwithSHA384KDF() {
         super("MQVwithSHA384KDF", new MQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
      }
   }

   public static class MQVwithSHA512CKDF extends KeyAgreementSpi {
      public MQVwithSHA512CKDF() {
         super("MQVwithSHA512CKDF", new MQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
      }
   }

   public static class MQVwithSHA512KDF extends KeyAgreementSpi {
      public MQVwithSHA512KDF() {
         super("MQVwithSHA512KDF", new MQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
      }
   }
}
