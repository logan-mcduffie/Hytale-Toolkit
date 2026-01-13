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

public final class XAesGcmParams extends GeneratedMessage implements XAesGcmParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int SALT_SIZE_FIELD_NUMBER = 1;
   private int saltSize_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final XAesGcmParams DEFAULT_INSTANCE = new XAesGcmParams();
   private static final Parser<XAesGcmParams> PARSER = new AbstractParser<XAesGcmParams>() {
      public XAesGcmParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         XAesGcmParams.Builder builder = XAesGcmParams.newBuilder();

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

   private XAesGcmParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private XAesGcmParams() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return XAesGcm.internal_static_google_crypto_tink_XAesGcmParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return XAesGcm.internal_static_google_crypto_tink_XAesGcmParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(XAesGcmParams.class, XAesGcmParams.Builder.class);
   }

   @Override
   public int getSaltSize() {
      return this.saltSize_;
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
      if (this.saltSize_ != 0) {
         output.writeUInt32(1, this.saltSize_);
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
         if (this.saltSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.saltSize_);
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
      } else if (!(obj instanceof XAesGcmParams)) {
         return super.equals(obj);
      } else {
         XAesGcmParams other = (XAesGcmParams)obj;
         return this.getSaltSize() != other.getSaltSize() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getSaltSize();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static XAesGcmParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static XAesGcmParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static XAesGcmParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static XAesGcmParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static XAesGcmParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static XAesGcmParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static XAesGcmParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static XAesGcmParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static XAesGcmParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static XAesGcmParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static XAesGcmParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static XAesGcmParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public XAesGcmParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static XAesGcmParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static XAesGcmParams.Builder newBuilder(XAesGcmParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public XAesGcmParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new XAesGcmParams.Builder() : new XAesGcmParams.Builder().mergeFrom(this);
   }

   protected XAesGcmParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new XAesGcmParams.Builder(parent);
   }

   public static XAesGcmParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<XAesGcmParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<XAesGcmParams> getParserForType() {
      return PARSER;
   }

   public XAesGcmParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", XAesGcmParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<XAesGcmParams.Builder> implements XAesGcmParamsOrBuilder {
      private int bitField0_;
      private int saltSize_;

      public static final Descriptors.Descriptor getDescriptor() {
         return XAesGcm.internal_static_google_crypto_tink_XAesGcmParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return XAesGcm.internal_static_google_crypto_tink_XAesGcmParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(XAesGcmParams.class, XAesGcmParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public XAesGcmParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.saltSize_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return XAesGcm.internal_static_google_crypto_tink_XAesGcmParams_descriptor;
      }

      public XAesGcmParams getDefaultInstanceForType() {
         return XAesGcmParams.getDefaultInstance();
      }

      public XAesGcmParams build() {
         XAesGcmParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public XAesGcmParams buildPartial() {
         XAesGcmParams result = new XAesGcmParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(XAesGcmParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.saltSize_ = this.saltSize_;
         }
      }

      public XAesGcmParams.Builder mergeFrom(Message other) {
         if (other instanceof XAesGcmParams) {
            return this.mergeFrom((XAesGcmParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public XAesGcmParams.Builder mergeFrom(XAesGcmParams other) {
         if (other == XAesGcmParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.getSaltSize() != 0) {
               this.setSaltSize(other.getSaltSize());
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

      public XAesGcmParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.saltSize_ = input.readUInt32();
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
      public int getSaltSize() {
         return this.saltSize_;
      }

      public XAesGcmParams.Builder setSaltSize(int value) {
         this.saltSize_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public XAesGcmParams.Builder clearSaltSize() {
         this.bitField0_ &= -2;
         this.saltSize_ = 0;
         this.onChanged();
         return this;
      }
   }
}
