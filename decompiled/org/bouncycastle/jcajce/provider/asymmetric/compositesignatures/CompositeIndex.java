package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;

public class CompositeIndex {
   private static Map<ASN1ObjectIdentifier, String[]> pairings = new HashMap<>();
   private static Map<ASN1ObjectIdentifier, AlgorithmParameterSpec[]> kpgInitSpecs = new HashMap<>();
   private static Map<ASN1ObjectIdentifier, String> algorithmNames = new HashMap<>();

   public static boolean isAlgorithmSupported(ASN1ObjectIdentifier var0) {
      return pairings.containsKey(var0);
   }

   public static Set<ASN1ObjectIdentifier> getSupportedIdentifiers() {
      return pairings.keySet();
   }

   public static String getAlgorithmName(ASN1ObjectIdentifier var0) {
      return algorithmNames.get(var0);
   }

   static String[] getPairing(ASN1ObjectIdentifier var0) {
      return pairings.get(var0);
   }

   static AlgorithmParameterSpec[] getKeyPairSpecs(ASN1ObjectIdentifier var0) {
      return kpgInitSpecs.get(var0);
   }

   static Digest getDigest(ASN1ObjectIdentifier var0) {
      String var1 = algorithmNames.get(var0);
      if (var1.endsWith("SHA256")) {
         return new SHA256Digest();
      } else if (var1.endsWith("SHA384")) {
         return new SHA384Digest();
      } else {
         return (Digest)(var1.endsWith("SHA512") ? new SHA512Digest() : new SHAKEDigest(256));
      }
   }

   static String getBaseName(String var0) {
      if (var0.indexOf("RSA") >= 0) {
         return "RSA";
      } else {
         return var0.indexOf("ECDSA") >= 0 ? "EC" : var0;
      }
   }

   static {
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new String[]{"ML-DSA-44", "RSASSA-PSS"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new String[]{"ML-DSA-44", "sha256WithRSAEncryption"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new String[]{"ML-DSA-44", "Ed25519"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new String[]{"ML-DSA-44", "SHA256withECDSA"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new String[]{"ML-DSA-65", "RSASSA-PSS"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new String[]{"ML-DSA-65", "sha256WithRSAEncryption"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new String[]{"ML-DSA-65", "RSASSA-PSS"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new String[]{"ML-DSA-65", "sha384WithRSAEncryption"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new String[]{"ML-DSA-65", "SHA256withECDSA"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new String[]{"ML-DSA-65", "SHA384withECDSA"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new String[]{"ML-DSA-65", "SHA256withECDSA"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new String[]{"ML-DSA-65", "Ed25519"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new String[]{"ML-DSA-87", "SHA384withECDSA"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new String[]{"ML-DSA-87", "SHA384withECDSA"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new String[]{"ML-DSA-87", "Ed448"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new String[]{"ML-DSA-87", "RSASSA-PSS"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new String[]{"ML-DSA-87", "RSASSA-PSS"});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new String[]{"ML-DSA-87", "SHA512withECDSA"});
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PSS_SHA256,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PKCS15_SHA256,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(MiscObjectIdentifiers.id_HashMLDSA44_Ed25519_SHA512, new AlgorithmParameterSpec[]{null, null});
      kpgInitSpecs.put(MiscObjectIdentifiers.id_HashMLDSA44_ECDSA_P256_SHA256, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-256")});
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PSS_SHA512,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PKCS15_SHA512,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PSS_SHA512,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(4096, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PKCS15_SHA512,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(4096, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_P384_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-384")});
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_brainpoolP256r1_SHA512,
         new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("brainpoolP256r1")}
      );
      kpgInitSpecs.put(MiscObjectIdentifiers.id_HashMLDSA65_Ed25519_SHA512, new AlgorithmParameterSpec[]{null, null});
      kpgInitSpecs.put(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_P384_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-384")});
      kpgInitSpecs.put(
         MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_brainpoolP384r1_SHA512,
         new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("brainpoolP384r1")}
      );
      kpgInitSpecs.put(MiscObjectIdentifiers.id_HashMLDSA87_Ed448_SHA512, new AlgorithmParameterSpec[]{null, null});
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new AlgorithmParameterSpec[]{null, null});
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-256")});
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(4096, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512,
         new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(4096, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-256")});
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-384")});
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("brainpoolP256r1")}
      );
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new AlgorithmParameterSpec[]{null, null});
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-384")});
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("brainpoolP384r1")}
      );
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new AlgorithmParameterSpec[]{null, null});
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(4096, RSAKeyGenParameterSpec.F4)}
      );
      kpgInitSpecs.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new AlgorithmParameterSpec[]{null, new ECNamedCurveGenParameterSpec("P-521")});
      kpgInitSpecs.put(
         IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new AlgorithmParameterSpec[]{null, new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4)}
      );
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PSS_SHA256, "HashMLDSA44-RSA2048-PSS-SHA256");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA44_RSA2048_PKCS15_SHA256, "HashMLDSA44-RSA2048-PKCS15-SHA256");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA44_Ed25519_SHA512, "HashMLDSA44-Ed25519-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA44_ECDSA_P256_SHA256, "HashMLDSA44-ECDSA-P256-SHA256");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PSS_SHA512, "HashMLDSA65-RSA3072-PSS-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_RSA3072_PKCS15_SHA512, "HashMLDSA65-RSA3072-PKCS15-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PSS_SHA512, "HashMLDSA65-RSA4096-PSS-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_RSA4096_PKCS15_SHA512, "HashMLDSA65-RSA4096-PKCS15-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_P384_SHA512, "HashMLDSA65-ECDSA-P384-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_ECDSA_brainpoolP256r1_SHA512, "HashMLDSA65-ECDSA-brainpoolP256r1-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA65_Ed25519_SHA512, "HashMLDSA65-Ed25519-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_P384_SHA512, "HashMLDSA87-ECDSA-P384-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA87_ECDSA_brainpoolP384r1_SHA512, "HashMLDSA87-ECDSA-brainpoolP384r1-SHA512");
      algorithmNames.put(MiscObjectIdentifiers.id_HashMLDSA87_Ed448_SHA512, "HashMLDSA87-Ed448-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, "MLDSA44-RSA2048-PSS-SHA256");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, "MLDSA44-RSA2048-PKCS15-SHA256");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, "MLDSA44-Ed25519-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, "MLDSA44-ECDSA-P256-SHA256");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, "MLDSA65-RSA3072-PSS-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, "MLDSA65-RSA3072-PKCS15-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, "MLDSA65-RSA4096-PSS-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, "MLDSA65-RSA4096-PKCS15-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, "MLDSA65-ECDSA-P256-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, "MLDSA65-ECDSA-P384-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, "MLDSA65-ECDSA-brainpoolP256r1-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, "MLDSA65-Ed25519-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, "MLDSA87-ECDSA-P384-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, "MLDSA87-ECDSA-brainpoolP384r1-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, "MLDSA87-Ed448-SHAKE256");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, "MLDSA87-RSA4096-PSS-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, "MLDSA87-ECDSA-P521-SHA512");
      algorithmNames.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, "MLDSA87-RSA3072-PSS-SHA512");
   }
}
