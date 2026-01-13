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

public final class EciesAeadDemParams extends GeneratedMessage implements EciesAeadDemParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int AEAD_DEM_FIELD_NUMBER = 2;
   private KeyTemplate aeadDem_;
   private byte memoizedIsInitialized = -1;
   private static final EciesAeadDemParams DEFAULT_INSTANCE = new EciesAeadDemParams();
   private static final Parser<EciesAeadDemParams> PARSER = new AbstractParser<EciesAeadDemParams>() {
      public EciesAeadDemParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         EciesAeadDemParams.Builder builder = EciesAeadDemParams.newBuilder();

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

   private EciesAeadDemParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private EciesAeadDemParams() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadDemParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadDemParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(EciesAeadDemParams.class, EciesAeadDemParams.Builder.class);
   }

   @Override
   public boolean hasAeadDem() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public KeyTemplate getAeadDem() {
      return this.aeadDem_ == null ? KeyTemplate.getDefaultInstance() : this.aeadDem_;
   }

   @Override
   public KeyTemplateOrBuilder getAeadDemOrBuilder() {
      return this.aeadDem_ == null ? KeyTemplate.getDefaultInstance() : this.aeadDem_;
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
      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(2, this.getAeadDem());
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
         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getAeadDem());
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
      } else if (!(obj instanceof EciesAeadDemParams)) {
         return super.equals(obj);
      } else {
         EciesAeadDemParams other = (EciesAeadDemParams)obj;
         if (this.hasAeadDem() != other.hasAeadDem()) {
            return false;
         } else {
            return this.hasAeadDem() && !this.getAeadDem().equals(other.getAeadDem()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasAeadDem()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getAeadDem().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static EciesAeadDemParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EciesAeadDemParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EciesAeadDemParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EciesAeadDemParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EciesAeadDemParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static EciesAeadDemParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static EciesAeadDemParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static EciesAeadDemParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static EciesAeadDemParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static EciesAeadDemParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static EciesAeadDemParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static EciesAeadDemParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public EciesAeadDemParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static EciesAeadDemParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static EciesAeadDemParams.Builder newBuilder(EciesAeadDemParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public EciesAeadDemParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new EciesAeadDemParams.Builder() : new EciesAeadDemParams.Builder().mergeFrom(this);
   }

   protected EciesAeadDemParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new EciesAeadDemParams.Builder(parent);
   }

   public static EciesAeadDemParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<EciesAeadDemParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<EciesAeadDemParams> getParserForType() {
      return PARSER;
   }

   public EciesAeadDemParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EciesAeadDemParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<EciesAeadDemParams.Builder> implements EciesAeadDemParamsOrBuilder {
      private int bitField0_;
      private KeyTemplate aeadDem_;
      private SingleFieldBuilder<KeyTemplate, KeyTemplate.Builder, KeyTemplateOrBuilder> aeadDemBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadDemParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadDemParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(EciesAeadDemParams.class, EciesAeadDemParams.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (EciesAeadDemParams.alwaysUseFieldBuilders) {
            this.internalGetAeadDemFieldBuilder();
         }
      }

      public EciesAeadDemParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.aeadDem_ = null;
         if (this.aeadDemBuilder_ != null) {
            this.aeadDemBuilder_.dispose();
            this.aeadDemBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadDemParams_descriptor;
      }

      public EciesAeadDemParams getDefaultInstanceForType() {
         return EciesAeadDemParams.getDefaultInstance();
      }

      public EciesAeadDemParams build() {
         EciesAeadDemParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public EciesAeadDemParams buildPartial() {
         EciesAeadDemParams result = new EciesAeadDemParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(EciesAeadDemParams result) {
         int from_bitField0_ = this.bitField0_;
         int to_bitField0_ = 0;
         if ((from_bitField0_ & 1) != 0) {
            result.aeadDem_ = this.aeadDemBuilder_ == null ? this.aeadDem_ : this.aeadDemBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public EciesAeadDemParams.Builder mergeFrom(Message other) {
         if (other instanceof EciesAeadDemParams) {
            return this.mergeFrom((EciesAeadDemParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public EciesAeadDemParams.Builder mergeFrom(EciesAeadDemParams other) {
         if (other == EciesAeadDemParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.hasAeadDem()) {
               this.mergeAeadDem(other.getAeadDem());
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

      public EciesAeadDemParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 18:
                        input.readMessage(this.internalGetAeadDemFieldBuilder().getBuilder(), extensionRegistry);
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
      public boolean hasAeadDem() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public KeyTemplate getAeadDem() {
         if (this.aeadDemBuilder_ == null) {
            return this.aeadDem_ == null ? KeyTemplate.getDefaultInstance() : this.aeadDem_;
         } else {
            return this.aeadDemBuilder_.getMessage();
         }
      }

      public EciesAeadDemParams.Builder setAeadDem(KeyTemplate value) {
         if (this.aeadDemBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.aeadDem_ = value;
         } else {
            this.aeadDemBuilder_.setMessage(value);
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public EciesAeadDemParams.Builder setAeadDem(KeyTemplate.Builder builderForValue) {
         if (this.aeadDemBuilder_ == null) {
            this.aeadDem_ = builderForValue.build();
         } else {
            this.aeadDemBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public EciesAeadDemParams.Builder mergeAeadDem(KeyTemplate value) {
         if (this.aeadDemBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0 && this.aeadDem_ != null && this.aeadDem_ != KeyTemplate.getDefaultInstance()) {
               this.getAeadDemBuilder().mergeFrom(value);
            } else {
               this.aeadDem_ = value;
            }
         } else {
            this.aeadDemBuilder_.mergeFrom(value);
         }

         if (this.aeadDem_ != null) {
            this.bitField0_ |= 1;
            this.onChanged();
         }

         return this;
      }

      public EciesAeadDemParams.Builder clearAeadDem() {
         this.bitField0_ &= -2;
         this.aeadDem_ = null;
         if (this.aeadDemBuilder_ != null) {
            this.aeadDemBuilder_.dispose();
            this.aeadDemBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public KeyTemplate.Builder getAeadDemBuilder() {
         this.bitField0_ |= 1;
         this.onChanged();
         return this.internalGetAeadDemFieldBuilder().getBuilder();
      }

      @Override
      public KeyTemplateOrBuilder getAeadDemOrBuilder() {
         if (this.aeadDemBuilder_ != null) {
            return this.aeadDemBuilder_.getMessageOrBuilder();
         } else {
            return this.aeadDem_ == null ? KeyTemplate.getDefaultInstance() : this.aeadDem_;
         }
      }

      private SingleFieldBuilder<KeyTemplate, KeyTemplate.Builder, KeyTemplateOrBuilder> internalGetAeadDemFieldBuilder() {
         if (this.aeadDemBuilder_ == null) {
            this.aeadDemBuilder_ = new SingleFieldBuilder<>(this.getAeadDem(), this.getParentForChildren(), this.isClean());
            this.aeadDem_ = null;
         }

         return this.aeadDemBuilder_;
      }
   }
}
