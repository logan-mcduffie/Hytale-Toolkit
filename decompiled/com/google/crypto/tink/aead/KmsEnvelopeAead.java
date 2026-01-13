package com.google.crypto.tink.aead;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.TinkProtoParametersFormat;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class KmsEnvelopeAead implements Aead {
   private static final byte[] EMPTY_AAD = new byte[0];
   private final String typeUrlForParsing;
   private final Parameters parametersForNewKeys;
   private final Aead remote;
   private static final int LENGTH_ENCRYPTED_DEK = 4;
   private static final int MAX_LENGTH_ENCRYPTED_DEK = 4096;
   private static final Set<String> supportedDekKeyTypes = listSupportedDekKeyTypes();

   private static Set<String> listSupportedDekKeyTypes() {
      HashSet<String> dekKeyTypeUrls = new HashSet<>();
      dekKeyTypeUrls.add("type.googleapis.com/google.crypto.tink.AesGcmKey");
      dekKeyTypeUrls.add("type.googleapis.com/google.crypto.tink.ChaCha20Poly1305Key");
      dekKeyTypeUrls.add("type.googleapis.com/google.crypto.tink.XChaCha20Poly1305Key");
      dekKeyTypeUrls.add("type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey");
      dekKeyTypeUrls.add("type.googleapis.com/google.crypto.tink.AesGcmSivKey");
      dekKeyTypeUrls.add("type.googleapis.com/google.crypto.tink.AesEaxKey");
      return Collections.unmodifiableSet(dekKeyTypeUrls);
   }

   public static boolean isSupportedDekKeyType(String dekKeyTypeUrl) {
      return supportedDekKeyTypes.contains(dekKeyTypeUrl);
   }

   private Parameters getRawParameters(KeyTemplate dekTemplate) throws GeneralSecurityException {
      KeyTemplate rawTemplate = KeyTemplate.newBuilder(dekTemplate).setOutputPrefixType(OutputPrefixType.RAW).build();
      return TinkProtoParametersFormat.parse(rawTemplate.toByteArray());
   }

   @Deprecated
   public KmsEnvelopeAead(KeyTemplate dekTemplate, Aead remote) throws GeneralSecurityException {
      if (!isSupportedDekKeyType(dekTemplate.getTypeUrl())) {
         throw new IllegalArgumentException("Unsupported DEK key type: " + dekTemplate.getTypeUrl() + ". Only Tink AEAD key types are supported.");
      } else {
         this.typeUrlForParsing = dekTemplate.getTypeUrl();
         this.parametersForNewKeys = this.getRawParameters(dekTemplate);
         this.remote = remote;
      }
   }

   public static Aead create(AeadParameters dekParameters, Aead remote) throws GeneralSecurityException {
      KeyTemplate dekTemplate;
      try {
         dekTemplate = KeyTemplate.parseFrom(TinkProtoParametersFormat.serialize(dekParameters), ExtensionRegistryLite.getEmptyRegistry());
      } catch (InvalidProtocolBufferException var4) {
         throw new GeneralSecurityException(var4);
      }

      return new KmsEnvelopeAead(dekTemplate, remote);
   }

   @Override
   public byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException {
      Key key = MutableKeyCreationRegistry.globalInstance().createKey(this.parametersForNewKeys, null);
      ProtoKeySerialization serialization = MutableSerializationRegistry.globalInstance()
         .serializeKey(key, ProtoKeySerialization.class, InsecureSecretKeyAccess.get());
      byte[] dek = serialization.getValue().toByteArray();
      byte[] encryptedDek = this.remote.encrypt(dek, EMPTY_AAD);
      if (encryptedDek.length > 4096) {
         throw new GeneralSecurityException("length of encrypted DEK too large");
      } else {
         Aead aead = MutablePrimitiveRegistry.globalInstance().getPrimitive(key, Aead.class);
         byte[] payload = aead.encrypt(plaintext, associatedData);
         return this.buildCiphertext(encryptedDek, payload);
      }
   }

   @Override
   public byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException {
      try {
         ByteBuffer buffer = ByteBuffer.wrap(ciphertext);
         int encryptedDekSize = buffer.getInt();
         if (encryptedDekSize > 0 && encryptedDekSize <= 4096 && encryptedDekSize <= ciphertext.length - 4) {
            byte[] encryptedDek = new byte[encryptedDekSize];
            buffer.get(encryptedDek, 0, encryptedDekSize);
            byte[] payload = new byte[buffer.remaining()];
            buffer.get(payload, 0, buffer.remaining());
            byte[] dek = this.remote.decrypt(encryptedDek, EMPTY_AAD);
            ProtoKeySerialization serialization = ProtoKeySerialization.create(
               this.typeUrlForParsing, ByteString.copyFrom(dek), KeyData.KeyMaterialType.SYMMETRIC, OutputPrefixType.RAW, null
            );
            Key key = MutableSerializationRegistry.globalInstance().parseKey(serialization, InsecureSecretKeyAccess.get());
            Aead aead = MutablePrimitiveRegistry.globalInstance().getPrimitive(key, Aead.class);
            return aead.decrypt(payload, associatedData);
         } else {
            throw new GeneralSecurityException("length of encrypted DEK too large");
         }
      } catch (BufferUnderflowException | NegativeArraySizeException | IndexOutOfBoundsException var11) {
         throw new GeneralSecurityException("invalid ciphertext", var11);
      }
   }

   private byte[] buildCiphertext(final byte[] encryptedDek, final byte[] payload) {
      return ByteBuffer.allocate(4 + encryptedDek.length + payload.length).putInt(encryptedDek.length).put(encryptedDek).put(payload).array();
   }
}
