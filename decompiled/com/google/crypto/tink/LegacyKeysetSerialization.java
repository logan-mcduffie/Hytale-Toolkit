package com.google.crypto.tink;

import com.google.crypto.tink.proto.KeysetInfo;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class LegacyKeysetSerialization {
   public static KeysetHandle parseKeysetWithoutSecret(KeysetReader reader) throws GeneralSecurityException, IOException {
      return KeysetHandle.readNoSecret(reader);
   }

   public static KeysetHandle parseKeyset(KeysetReader reader, SecretKeyAccess access) throws GeneralSecurityException, IOException {
      if (access == null) {
         throw new NullPointerException("SecretKeyAccess cannot be null");
      } else {
         return CleartextKeysetHandle.read(reader);
      }
   }

   public static KeysetHandle parseEncryptedKeyset(KeysetReader reader, Aead aead, byte[] associatedData) throws GeneralSecurityException, IOException {
      return KeysetHandle.readWithAssociatedData(reader, aead, associatedData);
   }

   public static void serializeKeysetWithoutSecret(KeysetHandle keysetHandle, KeysetWriter writer) throws GeneralSecurityException, IOException {
      keysetHandle.writeNoSecret(writer);
   }

   public static void serializeKeyset(KeysetHandle keysetHandle, KeysetWriter writer, SecretKeyAccess access) throws IOException {
      if (access == null) {
         throw new NullPointerException("SecretKeyAccess cannot be null");
      } else {
         CleartextKeysetHandle.write(keysetHandle, writer);
      }
   }

   public static void serializeEncryptedKeyset(KeysetHandle keysetHandle, KeysetWriter writer, Aead aead, byte[] associatedData) throws GeneralSecurityException, IOException {
      keysetHandle.writeWithAssociatedData(writer, aead, associatedData);
   }

   public static KeysetInfo getKeysetInfo(KeysetHandle handle) {
      return handle.getKeysetInfo();
   }

   private LegacyKeysetSerialization() {
   }
}
