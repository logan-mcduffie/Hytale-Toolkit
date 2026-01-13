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

public final class AesCmacParams extends GeneratedMessage implements AesCmacParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int TAG_SIZE_FIELD_NUMBER = 1;
   private int tagSize_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final AesCmacParams DEFAULT_INSTANCE = new AesCmacParams();
   private static final Parser<AesCmacParams> PARSER = new AbstractParser<AesCmacParams>() {
      public AesCmacParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         AesCmacParams.Builder builder = AesCmacParams.newBuilder();

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

   private AesCmacParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private AesCmacParams() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return AesCmac.internal_static_google_crypto_tink_AesCmacParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return AesCmac.internal_static_google_crypto_tink_AesCmacParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(AesCmacParams.class, AesCmacParams.Builder.class);
   }

   @Override
   public int getTagSize() {
      return this.tagSize_;
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
      if (this.tagSize_ != 0) {
         output.writeUInt32(1, this.tagSize_);
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
         if (this.tagSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.tagSize_);
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
      } else if (!(obj instanceof AesCmacParams)) {
         return super.equals(obj);
      } else {
         AesCmacParams other = (AesCmacParams)obj;
         return this.getTagSize() != other.getTagSize() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getTagSize();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static AesCmacParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCmacParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCmacParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCmacParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCmacParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static AesCmacParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static AesCmacParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCmacParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCmacParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static AesCmacParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static AesCmacParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static AesCmacParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public AesCmacParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static AesCmacParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static AesCmacParams.Builder newBuilder(AesCmacParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public AesCmacParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new AesCmacParams.Builder() : new AesCmacParams.Builder().mergeFrom(this);
   }

   protected AesCmacParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new AesCmacParams.Builder(parent);
   }

   public static AesCmacParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<AesCmacParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<AesCmacParams> getParserForType() {
      return PARSER;
   }

   public AesCmacParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCmacParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<AesCmacParams.Builder> implements AesCmacParamsOrBuilder {
      private int bitField0_;
      private int tagSize_;

      public static final Descriptors.Descriptor getDescriptor() {
         return AesCmac.internal_static_google_crypto_tink_AesCmacParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return AesCmac.internal_static_google_crypto_tink_AesCmacParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(AesCmacParams.class, AesCmacParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public AesCmacParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.tagSize_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return AesCmac.internal_static_google_crypto_tink_AesCmacParams_descriptor;
      }

      public AesCmacParams getDefaultInstanceForType() {
         return AesCmacParams.getDefaultInstance();
      }

      public AesCmacParams build() {
         AesCmacParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public AesCmacParams buildPartial() {
         AesCmacParams result = new AesCmacParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(AesCmacParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.tagSize_ = this.tagSize_;
         }
      }

      public AesCmacParams.Builder mergeFrom(Message other) {
         if (other instanceof AesCmacParams) {
            return this.mergeFrom((AesCmacParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public AesCmacParams.Builder mergeFrom(AesCmacParams other) {
         if (other == AesCmacParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.getTagSize() != 0) {
               this.setTagSize(other.getTagSize());
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

      public AesCmacParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.tagSize_ = input.readUInt32();
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
      public int getTagSize() {
         return this.tagSize_;
      }

      public AesCmacParams.Builder setTagSize(int value) {
         this.tagSize_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public AesCmacParams.Builder clearTagSize() {
         this.bitField0_ &= -2;
         this.tagSize_ = 0;
         this.onChanged();
         return this;
      }
   }
}
