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

public final class RsaSsaPssKeyFormat extends GeneratedMessage implements RsaSsaPssKeyFormatOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int PARAMS_FIELD_NUMBER = 1;
   private RsaSsaPssParams params_;
   public static final int MODULUS_SIZE_IN_BITS_FIELD_NUMBER = 2;
   private int modulusSizeInBits_ = 0;
   public static final int PUBLIC_EXPONENT_FIELD_NUMBER = 3;
   private ByteString publicExponent_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final RsaSsaPssKeyFormat DEFAULT_INSTANCE = new RsaSsaPssKeyFormat();
   private static final Parser<RsaSsaPssKeyFormat> PARSER = new AbstractParser<RsaSsaPssKeyFormat>() {
      public RsaSsaPssKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         RsaSsaPssKeyFormat.Builder builder = RsaSsaPssKeyFormat.newBuilder();

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

   private RsaSsaPssKeyFormat(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private RsaSsaPssKeyFormat() {
      this.publicExponent_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssKeyFormat_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssKeyFormat_fieldAccessorTable
         .ensureFieldAccessorsInitialized(RsaSsaPssKeyFormat.class, RsaSsaPssKeyFormat.Builder.class);
   }

   @Override
   public boolean hasParams() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public RsaSsaPssParams getParams() {
      return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
   }

   @Override
   public RsaSsaPssParamsOrBuilder getParamsOrBuilder() {
      return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
   }

   @Override
   public int getModulusSizeInBits() {
      return this.modulusSizeInBits_;
   }

   @Override
   public ByteString getPublicExponent() {
      return this.publicExponent_;
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

      if (this.modulusSizeInBits_ != 0) {
         output.writeUInt32(2, this.modulusSizeInBits_);
      }

      if (!this.publicExponent_.isEmpty()) {
         output.writeBytes(3, this.publicExponent_);
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

         if (this.modulusSizeInBits_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.modulusSizeInBits_);
         }

         if (!this.publicExponent_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.publicExponent_);
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
      } else if (!(obj instanceof RsaSsaPssKeyFormat)) {
         return super.equals(obj);
      } else {
         RsaSsaPssKeyFormat other = (RsaSsaPssKeyFormat)obj;
         if (this.hasParams() != other.hasParams()) {
            return false;
         } else if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
         } else if (this.getModulusSizeInBits() != other.getModulusSizeInBits()) {
            return false;
         } else {
            return !this.getPublicExponent().equals(other.getPublicExponent()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getModulusSizeInBits();
         hash = 37 * hash + 3;
         hash = 53 * hash + this.getPublicExponent().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static RsaSsaPssKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static RsaSsaPssKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static RsaSsaPssKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static RsaSsaPssKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static RsaSsaPssKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static RsaSsaPssKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static RsaSsaPssKeyFormat parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static RsaSsaPssKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static RsaSsaPssKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static RsaSsaPssKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static RsaSsaPssKeyFormat parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static RsaSsaPssKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public RsaSsaPssKeyFormat.Builder newBuilderForType() {
      return newBuilder();
   }

   public static RsaSsaPssKeyFormat.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static RsaSsaPssKeyFormat.Builder newBuilder(RsaSsaPssKeyFormat prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public RsaSsaPssKeyFormat.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new RsaSsaPssKeyFormat.Builder() : new RsaSsaPssKeyFormat.Builder().mergeFrom(this);
   }

   protected RsaSsaPssKeyFormat.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new RsaSsaPssKeyFormat.Builder(parent);
   }

   public static RsaSsaPssKeyFormat getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<RsaSsaPssKeyFormat> parser() {
      return PARSER;
   }

   @Override
   public Parser<RsaSsaPssKeyFormat> getParserForType() {
      return PARSER;
   }

   public RsaSsaPssKeyFormat getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPssKeyFormat.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<RsaSsaPssKeyFormat.Builder> implements RsaSsaPssKeyFormatOrBuilder {
      private int bitField0_;
      private RsaSsaPssParams params_;
      private SingleFieldBuilder<RsaSsaPssParams, RsaSsaPssParams.Builder, RsaSsaPssParamsOrBuilder> paramsBuilder_;
      private int modulusSizeInBits_;
      private ByteString publicExponent_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssKeyFormat_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssKeyFormat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(RsaSsaPssKeyFormat.class, RsaSsaPssKeyFormat.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (RsaSsaPssKeyFormat.alwaysUseFieldBuilders) {
            this.internalGetParamsFieldBuilder();
         }
      }

      public RsaSsaPssKeyFormat.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.modulusSizeInBits_ = 0;
         this.publicExponent_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssKeyFormat_descriptor;
      }

      public RsaSsaPssKeyFormat getDefaultInstanceForType() {
         return RsaSsaPssKeyFormat.getDefaultInstance();
      }

      public RsaSsaPssKeyFormat build() {
         RsaSsaPssKeyFormat result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public RsaSsaPssKeyFormat buildPartial() {
         RsaSsaPssKeyFormat result = new RsaSsaPssKeyFormat(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(RsaSsaPssKeyFormat result) {
         int from_bitField0_ = this.bitField0_;
         int to_bitField0_ = 0;
         if ((from_bitField0_ & 1) != 0) {
            result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.modulusSizeInBits_ = this.modulusSizeInBits_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.publicExponent_ = this.publicExponent_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public RsaSsaPssKeyFormat.Builder mergeFrom(Message other) {
         if (other instanceof RsaSsaPssKeyFormat) {
            return this.mergeFrom((RsaSsaPssKeyFormat)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public RsaSsaPssKeyFormat.Builder mergeFrom(RsaSsaPssKeyFormat other) {
         if (other == RsaSsaPssKeyFormat.getDefaultInstance()) {
            return this;
         } else {
            if (other.hasParams()) {
               this.mergeParams(other.getParams());
            }

            if (other.getModulusSizeInBits() != 0) {
               this.setModulusSizeInBits(other.getModulusSizeInBits());
            }

            if (!other.getPublicExponent().isEmpty()) {
               this.setPublicExponent(other.getPublicExponent());
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

      public RsaSsaPssKeyFormat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.modulusSizeInBits_ = input.readUInt32();
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        this.publicExponent_ = input.readBytes();
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
      public boolean hasParams() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public RsaSsaPssParams getParams() {
         if (this.paramsBuilder_ == null) {
            return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
         } else {
            return this.paramsBuilder_.getMessage();
         }
      }

      public RsaSsaPssKeyFormat.Builder setParams(RsaSsaPssParams value) {
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

      public RsaSsaPssKeyFormat.Builder setParams(RsaSsaPssParams.Builder builderForValue) {
         if (this.paramsBuilder_ == null) {
            this.params_ = builderForValue.build();
         } else {
            this.paramsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public RsaSsaPssKeyFormat.Builder mergeParams(RsaSsaPssParams value) {
         if (this.paramsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0 && this.params_ != null && this.params_ != RsaSsaPssParams.getDefaultInstance()) {
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

      public RsaSsaPssKeyFormat.Builder clearParams() {
         this.bitField0_ &= -2;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public RsaSsaPssParams.Builder getParamsBuilder() {
         this.bitField0_ |= 1;
         this.onChanged();
         return this.internalGetParamsFieldBuilder().getBuilder();
      }

      @Override
      public RsaSsaPssParamsOrBuilder getParamsOrBuilder() {
         if (this.paramsBuilder_ != null) {
            return this.paramsBuilder_.getMessageOrBuilder();
         } else {
            return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
         }
      }

      private SingleFieldBuilder<RsaSsaPssParams, RsaSsaPssParams.Builder, RsaSsaPssParamsOrBuilder> internalGetParamsFieldBuilder() {
         if (this.paramsBuilder_ == null) {
            this.paramsBuilder_ = new SingleFieldBuilder<>(this.getParams(), this.getParentForChildren(), this.isClean());
            this.params_ = null;
         }

         return this.paramsBuilder_;
      }

      @Override
      public int getModulusSizeInBits() {
         return this.modulusSizeInBits_;
      }

      public RsaSsaPssKeyFormat.Builder setModulusSizeInBits(int value) {
         this.modulusSizeInBits_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public RsaSsaPssKeyFormat.Builder clearModulusSizeInBits() {
         this.bitField0_ &= -3;
         this.modulusSizeInBits_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getPublicExponent() {
         return this.publicExponent_;
      }

      public RsaSsaPssKeyFormat.Builder setPublicExponent(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.publicExponent_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public RsaSsaPssKeyFormat.Builder clearPublicExponent() {
         this.bitField0_ &= -5;
         this.publicExponent_ = RsaSsaPssKeyFormat.getDefaultInstance().getPublicExponent();
         this.onChanged();
         return this;
      }
   }
}
