package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class AesCmac extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCmacParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCmacParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesCmacParams_descriptor, new String[]{"TagSize"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCmacKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCmacKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesCmacKey_descriptor, new String[]{"Version", "KeyValue", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCmacKeyFormat_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCmacKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesCmacKeyFormat_descriptor, new String[]{"KeySize", "Params"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AesCmac() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCmac.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0014proto/aes_cmac.proto\u0012\u0012google.crypto.tink\"!\n\rAesCmacParams\u0012\u0010\n\btag_size\u0018\u0001 \u0001(\r\"c\n\nAesCmacKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\u00121\n\u0006params\u0018\u0003 \u0001(\u000b2!.google.crypto.tink.AesCmacParams\"W\n\u0010AesCmacKeyFormat\u0012\u0010\n\bkey_size\u0018\u0001 \u0001(\r\u00121\n\u0006params\u0018\u0002 \u0001(\u000b2!.google.crypto.tink.AesCmacParamsB[\n\u001ccom.google.crypto.tink.protoP\u0001Z9github.com/tink-crypto/tink-go/v2/proto/aes_cmac_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
