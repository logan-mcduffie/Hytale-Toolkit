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

public final class SlhDsaPrivateKey extends GeneratedMessage implements SlhDsaPrivateKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int KEY_VALUE_FIELD_NUMBER = 2;
   private ByteString keyValue_ = ByteString.EMPTY;
   public static final int PUBLIC_KEY_FIELD_NUMBER = 3;
   private SlhDsaPublicKey publicKey_;
   private byte memoizedIsInitialized = -1;
   private static final SlhDsaPrivateKey DEFAULT_INSTANCE = new SlhDsaPrivateKey();
   private static final Parser<SlhDsaPrivateKey> PARSER = new AbstractParser<SlhDsaPrivateKey>() {
      public SlhDsaPrivateKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         SlhDsaPrivateKey.Builder builder = SlhDsaPrivateKey.newBuilder();

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

   private SlhDsaPrivateKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private SlhDsaPrivateKey() {
      this.keyValue_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(SlhDsaPrivateKey.class, SlhDsaPrivateKey.Builder.class);
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
   public SlhDsaPublicKey getPublicKey() {
      return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
   }

   @Override
   public SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder() {
      return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
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
      } else if (!(obj instanceof SlhDsaPrivateKey)) {
         return super.equals(obj);
      } else {
         SlhDsaPrivateKey other = (SlhDsaPrivateKey)obj;
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

   public static SlhDsaPrivateKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static SlhDsaPrivateKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static SlhDsaPrivateKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static SlhDsaPrivateKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static SlhDsaPrivateKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static SlhDsaPrivateKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static SlhDsaPrivateKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static SlhDsaPrivateKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static SlhDsaPrivateKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static SlhDsaPrivateKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static SlhDsaPrivateKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static SlhDsaPrivateKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public SlhDsaPrivateKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static SlhDsaPrivateKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static SlhDsaPrivateKey.Builder newBuilder(SlhDsaPrivateKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public SlhDsaPrivateKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new SlhDsaPrivateKey.Builder() : new SlhDsaPrivateKey.Builder().mergeFrom(this);
   }

   protected SlhDsaPrivateKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new SlhDsaPrivateKey.Builder(parent);
   }

   public static SlhDsaPrivateKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<SlhDsaPrivateKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<SlhDsaPrivateKey> getParserForType() {
      return PARSER;
   }

   public SlhDsaPrivateKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsaPrivateKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<SlhDsaPrivateKey.Builder> implements SlhDsaPrivateKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private ByteString keyValue_ = ByteString.EMPTY;
      private SlhDsaPublicKey publicKey_;
      private SingleFieldBuilder<SlhDsaPublicKey, SlhDsaPublicKey.Builder, SlhDsaPublicKeyOrBuilder> publicKeyBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(SlhDsaPrivateKey.class, SlhDsaPrivateKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (SlhDsaPrivateKey.alwaysUseFieldBuilders) {
            this.internalGetPublicKeyFieldBuilder();
         }
      }

      public SlhDsaPrivateKey.Builder clear() {
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
         return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor;
      }

      public SlhDsaPrivateKey getDefaultInstanceForType() {
         return SlhDsaPrivateKey.getDefaultInstance();
      }

      public SlhDsaPrivateKey build() {
         SlhDsaPrivateKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public SlhDsaPrivateKey buildPartial() {
         SlhDsaPrivateKey result = new SlhDsaPrivateKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(SlhDsaPrivateKey result) {
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

      public SlhDsaPrivateKey.Builder mergeFrom(Message other) {
         if (other instanceof SlhDsaPrivateKey) {
            return this.mergeFrom((SlhDsaPrivateKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public SlhDsaPrivateKey.Builder mergeFrom(SlhDsaPrivateKey other) {
         if (other == SlhDsaPrivateKey.getDefaultInstance()) {
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

      public SlhDsaPrivateKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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

      public SlhDsaPrivateKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public SlhDsaPrivateKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getKeyValue() {
         return this.keyValue_;
      }

      public SlhDsaPrivateKey.Builder setKeyValue(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.keyValue_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }
      }

      public SlhDsaPrivateKey.Builder clearKeyValue() {
         this.bitField0_ &= -3;
         this.keyValue_ = SlhDsaPrivateKey.getDefaultInstance().getKeyValue();
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasPublicKey() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public SlhDsaPublicKey getPublicKey() {
         if (this.publicKeyBuilder_ == null) {
            return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
         } else {
            return this.publicKeyBuilder_.getMessage();
         }
      }

      public SlhDsaPrivateKey.Builder setPublicKey(SlhDsaPublicKey value) {
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

      public SlhDsaPrivateKey.Builder setPublicKey(SlhDsaPublicKey.Builder builderForValue) {
         if (this.publicKeyBuilder_ == null) {
            this.publicKey_ = builderForValue.build();
         } else {
            this.publicKeyBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public SlhDsaPrivateKey.Builder mergePublicKey(SlhDsaPublicKey value) {
         if (this.publicKeyBuilder_ == null) {
            if ((this.bitField0_ & 4) != 0 && this.publicKey_ != null && this.publicKey_ != SlhDsaPublicKey.getDefaultInstance()) {
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

      public SlhDsaPrivateKey.Builder clearPublicKey() {
         this.bitField0_ &= -5;
         this.publicKey_ = null;
         if (this.publicKeyBuilder_ != null) {
            this.publicKeyBuilder_.dispose();
            this.publicKeyBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public SlhDsaPublicKey.Builder getPublicKeyBuilder() {
         this.bitField0_ |= 4;
         this.onChanged();
         return this.internalGetPublicKeyFieldBuilder().getBuilder();
      }

      @Override
      public SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder() {
         if (this.publicKeyBuilder_ != null) {
            return this.publicKeyBuilder_.getMessageOrBuilder();
         } else {
            return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
         }
      }

      private SingleFieldBuilder<SlhDsaPublicKey, SlhDsaPublicKey.Builder, SlhDsaPublicKeyOrBuilder> internalGetPublicKeyFieldBuilder() {
         if (this.publicKeyBuilder_ == null) {
            this.publicKeyBuilder_ = new SingleFieldBuilder<>(this.getPublicKey(), this.getParentForChildren(), this.isClean());
            this.publicKey_ = null;
         }

         return this.publicKeyBuilder_;
      }
   }
}
