package com.google.crypto.tink;

import com.google.crypto.tink.internal.MutablePrimitiveRegistry;
import com.google.crypto.tink.proto.KeyTypeEntry;
import com.google.crypto.tink.proto.RegistryConfig;
import java.security.GeneralSecurityException;

public final class Config {
   public static KeyTypeEntry getTinkKeyTypeEntry(String catalogueName, String primitiveName, String keyProtoName, int keyManagerVersion, boolean newKeyAllowed) {
      return KeyTypeEntry.newBuilder()
         .setPrimitiveName(primitiveName)
         .setTypeUrl("type.googleapis.com/google.crypto.tink." + keyProtoName)
         .setKeyManagerVersion(keyManagerVersion)
         .setNewKeyAllowed(newKeyAllowed)
         .setCatalogueName(catalogueName)
         .build();
   }

   public static void register(RegistryConfig config) throws GeneralSecurityException {
      for (KeyTypeEntry entry : config.getEntryList()) {
         registerKeyType(entry);
      }
   }

   public static void registerKeyType(KeyTypeEntry entry) throws GeneralSecurityException {
      validate(entry);
      if (!entry.getCatalogueName().equals("TinkAead")
         && !entry.getCatalogueName().equals("TinkMac")
         && !entry.getCatalogueName().equals("TinkHybridDecrypt")
         && !entry.getCatalogueName().equals("TinkHybridEncrypt")
         && !entry.getCatalogueName().equals("TinkPublicKeySign")
         && !entry.getCatalogueName().equals("TinkPublicKeyVerify")
         && !entry.getCatalogueName().equals("TinkStreamingAead")
         && !entry.getCatalogueName().equals("TinkDeterministicAead")) {
         Catalogue<?> catalogue = Registry.getCatalogue(entry.getCatalogueName());
         MutablePrimitiveRegistry.globalInstance().registerPrimitiveWrapper(catalogue.getPrimitiveWrapper());
         KeyManager<?> keyManager = catalogue.getKeyManager(entry.getTypeUrl(), entry.getPrimitiveName(), entry.getKeyManagerVersion());
         Registry.registerKeyManager(keyManager, entry.getNewKeyAllowed());
      }
   }

   private static void validate(KeyTypeEntry entry) throws GeneralSecurityException {
      if (entry.getTypeUrl().isEmpty()) {
         throw new GeneralSecurityException("Missing type_url.");
      } else if (entry.getPrimitiveName().isEmpty()) {
         throw new GeneralSecurityException("Missing primitive_name.");
      } else if (entry.getCatalogueName().isEmpty()) {
         throw new GeneralSecurityException("Missing catalogue_name.");
      }
   }

   private Config() {
   }
}
