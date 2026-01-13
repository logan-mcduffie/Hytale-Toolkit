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

public final class AesCtrParams extends GeneratedMessage implements AesCtrParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int IV_SIZE_FIELD_NUMBER = 1;
   private int ivSize_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final AesCtrParams DEFAULT_INSTANCE = new AesCtrParams();
   private static final Parser<AesCtrParams> PARSER = new AbstractParser<AesCtrParams>() {
      public AesCtrParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesCtrParams.Builder builder = AesCtrParams.newBuilder();

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

   private AesCtrParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesCtrParams() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesCtr.internal_static_google_crypto_tink_AesCtrParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesCtr.internal_static_google_crypto_tink_AesCtrParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesCtrParams.class, AesCtrParams.Builder.class);
   }

   @Override
   public int getIvSize() {
      return this.ivSize_;
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
      if (this.ivSize_ != 0) {
         output.writeUInt32(1, this.ivSize_);
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
         if (this.ivSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.ivSize_);
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
      } else if (!(obj instanceof AesCtrParams)) {
         return super.equals(obj);
      } else {
         AesCtrParams other = (AesCtrParams)obj;
         return this.getIvSize() != other.getIvSize() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getIvSize();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesCtrParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCtrParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCtrParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesCtrParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCtrParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCtrParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesCtrParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesCtrParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesCtrParams.Builder newBuilder(AesCtrParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesCtrParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesCtrParams.Builder() : new AesCtrParams.Builder().mergeFrom(this);
   }

   protected AesCtrParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesCtrParams.Builder(parent);
   }

   public static AesCtrParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesCtrParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesCtrParams> getParserForType() {
      return PARSER;
   }

   public AesCtrParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesCtrParams.Builder> implements AesCtrParamsOrBuilder {
      private int bitField0_;
      private int ivSize_;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesCtr.internal_static_google_crypto_tink_AesCtrParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesCtr.internal_static_google_crypto_tink_AesCtrParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesCtrParams.class, AesCtrParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public AesCtrParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.ivSize_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesCtr.internal_static_google_crypto_tink_AesCtrParams_descriptor;
      }

      public AesCtrParams getDefaultInstanceForType() {
         return AesCtrParams.getDefaultInstance();
      }

      public AesCtrParams build() {
         AesCtrParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesCtrParams buildPartial() {
         AesCtrParams result = new AesCtrParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesCtrParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.ivSize_ = this.ivSize_;
         }
      }

      public AesCtrParams.Builder mergeFrom(Message other) {
         if (other instanceof AesCtrParams) {
            return this.mergeFrom((AesCtrParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesCtrParams.Builder mergeFrom(AesCtrParams other) {
         if (other == AesCtrParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.getIvSize() != 0) {
               this.setIvSize(other.getIvSize());
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

      public AesCtrParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.ivSize_ = input.readUInt32();
                        this.bitField0_ |= 1;
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
      public int getIvSize() {
         return this.ivSize_;
      }

      public AesCtrParams.Builder setIvSize(int value) {
         this.ivSize_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesCtrParams.Builder clearIvSize() {
         this.bitField0_ &= -2;
         this.ivSize_ = 0;
         this.onChanged();
         return this;
      }
   }
}
