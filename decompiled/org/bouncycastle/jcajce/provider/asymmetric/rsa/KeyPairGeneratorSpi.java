package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   private static final AlgorithmIdentifier PKCS_ALGID = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
   private static final AlgorithmIdentifier PSS_ALGID = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS);
   static final BigInteger defaultPublicExponent = BigInteger.valueOf(65537L);
   RSAKeyGenerationParameters param;
   RSAKeyPairGenerator engine;
   AlgorithmIdentifier algId;

   public KeyPairGeneratorSpi(String var1, AlgorithmIdentifier var2) {
      super(var1);
      this.algId = var2;
      this.engine = new RSAKeyPairGenerator();
      this.param = new RSAKeyGenerationParameters(
         defaultPublicExponent, CryptoServicesRegistrar.getSecureRandom(), 2048, PrimeCertaintyCalculator.getDefaultCertainty(2048)
      );
      this.engine.init(this.param);
   }

   public KeyPairGeneratorSpi() {
      this("RSA", PKCS_ALGID);
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      this.param = new RSAKeyGenerationParameters(defaultPublicExponent, var2, var1, PrimeCertaintyCalculator.getDefaultCertainty(var1));
      this.engine.init(this.param);
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof RSAKeyGenParameterSpec)) {
         throw new InvalidAlgorithmParameterException("parameter object not a RSAKeyGenParameterSpec");
      } else {
         RSAKeyGenParameterSpec var3 = (RSAKeyGenParameterSpec)var1;
         this.param = new RSAKeyGenerationParameters(var3.getPublicExponent(), var2, var3.getKeysize(), PrimeCertaintyCalculator.getDefaultCertainty(2048));
         this.engine.init(this.param);
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      RSAKeyParameters var2 = (RSAKeyParameters)var1.getPublic();
      RSAPrivateCrtKeyParameters var3 = (RSAPrivateCrtKeyParameters)var1.getPrivate();
      return new KeyPair(new BCRSAPublicKey(this.algId, var2), new BCRSAPrivateCrtKey(this.algId, var3));
   }

   public static class PSS extends KeyPairGeneratorSpi {
      public PSS() {
         super("RSASSA-PSS", KeyPairGeneratorSpi.PSS_ALGID);
      }
   }
}
