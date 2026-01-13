package com.google.protobuf;

public final class StructProto extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_protobuf_Struct_descriptor = getDescriptor().getMessageType(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Struct_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Struct_descriptor, new String[]{"Fields"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Struct_FieldsEntry_descriptor = internal_static_google_protobuf_Struct_descriptor.getNestedType(
      0
   );
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Struct_FieldsEntry_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Struct_FieldsEntry_descriptor, new String[]{"Key", "Value"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Value_descriptor = getDescriptor().getMessageType(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Value_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Value_descriptor,
      new String[]{"NullValue", "NumberValue", "StringValue", "BoolValue", "StructValue", "ListValue", "Kind"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_ListValue_descriptor = getDescriptor().getMessageType(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_ListValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_ListValue_descriptor, new String[]{"Values"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private StructProto() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "StructProto");
      String[] descriptorData = new String[]{
         "\n\u001cgoogle/protobuf/struct.proto\u0012\u000fgoogle.protobuf\"\u0098\u0001\n\u0006Struct\u0012;\n\u0006fields\u0018\u0001 \u0003(\u000b2#.google.protobuf.Struct.FieldsEntryR\u0006fields\u001aQ\n\u000bFieldsEntry\u0012\u0010\n\u0003key\u0018\u0001 \u0001(\tR\u0003key\u0012,\n\u0005value\u0018\u0002 \u0001(\u000b2\u0016.google.protobuf.ValueR\u0005value:\u00028\u0001\"²\u0002\n\u0005Value\u0012;\n\nnull_value\u0018\u0001 \u0001(\u000e2\u001a.google.protobuf.NullValueH\u0000R\tnullValue\u0012#\n\fnumber_value\u0018\u0002 \u0001(\u0001H\u0000R\u000bnumberValue\u0012#\n\fstring_value\u0018\u0003 \u0001(\tH\u0000R\u000bstringValue\u0012\u001f\n\nbool_value\u0018\u0004 \u0001(\bH\u0000R\tboolValue\u0012<\n\fstruct_value\u0018\u0005 \u0001(\u000b2\u0017.google.protobuf.StructH\u0000R\u000bstructValue\u0012;\n\nlist_value\u0018\u0006 \u0001(\u000b2\u001a.google.protobuf.ListValueH\u0000R\tlistValueB\u0006\n\u0004kind\";\n\tListValue\u0012.\n\u0006values\u0018\u0001 \u0003(\u000b2\u0016.google.protobuf.ValueR\u0006values*\u001b\n\tNullValue\u0012\u000e\n\nNULL_VALUE\u0010\u0000B\u007f\n\u0013com.google.protobufB\u000bStructProtoP\u0001Z/google.golang.org/protobuf/types/known/structpbø\u0001\u0001¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
