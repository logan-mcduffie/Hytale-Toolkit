package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class SLHDSA {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.SLH-DSA", "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Pure");
         var1.addAlgorithm("KeyPairGenerator.SLH-DSA", "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Pure");
         var1.addAlgorithm("KeyFactory.HASH-SLH-DSA", "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Hash");
         var1.addAlgorithm("KeyPairGenerator.HASH-SLH-DSA", "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Hash");
         SLHDSAKeyFactorySpi.Hash var2 = new SLHDSAKeyFactorySpi.Hash();
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-128S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Sha2_128s",
            NISTObjectIdentifiers.id_slh_dsa_sha2_128s,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-128F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Sha2_128f",
            NISTObjectIdentifiers.id_slh_dsa_sha2_128f,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-192S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Sha2_192s",
            NISTObjectIdentifiers.id_slh_dsa_sha2_192s,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-192F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Sha2_192f",
            NISTObjectIdentifiers.id_slh_dsa_sha2_192f,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-256S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Sha2_256s",
            NISTObjectIdentifiers.id_slh_dsa_sha2_256s,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-256F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Sha2_256f",
            NISTObjectIdentifiers.id_slh_dsa_sha2_256f,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Shake_128s",
            NISTObjectIdentifiers.id_slh_dsa_shake_128s,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Shake_128f",
            NISTObjectIdentifiers.id_slh_dsa_shake_128f,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Shake_192s",
            NISTObjectIdentifiers.id_slh_dsa_shake_192s,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Shake_192f",
            NISTObjectIdentifiers.id_slh_dsa_shake_192f,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Shake_256s",
            NISTObjectIdentifiers.id_slh_dsa_shake_256s,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$Shake_256f",
            NISTObjectIdentifiers.id_slh_dsa_shake_256f,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-128S-WITH-SHA256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashSha2_128s",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-128F-WITH-SHA256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashSha2_128f",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-192S-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashSha2_192s",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-192F-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashSha2_192f",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-256S-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashSha2_256s",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHA2-256F-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashSha2_256f",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128S-WITH-SHAKE128",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashShake_128s",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128F-WITH-SHAKE128",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashShake_128f",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192S-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashShake_192s",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192F-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashShake_192f",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256S-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashShake_256s",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256,
            var2
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256F-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyFactorySpi$HashShake_256f",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256,
            var2
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-128S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Sha2_128s",
            NISTObjectIdentifiers.id_slh_dsa_sha2_128s
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-128F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Sha2_128f",
            NISTObjectIdentifiers.id_slh_dsa_sha2_128f
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-192S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Sha2_192s",
            NISTObjectIdentifiers.id_slh_dsa_sha2_192s
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-192F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Sha2_192f",
            NISTObjectIdentifiers.id_slh_dsa_sha2_192f
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-256S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Sha2_256s",
            NISTObjectIdentifiers.id_slh_dsa_sha2_256s
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-256F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Sha2_256f",
            NISTObjectIdentifiers.id_slh_dsa_sha2_256f
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Shake_128s",
            NISTObjectIdentifiers.id_slh_dsa_shake_128s
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Shake_128f",
            NISTObjectIdentifiers.id_slh_dsa_shake_128f
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Shake_192s",
            NISTObjectIdentifiers.id_slh_dsa_shake_192s
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Shake_192f",
            NISTObjectIdentifiers.id_slh_dsa_shake_192f
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256S",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Shake_256s",
            NISTObjectIdentifiers.id_slh_dsa_shake_256s
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256F",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$Shake_256f",
            NISTObjectIdentifiers.id_slh_dsa_shake_256f
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-128S-WITH-SHA256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashSha2_128s",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-128F-WITH-SHA256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashSha2_128f",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-192S-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashSha2_192s",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-192F-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashSha2_192f",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-256S-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashSha2_256s",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHA2-256F-WITH-SHA512",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashSha2_256f",
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128S-WITH-SHAKE128",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashShake_128s",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-128F-WITH-SHAKE128",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashShake_128f",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192S-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashShake_192s",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-192F-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashShake_192f",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256S-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashShake_256s",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "SLH-DSA-SHAKE-256F-WITH-SHAKE256",
            "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SLHDSAKeyPairGeneratorSpi$HashShake_256f",
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256
         );
         String[] var3 = new String[]{
            "SLH-DSA-SHA2-128S",
            "SLH-DSA-SHA2-128F",
            "SLH-DSA-SHA2-192S",
            "SLH-DSA-SHA2-192F",
            "SLH-DSA-SHA2-256S",
            "SLH-DSA-SHA2-256F",
            "SLH-DSA-SHAKE-128S",
            "SLH-DSA-SHAKE-128F",
            "SLH-DSA-SHAKE-192S",
            "SLH-DSA-SHAKE-192F",
            "SLH-DSA-SHAKE-256S",
            "SLH-DSA-SHAKE-256F"
         };
         String[] var4 = new String[]{
            "SLH-DSA-SHA2-128S-WITH-SHA256",
            "SLH-DSA-SHA2-128F-WITH-SHA256",
            "SLH-DSA-SHA2-192S-WITH-SHA512",
            "SLH-DSA-SHA2-192F-WITH-SHA512",
            "SLH-DSA-SHA2-256S-WITH-SHA512",
            "SLH-DSA-SHA2-256F-WITH-SHA512",
            "SLH-DSA-SHAKE-128S-WITH-SHAKE128",
            "SLH-DSA-SHAKE-128F-WITH-SHAKE128",
            "SLH-DSA-SHAKE-192S-WITH-SHAKE256",
            "SLH-DSA-SHAKE-192F-WITH-SHAKE256",
            "SLH-DSA-SHAKE-256S-WITH-SHAKE256",
            "SLH-DSA-SHAKE-256F-WITH-SHAKE256"
         };
         this.addSignatureAlgorithm(var1, "SLH-DSA", "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.SignatureSpi$Direct", (ASN1ObjectIdentifier)null);
         var1.addAlgorithm("Alg.Alias.Signature.SLHDSA", "SLH-DSA");
         this.addSignatureAlgorithm(
            var1, "HASH-SLH-DSA", "org.bouncycastle.jcajce.provider.asymmetric.slhdsa.HashSignatureSpi$Direct", (ASN1ObjectIdentifier)null
         );
         var1.addAlgorithm("Alg.Alias.Signature.HASHWITHSLHDSA", "HASH-SLH-DSA");

         for (int var5 = 0; var5 != var3.length; var5++) {
            var1.addAlgorithm("Alg.Alias.Signature." + var3[var5], "SLH-DSA");
         }

         for (int var7 = 0; var7 != var4.length; var7++) {
            var1.addAlgorithm("Alg.Alias.Signature." + var4[var7], "HASH-SLH-DSA");
         }

         ASN1ObjectIdentifier[] var8 = new ASN1ObjectIdentifier[]{
            NISTObjectIdentifiers.id_slh_dsa_sha2_128s,
            NISTObjectIdentifiers.id_slh_dsa_sha2_128f,
            NISTObjectIdentifiers.id_slh_dsa_sha2_192s,
            NISTObjectIdentifiers.id_slh_dsa_sha2_192f,
            NISTObjectIdentifiers.id_slh_dsa_sha2_256s,
            NISTObjectIdentifiers.id_slh_dsa_sha2_256f,
            NISTObjectIdentifiers.id_slh_dsa_shake_128s,
            NISTObjectIdentifiers.id_slh_dsa_shake_128f,
            NISTObjectIdentifiers.id_slh_dsa_shake_192s,
            NISTObjectIdentifiers.id_slh_dsa_shake_192f,
            NISTObjectIdentifiers.id_slh_dsa_shake_256s,
            NISTObjectIdentifiers.id_slh_dsa_shake_256f
         };

         for (int var6 = 0; var6 != var8.length; var6++) {
            var1.addAlgorithm("Alg.Alias.Signature." + var8[var6], "SLH-DSA");
            var1.addAlgorithm("Alg.Alias.Signature.OID." + var8[var6], "SLH-DSA");
         }

         var8 = new ASN1ObjectIdentifier[]{
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256,
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256,
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128,
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128,
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512,
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512,
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256,
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256,
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512,
            NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512,
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256,
            NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256
         };

         for (int var10 = 0; var10 != var8.length; var10++) {
            var1.addAlgorithm("Alg.Alias.Signature." + var8[var10], "HASH-SLH-DSA");
            var1.addAlgorithm("Alg.Alias.Signature.OID." + var8[var10], "HASH-SLH-DSA");
         }
      }
   }
}
