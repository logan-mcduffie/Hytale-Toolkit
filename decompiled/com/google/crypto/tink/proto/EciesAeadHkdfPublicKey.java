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

public final class EciesAeadHkdfPublicKey extends GeneratedMessage implements EciesAeadHkdfPublicKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int PARAMS_FIELD_NUMBER = 2;
   private EciesAeadHkdfParams params_;
   public static final int X_FIELD_NUMBER = 3;
   private ByteString x_ = ByteString.EMPTY;
   public static final int Y_FIELD_NUMBER = 4;
   private ByteString y_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final EciesAeadHkdfPublicKey DEFAULT_INSTANCE = new EciesAeadHkdfPublicKey();
   private static final Parser<EciesAeadHkdfPublicKey> PARSER = new AbstractParser<EciesAeadHkdfPublicKey>() {
      public EciesAeadHkdfPublicKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         EciesAeadHkdfPublicKey.Builder builder = EciesAeadHkdfPublicKey.newBuilder();

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

   private EciesAeadHkdfPublicKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private EciesAeadHkdfPublicKey() {
      this.x_ = ByteString.EMPTY;
      this.y_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfPublicKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfPublicKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(EciesAeadHkdfPublicKey.class, EciesAeadHkdfPublicKey.Builder.class);
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
   public EciesAeadHkdfParams getParams() {
      return this.params_ == null ? EciesAeadHkdfParams.getDefaultInstance() : this.params_;
   }

   @Override
   public EciesAeadHkdfParamsOrBuilder getParamsOrBuilder() {
      return this.params_ == null ? EciesAeadHkdfParams.getDefaultInstance() : this.params_;
   }

   @Override
   public ByteString getX() {
      return this.x_;
   }

   @Override
   public ByteString getY() {
      return this.y_;
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

      if (!this.x_.isEmpty()) {
         output.writeBytes(3, this.x_);
      }

      if (!this.y_.isEmpty()) {
         output.writeBytes(4, this.y_);
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

         if (!this.x_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.x_);
         }

         if (!this.y_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.y_);
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
      } else if (!(obj instanceof EciesAeadHkdfPublicKey)) {
         return super.equals(obj);
      } else {
         EciesAeadHkdfPublicKey other = (EciesAeadHkdfPublicKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.hasParams() != other.hasParams()) {
            return false;
         } else if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
         } else if (!this.getX().equals(other.getX())) {
            return false;
         } else {
            return !this.getY().equals(other.getY()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getX().hashCode();
         hash = 37 * hash + 4;
         hash = 53 * hash + this.getY().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static EciesAeadHkdfPublicKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EciesAeadHkdfPublicKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EciesAeadHkdfPublicKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EciesAeadHkdfPublicKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EciesAeadHkdfPublicKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EciesAeadHkdfPublicKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EciesAeadHkdfPublicKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static EciesAeadHkdfPublicKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static EciesAeadHkdfPublicKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static EciesAeadHkdfPublicKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static EciesAeadHkdfPublicKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static EciesAeadHkdfPublicKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public EciesAeadHkdfPublicKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static EciesAeadHkdfPublicKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static EciesAeadHkdfPublicKey.Builder newBuilder(EciesAeadHkdfPublicKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public EciesAeadHkdfPublicKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new EciesAeadHkdfPublicKey.Builder() : new EciesAeadHkdfPublicKey.Builder().mergeFrom(this);
   }

   protected EciesAeadHkdfPublicKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new EciesAeadHkdfPublicKey.Builder(parent);
   }

   public static EciesAeadHkdfPublicKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<EciesAeadHkdfPublicKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<EciesAeadHkdfPublicKey> getParserForType() {
      return PARSER;
   }

   public EciesAeadHkdfPublicKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EciesAeadHkdfPublicKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<EciesAeadHkdfPublicKey.Builder> implements EciesAeadHkdfPublicKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private EciesAeadHkdfParams params_;
      private SingleFieldBuilder<EciesAeadHkdfParams, EciesAeadHkdfParams.Builder, EciesAeadHkdfParamsOrBuilder> paramsBuilder_;
      private ByteString x_ = ByteString.EMPTY;
      private ByteString y_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfPublicKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfPublicKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(EciesAeadHkdfPublicKey.class, EciesAeadHkdfPublicKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (EciesAeadHkdfPublicKey.alwaysUseFieldBuilders) {
            this.internalGetParamsFieldBuilder();
         }
      }

      public EciesAeadHkdfPublicKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.x_ = ByteString.EMPTY;
         this.y_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfPublicKey_descriptor;
      }

      public EciesAeadHkdfPublicKey getDefaultInstanceForType() {
         return EciesAeadHkdfPublicKey.getDefaultInstance();
      }

      public EciesAeadHkdfPublicKey build() {
         EciesAeadHkdfPublicKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public EciesAeadHkdfPublicKey buildPartial() {
         EciesAeadHkdfPublicKey result = new EciesAeadHkdfPublicKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(EciesAeadHkdfPublicKey result) {
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
            result.x_ = this.x_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.y_ = this.y_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public EciesAeadHkdfPublicKey.Builder mergeFrom(Message other) {
         if (other instanceof EciesAeadHkdfPublicKey) {
            return this.mergeFrom((EciesAeadHkdfPublicKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public EciesAeadHkdfPublicKey.Builder mergeFrom(EciesAeadHkdfPublicKey other) {
         if (other == EciesAeadHkdfPublicKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.hasParams()) {
               this.mergeParams(other.getParams());
            }

            if (!other.getX().isEmpty()) {
               this.setX(other.getX());
            }

            if (!other.getY().isEmpty()) {
               this.setY(other.getY());
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

      public EciesAeadHkdfPublicKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.x_ = input.readBytes();
                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        this.y_ = input.readBytes();
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
      public int getVersion() {
         return this.version_;
      }

      public EciesAeadHkdfPublicKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public EciesAeadHkdfPublicKey.Builder clearVersion() {
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
      public EciesAeadHkdfParams getParams() {
         if (this.paramsBuilder_ == null) {
            return this.params_ == null ? EciesAeadHkdfParams.getDefaultInstance() : this.params_;
         } else {
            return this.paramsBuilder_.getMessage();
         }
      }

      public EciesAeadHkdfPublicKey.Builder setParams(EciesAeadHkdfParams value) {
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

      public EciesAeadHkdfPublicKey.Builder setParams(EciesAeadHkdfParams.Builder builderForValue) {
         if (this.paramsBuilder_ == null) {
            this.params_ = builderForValue.build();
         } else {
            this.paramsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public EciesAeadHkdfPublicKey.Builder mergeParams(EciesAeadHkdfParams value) {
         if (this.paramsBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.params_ != null && this.params_ != EciesAeadHkdfParams.getDefaultInstance()) {
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

      public EciesAeadHkdfPublicKey.Builder clearParams() {
         this.bitField0_ &= -3;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public EciesAeadHkdfParams.Builder getParamsBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetParamsFieldBuilder().getBuilder();
      }

      @Override
      public EciesAeadHkdfParamsOrBuilder getParamsOrBuilder() {
         if (this.paramsBuilder_ != null) {
            return this.paramsBuilder_.getMessageOrBuilder();
         } else {
            return this.params_ == null ? EciesAeadHkdfParams.getDefaultInstance() : this.params_;
         }
      }

      private SingleFieldBuilder<EciesAeadHkdfParams, EciesAeadHkdfParams.Builder, EciesAeadHkdfParamsOrBuilder> internalGetParamsFieldBuilder() {
         if (this.paramsBuilder_ == null) {
            this.paramsBuilder_ = new SingleFieldBuilder<>(this.getParams(), this.getParentForChildren(), this.isClean());
            this.params_ = null;
         }

         return this.paramsBuilder_;
      }

      @Override
      public ByteString getX() {
         return this.x_;
      }

      public EciesAeadHkdfPublicKey.Builder setX(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.x_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public EciesAeadHkdfPublicKey.Builder clearX() {
         this.bitField0_ &= -5;
         this.x_ = EciesAeadHkdfPublicKey.getDefaultInstance().getX();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getY() {
         return this.y_;
      }

      public EciesAeadHkdfPublicKey.Builder setY(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.y_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public EciesAeadHkdfPublicKey.Builder clearY() {
         this.bitField0_ &= -9;
         this.y_ = EciesAeadHkdfPublicKey.getDefaultInstance().getY();
         this.onChanged();
         return this;
      }
   }
}
