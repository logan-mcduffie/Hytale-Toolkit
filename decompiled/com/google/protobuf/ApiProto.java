package com.google.protobuf;

public final class ApiProto extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_protobuf_Api_descriptor = getDescriptor().getMessageType(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Api_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Api_descriptor, new String[]{"Name", "Methods", "Options", "Version", "SourceContext", "Mixins", "Syntax", "Edition"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Method_descriptor = getDescriptor().getMessageType(1);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Method_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Method_descriptor,
      new String[]{"Name", "RequestTypeUrl", "RequestStreaming", "ResponseTypeUrl", "ResponseStreaming", "Options", "Syntax", "Edition"}
   );
   static final Descriptors.Descriptor internal_static_google_protobuf_Mixin_descriptor = getDescriptor().getMessageType(2);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Mixin_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Mixin_descriptor, new String[]{"Name", "Root"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private ApiProto() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "ApiProto");
      String[] descriptorData = new String[]{
         "\n\u0019google/protobuf/api.proto\u0012\u000fgoogle.protobuf\u001a$google/protobuf/source_context.proto\u001a\u001agoogle/protobuf/type.proto\"Û\u0002\n\u0003Api\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u00121\n\u0007methods\u0018\u0002 \u0003(\u000b2\u0017.google.protobuf.MethodR\u0007methods\u00121\n\u0007options\u0018\u0003 \u0003(\u000b2\u0017.google.protobuf.OptionR\u0007options\u0012\u0018\n\u0007version\u0018\u0004 \u0001(\tR\u0007version\u0012E\n\u000esource_context\u0018\u0005 \u0001(\u000b2\u001e.google.protobuf.SourceContextR\rsourceContext\u0012.\n\u0006mixins\u0018\u0006 \u0003(\u000b2\u0016.google.protobuf.MixinR\u0006mixins\u0012/\n\u0006syntax\u0018\u0007 \u0001(\u000e2\u0017.google.protobuf.SyntaxR\u0006syntax\u0012\u0018\n\u0007edition\u0018\b \u0001(\tR\u0007edition\"Ô\u0002\n\u0006Method\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012(\n\u0010request_type_url\u0018\u0002 \u0001(\tR\u000erequestTypeUrl\u0012+\n\u0011request_streaming\u0018\u0003 \u0001(\bR\u0010requestStreaming\u0012*\n\u0011response_type_url\u0018\u0004 \u0001(\tR\u000fresponseTypeUrl\u0012-\n\u0012response_streaming\u0018\u0005 \u0001(\bR\u0011responseStreaming\u00121\n\u0007options\u0018\u0006 \u0003(\u000b2\u0017.google.protobuf.OptionR\u0007options\u00123\n\u0006syntax\u0018\u0007 \u0001(\u000e2\u0017.google.protobuf.SyntaxB\u0002\u0018\u0001R\u0006syntax\u0012\u001c\n\u0007edition\u0018\b \u0001(\tB\u0002\u0018\u0001R\u0007edition\"/\n\u0005Mixin\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012\u0012\n\u0004root\u0018\u0002 \u0001(\tR\u0004rootBv\n\u0013com.google.protobufB\bApiProtoP\u0001Z,google.golang.org/protobuf/types/known/apipb¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{SourceContextProto.getDescriptor(), TypeProto.getDescriptor()}
      );
      descriptor.resolveAllFeaturesImmutable();
      SourceContextProto.getDescriptor();
      TypeProto.getDescriptor();
   }
}
