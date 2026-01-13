package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class MlDsa extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_MlDsaParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_MlDsaParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_MlDsaParams_descriptor, new String[]{"MlDsaInstance"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_MlDsaKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_MlDsaKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_MlDsaKeyFormat_descriptor, new String[]{"Version", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_MlDsaPublicKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_MlDsaPublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_MlDsaPublicKey_descriptor, new String[]{"Version", "KeyValue", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_MlDsaPrivateKey_descriptor = getDescriptor().getMessageTypes().get(3);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_MlDsaPrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_MlDsaPrivateKey_descriptor, new String[]{"Version", "KeyValue", "PublicKey"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MlDsa() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", MlDsa.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0012proto/ml_dsa.proto\u0012\u0012google.crypto.tink\"I\n\u000bMlDsaParams\u0012:\n\u000fml_dsa_instance\u0018\u0001 \u0001(\u000e2!.google.crypto.tink.MlDsaInstance\"R\n\u000eMlDsaKeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012/\n\u0006params\u0018\u0002 \u0001(\u000b2\u001f.google.crypto.tink.MlDsaParams\"e\n\u000eMlDsaPublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\u0012/\n\u0006params\u0018\u0003 \u0001(\u000b2\u001f.google.crypto.tink.MlDsaParams\"m\n\u000fMlDsaPrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\u00126\n\npublic_key\u0018\u0003 \u0001(\u000b2\".google.crypto.tink.MlDsaPublicKey*J\n\rMlDsaInstance\u0012\u001b\n\u0017ML_DSA_UNKNOWN_INSTANCE\u0010\u0000\u0012\r\n\tML_DSA_65\u0010\u0001\u0012\r\n\tML_DSA_87\u0010\u0002BV\n\u001ccom.google.crypto.tink.protoP\u0001Z4github.com/tink-crypto/tink-go/v2/proto/ml_dsa_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
