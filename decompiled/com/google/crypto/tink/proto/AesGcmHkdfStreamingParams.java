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

public final class AesGcmHkdfStreamingParams extends GeneratedMessage implements AesGcmHkdfStreamingParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int CIPHERTEXT_SEGMENT_SIZE_FIELD_NUMBER = 1;
   private int ciphertextSegmentSize_ = 0;
   public static final int DERIVED_KEY_SIZE_FIELD_NUMBER = 2;
   private int derivedKeySize_ = 0;
   public static final int HKDF_HASH_TYPE_FIELD_NUMBER = 3;
   private int hkdfHashType_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final AesGcmHkdfStreamingParams DEFAULT_INSTANCE = new AesGcmHkdfStreamingParams();
   private static final Parser<AesGcmHkdfStreamingParams> PARSER = new AbstractParser<AesGcmHkdfStreamingParams>() {
      public AesGcmHkdfStreamingParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesGcmHkdfStreamingParams.Builder builder = AesGcmHkdfStreamingParams.newBuilder();

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

   private AesGcmHkdfStreamingParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesGcmHkdfStreamingParams() {
      this.hkdfHashType_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesGcmHkdfStreaming.internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesGcmHkdfStreaming.internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesGcmHkdfStreamingParams.class, AesGcmHkdfStreamingParams.Builder.class);
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

         size += this.getUnknownFields().getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AesGcmHkdfStreamingParams)) {
         return super.equals(obj);
      } else {
         AesGcmHkdfStreamingParams other = (AesGcmHkdfStreamingParams)obj;
         if (this.getCiphertextSegmentSize() != other.getCiphertextSegmentSize()) {
            return false;
         } else if (this.getDerivedKeySize() != other.getDerivedKeySize()) {
            return false;
         } else {
            return this.hkdfHashType_ != other.hkdfHashType_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesGcmHkdfStreamingParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesGcmHkdfStreamingParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesGcmHkdfStreamingParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesGcmHkdfStreamingParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesGcmHkdfStreamingParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesGcmHkdfStreamingParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesGcmHkdfStreamingParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesGcmHkdfStreamingParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesGcmHkdfStreamingParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesGcmHkdfStreamingParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesGcmHkdfStreamingParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesGcmHkdfStreamingParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesGcmHkdfStreamingParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesGcmHkdfStreamingParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesGcmHkdfStreamingParams.Builder newBuilder(AesGcmHkdfStreamingParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesGcmHkdfStreamingParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesGcmHkdfStreamingParams.Builder() : new AesGcmHkdfStreamingParams.Builder().mergeFrom(this);
   }

   protected AesGcmHkdfStreamingParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesGcmHkdfStreamingParams.Builder(parent);
   }

   public static AesGcmHkdfStreamingParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesGcmHkdfStreamingParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesGcmHkdfStreamingParams> getParserForType() {
      return PARSER;
   }

   public AesGcmHkdfStreamingParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesGcmHkdfStreamingParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesGcmHkdfStreamingParams.Builder> implements AesGcmHkdfStreamingParamsOrBuilder {
      private int bitField0_;
      private int ciphertextSegmentSize_;
      private int derivedKeySize_;
      private int hkdfHashType_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesGcmHkdfStreaming.internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesGcmHkdfStreaming.internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesGcmHkdfStreamingParams.class, AesGcmHkdfStreamingParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public AesGcmHkdfStreamingParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.ciphertextSegmentSize_ = 0;
         this.derivedKeySize_ = 0;
         this.hkdfHashType_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesGcmHkdfStreaming.internal_static_google_crypto_tink_AesGcmHkdfStreamingParams_descriptor;
      }

      public AesGcmHkdfStreamingParams getDefaultInstanceForType() {
         return AesGcmHkdfStreamingParams.getDefaultInstance();
      }

      public AesGcmHkdfStreamingParams build() {
         AesGcmHkdfStreamingParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesGcmHkdfStreamingParams buildPartial() {
         AesGcmHkdfStreamingParams result = new AesGcmHkdfStreamingParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesGcmHkdfStreamingParams result) {
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
      }

      public AesGcmHkdfStreamingParams.Builder mergeFrom(Message other) {
         if (other instanceof AesGcmHkdfStreamingParams) {
            return this.mergeFrom((AesGcmHkdfStreamingParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesGcmHkdfStreamingParams.Builder mergeFrom(AesGcmHkdfStreamingParams other) {
         if (other == AesGcmHkdfStreamingParams.getDefaultInstance()) {
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

            this.mergeUnknownFields(other.getUnknownFields());
            this.onChanged();
            return this;
         }
      }

      @Override
      public final boolean isInitialized() {
         return true;
      }

      public AesGcmHkdfStreamingParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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

      public AesGcmHkdfStreamingParams.Builder setCiphertextSegmentSize(int value) {
         this.ciphertextSegmentSize_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesGcmHkdfStreamingParams.Builder clearCiphertextSegmentSize() {
         this.bitField0_ &= -2;
         this.ciphertextSegmentSize_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getDerivedKeySize() {
         return this.derivedKeySize_;
      }

      public AesGcmHkdfStreamingParams.Builder setDerivedKeySize(int value) {
         this.derivedKeySize_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public AesGcmHkdfStreamingParams.Builder clearDerivedKeySize() {
         this.bitField0_ &= -3;
         this.derivedKeySize_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getHkdfHashTypeValue() {
         return this.hkdfHashType_;
      }

      public AesGcmHkdfStreamingParams.Builder setHkdfHashTypeValue(int value) {
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

      public AesGcmHkdfStreamingParams.Builder setHkdfHashType(HashType value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 4;
            this.hkdfHashType_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public AesGcmHkdfStreamingParams.Builder clearHkdfHashType() {
         this.bitField0_ &= -5;
         this.hkdfHashType_ = 0;
         this.onChanged();
         return this;
      }
   }
}
