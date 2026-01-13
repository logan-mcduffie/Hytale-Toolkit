package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class EdEC {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.edec.";
   private static final Map<String, String> edxAttributes = new HashMap<>();

   static {
      edxAttributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
      edxAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$XDH");
         var1.addAlgorithm("KeyFactory.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$X448");
         var1.addAlgorithm("KeyFactory.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$X25519");
         var1.addAlgorithm("KeyFactory.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$EdDSA");
         var1.addAlgorithm("KeyFactory.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$Ed448");
         var1.addAlgorithm("KeyFactory.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyFactorySpi$Ed25519");
         var1.addAlgorithm("Signature.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$EdDSA");
         var1.addAlgorithm("Signature.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$Ed448");
         var1.addAlgorithm("Signature.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.SignatureSpi$Ed25519");
         var1.addAlgorithm("Alg.Alias.Signature", EdECObjectIdentifiers.id_Ed448, "ED448");
         var1.addAlgorithm("Alg.Alias.Signature", EdECObjectIdentifiers.id_Ed25519, "ED25519");
         var1.addAlgorithm("KeyPairGenerator.EDDSA", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$EdDSA");
         var1.addAlgorithm("KeyPairGenerator.ED448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed448");
         var1.addAlgorithm("KeyPairGenerator.ED25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed25519");
         var1.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_Ed448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed448");
         var1.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_Ed25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$Ed25519");
         var1.addAlgorithm("KeyAgreement.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$XDH");
         var1.addAlgorithm("KeyAgreement.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448");
         var1.addAlgorithm("KeyAgreement.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519");
         var1.addAlgorithm("KeyAgreement", EdECObjectIdentifiers.id_X448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448");
         var1.addAlgorithm("KeyAgreement", EdECObjectIdentifiers.id_X25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519");
         var1.addAlgorithm("KeyAgreement.X25519WITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256CKDF");
         var1.addAlgorithm("KeyAgreement.X25519WITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA384CKDF");
         var1.addAlgorithm("KeyAgreement.X25519WITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA512CKDF");
         var1.addAlgorithm("KeyAgreement.X448WITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA256CKDF");
         var1.addAlgorithm("KeyAgreement.X448WITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA384CKDF");
         var1.addAlgorithm("KeyAgreement.X448WITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512CKDF");
         var1.addAlgorithm("KeyAgreement.X25519WITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256KDF");
         var1.addAlgorithm("KeyAgreement.X448WITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512KDF");
         var1.addAlgorithm("KeyAgreement.X25519UWITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519UwithSHA256KDF");
         var1.addAlgorithm("KeyAgreement.X448UWITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448UwithSHA512KDF");
         var1.addAlgorithm("KeyAgreement.X448withSHA512HKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X448withSHA512HKDF");
         var1.addAlgorithm("KeyAgreement.X25519withSHA256HKDF", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyAgreementSpi$X25519withSHA256HKDF");
         var1.addAlgorithm("KeyPairGenerator.XDH", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$XDH");
         var1.addAlgorithm("KeyPairGenerator.X448", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X448");
         var1.addAlgorithm("KeyPairGenerator.X25519", "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X25519");
         var1.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_X448, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X448");
         var1.addAlgorithm("KeyPairGenerator", EdECObjectIdentifiers.id_X25519, "org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi$X25519");
         var1.addAlgorithm("Cipher.XIES", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIES");
         var1.addAlgorithm("Cipher.XIESwithSHA1", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIES");
         var1.addAlgorithm("Cipher.XIESWITHSHA1", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIES");
         var1.addAlgorithm("Cipher.XIESwithSHA256", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256");
         var1.addAlgorithm("Cipher.XIESWITHSHA256", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256");
         var1.addAlgorithm("Cipher.XIESwithSHA384", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384");
         var1.addAlgorithm("Cipher.XIESWITHSHA384", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384");
         var1.addAlgorithm("Cipher.XIESwithSHA512", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512");
         var1.addAlgorithm("Cipher.XIESWITHSHA512", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512");
         var1.addAlgorithm("Cipher.XIESwithAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
         var1.addAlgorithm("Cipher.XIESWITHAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
         var1.addAlgorithm("Cipher.XIESwithSHA1andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
         var1.addAlgorithm("Cipher.XIESWITHSHA1ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithAESCBC");
         var1.addAlgorithm("Cipher.XIESwithSHA256andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256andAESCBC");
         var1.addAlgorithm("Cipher.XIESWITHSHA256ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA256andAESCBC");
         var1.addAlgorithm("Cipher.XIESwithSHA384andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384andAESCBC");
         var1.addAlgorithm("Cipher.XIESWITHSHA384ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA384andAESCBC");
         var1.addAlgorithm("Cipher.XIESwithSHA512andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512andAESCBC");
         var1.addAlgorithm("Cipher.XIESWITHSHA512ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.edec.IESCipher$XIESwithSHA512andAESCBC");
         this.registerOid(var1, EdECObjectIdentifiers.id_X448, "XDH", new KeyFactorySpi.X448());
         this.registerOid(var1, EdECObjectIdentifiers.id_X25519, "XDH", new KeyFactorySpi.X25519());
         this.registerOid(var1, EdECObjectIdentifiers.id_Ed448, "EDDSA", new KeyFactorySpi.Ed448());
         this.registerOid(var1, EdECObjectIdentifiers.id_Ed25519, "EDDSA", new KeyFactorySpi.Ed25519());
      }
   }
}
