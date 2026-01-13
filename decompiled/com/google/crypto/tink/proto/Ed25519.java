package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class Ed25519 extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_Ed25519KeyFormat_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_Ed25519KeyFormat_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_Ed25519KeyFormat_descriptor, new String[]{"Version"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_Ed25519PublicKey_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_Ed25519PublicKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_Ed25519PublicKey_descriptor, new String[]{"Version", "KeyValue"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_Ed25519PrivateKey_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_Ed25519PrivateKey_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_Ed25519PrivateKey_descriptor, new String[]{"Version", "KeyValue", "PublicKey"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private Ed25519() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Ed25519.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0013proto/ed25519.proto\u0012\u0012google.crypto.tink\"#\n\u0010Ed25519KeyFormat\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\"6\n\u0010Ed25519PublicKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\"q\n\u0011Ed25519PrivateKey\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u0012\u0011\n\tkey_value\u0018\u0002 \u0001(\f\u00128\n\npublic_key\u0018\u0003 \u0001(\u000b2$.google.crypto.tink.Ed25519PublicKeyBZ\n\u001ccom.google.crypto.tink.protoP\u0001Z8github.com/tink-crypto/tink-go/v2/proto/ed25519_go_protob\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
