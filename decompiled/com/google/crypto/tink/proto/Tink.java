package com.google.crypto.tink.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.RuntimeVersion;

public final class Tink extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KeyTemplate_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KeyTemplate_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KeyTemplate_descriptor, new String[]{"TypeUrl", "Value", "OutputPrefixType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KeyData_descriptor = getDescriptor().getMessageTypes().get(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KeyData_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KeyData_descriptor, new String[]{"TypeUrl", "Value", "KeyMaterialType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_Keyset_descriptor = getDescriptor().getMessageTypes().get(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_Keyset_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_Keyset_descriptor, new String[]{"PrimaryKeyId", "Key"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_Keyset_Key_descriptor = internal_static_google_crypto_tink_Keyset_descriptor.getNestedTypes()
      .get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_Keyset_Key_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_Keyset_Key_descriptor, new String[]{"KeyData", "Status", "KeyId", "OutputPrefixType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KeysetInfo_descriptor = getDescriptor().getMessageTypes().get(3);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KeysetInfo_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KeysetInfo_descriptor, new String[]{"PrimaryKeyId", "KeyInfo"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor = internal_static_google_crypto_tink_KeysetInfo_descriptor.getNestedTypes()
      .get(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_KeysetInfo_KeyInfo_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor, new String[]{"TypeUrl", "Status", "KeyId", "OutputPrefixType"}
   );
   static final Descriptors.Descriptor internal_static_google_crypto_tink_EncryptedKeyset_descriptor = getDescriptor().getMessageTypes().get(4);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_crypto_tink_EncryptedKeyset_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_crypto_tink_EncryptedKeyset_descriptor, new String[]{"EncryptedKeyset", "KeysetInfo"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private Tink() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Tink.class.getName());
      String[] descriptorData = new String[]{
         "\n\u0010proto/tink.proto\u0012\u0012google.crypto.tink\"p\n\u000bKeyTemplate\u0012\u0010\n\btype_url\u0018\u0001 \u0001(\t\u0012\r\n\u0005value\u0018\u0002 \u0001(\f\u0012@\n\u0012output_prefix_type\u0018\u0003 \u0001(\u000e2$.google.crypto.tink.OutputPrefixType\"è\u0001\n\u0007KeyData\u0012\u0010\n\btype_url\u0018\u0001 \u0001(\t\u0012\r\n\u0005value\u0018\u0002 \u0001(\f\u0012F\n\u0011key_material_type\u0018\u0003 \u0001(\u000e2+.google.crypto.tink.KeyData.KeyMaterialType\"t\n\u000fKeyMaterialType\u0012\u0017\n\u0013UNKNOWN_KEYMATERIAL\u0010\u0000\u0012\r\n\tSYMMETRIC\u0010\u0001\u0012\u0016\n\u0012ASYMMETRIC_PRIVATE\u0010\u0002\u0012\u0015\n\u0011ASYMMETRIC_PUBLIC\u0010\u0003\u0012\n\n\u0006REMOTE\u0010\u0004\"\u0089\u0002\n\u0006Keyset\u0012\u0016\n\u000eprimary_key_id\u0018\u0001 \u0001(\r\u0012+\n\u0003key\u0018\u0002 \u0003(\u000b2\u001e.google.crypto.tink.Keyset.Key\u001a¹\u0001\n\u0003Key\u0012-\n\bkey_data\u0018\u0001 \u0001(\u000b2\u001b.google.crypto.tink.KeyData\u00121\n\u0006status\u0018\u0002 \u0001(\u000e2!.google.crypto.tink.KeyStatusType\u0012\u000e\n\u0006key_id\u0018\u0003 \u0001(\r\u0012@\n\u0012output_prefix_type\u0018\u0004 \u0001(\u000e2$.google.crypto.tink.OutputPrefixType\"\u0081\u0002\n\nKeysetInfo\u0012\u0016\n\u000eprimary_key_id\u0018\u0001 \u0001(\r\u00128\n\bkey_info\u0018\u0002 \u0003(\u000b2&.google.crypto.tink.KeysetInfo.KeyInfo\u001a \u0001\n\u0007KeyInfo\u0012\u0010\n\btype_url\u0018\u0001 \u0001(\t\u00121\n\u0006status\u0018\u0002 \u0001(\u000e2!.google.crypto.tink.KeyStatusType\u0012\u000e\n\u0006key_id\u0018\u0003 \u0001(\r\u0012@\n\u0012output_prefix_type\u0018\u0004 \u0001(\u000e2$.google.crypto.tink.OutputPrefixType\"`\n\u000fEncryptedKeyset\u0012\u0018\n\u0010encrypted_keyset\u0018\u0002 \u0001(\f\u00123\n\u000bkeyset_info\u0018\u0003 \u0001(\u000b2\u001e.google.crypto.tink.KeysetInfo*M\n\rKeyStatusType\u0012\u0012\n\u000eUNKNOWN_STATUS\u0010\u0000\u0012\u000b\n\u0007ENABLED\u0010\u0001\u0012\f\n\bDISABLED\u0010\u0002\u0012\r\n\tDESTROYED\u0010\u0003*k\n\u0010OutputPrefixType\u0012\u0012\n\u000eUNKNOWN_PREFIX\u0010\u0000\u0012\b\n\u0004TINK\u0010\u0001\u0012\n\n\u0006LEGACY\u0010\u0002\u0012\u0007\n\u0003RAW\u0010\u0003\u0012\u000b\n\u0007CRUNCHY\u0010\u0004\u0012\u0017\n\u0013WITH_ID_REQUIREMENT\u0010\u0005B`\n\u001ccom.google.crypto.tink.protoP\u0001Z5github.com/tink-crypto/tink-go/v2/proto/tink_go_proto¢\u0002\u0006TINKPBb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
