package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class XAesGcm extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_XAesGcmParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_XAesGcmParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_XAesGcmParams_descriptor, new String[]{"SaltSize"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_XAesGcmKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_XAesGcmKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_XAesGcmKeyFormat_descriptor, new String[]{"Version", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_XAesGcmKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_XAesGcmKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_XAesGcmKey_descriptor, new String[]{"Version", "Params", "KeyValue"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private XAesGcm() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", XAesGcm.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0015proto/x_aes_gcm.proto\u0012\u0012google.crypto.tink\"\"\n\rXAesGcmParams\u0012\u0011\n\tsalt_size\u0018\u0001 \u0001(\r\"\\\n\u0010XAesGcmKeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00121\n\u0006params\u0018\u0003 \u0001(\u000b2!.google.crypto.tink.XAesGcmParamsJ\u0004\b\u0002\u0010\u0003\"c\n\nXAesGcmKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00121\n\u0006params\u0018\u0002 \u0001(\u000b2!.google.crypto.tink.XAesGcmParams\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fB\\\n\u001ccom.google.crypto.tink.protoP\u0001Z:github.com/tink-crypto/tink-go/v2/proto/x_aes_gcm_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
