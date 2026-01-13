package com.google.protobuf;

public final class AnyProto extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_protobuf_Any_descriptor = getDescriptor().getMessageType(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Any_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Any_descriptor, new String[]{"TypeUrl", "Value"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AnyProto() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "AnyProto");
      String[] descriptorData = new String[]{
         "\n\u0019google/protobuf/any.proto\u0012\u000fgoogle.protobuf\"6\n\u0003Any\u0012\u0019\n\btype_url\u0018\u0001 \u0001(\tR\u0007typeUrl\u0012\u0014\n\u0005value\u0018\u0002 \u0001(\fR\u0005valueBv\n\u0013com.google.protobufB\bAnyProtoP\u0001Z,google.golang.org/protobuf/types/known/anypb¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
