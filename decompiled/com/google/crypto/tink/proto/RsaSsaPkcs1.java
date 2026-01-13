package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class RsaSsaPkcs1 extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPkcs1Params_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPkcs1Params_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPkcs1Params_descriptor, new String[]{"HashType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPkcs1PublicKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPkcs1PublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPkcs1PublicKey_descriptor, new String[]{"Version", "Params", "N", "E"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPkcs1PrivateKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPkcs1PrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPkcs1PrivateKey_descriptor, new String[]{"Version", "PublicKey", "D", "P", "Q", "Dp", "Dq", "Crt"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_descriptor = getDescriptor().getMessageTypes().get(3);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_descriptor, new String[]{"Params", "ModulusSizeInBits", "PublicExponent"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private RsaSsaPkcs1() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPkcs1.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0019proto/rsa_ssa_pkcs1.proto\u0012\u0012google.crypto.tink\u001a\u0012proto/common.proto\"D\n\u0011RsaSsaPkcs1Params\u0012/\n\thash_type\u0018\u0001 \u0001(\u000e2\u001c.google.crypto.tink.HashType\"t\n\u0014RsaSsaPkcs1PublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00125\n\u0006params\u0018\u0002 \u0001(\u000b2%.google.crypto.tink.RsaSsaPkcs1Params\u0012\t\n\u0001n\u0018\u0003 \u0001(\f\u0012\t\n\u0001e\u0018\u0004 \u0001(\f\"¬\u0001\n\u0015RsaSsaPkcs1PrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012<\n\npublic_key\u0018\u0002 \u0001(\u000b2(.google.crypto.tink.RsaSsaPkcs1PublicKey\u0012\t\n\u0001d\u0018\u0003 \u0001(\f\u0012\t\n\u0001p\u0018\u0004 \u0001(\f\u0012\t\n\u0001q\u0018\u0005 \u0001(\f\u0012\n\n\u0002dp\u0018\u0006 \u0001(\f\u0012\n\n\u0002dq\u0018\u0007 \u0001(\f\u0012\u000b\n\u0003crt\u0018\b \u0001(\f\"\u0084\u0001\n\u0014RsaSsaPkcs1KeyFormat\u00125\n\u0006params\u0018\u0001 \u0001(\u000b2%.google.crypto.tink.RsaSsaPkcs1Params\u0012\u001c\n\u0014modulus_size_in_bits\u0018\u0002 \u0001(\r\u0012\u0017\n\u000fpublic_exponent\u0018\u0003 \u0001(\fB`\n\u001ccom.google.crypto.tink.protoP\u0001Z>github.com/tink-crypto/tink-go/v2/proto/rsa_ssa_pkcs1_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Common.getDescriptor()});
      descriptor.resolveAllFeaturesImmutable();
      Common.getDescriptor();
   }
}
