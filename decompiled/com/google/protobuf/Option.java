package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Option extends GeneratedMessage implements OptionOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_ = "";
   public static final int VALUE_FIELD_NUMBER = 2;
   private Any value_;
   private byte memoizedIsInitialized = -1;
   private static final Option DEFAULT_INSTANCE = new Option();
   private static final Parser<Option> PARSER = new AbstractParser<Option>() {
      public Option parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Option.Builder builder = Option.newBuilder();

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

   private Option(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Option() {
      this.name_ = "";
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_Option_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized(Option.class, Option.Builder.class);
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
   public boolean hasValue() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public Any getValue() {
      return this.value_ == null ? Any.getDefaultInstance() : this.value_;
   }

   @Override
   public AnyOrBuilder getValueOrBuilder() {
      return this.value_ == null ? Any.getDefaultInstance() : this.value_;
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

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(2, this.getValue());
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

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getValue());
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
      } else if (!(obj instanceof Option)) {
         return super.equals(obj);
      } else {
         Option other = (Option)obj;
         if (!this.getName().equals(other.getName())) {
            return false;
         } else if (this.hasValue() != other.hasValue()) {
            return false;
         } else {
            return this.hasValue() && !this.getValue().equals(other.getValue()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasValue()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getValue().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Option parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Option parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Option parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Option parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Option parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Option parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Option parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Option parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Option parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Option parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Option parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Option parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Option.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Option.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Option.Builder newBuilder(Option prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Option.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Option.Builder() : new Option.Builder().mergeFrom(this);
   }

   protected Option.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Option.Builder(parent);
   }

   public static Option getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Option> parser() {
      return PARSER;
   }

   @Override
   public Parser<Option> getParserForType() {
      return PARSER;
   }

   public Option getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Option");
   }

   public static final class Builder extends GeneratedMessage.Builder<Option.Builder> implements OptionOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private Any value_;
      private SingleFieldBuilder<Any, Any.Builder, AnyOrBuilder> valueBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_Option_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized(Option.class, Option.Builder.class);
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
            this.internalGetValueFieldBuilder();
         }
      }

      public Option.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.name_ = "";
         this.value_ = null;
         if (this.valueBuilder_ != null) {
            this.valueBuilder_.dispose();
            this.valueBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_Option_descriptor;
      }

      public Option getDefaultInstanceForType() {
         return Option.getDefaultInstance();
      }

      public Option build() {
         Option result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Option buildPartial() {
         Option result = new Option(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(Option result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.name_ = this.name_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 2) != 0) {
            result.value_ = this.valueBuilder_ == null ? this.value_ : this.valueBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public Option.Builder mergeFrom(Message other) {
         if (other instanceof Option) {
            return this.mergeFrom((Option)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Option.Builder mergeFrom(Option other) {
         if (other == Option.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getName().isEmpty()) {
               this.name_ = other.name_;
               this.bitField0_ |= 1;
               this.onChanged();
            }

            if (other.hasValue()) {
               this.mergeValue(other.getValue());
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

      public Option.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        input.readMessage(this.internalGetValueFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 2;
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

      public Option.Builder setName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.name_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public Option.Builder clearName() {
         this.name_ = Option.getDefaultInstance().getName();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public Option.Builder setNameBytes(ByteString value) {
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

      @Override
      public boolean hasValue() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public Any getValue() {
         if (this.valueBuilder_ == null) {
            return this.value_ == null ? Any.getDefaultInstance() : this.value_;
         } else {
            return this.valueBuilder_.getMessage();
         }
      }

      public Option.Builder setValue(Any value) {
         if (this.valueBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.value_ = value;
         } else {
            this.valueBuilder_.setMessage(value);
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public Option.Builder setValue(Any.Builder builderForValue) {
         if (this.valueBuilder_ == null) {
            this.value_ = builderForValue.build();
         } else {
            this.valueBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public Option.Builder mergeValue(Any value) {
         if (this.valueBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != Any.getDefaultInstance()) {
               this.getValueBuilder().mergeFrom(value);
            } else {
               this.value_ = value;
            }
         } else {
            this.valueBuilder_.mergeFrom(value);
         }

         if (this.value_ != null) {
            this.bitField0_ |= 2;
            this.onChanged();
         }

         return this;
      }

      public Option.Builder clearValue() {
         this.bitField0_ &= -3;
         this.value_ = null;
         if (this.valueBuilder_ != null) {
            this.valueBuilder_.dispose();
            this.valueBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public Any.Builder getValueBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetValueFieldBuilder().getBuilder();
      }

      @Override
      public AnyOrBuilder getValueOrBuilder() {
         if (this.valueBuilder_ != null) {
            return this.valueBuilder_.getMessageOrBuilder();
         } else {
            return this.value_ == null ? Any.getDefaultInstance() : this.value_;
         }
      }

      private SingleFieldBuilder<Any, Any.Builder, AnyOrBuilder> internalGetValueFieldBuilder() {
         if (this.valueBuilder_ == null) {
            this.valueBuilder_ = new SingleFieldBuilder<>(this.getValue(), this.getParentForChildren(), this.isClean());
            this.value_ = null;
         }

         return this.valueBuilder_;
      }
   }
}
