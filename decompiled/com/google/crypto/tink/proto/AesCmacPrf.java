package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class AesCmacPrf extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCmacPrfKey_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCmacPrfKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesCmacPrfKey_descriptor, new String[]{"Version", "KeyValue"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesCmacPrfKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesCmacPrfKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesCmacPrfKeyFormat_descriptor, new String[]{"Version", "KeySize"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AesCmacPrf() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCmacPrf.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0018proto/aes_cmac_prf.proto\u0012\u0012google.crypto.tink\"3\n\rAesCmacPrfKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\"8\n\u0013AesCmacPrfKeyFormat\u0012\u000f\n\u0007version\u0018\u0002 \u0001(\r\u0012\u0010\n\bkey_size\u0018\u0001 \u0001(\rB_\n\u001ccom.google.crypto.tink.protoP\u0001Z=github.com/tink-crypto/tink-go/v2/proto/aes_cmac_prf_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
