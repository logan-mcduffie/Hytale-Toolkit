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

public final class HmacParams extends GeneratedMessage implements HmacParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int HASH_FIELD_NUMBER = 1;
   private int hash_ = 0;
   public static final int TAG_SIZE_FIELD_NUMBER = 2;
   private int tagSize_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final HmacParams DEFAULT_INSTANCE = new HmacParams();
   private static final Parser<HmacParams> PARSER = new AbstractParser<HmacParams>() {
      public HmacParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         HmacParams.Builder builder = HmacParams.newBuilder();

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

   private HmacParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private HmacParams() {
      this.hash_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Hmac.internal_static_google_crypto_tink_HmacParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Hmac.internal_static_google_crypto_tink_HmacParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HmacParams.class, HmacParams.Builder.class);
   }

   @Override
   public int getHashValue() {
      return this.hash_;
   }

   @Override
   public HashType getHash() {
      HashType result = HashType.forNumber(this.hash_);
      return result == null ? HashType.UNRECOGNIZED : result;
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
      if (this.hash_ != HashType.UNKNOWN_HASH.getNumber()) {
         output.writeEnum(1, this.hash_);
      }

      if (this.tagSize_ != 0) {
         output.writeUInt32(2, this.tagSize_);
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
         if (this.hash_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.hash_);
         }

         if (this.tagSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.tagSize_);
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
      } else if (!(obj instanceof HmacParams)) {
         return super.equals(obj);
      } else {
         HmacParams other = (HmacParams)obj;
         if (this.hash_ != other.hash_) {
            return false;
         } else {
            return this.getTagSize() != other.getTagSize() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.hash_;
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getTagSize();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static HmacParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static HmacParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static HmacParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static HmacParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static HmacParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static HmacParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static HmacParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static HmacParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static HmacParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static HmacParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static HmacParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static HmacParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public HmacParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static HmacParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static HmacParams.Builder newBuilder(HmacParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public HmacParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new HmacParams.Builder() : new HmacParams.Builder().mergeFrom(this);
   }

   protected HmacParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new HmacParams.Builder(parent);
   }

   public static HmacParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<HmacParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<HmacParams> getParserForType() {
      return PARSER;
   }

   public HmacParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HmacParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<HmacParams.Builder> implements HmacParamsOrBuilder {
      private int bitField0_;
      private int hash_ = 0;
      private int tagSize_;

      public static final Descriptors.Descriptor getDescriptor() {
         return Hmac.internal_static_google_crypto_tink_HmacParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Hmac.internal_static_google_crypto_tink_HmacParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(HmacParams.class, HmacParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public HmacParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.hash_ = 0;
         this.tagSize_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Hmac.internal_static_google_crypto_tink_HmacParams_descriptor;
      }

      public HmacParams getDefaultInstanceForType() {
         return HmacParams.getDefaultInstance();
      }

      public HmacParams build() {
         HmacParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public HmacParams buildPartial() {
         HmacParams result = new HmacParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(HmacParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.hash_ = this.hash_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.tagSize_ = this.tagSize_;
         }
      }

      public HmacParams.Builder mergeFrom(Message other) {
         if (other instanceof HmacParams) {
            return this.mergeFrom((HmacParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public HmacParams.Builder mergeFrom(HmacParams other) {
         if (other == HmacParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.hash_ != 0) {
               this.setHashValue(other.getHashValue());
            }

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

      public HmacParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.hash_ = input.readEnum();
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.tagSize_ = input.readUInt32();
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
      public int getHashValue() {
         return this.hash_;
      }

      public HmacParams.Builder setHashValue(int value) {
         this.hash_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      @Override
      public HashType getHash() {
         HashType result = HashType.forNumber(this.hash_);
         return result == null ? HashType.UNRECOGNIZED : result;
      }

      public HmacParams.Builder setHash(HashType value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 1;
            this.hash_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public HmacParams.Builder clearHash() {
         this.bitField0_ &= -2;
         this.hash_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getTagSize() {
         return this.tagSize_;
      }

      public HmacParams.Builder setTagSize(int value) {
         this.tagSize_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public HmacParams.Builder clearTagSize() {
         this.bitField0_ &= -3;
         this.tagSize_ = 0;
         this.onChanged();
         return this;
      }
   }
}
