package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class AesEax extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesEaxParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesEaxParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesEaxParams_descriptor, new String[]{"IvSize"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesEaxKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesEaxKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesEaxKeyFormat_descriptor, new String[]{"Params", "KeySize"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_AesEaxKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_AesEaxKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_AesEaxKey_descriptor, new String[]{"Version", "Params", "KeyValue"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AesEax() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesEax.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0013proto/aes_eax.proto\u0012\u0012google.crypto.tink\"\u001f\n\fAesEaxParams\u0012\u000f\n\u0007iv_size\u0018\u0001 \u0001(\r\"U\n\u000fAesEaxKeyFormat\u00120\n\u0006params\u0018\u0001 \u0001(\u000b2 .google.crypto.tink.AesEaxParams\u0012\u0010\n\bkey_size\u0018\u0002 \u0001(\r\"a\n\tAesEaxKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00120\n\u0006params\u0018\u0002 \u0001(\u000b2 .google.crypto.tink.AesEaxParams\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\fBZ\n\u001ccom.google.crypto.tink.protoP\u0001Z8github.com/tink-crypto/tink-go/v2/proto/aes_eax_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
