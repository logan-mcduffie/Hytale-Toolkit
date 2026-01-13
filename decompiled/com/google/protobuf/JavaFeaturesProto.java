package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class JavaFeaturesProto extends GeneratedFile {
   public static final int JAVA_FIELD_NUMBER = 1001;
   public static final GeneratedMessage.GeneratedExtension<DescriptorProtos.FeatureSet, JavaFeaturesProto.JavaFeatures> java_ = GeneratedMessage.newFileScopedGeneratedExtension(
      JavaFeaturesProto.JavaFeatures.class, JavaFeaturesProto.JavaFeatures.getDefaultInstance()
   );
   private static final Descriptors.Descriptor internal_static_pb_JavaFeatures_descriptor = getDescriptor().getMessageType(0);
   private static final GeneratedMessage.FieldAccessorTable internal_static_pb_JavaFeatures_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_pb_JavaFeatures_descriptor,
      new String[]{"LegacyClosedEnum", "Utf8Validation", "LargeEnum", "UseOldOuterClassnameDefault", "NestInFileClass"}
   );
   private static final Descriptors.Descriptor internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor = internal_static_pb_JavaFeatures_descriptor.getNestedType(
      0
   );
   private static final GeneratedMessage.FieldAccessorTable internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(
      internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor, new String[0]
   );
   private static Descriptors.FileDescriptor descriptor;

   private JavaFeaturesProto() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
      registry.add(java_);
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "JavaFeaturesProto");
      String[] descriptorData = new String[]{
         "\n#google/protobuf/java_features.proto\u0012\u0002pb\u001a google/protobuf/descriptor.proto\"ß\b\n\fJavaFeatures\u0012\u0090\u0002\n\u0012legacy_closed_enum\u0018\u0001 \u0001(\bBá\u0001\u0088\u0001\u0001\u0098\u0001\u0004\u0098\u0001\u0001¢\u0001\t\u0012\u0004true\u0018\u0084\u0007¢\u0001\n\u0012\u0005false\u0018ç\u0007²\u0001»\u0001\bè\u0007\u0010è\u0007\u001a²\u0001The legacy closed enum behavior in Java is deprecated and is scheduled to be removed in edition 2025.  See http://protobuf.dev/programming-guides/enum/#java for more information.R\u0010legacyClosedEnum\u0012¯\u0002\n\u000futf8_validation\u0018\u0002 \u0001(\u000e2\u001f.pb.JavaFeatures.Utf8ValidationBä\u0001\u0088\u0001\u0001\u0098\u0001\u0004\u0098\u0001\u0001¢\u0001\f\u0012\u0007DEFAULT\u0018\u0084\u0007²\u0001È\u0001\bè\u0007\u0010é\u0007\u001a¿\u0001The Java-specific utf8 validation feature is deprecated and is scheduled to be removed in edition 2025.  Utf8 validation behavior should use the global cross-language utf8_validation feature.R\u000eutf8Validation\u0012;\n\nlarge_enum\u0018\u0003 \u0001(\bB\u001c\u0088\u0001\u0001\u0098\u0001\u0006\u0098\u0001\u0001¢\u0001\n\u0012\u0005false\u0018\u0084\u0007²\u0001\u0003\bé\u0007R\tlargeEnum\u0012n\n\u001fuse_old_outer_classname_default\u0018\u0004 \u0001(\bB(\u0088\u0001\u0001\u0098\u0001\u0001¢\u0001\t\u0012\u0004true\u0018\u0084\u0007¢\u0001\n\u0012\u0005false\u0018é\u0007²\u0001\u0006\bé\u0007 é\u0007R\u001buseOldOuterClassnameDefault\u0012\u0090\u0001\n\u0012nest_in_file_class\u0018\u0005 \u0001(\u000e27.pb.JavaFeatures.NestInFileClassFeature.NestInFileClassB*\u0088\u0001\u0001\u0098\u0001\u0003\u0098\u0001\u0006\u0098\u0001\b¢\u0001\u000b\u0012\u0006LEGACY\u0018\u0084\u0007¢\u0001\u0007\u0012\u0002NO\u0018é\u0007²\u0001\u0003\bé\u0007R\u000fnestInFileClass\u001a|\n\u0016NestInFileClassFeature\"X\n\u000fNestInFileClass\u0012\u001e\n\u001aNEST_IN_FILE_CLASS_UNKNOWN\u0010\u0000\u0012\u0006\n\u0002NO\u0010\u0001\u0012\u0007\n\u0003YES\u0010\u0002\u0012\u0014\n\u0006LEGACY\u0010\u0003\u001a\b\"\u0006\bé\u0007 é\u0007J\b\b\u0001\u0010\u0080\u0080\u0080\u0080\u0002\"F\n\u000eUtf8Validation\u0012\u001b\n\u0017UTF8_VALIDATION_UNKNOWN\u0010\u0000\u0012\u000b\n\u0007DEFAULT\u0010\u0001\u0012\n\n\u0006VERIFY\u0010\u0002J\u0004\b\u0006\u0010\u0007:B\n\u0004java\u0012\u001b.google.protobuf.FeatureSet\u0018é\u0007 \u0001(\u000b2\u0010.pb.JavaFeaturesR\u0004javaB(\n\u0013com.google.protobufB\u0011JavaFeaturesProto"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
      java_.internalInit(descriptor.getExtension(0));
      descriptor.resolveAllFeaturesImmutable();
      DescriptorProtos.getDescriptor();
   }

   public static final class JavaFeatures extends GeneratedMessage implements JavaFeaturesProto.JavaFeaturesOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int LEGACY_CLOSED_ENUM_FIELD_NUMBER = 1;
      private boolean legacyClosedEnum_ = false;
      public static final int UTF8_VALIDATION_FIELD_NUMBER = 2;
      private int utf8Validation_ = 0;
      public static final int LARGE_ENUM_FIELD_NUMBER = 3;
      private boolean largeEnum_ = false;
      public static final int USE_OLD_OUTER_CLASSNAME_DEFAULT_FIELD_NUMBER = 4;
      private boolean useOldOuterClassnameDefault_ = false;
      public static final int NEST_IN_FILE_CLASS_FIELD_NUMBER = 5;
      private int nestInFileClass_ = 0;
      private byte memoizedIsInitialized = -1;
      private static final JavaFeaturesProto.JavaFeatures DEFAULT_INSTANCE = new JavaFeaturesProto.JavaFeatures();
      private static final Parser<JavaFeaturesProto.JavaFeatures> PARSER = new AbstractParser<JavaFeaturesProto.JavaFeatures>() {
         public JavaFeaturesProto.JavaFeatures parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            JavaFeaturesProto.JavaFeatures.Builder builder = JavaFeaturesProto.JavaFeatures.newBuilder();

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

      private JavaFeatures(GeneratedMessage.Builder<?> builder) {
         super(builder);
      }

      private JavaFeatures() {
         this.utf8Validation_ = 0;
         this.nestInFileClass_ = 0;
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return JavaFeaturesProto.internal_static_pb_JavaFeatures_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JavaFeaturesProto.internal_static_pb_JavaFeatures_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JavaFeaturesProto.JavaFeatures.class, JavaFeaturesProto.JavaFeatures.Builder.class);
      }

      @Override
      public boolean hasLegacyClosedEnum() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getLegacyClosedEnum() {
         return this.legacyClosedEnum_;
      }

      @Override
      public boolean hasUtf8Validation() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public JavaFeaturesProto.JavaFeatures.Utf8Validation getUtf8Validation() {
         JavaFeaturesProto.JavaFeatures.Utf8Validation result = JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(this.utf8Validation_);
         return result == null ? JavaFeaturesProto.JavaFeatures.Utf8Validation.UTF8_VALIDATION_UNKNOWN : result;
      }

      @Override
      public boolean hasLargeEnum() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public boolean getLargeEnum() {
         return this.largeEnum_;
      }

      @Override
      public boolean hasUseOldOuterClassnameDefault() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public boolean getUseOldOuterClassnameDefault() {
         return this.useOldOuterClassnameDefault_;
      }

      @Override
      public boolean hasNestInFileClass() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass getNestInFileClass() {
         JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass result = JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass.forNumber(
            this.nestInFileClass_
         );
         return result == null ? JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass.NEST_IN_FILE_CLASS_UNKNOWN : result;
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
            output.writeBool(1, this.legacyClosedEnum_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.utf8Validation_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBool(3, this.largeEnum_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeBool(4, this.useOldOuterClassnameDefault_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeEnum(5, this.nestInFileClass_);
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
               size += CodedOutputStream.computeBoolSize(1, this.legacyClosedEnum_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.utf8Validation_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBoolSize(3, this.largeEnum_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeBoolSize(4, this.useOldOuterClassnameDefault_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeEnumSize(5, this.nestInFileClass_);
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
         } else if (!(obj instanceof JavaFeaturesProto.JavaFeatures)) {
            return super.equals(obj);
         } else {
            JavaFeaturesProto.JavaFeatures other = (JavaFeaturesProto.JavaFeatures)obj;
            if (this.hasLegacyClosedEnum() != other.hasLegacyClosedEnum()) {
               return false;
            } else if (this.hasLegacyClosedEnum() && this.getLegacyClosedEnum() != other.getLegacyClosedEnum()) {
               return false;
            } else if (this.hasUtf8Validation() != other.hasUtf8Validation()) {
               return false;
            } else if (this.hasUtf8Validation() && this.utf8Validation_ != other.utf8Validation_) {
               return false;
            } else if (this.hasLargeEnum() != other.hasLargeEnum()) {
               return false;
            } else if (this.hasLargeEnum() && this.getLargeEnum() != other.getLargeEnum()) {
               return false;
            } else if (this.hasUseOldOuterClassnameDefault() != other.hasUseOldOuterClassnameDefault()) {
               return false;
            } else if (this.hasUseOldOuterClassnameDefault() && this.getUseOldOuterClassnameDefault() != other.getUseOldOuterClassnameDefault()) {
               return false;
            } else if (this.hasNestInFileClass() != other.hasNestInFileClass()) {
               return false;
            } else {
               return this.hasNestInFileClass() && this.nestInFileClass_ != other.nestInFileClass_
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
            if (this.hasLegacyClosedEnum()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashBoolean(this.getLegacyClosedEnum());
            }

            if (this.hasUtf8Validation()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.utf8Validation_;
            }

            if (this.hasLargeEnum()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + Internal.hashBoolean(this.getLargeEnum());
            }

            if (this.hasUseOldOuterClassnameDefault()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + Internal.hashBoolean(this.getUseOldOuterClassnameDefault());
            }

            if (this.hasNestInFileClass()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.nestInFileClass_;
            }

            hash = 29 * hash + this.getUnknownFields().hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static JavaFeaturesProto.JavaFeatures parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static JavaFeaturesProto.JavaFeatures parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public JavaFeaturesProto.JavaFeatures.Builder newBuilderForType() {
         return newBuilder();
      }

      public static JavaFeaturesProto.JavaFeatures.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static JavaFeaturesProto.JavaFeatures.Builder newBuilder(JavaFeaturesProto.JavaFeatures prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public JavaFeaturesProto.JavaFeatures.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new JavaFeaturesProto.JavaFeatures.Builder() : new JavaFeaturesProto.JavaFeatures.Builder().mergeFrom(this);
      }

      protected JavaFeaturesProto.JavaFeatures.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new JavaFeaturesProto.JavaFeatures.Builder(parent);
      }

      public static JavaFeaturesProto.JavaFeatures getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<JavaFeaturesProto.JavaFeatures> parser() {
         return PARSER;
      }

      @Override
      public Parser<JavaFeaturesProto.JavaFeatures> getParserForType() {
         return PARSER;
      }

      public JavaFeaturesProto.JavaFeatures getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "JavaFeatures");
      }

      public static final class Builder
         extends GeneratedMessage.Builder<JavaFeaturesProto.JavaFeatures.Builder>
         implements JavaFeaturesProto.JavaFeaturesOrBuilder {
         private int bitField0_;
         private boolean legacyClosedEnum_;
         private int utf8Validation_ = 0;
         private boolean largeEnum_;
         private boolean useOldOuterClassnameDefault_;
         private int nestInFileClass_ = 0;

         public static final Descriptors.Descriptor getDescriptor() {
            return JavaFeaturesProto.internal_static_pb_JavaFeatures_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JavaFeaturesProto.internal_static_pb_JavaFeatures_fieldAccessorTable
               .ensureFieldAccessorsInitialized(JavaFeaturesProto.JavaFeatures.class, JavaFeaturesProto.JavaFeatures.Builder.class);
         }

         private Builder() {
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
         }

         public JavaFeaturesProto.JavaFeatures.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.legacyClosedEnum_ = false;
            this.utf8Validation_ = 0;
            this.largeEnum_ = false;
            this.useOldOuterClassnameDefault_ = false;
            this.nestInFileClass_ = 0;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return JavaFeaturesProto.internal_static_pb_JavaFeatures_descriptor;
         }

         public JavaFeaturesProto.JavaFeatures getDefaultInstanceForType() {
            return JavaFeaturesProto.JavaFeatures.getDefaultInstance();
         }

         public JavaFeaturesProto.JavaFeatures build() {
            JavaFeaturesProto.JavaFeatures result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public JavaFeaturesProto.JavaFeatures buildPartial() {
            JavaFeaturesProto.JavaFeatures result = new JavaFeaturesProto.JavaFeatures(this);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartial0(JavaFeaturesProto.JavaFeatures result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.legacyClosedEnum_ = this.legacyClosedEnum_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.utf8Validation_ = this.utf8Validation_;
               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               result.largeEnum_ = this.largeEnum_;
               to_bitField0_ |= 4;
            }

            if ((from_bitField0_ & 8) != 0) {
               result.useOldOuterClassnameDefault_ = this.useOldOuterClassnameDefault_;
               to_bitField0_ |= 8;
            }

            if ((from_bitField0_ & 16) != 0) {
               result.nestInFileClass_ = this.nestInFileClass_;
               to_bitField0_ |= 16;
            }

            result.bitField0_ |= to_bitField0_;
         }

         public JavaFeaturesProto.JavaFeatures.Builder mergeFrom(Message other) {
            if (other instanceof JavaFeaturesProto.JavaFeatures) {
               return this.mergeFrom((JavaFeaturesProto.JavaFeatures)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public JavaFeaturesProto.JavaFeatures.Builder mergeFrom(JavaFeaturesProto.JavaFeatures other) {
            if (other == JavaFeaturesProto.JavaFeatures.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasLegacyClosedEnum()) {
                  this.setLegacyClosedEnum(other.getLegacyClosedEnum());
               }

               if (other.hasUtf8Validation()) {
                  this.setUtf8Validation(other.getUtf8Validation());
               }

               if (other.hasLargeEnum()) {
                  this.setLargeEnum(other.getLargeEnum());
               }

               if (other.hasUseOldOuterClassnameDefault()) {
                  this.setUseOldOuterClassnameDefault(other.getUseOldOuterClassnameDefault());
               }

               if (other.hasNestInFileClass()) {
                  this.setNestInFileClass(other.getNestInFileClass());
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

         public JavaFeaturesProto.JavaFeatures.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                           this.legacyClosedEnum_ = input.readBool();
                           this.bitField0_ |= 1;
                           break;
                        case 16:
                           int tmpRawx = input.readEnum();
                           JavaFeaturesProto.JavaFeatures.Utf8Validation tmpValuex = JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(tmpRawx);
                           if (tmpValuex == null) {
                              this.mergeUnknownVarintField(2, tmpRawx);
                           } else {
                              this.utf8Validation_ = tmpRawx;
                              this.bitField0_ |= 2;
                           }
                           break;
                        case 24:
                           this.largeEnum_ = input.readBool();
                           this.bitField0_ |= 4;
                           break;
                        case 32:
                           this.useOldOuterClassnameDefault_ = input.readBool();
                           this.bitField0_ |= 8;
                           break;
                        case 40:
                           int tmpRaw = input.readEnum();
                           JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass tmpValue = JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass.forNumber(
                              tmpRaw
                           );
                           if (tmpValue == null) {
                              this.mergeUnknownVarintField(5, tmpRaw);
                           } else {
                              this.nestInFileClass_ = tmpRaw;
                              this.bitField0_ |= 16;
                           }
                           break;
                        default:
                           if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var10) {
                  throw var10.unwrapIOException();
               } finally {
                  this.onChanged();
               }

               return this;
            }
         }

         @Override
         public boolean hasLegacyClosedEnum() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public boolean getLegacyClosedEnum() {
            return this.legacyClosedEnum_;
         }

         public JavaFeaturesProto.JavaFeatures.Builder setLegacyClosedEnum(boolean value) {
            this.legacyClosedEnum_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearLegacyClosedEnum() {
            this.bitField0_ &= -2;
            this.legacyClosedEnum_ = false;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasUtf8Validation() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public JavaFeaturesProto.JavaFeatures.Utf8Validation getUtf8Validation() {
            JavaFeaturesProto.JavaFeatures.Utf8Validation result = JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(this.utf8Validation_);
            return result == null ? JavaFeaturesProto.JavaFeatures.Utf8Validation.UTF8_VALIDATION_UNKNOWN : result;
         }

         public JavaFeaturesProto.JavaFeatures.Builder setUtf8Validation(JavaFeaturesProto.JavaFeatures.Utf8Validation value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.utf8Validation_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearUtf8Validation() {
            this.bitField0_ &= -3;
            this.utf8Validation_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasLargeEnum() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public boolean getLargeEnum() {
            return this.largeEnum_;
         }

         public JavaFeaturesProto.JavaFeatures.Builder setLargeEnum(boolean value) {
            this.largeEnum_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearLargeEnum() {
            this.bitField0_ &= -5;
            this.largeEnum_ = false;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasUseOldOuterClassnameDefault() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public boolean getUseOldOuterClassnameDefault() {
            return this.useOldOuterClassnameDefault_;
         }

         public JavaFeaturesProto.JavaFeatures.Builder setUseOldOuterClassnameDefault(boolean value) {
            this.useOldOuterClassnameDefault_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearUseOldOuterClassnameDefault() {
            this.bitField0_ &= -9;
            this.useOldOuterClassnameDefault_ = false;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasNestInFileClass() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass getNestInFileClass() {
            JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass result = JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass.forNumber(
               this.nestInFileClass_
            );
            return result == null ? JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass.NEST_IN_FILE_CLASS_UNKNOWN : result;
         }

         public JavaFeaturesProto.JavaFeatures.Builder setNestInFileClass(JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.nestInFileClass_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public JavaFeaturesProto.JavaFeatures.Builder clearNestInFileClass() {
            this.bitField0_ &= -17;
            this.nestInFileClass_ = 0;
            this.onChanged();
            return this;
         }
      }

      public static final class NestInFileClassFeature extends GeneratedMessage implements JavaFeaturesProto.JavaFeatures.NestInFileClassFeatureOrBuilder {
         private static final long serialVersionUID = 0L;
         private byte memoizedIsInitialized = -1;
         private static final JavaFeaturesProto.JavaFeatures.NestInFileClassFeature DEFAULT_INSTANCE = new JavaFeaturesProto.JavaFeatures.NestInFileClassFeature();
         private static final Parser<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature> PARSER = new AbstractParser<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature>() {
            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder builder = JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.newBuilder();

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

         private NestInFileClassFeature(GeneratedMessage.Builder<?> builder) {
            super(builder);
         }

         private NestInFileClassFeature() {
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return JavaFeaturesProto.internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JavaFeaturesProto.internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable
               .ensureFieldAccessorsInitialized(
                  JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.class, JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder.class
               );
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
            this.getUnknownFields().writeTo(output);
         }

         @Override
         public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
               return size;
            } else {
               int var2 = 0;
               var2 += this.getUnknownFields().getSerializedSize();
               this.memoizedSize = var2;
               return var2;
            }
         }

         @Override
         public boolean equals(final Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof JavaFeaturesProto.JavaFeatures.NestInFileClassFeature)) {
               return super.equals(obj);
            } else {
               JavaFeaturesProto.JavaFeatures.NestInFileClassFeature other = (JavaFeaturesProto.JavaFeatures.NestInFileClassFeature)obj;
               return this.getUnknownFields().equals(other.getUnknownFields());
            }
         }

         @Override
         public int hashCode() {
            if (this.memoizedHashCode != 0) {
               return this.memoizedHashCode;
            } else {
               int hash = 41;
               hash = 19 * hash + getDescriptor().hashCode();
               hash = 29 * hash + this.getUnknownFields().hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder newBuilderForType() {
            return newBuilder();
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder newBuilder(JavaFeaturesProto.JavaFeatures.NestInFileClassFeature prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder toBuilder() {
            return this == DEFAULT_INSTANCE
               ? new JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder()
               : new JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder().mergeFrom(this);
         }

         protected JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
            return new JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder(parent);
         }

         public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature> parser() {
            return PARSER;
         }

         @Override
         public Parser<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature> getParserForType() {
            return PARSER;
         }

         public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "NestInFileClassFeature");
         }

         public static final class Builder
            extends GeneratedMessage.Builder<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder>
            implements JavaFeaturesProto.JavaFeatures.NestInFileClassFeatureOrBuilder {
            public static final Descriptors.Descriptor getDescriptor() {
               return JavaFeaturesProto.internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
               return JavaFeaturesProto.internal_static_pb_JavaFeatures_NestInFileClassFeature_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(
                     JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.class, JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder.class
                  );
            }

            private Builder() {
            }

            private Builder(AbstractMessage.BuilderParent parent) {
               super(parent);
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder clear() {
               super.clear();
               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return JavaFeaturesProto.internal_static_pb_JavaFeatures_NestInFileClassFeature_descriptor;
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature getDefaultInstanceForType() {
               return JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.getDefaultInstance();
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature build() {
               JavaFeaturesProto.JavaFeatures.NestInFileClassFeature result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature buildPartial() {
               JavaFeaturesProto.JavaFeatures.NestInFileClassFeature result = new JavaFeaturesProto.JavaFeatures.NestInFileClassFeature(this);
               this.onBuilt();
               return result;
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder mergeFrom(Message other) {
               if (other instanceof JavaFeaturesProto.JavaFeatures.NestInFileClassFeature) {
                  return this.mergeFrom((JavaFeaturesProto.JavaFeatures.NestInFileClassFeature)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder mergeFrom(JavaFeaturesProto.JavaFeatures.NestInFileClassFeature other) {
               if (other == JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.getDefaultInstance()) {
                  return this;
               } else {
                  this.mergeUnknownFields(other.getUnknownFields());
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public final boolean isInitialized() {
               return true;
            }

            public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
         }

         public static enum NestInFileClass implements ProtocolMessageEnum {
            NEST_IN_FILE_CLASS_UNKNOWN(0),
            NO(1),
            YES(2),
            LEGACY(3);

            public static final int NEST_IN_FILE_CLASS_UNKNOWN_VALUE = 0;
            public static final int NO_VALUE = 1;
            public static final int YES_VALUE = 2;
            public static final int LEGACY_VALUE = 3;
            private static final Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass> internalValueMap = new Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass>() {
               public JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass findValueByNumber(int number) {
                  return JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass.forNumber(number);
               }
            };
            private static final JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass[] VALUES = values();
            private final int value;

            @Override
            public final int getNumber() {
               return this.value;
            }

            @Deprecated
            public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass valueOf(int value) {
               return forNumber(value);
            }

            public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass forNumber(int value) {
               switch (value) {
                  case 0:
                     return NEST_IN_FILE_CLASS_UNKNOWN;
                  case 1:
                     return NO;
                  case 2:
                     return YES;
                  case 3:
                     return LEGACY;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass> internalGetValueMap() {
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
               return JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.getDescriptor().getEnumTypes().get(0);
            }

            public static JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass valueOf(Descriptors.EnumValueDescriptor desc) {
               if (desc.getType() != getDescriptor()) {
                  throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
               } else {
                  return VALUES[desc.getIndex()];
               }
            }

            private NestInFileClass(int value) {
               this.value = value;
            }

            static {
               RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "NestInFileClass");
            }
         }
      }

      public interface NestInFileClassFeatureOrBuilder extends MessageOrBuilder {
      }

      public static enum Utf8Validation implements ProtocolMessageEnum {
         UTF8_VALIDATION_UNKNOWN(0),
         DEFAULT(1),
         VERIFY(2);

         public static final int UTF8_VALIDATION_UNKNOWN_VALUE = 0;
         public static final int DEFAULT_VALUE = 1;
         public static final int VERIFY_VALUE = 2;
         private static final Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.Utf8Validation> internalValueMap = new Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.Utf8Validation>() {
            public JavaFeaturesProto.JavaFeatures.Utf8Validation findValueByNumber(int number) {
               return JavaFeaturesProto.JavaFeatures.Utf8Validation.forNumber(number);
            }
         };
         private static final JavaFeaturesProto.JavaFeatures.Utf8Validation[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static JavaFeaturesProto.JavaFeatures.Utf8Validation valueOf(int value) {
            return forNumber(value);
         }

         public static JavaFeaturesProto.JavaFeatures.Utf8Validation forNumber(int value) {
            switch (value) {
               case 0:
                  return UTF8_VALIDATION_UNKNOWN;
               case 1:
                  return DEFAULT;
               case 2:
                  return VERIFY;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<JavaFeaturesProto.JavaFeatures.Utf8Validation> internalGetValueMap() {
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
            return JavaFeaturesProto.JavaFeatures.getDescriptor().getEnumTypes().get(0);
         }

         public static JavaFeaturesProto.JavaFeatures.Utf8Validation valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Utf8Validation(int value) {
            this.value = value;
         }

         static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Utf8Validation");
         }
      }
   }

   public interface JavaFeaturesOrBuilder extends MessageOrBuilder {
      boolean hasLegacyClosedEnum();

      boolean getLegacyClosedEnum();

      boolean hasUtf8Validation();

      JavaFeaturesProto.JavaFeatures.Utf8Validation getUtf8Validation();

      boolean hasLargeEnum();

      boolean getLargeEnum();

      boolean hasUseOldOuterClassnameDefault();

      boolean getUseOldOuterClassnameDefault();

      boolean hasNestInFileClass();

      JavaFeaturesProto.JavaFeatures.NestInFileClassFeature.NestInFileClass getNestInFileClass();
   }
}
