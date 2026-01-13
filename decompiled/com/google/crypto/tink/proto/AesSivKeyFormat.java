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

public final class AesSivKeyFormat extends GeneratedMessage implements AesSivKeyFormatOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int KEY_SIZE_FIELD_NUMBER = 1;
   private int keySize_ = 0;
   public static final int VERSION_FIELD_NUMBER = 2;
   private int version_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final AesSivKeyFormat DEFAULT_INSTANCE = new AesSivKeyFormat();
   private static final Parser<AesSivKeyFormat> PARSER = new AbstractParser<AesSivKeyFormat>() {
      public AesSivKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesSivKeyFormat.Builder builder = AesSivKeyFormat.newBuilder();

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

   private AesSivKeyFormat(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesSivKeyFormat() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesSiv.internal_static_google_crypto_tink_AesSivKeyFormat_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesSiv.internal_static_google_crypto_tink_AesSivKeyFormat_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesSivKeyFormat.class, AesSivKeyFormat.Builder.class);
   }

   @Override
   public int getKeySize() {
      return this.keySize_;
   }

   @Override
   public int getVersion() {
      return this.version_;
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
      if (this.keySize_ != 0) {
         output.writeUInt32(1, this.keySize_);
      }

      if (this.version_ != 0) {
         output.writeUInt32(2, this.version_);
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
         if (this.keySize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.keySize_);
         }

         if (this.version_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.version_);
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
      } else if (!(obj instanceof AesSivKeyFormat)) {
         return super.equals(obj);
      } else {
         AesSivKeyFormat other = (AesSivKeyFormat)obj;
         if (this.getKeySize() != other.getKeySize()) {
            return false;
         } else {
            return this.getVersion() != other.getVersion() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getKeySize();
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getVersion();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesSivKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesSivKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesSivKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesSivKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesSivKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesSivKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesSivKeyFormat parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesSivKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesSivKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesSivKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesSivKeyFormat parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesSivKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesSivKeyFormat.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesSivKeyFormat.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesSivKeyFormat.Builder newBuilder(AesSivKeyFormat prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesSivKeyFormat.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesSivKeyFormat.Builder() : new AesSivKeyFormat.Builder().mergeFrom(this);
   }

   protected AesSivKeyFormat.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesSivKeyFormat.Builder(parent);
   }

   public static AesSivKeyFormat getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesSivKeyFormat> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesSivKeyFormat> getParserForType() {
      return PARSER;
   }

   public AesSivKeyFormat getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesSivKeyFormat.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesSivKeyFormat.Builder> implements AesSivKeyFormatOrBuilder {
      private int bitField0_;
      private int keySize_;
      private int version_;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesSiv.internal_static_google_crypto_tink_AesSivKeyFormat_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesSiv.internal_static_google_crypto_tink_AesSivKeyFormat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesSivKeyFormat.class, AesSivKeyFormat.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public AesSivKeyFormat.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.keySize_ = 0;
         this.version_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesSiv.internal_static_google_crypto_tink_AesSivKeyFormat_descriptor;
      }

      public AesSivKeyFormat getDefaultInstanceForType() {
         return AesSivKeyFormat.getDefaultInstance();
      }

      public AesSivKeyFormat build() {
         AesSivKeyFormat result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesSivKeyFormat buildPartial() {
         AesSivKeyFormat result = new AesSivKeyFormat(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesSivKeyFormat result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.keySize_ = this.keySize_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.version_ = this.version_;
         }
      }

      public AesSivKeyFormat.Builder mergeFrom(Message other) {
         if (other instanceof AesSivKeyFormat) {
            return this.mergeFrom((AesSivKeyFormat)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesSivKeyFormat.Builder mergeFrom(AesSivKeyFormat other) {
         if (other == AesSivKeyFormat.getDefaultInstance()) {
            return this;
         } else {
            if (other.getKeySize() != 0) {
               this.setKeySize(other.getKeySize());
            }

            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
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

      public AesSivKeyFormat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.keySize_ = input.readUInt32();
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.version_ = input.readUInt32();
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
      public int getKeySize() {
         return this.keySize_;
      }

      public AesSivKeyFormat.Builder setKeySize(int value) {
         this.keySize_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesSivKeyFormat.Builder clearKeySize() {
         this.bitField0_ &= -2;
         this.keySize_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getVersion() {
         return this.version_;
      }

      public AesSivKeyFormat.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public AesSivKeyFormat.Builder clearVersion() {
         this.bitField0_ &= -3;
         this.version_ = 0;
         this.onChanged();
         return this;
      }
   }
}
