package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class Config extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KeyTypeEntry_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KeyTypeEntry_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KeyTypeEntry_descriptor,
      new String[]{"PrimitiveName", "TypeUrl", "KeyManagerVersion", "NewKeyAllowed", "CatalogueName"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RegistryConfig_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RegistryConfig_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RegistryConfig_descriptor, new String[]{"ConfigName", "Entry"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private Config() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Config.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0012proto/config.proto\u0012\u0012google.crypto.tink\"\u008a\u0001\n\fKeyTypeEntry\u0012\u0016\n\u000eprimitive_name\u0018\u0001 \u0001(\t\u0012\u0010\n\btype_url\u0018\u0002 \u0001(\t\u0012\u001b\n\u0013key_manager_version\u0018\u0003 \u0001(\r\u0012\u0017\n\u000fnew_key_allowed\u0018\u0004 \u0001(\b\u0012\u0016\n\u000ecatalogue_name\u0018\u0005 \u0001(\t:\u0002\u0018\u0001\"Z\n\u000eRegistryConfig\u0012\u0013\n\u000bconfig_name\u0018\u0001 \u0001(\t\u0012/\n\u0005entry\u0018\u0002 \u0003(\u000b2 .google.crypto.tink.KeyTypeEntry:\u0002\u0018\u0001BY\n\u001ccom.google.crypto.tink.protoP\u0001Z7github.com/tink-crypto/tink-go/v2/proto/config_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
