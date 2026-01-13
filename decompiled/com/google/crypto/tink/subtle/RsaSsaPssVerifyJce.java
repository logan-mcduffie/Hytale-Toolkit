package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.RsaSsaPssParameters;
import com.google.crypto.tink.signature.RsaSsaPssPublicKey;
import com.google.crypto.tink.signature.internal.RsaSsaPssVerifyConscrypt;
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
public final class RsaSsaPssVerifyJce implements PublicKeyVerify {
   public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_REQUIRES_BORINGCRYPTO;
   static final EnumTypeProtoConverter<Enums.HashType, RsaSsaPssParameters.HashType> HASH_TYPE_CONVERTER = EnumTypeProtoConverter.<Enums.HashType, RsaSsaPssParameters.HashType>builder()
      .add(Enums.HashType.SHA256, RsaSsaPssParameters.HashType.SHA256)
      .add(Enums.HashType.SHA384, RsaSsaPssParameters.HashType.SHA384)
      .add(Enums.HashType.SHA512, RsaSsaPssParameters.HashType.SHA512)
      .build();
   private static final byte[] EMPTY = new byte[0];
   private static final byte[] legacyMessageSuffix = new byte[]{0};
   private final PublicKeyVerify verify;

   @AccessesPartialKey
   public static PublicKeyVerify create(RsaSsaPssPublicKey key) throws GeneralSecurityException {
      try {
         return RsaSsaPssVerifyConscrypt.create(key);
      } catch (NoSuchProviderException var4) {
         KeyFactory keyFactory = EngineFactory.KEY_FACTORY.getInstance("RSA");
         RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(new RSAPublicKeySpec(key.getModulus(), key.getParameters().getPublicExponent()));
         RsaSsaPssParameters params = key.getParameters();
         return new RsaSsaPssVerifyJce.InternalImpl(
            publicKey,
            HASH_TYPE_CONVERTER.toProtoEnum(params.getSigHashType()),
            HASH_TYPE_CONVERTER.toProtoEnum(params.getMgf1HashType()),
            params.getSaltLengthBytes(),
            key.getOutputPrefix().toByteArray(),
            key.getParameters().getVariant().equals(RsaSsaPssParameters.Variant.LEGACY) ? legacyMessageSuffix : EMPTY
         );
      }
   }

   private static RsaSsaPssParameters.HashType getHashType(Enums.HashType hash) throws GeneralSecurityException {
      switch (hash) {
         case SHA256:
            return RsaSsaPssParameters.HashType.SHA256;
         case SHA384:
            return RsaSsaPssParameters.HashType.SHA384;
         case SHA512:
            return RsaSsaPssParameters.HashType.SHA512;
         default:
            throw new GeneralSecurityException("Unsupported hash: " + hash);
      }
   }

   @AccessesPartialKey
   private RsaSsaPssPublicKey convertKey(final RSAPublicKey pubKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength) throws GeneralSecurityException {
      RsaSsaPssParameters parameters = RsaSsaPssParameters.builder()
         .setModulusSizeBits(pubKey.getModulus().bitLength())
         .setPublicExponent(pubKey.getPublicExponent())
         .setSigHashType(getHashType(sigHash))
         .setMgf1HashType(getHashType(mgf1Hash))
         .setSaltLengthBytes(saltLength)
         .setVariant(RsaSsaPssParameters.Variant.NO_PREFIX)
         .build();
      return RsaSsaPssPublicKey.builder().setParameters(parameters).setModulus(pubKey.getModulus()).build();
   }

   public RsaSsaPssVerifyJce(final RSAPublicKey pubKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength) throws GeneralSecurityException {
      this.verify = create(this.convertKey(pubKey, sigHash, mgf1Hash, saltLength));
   }

