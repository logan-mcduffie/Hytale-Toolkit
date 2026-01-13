package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class HkdfPrf extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_HkdfPrfParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_HkdfPrfParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_HkdfPrfParams_descriptor, new String[]{"Hash", "Salt"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_HkdfPrfKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_HkdfPrfKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_HkdfPrfKey_descriptor, new String[]{"Version", "Params", "KeyValue"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_HkdfPrfKeyFormat_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_HkdfPrfKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_HkdfPrfKeyFormat_descriptor, new String[]{"Params", "KeySize", "Version"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private HkdfPrf() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HkdfPrf.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0014proto/hkdf_prf.proto\u0012\u0012google.crypto.tink\u001a\u0012proto/common.proto\"I\n\rHkdfPrfParams\u0012*\n\u0004hash\u0018\u0001 \u0001(\u000e2\u001c.google.crypto.tink.HashType\u0012\f\n\u0004salt\u0018\u0002 \u0001(\f\"c\n\nHkdfPrfKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00121\n\u0006params\u0018\u0002 \u0001(\u000b2!.google.crypto.tink.HkdfPrfParams\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\f\"h\n\u0010HkdfPrfKeyFormat\u00121\n\u0006params\u0018\u0001 \u0001(\u000b2!.google.crypto.tink.HkdfPrfParams\u0012\u0010\n\bkey_size\u0018\u0002 \u0001(\r\u0012\u000f\n\u0007version\u0018\u0003 \u0001(\rBX\n\u001ccom.google.crypto.tink.protoP\u0001Z6github.com/tink-crypto/tink-go/v2/proto/hkdf_prf_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Common.getDescriptor()});
      descriptor.resolveAllFeaturesImmutable();
      Common.getDescriptor();
   }
}
