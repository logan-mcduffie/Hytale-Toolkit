package com.google.crypto.tink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class TinkJsonProtoKeysetFormat {
   public static KeysetHandle parseKeyset(String serializedKeyset, SecretKeyAccess access) throws GeneralSecurityException {
      if (access == null) {
         throw new NullPointerException("SecretKeyAccess cannot be null");
      } else {
         try {
            return CleartextKeysetHandle.read(JsonKeysetReader.withString(serializedKeyset));
         } catch (IOException var3) {
            throw new GeneralSecurityException("Parse keyset failed");
         }
      }
   }

   public static String serializeKeyset(KeysetHandle keysetHandle, SecretKeyAccess access) throws GeneralSecurityException {
      if (access == null) {
         throw new NullPointerException("SecretKeyAccess cannot be null");
      } else {
         try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withOutputStream(outputStream));
            return new String(outputStream.toByteArray(), com.google.crypto.tink.internal.Util.UTF_8);
         } catch (IOException var3) {
            throw new GeneralSecurityException("Serialize keyset failed");
         }
      }
   }

   public static KeysetHandle parseKeysetWithoutSecret(String serializedKeyset) throws GeneralSecurityException {
      try {
         return KeysetHandle.readNoSecret(JsonKeysetReader.withString(serializedKeyset));
      } catch (IOException var2) {
         throw new GeneralSecurityException("Parse keyset failed");
      }
   }

   public static String serializeKeysetWithoutSecret(KeysetHandle keysetHandle) throws GeneralSecurityException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         keysetHandle.writeNoSecret(JsonKeysetWriter.withOutputStream(outputStream));
         return new String(outputStream.toByteArray(), com.google.crypto.tink.internal.Util.UTF_8);
      } catch (IOException var2) {
         throw new GeneralSecurityException("Serialize keyset failed");
      }
   }

   public static KeysetHandle parseEncryptedKeyset(String serializedEncryptedKeyset, Aead keysetEncryptionAead, byte[] associatedData) throws GeneralSecurityException {
      try {
         return KeysetHandle.readWithAssociatedData(JsonKeysetReader.withString(serializedEncryptedKeyset), keysetEncryptionAead, associatedData);
      } catch (IOException var4) {
         throw new GeneralSecurityException("Parse keyset failed");
      }
   }

   public static String serializeEncryptedKeyset(KeysetHandle keysetHandle, Aead keysetEncryptionAead, byte[] associatedData) throws GeneralSecurityException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         keysetHandle.writeWithAssociatedData(JsonKeysetWriter.withOutputStream(outputStream), keysetEncryptionAead, associatedData);
         return new String(outputStream.toByteArray(), com.google.crypto.tink.internal.Util.UTF_8);
      } catch (IOException var4) {
         throw new GeneralSecurityException("Serialize keyset failed");
      }
   }

   private TinkJsonProtoKeysetFormat() {
   }
}
