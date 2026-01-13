package com.google.protobuf.compiler;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedFile;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginProtos extends GeneratedFile {
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_Version_descriptor = getDescriptor().getMessageType(0);
   private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_Version_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_compiler_Version_descriptor, new String[]{"Major", "Minor", "Patch", "Suffix"}
   );
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor = getDescriptor().getMessageType(1);
   private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor,
      new String[]{"FileToGenerate", "Parameter", "ProtoFile", "SourceFileDescriptors", "CompilerVersion"}
   );
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor = getDescriptor().getMessageType(2);
   private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor,
      new String[]{"Error", "SupportedFeatures", "MinimumEdition", "MaximumEdition", "File"}
   );
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor = internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor.getNestedType(
      0
   );
   private static final GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor, new String[]{"Name", "InsertionPoint", "Content", "GeneratedCodeInfo"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private PluginProtos() {
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
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "PluginProtos");
      String[] descriptorData = new String[]{
         "\n%google/protobuf/compiler/plugin.proto\u0012\u0018google.protobuf.compiler\u001a google/protobuf/descriptor.proto\"c\n\u0007Version\u0012\u0014\n\u0005major\u0018\u0001 \u0001(\u0005R\u0005major\u0012\u0014\n\u0005minor\u0018\u0002 \u0001(\u0005R\u0005minor\u0012\u0014\n\u0005patch\u0018\u0003 \u0001(\u0005R\u0005patch\u0012\u0016\n\u0006suffix\u0018\u0004 \u0001(\tR\u0006suffix\"Ï\u0002\n\u0014CodeGeneratorRequest\u0012(\n\u0010file_to_generate\u0018\u0001 \u0003(\tR\u000efileToGenerate\u0012\u001c\n\tparameter\u0018\u0002 \u0001(\tR\tparameter\u0012C\n\nproto_file\u0018\u000f \u0003(\u000b2$.google.protobuf.FileDescriptorProtoR\tprotoFile\u0012\\\n\u0017source_file_descriptors\u0018\u0011 \u0003(\u000b2$.google.protobuf.FileDescriptorProtoR\u0015sourceFileDescriptors\u0012L\n\u0010compiler_version\u0018\u0003 \u0001(\u000b2!.google.protobuf.compiler.VersionR\u000fcompilerVersion\"\u0085\u0004\n\u0015CodeGeneratorResponse\u0012\u0014\n\u0005error\u0018\u0001 \u0001(\tR\u0005error\u0012-\n\u0012supported_features\u0018\u0002 \u0001(\u0004R\u0011supportedFeatures\u0012'\n\u000fminimum_edition\u0018\u0003 \u0001(\u0005R\u000eminimumEdition\u0012'\n\u000fmaximum_edition\u0018\u0004 \u0001(\u0005R\u000emaximumEdition\u0012H\n\u0004file\u0018\u000f \u0003(\u000b24.google.protobuf.compiler.CodeGeneratorResponse.FileR\u0004file\u001a±\u0001\n\u0004File\u0012\u0012\n\u0004name\u0018\u0001 \u0001(\tR\u0004name\u0012'\n\u000finsertion_point\u0018\u0002 \u0001(\tR\u000einsertionPoint\u0012\u0018\n\u0007content\u0018\u000f \u0001(\tR\u0007content\u0012R\n\u0013generated_code_info\u0018\u0010 \u0001(\u000b2\".google.protobuf.GeneratedCodeInfoR\u0011generatedCodeInfo\"W\n\u0007Feature\u0012\u0010\n\fFEATURE_NONE\u0010\u0000\u0012\u001b\n\u0017FEATURE_PROTO3_OPTIONAL\u0010\u0001\u0012\u001d\n\u0019FEATURE_SUPPORTS_EDITIONS\u0010\u0002Br\n\u001ccom.google.protobuf.compilerB\fPluginProtosZ)google.golang.org/protobuf/types/pluginpbª\u0002\u0018Google.Protobuf.Compiler"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
      descriptor.resolveAllFeaturesImmutable();
      DescriptorProtos.getDescriptor();
   }

   public static final class CodeGeneratorRequest extends GeneratedMessage implements PluginProtos.CodeGeneratorRequestOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int FILE_TO_GENERATE_FIELD_NUMBER = 1;
      private LazyStringArrayList fileToGenerate_ = LazyStringArrayList.emptyList();
      public static final int PARAMETER_FIELD_NUMBER = 2;
      private volatile Object parameter_ = "";
      public static final int PROTO_FILE_FIELD_NUMBER = 15;
      private List<DescriptorProtos.FileDescriptorProto> protoFile_;
      public static final int SOURCE_FILE_DESCRIPTORS_FIELD_NUMBER = 17;
      private List<DescriptorProtos.FileDescriptorProto> sourceFileDescriptors_;
      public static final int COMPILER_VERSION_FIELD_NUMBER = 3;
      private PluginProtos.Version compilerVersion_;
      private byte memoizedIsInitialized = -1;
      private static final PluginProtos.CodeGeneratorRequest DEFAULT_INSTANCE = new PluginProtos.CodeGeneratorRequest();
      private static final Parser<PluginProtos.CodeGeneratorRequest> PARSER = new AbstractParser<PluginProtos.CodeGeneratorRequest>() {
         public PluginProtos.CodeGeneratorRequest parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            PluginProtos.CodeGeneratorRequest.Builder builder = PluginProtos.CodeGeneratorRequest.newBuilder();

            try {
               builder.mergeFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var5) {
               throw var5.setUnfinishedMessage(builder.buildPartial());
            } catch (UninitializedMessageException var6) {
               throw var6.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
            } catch (IOException var7) {
               throw new InvalidProtocolBufferException(var7).setUnfinishedMessage(builder.buildPartial());
            }

            return builder.buildPartial();
         }
      };

      private CodeGeneratorRequest(GeneratedMessage.Builder<?> builder) {
         super(builder);
      }

      private CodeGeneratorRequest() {
         this.fileToGenerate_ = LazyStringArrayList.emptyList();
         this.parameter_ = "";
         this.protoFile_ = Collections.emptyList();
         this.sourceFileDescriptors_ = Collections.emptyList();
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorRequest.class, PluginProtos.CodeGeneratorRequest.Builder.class);
      }

      public ProtocolStringList getFileToGenerateList() {
         return this.fileToGenerate_;
      }

      @Override
      public int getFileToGenerateCount() {
         return this.fileToGenerate_.size();
      }

      @Override
      public String getFileToGenerate(int index) {
         return this.fileToGenerate_.get(index);
      }

      @Override
      public ByteString getFileToGenerateBytes(int index) {
         return this.fileToGenerate_.getByteString(index);
      }

      @Override
      public boolean hasParameter() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getParameter() {
         Object ref = this.parameter_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.parameter_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getParameterBytes() {
         Object ref = this.parameter_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.parameter_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
         return this.protoFile_;
      }

      @Override
      public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
         return this.protoFile_;
      }

      @Override
      public int getProtoFileCount() {
         return this.protoFile_.size();
      }

      @Override
      public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
         return this.protoFile_.get(index);
      }

      @Override
      public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
         return this.protoFile_.get(index);
      }

      @Override
      public List<DescriptorProtos.FileDescriptorProto> getSourceFileDescriptorsList() {
         return this.sourceFileDescriptors_;
      }

      @Override
      public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getSourceFileDescriptorsOrBuilderList() {
         return this.sourceFileDescriptors_;
      }

      @Override
      public int getSourceFileDescriptorsCount() {
         return this.sourceFileDescriptors_.size();
      }

      @Override
      public DescriptorProtos.FileDescriptorProto getSourceFileDescriptors(int index) {
         return this.sourceFileDescriptors_.get(index);
      }

      @Override
      public DescriptorProtos.FileDescriptorProtoOrBuilder getSourceFileDescriptorsOrBuilder(int index) {
         return this.sourceFileDescriptors_.get(index);
      }

      @Override
      public boolean hasCompilerVersion() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public PluginProtos.Version getCompilerVersion() {
         return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
      }

      @Override
      public PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder() {
         return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getProtoFileCount(); i++) {
               if (!this.getProtoFile(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            for (int ix = 0; ix < this.getSourceFileDescriptorsCount(); ix++) {
               if (!this.getSourceFileDescriptors(ix).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         for (int i = 0; i < this.fileToGenerate_.size(); i++) {
            GeneratedMessage.writeString(output, 1, this.fileToGenerate_.getRaw(i));
         }

         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessage.writeString(output, 2, this.parameter_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(3, this.getCompilerVersion());
         }

         for (int i = 0; i < this.protoFile_.size(); i++) {
            output.writeMessage(15, this.protoFile_.get(i));
         }

         for (int i = 0; i < this.sourceFileDescriptors_.size(); i++) {
            output.writeMessage(17, this.sourceFileDescriptors_.get(i));
         }

         this.getUnknownFields().writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            int var4 = 0;
            int dataSize = 0;

            for (int i = 0; i < this.fileToGenerate_.size(); i++) {
               dataSize += computeStringSizeNoTag(this.fileToGenerate_.getRaw(i));
            }

            var4 += dataSize;
            var4 += 1 * this.getFileToGenerateList().size();
            if ((this.bitField0_ & 1) != 0) {
               var4 += GeneratedMessage.computeStringSize(2, this.parameter_);
            }

            if ((this.bitField0_ & 2) != 0) {
               var4 += CodedOutputStream.computeMessageSize(3, this.getCompilerVersion());
            }

            for (int i = 0; i < this.protoFile_.size(); i++) {
               var4 += CodedOutputStream.computeMessageSize(15, this.protoFile_.get(i));
            }

            for (int i = 0; i < this.sourceFileDescriptors_.size(); i++) {
               var4 += CodedOutputStream.computeMessageSize(17, this.sourceFileDescriptors_.get(i));
            }

            var4 += this.getUnknownFields().getSerializedSize();
            this.memoizedSize = var4;
            return var4;
         }
      }

      @Override
      public boolean equals(final Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof PluginProtos.CodeGeneratorRequest)) {
            return super.equals(obj);
         } else {
            PluginProtos.CodeGeneratorRequest other = (PluginProtos.CodeGeneratorRequest)obj;
            if (!this.getFileToGenerateList().equals(other.getFileToGenerateList())) {
               return false;
            } else if (this.hasParameter() != other.hasParameter()) {
               return false;
            } else if (this.hasParameter() && !this.getParameter().equals(other.getParameter())) {
               return false;
            } else if (!this.getProtoFileList().equals(other.getProtoFileList())) {
               return false;
            } else if (!this.getSourceFileDescriptorsList().equals(other.getSourceFileDescriptorsList())) {
               return false;
            } else if (this.hasCompilerVersion() != other.hasCompilerVersion()) {
               return false;
            } else {
               return this.hasCompilerVersion() && !this.getCompilerVersion().equals(other.getCompilerVersion())
                  ? false
                  : this.getUnknownFields().equals(other.getUnknownFields());
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getFileToGenerateCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getFileToGenerateList().hashCode();
            }

            if (this.hasParameter()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getParameter().hashCode();
            }

            if (this.getProtoFileCount() > 0) {
               hash = 37 * hash + 15;
               hash = 53 * hash + this.getProtoFileList().hashCode();
            }

            if (this.getSourceFileDescriptorsCount() > 0) {
               hash = 37 * hash + 17;
               hash = 53 * hash + this.getSourceFileDescriptorsList().hashCode();
            }

            if (this.hasCompilerVersion()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getCompilerVersion().hashCode();
            }

            hash = 29 * hash + this.getUnknownFields().hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorRequest parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static PluginProtos.CodeGeneratorRequest parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public PluginProtos.CodeGeneratorRequest.Builder newBuilderForType() {
         return newBuilder();
      }

      public static PluginProtos.CodeGeneratorRequest.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static PluginProtos.CodeGeneratorRequest.Builder newBuilder(PluginProtos.CodeGeneratorRequest prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public PluginProtos.CodeGeneratorRequest.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new PluginProtos.CodeGeneratorRequest.Builder() : new PluginProtos.CodeGeneratorRequest.Builder().mergeFrom(this);
      }

      protected PluginProtos.CodeGeneratorRequest.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new PluginProtos.CodeGeneratorRequest.Builder(parent);
      }

      public static PluginProtos.CodeGeneratorRequest getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<PluginProtos.CodeGeneratorRequest> parser() {
         return PARSER;
      }

      @Override
      public Parser<PluginProtos.CodeGeneratorRequest> getParserForType() {
         return PARSER;
      }

      public PluginProtos.CodeGeneratorRequest getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "CodeGeneratorRequest");
      }

      public static final class Builder
         extends GeneratedMessage.Builder<PluginProtos.CodeGeneratorRequest.Builder>
         implements PluginProtos.CodeGeneratorRequestOrBuilder {
         private int bitField0_;
         private LazyStringArrayList fileToGenerate_ = LazyStringArrayList.emptyList();
         private Object parameter_ = "";
         private List<DescriptorProtos.FileDescriptorProto> protoFile_ = Collections.emptyList();
         private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> protoFileBuilder_;
         private List<DescriptorProtos.FileDescriptorProto> sourceFileDescriptors_ = Collections.emptyList();
         private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> sourceFileDescriptorsBuilder_;
         private PluginProtos.Version compilerVersion_;
         private SingleFieldBuilder<PluginProtos.Version, PluginProtos.Version.Builder, PluginProtos.VersionOrBuilder> compilerVersionBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorRequest.class, PluginProtos.CodeGeneratorRequest.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders) {
               this.internalGetProtoFileFieldBuilder();
               this.internalGetSourceFileDescriptorsFieldBuilder();
               this.internalGetCompilerVersionFieldBuilder();
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.fileToGenerate_ = LazyStringArrayList.emptyList();
            this.parameter_ = "";
            if (this.protoFileBuilder_ == null) {
               this.protoFile_ = Collections.emptyList();
            } else {
               this.protoFile_ = null;
               this.protoFileBuilder_.clear();
            }

            this.bitField0_ &= -5;
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.sourceFileDescriptors_ = Collections.emptyList();
            } else {
               this.sourceFileDescriptors_ = null;
               this.sourceFileDescriptorsBuilder_.clear();
            }

            this.bitField0_ &= -9;
            this.compilerVersion_ = null;
            if (this.compilerVersionBuilder_ != null) {
               this.compilerVersionBuilder_.dispose();
               this.compilerVersionBuilder_ = null;
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
         }

         public PluginProtos.CodeGeneratorRequest getDefaultInstanceForType() {
            return PluginProtos.CodeGeneratorRequest.getDefaultInstance();
         }

         public PluginProtos.CodeGeneratorRequest build() {
            PluginProtos.CodeGeneratorRequest result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public PluginProtos.CodeGeneratorRequest buildPartial() {
            PluginProtos.CodeGeneratorRequest result = new PluginProtos.CodeGeneratorRequest(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartialRepeatedFields(PluginProtos.CodeGeneratorRequest result) {
            if (this.protoFileBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.protoFile_ = Collections.unmodifiableList(this.protoFile_);
                  this.bitField0_ &= -5;
               }

               result.protoFile_ = this.protoFile_;
            } else {
               result.protoFile_ = this.protoFileBuilder_.build();
            }

            if (this.sourceFileDescriptorsBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0) {
                  this.sourceFileDescriptors_ = Collections.unmodifiableList(this.sourceFileDescriptors_);
                  this.bitField0_ &= -9;
               }

               result.sourceFileDescriptors_ = this.sourceFileDescriptors_;
            } else {
               result.sourceFileDescriptors_ = this.sourceFileDescriptorsBuilder_.build();
            }
         }

         private void buildPartial0(PluginProtos.CodeGeneratorRequest result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
               this.fileToGenerate_.makeImmutable();
               result.fileToGenerate_ = this.fileToGenerate_;
            }

            int to_bitField0_ = 0;
            if ((from_bitField0_ & 2) != 0) {
               result.parameter_ = this.parameter_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 16) != 0) {
               result.compilerVersion_ = this.compilerVersionBuilder_ == null ? this.compilerVersion_ : this.compilerVersionBuilder_.build();
               to_bitField0_ |= 2;
            }

            result.bitField0_ |= to_bitField0_;
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeFrom(Message other) {
            if (other instanceof PluginProtos.CodeGeneratorRequest) {
               return this.mergeFrom((PluginProtos.CodeGeneratorRequest)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeFrom(PluginProtos.CodeGeneratorRequest other) {
            if (other == PluginProtos.CodeGeneratorRequest.getDefaultInstance()) {
               return this;
            } else {
               if (!other.fileToGenerate_.isEmpty()) {
                  if (this.fileToGenerate_.isEmpty()) {
                     this.fileToGenerate_ = other.fileToGenerate_;
                     this.bitField0_ |= 1;
                  } else {
                     this.ensureFileToGenerateIsMutable();
                     this.fileToGenerate_.addAll(other.fileToGenerate_);
                  }

                  this.onChanged();
               }

               if (other.hasParameter()) {
                  this.parameter_ = other.parameter_;
                  this.bitField0_ |= 2;
                  this.onChanged();
               }

               if (this.protoFileBuilder_ == null) {
                  if (!other.protoFile_.isEmpty()) {
                     if (this.protoFile_.isEmpty()) {
                        this.protoFile_ = other.protoFile_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureProtoFileIsMutable();
                        this.protoFile_.addAll(other.protoFile_);
                     }

                     this.onChanged();
                  }
               } else if (!other.protoFile_.isEmpty()) {
                  if (this.protoFileBuilder_.isEmpty()) {
                     this.protoFileBuilder_.dispose();
                     this.protoFileBuilder_ = null;
                     this.protoFile_ = other.protoFile_;
                     this.bitField0_ &= -5;
                     this.protoFileBuilder_ = PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders ? this.internalGetProtoFileFieldBuilder() : null;
                  } else {
                     this.protoFileBuilder_.addAllMessages(other.protoFile_);
                  }
               }

               if (this.sourceFileDescriptorsBuilder_ == null) {
                  if (!other.sourceFileDescriptors_.isEmpty()) {
                     if (this.sourceFileDescriptors_.isEmpty()) {
                        this.sourceFileDescriptors_ = other.sourceFileDescriptors_;
                        this.bitField0_ &= -9;
                     } else {
                        this.ensureSourceFileDescriptorsIsMutable();
                        this.sourceFileDescriptors_.addAll(other.sourceFileDescriptors_);
                     }

                     this.onChanged();
                  }
               } else if (!other.sourceFileDescriptors_.isEmpty()) {
                  if (this.sourceFileDescriptorsBuilder_.isEmpty()) {
                     this.sourceFileDescriptorsBuilder_.dispose();
                     this.sourceFileDescriptorsBuilder_ = null;
                     this.sourceFileDescriptors_ = other.sourceFileDescriptors_;
                     this.bitField0_ &= -9;
                     this.sourceFileDescriptorsBuilder_ = PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders
                        ? this.internalGetSourceFileDescriptorsFieldBuilder()
                        : null;
                  } else {
                     this.sourceFileDescriptorsBuilder_.addAllMessages(other.sourceFileDescriptors_);
                  }
               }

               if (other.hasCompilerVersion()) {
                  this.mergeCompilerVersion(other.getCompilerVersion());
               }

               this.mergeUnknownFields(other.getUnknownFields());
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getProtoFileCount(); i++) {
               if (!this.getProtoFile(i).isInitialized()) {
                  return false;
               }
            }

            for (int ix = 0; ix < this.getSourceFileDescriptorsCount(); ix++) {
               if (!this.getSourceFileDescriptors(ix).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               try {
                  boolean done = false;

                  while (!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 10:
                           ByteString bs = input.readBytes();
                           this.ensureFileToGenerateIsMutable();
                           this.fileToGenerate_.add(bs);
                           break;
                        case 18:
                           this.parameter_ = input.readBytes();
                           this.bitField0_ |= 2;
                           break;
                        case 26:
                           input.readMessage(this.internalGetCompilerVersionFieldBuilder().getBuilder(), extensionRegistry);
                           this.bitField0_ |= 16;
                           break;
                        case 122:
                           DescriptorProtos.FileDescriptorProto mx = input.readMessage(DescriptorProtos.FileDescriptorProto.parser(), extensionRegistry);
                           if (this.protoFileBuilder_ == null) {
                              this.ensureProtoFileIsMutable();
                              this.protoFile_.add(mx);
                           } else {
                              this.protoFileBuilder_.addMessage(mx);
                           }
                           break;
                        case 138:
                           DescriptorProtos.FileDescriptorProto m = input.readMessage(DescriptorProtos.FileDescriptorProto.parser(), extensionRegistry);
                           if (this.sourceFileDescriptorsBuilder_ == null) {
                              this.ensureSourceFileDescriptorsIsMutable();
                              this.sourceFileDescriptors_.add(m);
                           } else {
                              this.sourceFileDescriptorsBuilder_.addMessage(m);
                           }
                           break;
                        default:
                           if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var9) {
                  throw var9.unwrapIOException();
               } finally {
                  this.onChanged();
               }

               return this;
            }
         }

         private void ensureFileToGenerateIsMutable() {
            if (!this.fileToGenerate_.isModifiable()) {
               this.fileToGenerate_ = new LazyStringArrayList(this.fileToGenerate_);
            }

            this.bitField0_ |= 1;
         }

         public ProtocolStringList getFileToGenerateList() {
            this.fileToGenerate_.makeImmutable();
            return this.fileToGenerate_;
         }

         @Override
         public int getFileToGenerateCount() {
            return this.fileToGenerate_.size();
         }

         @Override
         public String getFileToGenerate(int index) {
            return this.fileToGenerate_.get(index);
         }

         @Override
         public ByteString getFileToGenerateBytes(int index) {
            return this.fileToGenerate_.getByteString(index);
         }

         public PluginProtos.CodeGeneratorRequest.Builder setFileToGenerate(int index, String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFileToGenerateIsMutable();
               this.fileToGenerate_.set(index, value);
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder addFileToGenerate(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFileToGenerateIsMutable();
               this.fileToGenerate_.add(value);
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder addAllFileToGenerate(Iterable<String> values) {
            this.ensureFileToGenerateIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.fileToGenerate_);
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearFileToGenerate() {
            this.fileToGenerate_ = LazyStringArrayList.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addFileToGenerateBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFileToGenerateIsMutable();
               this.fileToGenerate_.add(value);
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasParameter() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getParameter() {
            Object ref = this.parameter_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.parameter_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getParameterBytes() {
            Object ref = this.parameter_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.parameter_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder setParameter(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.parameter_ = value;
               this.bitField0_ |= 2;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearParameter() {
            this.parameter_ = PluginProtos.CodeGeneratorRequest.getDefaultInstance().getParameter();
            this.bitField0_ &= -3;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setParameterBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.parameter_ = value;
               this.bitField0_ |= 2;
               this.onChanged();
               return this;
            }
         }

         private void ensureProtoFileIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.protoFile_ = new ArrayList<>(this.protoFile_);
               this.bitField0_ |= 4;
            }
         }

         @Override
         public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
            return this.protoFileBuilder_ == null ? Collections.unmodifiableList(this.protoFile_) : this.protoFileBuilder_.getMessageList();
         }

         @Override
         public int getProtoFileCount() {
            return this.protoFileBuilder_ == null ? this.protoFile_.size() : this.protoFileBuilder_.getCount();
         }

         @Override
         public DescriptorProtos.FileDescriptorProto getProtoFile(int index) {
            return this.protoFileBuilder_ == null ? this.protoFile_.get(index) : this.protoFileBuilder_.getMessage(index);
         }

         public PluginProtos.CodeGeneratorRequest.Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
            if (this.protoFileBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProtoFileIsMutable();
               this.protoFile_.set(index, value);
               this.onChanged();
            } else {
               this.protoFileBuilder_.setMessage(index, value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.protoFileBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(DescriptorProtos.FileDescriptorProto value) {
            if (this.protoFileBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProtoFileIsMutable();
               this.protoFile_.add(value);
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto value) {
            if (this.protoFileBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureProtoFileIsMutable();
               this.protoFile_.add(index, value);
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(index, value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addAllProtoFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.protoFile_);
               this.onChanged();
            } else {
               this.protoFileBuilder_.addAllMessages(values);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearProtoFile() {
            if (this.protoFileBuilder_ == null) {
               this.protoFile_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.protoFileBuilder_.clear();
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder removeProtoFile(int index) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.remove(index);
               this.onChanged();
            } else {
               this.protoFileBuilder_.remove(index);
            }

            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder getProtoFileBuilder(int index) {
            return this.internalGetProtoFileFieldBuilder().getBuilder(index);
         }

         @Override
         public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index) {
            return this.protoFileBuilder_ == null ? this.protoFile_.get(index) : this.protoFileBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
            return this.protoFileBuilder_ != null ? this.protoFileBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.protoFile_);
         }

         public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder() {
            return this.internalGetProtoFileFieldBuilder().addBuilder(DescriptorProtos.FileDescriptorProto.getDefaultInstance());
         }

         public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder(int index) {
            return this.internalGetProtoFileFieldBuilder().addBuilder(index, DescriptorProtos.FileDescriptorProto.getDefaultInstance());
         }

         public List<DescriptorProtos.FileDescriptorProto.Builder> getProtoFileBuilderList() {
            return this.internalGetProtoFileFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> internalGetProtoFileFieldBuilder() {
            if (this.protoFileBuilder_ == null) {
               this.protoFileBuilder_ = new RepeatedFieldBuilder<>(this.protoFile_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.protoFile_ = null;
            }

            return this.protoFileBuilder_;
         }

         private void ensureSourceFileDescriptorsIsMutable() {
            if ((this.bitField0_ & 8) == 0) {
               this.sourceFileDescriptors_ = new ArrayList<>(this.sourceFileDescriptors_);
               this.bitField0_ |= 8;
            }
         }

         @Override
         public List<DescriptorProtos.FileDescriptorProto> getSourceFileDescriptorsList() {
            return this.sourceFileDescriptorsBuilder_ == null
               ? Collections.unmodifiableList(this.sourceFileDescriptors_)
               : this.sourceFileDescriptorsBuilder_.getMessageList();
         }

         @Override
         public int getSourceFileDescriptorsCount() {
            return this.sourceFileDescriptorsBuilder_ == null ? this.sourceFileDescriptors_.size() : this.sourceFileDescriptorsBuilder_.getCount();
         }

         @Override
         public DescriptorProtos.FileDescriptorProto getSourceFileDescriptors(int index) {
            return this.sourceFileDescriptorsBuilder_ == null ? this.sourceFileDescriptors_.get(index) : this.sourceFileDescriptorsBuilder_.getMessage(index);
         }

         public PluginProtos.CodeGeneratorRequest.Builder setSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto value) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.set(index, value);
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addSourceFileDescriptors(DescriptorProtos.FileDescriptorProto value) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.add(value);
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.addMessage(value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto value) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.add(index, value);
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addSourceFileDescriptors(DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addSourceFileDescriptors(int index, DescriptorProtos.FileDescriptorProto.Builder builderForValue) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addAllSourceFileDescriptors(Iterable<? extends DescriptorProtos.FileDescriptorProto> values) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.ensureSourceFileDescriptorsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.sourceFileDescriptors_);
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearSourceFileDescriptors() {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.sourceFileDescriptors_ = Collections.emptyList();
               this.bitField0_ &= -9;
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.clear();
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder removeSourceFileDescriptors(int index) {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.ensureSourceFileDescriptorsIsMutable();
               this.sourceFileDescriptors_.remove(index);
               this.onChanged();
            } else {
               this.sourceFileDescriptorsBuilder_.remove(index);
            }

            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder getSourceFileDescriptorsBuilder(int index) {
            return this.internalGetSourceFileDescriptorsFieldBuilder().getBuilder(index);
         }

         @Override
         public DescriptorProtos.FileDescriptorProtoOrBuilder getSourceFileDescriptorsOrBuilder(int index) {
            return this.sourceFileDescriptorsBuilder_ == null
               ? this.sourceFileDescriptors_.get(index)
               : this.sourceFileDescriptorsBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getSourceFileDescriptorsOrBuilderList() {
            return this.sourceFileDescriptorsBuilder_ != null
               ? this.sourceFileDescriptorsBuilder_.getMessageOrBuilderList()
               : Collections.unmodifiableList(this.sourceFileDescriptors_);
         }

         public DescriptorProtos.FileDescriptorProto.Builder addSourceFileDescriptorsBuilder() {
            return this.internalGetSourceFileDescriptorsFieldBuilder().addBuilder(DescriptorProtos.FileDescriptorProto.getDefaultInstance());
         }

         public DescriptorProtos.FileDescriptorProto.Builder addSourceFileDescriptorsBuilder(int index) {
            return this.internalGetSourceFileDescriptorsFieldBuilder().addBuilder(index, DescriptorProtos.FileDescriptorProto.getDefaultInstance());
         }

         public List<DescriptorProtos.FileDescriptorProto.Builder> getSourceFileDescriptorsBuilderList() {
            return this.internalGetSourceFileDescriptorsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilder<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> internalGetSourceFileDescriptorsFieldBuilder() {
            if (this.sourceFileDescriptorsBuilder_ == null) {
               this.sourceFileDescriptorsBuilder_ = new RepeatedFieldBuilder<>(
                  this.sourceFileDescriptors_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean()
               );
               this.sourceFileDescriptors_ = null;
            }

            return this.sourceFileDescriptorsBuilder_;
         }

         @Override
         public boolean hasCompilerVersion() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public PluginProtos.Version getCompilerVersion() {
            if (this.compilerVersionBuilder_ == null) {
               return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
            } else {
               return this.compilerVersionBuilder_.getMessage();
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder setCompilerVersion(PluginProtos.Version value) {
            if (this.compilerVersionBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.compilerVersion_ = value;
            } else {
               this.compilerVersionBuilder_.setMessage(value);
            }

            this.bitField0_ |= 16;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setCompilerVersion(PluginProtos.Version.Builder builderForValue) {
            if (this.compilerVersionBuilder_ == null) {
               this.compilerVersion_ = builderForValue.build();
            } else {
               this.compilerVersionBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 16;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeCompilerVersion(PluginProtos.Version value) {
            if (this.compilerVersionBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0 && this.compilerVersion_ != null && this.compilerVersion_ != PluginProtos.Version.getDefaultInstance()) {
                  this.getCompilerVersionBuilder().mergeFrom(value);
               } else {
                  this.compilerVersion_ = value;
               }
            } else {
               this.compilerVersionBuilder_.mergeFrom(value);
            }

            if (this.compilerVersion_ != null) {
               this.bitField0_ |= 16;
               this.onChanged();
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearCompilerVersion() {
            this.bitField0_ &= -17;
            this.compilerVersion_ = null;
            if (this.compilerVersionBuilder_ != null) {
               this.compilerVersionBuilder_.dispose();
               this.compilerVersionBuilder_ = null;
            }

            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder getCompilerVersionBuilder() {
            this.bitField0_ |= 16;
            this.onChanged();
            return this.internalGetCompilerVersionFieldBuilder().getBuilder();
         }

         @Override
         public PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder() {
            if (this.compilerVersionBuilder_ != null) {
               return this.compilerVersionBuilder_.getMessageOrBuilder();
            } else {
               return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
            }
         }

         private SingleFieldBuilder<PluginProtos.Version, PluginProtos.Version.Builder, PluginProtos.VersionOrBuilder> internalGetCompilerVersionFieldBuilder() {
            if (this.compilerVersionBuilder_ == null) {
               this.compilerVersionBuilder_ = new SingleFieldBuilder<>(this.getCompilerVersion(), this.getParentForChildren(), this.isClean());
               this.compilerVersion_ = null;
            }

            return this.compilerVersionBuilder_;
         }
      }
   }

   public interface CodeGeneratorRequestOrBuilder extends MessageOrBuilder {
      List<String> getFileToGenerateList();

      int getFileToGenerateCount();

      String getFileToGenerate(int index);

      ByteString getFileToGenerateBytes(int index);

      boolean hasParameter();

      String getParameter();

      ByteString getParameterBytes();

      List<DescriptorProtos.FileDescriptorProto> getProtoFileList();

      DescriptorProtos.FileDescriptorProto getProtoFile(int index);

      int getProtoFileCount();

      List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList();

      DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int index);

      List<DescriptorProtos.FileDescriptorProto> getSourceFileDescriptorsList();

      DescriptorProtos.FileDescriptorProto getSourceFileDescriptors(int index);

      int getSourceFileDescriptorsCount();

      List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getSourceFileDescriptorsOrBuilderList();

      DescriptorProtos.FileDescriptorProtoOrBuilder getSourceFileDescriptorsOrBuilder(int index);

      boolean hasCompilerVersion();

      PluginProtos.Version getCompilerVersion();

      PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder();
   }

   public static final class CodeGeneratorResponse extends GeneratedMessage implements PluginProtos.CodeGeneratorResponseOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int ERROR_FIELD_NUMBER = 1;
      private volatile Object error_ = "";
      public static final int SUPPORTED_FEATURES_FIELD_NUMBER = 2;
      private long supportedFeatures_ = 0L;
      public static final int MINIMUM_EDITION_FIELD_NUMBER = 3;
      private int minimumEdition_ = 0;
      public static final int MAXIMUM_EDITION_FIELD_NUMBER = 4;
      private int maximumEdition_ = 0;
      public static final int FILE_FIELD_NUMBER = 15;
      private List<PluginProtos.CodeGeneratorResponse.File> file_;
      private byte memoizedIsInitialized = -1;
      private static final PluginProtos.CodeGeneratorResponse DEFAULT_INSTANCE = new PluginProtos.CodeGeneratorResponse();
      private static final Parser<PluginProtos.CodeGeneratorResponse> PARSER = new AbstractParser<PluginProtos.CodeGeneratorResponse>() {
         public PluginProtos.CodeGeneratorResponse parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            PluginProtos.CodeGeneratorResponse.Builder builder = PluginProtos.CodeGeneratorResponse.newBuilder();

            try {
               builder.mergeFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var5) {
               throw var5.setUnfinishedMessage(builder.buildPartial());
            } catch (UninitializedMessageException var6) {
               throw var6.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
            } catch (IOException var7) {
               throw new InvalidProtocolBufferException(var7).setUnfinishedMessage(builder.buildPartial());
            }

            return builder.buildPartial();
         }
      };

      private CodeGeneratorResponse(GeneratedMessage.Builder<?> builder) {
         super(builder);
      }

      private CodeGeneratorResponse() {
         this.error_ = "";
         this.file_ = Collections.emptyList();
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.class, PluginProtos.CodeGeneratorResponse.Builder.class);
      }

      @Override
      public boolean hasError() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getError() {
         Object ref = this.error_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.error_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getErrorBytes() {
         Object ref = this.error_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.error_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasSupportedFeatures() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public long getSupportedFeatures() {
         return this.supportedFeatures_;
      }

      @Override
      public boolean hasMinimumEdition() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public int getMinimumEdition() {
         return this.minimumEdition_;
      }

      @Override
      public boolean hasMaximumEdition() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public int getMaximumEdition() {
         return this.maximumEdition_;
      }

      @Override
      public List<PluginProtos.CodeGeneratorResponse.File> getFileList() {
         return this.file_;
      }

      @Override
      public List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList() {
         return this.file_;
      }

      @Override
      public int getFileCount() {
         return this.file_.size();
      }

      @Override
      public PluginProtos.CodeGeneratorResponse.File getFile(int index) {
         return this.file_.get(index);
      }

      @Override
      public PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int index) {
         return this.file_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessage.writeString(output, 1, this.error_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt64(2, this.supportedFeatures_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeInt32(3, this.minimumEdition_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeInt32(4, this.maximumEdition_);
         }

         for (int i = 0; i < this.file_.size(); i++) {
            output.writeMessage(15, this.file_.get(i));
         }

         this.getUnknownFields().writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessage.computeStringSize(1, this.error_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt64Size(2, this.supportedFeatures_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeInt32Size(3, this.minimumEdition_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeInt32Size(4, this.maximumEdition_);
            }

            for (int i = 0; i < this.file_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(15, this.file_.get(i));
            }

            size += this.getUnknownFields().getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(final Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof PluginProtos.CodeGeneratorResponse)) {
            return super.equals(obj);
         } else {
            PluginProtos.CodeGeneratorResponse other = (PluginProtos.CodeGeneratorResponse)obj;
            if (this.hasError() != other.hasError()) {
               return false;
            } else if (this.hasError() && !this.getError().equals(other.getError())) {
               return false;
            } else if (this.hasSupportedFeatures() != other.hasSupportedFeatures()) {
               return false;
            } else if (this.hasSupportedFeatures() && this.getSupportedFeatures() != other.getSupportedFeatures()) {
               return false;
            } else if (this.hasMinimumEdition() != other.hasMinimumEdition()) {
               return false;
            } else if (this.hasMinimumEdition() && this.getMinimumEdition() != other.getMinimumEdition()) {
               return false;
            } else if (this.hasMaximumEdition() != other.hasMaximumEdition()) {
               return false;
            } else if (this.hasMaximumEdition() && this.getMaximumEdition() != other.getMaximumEdition()) {
               return false;
            } else {
               return !this.getFileList().equals(other.getFileList()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasError()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getError().hashCode();
            }

            if (this.hasSupportedFeatures()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + Internal.hashLong(this.getSupportedFeatures());
            }

            if (this.hasMinimumEdition()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getMinimumEdition();
            }

            if (this.hasMaximumEdition()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getMaximumEdition();
            }

            if (this.getFileCount() > 0) {
               hash = 37 * hash + 15;
               hash = 53 * hash + this.getFileList().hashCode();
            }

            hash = 29 * hash + this.getUnknownFields().hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorResponse parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static PluginProtos.CodeGeneratorResponse parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public PluginProtos.CodeGeneratorResponse.Builder newBuilderForType() {
         return newBuilder();
      }

      public static PluginProtos.CodeGeneratorResponse.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static PluginProtos.CodeGeneratorResponse.Builder newBuilder(PluginProtos.CodeGeneratorResponse prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public PluginProtos.CodeGeneratorResponse.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new PluginProtos.CodeGeneratorResponse.Builder() : new PluginProtos.CodeGeneratorResponse.Builder().mergeFrom(this);
      }

      protected PluginProtos.CodeGeneratorResponse.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new PluginProtos.CodeGeneratorResponse.Builder(parent);
      }

      public static PluginProtos.CodeGeneratorResponse getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<PluginProtos.CodeGeneratorResponse> parser() {
         return PARSER;
      }

      @Override
      public Parser<PluginProtos.CodeGeneratorResponse> getParserForType() {
         return PARSER;
      }

      public PluginProtos.CodeGeneratorResponse getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "CodeGeneratorResponse");
      }

      public static final class Builder
         extends GeneratedMessage.Builder<PluginProtos.CodeGeneratorResponse.Builder>
         implements PluginProtos.CodeGeneratorResponseOrBuilder {
         private int bitField0_;
         private Object error_ = "";
         private long supportedFeatures_;
         private int minimumEdition_;
         private int maximumEdition_;
         private List<PluginProtos.CodeGeneratorResponse.File> file_ = Collections.emptyList();
         private RepeatedFieldBuilder<PluginProtos.CodeGeneratorResponse.File, PluginProtos.CodeGeneratorResponse.File.Builder, PluginProtos.CodeGeneratorResponse.FileOrBuilder> fileBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.class, PluginProtos.CodeGeneratorResponse.Builder.class);
         }

         private Builder() {
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
         }

         public PluginProtos.CodeGeneratorResponse.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.error_ = "";
            this.supportedFeatures_ = 0L;
            this.minimumEdition_ = 0;
            this.maximumEdition_ = 0;
            if (this.fileBuilder_ == null) {
               this.file_ = Collections.emptyList();
            } else {
               this.file_ = null;
               this.fileBuilder_.clear();
            }

            this.bitField0_ &= -17;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
         }

         public PluginProtos.CodeGeneratorResponse getDefaultInstanceForType() {
            return PluginProtos.CodeGeneratorResponse.getDefaultInstance();
         }

         public PluginProtos.CodeGeneratorResponse build() {
            PluginProtos.CodeGeneratorResponse result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public PluginProtos.CodeGeneratorResponse buildPartial() {
            PluginProtos.CodeGeneratorResponse result = new PluginProtos.CodeGeneratorResponse(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartialRepeatedFields(PluginProtos.CodeGeneratorResponse result) {
            if (this.fileBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0) {
                  this.file_ = Collections.unmodifiableList(this.file_);
                  this.bitField0_ &= -17;
               }

               result.file_ = this.file_;
            } else {
               result.file_ = this.fileBuilder_.build();
            }
         }

         private void buildPartial0(PluginProtos.CodeGeneratorResponse result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.error_ = this.error_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.supportedFeatures_ = this.supportedFeatures_;
               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               result.minimumEdition_ = this.minimumEdition_;
               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               result.maximumEdition_ = this.maximumEdition_;
               to_bitField0_ |= 8;
            }

            result.bitField0_ |= to_bitField0_;
         }

         public PluginProtos.CodeGeneratorResponse.Builder mergeFrom(Message other) {
            if (other instanceof PluginProtos.CodeGeneratorResponse) {
               return this.mergeFrom((PluginProtos.CodeGeneratorResponse)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder mergeFrom(PluginProtos.CodeGeneratorResponse other) {
            if (other == PluginProtos.CodeGeneratorResponse.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasError()) {
                  this.error_ = other.error_;
                  this.bitField0_ |= 1;
                  this.onChanged();
               }

               if (other.hasSupportedFeatures()) {
                  this.setSupportedFeatures(other.getSupportedFeatures());
               }

               if (other.hasMinimumEdition()) {
                  this.setMinimumEdition(other.getMinimumEdition());
               }

               if (other.hasMaximumEdition()) {
                  this.setMaximumEdition(other.getMaximumEdition());
               }

               if (this.fileBuilder_ == null) {
                  if (!other.file_.isEmpty()) {
                     if (this.file_.isEmpty()) {
                        this.file_ = other.file_;
                        this.bitField0_ &= -17;
                     } else {
                        this.ensureFileIsMutable();
                        this.file_.addAll(other.file_);
                     }

                     this.onChanged();
                  }
               } else if (!other.file_.isEmpty()) {
                  if (this.fileBuilder_.isEmpty()) {
                     this.fileBuilder_.dispose();
                     this.fileBuilder_ = null;
                     this.file_ = other.file_;
                     this.bitField0_ &= -17;
                     this.fileBuilder_ = PluginProtos.CodeGeneratorResponse.alwaysUseFieldBuilders ? this.internalGetFileFieldBuilder() : null;
                  } else {
                     this.fileBuilder_.addAllMessages(other.file_);
                  }
               }

               this.mergeUnknownFields(other.getUnknownFields());
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return true;
         }

         public PluginProtos.CodeGeneratorResponse.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               try {
                  boolean done = false;

                  while (!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 10:
                           this.error_ = input.readBytes();
                           this.bitField0_ |= 1;
                           break;
                        case 16:
                           this.supportedFeatures_ = input.readUInt64();
                           this.bitField0_ |= 2;
                           break;
                        case 24:
                           this.minimumEdition_ = input.readInt32();
                           this.bitField0_ |= 4;
                           break;
                        case 32:
                           this.maximumEdition_ = input.readInt32();
                           this.bitField0_ |= 8;
                           break;
                        case 122:
                           PluginProtos.CodeGeneratorResponse.File m = input.readMessage(PluginProtos.CodeGeneratorResponse.File.parser(), extensionRegistry);
                           if (this.fileBuilder_ == null) {
                              this.ensureFileIsMutable();
                              this.file_.add(m);
                           } else {
                              this.fileBuilder_.addMessage(m);
                           }
                           break;
                        default:
                           if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var9) {
                  throw var9.unwrapIOException();
               } finally {
                  this.onChanged();
               }

               return this;
            }
         }

         @Override
         public boolean hasError() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getError() {
            Object ref = this.error_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.error_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getErrorBytes() {
            Object ref = this.error_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.error_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder setError(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.error_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearError() {
            this.error_ = PluginProtos.CodeGeneratorResponse.getDefaultInstance().getError();
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setErrorBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.error_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasSupportedFeatures() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public long getSupportedFeatures() {
            return this.supportedFeatures_;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setSupportedFeatures(long value) {
            this.supportedFeatures_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearSupportedFeatures() {
            this.bitField0_ &= -3;
            this.supportedFeatures_ = 0L;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasMinimumEdition() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public int getMinimumEdition() {
            return this.minimumEdition_;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setMinimumEdition(int value) {
            this.minimumEdition_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearMinimumEdition() {
            this.bitField0_ &= -5;
            this.minimumEdition_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasMaximumEdition() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public int getMaximumEdition() {
            return this.maximumEdition_;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setMaximumEdition(int value) {
            this.maximumEdition_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearMaximumEdition() {
            this.bitField0_ &= -9;
            this.maximumEdition_ = 0;
            this.onChanged();
            return this;
         }

         private void ensureFileIsMutable() {
            if ((this.bitField0_ & 16) == 0) {
               this.file_ = new ArrayList<>(this.file_);
               this.bitField0_ |= 16;
            }
         }

         @Override
         public List<PluginProtos.CodeGeneratorResponse.File> getFileList() {
            return this.fileBuilder_ == null ? Collections.unmodifiableList(this.file_) : this.fileBuilder_.getMessageList();
         }

         @Override
         public int getFileCount() {
            return this.fileBuilder_ == null ? this.file_.size() : this.fileBuilder_.getCount();
         }

         @Override
         public PluginProtos.CodeGeneratorResponse.File getFile(int index) {
            return this.fileBuilder_ == null ? this.file_.get(index) : this.fileBuilder_.getMessage(index);
         }

         public PluginProtos.CodeGeneratorResponse.Builder setFile(int index, PluginProtos.CodeGeneratorResponse.File value) {
            if (this.fileBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFileIsMutable();
               this.file_.set(index, value);
               this.onChanged();
            } else {
               this.fileBuilder_.setMessage(index, value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setFile(int index, PluginProtos.CodeGeneratorResponse.File.Builder builderForValue) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.fileBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(PluginProtos.CodeGeneratorResponse.File value) {
            if (this.fileBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFileIsMutable();
               this.file_.add(value);
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(int index, PluginProtos.CodeGeneratorResponse.File value) {
            if (this.fileBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFileIsMutable();
               this.file_.add(index, value);
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(index, value);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(PluginProtos.CodeGeneratorResponse.File.Builder builderForValue) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(int index, PluginProtos.CodeGeneratorResponse.File.Builder builderForValue) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addAllFile(Iterable<? extends PluginProtos.CodeGeneratorResponse.File> values) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.file_);
               this.onChanged();
            } else {
               this.fileBuilder_.addAllMessages(values);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearFile() {
            if (this.fileBuilder_ == null) {
               this.file_ = Collections.emptyList();
               this.bitField0_ &= -17;
               this.onChanged();
            } else {
               this.fileBuilder_.clear();
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder removeFile(int index) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.remove(index);
               this.onChanged();
            } else {
               this.fileBuilder_.remove(index);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder getFileBuilder(int index) {
            return this.internalGetFileFieldBuilder().getBuilder(index);
         }

         @Override
         public PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int index) {
            return this.fileBuilder_ == null ? this.file_.get(index) : this.fileBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList() {
            return this.fileBuilder_ != null ? this.fileBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.file_);
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder addFileBuilder() {
            return this.internalGetFileFieldBuilder().addBuilder(PluginProtos.CodeGeneratorResponse.File.getDefaultInstance());
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder addFileBuilder(int index) {
            return this.internalGetFileFieldBuilder().addBuilder(index, PluginProtos.CodeGeneratorResponse.File.getDefaultInstance());
         }

         public List<PluginProtos.CodeGeneratorResponse.File.Builder> getFileBuilderList() {
            return this.internalGetFileFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilder<PluginProtos.CodeGeneratorResponse.File, PluginProtos.CodeGeneratorResponse.File.Builder, PluginProtos.CodeGeneratorResponse.FileOrBuilder> internalGetFileFieldBuilder() {
            if (this.fileBuilder_ == null) {
               this.fileBuilder_ = new RepeatedFieldBuilder<>(this.file_, (this.bitField0_ & 16) != 0, this.getParentForChildren(), this.isClean());
               this.file_ = null;
            }

            return this.fileBuilder_;
         }
      }

      public static enum Feature implements ProtocolMessageEnum {
         FEATURE_NONE(0),
         FEATURE_PROTO3_OPTIONAL(1),
         FEATURE_SUPPORTS_EDITIONS(2);

         public static final int FEATURE_NONE_VALUE = 0;
         public static final int FEATURE_PROTO3_OPTIONAL_VALUE = 1;
         public static final int FEATURE_SUPPORTS_EDITIONS_VALUE = 2;
         private static final Internal.EnumLiteMap<PluginProtos.CodeGeneratorResponse.Feature> internalValueMap = new Internal.EnumLiteMap<PluginProtos.CodeGeneratorResponse.Feature>() {
            public PluginProtos.CodeGeneratorResponse.Feature findValueByNumber(int number) {
               return PluginProtos.CodeGeneratorResponse.Feature.forNumber(number);
            }
         };
         private static final PluginProtos.CodeGeneratorResponse.Feature[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static PluginProtos.CodeGeneratorResponse.Feature valueOf(int value) {
            return forNumber(value);
         }

         public static PluginProtos.CodeGeneratorResponse.Feature forNumber(int value) {
            switch (value) {
               case 0:
                  return FEATURE_NONE;
               case 1:
                  return FEATURE_PROTO3_OPTIONAL;
               case 2:
                  return FEATURE_SUPPORTS_EDITIONS;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<PluginProtos.CodeGeneratorResponse.Feature> internalGetValueMap() {
            return internalValueMap;
         }

         @Override
         public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(this.ordinal());
         }

         @Override
         public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
         }

         public static Descriptors.EnumDescriptor getDescriptor() {
            return PluginProtos.CodeGeneratorResponse.getDescriptor().getEnumTypes().get(0);
         }

         public static PluginProtos.CodeGeneratorResponse.Feature valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Feature(int value) {
            this.value = value;
         }

         static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Feature");
         }
      }

      public static final class File extends GeneratedMessage implements PluginProtos.CodeGeneratorResponse.FileOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int NAME_FIELD_NUMBER = 1;
         private volatile Object name_ = "";
         public static final int INSERTION_POINT_FIELD_NUMBER = 2;
         private volatile Object insertionPoint_ = "";
         public static final int CONTENT_FIELD_NUMBER = 15;
         private volatile Object content_ = "";
         public static final int GENERATED_CODE_INFO_FIELD_NUMBER = 16;
         private DescriptorProtos.GeneratedCodeInfo generatedCodeInfo_;
         private byte memoizedIsInitialized = -1;
         private static final PluginProtos.CodeGeneratorResponse.File DEFAULT_INSTANCE = new PluginProtos.CodeGeneratorResponse.File();
         private static final Parser<PluginProtos.CodeGeneratorResponse.File> PARSER = new AbstractParser<PluginProtos.CodeGeneratorResponse.File>() {
            public PluginProtos.CodeGeneratorResponse.File parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               PluginProtos.CodeGeneratorResponse.File.Builder builder = PluginProtos.CodeGeneratorResponse.File.newBuilder();

               try {
                  builder.mergeFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var5) {
                  throw var5.setUnfinishedMessage(builder.buildPartial());
               } catch (UninitializedMessageException var6) {
                  throw var6.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
               } catch (IOException var7) {
                  throw new InvalidProtocolBufferException(var7).setUnfinishedMessage(builder.buildPartial());
               }

               return builder.buildPartial();
            }
         };

         private File(GeneratedMessage.Builder<?> builder) {
            super(builder);
         }

         private File() {
            this.name_ = "";
            this.insertionPoint_ = "";
            this.content_ = "";
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.File.class, PluginProtos.CodeGeneratorResponse.File.Builder.class);
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getName() {
            Object ref = this.name_;
            if (ref instanceof String) {
               return (String)ref;
            } else {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            }
         }

         @Override
         public ByteString getNameBytes() {
            Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         @Override
         public boolean hasInsertionPoint() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getInsertionPoint() {
            Object ref = this.insertionPoint_;
            if (ref instanceof String) {
               return (String)ref;
            } else {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.insertionPoint_ = s;
               }

               return s;
            }
         }

         @Override
         public ByteString getInsertionPointBytes() {
            Object ref = this.insertionPoint_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.insertionPoint_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         @Override
         public boolean hasContent() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public String getContent() {
            Object ref = this.content_;
            if (ref instanceof String) {
               return (String)ref;
            } else {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.content_ = s;
               }

               return s;
            }
         }

         @Override
         public ByteString getContentBytes() {
            Object ref = this.content_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.content_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         @Override
         public boolean hasGeneratedCodeInfo() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public DescriptorProtos.GeneratedCodeInfo getGeneratedCodeInfo() {
            return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
         }

         @Override
         public DescriptorProtos.GeneratedCodeInfoOrBuilder getGeneratedCodeInfoOrBuilder() {
            return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
         }

         @Override
         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         @Override
         public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
               GeneratedMessage.writeString(output, 1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               GeneratedMessage.writeString(output, 2, this.insertionPoint_);
            }

            if ((this.bitField0_ & 4) != 0) {
               GeneratedMessage.writeString(output, 15, this.content_);
            }

            if ((this.bitField0_ & 8) != 0) {
               output.writeMessage(16, this.getGeneratedCodeInfo());
            }

            this.getUnknownFields().writeTo(output);
         }

         @Override
         public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
               return size;
            } else {
               size = 0;
               if ((this.bitField0_ & 1) != 0) {
                  size += GeneratedMessage.computeStringSize(1, this.name_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  size += GeneratedMessage.computeStringSize(2, this.insertionPoint_);
               }

               if ((this.bitField0_ & 4) != 0) {
                  size += GeneratedMessage.computeStringSize(15, this.content_);
               }

               if ((this.bitField0_ & 8) != 0) {
                  size += CodedOutputStream.computeMessageSize(16, this.getGeneratedCodeInfo());
               }

               size += this.getUnknownFields().getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         @Override
         public boolean equals(final Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof PluginProtos.CodeGeneratorResponse.File)) {
               return super.equals(obj);
            } else {
               PluginProtos.CodeGeneratorResponse.File other = (PluginProtos.CodeGeneratorResponse.File)obj;
               if (this.hasName() != other.hasName()) {
                  return false;
               } else if (this.hasName() && !this.getName().equals(other.getName())) {
                  return false;
               } else if (this.hasInsertionPoint() != other.hasInsertionPoint()) {
                  return false;
               } else if (this.hasInsertionPoint() && !this.getInsertionPoint().equals(other.getInsertionPoint())) {
                  return false;
               } else if (this.hasContent() != other.hasContent()) {
                  return false;
               } else if (this.hasContent() && !this.getContent().equals(other.getContent())) {
                  return false;
               } else if (this.hasGeneratedCodeInfo() != other.hasGeneratedCodeInfo()) {
                  return false;
               } else {
                  return this.hasGeneratedCodeInfo() && !this.getGeneratedCodeInfo().equals(other.getGeneratedCodeInfo())
                     ? false
                     : this.getUnknownFields().equals(other.getUnknownFields());
               }
            }
         }

         @Override
         public int hashCode() {
            if (this.memoizedHashCode != 0) {
               return this.memoizedHashCode;
            } else {
               int hash = 41;
               hash = 19 * hash + getDescriptor().hashCode();
               if (this.hasName()) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getName().hashCode();
               }

               if (this.hasInsertionPoint()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getInsertionPoint().hashCode();
               }

               if (this.hasContent()) {
                  hash = 37 * hash + 15;
                  hash = 53 * hash + this.getContent().hashCode();
               }

               if (this.hasGeneratedCodeInfo()) {
                  hash = 37 * hash + 16;
                  hash = 53 * hash + this.getGeneratedCodeInfo().hashCode();
               }

               hash = 29 * hash + this.getUnknownFields().hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder newBuilderForType() {
            return newBuilder();
         }

         public static PluginProtos.CodeGeneratorResponse.File.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static PluginProtos.CodeGeneratorResponse.File.Builder newBuilder(PluginProtos.CodeGeneratorResponse.File prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder toBuilder() {
            return this == DEFAULT_INSTANCE
               ? new PluginProtos.CodeGeneratorResponse.File.Builder()
               : new PluginProtos.CodeGeneratorResponse.File.Builder().mergeFrom(this);
         }

         protected PluginProtos.CodeGeneratorResponse.File.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
            return new PluginProtos.CodeGeneratorResponse.File.Builder(parent);
         }

         public static PluginProtos.CodeGeneratorResponse.File getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<PluginProtos.CodeGeneratorResponse.File> parser() {
            return PARSER;
         }

         @Override
         public Parser<PluginProtos.CodeGeneratorResponse.File> getParserForType() {
            return PARSER;
         }

         public PluginProtos.CodeGeneratorResponse.File getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "File");
         }

         public static final class Builder
            extends GeneratedMessage.Builder<PluginProtos.CodeGeneratorResponse.File.Builder>
            implements PluginProtos.CodeGeneratorResponse.FileOrBuilder {
            private int bitField0_;
            private Object name_ = "";
            private Object insertionPoint_ = "";
            private Object content_ = "";
            private DescriptorProtos.GeneratedCodeInfo generatedCodeInfo_;
            private SingleFieldBuilder<DescriptorProtos.GeneratedCodeInfo, DescriptorProtos.GeneratedCodeInfo.Builder, DescriptorProtos.GeneratedCodeInfoOrBuilder> generatedCodeInfoBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
               return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.File.class, PluginProtos.CodeGeneratorResponse.File.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(AbstractMessage.BuilderParent parent) {
               super(parent);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (PluginProtos.CodeGeneratorResponse.File.alwaysUseFieldBuilders) {
                  this.internalGetGeneratedCodeInfoFieldBuilder();
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clear() {
               super.clear();
               this.bitField0_ = 0;
               this.name_ = "";
               this.insertionPoint_ = "";
               this.content_ = "";
               this.generatedCodeInfo_ = null;
               if (this.generatedCodeInfoBuilder_ != null) {
                  this.generatedCodeInfoBuilder_.dispose();
                  this.generatedCodeInfoBuilder_ = null;
               }

               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
            }

            public PluginProtos.CodeGeneratorResponse.File getDefaultInstanceForType() {
               return PluginProtos.CodeGeneratorResponse.File.getDefaultInstance();
            }

            public PluginProtos.CodeGeneratorResponse.File build() {
               PluginProtos.CodeGeneratorResponse.File result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File buildPartial() {
               PluginProtos.CodeGeneratorResponse.File result = new PluginProtos.CodeGeneratorResponse.File(this);
               if (this.bitField0_ != 0) {
                  this.buildPartial0(result);
               }

               this.onBuilt();
               return result;
            }

            private void buildPartial0(PluginProtos.CodeGeneratorResponse.File result) {
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  result.name_ = this.name_;
                  to_bitField0_ |= 1;
               }

               if ((from_bitField0_ & 2) != 0) {
                  result.insertionPoint_ = this.insertionPoint_;
                  to_bitField0_ |= 2;
               }

               if ((from_bitField0_ & 4) != 0) {
                  result.content_ = this.content_;
                  to_bitField0_ |= 4;
               }

               if ((from_bitField0_ & 8) != 0) {
                  result.generatedCodeInfo_ = this.generatedCodeInfoBuilder_ == null ? this.generatedCodeInfo_ : this.generatedCodeInfoBuilder_.build();
                  to_bitField0_ |= 8;
               }

               result.bitField0_ |= to_bitField0_;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeFrom(Message other) {
               if (other instanceof PluginProtos.CodeGeneratorResponse.File) {
                  return this.mergeFrom((PluginProtos.CodeGeneratorResponse.File)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeFrom(PluginProtos.CodeGeneratorResponse.File other) {
               if (other == PluginProtos.CodeGeneratorResponse.File.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasName()) {
                     this.name_ = other.name_;
                     this.bitField0_ |= 1;
                     this.onChanged();
                  }

                  if (other.hasInsertionPoint()) {
                     this.insertionPoint_ = other.insertionPoint_;
                     this.bitField0_ |= 2;
                     this.onChanged();
                  }

                  if (other.hasContent()) {
                     this.content_ = other.content_;
                     this.bitField0_ |= 4;
                     this.onChanged();
                  }

                  if (other.hasGeneratedCodeInfo()) {
                     this.mergeGeneratedCodeInfo(other.getGeneratedCodeInfo());
                  }

                  this.mergeUnknownFields(other.getUnknownFields());
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public final boolean isInitialized() {
               return true;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               if (extensionRegistry == null) {
                  throw new NullPointerException();
               } else {
                  try {
                     boolean done = false;

                     while (!done) {
                        int tag = input.readTag();
                        switch (tag) {
                           case 0:
                              done = true;
                              break;
                           case 10:
                              this.name_ = input.readBytes();
                              this.bitField0_ |= 1;
                              break;
                           case 18:
                              this.insertionPoint_ = input.readBytes();
                              this.bitField0_ |= 2;
                              break;
                           case 122:
                              this.content_ = input.readBytes();
                              this.bitField0_ |= 4;
                              break;
                           case 130:
                              input.readMessage(this.internalGetGeneratedCodeInfoFieldBuilder().getBuilder(), extensionRegistry);
                              this.bitField0_ |= 8;
                              break;
                           default:
                              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                                 done = true;
                              }
                        }
                     }
                  } catch (InvalidProtocolBufferException var8) {
                     throw var8.unwrapIOException();
                  } finally {
                     this.onChanged();
                  }

                  return this;
               }
            }

            @Override
            public boolean hasName() {
               return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getName() {
               Object ref = this.name_;
               if (!(ref instanceof String)) {
                  ByteString bs = (ByteString)ref;
                  String s = bs.toStringUtf8();
                  if (bs.isValidUtf8()) {
                     this.name_ = s;
                  }

                  return s;
               } else {
                  return (String)ref;
               }
            }

            @Override
            public ByteString getNameBytes() {
               Object ref = this.name_;
               if (ref instanceof String) {
                  ByteString b = ByteString.copyFromUtf8((String)ref);
                  this.name_ = b;
                  return b;
               } else {
                  return (ByteString)ref;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setName(String value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.name_ = value;
                  this.bitField0_ |= 1;
                  this.onChanged();
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearName() {
               this.name_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getName();
               this.bitField0_ &= -2;
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setNameBytes(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.name_ = value;
                  this.bitField0_ |= 1;
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public boolean hasInsertionPoint() {
               return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getInsertionPoint() {
               Object ref = this.insertionPoint_;
               if (!(ref instanceof String)) {
                  ByteString bs = (ByteString)ref;
                  String s = bs.toStringUtf8();
                  if (bs.isValidUtf8()) {
                     this.insertionPoint_ = s;
                  }

                  return s;
               } else {
                  return (String)ref;
               }
            }

            @Override
            public ByteString getInsertionPointBytes() {
               Object ref = this.insertionPoint_;
               if (ref instanceof String) {
                  ByteString b = ByteString.copyFromUtf8((String)ref);
                  this.insertionPoint_ = b;
                  return b;
               } else {
                  return (ByteString)ref;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setInsertionPoint(String value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.insertionPoint_ = value;
                  this.bitField0_ |= 2;
                  this.onChanged();
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearInsertionPoint() {
               this.insertionPoint_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getInsertionPoint();
               this.bitField0_ &= -3;
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setInsertionPointBytes(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.insertionPoint_ = value;
                  this.bitField0_ |= 2;
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public boolean hasContent() {
               return (this.bitField0_ & 4) != 0;
            }

            @Override
            public String getContent() {
               Object ref = this.content_;
               if (!(ref instanceof String)) {
                  ByteString bs = (ByteString)ref;
                  String s = bs.toStringUtf8();
                  if (bs.isValidUtf8()) {
                     this.content_ = s;
                  }

                  return s;
               } else {
                  return (String)ref;
               }
            }

            @Override
            public ByteString getContentBytes() {
               Object ref = this.content_;
               if (ref instanceof String) {
                  ByteString b = ByteString.copyFromUtf8((String)ref);
                  this.content_ = b;
                  return b;
               } else {
                  return (ByteString)ref;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setContent(String value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.content_ = value;
                  this.bitField0_ |= 4;
                  this.onChanged();
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearContent() {
               this.content_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getContent();
               this.bitField0_ &= -5;
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setContentBytes(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.content_ = value;
                  this.bitField0_ |= 4;
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public boolean hasGeneratedCodeInfo() {
               return (this.bitField0_ & 8) != 0;
            }

            @Override
            public DescriptorProtos.GeneratedCodeInfo getGeneratedCodeInfo() {
               if (this.generatedCodeInfoBuilder_ == null) {
                  return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
               } else {
                  return this.generatedCodeInfoBuilder_.getMessage();
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setGeneratedCodeInfo(DescriptorProtos.GeneratedCodeInfo value) {
               if (this.generatedCodeInfoBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.generatedCodeInfo_ = value;
               } else {
                  this.generatedCodeInfoBuilder_.setMessage(value);
               }

               this.bitField0_ |= 8;
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setGeneratedCodeInfo(DescriptorProtos.GeneratedCodeInfo.Builder builderForValue) {
               if (this.generatedCodeInfoBuilder_ == null) {
                  this.generatedCodeInfo_ = builderForValue.build();
               } else {
                  this.generatedCodeInfoBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 8;
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeGeneratedCodeInfo(DescriptorProtos.GeneratedCodeInfo value) {
               if (this.generatedCodeInfoBuilder_ == null) {
                  if ((this.bitField0_ & 8) != 0
                     && this.generatedCodeInfo_ != null
                     && this.generatedCodeInfo_ != DescriptorProtos.GeneratedCodeInfo.getDefaultInstance()) {
                     this.getGeneratedCodeInfoBuilder().mergeFrom(value);
                  } else {
                     this.generatedCodeInfo_ = value;
                  }
               } else {
                  this.generatedCodeInfoBuilder_.mergeFrom(value);
               }

               if (this.generatedCodeInfo_ != null) {
                  this.bitField0_ |= 8;
                  this.onChanged();
               }

               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearGeneratedCodeInfo() {
               this.bitField0_ &= -9;
               this.generatedCodeInfo_ = null;
               if (this.generatedCodeInfoBuilder_ != null) {
                  this.generatedCodeInfoBuilder_.dispose();
                  this.generatedCodeInfoBuilder_ = null;
               }

               this.onChanged();
               return this;
            }

            public DescriptorProtos.GeneratedCodeInfo.Builder getGeneratedCodeInfoBuilder() {
               this.bitField0_ |= 8;
               this.onChanged();
               return this.internalGetGeneratedCodeInfoFieldBuilder().getBuilder();
            }

            @Override
            public DescriptorProtos.GeneratedCodeInfoOrBuilder getGeneratedCodeInfoOrBuilder() {
               if (this.generatedCodeInfoBuilder_ != null) {
                  return this.generatedCodeInfoBuilder_.getMessageOrBuilder();
               } else {
                  return this.generatedCodeInfo_ == null ? DescriptorProtos.GeneratedCodeInfo.getDefaultInstance() : this.generatedCodeInfo_;
               }
            }

            private SingleFieldBuilder<DescriptorProtos.GeneratedCodeInfo, DescriptorProtos.GeneratedCodeInfo.Builder, DescriptorProtos.GeneratedCodeInfoOrBuilder> internalGetGeneratedCodeInfoFieldBuilder() {
               if (this.generatedCodeInfoBuilder_ == null) {
                  this.generatedCodeInfoBuilder_ = new SingleFieldBuilder<>(this.getGeneratedCodeInfo(), this.getParentForChildren(), this.isClean());
                  this.generatedCodeInfo_ = null;
               }

               return this.generatedCodeInfoBuilder_;
            }
         }
      }

      public interface FileOrBuilder extends MessageOrBuilder {
         boolean hasName();

         String getName();

         ByteString getNameBytes();

         boolean hasInsertionPoint();

         String getInsertionPoint();

         ByteString getInsertionPointBytes();

         boolean hasContent();

         String getContent();

         ByteString getContentBytes();

         boolean hasGeneratedCodeInfo();

         DescriptorProtos.GeneratedCodeInfo getGeneratedCodeInfo();

         DescriptorProtos.GeneratedCodeInfoOrBuilder getGeneratedCodeInfoOrBuilder();
      }
   }

   public interface CodeGeneratorResponseOrBuilder extends MessageOrBuilder {
      boolean hasError();

      String getError();

      ByteString getErrorBytes();

      boolean hasSupportedFeatures();

      long getSupportedFeatures();

      boolean hasMinimumEdition();

      int getMinimumEdition();

      boolean hasMaximumEdition();

      int getMaximumEdition();

      List<PluginProtos.CodeGeneratorResponse.File> getFileList();

      PluginProtos.CodeGeneratorResponse.File getFile(int index);

      int getFileCount();

      List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList();

      PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int index);
   }

   public static final class Version extends GeneratedMessage implements PluginProtos.VersionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int MAJOR_FIELD_NUMBER = 1;
      private int major_ = 0;
      public static final int MINOR_FIELD_NUMBER = 2;
      private int minor_ = 0;
      public static final int PATCH_FIELD_NUMBER = 3;
      private int patch_ = 0;
      public static final int SUFFIX_FIELD_NUMBER = 4;
      private volatile Object suffix_ = "";
      private byte memoizedIsInitialized = -1;
      private static final PluginProtos.Version DEFAULT_INSTANCE = new PluginProtos.Version();
      private static final Parser<PluginProtos.Version> PARSER = new AbstractParser<PluginProtos.Version>() {
         public PluginProtos.Version parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            PluginProtos.Version.Builder builder = PluginProtos.Version.newBuilder();

            try {
               builder.mergeFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var5) {
               throw var5.setUnfinishedMessage(builder.buildPartial());
            } catch (UninitializedMessageException var6) {
               throw var6.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
            } catch (IOException var7) {
               throw new InvalidProtocolBufferException(var7).setUnfinishedMessage(builder.buildPartial());
            }

            return builder.buildPartial();
         }
      };

      private Version(GeneratedMessage.Builder<?> builder) {
         super(builder);
      }

      private Version() {
         this.suffix_ = "";
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return PluginProtos.internal_static_google_protobuf_compiler_Version_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PluginProtos.Version.class, PluginProtos.Version.Builder.class);
      }

      @Override
      public boolean hasMajor() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getMajor() {
         return this.major_;
      }

      @Override
      public boolean hasMinor() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public int getMinor() {
         return this.minor_;
      }

      @Override
      public boolean hasPatch() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public int getPatch() {
         return this.patch_;
      }

      @Override
      public boolean hasSuffix() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public String getSuffix() {
         Object ref = this.suffix_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.suffix_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getSuffixBytes() {
         Object ref = this.suffix_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.suffix_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeInt32(1, this.major_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeInt32(2, this.minor_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeInt32(3, this.patch_);
         }

         if ((this.bitField0_ & 8) != 0) {
            GeneratedMessage.writeString(output, 4, this.suffix_);
         }

         this.getUnknownFields().writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeInt32Size(1, this.major_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeInt32Size(2, this.minor_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeInt32Size(3, this.patch_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += GeneratedMessage.computeStringSize(4, this.suffix_);
            }

            size += this.getUnknownFields().getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(final Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof PluginProtos.Version)) {
            return super.equals(obj);
         } else {
            PluginProtos.Version other = (PluginProtos.Version)obj;
            if (this.hasMajor() != other.hasMajor()) {
               return false;
            } else if (this.hasMajor() && this.getMajor() != other.getMajor()) {
               return false;
            } else if (this.hasMinor() != other.hasMinor()) {
               return false;
            } else if (this.hasMinor() && this.getMinor() != other.getMinor()) {
               return false;
            } else if (this.hasPatch() != other.hasPatch()) {
               return false;
            } else if (this.hasPatch() && this.getPatch() != other.getPatch()) {
               return false;
            } else if (this.hasSuffix() != other.hasSuffix()) {
               return false;
            } else {
               return this.hasSuffix() && !this.getSuffix().equals(other.getSuffix()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasMajor()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getMajor();
            }

            if (this.hasMinor()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getMinor();
            }

            if (this.hasPatch()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getPatch();
            }

            if (this.hasSuffix()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getSuffix().hashCode();
            }

            hash = 29 * hash + this.getUnknownFields().hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static PluginProtos.Version parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.Version parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.Version parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.Version parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.Version parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static PluginProtos.Version parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static PluginProtos.Version parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static PluginProtos.Version parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static PluginProtos.Version parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static PluginProtos.Version parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static PluginProtos.Version parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static PluginProtos.Version parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public PluginProtos.Version.Builder newBuilderForType() {
         return newBuilder();
      }

      public static PluginProtos.Version.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static PluginProtos.Version.Builder newBuilder(PluginProtos.Version prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public PluginProtos.Version.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new PluginProtos.Version.Builder() : new PluginProtos.Version.Builder().mergeFrom(this);
      }

      protected PluginProtos.Version.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new PluginProtos.Version.Builder(parent);
      }

      public static PluginProtos.Version getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<PluginProtos.Version> parser() {
         return PARSER;
      }

      @Override
      public Parser<PluginProtos.Version> getParserForType() {
         return PARSER;
      }

      public PluginProtos.Version getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Version");
      }

      public static final class Builder extends GeneratedMessage.Builder<PluginProtos.Version.Builder> implements PluginProtos.VersionOrBuilder {
         private int bitField0_;
         private int major_;
         private int minor_;
         private int patch_;
         private Object suffix_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_Version_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.Version.class, PluginProtos.Version.Builder.class);
         }

         private Builder() {
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
         }

         public PluginProtos.Version.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.major_ = 0;
            this.minor_ = 0;
            this.patch_ = 0;
            this.suffix_ = "";
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
         }

         public PluginProtos.Version getDefaultInstanceForType() {
            return PluginProtos.Version.getDefaultInstance();
         }

         public PluginProtos.Version build() {
            PluginProtos.Version result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public PluginProtos.Version buildPartial() {
            PluginProtos.Version result = new PluginProtos.Version(this);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartial0(PluginProtos.Version result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.major_ = this.major_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.minor_ = this.minor_;
               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               result.patch_ = this.patch_;
               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               result.suffix_ = this.suffix_;
               to_bitField0_ |= 8;
            }

            result.bitField0_ |= to_bitField0_;
         }

         public PluginProtos.Version.Builder mergeFrom(Message other) {
            if (other instanceof PluginProtos.Version) {
               return this.mergeFrom((PluginProtos.Version)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public PluginProtos.Version.Builder mergeFrom(PluginProtos.Version other) {
            if (other == PluginProtos.Version.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasMajor()) {
                  this.setMajor(other.getMajor());
               }

               if (other.hasMinor()) {
                  this.setMinor(other.getMinor());
               }

               if (other.hasPatch()) {
                  this.setPatch(other.getPatch());
               }

               if (other.hasSuffix()) {
                  this.suffix_ = other.suffix_;
                  this.bitField0_ |= 8;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.getUnknownFields());
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return true;
         }

         public PluginProtos.Version.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               try {
                  boolean done = false;

                  while (!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 8:
                           this.major_ = input.readInt32();
                           this.bitField0_ |= 1;
                           break;
                        case 16:
                           this.minor_ = input.readInt32();
                           this.bitField0_ |= 2;
                           break;
                        case 24:
                           this.patch_ = input.readInt32();
                           this.bitField0_ |= 4;
                           break;
                        case 34:
                           this.suffix_ = input.readBytes();
                           this.bitField0_ |= 8;
                           break;
                        default:
                           if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var8) {
                  throw var8.unwrapIOException();
               } finally {
                  this.onChanged();
               }

               return this;
            }
         }

         @Override
         public boolean hasMajor() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getMajor() {
            return this.major_;
         }

         public PluginProtos.Version.Builder setMajor(int value) {
            this.major_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder clearMajor() {
            this.bitField0_ &= -2;
            this.major_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasMinor() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getMinor() {
            return this.minor_;
         }

         public PluginProtos.Version.Builder setMinor(int value) {
            this.minor_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder clearMinor() {
            this.bitField0_ &= -3;
            this.minor_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasPatch() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public int getPatch() {
            return this.patch_;
         }

         public PluginProtos.Version.Builder setPatch(int value) {
            this.patch_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder clearPatch() {
            this.bitField0_ &= -5;
            this.patch_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasSuffix() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public String getSuffix() {
            Object ref = this.suffix_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.suffix_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getSuffixBytes() {
            Object ref = this.suffix_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.suffix_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public PluginProtos.Version.Builder setSuffix(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.suffix_ = value;
               this.bitField0_ |= 8;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.Version.Builder clearSuffix() {
            this.suffix_ = PluginProtos.Version.getDefaultInstance().getSuffix();
            this.bitField0_ &= -9;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder setSuffixBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.suffix_ = value;
               this.bitField0_ |= 8;
               this.onChanged();
               return this;
            }
         }
      }
   }

   public interface VersionOrBuilder extends MessageOrBuilder {
      boolean hasMajor();

      int getMajor();

      boolean hasMinor();

      int getMinor();

      boolean hasPatch();

      int getPatch();

      boolean hasSuffix();

      String getSuffix();

      ByteString getSuffixBytes();
   }
}
