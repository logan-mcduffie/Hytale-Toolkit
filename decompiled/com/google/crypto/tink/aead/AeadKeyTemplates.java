package com.google.crypto.tink.aead;

import com.google.crypto.tink.proto.AesCtrHmacAeadKeyFormat;
import com.google.crypto.tink.proto.AesCtrKeyFormat;
import com.google.crypto.tink.proto.AesCtrParams;
import com.google.crypto.tink.proto.AesEaxKeyFormat;
import com.google.crypto.tink.proto.AesEaxParams;
import com.google.crypto.tink.proto.AesGcmKeyFormat;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HmacKeyFormat;
import com.google.crypto.tink.proto.HmacParams;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KmsEnvelopeAeadKeyFormat;
import com.google.crypto.tink.proto.OutputPrefixType;

public final class AeadKeyTemplates {
   public static final KeyTemplate AES128_GCM = createAesGcmKeyTemplate(16);
   public static final KeyTemplate AES256_GCM = createAesGcmKeyTemplate(32);
   public static final KeyTemplate AES128_EAX = createAesEaxKeyTemplate(16, 16);
   public static final KeyTemplate AES256_EAX = createAesEaxKeyTemplate(32, 16);
   public static final KeyTemplate AES128_CTR_HMAC_SHA256 = createAesCtrHmacAeadKeyTemplate(16, 16, 32, 16, HashType.SHA256);
   public static final KeyTemplate AES256_CTR_HMAC_SHA256 = createAesCtrHmacAeadKeyTemplate(32, 16, 32, 32, HashType.SHA256);
   public static final KeyTemplate CHACHA20_POLY1305 = KeyTemplate.newBuilder()
      .setTypeUrl(ChaCha20Poly1305KeyManager.getKeyType())
      .setOutputPrefixType(OutputPrefixType.TINK)
      .build();
   public static final KeyTemplate XCHACHA20_POLY1305 = KeyTemplate.newBuilder()
      .setTypeUrl(XChaCha20Poly1305KeyManager.getKeyType())
      .setOutputPrefixType(OutputPrefixType.TINK)
      .build();

   public static KeyTemplate createAesGcmKeyTemplate(int keySize) {
      AesGcmKeyFormat format = AesGcmKeyFormat.newBuilder().setKeySize(keySize).build();
      return KeyTemplate.newBuilder()
         .setValue(format.toByteString())
         .setTypeUrl(AesGcmKeyManager.getKeyType())
         .setOutputPrefixType(OutputPrefixType.TINK)
         .build();
   }

   public static KeyTemplate createAesEaxKeyTemplate(int keySize, int ivSize) {
      AesEaxKeyFormat format = AesEaxKeyFormat.newBuilder().setKeySize(keySize).setParams(AesEaxParams.newBuilder().setIvSize(ivSize).build()).build();
      return KeyTemplate.newBuilder()
         .setValue(format.toByteString())
         .setTypeUrl(AesEaxKeyManager.getKeyType())
         .setOutputPrefixType(OutputPrefixType.TINK)
         .build();
   }

   public static KeyTemplate createAesCtrHmacAeadKeyTemplate(int aesKeySize, int ivSize, int hmacKeySize, int tagSize, HashType hashType) {
      AesCtrKeyFormat aesCtrKeyFormat = AesCtrKeyFormat.newBuilder()
         .setParams(AesCtrParams.newBuilder().setIvSize(ivSize).build())
         .setKeySize(aesKeySize)
         .build();
      HmacKeyFormat hmacKeyFormat = HmacKeyFormat.newBuilder()
         .setParams(HmacParams.newBuilder().setHash(hashType).setTagSize(tagSize).build())
         .setKeySize(hmacKeySize)
         .build();
      AesCtrHmacAeadKeyFormat format = AesCtrHmacAeadKeyFormat.newBuilder().setAesCtrKeyFormat(aesCtrKeyFormat).setHmacKeyFormat(hmacKeyFormat).build();
      return KeyTemplate.newBuilder()
         .setValue(format.toByteString())
         .setTypeUrl(AesCtrHmacAeadKeyManager.getKeyType())
         .setOutputPrefixType(OutputPrefixType.TINK)
         .build();
   }

   public static KeyTemplate createKmsEnvelopeAeadKeyTemplate(String kekUri, KeyTemplate dekTemplate) {
      KmsEnvelopeAeadKeyFormat format = KmsEnvelopeAeadKeyFormat.newBuilder().setDekTemplate(dekTemplate).setKekUri(kekUri).build();
      return KeyTemplate.newBuilder()
         .setValue(format.toByteString())
         .setTypeUrl(KmsEnvelopeAeadKeyManager.getKeyType())
         .setOutputPrefixType(OutputPrefixType.RAW)
         .build();
   }

   private AeadKeyTemplates() {
   }
}
