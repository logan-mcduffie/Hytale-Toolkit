package com.google.protobuf;

public final class TypeProto extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_protobuf_Type_descriptor = getDescriptor().getMessageType(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Type_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Type_descriptor, new String[]{"Name", "Fields", "Oneofs", "Options", "SourceContext", "Syntax", "Edition"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Field_descriptor = getDescriptor().getMessageType(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Field_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Field_descriptor,
      new String[]{"Kind", "Cardinality", "Number", "Name", "TypeUrl", "OneofIndex", "Packed", "Options", "JsonName", "DefaultValue"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Enum_descriptor = getDescriptor().getMessageType(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Enum_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Enum_descriptor, new String[]{"Name", "Enumvalue", "Options", "SourceContext", "Syntax", "Edition"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_EnumValue_descriptor = getDescriptor().getMessageType(3);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumValue_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_EnumValue_descriptor, new String[]{"Name", "Number", "Options"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Option_descriptor = getDescriptor().getMessageType(4);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Option_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Option_descriptor, new String[]{"Name", "Value"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private TypeProto() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "TypeProto");
      String[] descriptorData = new String[]{
         "\n\u001agoogle/protobuf/type.proto\u0012\u000fgoogle.protobuf\u001a\u0019google/protobuf/any.proto\u001a$google/protobuf/source_context.proto\"§\u0002\n\u0004Type\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012.\n\u0006fields\u0018\u0002 \u0003(\u000b2\u0016.google.protobuf.FieldR\u0006fields\u0012\u0016\n\u0006oneofs\u0018\u0003 \u0003(\tR\u0006oneofs\u00121\n\u0007options\u0018\u0004 \u0003(\u000b2\u0017.google.protobuf.OptionR\u0007options\u0012E\n\u000esource_context\u0018\u0005 \u0001(\u000b2\u001e.google.protobuf.SourceContextR\rsourceContext\u0012/\n\u0006syntax\u0018\u0006 \u0001(\u000e2\u0017.google.protobuf.SyntaxR\u0006syntax\u0012\u0018\n\u0007edition\u0018\u0007 \u0001(\tR\u0007edition\"´\u0006\n\u0005Field\u0012/\n\u0004kind\u0018\u0001 \u0001(\u000e2\u001b.google.protobuf.Field.KindR\u0004kind\u0012D\n\u000bcardinality\u0018\u0002 \u0001(\u000e2\".google.protobuf.Field.CardinalityR\u000bcardinality\u0012\u0016\n\u0006number\u0018\u0003 \u0001(\u0005R\u0006number\u0012\u0012\n\u0004name\u0018\u0004 \u0001(\tR\u0004name\u0012\u0019\n\btype_url\u0018\u0006 \u0001(\tR\u0007typeUrl\u0012\u001f\n\u000boneof_index\u0018\u0007 \u0001(\u0005R\noneofIndex\u0012\u0016\n\u0006packed\u0018\b \u0001(\bR\u0006packed\u00121\n\u0007options\u0018\t \u0003(\u000b2\u0017.google.protobuf.OptionR\u0007options\u0012\u001b\n\tjson_name\u0018\n \u0001(\tR\bjsonName\u0012#\n\rdefault_value\u0018\u000b \u0001(\tR\fdefaultValue\"È\u0002\n\u0004Kind\u0012\u0010\n\fTYPE_UNKNOWN\u0010\u0000\u0012\u000f\n\u000bTYPE_DOUBLE\u0010\u0001\u0012\u000e\n\nTYPE_FLOAT\u0010\u0002\u0012\u000e\n\nTYPE_INT64\u0010\u0003\u0012\u000f\n\u000bTYPE_UINT64\u0010\u0004\u0012\u000e\n\nTYPE_INT32\u0010\u0005\u0012\u0010\n\fTYPE_FIXED64\u0010\u0006\u0012\u0010\n\fTYPE_FIXED32\u0010\u0007\u0012\r\n\tTYPE_BOOL\u0010\b\u0012\u000f\n\u000bTYPE_STRING\u0010\t\u0012\u000e\n\nTYPE_GROUP\u0010\n\u0012\u0010\n\fTYPE_MESSAGE\u0010\u000b\u0012\u000e\n\nTYPE_BYTES\u0010\f\u0012\u000f\n\u000bTYPE_UINT32\u0010\r\u0012\r\n\tTYPE_ENUM\u0010\u000e\u0012\u0011\n\rTYPE_SFIXED32\u0010\u000f\u0012\u0011\n\rTYPE_SFIXED64\u0010\u0010\u0012\u000f\n\u000bTYPE_SINT32\u0010\u0011\u0012\u000f\n\u000bTYPE_SINT64\u0010\u0012\"t\n\u000bCardinality\u0012\u0017\n\u0013CARDINALITY_UNKNOWN\u0010\u0000\u0012\u0018\n\u0014CARDINALITY_OPTIONAL\u0010\u0001\u0012\u0018\n\u0014CARDINALITY_REQUIRED\u0010\u0002\u0012\u0018\n\u0014CARDINALITY_REPEATED\u0010\u0003\"\u0099\u0002\n\u0004Enum\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u00128\n\tenumvalue\u0018\u0002 \u0003(\u000b2\u001a.google.protobuf.EnumValueR\tenumvalue\u00121\n\u0007options\u0018\u0003 \u0003(\u000b2\u0017.google.protobuf.OptionR\u0007options\u0012E\n\u000esource_context\u0018\u0004 \u0001(\u000b2\u001e.google.protobuf.SourceContextR\rsourceContext\u0012/\n\u0006syntax\u0018\u0005 \u0001(\u000e2\u0017.google.protobuf.SyntaxR\u0006syntax\u0012\u0018\n\u0007edition\u0018\u0006 \u0001(\tR\u0007edition\"j\n\tEnumValue\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012\u0016\n\u0006number\u0018\u0002 \u0001(\u0005R\u0006number\u00121\n\u0007options\u0018\u0003 \u0003(\u000b2\u0017.google.protobuf.OptionR\u0007options\"H\n\u0006Option\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012*\n\u0005value\u0018\u0002 \u0001(\u000b2\u0014.google.protobuf.AnyR\u0005value*C\n\u0006Syntax\u0012\u0011\n\rSYNTAX_PROTO2\u0010\u0000\u0012\u0011\n\rSYNTAX_PROTO3\u0010\u0001\u0012\u0013\n\u000fSYNTAX_EDITIONS\u0010\u0002B{\n\u0013com.google.protobufB\tTypeProtoP\u0001Z-google.golang.org/protobuf/types/known/typepbø\u0001\u0001¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{AnyProto.getDescriptor(), SourceContextProto.getDescriptor()}
      );
      descriptor.resolveAllFeaturesImmutable();
      AnyProto.getDescriptor();
      SourceContextProto.getDescriptor();
   }
}
