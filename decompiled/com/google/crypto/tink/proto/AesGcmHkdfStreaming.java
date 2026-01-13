package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class AesGcmHkdfStreaming extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_descriptor, new String[]{"CiphertextSegmentSize", "DerivedKeySize", "HkdfHashType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmHkdfStreamingKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmHkdfStreamingKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesGcmHkdfStreamingKeyFormat_descriptor, new String[]{"Version", "Params", "KeySize"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesGcmHkdfStreamingKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesGcmHkdfStreamingKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesGcmHkdfStreamingKey_descriptor, new String[]{"Version", "Params", "KeyValue"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AesGcmHkdfStreaming() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesGcmHkdfStreaming.class.getName());
      String[] descriptorData = new String[]{
         "\n\"proto/aes_gcm_hkdf_streaming.proto\u0012\u0012google.crypto.tink\u001a\u0012proto/common.proto\"\u008c\u0001\n\u0019AesGcmHkdfStreamingParams\u0012\u001f\n\u0017ciphertext_segment_size\u0018\u0001 \u0001(\r\u0012\u0018\n\u0010derived_key_size\u0018\u0002 \u0001(\r\u00124\n\u000ehkdf_hash_type\u0018\u0003 \u0001(\u000e2\u001c.google.crypto.tink.HashType\"\u0080\u0001\n\u001cAesGcmHkdfStreamingKeyFormat\u0012\u000f\n\u0007version\u0018\u0003 \u0001(\r\u0012=\n\u0006params\u0018\u0001 \u0001(\u000b2-.google.crypto.tink.AesGcmHkdfStreamingParams\u0012\u0010\n\bkey_size\u0018\u0002 \u0001(\r\"{\n\u0016AesGcmHkdfStreamingKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012=\n\u0006params\u0018\u0002 \u0001(\u000b2-.google.crypto.tink.AesGcmHkdfStreamingParams\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fBi\n\u001ccom.google.crypto.tink.protoP\u0001ZGgithub.com/tink-crypto/tink-go/v2/proto/aes_gcm_hkdf_streaming_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Common.getDescriptor()});
      descriptor.resolveAllFeaturesImmutable();
      Common.getDescriptor();
   }
}
