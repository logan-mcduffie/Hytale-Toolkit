package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class KmsAead extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KmsAeadKeyFormat_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KmsAeadKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KmsAeadKeyFormat_descriptor, new String[]{"KeyUri"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KmsAeadKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KmsAeadKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KmsAeadKey_descriptor, new String[]{"Version", "Params"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private KmsAead() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KmsAead.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0014proto/kms_aead.proto\u0012\u0012google.crypto.tink\"#\n\u0010KmsAeadKeyFormat\u0012\u000f\n\u0007key_uri\u0018\u0001 \u0001(\t\"S\n\nKmsAeadKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00124\n\u0006params\u0018\u0002 \u0001(\u000b2$.google.crypto.tink.KmsAeadKeyFormatB[\n\u001ccom.google.crypto.tink.protoP\u0001Z9github.com/tink-crypto/tink-go/v2/proto/kms_aead_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
