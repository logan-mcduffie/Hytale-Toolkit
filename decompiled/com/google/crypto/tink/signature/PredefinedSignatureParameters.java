package com.google.crypto.tink.signature;

import com.google.crypto.tink.internal.TinkBugException;

public final class PredefinedSignatureParameters {
   public static final EcdsaParameters ECDSA_P256 = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setHashType(EcdsaParameters.HashType.SHA256)
         .setCurveType(EcdsaParameters.CurveType.NIST_P256)
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.DER)
         .setVariant(EcdsaParameters.Variant.TINK)
         .build()
   );
   public static final EcdsaParameters ECDSA_P384 = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setHashType(EcdsaParameters.HashType.SHA512)
         .setCurveType(EcdsaParameters.CurveType.NIST_P384)
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.DER)
         .setVariant(EcdsaParameters.Variant.TINK)
         .build()
   );
   public static final EcdsaParameters ECDSA_P521 = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setHashType(EcdsaParameters.HashType.SHA512)
         .setCurveType(EcdsaParameters.CurveType.NIST_P521)
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.DER)
         .setVariant(EcdsaParameters.Variant.TINK)
         .build()
   );
   public static final EcdsaParameters ECDSA_P256_IEEE_P1363 = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.IEEE_P1363)
         .setCurveType(EcdsaParameters.CurveType.NIST_P256)
         .setHashType(EcdsaParameters.HashType.SHA256)
         .setVariant(EcdsaParameters.Variant.TINK)
         .build()
   );
   public static final EcdsaParameters ECDSA_P384_IEEE_P1363 = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.IEEE_P1363)
         .setCurveType(EcdsaParameters.CurveType.NIST_P384)
         .setHashType(EcdsaParameters.HashType.SHA512)
         .setVariant(EcdsaParameters.Variant.TINK)
         .build()
   );
   public static final EcdsaParameters ECDSA_P256_IEEE_P1363_WITHOUT_PREFIX = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.IEEE_P1363)
         .setCurveType(EcdsaParameters.CurveType.NIST_P256)
         .setHashType(EcdsaParameters.HashType.SHA256)
         .setVariant(EcdsaParameters.Variant.NO_PREFIX)
         .build()
   );
   public static final EcdsaParameters ECDSA_P521_IEEE_P1363 = TinkBugException.exceptionIsBug(
      () -> EcdsaParameters.builder()
         .setHashType(EcdsaParameters.HashType.SHA512)
         .setCurveType(EcdsaParameters.CurveType.NIST_P521)
         .setSignatureEncoding(EcdsaParameters.SignatureEncoding.IEEE_P1363)
         .setVariant(EcdsaParameters.Variant.TINK)
         .build()
   );
   public static final Ed25519Parameters ED25519 = TinkBugException.exceptionIsBug(() -> Ed25519Parameters.create(Ed25519Parameters.Variant.TINK));
   public static final Ed25519Parameters ED25519WithRawOutput = TinkBugException.exceptionIsBug(
      () -> Ed25519Parameters.create(Ed25519Parameters.Variant.NO_PREFIX)
   );
   public static final RsaSsaPkcs1Parameters RSA_SSA_PKCS1_3072_SHA256_F4 = TinkBugException.exceptionIsBug(
      () -> RsaSsaPkcs1Parameters.builder()
         .setHashType(RsaSsaPkcs1Parameters.HashType.SHA256)
         .setModulusSizeBits(3072)
         .setPublicExponent(RsaSsaPkcs1Parameters.F4)
         .setVariant(RsaSsaPkcs1Parameters.Variant.TINK)
         .build()
   );
   public static final RsaSsaPkcs1Parameters RSA_SSA_PKCS1_3072_SHA256_F4_WITHOUT_PREFIX = TinkBugException.exceptionIsBug(
      () -> RsaSsaPkcs1Parameters.builder()
         .setHashType(RsaSsaPkcs1Parameters.HashType.SHA256)
         .setModulusSizeBits(3072)
         .setPublicExponent(RsaSsaPkcs1Parameters.F4)
         .setVariant(RsaSsaPkcs1Parameters.Variant.NO_PREFIX)
         .build()
   );
   public static final RsaSsaPkcs1Parameters RSA_SSA_PKCS1_4096_SHA512_F4 = TinkBugException.exceptionIsBug(
      () -> RsaSsaPkcs1Parameters.builder()
         .setHashType(RsaSsaPkcs1Parameters.HashType.SHA512)
         .setModulusSizeBits(4096)
         .setPublicExponent(RsaSsaPkcs1Parameters.F4)
         .setVariant(RsaSsaPkcs1Parameters.Variant.TINK)
         .build()
   );
   public static final RsaSsaPssParameters RSA_SSA_PSS_3072_SHA256_SHA256_32_F4 = TinkBugException.exceptionIsBug(
      () -> RsaSsaPssParameters.builder()
         .setSigHashType(RsaSsaPssParameters.HashType.SHA256)
         .setMgf1HashType(RsaSsaPssParameters.HashType.SHA256)
         .setSaltLengthBytes(32)
         .setModulusSizeBits(3072)
         .setPublicExponent(RsaSsaPssParameters.F4)
         .setVariant(RsaSsaPssParameters.Variant.TINK)
         .build()
   );
   public static final RsaSsaPssParameters RSA_SSA_PSS_4096_SHA512_SHA512_64_F4 = TinkBugException.exceptionIsBug(
      () -> RsaSsaPssParameters.builder()
         .setSigHashType(RsaSsaPssParameters.HashType.SHA512)
         .setMgf1HashType(RsaSsaPssParameters.HashType.SHA512)
         .setSaltLengthBytes(64)
         .setModulusSizeBits(4096)
         .setPublicExponent(RsaSsaPssParameters.F4)
         .setVariant(RsaSsaPssParameters.Variant.TINK)
         .build()
   );
   public static final MlDsaParameters ML_DSA_65 = TinkBugException.exceptionIsBug(
      () -> MlDsaParameters.create(MlDsaParameters.MlDsaInstance.ML_DSA_65, MlDsaParameters.Variant.TINK)
   );

   private PredefinedSignatureParameters() {
   }
}
