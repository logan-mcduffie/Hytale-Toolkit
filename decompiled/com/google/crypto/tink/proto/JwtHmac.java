package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class JwtHmac extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtHmacKey_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtHmacKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtHmacKey_descriptor, new String[]{"Version", "Algorithm", "KeyValue", "CustomKid"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtHmacKey_CustomKid_descriptor = internal_static_google_crypto_tink_JwtHmacKey_descriptor.getNestedTypes()
      .get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtHmacKey_CustomKid_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtHmacKey_CustomKid_descriptor, new String[]{"Value"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtHmacKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtHmacKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtHmacKeyFormat_descriptor, new String[]{"Version", "Algorithm", "KeySize"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private JwtHmac() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtHmac.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0014proto/jwt_hmac.proto\u0012\u0012google.crypto.tink\"Ã\u0001\n\nJwtHmacKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00127\n\talgorithm\u0018\u0002 \u0001(\u000e2$.google.crypto.tink.JwtHmacAlgorithm\u0012\u0011\n\tkey_value\u0018\u0003 \u0001(\f\u0012<\n\ncustom_kid\u0018\u0004 \u0001(\u000b2(.google.crypto.tink.JwtHmacKey.CustomKid\u001a\u001a\n\tCustomKid\u0012\r\n\u0005value\u0018\u0001 \u0001(\t\"n\n\u0010JwtHmacKeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00127\n\talgorithm\u0018\u0002 \u0001(\u000e2$.google.crypto.tink.JwtHmacAlgorithm\u0012\u0010\n\bkey_size\u0018\u0003 \u0001(\r*C\n\u0010JwtHmacAlgorithm\u0012\u000e\n\nHS_UNKNOWN\u0010\u0000\u0012\t\n\u0005HS256\u0010\u0001\u0012\t\n\u0005HS384\u0010\u0002\u0012\t\n\u0005HS512\u0010\u0003B[\n\u001ccom.google.crypto.tink.protoP\u0001Z9github.com/tink-crypto/tink-go/v2/proto/jwt_hmac_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
