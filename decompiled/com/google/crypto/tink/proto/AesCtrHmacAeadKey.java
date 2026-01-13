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

public final class AesCtrHmacAeadKey extends GeneratedMessage implements AesCtrHmacAeadKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int AES_CTR_KEY_FIELD_NUMBER = 2;
   private AesCtrKey aesCtrKey_;
   public static final int HMAC_KEY_FIELD_NUMBER = 3;
   private HmacKey hmacKey_;
   private byte memoizedIsInitialized = -1;
   private static final AesCtrHmacAeadKey DEFAULT_INSTANCE = new AesCtrHmacAeadKey();
   private static final Parser<AesCtrHmacAeadKey> PARSER = new AbstractParser<AesCtrHmacAeadKey>() {
      public AesCtrHmacAeadKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesCtrHmacAeadKey.Builder builder = AesCtrHmacAeadKey.newBuilder();

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

   private AesCtrHmacAeadKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesCtrHmacAeadKey() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesCtrHmacAeadKey.class, AesCtrHmacAeadKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public boolean hasAesCtrKey() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public AesCtrKey getAesCtrKey() {
      return this.aesCtrKey_ == null ? AesCtrKey.getDefaultInstance() : this.aesCtrKey_;
   }

   @Override
   public AesCtrKeyOrBuilder getAesCtrKeyOrBuilder() {
      return this.aesCtrKey_ == null ? AesCtrKey.getDefaultInstance() : this.aesCtrKey_;
   }

   @Override
   public boolean hasHmacKey() {
      return (this.bitField0_ & 2) != 0;
   }

   @Override
   public HmacKey getHmacKey() {
      return this.hmacKey_ == null ? HmacKey.getDefaultInstance() : this.hmacKey_;
   }

   @Override
   public HmacKeyOrBuilder getHmacKeyOrBuilder() {
      return this.hmacKey_ == null ? HmacKey.getDefaultInstance() : this.hmacKey_;
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
         output.writeMessage(2, this.getAesCtrKey());
      }

      if ((this.bitField0_ & 2) != 0) {
         output.writeMessage(3, this.getHmacKey());
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
            size += CodedOutputStream.computeMessageSize(2, this.getAesCtrKey());
         }

         if ((this.bitField0_ & 2) != 0) {
            size += CodedOutputStream.computeMessageSize(3, this.getHmacKey());
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
      } else if (!(obj instanceof AesCtrHmacAeadKey)) {
         return super.equals(obj);
      } else {
         AesCtrHmacAeadKey other = (AesCtrHmacAeadKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.hasAesCtrKey() != other.hasAesCtrKey()) {
            return false;
         } else if (this.hasAesCtrKey() && !this.getAesCtrKey().equals(other.getAesCtrKey())) {
            return false;
         } else if (this.hasHmacKey() != other.hasHmacKey()) {
            return false;
         } else {
            return this.hasHmacKey() && !this.getHmacKey().equals(other.getHmacKey()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasAesCtrKey()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getAesCtrKey().hashCode();
         }

         if (this.hasHmacKey()) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getHmacKey().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesCtrHmacAeadKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrHmacAeadKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrHmacAeadKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrHmacAeadKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrHmacAeadKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrHmacAeadKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrHmacAeadKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrHmacAeadKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrHmacAeadKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesCtrHmacAeadKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrHmacAeadKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrHmacAeadKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesCtrHmacAeadKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesCtrHmacAeadKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesCtrHmacAeadKey.Builder newBuilder(AesCtrHmacAeadKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesCtrHmacAeadKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesCtrHmacAeadKey.Builder() : new AesCtrHmacAeadKey.Builder().mergeFrom(this);
   }

   protected AesCtrHmacAeadKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesCtrHmacAeadKey.Builder(parent);
   }

   public static AesCtrHmacAeadKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesCtrHmacAeadKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesCtrHmacAeadKey> getParserForType() {
      return PARSER;
   }

   public AesCtrHmacAeadKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrHmacAeadKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesCtrHmacAeadKey.Builder> implements AesCtrHmacAeadKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private AesCtrKey aesCtrKey_;
      private SingleFieldBuilder<AesCtrKey, AesCtrKey.Builder, AesCtrKeyOrBuilder> aesCtrKeyBuilder_;
      private HmacKey hmacKey_;
      private SingleFieldBuilder<HmacKey, HmacKey.Builder, HmacKeyOrBuilder> hmacKeyBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesCtrHmacAeadKey.class, AesCtrHmacAeadKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (AesCtrHmacAeadKey.alwaysUseFieldBuilders) {
            this.internalGetAesCtrKeyFieldBuilder();
            this.internalGetHmacKeyFieldBuilder();
         }
      }

      public AesCtrHmacAeadKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.aesCtrKey_ = null;
         if (this.aesCtrKeyBuilder_ != null) {
            this.aesCtrKeyBuilder_.dispose();
            this.aesCtrKeyBuilder_ = null;
         }

         this.hmacKey_ = null;
         if (this.hmacKeyBuilder_ != null) {
            this.hmacKeyBuilder_.dispose();
            this.hmacKeyBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKey_descriptor;
      }

      public AesCtrHmacAeadKey getDefaultInstanceForType() {
         return AesCtrHmacAeadKey.getDefaultInstance();
      }

      public AesCtrHmacAeadKey build() {
         AesCtrHmacAeadKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesCtrHmacAeadKey buildPartial() {
         AesCtrHmacAeadKey result = new AesCtrHmacAeadKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesCtrHmacAeadKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 2) != 0) {
            result.aesCtrKey_ = this.aesCtrKeyBuilder_ == null ? this.aesCtrKey_ : this.aesCtrKeyBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.hmacKey_ = this.hmacKeyBuilder_ == null ? this.hmacKey_ : this.hmacKeyBuilder_.build();
            to_bitField0_ |= 2;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public AesCtrHmacAeadKey.Builder mergeFrom(Message other) {
         if (other instanceof AesCtrHmacAeadKey) {
            return this.mergeFrom((AesCtrHmacAeadKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesCtrHmacAeadKey.Builder mergeFrom(AesCtrHmacAeadKey other) {
         if (other == AesCtrHmacAeadKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.hasAesCtrKey()) {
               this.mergeAesCtrKey(other.getAesCtrKey());
            }

            if (other.hasHmacKey()) {
               this.mergeHmacKey(other.getHmacKey());
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

      public AesCtrHmacAeadKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        input.readMessage(this.internalGetAesCtrKeyFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        input.readMessage(this.internalGetHmacKeyFieldBuilder().getBuilder(), extensionRegistry);
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

      public AesCtrHmacAeadKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesCtrHmacAeadKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasAesCtrKey() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public AesCtrKey getAesCtrKey() {
         if (this.aesCtrKeyBuilder_ == null) {
            return this.aesCtrKey_ == null ? AesCtrKey.getDefaultInstance() : this.aesCtrKey_;
         } else {
            return this.aesCtrKeyBuilder_.getMessage();
         }
      }

      public AesCtrHmacAeadKey.Builder setAesCtrKey(AesCtrKey value) {
         if (this.aesCtrKeyBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.aesCtrKey_ = value;
         } else {
            this.aesCtrKeyBuilder_.setMessage(value);
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public AesCtrHmacAeadKey.Builder setAesCtrKey(AesCtrKey.Builder builderForValue) {
         if (this.aesCtrKeyBuilder_ == null) {
            this.aesCtrKey_ = builderForValue.build();
         } else {
            this.aesCtrKeyBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public AesCtrHmacAeadKey.Builder mergeAesCtrKey(AesCtrKey value) {
         if (this.aesCtrKeyBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.aesCtrKey_ != null && this.aesCtrKey_ != AesCtrKey.getDefaultInstance()) {
               this.getAesCtrKeyBuilder().mergeFrom(value);
            } else {
               this.aesCtrKey_ = value;
            }
         } else {
            this.aesCtrKeyBuilder_.mergeFrom(value);
         }

         if (this.aesCtrKey_ != null) {
            this.bitField0_ |= 2;
            this.onChanged();
         }

         return this;
      }

      public AesCtrHmacAeadKey.Builder clearAesCtrKey() {
         this.bitField0_ &= -3;
         this.aesCtrKey_ = null;
         if (this.aesCtrKeyBuilder_ != null) {
            this.aesCtrKeyBuilder_.dispose();
            this.aesCtrKeyBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public AesCtrKey.Builder getAesCtrKeyBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetAesCtrKeyFieldBuilder().getBuilder();
      }

      @Override
      public AesCtrKeyOrBuilder getAesCtrKeyOrBuilder() {
         if (this.aesCtrKeyBuilder_ != null) {
            return this.aesCtrKeyBuilder_.getMessageOrBuilder();
         } else {
            return this.aesCtrKey_ == null ? AesCtrKey.getDefaultInstance() : this.aesCtrKey_;
         }
      }

      private SingleFieldBuilder<AesCtrKey, AesCtrKey.Builder, AesCtrKeyOrBuilder> internalGetAesCtrKeyFieldBuilder() {
         if (this.aesCtrKeyBuilder_ == null) {
            this.aesCtrKeyBuilder_ = new SingleFieldBuilder<>(this.getAesCtrKey(), this.getParentForChildren(), this.isClean());
            this.aesCtrKey_ = null;
         }

         return this.aesCtrKeyBuilder_;
      }

      @Override
      public boolean hasHmacKey() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public HmacKey getHmacKey() {
         if (this.hmacKeyBuilder_ == null) {
            return this.hmacKey_ == null ? HmacKey.getDefaultInstance() : this.hmacKey_;
         } else {
            return this.hmacKeyBuilder_.getMessage();
         }
      }

      public AesCtrHmacAeadKey.Builder setHmacKey(HmacKey value) {
         if (this.hmacKeyBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.hmacKey_ = value;
         } else {
            this.hmacKeyBuilder_.setMessage(value);
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public AesCtrHmacAeadKey.Builder setHmacKey(HmacKey.Builder builderForValue) {
         if (this.hmacKeyBuilder_ == null) {
            this.hmacKey_ = builderForValue.build();
         } else {
            this.hmacKeyBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public AesCtrHmacAeadKey.Builder mergeHmacKey(HmacKey value) {
         if (this.hmacKeyBuilder_ == null) {
            if ((this.bitField0_ & 4) != 0 && this.hmacKey_ != null && this.hmacKey_ != HmacKey.getDefaultInstance()) {
               this.getHmacKeyBuilder().mergeFrom(value);
            } else {
               this.hmacKey_ = value;
            }
         } else {
            this.hmacKeyBuilder_.mergeFrom(value);
         }

         if (this.hmacKey_ != null) {
            this.bitField0_ |= 4;
            this.onChanged();
         }

         return this;
      }

      public AesCtrHmacAeadKey.Builder clearHmacKey() {
         this.bitField0_ &= -5;
         this.hmacKey_ = null;
         if (this.hmacKeyBuilder_ != null) {
            this.hmacKeyBuilder_.dispose();
            this.hmacKeyBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public HmacKey.Builder getHmacKeyBuilder() {
         this.bitField0_ |= 4;
         this.onChanged();
         return this.internalGetHmacKeyFieldBuilder().getBuilder();
      }

      @Override
      public HmacKeyOrBuilder getHmacKeyOrBuilder() {
         if (this.hmacKeyBuilder_ != null) {
            return this.hmacKeyBuilder_.getMessageOrBuilder();
         } else {
            return this.hmacKey_ == null ? HmacKey.getDefaultInstance() : this.hmacKey_;
         }
      }

      private SingleFieldBuilder<HmacKey, HmacKey.Builder, HmacKeyOrBuilder> internalGetHmacKeyFieldBuilder() {
         if (this.hmacKeyBuilder_ == null) {
            this.hmacKeyBuilder_ = new SingleFieldBuilder<>(this.getHmacKey(), this.getParentForChildren(), this.isClean());
            this.hmacKey_ = null;
         }

         return this.hmacKeyBuilder_;
      }
   }
}
