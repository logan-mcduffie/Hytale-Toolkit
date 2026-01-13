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

public final class AesCtrHmacStreamingParams extends GeneratedMessage implements AesCtrHmacStreamingParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int CIPHERTEXT_SEGMENT_SIZE_FIELD_NUMBER = 1;
   private int ciphertextSegmentSize_ = 0;
   public static final int DERIVED_KEY_SIZE_FIELD_NUMBER = 2;
   private int derivedKeySize_ = 0;
   public static final int HKDF_HASH_TYPE_FIELD_NUMBER = 3;
   private int hkdfHashType_ = 0;
   public static final int HMAC_PARAMS_FIELD_NUMBER = 4;
   private HmacParams hmacParams_;
   private byte memoizedIsInitialized = -1;
   private static final AesCtrHmacStreamingParams DEFAULT_INSTANCE = new AesCtrHmacStreamingParams();
   private static final Parser<AesCtrHmacStreamingParams> PARSER = new AbstractParser<AesCtrHmacStreamingParams>() {
      public AesCtrHmacStreamingParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesCtrHmacStreamingParams.Builder builder = AesCtrHmacStreamingParams.newBuilder();

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

   private AesCtrHmacStreamingParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesCtrHmacStreamingParams() {
      this.hkdfHashType_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesCtrHmacStreamingParams.class, AesCtrHmacStreamingParams.Builder.class);
   }

   @Override
   public int getCiphertextSegmentSize() {
      return this.ciphertextSegmentSize_;
   }

   @Override
   public int getDerivedKeySize() {
      return this.derivedKeySize_;
   }

   @Override
   public int getHkdfHashTypeValue() {
      return this.hkdfHashType_;
   }

   @Override
   public HashType getHkdfHashType() {
      HashType result = HashType.forNumber(this.hkdfHashType_);
      return result == null ? HashType.UNRECOGNIZED : result;
   }

   @Override
   public boolean hasHmacParams() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public HmacParams getHmacParams() {
      return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
   }

   @Override
   public HmacParamsOrBuilder getHmacParamsOrBuilder() {
      return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
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
      if (this.ciphertextSegmentSize_ != 0) {
         output.writeUInt32(1, this.ciphertextSegmentSize_);
      }

      if (this.derivedKeySize_ != 0) {
         output.writeUInt32(2, this.derivedKeySize_);
      }

      if (this.hkdfHashType_ != HashType.UNKNOWN_HASH.getNumber()) {
         output.writeEnum(3, this.hkdfHashType_);
      }

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(4, this.getHmacParams());
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
         if (this.ciphertextSegmentSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.ciphertextSegmentSize_);
         }

         if (this.derivedKeySize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.derivedKeySize_);
         }

         if (this.hkdfHashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.hkdfHashType_);
         }

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(4, this.getHmacParams());
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
      } else if (!(obj instanceof AesCtrHmacStreamingParams)) {
         return super.equals(obj);
      } else {
         AesCtrHmacStreamingParams other = (AesCtrHmacStreamingParams)obj;
         if (this.getCiphertextSegmentSize() != other.getCiphertextSegmentSize()) {
            return false;
         } else if (this.getDerivedKeySize() != other.getDerivedKeySize()) {
            return false;
         } else if (this.hkdfHashType_ != other.hkdfHashType_) {
            return false;
         } else if (this.hasHmacParams() != other.hasHmacParams()) {
            return false;
         } else {
            return this.hasHmacParams() && !this.getHmacParams().equals(other.getHmacParams())
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
         hash = 37 * hash + 1;
         hash = 53 * hash + this.getCiphertextSegmentSize();
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getDerivedKeySize();
         hash = 37 * hash + 3;
         hash = 53 * hash + this.hkdfHashType_;
         if (this.hasHmacParams()) {
            hash = 37 * hash + 4;
            hash = 53 * hash + this.getHmacParams().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesCtrHmacStreamingParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrHmacStreamingParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrHmacStreamingParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrHmacStreamingParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrHmacStreamingParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrHmacStreamingParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrHmacStreamingParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrHmacStreamingParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrHmacStreamingParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesCtrHmacStreamingParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrHmacStreamingParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrHmacStreamingParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesCtrHmacStreamingParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesCtrHmacStreamingParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesCtrHmacStreamingParams.Builder newBuilder(AesCtrHmacStreamingParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesCtrHmacStreamingParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesCtrHmacStreamingParams.Builder() : new AesCtrHmacStreamingParams.Builder().mergeFrom(this);
   }

   protected AesCtrHmacStreamingParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesCtrHmacStreamingParams.Builder(parent);
   }

   public static AesCtrHmacStreamingParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesCtrHmacStreamingParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesCtrHmacStreamingParams> getParserForType() {
      return PARSER;
   }

   public AesCtrHmacStreamingParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrHmacStreamingParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesCtrHmacStreamingParams.Builder> implements AesCtrHmacStreamingParamsOrBuilder {
      private int bitField0_;
      private int ciphertextSegmentSize_;
      private int derivedKeySize_;
      private int hkdfHashType_ = 0;
      private HmacParams hmacParams_;
      private SingleFieldBuilder<HmacParams, HmacParams.Builder, HmacParamsOrBuilder> hmacParamsBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesCtrHmacStreamingParams.class, AesCtrHmacStreamingParams.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (AesCtrHmacStreamingParams.alwaysUseFieldBuilders) {
            this.internalGetHmacParamsFieldBuilder();
         }
      }

      public AesCtrHmacStreamingParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.ciphertextSegmentSize_ = 0;
         this.derivedKeySize_ = 0;
         this.hkdfHashType_ = 0;
         this.hmacParams_ = null;
         if (this.hmacParamsBuilder_ != null) {
            this.hmacParamsBuilder_.dispose();
            this.hmacParamsBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_descriptor;
      }

      public AesCtrHmacStreamingParams getDefaultInstanceForType() {
         return AesCtrHmacStreamingParams.getDefaultInstance();
      }

      public AesCtrHmacStreamingParams build() {
         AesCtrHmacStreamingParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesCtrHmacStreamingParams buildPartial() {
         AesCtrHmacStreamingParams result = new AesCtrHmacStreamingParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesCtrHmacStreamingParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.ciphertextSegmentSize_ = this.ciphertextSegmentSize_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.derivedKeySize_ = this.derivedKeySize_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.hkdfHashType_ = this.hkdfHashType_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 8) != 0) {
            result.hmacParams_ = this.hmacParamsBuilder_ == null ? this.hmacParams_ : this.hmacParamsBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public AesCtrHmacStreamingParams.Builder mergeFrom(Message other) {
         if (other instanceof AesCtrHmacStreamingParams) {
            return this.mergeFrom((AesCtrHmacStreamingParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesCtrHmacStreamingParams.Builder mergeFrom(AesCtrHmacStreamingParams other) {
         if (other == AesCtrHmacStreamingParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.getCiphertextSegmentSize() != 0) {
               this.setCiphertextSegmentSize(other.getCiphertextSegmentSize());
            }

            if (other.getDerivedKeySize() != 0) {
               this.setDerivedKeySize(other.getDerivedKeySize());
            }

            if (other.hkdfHashType_ != 0) {
               this.setHkdfHashTypeValue(other.getHkdfHashTypeValue());
            }

            if (other.hasHmacParams()) {
               this.mergeHmacParams(other.getHmacParams());
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

      public AesCtrHmacStreamingParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.ciphertextSegmentSize_ = input.readUInt32();
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.derivedKeySize_ = input.readUInt32();
                        this.bitField0_ |= 2;
                        break;
                     case 24:
                        this.hkdfHashType_ = input.readEnum();
                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        input.readMessage(this.internalGetHmacParamsFieldBuilder().getBuilder(), extensionRegistry);
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
      public int getCiphertextSegmentSize() {
         return this.ciphertextSegmentSize_;
      }

      public AesCtrHmacStreamingParams.Builder setCiphertextSegmentSize(int value) {
         this.ciphertextSegmentSize_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesCtrHmacStreamingParams.Builder clearCiphertextSegmentSize() {
         this.bitField0_ &= -2;
         this.ciphertextSegmentSize_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getDerivedKeySize() {
         return this.derivedKeySize_;
      }

      public AesCtrHmacStreamingParams.Builder setDerivedKeySize(int value) {
         this.derivedKeySize_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public AesCtrHmacStreamingParams.Builder clearDerivedKeySize() {
         this.bitField0_ &= -3;
         this.derivedKeySize_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getHkdfHashTypeValue() {
         return this.hkdfHashType_;
      }

      public AesCtrHmacStreamingParams.Builder setHkdfHashTypeValue(int value) {
         this.hkdfHashType_ = value;
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      @Override
      public HashType getHkdfHashType() {
         HashType result = HashType.forNumber(this.hkdfHashType_);
         return result == null ? HashType.UNRECOGNIZED : result;
      }

      public AesCtrHmacStreamingParams.Builder setHkdfHashType(HashType value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 4;
            this.hkdfHashType_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public AesCtrHmacStreamingParams.Builder clearHkdfHashType() {
         this.bitField0_ &= -5;
         this.hkdfHashType_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasHmacParams() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public HmacParams getHmacParams() {
         if (this.hmacParamsBuilder_ == null) {
            return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
         } else {
            return this.hmacParamsBuilder_.getMessage();
         }
      }

      public AesCtrHmacStreamingParams.Builder setHmacParams(HmacParams value) {
         if (this.hmacParamsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.hmacParams_ = value;
         } else {
            this.hmacParamsBuilder_.setMessage(value);
         }

         this.bitField0_ |= 8;
         this.onChanged();
         return this;
      }

      public AesCtrHmacStreamingParams.Builder setHmacParams(HmacParams.Builder builderForValue) {
         if (this.hmacParamsBuilder_ == null) {
            this.hmacParams_ = builderForValue.build();
         } else {
            this.hmacParamsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 8;
         this.onChanged();
         return this;
      }

      public AesCtrHmacStreamingParams.Builder mergeHmacParams(HmacParams value) {
         if (this.hmacParamsBuilder_ == null) {
            if ((this.bitField0_ & 8) != 0 && this.hmacParams_ != null && this.hmacParams_ != HmacParams.getDefaultInstance()) {
               this.getHmacParamsBuilder().mergeFrom(value);
            } else {
               this.hmacParams_ = value;
            }
         } else {
            this.hmacParamsBuilder_.mergeFrom(value);
         }

         if (this.hmacParams_ != null) {
            this.bitField0_ |= 8;
            this.onChanged();
         }

         return this;
      }

      public AesCtrHmacStreamingParams.Builder clearHmacParams() {
         this.bitField0_ &= -9;
         this.hmacParams_ = null;
         if (this.hmacParamsBuilder_ != null) {
            this.hmacParamsBuilder_.dispose();
            this.hmacParamsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public HmacParams.Builder getHmacParamsBuilder() {
         this.bitField0_ |= 8;
         this.onChanged();
         return this.internalGetHmacParamsFieldBuilder().getBuilder();
      }

      @Override
      public HmacParamsOrBuilder getHmacParamsOrBuilder() {
         if (this.hmacParamsBuilder_ != null) {
            return this.hmacParamsBuilder_.getMessageOrBuilder();
         } else {
            return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
         }
      }

      private SingleFieldBuilder<HmacParams, HmacParams.Builder, HmacParamsOrBuilder> internalGetHmacParamsFieldBuilder() {
         if (this.hmacParamsBuilder_ == null) {
            this.hmacParamsBuilder_ = new SingleFieldBuilder<>(this.getHmacParams(), this.getParentForChildren(), this.isClean());
            this.hmacParams_ = null;
         }

         return this.hmacParamsBuilder_;
      }
   }
}
