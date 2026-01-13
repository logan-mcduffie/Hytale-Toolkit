package com.google.protobuf;

public final class TimestampProto extends GeneratedFile {
   static final Descriptors.Descriptor internal_static_google_protobuf_Timestamp_descriptor = getDescriptor().getMessageType(0);
   static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_Timestamp_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_Timestamp_descriptor, new String[]{"Seconds", "Nanos"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private TimestampProto() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "TimestampProto");
      String[] descriptorData = new String[]{
         "\n\u001fgoogle/protobuf/timestamp.proto\u0012\u000fgoogle.protobuf\";\n\tTimestamp\u0012\u0018\n\u0007seconds\u0018\u0001 \u0001(\u0003R\u0007seconds\u0012\u0014\n\u0005nanos\u0018\u0002 \u0001(\u0005R\u0005nanosB\u0085\u0001\n\u0013com.google.protobufB\u000eTimestampProtoP\u0001Z2google.golang.org/protobuf/types/known/timestamppbø\u0001\u0001¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      descriptor.resolveAllFeaturesImmutable();
   }
}
