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

public final class AesCtrKeyFormat extends GeneratedMessage implements AesCtrKeyFormatOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int PARAMS_FIELD_NUMBER = 1;
   private AesCtrParams params_;
   public static final int KEY_SIZE_FIELD_NUMBER = 2;
   private int keySize_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final AesCtrKeyFormat DEFAULT_INSTANCE = new AesCtrKeyFormat();
   private static final Parser<AesCtrKeyFormat> PARSER = new AbstractParser<AesCtrKeyFormat>() {
      public AesCtrKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesCtrKeyFormat.Builder builder = AesCtrKeyFormat.newBuilder();

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

   private AesCtrKeyFormat(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesCtrKeyFormat() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesCtr.internal_static_google_crypto_tink_AesCtrKeyFormat_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesCtr.internal_static_google_crypto_tink_AesCtrKeyFormat_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesCtrKeyFormat.class, AesCtrKeyFormat.Builder.class);
   }

   @Override
   public boolean hasParams() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public AesCtrParams getParams() {
      return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
   }

   @Override
   public AesCtrParamsOrBuilder getParamsOrBuilder() {
      return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
   }

   @Override
   public int getKeySize() {
      return this.keySize_;
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
         output.writeMessage(1, this.getParams());
      }

      if (this.keySize_ != 0) {
         output.writeUInt32(2, this.keySize_);
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
            size += CodedOutputStream.computeMessageSize(1, this.getParams());
         }

         if (this.keySize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.keySize_);
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
      } else if (!(obj instanceof AesCtrKeyFormat)) {
         return super.equals(obj);
      } else {
         AesCtrKeyFormat other = (AesCtrKeyFormat)obj;
         if (this.hasParams() != other.hasParams()) {
            return false;
         } else if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
         } else {
            return this.getKeySize() != other.getKeySize() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasParams()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getParams().hashCode();
         }

         hash = 37 * hash + 2;
         hash = 53 * hash + this.getKeySize();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesCtrKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrKeyFormat parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesCtrKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrKeyFormat parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesCtrKeyFormat.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesCtrKeyFormat.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesCtrKeyFormat.Builder newBuilder(AesCtrKeyFormat prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesCtrKeyFormat.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesCtrKeyFormat.Builder() : new AesCtrKeyFormat.Builder().mergeFrom(this);
   }

   protected AesCtrKeyFormat.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesCtrKeyFormat.Builder(parent);
   }

   public static AesCtrKeyFormat getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesCtrKeyFormat> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesCtrKeyFormat> getParserForType() {
      return PARSER;
   }

   public AesCtrKeyFormat getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrKeyFormat.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesCtrKeyFormat.Builder> implements AesCtrKeyFormatOrBuilder {
      private int bitField0_;
      private AesCtrParams params_;
      private SingleFieldBuilder<AesCtrParams, AesCtrParams.Builder, AesCtrParamsOrBuilder> paramsBuilder_;
      private int keySize_;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesCtr.internal_static_google_crypto_tink_AesCtrKeyFormat_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesCtr.internal_static_google_crypto_tink_AesCtrKeyFormat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesCtrKeyFormat.class, AesCtrKeyFormat.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (AesCtrKeyFormat.alwaysUseFieldBuilders) {
            this.internalGetParamsFieldBuilder();
         }
      }

      public AesCtrKeyFormat.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.keySize_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesCtr.internal_static_google_crypto_tink_AesCtrKeyFormat_descriptor;
      }

      public AesCtrKeyFormat getDefaultInstanceForType() {
         return AesCtrKeyFormat.getDefaultInstance();
      }

      public AesCtrKeyFormat build() {
         AesCtrKeyFormat result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesCtrKeyFormat buildPartial() {
         AesCtrKeyFormat result = new AesCtrKeyFormat(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesCtrKeyFormat result) {
         int from_bitField0_ = this.bitField0_;
         int to_bitField0_ = 0;
         if ((from_bitField0_ & 1) != 0) {
            result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.keySize_ = this.keySize_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public AesCtrKeyFormat.Builder mergeFrom(Message other) {
         if (other instanceof AesCtrKeyFormat) {
            return this.mergeFrom((AesCtrKeyFormat)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesCtrKeyFormat.Builder mergeFrom(AesCtrKeyFormat other) {
         if (other == AesCtrKeyFormat.getDefaultInstance()) {
            return this;
         } else {
            if (other.hasParams()) {
               this.mergeParams(other.getParams());
            }

            if (other.getKeySize() != 0) {
               this.setKeySize(other.getKeySize());
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

      public AesCtrKeyFormat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.keySize_ = input.readUInt32();
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
      public boolean hasParams() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public AesCtrParams getParams() {
         if (this.paramsBuilder_ == null) {
            return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
         } else {
            return this.paramsBuilder_.getMessage();
         }
      }

      public AesCtrKeyFormat.Builder setParams(AesCtrParams value) {
         if (this.paramsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.params_ = value;
         } else {
            this.paramsBuilder_.setMessage(value);
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesCtrKeyFormat.Builder setParams(AesCtrParams.Builder builderForValue) {
         if (this.paramsBuilder_ == null) {
            this.params_ = builderForValue.build();
         } else {
            this.paramsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesCtrKeyFormat.Builder mergeParams(AesCtrParams value) {
         if (this.paramsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0 && this.params_ != null && this.params_ != AesCtrParams.getDefaultInstance()) {
               this.getParamsBuilder().mergeFrom(value);
            } else {
               this.params_ = value;
            }
         } else {
            this.paramsBuilder_.mergeFrom(value);
         }

         if (this.params_ != null) {
            this.bitField0_ |= 1;
            this.onChanged();
         }

         return this;
      }

      public AesCtrKeyFormat.Builder clearParams() {
         this.bitField0_ &= -2;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public AesCtrParams.Builder getParamsBuilder() {
         this.bitField0_ |= 1;
         this.onChanged();
         return this.internalGetParamsFieldBuilder().getBuilder();
      }

      @Override
      public AesCtrParamsOrBuilder getParamsOrBuilder() {
         if (this.paramsBuilder_ != null) {
            return this.paramsBuilder_.getMessageOrBuilder();
         } else {
            return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
         }
      }

      private SingleFieldBuilder<AesCtrParams, AesCtrParams.Builder, AesCtrParamsOrBuilder> internalGetParamsFieldBuilder() {
         if (this.paramsBuilder_ == null) {
            this.paramsBuilder_ = new SingleFieldBuilder<>(this.getParams(), this.getParentForChildren(), this.isClean());
            this.params_ = null;
         }

         return this.paramsBuilder_;
      }

      @Override
      public int getKeySize() {
         return this.keySize_;
      }

      public AesCtrKeyFormat.Builder setKeySize(int value) {
         this.keySize_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public AesCtrKeyFormat.Builder clearKeySize() {
         this.bitField0_ &= -3;
         this.keySize_ = 0;
         this.onChanged();
         return this;
      }
   }
}
