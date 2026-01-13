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

public final class Ed25519PrivateKey extends GeneratedMessage implements Ed25519PrivateKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int KEY_VALUE_FIELD_NUMBER = 2;
   private ByteString keyValue_ = ByteString.EMPTY;
   public static final int PUBLIC_KEY_FIELD_NUMBER = 3;
   private Ed25519PublicKey publicKey_;
   private byte memoizedIsInitialized = -1;
   private static final Ed25519PrivateKey DEFAULT_INSTANCE = new Ed25519PrivateKey();
   private static final Parser<Ed25519PrivateKey> PARSER = new AbstractParser<Ed25519PrivateKey>() {
      public Ed25519PrivateKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Ed25519PrivateKey.Builder builder = Ed25519PrivateKey.newBuilder();

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

   private Ed25519PrivateKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Ed25519PrivateKey() {
      this.keyValue_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Ed25519.internal_static_google_crypto_tink_Ed25519PrivateKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Ed25519.internal_static_google_crypto_tink_Ed25519PrivateKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(Ed25519PrivateKey.class, Ed25519PrivateKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public ByteString getKeyValue() {
      return this.keyValue_;
   }

   @Override
   public boolean hasPublicKey() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public Ed25519PublicKey getPublicKey() {
      return this.publicKey_ == null ? Ed25519PublicKey.getDefaultInstance() : this.publicKey_;
   }

   @Override
   public Ed25519PublicKeyOrBuilder getPublicKeyOrBuilder() {
      return this.publicKey_ == null ? Ed25519PublicKey.getDefaultInstance() : this.publicKey_;
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

      if (!this.keyValue_.isEmpty()) {
         output.writeBytes(2, this.keyValue_);
      }

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(3, this.getPublicKey());
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

         if (!this.keyValue_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.keyValue_);
         }

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(3, this.getPublicKey());
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
      } else if (!(obj instanceof Ed25519PrivateKey)) {
         return super.equals(obj);
      } else {
         Ed25519PrivateKey other = (Ed25519PrivateKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (!this.getKeyValue().equals(other.getKeyValue())) {
            return false;
         } else if (this.hasPublicKey() != other.hasPublicKey()) {
            return false;
         } else {
            return this.hasPublicKey() && !this.getPublicKey().equals(other.getPublicKey()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getKeyValue().hashCode();
         if (this.hasPublicKey()) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getPublicKey().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Ed25519PrivateKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Ed25519PrivateKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Ed25519PrivateKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Ed25519PrivateKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Ed25519PrivateKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Ed25519PrivateKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Ed25519PrivateKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Ed25519PrivateKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Ed25519PrivateKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Ed25519PrivateKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Ed25519PrivateKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Ed25519PrivateKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Ed25519PrivateKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Ed25519PrivateKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Ed25519PrivateKey.Builder newBuilder(Ed25519PrivateKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Ed25519PrivateKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Ed25519PrivateKey.Builder() : new Ed25519PrivateKey.Builder().mergeFrom(this);
   }

   protected Ed25519PrivateKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Ed25519PrivateKey.Builder(parent);
   }

   public static Ed25519PrivateKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Ed25519PrivateKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<Ed25519PrivateKey> getParserForType() {
      return PARSER;
   }

   public Ed25519PrivateKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Ed25519PrivateKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<Ed25519PrivateKey.Builder> implements Ed25519PrivateKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private ByteString keyValue_ = ByteString.EMPTY;
      private Ed25519PublicKey publicKey_;
      private SingleFieldBuilder<Ed25519PublicKey, Ed25519PublicKey.Builder, Ed25519PublicKeyOrBuilder> publicKeyBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return Ed25519.internal_static_google_crypto_tink_Ed25519PrivateKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Ed25519.internal_static_google_crypto_tink_Ed25519PrivateKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(Ed25519PrivateKey.class, Ed25519PrivateKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (Ed25519PrivateKey.alwaysUseFieldBuilders) {
            this.internalGetPublicKeyFieldBuilder();
         }
      }

      public Ed25519PrivateKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.keyValue_ = ByteString.EMPTY;
         this.publicKey_ = null;
         if (this.publicKeyBuilder_ != null) {
            this.publicKeyBuilder_.dispose();
            this.publicKeyBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Ed25519.internal_static_google_crypto_tink_Ed25519PrivateKey_descriptor;
      }

      public Ed25519PrivateKey getDefaultInstanceForType() {
         return Ed25519PrivateKey.getDefaultInstance();
      }

      public Ed25519PrivateKey build() {
         Ed25519PrivateKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Ed25519PrivateKey buildPartial() {
         Ed25519PrivateKey result = new Ed25519PrivateKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(Ed25519PrivateKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.keyValue_ = this.keyValue_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 4) != 0) {
            result.publicKey_ = this.publicKeyBuilder_ == null ? this.publicKey_ : this.publicKeyBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public Ed25519PrivateKey.Builder mergeFrom(Message other) {
         if (other instanceof Ed25519PrivateKey) {
            return this.mergeFrom((Ed25519PrivateKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Ed25519PrivateKey.Builder mergeFrom(Ed25519PrivateKey other) {
         if (other == Ed25519PrivateKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (!other.getKeyValue().isEmpty()) {
               this.setKeyValue(other.getKeyValue());
            }

            if (other.hasPublicKey()) {
               this.mergePublicKey(other.getPublicKey());
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

      public Ed25519PrivateKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.keyValue_ = input.readBytes();
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        input.readMessage(this.internalGetPublicKeyFieldBuilder().getBuilder(), extensionRegistry);
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

      public Ed25519PrivateKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public Ed25519PrivateKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getKeyValue() {
         return this.keyValue_;
      }

      public Ed25519PrivateKey.Builder setKeyValue(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.keyValue_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }
      }

      public Ed25519PrivateKey.Builder clearKeyValue() {
         this.bitField0_ &= -3;
         this.keyValue_ = Ed25519PrivateKey.getDefaultInstance().getKeyValue();
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasPublicKey() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public Ed25519PublicKey getPublicKey() {
         if (this.publicKeyBuilder_ == null) {
            return this.publicKey_ == null ? Ed25519PublicKey.getDefaultInstance() : this.publicKey_;
         } else {
            return this.publicKeyBuilder_.getMessage();
         }
      }

      public Ed25519PrivateKey.Builder setPublicKey(Ed25519PublicKey value) {
         if (this.publicKeyBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.publicKey_ = value;
         } else {
            this.publicKeyBuilder_.setMessage(value);
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public Ed25519PrivateKey.Builder setPublicKey(Ed25519PublicKey.Builder builderForValue) {
         if (this.publicKeyBuilder_ == null) {
            this.publicKey_ = builderForValue.build();
         } else {
            this.publicKeyBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public Ed25519PrivateKey.Builder mergePublicKey(Ed25519PublicKey value) {
         if (this.publicKeyBuilder_ == null) {
            if ((this.bitField0_ & 4) != 0 && this.publicKey_ != null && this.publicKey_ != Ed25519PublicKey.getDefaultInstance()) {
               this.getPublicKeyBuilder().mergeFrom(value);
            } else {
               this.publicKey_ = value;
            }
         } else {
            this.publicKeyBuilder_.mergeFrom(value);
         }

         if (this.publicKey_ != null) {
            this.bitField0_ |= 4;
            this.onChanged();
         }

         return this;
      }

      public Ed25519PrivateKey.Builder clearPublicKey() {
         this.bitField0_ &= -5;
         this.publicKey_ = null;
         if (this.publicKeyBuilder_ != null) {
            this.publicKeyBuilder_.dispose();
            this.publicKeyBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public Ed25519PublicKey.Builder getPublicKeyBuilder() {
         this.bitField0_ |= 4;
         this.onChanged();
         return this.internalGetPublicKeyFieldBuilder().getBuilder();
      }

      @Override
      public Ed25519PublicKeyOrBuilder getPublicKeyOrBuilder() {
         if (this.publicKeyBuilder_ != null) {
            return this.publicKeyBuilder_.getMessageOrBuilder();
         } else {
            return this.publicKey_ == null ? Ed25519PublicKey.getDefaultInstance() : this.publicKey_;
         }
      }

      private SingleFieldBuilder<Ed25519PublicKey, Ed25519PublicKey.Builder, Ed25519PublicKeyOrBuilder> internalGetPublicKeyFieldBuilder() {
         if (this.publicKeyBuilder_ == null) {
            this.publicKeyBuilder_ = new SingleFieldBuilder<>(this.getPublicKey(), this.getParentForChildren(), this.isClean());
            this.publicKey_ = null;
         }

         return this.publicKeyBuilder_;
      }
   }
}
