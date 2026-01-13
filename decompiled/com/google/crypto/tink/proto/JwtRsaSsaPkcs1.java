package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class JwtRsaSsaPkcs1 extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_descriptor, new String[]{"Version", "Algorithm", "N", "E", "CustomKid"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_descriptor = internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_descriptor.getNestedTypes()
      .get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_descriptor, new String[]{"Value"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtRsaSsaPkcs1PrivateKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtRsaSsaPkcs1PrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtRsaSsaPkcs1PrivateKey_descriptor, new String[]{"Version", "PublicKey", "D", "P", "Q", "Dp", "Dq", "Crt"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_descriptor, new String[]{"Version", "Algorithm", "ModulusSizeInBits", "PublicExponent"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private JwtRsaSsaPkcs1() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPkcs1.class.getName());
      String[] descriptorData = new String[]{
         "\n\u001dproto/jwt_rsa_ssa_pkcs1.proto\u0012\u0012google.crypto.tink\"ç\u0001\n\u0017JwtRsaSsaPkcs1PublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012>\n\talgorithm\u0018\u0002 \u0001(\u000e2+.google.crypto.tink.JwtRsaSsaPkcs1Algorithm\u0012\t\n\u0001n\u0018\u0003 \u0001(\f\u0012\t\n\u0001e\u0018\u0004 \u0001(\f\u0012I\n\ncustom_kid\u0018\u0005 \u0001(\u000b25.google.crypto.tink.JwtRsaSsaPkcs1PublicKey.CustomKid\u001a\u001a\n\tCustomKid\u0012\r\n\u0005value\u0018\u0001 \u0001(\t\"²\u0001\n\u0018JwtRsaSsaPkcs1PrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012?\n\npublic_key\u0018\u0002 \u0001(\u000b2+.google.crypto.tink.JwtRsaSsaPkcs1PublicKey\u0012\t\n\u0001d\u0018\u0003 \u0001(\f\u0012\t\n\u0001p\u0018\u0004 \u0001(\f\u0012\t\n\u0001q\u0018\u0005 \u0001(\f\u0012\n\n\u0002dp\u0018\u0006 \u0001(\f\u0012\n\n\u0002dq\u0018\u0007 \u0001(\f\u0012\u000b\n\u0003crt\u0018\b \u0001(\f\"¡\u0001\n\u0017JwtRsaSsaPkcs1KeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012>\n\talgorithm\u0018\u0002 \u0001(\u000e2+.google.crypto.tink.JwtRsaSsaPkcs1Algorithm\u0012\u001c\n\u0014modulus_size_in_bits\u0018\u0003 \u0001(\r\u0012\u0017\n\u000fpublic_exponent\u0018\u0004 \u0001(\f*J\n\u0017JwtRsaSsaPkcs1Algorithm\u0012\u000e\n\nRS_UNKNOWN\u0010\u0000\u0012\t\n\u0005RS256\u0010\u0001\u0012\t\n\u0005RS384\u0010\u0002\u0012\t\n\u0005RS512\u0010\u0003B`\n\u001ccom.google.crypto.tink.protoP\u0001Z>github.com/tink-crypto/tink-go/v2/proto/rsa_ssa_pkcs1_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
