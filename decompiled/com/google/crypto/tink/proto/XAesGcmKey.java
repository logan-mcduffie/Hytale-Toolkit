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
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class XAesGcmKey extends GeneratedMessage implements XAesGcmKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int PARAMS_FIELD_NUMBER = 2;
   private XAesGcmParams params_;
   public static final int KEY_VALUE_FIELD_NUMBER = 3;
   private ByteString keyValue_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final XAesGcmKey DEFAULT_INSTANCE = new XAesGcmKey();
   private static final Parser<XAesGcmKey> PARSER = new AbstractParser<XAesGcmKey>() {
      public XAesGcmKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         XAesGcmKey.Builder builder = XAesGcmKey.newBuilder();

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

   private XAesGcmKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private XAesGcmKey() {
      this.keyValue_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return XAesGcm.internal_static_google_crypto_tink_XAesGcmKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return XAesGcm.internal_static_google_crypto_tink_XAesGcmKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(XAesGcmKey.class, XAesGcmKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public boolean hasParams() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public XAesGcmParams getParams() {
      return this.params_ == null ? XAesGcmParams.getDefaultInstance() : this.params_;
   }

   @Override
   public XAesGcmParamsOrBuilder getParamsOrBuilder() {
      return this.params_ == null ? XAesGcmParams.getDefaultInstance() : this.params_;
   }

   @Override
   public ByteString getKeyValue() {
      return this.keyValue_;
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
      if (this.version_ != 0) {
         output.writeUInt32(1, this.version_);
      }

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(2, this.getParams());
      }

      if (!this.keyValue_.isEmpty()) {
         output.writeBytes(3, this.keyValue_);
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
         if (this.version_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.version_);
         }

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getParams());
         }

         if (!this.keyValue_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.keyValue_);
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
      } else if (!(obj instanceof XAesGcmKey)) {
         return super.equals(obj);
      } else {
         XAesGcmKey other = (XAesGcmKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.hasParams() != other.hasParams()) {
            return false;
         } else if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
         } else {
            return !this.getKeyValue().equals(other.getKeyValue()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getVersion();
         if (this.hasParams()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getParams().hashCode();
         }

         hash = 37 * hash + 3;
         hash = 53 * hash + this.getKeyValue().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static XAesGcmKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static XAesGcmKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static XAesGcmKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static XAesGcmKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static XAesGcmKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static XAesGcmKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static XAesGcmKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static XAesGcmKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static XAesGcmKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static XAesGcmKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static XAesGcmKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static XAesGcmKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public XAesGcmKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static XAesGcmKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static XAesGcmKey.Builder newBuilder(XAesGcmKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public XAesGcmKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new XAesGcmKey.Builder() : new XAesGcmKey.Builder().mergeFrom(this);
   }

   protected XAesGcmKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new XAesGcmKey.Builder(parent);
   }

   public static XAesGcmKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<XAesGcmKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<XAesGcmKey> getParserForType() {
      return PARSER;
   }

   public XAesGcmKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", XAesGcmKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<XAesGcmKey.Builder> implements XAesGcmKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private XAesGcmParams params_;
      private SingleFieldBuilder<XAesGcmParams, XAesGcmParams.Builder, XAesGcmParamsOrBuilder> paramsBuilder_;
      private ByteString keyValue_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return XAesGcm.internal_static_google_crypto_tink_XAesGcmKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return XAesGcm.internal_static_google_crypto_tink_XAesGcmKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(XAesGcmKey.class, XAesGcmKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (XAesGcmKey.alwaysUseFieldBuilders) {
            this.internalGetParamsFieldBuilder();
         }
      }

      public XAesGcmKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.keyValue_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return XAesGcm.internal_static_google_crypto_tink_XAesGcmKey_descriptor;
      }

      public XAesGcmKey getDefaultInstanceForType() {
         return XAesGcmKey.getDefaultInstance();
      }

      public XAesGcmKey build() {
         XAesGcmKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public XAesGcmKey buildPartial() {
         XAesGcmKey result = new XAesGcmKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(XAesGcmKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 2) != 0) {
            result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.keyValue_ = this.keyValue_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public XAesGcmKey.Builder mergeFrom(Message other) {
         if (other instanceof XAesGcmKey) {
            return this.mergeFrom((XAesGcmKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public XAesGcmKey.Builder mergeFrom(XAesGcmKey other) {
         if (other == XAesGcmKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.hasParams()) {
               this.mergeParams(other.getParams());
            }

            if (!other.getKeyValue().isEmpty()) {
               this.setKeyValue(other.getKeyValue());
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

      public XAesGcmKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.version_ = input.readUInt32();
                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        this.keyValue_ = input.readBytes();
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
      public int getVersion() {
         return this.version_;
      }

      public XAesGcmKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public XAesGcmKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasParams() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public XAesGcmParams getParams() {
         if (this.paramsBuilder_ == null) {
            return this.params_ == null ? XAesGcmParams.getDefaultInstance() : this.params_;
         } else {
            return this.paramsBuilder_.getMessage();
         }
      }

      public XAesGcmKey.Builder setParams(XAesGcmParams value) {
         if (this.paramsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.params_ = value;
         } else {
            this.paramsBuilder_.setMessage(value);
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public XAesGcmKey.Builder setParams(XAesGcmParams.Builder builderForValue) {
         if (this.paramsBuilder_ == null) {
            this.params_ = builderForValue.build();
         } else {
            this.paramsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public XAesGcmKey.Builder mergeParams(XAesGcmParams value) {
         if (this.paramsBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.params_ != null && this.params_ != XAesGcmParams.getDefaultInstance()) {
               this.getParamsBuilder().mergeFrom(value);
            } else {
               this.params_ = value;
            }
         } else {
            this.paramsBuilder_.mergeFrom(value);
         }

         if (this.params_ != null) {
            this.bitField0_ |= 2;
            this.onChanged();
         }

         return this;
      }

      public XAesGcmKey.Builder clearParams() {
         this.bitField0_ &= -3;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public XAesGcmParams.Builder getParamsBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetParamsFieldBuilder().getBuilder();
      }

      @Override
      public XAesGcmParamsOrBuilder getParamsOrBuilder() {
         if (this.paramsBuilder_ != null) {
            return this.paramsBuilder_.getMessageOrBuilder();
         } else {
            return this.params_ == null ? XAesGcmParams.getDefaultInstance() : this.params_;
         }
      }

      private SingleFieldBuilder<XAesGcmParams, XAesGcmParams.Builder, XAesGcmParamsOrBuilder> internalGetParamsFieldBuilder() {
         if (this.paramsBuilder_ == null) {
            this.paramsBuilder_ = new SingleFieldBuilder<>(this.getParams(), this.getParentForChildren(), this.isClean());
            this.params_ = null;
         }

         return this.paramsBuilder_;
      }

      @Override
      public ByteString getKeyValue() {
         return this.keyValue_;
      }

      public XAesGcmKey.Builder setKeyValue(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.keyValue_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public XAesGcmKey.Builder clearKeyValue() {
         this.bitField0_ &= -5;
         this.keyValue_ = XAesGcmKey.getDefaultInstance().getKeyValue();
         this.onChanged();
         return this;
      }
   }
}