   @Override
   public void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
      this.verify.verify(signature, data);
   }

   private static final class InternalImpl implements PublicKeyVerify {
      private final RSAPublicKey publicKey;
      private final Enums.HashType sigHash;
      private final Enums.HashType mgf1Hash;
      private final int saltLength;
      private final byte[] outputPrefix;
      private final byte[] messageSuffix;

      private InternalImpl(
         final RSAPublicKey pubKey, Enums.HashType sigHash, Enums.HashType mgf1Hash, int saltLength, byte[] outputPrefix, byte[] messageSuffix
      ) throws GeneralSecurityException {
         if (TinkFipsUtil.useOnlyFips()) {
            throw new GeneralSecurityException("Can not use RSA PSS in FIPS-mode, as BoringCrypto module is not available.");
         } else {
            Validators.validateSignatureHash(sigHash);
            if (!sigHash.equals(mgf1Hash)) {
               throw new GeneralSecurityException("sigHash and mgf1Hash must be the same");
            } else {
               Validators.validateRsaModulusSize(pubKey.getModulus().bitLength());
               Validators.validateRsaPublicExponent(pubKey.getPublicExponent());
               this.publicKey = pubKey;
               this.sigHash = sigHash;
               this.mgf1Hash = mgf1Hash;
               this.saltLength = saltLength;
               this.outputPrefix = outputPrefix;
               this.messageSuffix = messageSuffix;
            }
         }
      }

      private void noPrefixVerify(final byte[] signature, final byte[] data) throws GeneralSecurityException {
         BigInteger e = this.publicKey.getPublicExponent();
         BigInteger n = this.publicKey.getModulus();
         int nLengthInBytes = (n.bitLength() + 7) / 8;
         int mLen = (n.bitLength() - 1 + 7) / 8;
         if (nLengthInBytes != signature.length) {
            throw new GeneralSecurityException("invalid signature's length");
         } else {
            BigInteger s = SubtleUtil.bytes2Integer(signature);
            if (s.compareTo(n) >= 0) {
               throw new GeneralSecurityException("signature out of range");
            } else {
               BigInteger m = s.modPow(e, n);
               byte[] em = SubtleUtil.integer2Bytes(m, mLen);
               this.emsaPssVerify(data, em, n.bitLength() - 1);
            }
         }
      }

      private void emsaPssVerify(byte[] message, byte[] em, int emBits) throws GeneralSecurityException {
         Validators.validateSignatureHash(this.sigHash);
         MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance(SubtleUtil.toDigestAlgo(this.sigHash));
         digest.update(message);
         if (this.messageSuffix.length != 0) {
            digest.update(this.messageSuffix);
         }

         byte[] mHash = digest.digest();
         int hLen = digest.getDigestLength();
         int emLen = em.length;
         if (emLen < hLen + this.saltLength + 2) {
            throw new GeneralSecurityException("inconsistent");
         } else if (em[em.length - 1] != -68) {
            throw new GeneralSecurityException("inconsistent");
         } else {
            byte[] maskedDb = Arrays.copyOf(em, emLen - hLen - 1);
            byte[] h = Arrays.copyOfRange(em, maskedDb.length, maskedDb.length + hLen);

            for (int i = 0; i < emLen * 8L - emBits; i++) {
               int bytePos = i / 8;
               int bitPos = 7 - i % 8;
               if ((maskedDb[bytePos] >> bitPos & 1) != 0) {
                  throw new GeneralSecurityException("inconsistent");
               }
            }

            byte[] dbMask = SubtleUtil.mgf1(h, emLen - hLen - 1, this.mgf1Hash);
            byte[] db = new byte[dbMask.length];

            for (int ix = 0; ix < db.length; ix++) {
               db[ix] = (byte)(dbMask[ix] ^ maskedDb[ix]);
            }

            for (int ix = 0; ix <= emLen * 8L - emBits; ix++) {
               int bytePos = ix / 8;
               int bitPos = 7 - ix % 8;
               db[bytePos] = (byte)(db[bytePos] & ~(1 << bitPos));
            }

            for (int ix = 0; ix < emLen - hLen - this.saltLength - 2; ix++) {
               if (db[ix] != 0) {
                  throw new GeneralSecurityException("inconsistent");
               }
            }

            if (db[emLen - hLen - this.saltLength - 2] != 1) {
               throw new GeneralSecurityException("inconsistent");
            } else {
               byte[] salt = Arrays.copyOfRange(db, db.length - this.saltLength, db.length);
               byte[] mPrime = new byte[8 + hLen + this.saltLength];
               System.arraycopy(mHash, 0, mPrime, 8, mHash.length);
               System.arraycopy(salt, 0, mPrime, 8 + hLen, salt.length);
               byte[] hPrime = digest.digest(mPrime);
               if (!Bytes.equal(hPrime, h)) {
                  throw new GeneralSecurityException("inconsistent");
               }
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
}
