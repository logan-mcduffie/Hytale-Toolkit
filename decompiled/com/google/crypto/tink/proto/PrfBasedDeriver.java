package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class PrfBasedDeriver extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_PrfBasedDeriverParams_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_PrfBasedDeriverParams_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_PrfBasedDeriverParams_descriptor, new String[]{"DerivedKeyTemplate"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_PrfBasedDeriverKeyFormat_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_PrfBasedDeriverKeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_PrfBasedDeriverKeyFormat_descriptor, new String[]{"PrfKeyTemplate", "Params"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_PrfBasedDeriverKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_PrfBasedDeriverKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_PrfBasedDeriverKey_descriptor, new String[]{"Version", "PrfKey", "Params"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private PrfBasedDeriver() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", PrfBasedDeriver.class.getName());
      String[] descriptorData = new String[]{
         "\n\u001dproto/prf_based_deriver.proto\u0012\u0012google.crypto.tink\u001a\u0010proto/tink.proto\"V\n\u0015PrfBasedDeriverParams\u0012=\n\u0014derived_key_template\u0018\u0001 \u0001(\u000b2\u001f.google.crypto.tink.KeyTemplate\"\u0090\u0001\n\u0018PrfBasedDeriverKeyFormat\u00129\n\u0010prf_key_template\u0018\u0001 \u0001(\u000b2\u001f.google.crypto.tink.KeyTemplate\u00129\n\u0006params\u0018\u0002 \u0001(\u000b2).google.crypto.tink.PrfBasedDeriverParams\"\u008e\u0001\n\u0012PrfBasedDeriverKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012,\n\u0007prf_key\u0018\u0002 \u0001(\u000b2\u001b.google.crypto.tink.KeyData\u00129\n\u0006params\u0018\u0003 \u0001(\u000b2).google.crypto.tink.PrfBasedDeriverParamsBd\n\u001ccom.google.crypto.tink.protoP\u0001ZBgithub.com/tink-crypto/tink-go/v2/proto/prf_based_deriver_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Tink.getDescriptor()});
      descriptor.resolveAllFeaturesImmutable();
      Tink.getDescriptor();
   }
}
