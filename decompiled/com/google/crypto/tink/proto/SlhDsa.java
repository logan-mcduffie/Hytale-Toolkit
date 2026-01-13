package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class SlhDsa extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_SlhDsaParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_SlhDsaParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_SlhDsaParams_descriptor, new String[]{"KeySize", "HashType", "SigType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_SlhDsaKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_SlhDsaKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_SlhDsaKeyFormat_descriptor, new String[]{"Version", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_SlhDsaPublicKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_SlhDsaPublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_SlhDsaPublicKey_descriptor, new String[]{"Version", "KeyValue", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor = getDescriptor().getMessageTypes().get(3);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_SlhDsaPrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor, new String[]{"Version", "KeyValue", "PublicKey"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private SlhDsa() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsa.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0013proto/slh_dsa.proto\u0012\u0012google.crypto.tink\"\u0092\u0001\n\fSlhDsaParams\u0012\u0010\n\bkey_size\u0018\u0001 \u0001(\u0005\u00125\n\thash_type\u0018\u0002 \u0001(\u000e2\".google.crypto.tink.SlhDsaHashType\u00129\n\bsig_type\u0018\u0003 \u0001(\u000e2'.google.crypto.tink.SlhDsaSignatureType\"T\n\u000fSlhDsaKeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00120\n\u0006params\u0018\u0002 \u0001(\u000b2 .google.crypto.tink.SlhDsaParams\"g\n\u000fSlhDsaPublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\u00120\n\u0006params\u0018\u0003 \u0001(\u000b2 .google.crypto.tink.SlhDsaParams\"o\n\u0010SlhDsaPrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\u00127\n\npublic_key\u0018\u0003 \u0001(\u000b2#.google.crypto.tink.SlhDsaPublicKey*H\n\u000eSlhDsaHashType\u0012!\n\u001dSLH_DSA_HASH_TYPE_UNSPECIFIED\u0010\u0000\u0012\b\n\u0004SHA2\u0010\u0001\u0012\t\n\u0005SHAKE\u0010\u0002*d\n\u0013SlhDsaSignatureType\u0012&\n\"SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED\u0010\u0000\u0012\u0010\n\fFAST_SIGNING\u0010\u0001\u0012\u0013\n\u000fSMALL_SIGNATURE\u0010\u0002BW\n\u001ccom.google.crypto.tink.protoP\u0001Z5github.com/tink-crypto/tink-go/v2/proto/slh_dsa_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
