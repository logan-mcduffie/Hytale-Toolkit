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

public final class PrfBasedDeriverKey extends GeneratedMessage implements PrfBasedDeriverKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int PRF_KEY_FIELD_NUMBER = 2;
   private KeyData prfKey_;
   public static final int PARAMS_FIELD_NUMBER = 3;
   private PrfBasedDeriverParams params_;
   private byte memoizedIsInitialized = -1;
   private static final PrfBasedDeriverKey DEFAULT_INSTANCE = new PrfBasedDeriverKey();
   private static final Parser<PrfBasedDeriverKey> PARSER = new AbstractParser<PrfBasedDeriverKey>() {
      public PrfBasedDeriverKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         PrfBasedDeriverKey.Builder builder = PrfBasedDeriverKey.newBuilder();

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

   private PrfBasedDeriverKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private PrfBasedDeriverKey() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(PrfBasedDeriverKey.class, PrfBasedDeriverKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public boolean hasPrfKey() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public KeyData getPrfKey() {
      return this.prfKey_ == null ? KeyData.getDefaultInstance() : this.prfKey_;
   }

   @Override
   public KeyDataOrBuilder getPrfKeyOrBuilder() {
      return this.prfKey_ == null ? KeyData.getDefaultInstance() : this.prfKey_;
   }

   @Override
   public boolean hasParams() {
      return (this.bitField0_ & 2) != 0;
   }

   @Override
   public PrfBasedDeriverParams getParams() {
      return this.params_ == null ? PrfBasedDeriverParams.getDefaultInstance() : this.params_;
   }

   @Override
   public PrfBasedDeriverParamsOrBuilder getParamsOrBuilder() {
      return this.params_ == null ? PrfBasedDeriverParams.getDefaultInstance() : this.params_;
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
         output.writeMessage(2, this.getPrfKey());
      }

      if ((this.bitField0_ & 2) != 0) {
         output.writeMessage(3, this.getParams());
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
            size += CodedOutputStream.computeMessageSize(2, this.getPrfKey());
         }

         if ((this.bitField0_ & 2) != 0) {
            size += CodedOutputStream.computeMessageSize(3, this.getParams());
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
      } else if (!(obj instanceof PrfBasedDeriverKey)) {
         return super.equals(obj);
      } else {
         PrfBasedDeriverKey other = (PrfBasedDeriverKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.hasPrfKey() != other.hasPrfKey()) {
            return false;
         } else if (this.hasPrfKey() && !this.getPrfKey().equals(other.getPrfKey())) {
            return false;
         } else if (this.hasParams() != other.hasParams()) {
            return false;
         } else {
            return this.hasParams() && !this.getParams().equals(other.getParams()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasPrfKey()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getPrfKey().hashCode();
         }

         if (this.hasParams()) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getParams().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static PrfBasedDeriverKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static PrfBasedDeriverKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static PrfBasedDeriverKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static PrfBasedDeriverKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static PrfBasedDeriverKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static PrfBasedDeriverKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static PrfBasedDeriverKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static PrfBasedDeriverKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static PrfBasedDeriverKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static PrfBasedDeriverKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static PrfBasedDeriverKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static PrfBasedDeriverKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public PrfBasedDeriverKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static PrfBasedDeriverKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static PrfBasedDeriverKey.Builder newBuilder(PrfBasedDeriverKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public PrfBasedDeriverKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new PrfBasedDeriverKey.Builder() : new PrfBasedDeriverKey.Builder().mergeFrom(this);
   }

   protected PrfBasedDeriverKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new PrfBasedDeriverKey.Builder(parent);
   }

   public static PrfBasedDeriverKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<PrfBasedDeriverKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<PrfBasedDeriverKey> getParserForType() {
      return PARSER;
   }

   public PrfBasedDeriverKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", PrfBasedDeriverKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<PrfBasedDeriverKey.Builder> implements PrfBasedDeriverKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private KeyData prfKey_;
      private SingleFieldBuilder<KeyData, KeyData.Builder, KeyDataOrBuilder> prfKeyBuilder_;
      private PrfBasedDeriverParams params_;
      private SingleFieldBuilder<PrfBasedDeriverParams, PrfBasedDeriverParams.Builder, PrfBasedDeriverParamsOrBuilder> paramsBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PrfBasedDeriverKey.class, PrfBasedDeriverKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (PrfBasedDeriverKey.alwaysUseFieldBuilders) {
            this.internalGetPrfKeyFieldBuilder();
            this.internalGetParamsFieldBuilder();
         }
      }

      public PrfBasedDeriverKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.prfKey_ = null;
         if (this.prfKeyBuilder_ != null) {
            this.prfKeyBuilder_.dispose();
            this.prfKeyBuilder_ = null;
         }

         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverKey_descriptor;
      }

      public PrfBasedDeriverKey getDefaultInstanceForType() {
         return PrfBasedDeriverKey.getDefaultInstance();
      }

      public PrfBasedDeriverKey build() {
         PrfBasedDeriverKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public PrfBasedDeriverKey buildPartial() {
         PrfBasedDeriverKey result = new PrfBasedDeriverKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(PrfBasedDeriverKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 2) != 0) {
            result.prfKey_ = this.prfKeyBuilder_ == null ? this.prfKey_ : this.prfKeyBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
            to_bitField0_ |= 2;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public PrfBasedDeriverKey.Builder mergeFrom(Message other) {
         if (other instanceof PrfBasedDeriverKey) {
            return this.mergeFrom((PrfBasedDeriverKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public PrfBasedDeriverKey.Builder mergeFrom(PrfBasedDeriverKey other) {
         if (other == PrfBasedDeriverKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.hasPrfKey()) {
               this.mergePrfKey(other.getPrfKey());
            }

            if (other.hasParams()) {
               this.mergeParams(other.getParams());
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

      public PrfBasedDeriverKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        input.readMessage(this.internalGetPrfKeyFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
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

      public PrfBasedDeriverKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasPrfKey() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public KeyData getPrfKey() {
         if (this.prfKeyBuilder_ == null) {
            return this.prfKey_ == null ? KeyData.getDefaultInstance() : this.prfKey_;
         } else {
            return this.prfKeyBuilder_.getMessage();
         }
      }

      public PrfBasedDeriverKey.Builder setPrfKey(KeyData value) {
         if (this.prfKeyBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.prfKey_ = value;
         } else {
            this.prfKeyBuilder_.setMessage(value);
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverKey.Builder setPrfKey(KeyData.Builder builderForValue) {
         if (this.prfKeyBuilder_ == null) {
            this.prfKey_ = builderForValue.build();
         } else {
            this.prfKeyBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverKey.Builder mergePrfKey(KeyData value) {
         if (this.prfKeyBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.prfKey_ != null && this.prfKey_ != KeyData.getDefaultInstance()) {
               this.getPrfKeyBuilder().mergeFrom(value);
            } else {
               this.prfKey_ = value;
            }
         } else {
            this.prfKeyBuilder_.mergeFrom(value);
         }

         if (this.prfKey_ != null) {
            this.bitField0_ |= 2;
            this.onChanged();
         }

         return this;
      }

      public PrfBasedDeriverKey.Builder clearPrfKey() {
         this.bitField0_ &= -3;
         this.prfKey_ = null;
         if (this.prfKeyBuilder_ != null) {
            this.prfKeyBuilder_.dispose();
            this.prfKeyBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public KeyData.Builder getPrfKeyBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetPrfKeyFieldBuilder().getBuilder();
      }

      @Override
      public KeyDataOrBuilder getPrfKeyOrBuilder() {
         if (this.prfKeyBuilder_ != null) {
            return this.prfKeyBuilder_.getMessageOrBuilder();
         } else {
            return this.prfKey_ == null ? KeyData.getDefaultInstance() : this.prfKey_;
         }
      }

      private SingleFieldBuilder<KeyData, KeyData.Builder, KeyDataOrBuilder> internalGetPrfKeyFieldBuilder() {
         if (this.prfKeyBuilder_ == null) {
            this.prfKeyBuilder_ = new SingleFieldBuilder<>(this.getPrfKey(), this.getParentForChildren(), this.isClean());
            this.prfKey_ = null;
         }

         return this.prfKeyBuilder_;
      }

      @Override
      public boolean hasParams() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public PrfBasedDeriverParams getParams() {
         if (this.paramsBuilder_ == null) {
            return this.params_ == null ? PrfBasedDeriverParams.getDefaultInstance() : this.params_;
         } else {
            return this.paramsBuilder_.getMessage();
         }
      }

      public PrfBasedDeriverKey.Builder setParams(PrfBasedDeriverParams value) {
         if (this.paramsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.params_ = value;
         } else {
            this.paramsBuilder_.setMessage(value);
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverKey.Builder setParams(PrfBasedDeriverParams.Builder builderForValue) {
         if (this.paramsBuilder_ == null) {
            this.params_ = builderForValue.build();
         } else {
            this.paramsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverKey.Builder mergeParams(PrfBasedDeriverParams value) {
         if (this.paramsBuilder_ == null) {
            if ((this.bitField0_ & 4) != 0 && this.params_ != null && this.params_ != PrfBasedDeriverParams.getDefaultInstance()) {
               this.getParamsBuilder().mergeFrom(value);
            } else {
               this.params_ = value;
            }
         } else {
            this.paramsBuilder_.mergeFrom(value);
         }

         if (this.params_ != null) {
            this.bitField0_ |= 4;
            this.onChanged();
         }

         return this;
      }

      public PrfBasedDeriverKey.Builder clearParams() {
         this.bitField0_ &= -5;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public PrfBasedDeriverParams.Builder getParamsBuilder() {
         this.bitField0_ |= 4;
         this.onChanged();
         return this.internalGetParamsFieldBuilder().getBuilder();
      }

      @Override
      public PrfBasedDeriverParamsOrBuilder getParamsOrBuilder() {
         if (this.paramsBuilder_ != null) {
            return this.paramsBuilder_.getMessageOrBuilder();
         } else {
            return this.params_ == null ? PrfBasedDeriverParams.getDefaultInstance() : this.params_;
         }
      }

      private SingleFieldBuilder<PrfBasedDeriverParams, PrfBasedDeriverParams.Builder, PrfBasedDeriverParamsOrBuilder> internalGetParamsFieldBuilder() {
         if (this.paramsBuilder_ == null) {
            this.paramsBuilder_ = new SingleFieldBuilder<>(this.getParams(), this.getParentForChildren(), this.isClean());
            this.params_ = null;
         }

         return this.paramsBuilder_;
      }
   }
}
