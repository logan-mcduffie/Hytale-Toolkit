package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class RsaSsaPss extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPssParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPssParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPssParams_descriptor, new String[]{"SigHash", "Mgf1Hash", "SaltLength"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPssPublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor, new String[]{"Version", "Params", "N", "E"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPssPrivateKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPssPrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPssPrivateKey_descriptor, new String[]{"Version", "PublicKey", "D", "P", "Q", "Dp", "Dq", "Crt"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_RsaSsaPssKeyFormat_descriptor = getDescriptor().getMessageTypes().get(3);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_RsaSsaPssKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_RsaSsaPssKeyFormat_descriptor, new String[]{"Params", "ModulusSizeInBits", "PublicExponent"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private RsaSsaPss() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPss.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0017proto/rsa_ssa_pss.proto\u0012\u0012google.crypto.tink\u001a\u0012proto/common.proto\"\u0087\u0001\n\u000fRsaSsaPssParams\u0012.\n\bsig_hash\u0018\u0001 \u0001(\u000e2\u001c.google.crypto.tink.HashType\u0012/\n\tmgf1_hash\u0018\u0002 \u0001(\u000e2\u001c.google.crypto.tink.HashType\u0012\u0013\n\u000bsalt_length\u0018\u0003 \u0001(\u0005\"p\n\u0012RsaSsaPssPublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00123\n\u0006params\u0018\u0002 \u0001(\u000b2#.google.crypto.tink.RsaSsaPssParams\u0012\t\n\u0001n\u0018\u0003 \u0001(\f\u0012\t\n\u0001e\u0018\u0004 \u0001(\f\"¨\u0001\n\u0013RsaSsaPssPrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012:\n\npublic_key\u0018\u0002 \u0001(\u000b2&.google.crypto.tink.RsaSsaPssPublicKey\u0012\t\n\u0001d\u0018\u0003 \u0001(\f\u0012\t\n\u0001p\u0018\u0004 \u0001(\f\u0012\t\n\u0001q\u0018\u0005 \u0001(\f\u0012\n\n\u0002dp\u0018\u0006 \u0001(\f\u0012\n\n\u0002dq\u0018\u0007 \u0001(\f\u0012\u000b\n\u0003crt\u0018\b \u0001(\f\"\u0080\u0001\n\u0012RsaSsaPssKeyFormat\u00123\n\u0006params\u0018\u0001 \u0001(\u000b2#.google.crypto.tink.RsaSsaPssParams\u0012\u001c\n\u0014modulus_size_in_bits\u0018\u0002 \u0001(\r\u0012\u0017\n\u000fpublic_exponent\u0018\u0003 \u0001(\fB^\n\u001ccom.google.crypto.tink.protoP\u0001Z<github.com/tink-crypto/tink-go/v2/proto/rsa_ssa_pss_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Common.getDescriptor()});
      descriptor.resolveAllFeaturesImmutable();
      Common.getDescriptor();
   }
}
