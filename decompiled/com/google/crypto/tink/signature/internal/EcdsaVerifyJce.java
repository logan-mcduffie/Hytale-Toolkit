package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.EcdsaParameters;
import com.google.crypto.tink.signature.EcdsaPublicKey;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Enums;
import com.google.crypto.tink.subtle.SubtleUtil;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Arrays;
import javax.annotation.Nullable;

@Immutable
public final class EcdsaVerifyJce implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private final ECPublicKey publicKey;
   private final String signatureAlgorithm;
   private final EllipticCurves.EcdsaEncoding encoding;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;
   @Nullable
   private final Provider provider;
   static final EnumTypeProtoConverter<Enums.HashType, EcdsaParameters.HashType> HASH_TYPE_CONVERTER = EnumTypeProtoConverter.<Enums.HashType, EcdsaParameters.HashType>builder()
      .add(Enums.HashType.SHA256, EcdsaParameters.HashType.SHA256)
      .add(Enums.HashType.SHA384, EcdsaParameters.HashType.SHA384)
      .add(Enums.HashType.SHA512, EcdsaParameters.HashType.SHA512)
      .build();
   static final EnumTypeProtoConverter<EllipticCurves.EcdsaEncoding, EcdsaParameters.SignatureEncoding> ENCODING_CONVERTER = EnumTypeProtoConverter.<EllipticCurves.EcdsaEncoding, EcdsaParameters.SignatureEncoding>builder()
      .add(EllipticCurves.EcdsaEncoding.IEEE_P1363, EcdsaParameters.SignatureEncoding.IEEE_P1363)
      .add(EllipticCurves.EcdsaEncoding.DER, EcdsaParameters.SignatureEncoding.DER)
      .build();
   static final EnumTypeProtoConverter<EllipticCurves.CurveType, EcdsaParameters.CurveType> CURVE_TYPE_CONVERTER = EnumTypeProtoConverter.<EllipticCurves.CurveType, EcdsaParameters.CurveType>builder()
      .add(EllipticCurves.CurveType.NIST_P256, EcdsaParameters.CurveType.NIST_P256)
      .add(EllipticCurves.CurveType.NIST_P384, EcdsaParameters.CurveType.NIST_P384)
      .add(EllipticCurves.CurveType.NIST_P521, EcdsaParameters.CurveType.NIST_P521)
      .build();

   public static PublicKeyVerify create(EcdsaPublicKey key) throws GeneralSecurityException {
      Provider provider = ConscryptUtil.providerOrNull();
      return createWithProviderOrNull(key, provider);
   }

   public static PublicKeyVerify createWithProvider(EcdsaPublicKey key, Provider provider) throws GeneralSecurityException {
      if (provider == null) {
         throw new NullPointerException("provider must not be null");
      } else {
         return createWithProviderOrNull(key, provider);
      }
   }

   @AccessesPartialKey
   public static PublicKeyVerify createWithProviderOrNull(EcdsaPublicKey key, @Nullable Provider provider) throws GeneralSecurityException {
      ECParameterSpec ecParams = EllipticCurves.getCurveSpec(CURVE_TYPE_CONVERTER.toProtoEnum(key.getParameters().getCurveType()));
      ECPoint publicPoint = key.getPublicPoint();
      ECPublicKeySpec spec = new ECPublicKeySpec(publicPoint, ecParams);
      KeyFactory keyFactory;
      if (provider != null) {
         keyFactory = KeyFactory.getInstance("EC", provider);
      } else {
         keyFactory = EngineFactory.KEY_FACTORY.getInstance("EC");
      }

      ECPublicKey publicKey = (ECPublicKey)keyFactory.generatePublic(spec);
      return new EcdsaVerifyJce(
         publicKey,
         HASH_TYPE_CONVERTER.toProtoEnum(key.getParameters().getHashType()),
         ENCODING_CONVERTER.toProtoEnum(key.getParameters().getSignatureEncoding()),
         key.getOutputPrefix().toByteArray(),
         key.getParameters().getVariant().equals(EcdsaParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY,
         provider
      );
   }

   private EcdsaVerifyJce(
      final ECPublicKey publicKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding, byte[] outputPrefix, byte[] messageSuffix, Provider provider
   ) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ECDSA in FIPS-mode, as BoringCrypto is not available.");
      } else {
         this.signatureAlgorithm = SubtleUtil.toEcdsaAlgo(hash);
         this.publicKey = publicKey;
         this.encoding = encoding;
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         this.provider = provider;
      }
   }

   public EcdsaVerifyJce(final ECPublicKey publicKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding) throws GeneralSecurityException {
      this(publicKey, hash, encoding, EMPTY, EMPTY, ConscryptUtil.providerOrNull());
      EllipticCurvesUtil.checkPointOnCurve(publicKey.getW(), publicKey.getParams().getCurve());
   }

   private Signature getInstance(String signatureAlgorithm) throws GeneralSecurityException {
      return this.provider != null ? Signature.getInstance(signatureAlgorithm, this.provider) : EngineFactory.SIGNATURE.getInstance(signatureAlgorithm);
   }

   private void noPrefixVerify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      byte[] derSignature = signature;
      if (this.encoding == EllipticCurves.EcdsaEncoding.IEEE_P1363) {
         EllipticCurve curve = this.publicKey.getParams().getCurve();
         if (signature.length != 2 * EllipticCurves.fieldSizeInBytes(curve)) {
            throw new GeneralSecurityException("Invalid signature");
         }

         derSignature = EllipticCurves.ecdsaIeee2Der(signature);
      }

      if (!EllipticCurves.isValidDerEncoding(derSignature)) {
         throw new GeneralSecurityException("Invalid signature");
      } else {
         Signature verifier = this.getInstance(this.signatureAlgorithm);
         verifier.initVerify(this.publicKey);
         verifier.update(data);
         if (this.messageSuffix.length > 0) {
            verifier.update(this.messageSuffix);
         }

         boolean verified = false;

         try {
            verified = verifier.verify(derSignature);
         } catch (RuntimeException var7) {
            verified = false;
         }

         if (!verified) {
            throw new GeneralSecurityException("Invalid signature");
         }
      }
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      if (this.outputPrefix.length == 0) {
         this.noPrefixVerify(signature, data);
      } else if (!Util.isPrefix(this.outputPrefix, signature)) {
         throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
      } else {
         byte[] signatureNoPrefix = Arrays.copyOfRange(signature, this.outputPrefix.length, signature.length);
         this.noPrefixVerify(signatureNoPrefix, data);
      }
   }
}
