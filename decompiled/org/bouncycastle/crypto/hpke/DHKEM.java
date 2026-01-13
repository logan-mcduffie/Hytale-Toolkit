package org.bouncycastle.crypto.hpke;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.BasicRawAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

class DHKEM extends KEM {
   private AsymmetricCipherKeyPairGenerator kpGen;
   private RawAgreement rawAgreement;
   private final short kemId;
   private HKDF hkdf;
   private byte bitmask;
   private int Nsk;
   private int Nsecret;
   private int Nenc;
   ECDomainParameters domainParams;

   protected DHKEM(short var1) {
      this.kemId = var1;
      switch (var1) {
         case 16:
            this.hkdf = new HKDF((short)1);
            this.domainParams = getDomainParameters("P-256");
            this.rawAgreement = new BasicRawAgreement(new ECDHCBasicAgreement());
            this.bitmask = -1;
            this.Nsk = 32;
            this.Nsecret = 32;
            this.Nenc = 65;
            this.kpGen = new ECKeyPairGenerator();
            this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, getSecureRandom()));
            break;
         case 17:
            this.hkdf = new HKDF((short)2);
            this.domainParams = getDomainParameters("P-384");
            this.rawAgreement = new BasicRawAgreement(new ECDHCBasicAgreement());
            this.bitmask = -1;
            this.Nsk = 48;
            this.Nsecret = 48;
            this.Nenc = 97;
            this.kpGen = new ECKeyPairGenerator();
            this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, getSecureRandom()));
            break;
         case 18:
            this.hkdf = new HKDF((short)3);
            this.domainParams = getDomainParameters("P-521");
            this.rawAgreement = new BasicRawAgreement(new ECDHCBasicAgreement());
            this.bitmask = 1;
            this.Nsk = 66;
            this.Nsecret = 64;
            this.Nenc = 133;
            this.kpGen = new ECKeyPairGenerator();
            this.kpGen.init(new ECKeyGenerationParameters(this.domainParams, getSecureRandom()));
            break;
         case 32:
            this.hkdf = new HKDF((short)1);
            this.rawAgreement = new X25519Agreement();
            this.Nsecret = 32;
            this.Nsk = 32;
            this.Nenc = 32;
            this.kpGen = new X25519KeyPairGenerator();
            this.kpGen.init(new X25519KeyGenerationParameters(getSecureRandom()));
            break;
         case 33:
            this.hkdf = new HKDF((short)3);
            this.rawAgreement = new X448Agreement();
            this.Nsecret = 64;
            this.Nsk = 56;
            this.Nenc = 56;
            this.kpGen = new X448KeyPairGenerator();
            this.kpGen.init(new X448KeyGenerationParameters(getSecureRandom()));
            break;
         default:
            throw new IllegalArgumentException("invalid kem id");
      }
   }

   @Override
   public byte[] SerializePublicKey(AsymmetricKeyParameter var1) {
      switch (this.kemId) {
         case 16:
         case 17:
         case 18:
            return ((ECPublicKeyParameters)var1).getQ().getEncoded(false);
         case 32:
            return ((X25519PublicKeyParameters)var1).getEncoded();
         case 33:
            return ((X448PublicKeyParameters)var1).getEncoded();
         default:
            throw new IllegalStateException("invalid kem id");
      }
   }

   @Override
   public byte[] SerializePrivateKey(AsymmetricKeyParameter var1) {
      switch (this.kemId) {
         case 16:
         case 17:
         case 18:
            return BigIntegers.asUnsignedByteArray(this.Nsk, ((ECPrivateKeyParameters)var1).getD());
         case 32:
            byte[] var3 = ((X25519PrivateKeyParameters)var1).getEncoded();
            X25519.clampPrivateKey(var3);
            return var3;
         case 33:
            byte[] var2 = ((X448PrivateKeyParameters)var1).getEncoded();
            X448.clampPrivateKey(var2);
            return var2;
         default:
            throw new IllegalStateException("invalid kem id");
      }
   }

   @Override
   public AsymmetricKeyParameter DeserializePublicKey(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("'pkEncoded' cannot be null");
      } else if (var1.length != this.Nenc) {
         throw new IllegalArgumentException("'pkEncoded' has invalid length");
      } else {
         switch (this.kemId) {
            case 16:
            case 17:
            case 18:
               if (var1[0] != 4) {
                  throw new IllegalArgumentException("'pkEncoded' has invalid format");
               }

               ECPoint var2 = this.domainParams.getCurve().decodePoint(var1);
               return new ECPublicKeyParameters(var2, this.domainParams);
            case 32:
               return new X25519PublicKeyParameters(var1);
            case 33:
               return new X448PublicKeyParameters(var1);
            default:
               throw new IllegalStateException("invalid kem id");
         }
      }
   }

   @Override
   public AsymmetricCipherKeyPair DeserializePrivateKey(byte[] var1, byte[] var2) {
      if (var1 == null) {
         throw new NullPointerException("'skEncoded' cannot be null");
      } else if (var1.length != this.Nsk) {
         throw new IllegalArgumentException("'skEncoded' has invalid length");
      } else {
         Object var3 = null;
         if (var2 != null) {
            var3 = this.DeserializePublicKey(var2);
         }

         switch (this.kemId) {
            case 16:
            case 17:
            case 18:
               BigInteger var4 = new BigInteger(1, var1);
               ECPrivateKeyParameters var5 = new ECPrivateKeyParameters(var4, this.domainParams);
               if (var3 == null) {
                  ECPoint var8 = new FixedPointCombMultiplier().multiply(this.domainParams.getG(), var5.getD());
                  var3 = new ECPublicKeyParameters(var8, this.domainParams);
               }

               return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, var5);
            case 32:
               X25519PrivateKeyParameters var7 = new X25519PrivateKeyParameters(var1);
               if (var3 == null) {
                  var3 = var7.generatePublicKey();
               }

               return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, var7);
            case 33:
               X448PrivateKeyParameters var6 = new X448PrivateKeyParameters(var1);
               if (var3 == null) {
                  var3 = var6.generatePublicKey();
               }

               return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, var6);
            default:
               throw new IllegalStateException("invalid kem id");
         }
      }
   }

   @Override
   int getEncryptionSize() {
      return this.Nenc;
   }

   private boolean validateSk(BigInteger var1) {
      BigInteger var2 = this.domainParams.getN();
      int var3 = var2.bitLength();
      int var4 = var3 >>> 2;
      return var1.compareTo(BigInteger.valueOf(1L)) >= 0 && var1.compareTo(var2) < 0 ? WNafUtil.getNafWeight(var1) >= var4 : false;
   }

   @Override
   public AsymmetricCipherKeyPair GeneratePrivateKey() {
      return this.kpGen.generateKeyPair();
   }

   @Override
   public AsymmetricCipherKeyPair DeriveKeyPair(byte[] var1) {
      byte[] var2 = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
      switch (this.kemId) {
         case 16:
         case 17:
         case 18:
            byte[] var12 = this.hkdf.LabeledExtract(null, var2, "dkp_prk", var1);
            byte[] var14 = new byte[1];

            for (int var16 = 0; var16 < 256; var16++) {
               var14[0] = (byte)var16;
               byte[] var6 = this.hkdf.LabeledExpand(var12, var2, "candidate", var14, this.Nsk);
               var6[0] &= this.bitmask;
               BigInteger var7 = new BigInteger(1, var6);
               if (this.validateSk(var7)) {
                  ECPoint var8 = new FixedPointCombMultiplier().multiply(this.domainParams.getG(), var7);
                  ECPrivateKeyParameters var9 = new ECPrivateKeyParameters(var7, this.domainParams);
                  ECPublicKeyParameters var10 = new ECPublicKeyParameters(var8, this.domainParams);
                  return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var10, (AsymmetricKeyParameter)var9);
               }
            }

            throw new IllegalStateException("DeriveKeyPairError");
         case 32:
            byte[] var11 = this.hkdf.LabeledExtract(null, var2, "dkp_prk", var1);
            byte[] var13 = this.hkdf.LabeledExpand(var11, var2, "sk", null, this.Nsk);
            X25519PrivateKeyParameters var15 = new X25519PrivateKeyParameters(var13);
            return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var15.generatePublicKey(), (AsymmetricKeyParameter)var15);
         case 33:
            byte[] var3 = this.hkdf.LabeledExtract(null, var2, "dkp_prk", var1);
            byte[] var4 = this.hkdf.LabeledExpand(var3, var2, "sk", null, this.Nsk);
            X448PrivateKeyParameters var5 = new X448PrivateKeyParameters(var4);
            return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var5.generatePublicKey(), (AsymmetricKeyParameter)var5);
         default:
            throw new IllegalStateException("invalid kem id");
      }
   }

   @Override
   protected byte[][] Encap(AsymmetricKeyParameter var1) {
      return this.Encap(var1, this.kpGen.generateKeyPair());
   }

   @Override
   protected byte[][] Encap(AsymmetricKeyParameter var1, AsymmetricCipherKeyPair var2) {
      byte[][] var3 = new byte[2][];
      byte[] var4 = calculateRawAgreement(this.rawAgreement, var2.getPrivate(), var1);
      byte[] var5 = this.SerializePublicKey(var2.getPublic());
      byte[] var6 = this.SerializePublicKey(var1);
      byte[] var7 = Arrays.concatenate(var5, var6);
      byte[] var8 = this.ExtractAndExpand(var4, var7);
      var3[0] = var8;
      var3[1] = var5;
      return var3;
   }

   @Override
   protected byte[] Decap(byte[] var1, AsymmetricCipherKeyPair var2) {
      AsymmetricKeyParameter var3 = this.DeserializePublicKey(var1);
      byte[] var4 = calculateRawAgreement(this.rawAgreement, var2.getPrivate(), var3);
      byte[] var5 = this.SerializePublicKey(var2.getPublic());
      byte[] var6 = Arrays.concatenate(var1, var5);
      return this.ExtractAndExpand(var4, var6);
   }

   @Override
   protected byte[][] AuthEncap(AsymmetricKeyParameter var1, AsymmetricCipherKeyPair var2) {
      byte[][] var3 = new byte[2][];
      AsymmetricCipherKeyPair var4 = this.kpGen.generateKeyPair();
      this.rawAgreement.init(var4.getPrivate());
      int var5 = this.rawAgreement.getAgreementSize();
      byte[] var6 = new byte[var5 * 2];
      this.rawAgreement.calculateAgreement(var1, var6, 0);
      this.rawAgreement.init(var2.getPrivate());
      if (var5 != this.rawAgreement.getAgreementSize()) {
         throw new IllegalStateException();
      } else {
         this.rawAgreement.calculateAgreement(var1, var6, var5);
         byte[] var7 = this.SerializePublicKey(var4.getPublic());
         byte[] var8 = this.SerializePublicKey(var1);
         byte[] var9 = this.SerializePublicKey(var2.getPublic());
         byte[] var10 = Arrays.concatenate(var7, var8, var9);
         byte[] var11 = this.ExtractAndExpand(var6, var10);
         var3[0] = var11;
         var3[1] = var7;
         return var3;
      }
   }

   @Override
   protected byte[] AuthDecap(byte[] var1, AsymmetricCipherKeyPair var2, AsymmetricKeyParameter var3) {
      AsymmetricKeyParameter var4 = this.DeserializePublicKey(var1);
      this.rawAgreement.init(var2.getPrivate());
      int var5 = this.rawAgreement.getAgreementSize();
      byte[] var6 = new byte[var5 * 2];
      this.rawAgreement.calculateAgreement(var4, var6, 0);
      this.rawAgreement.calculateAgreement(var3, var6, var5);
      byte[] var7 = this.SerializePublicKey(var2.getPublic());
      byte[] var8 = this.SerializePublicKey(var3);
      byte[] var9 = Arrays.concatenate(var1, var7, var8);
      return this.ExtractAndExpand(var6, var9);
   }

   private byte[] ExtractAndExpand(byte[] var1, byte[] var2) {
      byte[] var3 = Arrays.concatenate(Strings.toByteArray("KEM"), Pack.shortToBigEndian(this.kemId));
      byte[] var4 = this.hkdf.LabeledExtract(null, var3, "eae_prk", var1);
      return this.hkdf.LabeledExpand(var4, var3, "shared_secret", var2, this.Nsecret);
   }

   private static byte[] calculateRawAgreement(RawAgreement var0, AsymmetricKeyParameter var1, AsymmetricKeyParameter var2) {
      var0.init(var1);
      byte[] var3 = new byte[var0.getAgreementSize()];
      var0.calculateAgreement(var2, var3, 0);
      return var3;
   }

   private static ECDomainParameters getDomainParameters(String var0) {
      return new ECDomainParameters(CustomNamedCurves.getByName(var0));
   }

   private static SecureRandom getSecureRandom() {
      return CryptoServicesRegistrar.getSecureRandom();
   }
}
