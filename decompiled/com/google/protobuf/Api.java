package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Api extends GeneratedMessage implements ApiOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_ = "";
   public static final int METHODS_FIELD_NUMBER = 2;
   private List<Method> methods_;
   public static final int OPTIONS_FIELD_NUMBER = 3;
   private List<Option> options_;
   public static final int VERSION_FIELD_NUMBER = 4;
   private volatile Object version_ = "";
   public static final int SOURCE_CONTEXT_FIELD_NUMBER = 5;
   private SourceContext sourceContext_;
   public static final int MIXINS_FIELD_NUMBER = 6;
   private List<Mixin> mixins_;
   public static final int SYNTAX_FIELD_NUMBER = 7;
   private int syntax_ = 0;
   public static final int EDITION_FIELD_NUMBER = 8;
   private volatile Object edition_ = "";
   private byte memoizedIsInitialized = -1;
   private static final Api DEFAULT_INSTANCE = new Api();
   private static final Parser<Api> PARSER = new AbstractParser<Api>() {
      public Api parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Api.Builder builder = Api.newBuilder();

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

   private Api(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Api() {
      this.name_ = "";
      this.methods_ = Collections.emptyList();
      this.options_ = Collections.emptyList();
      this.version_ = "";
      this.mixins_ = Collections.emptyList();
      this.syntax_ = 0;
      this.edition_ = "";
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return ApiProto.internal_static_google_protobuf_Api_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Api_fieldAccessorTable.ensureFieldAccessorsInitialized(Api.class, Api.Builder.class);
   }

   @Override
   public String getName() {
      Object ref = this.name_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.name_ = s;
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
   public List<Method> getMethodsList() {
      return this.methods_;
   }

   @Override
   public List<? extends MethodOrBuilder> getMethodsOrBuilderList() {
      return this.methods_;
   }

   @Override
   public int getMethodsCount() {
      return this.methods_.size();
   }

   @Override
   public Method getMethods(int index) {
      return this.methods_.get(index);
   }

   @Override
   public MethodOrBuilder getMethodsOrBuilder(int index) {
      return this.methods_.get(index);
   }

   @Override
   public List<Option> getOptionsList() {
      return this.options_;
   }

   @Override
   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      return this.options_;
   }

   @Override
   public int getOptionsCount() {
      return this.options_.size();
   }

   @Override
   public Option getOptions(int index) {
      return this.options_.get(index);
   }

   @Override
   public OptionOrBuilder getOptionsOrBuilder(int index) {
      return this.options_.get(index);
   }

   @Override
   public String getVersion() {
      Object ref = this.version_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.version_ = s;
         return s;
      }
   }

   @Override
   public ByteString getVersionBytes() {
      Object ref = this.version_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.version_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
   }

   @Override
   public boolean hasSourceContext() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public SourceContext getSourceContext() {
      return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
   }

   @Override
   public SourceContextOrBuilder getSourceContextOrBuilder() {
      return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
   }

   @Override
   public List<Mixin> getMixinsList() {
      return this.mixins_;
   }

   @Override
   public List<? extends MixinOrBuilder> getMixinsOrBuilderList() {
      return this.mixins_;
   }

   @Override
   public int getMixinsCount() {
      return this.mixins_.size();
   }

   @Override
   public Mixin getMixins(int index) {
      return this.mixins_.get(index);
   }

   @Override
   public MixinOrBuilder getMixinsOrBuilder(int index) {
      return this.mixins_.get(index);
   }

   @Override
   public int getSyntaxValue() {
      return this.syntax_;
   }

   @Override
   public Syntax getSyntax() {
      Syntax result = Syntax.forNumber(this.syntax_);
      return result == null ? Syntax.UNRECOGNIZED : result;
   }

   @Override
   public String getEdition() {
      Object ref = this.edition_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.edition_ = s;
         return s;
      }
   }

   @Override
   public ByteString getEditionBytes() {
      Object ref = this.edition_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.edition_ = b;
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
      if (!GeneratedMessage.isStringEmpty(this.name_)) {
         GeneratedMessage.writeString(output, 1, this.name_);
      }

      for (int i = 0; i < this.methods_.size(); i++) {
         output.writeMessage(2, this.methods_.get(i));
      }

      for (int i = 0; i < this.options_.size(); i++) {
         output.writeMessage(3, this.options_.get(i));
      }

      if (!GeneratedMessage.isStringEmpty(this.version_)) {
         GeneratedMessage.writeString(output, 4, this.version_);
      }

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(5, this.getSourceContext());
      }

      for (int i = 0; i < this.mixins_.size(); i++) {
         output.writeMessage(6, this.mixins_.get(i));
      }

      if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
         output.writeEnum(7, this.syntax_);
      }

      if (!GeneratedMessage.isStringEmpty(this.edition_)) {
         GeneratedMessage.writeString(output, 8, this.edition_);
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
         if (!GeneratedMessage.isStringEmpty(this.name_)) {
            size += GeneratedMessage.computeStringSize(1, this.name_);
         }

         for (int i = 0; i < this.methods_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(2, this.methods_.get(i));
         }

         for (int i = 0; i < this.options_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(3, this.options_.get(i));
         }

         if (!GeneratedMessage.isStringEmpty(this.version_)) {
            size += GeneratedMessage.computeStringSize(4, this.version_);
         }

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(5, this.getSourceContext());
         }

         for (int i = 0; i < this.mixins_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(6, this.mixins_.get(i));
         }

         if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            size += CodedOutputStream.computeEnumSize(7, this.syntax_);
         }

         if (!GeneratedMessage.isStringEmpty(this.edition_)) {
            size += GeneratedMessage.computeStringSize(8, this.edition_);
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
      } else if (!(obj instanceof Api)) {
         return super.equals(obj);
      } else {
         Api other = (Api)obj;
         if (!this.getName().equals(other.getName())) {
            return false;
         } else if (!this.getMethodsList().equals(other.getMethodsList())) {
            return false;
         } else if (!this.getOptionsList().equals(other.getOptionsList())) {
            return false;
         } else if (!this.getVersion().equals(other.getVersion())) {
            return false;
         } else if (this.hasSourceContext() != other.hasSourceContext()) {
            return false;
         } else if (this.hasSourceContext() && !this.getSourceContext().equals(other.getSourceContext())) {
            return false;
         } else if (!this.getMixinsList().equals(other.getMixinsList())) {
            return false;
         } else if (this.syntax_ != other.syntax_) {
            return false;
         } else {
            return !this.getEdition().equals(other.getEdition()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 37 * hash + 1;
         hash = 53 * hash + this.getName().hashCode();
         if (this.getMethodsCount() > 0) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getMethodsList().hashCode();
         }

         if (this.getOptionsCount() > 0) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getOptionsList().hashCode();
         }

         hash = 37 * hash + 4;
         hash = 53 * hash + this.getVersion().hashCode();
         if (this.hasSourceContext()) {
            hash = 37 * hash + 5;
            hash = 53 * hash + this.getSourceContext().hashCode();
         }

         if (this.getMixinsCount() > 0) {
            hash = 37 * hash + 6;
            hash = 53 * hash + this.getMixinsList().hashCode();
         }

         hash = 37 * hash + 7;
         hash = 53 * hash + this.syntax_;
         hash = 37 * hash + 8;
         hash = 53 * hash + this.getEdition().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Api parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Api parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Api parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Api parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Api parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Api parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Api parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Api parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Api parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Api parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Api parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Api parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Api.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Api.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Api.Builder newBuilder(Api prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Api.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Api.Builder() : new Api.Builder().mergeFrom(this);
   }

   protected Api.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Api.Builder(parent);
   }

   public static Api getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Api> parser() {
      return PARSER;
   }

   @Override
   public Parser<Api> getParserForType() {
      return PARSER;
   }

   public Api getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Api");
   }

   public static final class Builder extends GeneratedMessage.Builder<Api.Builder> implements ApiOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private List<Method> methods_ = Collections.emptyList();
      private RepeatedFieldBuilder<Method, Method.Builder, MethodOrBuilder> methodsBuilder_;
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilder<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
      private Object version_ = "";
      private SourceContext sourceContext_;
      private SingleFieldBuilder<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
      private List<Mixin> mixins_ = Collections.emptyList();
      private RepeatedFieldBuilder<Mixin, Mixin.Builder, MixinOrBuilder> mixinsBuilder_;
      private int syntax_ = 0;
      private Object edition_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return ApiProto.internal_static_google_protobuf_Api_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return ApiProto.internal_static_google_protobuf_Api_fieldAccessorTable.ensureFieldAccessorsInitialized(Api.class, Api.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessage.alwaysUseFieldBuilders) {
            this.internalGetMethodsFieldBuilder();
            this.internalGetOptionsFieldBuilder();
            this.internalGetSourceContextFieldBuilder();
            this.internalGetMixinsFieldBuilder();
         }
      }

      public Api.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.name_ = "";
         if (this.methodsBuilder_ == null) {
            this.methods_ = Collections.emptyList();
         } else {
            this.methods_ = null;
            this.methodsBuilder_.clear();
         }

         this.bitField0_ &= -3;
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
         } else {
            this.options_ = null;
            this.optionsBuilder_.clear();
         }

         this.bitField0_ &= -5;
         this.version_ = "";
         this.sourceContext_ = null;
         if (this.sourceContextBuilder_ != null) {
            this.sourceContextBuilder_.dispose();
            this.sourceContextBuilder_ = null;
         }

         if (this.mixinsBuilder_ == null) {
            this.mixins_ = Collections.emptyList();
         } else {
            this.mixins_ = null;
            this.mixinsBuilder_.clear();
         }

         this.bitField0_ &= -33;
         this.syntax_ = 0;
         this.edition_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return ApiProto.internal_static_google_protobuf_Api_descriptor;
      }

      public Api getDefaultInstanceForType() {
         return Api.getDefaultInstance();
      }

      public Api build() {
         Api result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Api buildPartial() {
         Api result = new Api(this);
         this.buildPartialRepeatedFields(result);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartialRepeatedFields(Api result) {
         if (this.methodsBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0) {
               this.methods_ = Collections.unmodifiableList(this.methods_);
               this.bitField0_ &= -3;
            }

            result.methods_ = this.methods_;
         } else {
            result.methods_ = this.methodsBuilder_.build();
         }

         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 4) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -5;
            }

            result.options_ = this.options_;
         } else {
            result.options_ = this.optionsBuilder_.build();
         }

         if (this.mixinsBuilder_ == null) {
            if ((this.bitField0_ & 32) != 0) {
               this.mixins_ = Collections.unmodifiableList(this.mixins_);
               this.bitField0_ &= -33;
            }

            result.mixins_ = this.mixins_;
         } else {
            result.mixins_ = this.mixinsBuilder_.build();
         }
      }

      private void buildPartial0(Api result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.name_ = this.name_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.version_ = this.version_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 16) != 0) {
            result.sourceContext_ = this.sourceContextBuilder_ == null ? this.sourceContext_ : this.sourceContextBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 64) != 0) {
            result.syntax_ = this.syntax_;
         }

         if ((from_bitField0_ & 128) != 0) {
            result.edition_ = this.edition_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public Api.Builder mergeFrom(Message other) {
         if (other instanceof Api) {
            return this.mergeFrom((Api)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Api.Builder mergeFrom(Api other) {
         if (other == Api.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getName().isEmpty()) {
               this.name_ = other.name_;
               this.bitField0_ |= 1;
               this.onChanged();
            }

            if (this.methodsBuilder_ == null) {
               if (!other.methods_.isEmpty()) {
                  if (this.methods_.isEmpty()) {
                     this.methods_ = other.methods_;
                     this.bitField0_ &= -3;
                  } else {
                     this.ensureMethodsIsMutable();
                     this.methods_.addAll(other.methods_);
                  }

                  this.onChanged();
               }
            } else if (!other.methods_.isEmpty()) {
               if (this.methodsBuilder_.isEmpty()) {
                  this.methodsBuilder_.dispose();
                  this.methodsBuilder_ = null;
                  this.methods_ = other.methods_;
                  this.bitField0_ &= -3;
                  this.methodsBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetMethodsFieldBuilder() : null;
               } else {
                  this.methodsBuilder_.addAllMessages(other.methods_);
               }
            }

            if (this.optionsBuilder_ == null) {
               if (!other.options_.isEmpty()) {
                  if (this.options_.isEmpty()) {
                     this.options_ = other.options_;
                     this.bitField0_ &= -5;
                  } else {
                     this.ensureOptionsIsMutable();
                     this.options_.addAll(other.options_);
                  }

                  this.onChanged();
               }
            } else if (!other.options_.isEmpty()) {
               if (this.optionsBuilder_.isEmpty()) {
                  this.optionsBuilder_.dispose();
                  this.optionsBuilder_ = null;
                  this.options_ = other.options_;
                  this.bitField0_ &= -5;
                  this.optionsBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetOptionsFieldBuilder() : null;
               } else {
                  this.optionsBuilder_.addAllMessages(other.options_);
               }
            }

            if (!other.getVersion().isEmpty()) {
               this.version_ = other.version_;
               this.bitField0_ |= 8;
               this.onChanged();
            }

            if (other.hasSourceContext()) {
               this.mergeSourceContext(other.getSourceContext());
            }

            if (this.mixinsBuilder_ == null) {
               if (!other.mixins_.isEmpty()) {
                  if (this.mixins_.isEmpty()) {
                     this.mixins_ = other.mixins_;
                     this.bitField0_ &= -33;
                  } else {
                     this.ensureMixinsIsMutable();
                     this.mixins_.addAll(other.mixins_);
                  }

                  this.onChanged();
               }
            } else if (!other.mixins_.isEmpty()) {
               if (this.mixinsBuilder_.isEmpty()) {
                  this.mixinsBuilder_.dispose();
                  this.mixinsBuilder_ = null;
                  this.mixins_ = other.mixins_;
                  this.bitField0_ &= -33;
                  this.mixinsBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetMixinsFieldBuilder() : null;
               } else {
                  this.mixinsBuilder_.addAllMessages(other.mixins_);
               }
            }

            if (other.syntax_ != 0) {
               this.setSyntaxValue(other.getSyntaxValue());
            }

            if (!other.getEdition().isEmpty()) {
               this.edition_ = other.edition_;
               this.bitField0_ |= 128;
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

      public Api.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.name_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        Method mxx = input.readMessage(Method.parser(), extensionRegistry);
                        if (this.methodsBuilder_ == null) {
                           this.ensureMethodsIsMutable();
                           this.methods_.add(mxx);
                        } else {
                           this.methodsBuilder_.addMessage(mxx);
                        }
                        break;
                     case 26:
                        Option mx = input.readMessage(Option.parser(), extensionRegistry);
                        if (this.optionsBuilder_ == null) {
                           this.ensureOptionsIsMutable();
                           this.options_.add(mx);
                        } else {
                           this.optionsBuilder_.addMessage(mx);
                        }
                        break;
                     case 34:
                        this.version_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        input.readMessage(this.internalGetSourceContextFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 16;
                        break;
                     case 50:
                        Mixin m = input.readMessage(Mixin.parser(), extensionRegistry);
                        if (this.mixinsBuilder_ == null) {
                           this.ensureMixinsIsMutable();
                           this.mixins_.add(m);
                        } else {
                           this.mixinsBuilder_.addMessage(m);
                        }
                        break;
                     case 56:
                        this.syntax_ = input.readEnum();
                        this.bitField0_ |= 64;
                        break;
                     case 66:
                        this.edition_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 128;
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
      public String getName() {
         Object ref = this.name_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.name_ = s;
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

      public Api.Builder setName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.name_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearName() {
         this.name_ = Api.getDefaultInstance().getName();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public Api.Builder setNameBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.name_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      private void ensureMethodsIsMutable() {
         if ((this.bitField0_ & 2) == 0) {
            this.methods_ = new ArrayList<>(this.methods_);
            this.bitField0_ |= 2;
         }
      }

      @Override
      public List<Method> getMethodsList() {
         return this.methodsBuilder_ == null ? Collections.unmodifiableList(this.methods_) : this.methodsBuilder_.getMessageList();
      }

      @Override
      public int getMethodsCount() {
         return this.methodsBuilder_ == null ? this.methods_.size() : this.methodsBuilder_.getCount();
      }

      @Override
      public Method getMethods(int index) {
         return this.methodsBuilder_ == null ? this.methods_.get(index) : this.methodsBuilder_.getMessage(index);
      }

      public Api.Builder setMethods(int index, Method value) {
         if (this.methodsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureMethodsIsMutable();
            this.methods_.set(index, value);
            this.onChanged();
         } else {
            this.methodsBuilder_.setMessage(index, value);
         }

         return this;
      }

      public Api.Builder setMethods(int index, Method.Builder builderForValue) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.methodsBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public Api.Builder addMethods(Method value) {
         if (this.methodsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureMethodsIsMutable();
            this.methods_.add(value);
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(value);
         }

         return this;
      }

      public Api.Builder addMethods(int index, Method value) {
         if (this.methodsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureMethodsIsMutable();
            this.methods_.add(index, value);
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(index, value);
         }

         return this;
      }

      public Api.Builder addMethods(Method.Builder builderForValue) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public Api.Builder addMethods(int index, Method.Builder builderForValue) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public Api.Builder addAllMethods(Iterable<? extends Method> values) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.methods_);
            this.onChanged();
         } else {
            this.methodsBuilder_.addAllMessages(values);
         }

         return this;
      }

      public Api.Builder clearMethods() {
         if (this.methodsBuilder_ == null) {
            this.methods_ = Collections.emptyList();
            this.bitField0_ &= -3;
            this.onChanged();
         } else {
            this.methodsBuilder_.clear();
         }

         return this;
      }

      public Api.Builder removeMethods(int index) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.remove(index);
            this.onChanged();
         } else {
            this.methodsBuilder_.remove(index);
         }

         return this;
      }

      public Method.Builder getMethodsBuilder(int index) {
         return this.internalGetMethodsFieldBuilder().getBuilder(index);
      }

      @Override
      public MethodOrBuilder getMethodsOrBuilder(int index) {
         return this.methodsBuilder_ == null ? this.methods_.get(index) : this.methodsBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends MethodOrBuilder> getMethodsOrBuilderList() {
         return this.methodsBuilder_ != null ? this.methodsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.methods_);
      }

      public Method.Builder addMethodsBuilder() {
         return this.internalGetMethodsFieldBuilder().addBuilder(Method.getDefaultInstance());
      }

      public Method.Builder addMethodsBuilder(int index) {
         return this.internalGetMethodsFieldBuilder().addBuilder(index, Method.getDefaultInstance());
      }

      public List<Method.Builder> getMethodsBuilderList() {
         return this.internalGetMethodsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<Method, Method.Builder, MethodOrBuilder> internalGetMethodsFieldBuilder() {
         if (this.methodsBuilder_ == null) {
            this.methodsBuilder_ = new RepeatedFieldBuilder<>(this.methods_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
            this.methods_ = null;
         }

         return this.methodsBuilder_;
      }

      private void ensureOptionsIsMutable() {
         if ((this.bitField0_ & 4) == 0) {
            this.options_ = new ArrayList<>(this.options_);
            this.bitField0_ |= 4;
         }
      }

      @Override
      public List<Option> getOptionsList() {
         return this.optionsBuilder_ == null ? Collections.unmodifiableList(this.options_) : this.optionsBuilder_.getMessageList();
      }

      @Override
      public int getOptionsCount() {
         return this.optionsBuilder_ == null ? this.options_.size() : this.optionsBuilder_.getCount();
      }

      @Override
      public Option getOptions(int index) {
         return this.optionsBuilder_ == null ? this.options_.get(index) : this.optionsBuilder_.getMessage(index);
      }

      public Api.Builder setOptions(int index, Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.set(index, value);
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(index, value);
         }

         return this;
      }

      public Api.Builder setOptions(int index, Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public Api.Builder addOptions(Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(value);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(value);
         }

         return this;
      }

      public Api.Builder addOptions(int index, Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(index, value);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(index, value);
         }

         return this;
      }

      public Api.Builder addOptions(Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public Api.Builder addOptions(int index, Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public Api.Builder addAllOptions(Iterable<? extends Option> values) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(values);
         }

         return this;
      }

      public Api.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -5;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Api.Builder removeOptions(int index) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.remove(index);
            this.onChanged();
         } else {
            this.optionsBuilder_.remove(index);
         }

         return this;
      }

      public Option.Builder getOptionsBuilder(int index) {
         return this.internalGetOptionsFieldBuilder().getBuilder(index);
      }

      @Override
      public OptionOrBuilder getOptionsOrBuilder(int index) {
         return this.optionsBuilder_ == null ? this.options_.get(index) : this.optionsBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
         return this.optionsBuilder_ != null ? this.optionsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.options_);
      }

      public Option.Builder addOptionsBuilder() {
         return this.internalGetOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
      }

      public Option.Builder addOptionsBuilder(int index) {
         return this.internalGetOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
      }

      public List<Option.Builder> getOptionsBuilderList() {
         return this.internalGetOptionsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<Option, Option.Builder, OptionOrBuilder> internalGetOptionsFieldBuilder() {
         if (this.optionsBuilder_ == null) {
            this.optionsBuilder_ = new RepeatedFieldBuilder<>(this.options_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
            this.options_ = null;
         }

         return this.optionsBuilder_;
      }

      @Override
      public String getVersion() {
         Object ref = this.version_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.version_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getVersionBytes() {
         Object ref = this.version_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.version_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Api.Builder setVersion(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.version_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearVersion() {
         this.version_ = Api.getDefaultInstance().getVersion();
         this.bitField0_ &= -9;
         this.onChanged();
         return this;
      }

      public Api.Builder setVersionBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.version_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean hasSourceContext() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public SourceContext getSourceContext() {
         if (this.sourceContextBuilder_ == null) {
            return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
         } else {
            return this.sourceContextBuilder_.getMessage();
         }
      }

      public Api.Builder setSourceContext(SourceContext value) {
         if (this.sourceContextBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.sourceContext_ = value;
         } else {
            this.sourceContextBuilder_.setMessage(value);
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public Api.Builder setSourceContext(SourceContext.Builder builderForValue) {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = builderForValue.build();
         } else {
            this.sourceContextBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public Api.Builder mergeSourceContext(SourceContext value) {
         if (this.sourceContextBuilder_ == null) {
            if ((this.bitField0_ & 16) != 0 && this.sourceContext_ != null && this.sourceContext_ != SourceContext.getDefaultInstance()) {
               this.getSourceContextBuilder().mergeFrom(value);
            } else {
               this.sourceContext_ = value;
            }
         } else {
            this.sourceContextBuilder_.mergeFrom(value);
         }

         if (this.sourceContext_ != null) {
            this.bitField0_ |= 16;
            this.onChanged();
         }

         return this;
      }

      public Api.Builder clearSourceContext() {
         this.bitField0_ &= -17;
         this.sourceContext_ = null;
         if (this.sourceContextBuilder_ != null) {
            this.sourceContextBuilder_.dispose();
            this.sourceContextBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public SourceContext.Builder getSourceContextBuilder() {
         this.bitField0_ |= 16;
         this.onChanged();
         return this.internalGetSourceContextFieldBuilder().getBuilder();
      }

      @Override
      public SourceContextOrBuilder getSourceContextOrBuilder() {
         if (this.sourceContextBuilder_ != null) {
            return this.sourceContextBuilder_.getMessageOrBuilder();
         } else {
            return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
         }
      }

      private SingleFieldBuilder<SourceContext, SourceContext.Builder, SourceContextOrBuilder> internalGetSourceContextFieldBuilder() {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContextBuilder_ = new SingleFieldBuilder<>(this.getSourceContext(), this.getParentForChildren(), this.isClean());
            this.sourceContext_ = null;
         }

         return this.sourceContextBuilder_;
      }

      private void ensureMixinsIsMutable() {
         if ((this.bitField0_ & 32) == 0) {
            this.mixins_ = new ArrayList<>(this.mixins_);
            this.bitField0_ |= 32;
         }
      }

      @Override
      public List<Mixin> getMixinsList() {
         return this.mixinsBuilder_ == null ? Collections.unmodifiableList(this.mixins_) : this.mixinsBuilder_.getMessageList();
      }

      @Override
      public int getMixinsCount() {
         return this.mixinsBuilder_ == null ? this.mixins_.size() : this.mixinsBuilder_.getCount();
      }

      @Override
      public Mixin getMixins(int index) {
         return this.mixinsBuilder_ == null ? this.mixins_.get(index) : this.mixinsBuilder_.getMessage(index);
      }

      public Api.Builder setMixins(int index, Mixin value) {
         if (this.mixinsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureMixinsIsMutable();
            this.mixins_.set(index, value);
            this.onChanged();
         } else {
            this.mixinsBuilder_.setMessage(index, value);
         }

         return this;
      }

      public Api.Builder setMixins(int index, Mixin.Builder builderForValue) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.mixinsBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public Api.Builder addMixins(Mixin value) {
         if (this.mixinsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureMixinsIsMutable();
            this.mixins_.add(value);
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(value);
         }

         return this;
      }

      public Api.Builder addMixins(int index, Mixin value) {
         if (this.mixinsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureMixinsIsMutable();
            this.mixins_.add(index, value);
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(index, value);
         }

         return this;
      }

      public Api.Builder addMixins(Mixin.Builder builderForValue) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public Api.Builder addMixins(int index, Mixin.Builder builderForValue) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public Api.Builder addAllMixins(Iterable<? extends Mixin> values) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.mixins_);
            this.onChanged();
         } else {
            this.mixinsBuilder_.addAllMessages(values);
         }

         return this;
      }

      public Api.Builder clearMixins() {
         if (this.mixinsBuilder_ == null) {
            this.mixins_ = Collections.emptyList();
            this.bitField0_ &= -33;
            this.onChanged();
         } else {
            this.mixinsBuilder_.clear();
         }

         return this;
      }

      public Api.Builder removeMixins(int index) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.remove(index);
            this.onChanged();
         } else {
            this.mixinsBuilder_.remove(index);
         }

         return this;
      }

      public Mixin.Builder getMixinsBuilder(int index) {
         return this.internalGetMixinsFieldBuilder().getBuilder(index);
      }

      @Override
      public MixinOrBuilder getMixinsOrBuilder(int index) {
         return this.mixinsBuilder_ == null ? this.mixins_.get(index) : this.mixinsBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends MixinOrBuilder> getMixinsOrBuilderList() {
         return this.mixinsBuilder_ != null ? this.mixinsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.mixins_);
      }

      public Mixin.Builder addMixinsBuilder() {
         return this.internalGetMixinsFieldBuilder().addBuilder(Mixin.getDefaultInstance());
      }

      public Mixin.Builder addMixinsBuilder(int index) {
         return this.internalGetMixinsFieldBuilder().addBuilder(index, Mixin.getDefaultInstance());
      }

      public List<Mixin.Builder> getMixinsBuilderList() {
         return this.internalGetMixinsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<Mixin, Mixin.Builder, MixinOrBuilder> internalGetMixinsFieldBuilder() {
         if (this.mixinsBuilder_ == null) {
            this.mixinsBuilder_ = new RepeatedFieldBuilder<>(this.mixins_, (this.bitField0_ & 32) != 0, this.getParentForChildren(), this.isClean());
            this.mixins_ = null;
         }

         return this.mixinsBuilder_;
      }

      @Override
      public int getSyntaxValue() {
         return this.syntax_;
      }

      public Api.Builder setSyntaxValue(int value) {
         this.syntax_ = value;
         this.bitField0_ |= 64;
         this.onChanged();
         return this;
      }

      @Override
      public Syntax getSyntax() {
         Syntax result = Syntax.forNumber(this.syntax_);
         return result == null ? Syntax.UNRECOGNIZED : result;
      }

      public Api.Builder setSyntax(Syntax value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 64;
            this.syntax_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearSyntax() {
         this.bitField0_ &= -65;
         this.syntax_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public String getEdition() {
         Object ref = this.edition_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.edition_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getEditionBytes() {
         Object ref = this.edition_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.edition_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Api.Builder setEdition(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.edition_ = value;
            this.bitField0_ |= 128;
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearEdition() {
         this.edition_ = Api.getDefaultInstance().getEdition();
         this.bitField0_ &= -129;
         this.onChanged();
         return this;
      }

      public Api.Builder setEditionBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.edition_ = value;
            this.bitField0_ |= 128;
            this.onChanged();
            return this;
         }
      }
   }
}
