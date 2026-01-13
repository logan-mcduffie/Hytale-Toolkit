package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.RsaSsaPkcs1Parameters;
import com.google.crypto.tink.signature.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.signature.internal.RsaSsaPkcs1VerifyConscrypt;
import com.google.errorprone.annotations.Immutable;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

@Immutable
public final class RsaSsaPkcs1VerifyJce implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   static final EnumTypeProtoConverter<Enums.HashType, RsaSsaPkcs1Parameters.HashType> HASH_TYPE_CONVERTER = EnumTypeProtoConverter.<Enums.HashType, RsaSsaPkcs1Parameters.HashType>builder()
      .add(Enums.HashType.SHA256, RsaSsaPkcs1Parameters.HashType.SHA256)
      .add(Enums.HashType.SHA384, RsaSsaPkcs1Parameters.HashType.SHA384)
      .add(Enums.HashType.SHA512, RsaSsaPkcs1Parameters.HashType.SHA512)
      .build();
   private final PublicKeyVerify verify;

   @AccessesPartialKey
   public static PublicKeyVerify create(RsaSsaPkcs1PublicKey key) throws GeneralSecurityException {
      try {
         return RsaSsaPkcs1VerifyConscrypt.create(key);
      } catch (NoSuchProviderException var3) {
         KeyFactory keyFactory = EngineFactory.KEY_FACTORY.getInstance("RSA");
         RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(new RSAPublicKeySpec(key.getModulus(), key.getParameters().getPublicExponent()));
         return new RsaSsaPkcs1VerifyJce.InternalJavaImpl(
            publicKey,
            HASH_TYPE_CONVERTER.toProtoEnum(key.getParameters().getHashType()),
            key.getOutputPrefix().toByteArray(),
            key.getParameters().getVariant().equals(RsaSsaPkcs1Parameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY
         );
      }
   }

   private static RsaSsaPkcs1Parameters.HashType getHashType(Enums.HashType hash) throws GeneralSecurityException {
      switch (hash) {
         case SHA256:
            return RsaSsaPkcs1Parameters.HashType.SHA256;
         case SHA384:
            return RsaSsaPkcs1Parameters.HashType.SHA384;
         case SHA512:
            return RsaSsaPkcs1Parameters.HashType.SHA512;
         default:
            throw new GeneralSecurityException("Unsupported hash: " + hash);
      }
   }

   @AccessesPartialKey
   private RsaSsaPkcs1PublicKey convertKey(final RSAPublicKey pubKey, Enums.HashType hash) throws GeneralSecurityException {
      RsaSsaPkcs1Parameters parameters = RsaSsaPkcs1Parameters.builder()
         .setModulusSizeBits(pubKey.getModulus().bitLength())
         .setPublicExponent(pubKey.getPublicExponent())
         .setHashType(getHashType(hash))
         .setVariant(RsaSsaPkcs1Parameters.Variant.NO_PREFIX)
         .build();
      return RsaSsaPkcs1PublicKey.builder().setParameters(parameters).setModulus(pubKey.getModulus()).build();
   }

   public RsaSsaPkcs1VerifyJce(final RSAPublicKey pubKey, Enums.HashType hash) throws GeneralSecurityException {
      this.verify = create(this.convertKey(pubKey, hash));
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      this.verify.verify(signature, data);
   }

   private static final class InternalJavaImpl implements PublicKeyVerify {
      private static final String ASN_PREFIX_SHA256 = "3031300d060960864801650304020105000420";
      private static final String ASN_PREFIX_SHA384 = "3041300d060960864801650304020205000430";
      private static final String ASN_PREFIX_SHA512 = "3051300d060960864801650304020305000440";
      private final RSAPublicKey publicKey;
      private final Enums.HashType hash;
      private final byte[] outputPrefix;
      private final byte[] messageSuffix;

      private InternalJavaImpl(final RSAPublicKey pubKey, Enums.HashType hash, byte[] outputPrefix, byte[] messageSuffix) throws GeneralSecurityException {
         if (TinkFipsUtil.useOnlyFips()) {
            throw new GeneralSecurityException("Conscrypt is not available, and we cannot use Java Implementation of RSA-PKCS1.5 in FIPS-mode.");
         } else {
            Validators.validateSignatureHash(hash);
            Validators.validateRsaModulusSize(pubKey.getModulus().bitLength());
            Validators.validateRsaPublicExponent(pubKey.getPublicExponent());
            this.publicKey = pubKey;
            this.hash = hash;
            this.outputPrefix = outputPrefix;
            this.messageSuffix = messageSuffix;
         }
      }

      private void noPrefixVerify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
         BigInteger e = this.publicKey.getPublicExponent();
         BigInteger n = this.publicKey.getModulus();
         int nLengthInBytes = (n.bitLength() + 7) / 8;
         if (nLengthInBytes != signature.length) {
            throw new GeneralSecurityException("invalid signature's length");
         } else {
            BigInteger s = SubtleUtil.bytes2Integer(signature);
            if (s.compareTo(n) >= 0) {
               throw new GeneralSecurityException("signature out of range");
            } else {
               BigInteger m = s.modPow(e, n);
               byte[] em = SubtleUtil.integer2Bytes(m, nLengthInBytes);
               byte[] expectedEm = this.emsaPkcs1(data, nLengthInBytes, this.hash);
               if (!Bytes.equal(em, expectedEm)) {
                  throw new GeneralSecurityException("invalid signature");
               }
            }
         }
      }

      private byte[] emsaPkcs1(byte[] m, int emLen, Enums.HashType hash) throws GeneralSecurityException {
         Validators.validateSignatureHash(hash);
         MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance(SubtleUtil.toDigestAlgo(this.hash));
         digest.update(m);
         if (this.messageSuffix.length != 0) {
            digest.update(this.messageSuffix);
         }

         byte[] h = digest.digest();
         byte[] asnPrefix = this.toAsnPrefix(hash);
         int tLen = asnPrefix.length + h.length;
         if (emLen < tLen + 11) {
            throw new GeneralSecurityException("intended encoded message length too short");
         } else {
            byte[] em = new byte[emLen];
            int offset = 0;
            em[offset++] = 0;
            em[offset++] = 1;

            for (int i = 0; i < emLen - tLen - 3; i++) {
               em[offset++] = -1;
            }

            em[offset++] = 0;
            System.arraycopy(asnPrefix, 0, em, offset, asnPrefix.length);
            System.arraycopy(h, 0, em, offset + asnPrefix.length, h.length);
            return em;
         }
      }

      private byte[] toAsnPrefix(Enums.HashType hash) throws GeneralSecurityException {
         switch (hash) {
            case SHA256:
               return Hex.decode("3031300d060960864801650304020105000420");
            case SHA384:
               return Hex.decode("3041300d060960864801650304020205000430");
            case SHA512:
               return Hex.decode("3051300d060960864801650304020305000440");
            default:
               throw new GeneralSecurityException("Unsupported hash " + hash);
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
}
