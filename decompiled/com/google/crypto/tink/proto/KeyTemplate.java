package com.google.crypto.tink.proto;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class KeyTemplate extends GeneratedMessage implements KeyTemplateOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int TYPE_URL_FIELD_NUMBER = 1;
   private volatile Object typeUrl_ = "";
   public static final int VALUE_FIELD_NUMBER = 2;
   private ByteString value_ = ByteString.EMPTY;
   public static final int OUTPUT_PREFIX_TYPE_FIELD_NUMBER = 3;
   private int outputPrefixType_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final KeyTemplate DEFAULT_INSTANCE = new KeyTemplate();
   private static final Parser<KeyTemplate> PARSER = new AbstractParser<KeyTemplate>() {
      public KeyTemplate parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         KeyTemplate.Builder builder = KeyTemplate.newBuilder();

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

   private KeyTemplate(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private KeyTemplate() {
      this.typeUrl_ = "";
      this.value_ = ByteString.EMPTY;
      this.outputPrefixType_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Tink.internal_static_google_crypto_tink_KeyTemplate_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Tink.internal_static_google_crypto_tink_KeyTemplate_fieldAccessorTable
         .ensureFieldAccessorsInitialized(KeyTemplate.class, KeyTemplate.Builder.class);
   }

   @Override
   public String getTypeUrl() {
      Object ref = this.typeUrl_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.typeUrl_ = s;
         return s;
      }
   }

   @Override
   public ByteString getTypeUrlBytes() {
      Object ref = this.typeUrl_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.typeUrl_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
   }

   @Override
   public ByteString getValue() {
      return this.value_;
   }

   @Override
   public int getOutputPrefixTypeValue() {
      return this.outputPrefixType_;
   }

   @Override
   public OutputPrefixType getOutputPrefixType() {
      OutputPrefixType result = OutputPrefixType.forNumber(this.outputPrefixType_);
      return result == null ? OutputPrefixType.UNRECOGNIZED : result;
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
      if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
         GeneratedMessage.writeString(output, 1, this.typeUrl_);
      }

      if (!this.value_.isEmpty()) {
         output.writeBytes(2, this.value_);
      }

      if (this.outputPrefixType_ != OutputPrefixType.UNKNOWN_PREFIX.getNumber()) {
         output.writeEnum(3, this.outputPrefixType_);
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
         if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
            size += GeneratedMessage.computeStringSize(1, this.typeUrl_);
         }

         if (!this.value_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.value_);
         }

         if (this.outputPrefixType_ != OutputPrefixType.UNKNOWN_PREFIX.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.outputPrefixType_);
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
      } else if (!(obj instanceof KeyTemplate)) {
         return super.equals(obj);
      } else {
         KeyTemplate other = (KeyTemplate)obj;
         if (!this.getTypeUrl().equals(other.getTypeUrl())) {
            return false;
         } else if (!this.getValue().equals(other.getValue())) {
            return false;
         } else {
            return this.outputPrefixType_ != other.outputPrefixType_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getTypeUrl().hashCode();
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getValue().hashCode();
         hash = 37 * hash + 3;
         hash = 53 * hash + this.outputPrefixType_;
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static KeyTemplate parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeyTemplate parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeyTemplate parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeyTemplate parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeyTemplate parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeyTemplate parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeyTemplate parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static KeyTemplate parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static KeyTemplate parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static KeyTemplate parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static KeyTemplate parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static KeyTemplate parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public KeyTemplate.Builder newBuilderForType() {
      return newBuilder();
   }

   public static KeyTemplate.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static KeyTemplate.Builder newBuilder(KeyTemplate prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public KeyTemplate.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new KeyTemplate.Builder() : new KeyTemplate.Builder().mergeFrom(this);
   }

   protected KeyTemplate.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new KeyTemplate.Builder(parent);
   }

   public static KeyTemplate getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<KeyTemplate> parser() {
      return PARSER;
   }

   @Override
   public Parser<KeyTemplate> getParserForType() {
      return PARSER;
   }

   public KeyTemplate getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeyTemplate.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<KeyTemplate.Builder> implements KeyTemplateOrBuilder {
      private int bitField0_;
      private Object typeUrl_ = "";
      private ByteString value_ = ByteString.EMPTY;
      private int outputPrefixType_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return Tink.internal_static_google_crypto_tink_KeyTemplate_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Tink.internal_static_google_crypto_tink_KeyTemplate_fieldAccessorTable
            .ensureFieldAccessorsInitialized(KeyTemplate.class, KeyTemplate.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public KeyTemplate.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.typeUrl_ = "";
         this.value_ = ByteString.EMPTY;
         this.outputPrefixType_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Tink.internal_static_google_crypto_tink_KeyTemplate_descriptor;
      }

      public KeyTemplate getDefaultInstanceForType() {
         return KeyTemplate.getDefaultInstance();
      }

      public KeyTemplate build() {
         KeyTemplate result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public KeyTemplate buildPartial() {
         KeyTemplate result = new KeyTemplate(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(KeyTemplate result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.typeUrl_ = this.typeUrl_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.value_ = this.value_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.outputPrefixType_ = this.outputPrefixType_;
         }
      }

      public KeyTemplate.Builder mergeFrom(Message other) {
         if (other instanceof KeyTemplate) {
            return this.mergeFrom((KeyTemplate)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public KeyTemplate.Builder mergeFrom(KeyTemplate other) {
         if (other == KeyTemplate.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getTypeUrl().isEmpty()) {
               this.typeUrl_ = other.typeUrl_;
               this.bitField0_ |= 1;
               this.onChanged();
            }

            if (!other.getValue().isEmpty()) {
               this.setValue(other.getValue());
            }

            if (other.outputPrefixType_ != 0) {
               this.setOutputPrefixTypeValue(other.getOutputPrefixTypeValue());
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

      public KeyTemplate.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.typeUrl_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        this.value_ = input.readBytes();
                        this.bitField0_ |= 2;
                        break;
                     case 24:
                        this.outputPrefixType_ = input.readEnum();
                        this.bitField0_ |= 4;
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
      public String getTypeUrl() {
         Object ref = this.typeUrl_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.typeUrl_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getTypeUrlBytes() {
         Object ref = this.typeUrl_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.typeUrl_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public KeyTemplate.Builder setTypeUrl(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.typeUrl_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public KeyTemplate.Builder clearTypeUrl() {
         this.typeUrl_ = KeyTemplate.getDefaultInstance().getTypeUrl();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public KeyTemplate.Builder setTypeUrlBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            KeyTemplate.checkByteStringIsUtf8(value);
            this.typeUrl_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public ByteString getValue() {
         return this.value_;
      }

      public KeyTemplate.Builder setValue(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.value_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }
      }

      public KeyTemplate.Builder clearValue() {
         this.bitField0_ &= -3;
         this.value_ = KeyTemplate.getDefaultInstance().getValue();
         this.onChanged();
         return this;
      }

      @Override
      public int getOutputPrefixTypeValue() {
         return this.outputPrefixType_;
      }

      public KeyTemplate.Builder setOutputPrefixTypeValue(int value) {
         this.outputPrefixType_ = value;
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      @Override
      public OutputPrefixType getOutputPrefixType() {
         OutputPrefixType result = OutputPrefixType.forNumber(this.outputPrefixType_);
         return result == null ? OutputPrefixType.UNRECOGNIZED : result;
      }

      public KeyTemplate.Builder setOutputPrefixType(OutputPrefixType value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 4;
            this.outputPrefixType_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public KeyTemplate.Builder clearOutputPrefixType() {
         this.bitField0_ &= -5;
         this.outputPrefixType_ = 0;
         this.onChanged();
         return this;
      }
   }
}
