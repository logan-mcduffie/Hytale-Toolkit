package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class AesGcmSiv extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmSivKeyFormat_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmSivKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesGcmSivKeyFormat_descriptor, new String[]{"KeySize", "Version"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmSivKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmSivKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesGcmSivKey_descriptor, new String[]{"Version", "KeyValue"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AesGcmSiv() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesGcmSiv.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0017proto/aes_gcm_siv.proto\u0012\u0012google.crypto.tink\"7\n\u0012AesGcmSivKeyFormat\u0012\u0010\n\bkey_size\u0018\u0002 \u0001(\r\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\"2\n\fAesGcmSivKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fB^\n\u001ccom.google.crypto.tink.protoP\u0001Z<github.com/tink-crypto/tink-go/v2/proto/aes_gcm_siv_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
