package com.google.crypto.tink.tinkkey;

import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.TinkProtoParametersFormat;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.KeyTemplateProtoConverter;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.tinkkey.internal.ProtoKey;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;

@Immutable
public class KeyHandle {
   private final TinkKey key;
   private final KeyHandle.KeyStatusType status;
   private final int id;

   private static KeyData newKeyData(KeyTemplate keyTemplate) throws GeneralSecurityException {
      try {
         byte[] serializedKeyTemplate = TinkProtoParametersFormat.serialize(keyTemplate.toParameters());
         com.google.crypto.tink.proto.KeyTemplate protoTemplate = com.google.crypto.tink.proto.KeyTemplate.parseFrom(
            serializedKeyTemplate, ExtensionRegistryLite.getEmptyRegistry()
         );
         KeyManager<?> manager = KeyManagerRegistry.globalInstance().getUntypedKeyManager(protoTemplate.getTypeUrl());
         if (KeyManagerRegistry.globalInstance().isNewKeyAllowed(protoTemplate.getTypeUrl())) {
            return manager.newKeyData(protoTemplate.getValue());
         } else {
            throw new GeneralSecurityException("newKey-operation not permitted for key type " + protoTemplate.getTypeUrl());
         }
      } catch (InvalidProtocolBufferException var4) {
         throw new GeneralSecurityException("Failed to parse serialized parameters", var4);
      }
   }

   public static KeyHandle createFromKey(TinkKey key, KeyAccess access) throws GeneralSecurityException {
      KeyHandle result = new KeyHandle(key);
      result.checkAccess(access);
      return result;
   }

   public static KeyHandle createFromKey(KeyData keyData, KeyTemplate.OutputPrefixType opt) {
      return new KeyHandle(new ProtoKey(keyData, opt));
   }

   private KeyHandle(TinkKey key) {
      this.key = key;
      this.status = KeyHandle.KeyStatusType.ENABLED;
      this.id = Util.randKeyId();
   }

   protected KeyHandle(TinkKey key, KeyHandle.KeyStatusType status, int keyId) {
      this.key = key;
      this.status = status;
      this.id = keyId;
   }

   public static KeyHandle generateNew(KeyTemplate keyTemplate) throws GeneralSecurityException {
      ProtoKey protoKey = new ProtoKey(newKeyData(keyTemplate), KeyTemplateProtoConverter.getOutputPrefixType(keyTemplate));
      return new KeyHandle(protoKey);
   }

   public boolean hasSecret() {
      return this.key.hasSecret();
   }

   public KeyHandle.KeyStatusType getStatus() {
      return this.status;
   }

   public int getId() {
      return this.id;
   }

   public TinkKey getKey(KeyAccess access) throws GeneralSecurityException {
      this.checkAccess(access);
      return this.key;
   }

   public KeyTemplate getKeyTemplate() {
      return this.key.getKeyTemplate();
   }

   private void checkAccess(KeyAccess access) throws GeneralSecurityException {
      if (this.hasSecret() && !access.canAccessSecret()) {
         throw new GeneralSecurityException("No access");
      }
   }

   public static enum KeyStatusType {
      ENABLED,
      DISABLED,
      DESTROYED;
   }
}
