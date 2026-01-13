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

public final class RsaSsaPssPublicKey extends GeneratedMessage implements RsaSsaPssPublicKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int PARAMS_FIELD_NUMBER = 2;
   private RsaSsaPssParams params_;
   public static final int N_FIELD_NUMBER = 3;
   private ByteString n_ = ByteString.EMPTY;
   public static final int E_FIELD_NUMBER = 4;
   private ByteString e_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final RsaSsaPssPublicKey DEFAULT_INSTANCE = new RsaSsaPssPublicKey();
   private static final Parser<RsaSsaPssPublicKey> PARSER = new AbstractParser<RsaSsaPssPublicKey>() {
      public RsaSsaPssPublicKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         RsaSsaPssPublicKey.Builder builder = RsaSsaPssPublicKey.newBuilder();

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

   private RsaSsaPssPublicKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private RsaSsaPssPublicKey() {
      this.n_ = ByteString.EMPTY;
      this.e_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(RsaSsaPssPublicKey.class, RsaSsaPssPublicKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public boolean hasParams() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public RsaSsaPssParams getParams() {
      return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
   }

   @Override
   public RsaSsaPssParamsOrBuilder getParamsOrBuilder() {
      return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
   }

   @Override
   public ByteString getN() {
      return this.n_;
   }

   @Override
   public ByteString getE() {
      return this.e_;
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
         output.writeMessage(2, this.getParams());
      }

      if (!this.n_.isEmpty()) {
         output.writeBytes(3, this.n_);
      }

      if (!this.e_.isEmpty()) {
         output.writeBytes(4, this.e_);
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
            size += CodedOutputStream.computeMessageSize(2, this.getParams());
         }

         if (!this.n_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.n_);
         }

         if (!this.e_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.e_);
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
      } else if (!(obj instanceof RsaSsaPssPublicKey)) {
         return super.equals(obj);
      } else {
         RsaSsaPssPublicKey other = (RsaSsaPssPublicKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.hasParams() != other.hasParams()) {
            return false;
         } else if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
         } else if (!this.getN().equals(other.getN())) {
            return false;
         } else {
            return !this.getE().equals(other.getE()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasParams()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getParams().hashCode();
         }

         hash = 37 * hash + 3;
         hash = 53 * hash + this.getN().hashCode();
         hash = 37 * hash + 4;
         hash = 53 * hash + this.getE().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static RsaSsaPssPublicKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static RsaSsaPssPublicKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static RsaSsaPssPublicKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static RsaSsaPssPublicKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static RsaSsaPssPublicKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static RsaSsaPssPublicKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static RsaSsaPssPublicKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static RsaSsaPssPublicKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static RsaSsaPssPublicKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static RsaSsaPssPublicKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static RsaSsaPssPublicKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static RsaSsaPssPublicKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public RsaSsaPssPublicKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static RsaSsaPssPublicKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static RsaSsaPssPublicKey.Builder newBuilder(RsaSsaPssPublicKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public RsaSsaPssPublicKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new RsaSsaPssPublicKey.Builder() : new RsaSsaPssPublicKey.Builder().mergeFrom(this);
   }

   protected RsaSsaPssPublicKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new RsaSsaPssPublicKey.Builder(parent);
   }

   public static RsaSsaPssPublicKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<RsaSsaPssPublicKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<RsaSsaPssPublicKey> getParserForType() {
      return PARSER;
   }

   public RsaSsaPssPublicKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPssPublicKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<RsaSsaPssPublicKey.Builder> implements RsaSsaPssPublicKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private RsaSsaPssParams params_;
      private SingleFieldBuilder<RsaSsaPssParams, RsaSsaPssParams.Builder, RsaSsaPssParamsOrBuilder> paramsBuilder_;
      private ByteString n_ = ByteString.EMPTY;
      private ByteString e_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(RsaSsaPssPublicKey.class, RsaSsaPssPublicKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (RsaSsaPssPublicKey.alwaysUseFieldBuilders) {
            this.internalGetParamsFieldBuilder();
         }
      }

      public RsaSsaPssPublicKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.n_ = ByteString.EMPTY;
         this.e_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor;
      }

      public RsaSsaPssPublicKey getDefaultInstanceForType() {
         return RsaSsaPssPublicKey.getDefaultInstance();
      }

      public RsaSsaPssPublicKey build() {
         RsaSsaPssPublicKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public RsaSsaPssPublicKey buildPartial() {
         RsaSsaPssPublicKey result = new RsaSsaPssPublicKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(RsaSsaPssPublicKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 2) != 0) {
            result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.n_ = this.n_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.e_ = this.e_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public RsaSsaPssPublicKey.Builder mergeFrom(Message other) {
         if (other instanceof RsaSsaPssPublicKey) {
            return this.mergeFrom((RsaSsaPssPublicKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public RsaSsaPssPublicKey.Builder mergeFrom(RsaSsaPssPublicKey other) {
         if (other == RsaSsaPssPublicKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.hasParams()) {
               this.mergeParams(other.getParams());
            }

            if (!other.getN().isEmpty()) {
               this.setN(other.getN());
            }

            if (!other.getE().isEmpty()) {
               this.setE(other.getE());
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

      public RsaSsaPssPublicKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        this.n_ = input.readBytes();
                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        this.e_ = input.readBytes();
                        this.bitField0_ |= 8;
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

      public RsaSsaPssPublicKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public RsaSsaPssPublicKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasParams() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public RsaSsaPssParams getParams() {
         if (this.paramsBuilder_ == null) {
            return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
         } else {
            return this.paramsBuilder_.getMessage();
         }
      }

      public RsaSsaPssPublicKey.Builder setParams(RsaSsaPssParams value) {
         if (this.paramsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.params_ = value;
         } else {
            this.paramsBuilder_.setMessage(value);
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public RsaSsaPssPublicKey.Builder setParams(RsaSsaPssParams.Builder builderForValue) {
         if (this.paramsBuilder_ == null) {
            this.params_ = builderForValue.build();
         } else {
            this.paramsBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public RsaSsaPssPublicKey.Builder mergeParams(RsaSsaPssParams value) {
         if (this.paramsBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.params_ != null && this.params_ != RsaSsaPssParams.getDefaultInstance()) {
               this.getParamsBuilder().mergeFrom(value);
            } else {
               this.params_ = value;
            }
         } else {
            this.paramsBuilder_.mergeFrom(value);
         }

         if (this.params_ != null) {
            this.bitField0_ |= 2;
            this.onChanged();
         }

         return this;
      }

      public RsaSsaPssPublicKey.Builder clearParams() {
         this.bitField0_ &= -3;
         this.params_ = null;
         if (this.paramsBuilder_ != null) {
            this.paramsBuilder_.dispose();
            this.paramsBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public RsaSsaPssParams.Builder getParamsBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetParamsFieldBuilder().getBuilder();
      }

      @Override
      public RsaSsaPssParamsOrBuilder getParamsOrBuilder() {
         if (this.paramsBuilder_ != null) {
            return this.paramsBuilder_.getMessageOrBuilder();
         } else {
            return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
         }
      }

      private SingleFieldBuilder<RsaSsaPssParams, RsaSsaPssParams.Builder, RsaSsaPssParamsOrBuilder> internalGetParamsFieldBuilder() {
         if (this.paramsBuilder_ == null) {
            this.paramsBuilder_ = new SingleFieldBuilder<>(this.getParams(), this.getParentForChildren(), this.isClean());
            this.params_ = null;
         }

         return this.paramsBuilder_;
      }

      @Override
      public ByteString getN() {
         return this.n_;
      }

      public RsaSsaPssPublicKey.Builder setN(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.n_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public RsaSsaPssPublicKey.Builder clearN() {
         this.bitField0_ &= -5;
         this.n_ = RsaSsaPssPublicKey.getDefaultInstance().getN();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getE() {
         return this.e_;
      }

      public RsaSsaPssPublicKey.Builder setE(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.e_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public RsaSsaPssPublicKey.Builder clearE() {
         this.bitField0_ &= -9;
         this.e_ = RsaSsaPssPublicKey.getDefaultInstance().getE();
         this.onChanged();
         return this;
      }
   }
}
