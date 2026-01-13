package com.google.crypto.tink;

import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.proto.OutputPrefixType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

final class Util {
   public static final Charset UTF_8 = Charset.forName("UTF-8");

   public static KeysetInfo getKeysetInfo(Keyset keyset) {
      KeysetInfo.Builder info = KeysetInfo.newBuilder().setPrimaryKeyId(keyset.getPrimaryKeyId());

      for (Keyset.Key key : keyset.getKeyList()) {
         info.addKeyInfo(getKeyInfo(key));
      }

      return info.build();
   }

   public static KeysetInfo.KeyInfo getKeyInfo(Keyset.Key key) {
      return KeysetInfo.KeyInfo.newBuilder()
         .setTypeUrl(key.getKeyData().getTypeUrl())
         .setStatus(key.getStatus())
         .setOutputPrefixType(key.getOutputPrefixType())
         .setKeyId(key.getKeyId())
         .build();
   }

   public static void validateKey(Keyset.Key key) throws GeneralSecurityException {
      if (!key.hasKeyData()) {
         throw new GeneralSecurityException(String.format("key %d has no key data", key.getKeyId()));
      } else if (key.getOutputPrefixType() == OutputPrefixType.UNKNOWN_PREFIX) {
         throw new GeneralSecurityException(String.format("key %d has unknown prefix", key.getKeyId()));
      } else if (key.getStatus() == KeyStatusType.UNKNOWN_STATUS) {
         throw new GeneralSecurityException(String.format("key %d has unknown status", key.getKeyId()));
      }
   }

   public static void validateKeyset(Keyset keyset) throws GeneralSecurityException {
      int primaryKeyId = keyset.getPrimaryKeyId();
      boolean hasPrimaryKey = false;
      boolean containsOnlyPublicKeyMaterial = true;
      int numEnabledKeys = 0;

      for (Keyset.Key key : keyset.getKeyList()) {
         if (key.getStatus() == KeyStatusType.ENABLED) {
            validateKey(key);
            if (key.getKeyId() == primaryKeyId) {
               if (hasPrimaryKey) {
                  throw new GeneralSecurityException("keyset contains multiple primary keys");
               }

               hasPrimaryKey = true;
            }

            if (key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC) {
               containsOnlyPublicKeyMaterial = false;
            }

            numEnabledKeys++;
         }
      }

      if (numEnabledKeys == 0) {
         throw new GeneralSecurityException("keyset must contain at least one ENABLED key");
      } else if (!hasPrimaryKey && !containsOnlyPublicKeyMaterial) {
         throw new GeneralSecurityException("keyset doesn't contain a valid primary key");
      }
   }

   public static byte[] readAll(InputStream inputStream) throws IOException {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];

      int count;
      while ((count = inputStream.read(buf)) != -1) {
         result.write(buf, 0, count);
      }

      return result.toByteArray();
   }

   private Util() {
   }
}
