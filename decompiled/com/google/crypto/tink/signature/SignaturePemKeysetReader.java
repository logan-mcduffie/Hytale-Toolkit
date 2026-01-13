package com.google.crypto.tink.signature;

import com.google.crypto.tink.KeysetReader;
import com.google.crypto.tink.PemKeyType;
import com.google.crypto.tink.proto.EcdsaParams;
import com.google.crypto.tink.proto.EcdsaSignatureEncoding;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.proto.RsaSsaPkcs1Params;
import com.google.crypto.tink.proto.RsaSsaPssParams;
import com.google.crypto.tink.signature.internal.SigUtil;
import com.google.crypto.tink.subtle.Random;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class SignaturePemKeysetReader implements KeysetReader {
   private List<SignaturePemKeysetReader.PemKey> pemKeys;

   SignaturePemKeysetReader(List<SignaturePemKeysetReader.PemKey> pemKeys) {
      this.pemKeys = pemKeys;
   }

   public static SignaturePemKeysetReader.Builder newBuilder() {
      return new SignaturePemKeysetReader.Builder();
   }

   @Override
   public Keyset read() throws IOException {
      Keyset.Builder keyset = Keyset.newBuilder();

      for (SignaturePemKeysetReader.PemKey pemKey : this.pemKeys) {
         for (Keyset.Key key = readKey(pemKey.reader, pemKey.type); key != null; key = readKey(pemKey.reader, pemKey.type)) {
            keyset.addKey(key);
         }
      }

      if (keyset.getKeyCount() == 0) {
         throw new IOException("cannot find any key");
      } else {
         keyset.setPrimaryKeyId(keyset.getKey(0).getKeyId());
         return keyset.build();
      }
   }

   @Override
   public EncryptedKeyset readEncrypted() throws IOException {
      throw new UnsupportedOperationException();
   }

   @Nullable
   private static Keyset.Key readKey(BufferedReader reader, PemKeyType pemKeyType) throws IOException {
      Key key = pemKeyType.readKey(reader);
      if (key == null) {
         return null;
      } else {
         KeyData keyData;
         if (key instanceof RSAPublicKey) {
            keyData = convertRsaPublicKey(pemKeyType, (RSAPublicKey)key);
         } else {
            if (!(key instanceof ECPublicKey)) {
               return null;
            }

            keyData = convertEcPublicKey(pemKeyType, (ECPublicKey)key);
         }

         return Keyset.Key.newBuilder()
            .setKeyData(keyData)
            .setStatus(KeyStatusType.ENABLED)
            .setOutputPrefixType(OutputPrefixType.RAW)
            .setKeyId(Random.randInt())
            .build();
      }
   }

   private static KeyData convertRsaPublicKey(PemKeyType pemKeyType, RSAPublicKey key) throws IOException {
      if (pemKeyType.algorithm.equals("RSASSA-PKCS1-v1_5")) {
         RsaSsaPkcs1Params params = RsaSsaPkcs1Params.newBuilder().setHashType(getHashType(pemKeyType)).build();
         com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey pkcs1PubKey = com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey.newBuilder()
            .setVersion(0)
            .setParams(params)
            .setE(SigUtil.toUnsignedIntByteString(key.getPublicExponent()))
            .setN(SigUtil.toUnsignedIntByteString(key.getModulus()))
            .build();
         return KeyData.newBuilder()
            .setTypeUrl(RsaSsaPkcs1VerifyKeyManager.getKeyType())
            .setValue(pkcs1PubKey.toByteString())
            .setKeyMaterialType(KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC)
            .build();
      } else if (pemKeyType.algorithm.equals("RSASSA-PSS")) {
         RsaSsaPssParams params = RsaSsaPssParams.newBuilder()
            .setSigHash(getHashType(pemKeyType))
            .setMgf1Hash(getHashType(pemKeyType))
            .setSaltLength(getDigestSizeInBytes(pemKeyType))
            .build();
         com.google.crypto.tink.proto.RsaSsaPssPublicKey pssPubKey = com.google.crypto.tink.proto.RsaSsaPssPublicKey.newBuilder()
            .setVersion(0)
            .setParams(params)
            .setE(SigUtil.toUnsignedIntByteString(key.getPublicExponent()))
            .setN(SigUtil.toUnsignedIntByteString(key.getModulus()))
            .build();
         return KeyData.newBuilder()
            .setTypeUrl(RsaSsaPssVerifyKeyManager.getKeyType())
            .setValue(pssPubKey.toByteString())
            .setKeyMaterialType(KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC)
            .build();
      } else {
         throw new IOException("unsupported RSA signature algorithm: " + pemKeyType.algorithm);
      }
   }

   private static KeyData convertEcPublicKey(PemKeyType pemKeyType, ECPublicKey key) throws IOException {
      if (pemKeyType.algorithm.equals("ECDSA")) {
         EcdsaParams params = EcdsaParams.newBuilder()
            .setHashType(getHashType(pemKeyType))
            .setCurve(getCurveType(pemKeyType))
            .setEncoding(EcdsaSignatureEncoding.DER)
            .build();
         com.google.crypto.tink.proto.EcdsaPublicKey ecdsaPubKey = com.google.crypto.tink.proto.EcdsaPublicKey.newBuilder()
            .setVersion(0)
            .setParams(params)
            .setX(SigUtil.toUnsignedIntByteString(key.getW().getAffineX()))
            .setY(SigUtil.toUnsignedIntByteString(key.getW().getAffineY()))
            .build();
         return KeyData.newBuilder()
            .setTypeUrl(EcdsaVerifyKeyManager.getKeyType())
            .setValue(ecdsaPubKey.toByteString())
            .setKeyMaterialType(KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC)
            .build();
      } else {
         throw new IOException("unsupported EC signature algorithm: " + pemKeyType.algorithm);
      }
   }

   private static HashType getHashType(PemKeyType pemKeyType) {
      switch (pemKeyType.hash) {
         case SHA256:
            return HashType.SHA256;
         case SHA384:
            return HashType.SHA384;
         case SHA512:
            return HashType.SHA512;
         default:
            throw new IllegalArgumentException("unsupported hash type: " + pemKeyType.hash.name());
      }
   }

   private static int getDigestSizeInBytes(PemKeyType pemKeyType) {
      switch (pemKeyType.hash) {
         case SHA256:
            return 32;
         case SHA384:
            return 48;
         case SHA512:
            return 64;
         default:
            throw new IllegalArgumentException("unsupported hash type: " + pemKeyType.hash.name());
      }
   }

   private static EllipticCurveType getCurveType(PemKeyType pemKeyType) {
      switch (pemKeyType.keySizeInBits) {
         case 256:
            return EllipticCurveType.NIST_P256;
         case 384:
            return EllipticCurveType.NIST_P384;
         case 521:
            return EllipticCurveType.NIST_P521;
         default:
            throw new IllegalArgumentException("unsupported curve for key size: " + pemKeyType.keySizeInBits);
      }
   }

   public static final class Builder {
      private List<SignaturePemKeysetReader.PemKey> pemKeys = new ArrayList<>();

      Builder() {
      }

      public KeysetReader build() {
         return new SignaturePemKeysetReader(this.pemKeys);
      }

      @CanIgnoreReturnValue
      public SignaturePemKeysetReader.Builder addPem(String pem, PemKeyType keyType) {
         SignaturePemKeysetReader.PemKey pemKey = new SignaturePemKeysetReader.PemKey();
         pemKey.reader = new BufferedReader(new StringReader(pem));
         pemKey.type = keyType;
         this.pemKeys.add(pemKey);
         return this;
      }
   }

   private static final class PemKey {
      BufferedReader reader;
      PemKeyType type;

      private PemKey() {
      }
   }
}
