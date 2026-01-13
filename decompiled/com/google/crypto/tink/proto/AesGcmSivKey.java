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

public final class AesGcmSivKey extends GeneratedMessage implements AesGcmSivKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int KEY_VALUE_FIELD_NUMBER = 3;
   private ByteString keyValue_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final AesGcmSivKey DEFAULT_INSTANCE = new AesGcmSivKey();
   private static final Parser<AesGcmSivKey> PARSER = new AbstractParser<AesGcmSivKey>() {
      public AesGcmSivKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesGcmSivKey.Builder builder = AesGcmSivKey.newBuilder();

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

   private AesGcmSivKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesGcmSivKey() {
      this.keyValue_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesGcmSiv.internal_static_google_crypto_tink_AesGcmSivKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesGcmSiv.internal_static_google_crypto_tink_AesGcmSivKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesGcmSivKey.class, AesGcmSivKey.Builder.class);
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
      } else if (!(obj instanceof AesGcmSivKey)) {
         return super.equals(obj);
      } else {
         AesGcmSivKey other = (AesGcmSivKey)obj;
         if (this.getVersion() != other.getVersion()) {
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
         hash = 37 * hash + 3;
         hash = 53 * hash + this.getKeyValue().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesGcmSivKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesGcmSivKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesGcmSivKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesGcmSivKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesGcmSivKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesGcmSivKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesGcmSivKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesGcmSivKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesGcmSivKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesGcmSivKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesGcmSivKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesGcmSivKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesGcmSivKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesGcmSivKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesGcmSivKey.Builder newBuilder(AesGcmSivKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesGcmSivKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesGcmSivKey.Builder() : new AesGcmSivKey.Builder().mergeFrom(this);
   }

   protected AesGcmSivKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesGcmSivKey.Builder(parent);
   }

   public static AesGcmSivKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesGcmSivKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesGcmSivKey> getParserForType() {
      return PARSER;
   }

   public AesGcmSivKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesGcmSivKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesGcmSivKey.Builder> implements AesGcmSivKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private ByteString keyValue_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesGcmSiv.internal_static_google_crypto_tink_AesGcmSivKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesGcmSiv.internal_static_google_crypto_tink_AesGcmSivKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesGcmSivKey.class, AesGcmSivKey.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public AesGcmSivKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.keyValue_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesGcmSiv.internal_static_google_crypto_tink_AesGcmSivKey_descriptor;
      }

      public AesGcmSivKey getDefaultInstanceForType() {
         return AesGcmSivKey.getDefaultInstance();
      }

      public AesGcmSivKey build() {
         AesGcmSivKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesGcmSivKey buildPartial() {
         AesGcmSivKey result = new AesGcmSivKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesGcmSivKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.keyValue_ = this.keyValue_;
         }
      }

      public AesGcmSivKey.Builder mergeFrom(Message other) {
         if (other instanceof AesGcmSivKey) {
            return this.mergeFrom((AesGcmSivKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesGcmSivKey.Builder mergeFrom(AesGcmSivKey other) {
         if (other == AesGcmSivKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
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

      public AesGcmSivKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 26:
                        this.keyValue_ = input.readBytes();
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
      public int getVersion() {
         return this.version_;
      }

      public AesGcmSivKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesGcmSivKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getKeyValue() {
         return this.keyValue_;
      }

      public AesGcmSivKey.Builder setKeyValue(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.keyValue_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }
      }

      public AesGcmSivKey.Builder clearKeyValue() {
         this.bitField0_ &= -3;
         this.keyValue_ = AesGcmSivKey.getDefaultInstance().getKeyValue();
         this.onChanged();
         return this;
      }
   }
}
