package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.signature.EcdsaParameters;
import com.google.crypto.tink.signature.EcdsaPrivateKey;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.EngineFactory;
import com.google.crypto.tink.subtle.Enums;
import com.google.crypto.tink.subtle.SubtleUtil;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import javax.annotation.Nullable;

@Immutable
public final class EcdsaSignJce implements PublicKeySign {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private final ECPrivateKey privateKey;
   private final String signatureAlgorithm;
   private final EllipticCurves.EcdsaEncoding encoding;
   private final byte[] outputPrefix;
   private final byte[] messageSuffix;
   @Nullable
   private final Provider provider;

   private EcdsaSignJce(
      final ECPrivateKey privateKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding, byte[] outputPrefix, byte[] messageSuffix, Provider provider
   ) throws GeneralSecurityException {
      if (!FIPS.isCompatible()) {
         throw new GeneralSecurityException("Can not use ECDSA in FIPS-mode, as BoringCrypto is not available.");
      } else {
         this.privateKey = privateKey;
         this.signatureAlgorithm = SubtleUtil.toEcdsaAlgo(hash);
         this.encoding = encoding;
         this.outputPrefix = outputPrefix;
         this.messageSuffix = messageSuffix;
         this.provider = provider;
      }
   }

   public EcdsaSignJce(final ECPrivateKey privateKey, Enums.HashType hash, EllipticCurves.EcdsaEncoding encoding) throws GeneralSecurityException {
      this(privateKey, hash, encoding, EMPTY, EMPTY, ConscryptUtil.providerOrNull());
   }

   public static PublicKeySign create(EcdsaPrivateKey key) throws GeneralSecurityException {
      Provider provider = ConscryptUtil.providerOrNull();
      return createWithProviderOrNull(key, provider);
   }

   public static PublicKeySign createWithProvider(EcdsaPrivateKey key, Provider provider) throws GeneralSecurityException {
      if (provider == null) {
         throw new NullPointerException("provider must not be null");
      } else {
         return createWithProviderOrNull(key, provider);
      }
   }

   @AccessesPartialKey
   private static PublicKeySign createWithProviderOrNull(EcdsaPrivateKey key, @Nullable Provider provider) throws GeneralSecurityException {
      Enums.HashType hashType = EcdsaVerifyJce.HASH_TYPE_CONVERTER.toProtoEnum(key.getParameters().getHashType());
      EllipticCurves.EcdsaEncoding ecdsaEncoding = EcdsaVerifyJce.ENCODING_CONVERTER.toProtoEnum(key.getParameters().getSignatureEncoding());
      EllipticCurves.CurveType curveType = EcdsaVerifyJce.CURVE_TYPE_CONVERTER.toProtoEnum(key.getParameters().getCurveType());
      ECParameterSpec ecParams = EllipticCurves.getCurveSpec(curveType);
      ECPrivateKeySpec spec = new ECPrivateKeySpec(key.getPrivateValue().getBigInteger(InsecureSecretKeyAccess.get()), ecParams);
      KeyFactory keyFactory;
      if (provider != null) {
         keyFactory = KeyFactory.getInstance("EC", provider);
      } else {
         keyFactory = EngineFactory.KEY_FACTORY.getInstance("EC");
      }

      ECPrivateKey privateKey = (ECPrivateKey)keyFactory.generatePrivate(spec);
      return new EcdsaSignJce(
         privateKey,
         hashType,
         ecdsaEncoding,
         key.getOutputPrefix().toByteArray(),
         key.getParameters().getVariant().equals(EcdsaParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY,
         provider
      );
   }

   private Signature getInstance(String signatureAlgorithm) throws GeneralSecurityException {
      return this.provider != null ? Signature.getInstance(signatureAlgorithm, this.provider) : EngineFactory.SIGNATURE.getInstance(signatureAlgorithm);
   }

   @Override
   public byte[] sign(final byte[] data) throws GeneralSecurityException {
      Signature signer = this.getInstance(this.signatureAlgorithm);
      signer.initSign(this.privateKey);
      signer.update(data);
      if (this.messageSuffix.length > 0) {
         signer.update(this.messageSuffix);
      }

      byte[] signature = signer.sign();
      if (this.encoding == EllipticCurves.EcdsaEncoding.IEEE_P1363) {
         EllipticCurve curve = this.privateKey.getParams().getCurve();
         signature = EllipticCurves.ecdsaDer2Ieee(signature, 2 * EllipticCurves.fieldSizeInBytes(curve));
      }

      return this.outputPrefix.length == 0 ? signature : Bytes.concat(this.outputPrefix, signature);
   }
}
