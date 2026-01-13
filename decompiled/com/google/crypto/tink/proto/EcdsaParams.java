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

public final class EcdsaParams extends GeneratedMessage implements EcdsaParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int HASH_TYPE_FIELD_NUMBER = 1;
   private int hashType_ = 0;
   public static final int CURVE_FIELD_NUMBER = 2;
   private int curve_ = 0;
   public static final int ENCODING_FIELD_NUMBER = 3;
   private int encoding_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final EcdsaParams DEFAULT_INSTANCE = new EcdsaParams();
   private static final Parser<EcdsaParams> PARSER = new AbstractParser<EcdsaParams>() {
      public EcdsaParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         EcdsaParams.Builder builder = EcdsaParams.newBuilder();

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

   private EcdsaParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private EcdsaParams() {
      this.hashType_ = 0;
      this.curve_ = 0;
      this.encoding_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(EcdsaParams.class, EcdsaParams.Builder.class);
   }

   @Override
   public int getHashTypeValue() {
      return this.hashType_;
   }

   @Override
   public HashType getHashType() {
      HashType result = HashType.forNumber(this.hashType_);
      return result == null ? HashType.UNRECOGNIZED : result;
   }

   @Override
   public int getCurveValue() {
      return this.curve_;
   }

   @Override
   public EllipticCurveType getCurve() {
      EllipticCurveType result = EllipticCurveType.forNumber(this.curve_);
      return result == null ? EllipticCurveType.UNRECOGNIZED : result;
   }

   @Override
   public int getEncodingValue() {
      return this.encoding_;
   }

   @Override
   public EcdsaSignatureEncoding getEncoding() {
      EcdsaSignatureEncoding result = EcdsaSignatureEncoding.forNumber(this.encoding_);
      return result == null ? EcdsaSignatureEncoding.UNRECOGNIZED : result;
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
      if (this.hashType_ != HashType.UNKNOWN_HASH.getNumber()) {
         output.writeEnum(1, this.hashType_);
      }

      if (this.curve_ != EllipticCurveType.UNKNOWN_CURVE.getNumber()) {
         output.writeEnum(2, this.curve_);
      }

      if (this.encoding_ != EcdsaSignatureEncoding.UNKNOWN_ENCODING.getNumber()) {
         output.writeEnum(3, this.encoding_);
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
         if (this.hashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.hashType_);
         }

         if (this.curve_ != EllipticCurveType.UNKNOWN_CURVE.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.curve_);
         }

         if (this.encoding_ != EcdsaSignatureEncoding.UNKNOWN_ENCODING.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.encoding_);
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
      } else if (!(obj instanceof EcdsaParams)) {
         return super.equals(obj);
      } else {
         EcdsaParams other = (EcdsaParams)obj;
         if (this.hashType_ != other.hashType_) {
            return false;
         } else if (this.curve_ != other.curve_) {
            return false;
         } else {
            return this.encoding_ != other.encoding_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.hashType_;
         hash = 37 * hash + 2;
         hash = 53 * hash + this.curve_;
         hash = 37 * hash + 3;
         hash = 53 * hash + this.encoding_;
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static EcdsaParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EcdsaParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EcdsaParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EcdsaParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EcdsaParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EcdsaParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EcdsaParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static EcdsaParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static EcdsaParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static EcdsaParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static EcdsaParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static EcdsaParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public EcdsaParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static EcdsaParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static EcdsaParams.Builder newBuilder(EcdsaParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public EcdsaParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new EcdsaParams.Builder() : new EcdsaParams.Builder().mergeFrom(this);
   }

   protected EcdsaParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new EcdsaParams.Builder(parent);
   }

   public static EcdsaParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<EcdsaParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<EcdsaParams> getParserForType() {
      return PARSER;
   }

   public EcdsaParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EcdsaParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<EcdsaParams.Builder> implements EcdsaParamsOrBuilder {
      private int bitField0_;
      private int hashType_ = 0;
      private int curve_ = 0;
      private int encoding_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(EcdsaParams.class, EcdsaParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public EcdsaParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.hashType_ = 0;
         this.curve_ = 0;
         this.encoding_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_descriptor;
      }

      public EcdsaParams getDefaultInstanceForType() {
         return EcdsaParams.getDefaultInstance();
      }

      public EcdsaParams build() {
         EcdsaParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public EcdsaParams buildPartial() {
         EcdsaParams result = new EcdsaParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(EcdsaParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.hashType_ = this.hashType_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.curve_ = this.curve_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.encoding_ = this.encoding_;
         }
      }

      public EcdsaParams.Builder mergeFrom(Message other) {
         if (other instanceof EcdsaParams) {
            return this.mergeFrom((EcdsaParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public EcdsaParams.Builder mergeFrom(EcdsaParams other) {
         if (other == EcdsaParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.hashType_ != 0) {
               this.setHashTypeValue(other.getHashTypeValue());
            }

            if (other.curve_ != 0) {
               this.setCurveValue(other.getCurveValue());
            }

            if (other.encoding_ != 0) {
               this.setEncodingValue(other.getEncodingValue());
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

      public EcdsaParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.hashType_ = input.readEnum();
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.curve_ = input.readEnum();
                        this.bitField0_ |= 2;
                        break;
                     case 24:
                        this.encoding_ = input.readEnum();
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
      public int getHashTypeValue() {
         return this.hashType_;
      }

      public EcdsaParams.Builder setHashTypeValue(int value) {
         this.hashType_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      @Override
      public HashType getHashType() {
         HashType result = HashType.forNumber(this.hashType_);
         return result == null ? HashType.UNRECOGNIZED : result;
      }

      public EcdsaParams.Builder setHashType(HashType value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 1;
            this.hashType_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public EcdsaParams.Builder clearHashType() {
         this.bitField0_ &= -2;
         this.hashType_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getCurveValue() {
         return this.curve_;
      }

      public EcdsaParams.Builder setCurveValue(int value) {
         this.curve_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      @Override
      public EllipticCurveType getCurve() {
         EllipticCurveType result = EllipticCurveType.forNumber(this.curve_);
         return result == null ? EllipticCurveType.UNRECOGNIZED : result;
      }

      public EcdsaParams.Builder setCurve(EllipticCurveType value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 2;
            this.curve_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public EcdsaParams.Builder clearCurve() {
         this.bitField0_ &= -3;
         this.curve_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getEncodingValue() {
         return this.encoding_;
      }

      public EcdsaParams.Builder setEncodingValue(int value) {
         this.encoding_ = value;
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      @Override
      public EcdsaSignatureEncoding getEncoding() {
         EcdsaSignatureEncoding result = EcdsaSignatureEncoding.forNumber(this.encoding_);
         return result == null ? EcdsaSignatureEncoding.UNRECOGNIZED : result;
      }

      public EcdsaParams.Builder setEncoding(EcdsaSignatureEncoding value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 4;
            this.encoding_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public EcdsaParams.Builder clearEncoding() {
         this.bitField0_ &= -5;
         this.encoding_ = 0;
         this.onChanged();
         return this;
      }
   }
}
