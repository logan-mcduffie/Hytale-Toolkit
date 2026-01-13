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

public final class PrfBasedDeriverParams extends GeneratedMessage implements PrfBasedDeriverParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int DERIVED_KEY_TEMPLATE_FIELD_NUMBER = 1;
   private KeyTemplate derivedKeyTemplate_;
   private byte memoizedIsInitialized = -1;
   private static final PrfBasedDeriverParams DEFAULT_INSTANCE = new PrfBasedDeriverParams();
   private static final Parser<PrfBasedDeriverParams> PARSER = new AbstractParser<PrfBasedDeriverParams>() {
      public PrfBasedDeriverParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         PrfBasedDeriverParams.Builder builder = PrfBasedDeriverParams.newBuilder();

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

   private PrfBasedDeriverParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private PrfBasedDeriverParams() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(PrfBasedDeriverParams.class, PrfBasedDeriverParams.Builder.class);
   }

   @Override
   public boolean hasDerivedKeyTemplate() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public KeyTemplate getDerivedKeyTemplate() {
      return this.derivedKeyTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.derivedKeyTemplate_;
   }

   @Override
   public KeyTemplateOrBuilder getDerivedKeyTemplateOrBuilder() {
      return this.derivedKeyTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.derivedKeyTemplate_;
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
         output.writeMessage(1, this.getDerivedKeyTemplate());
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
            size += CodedOutputStream.computeMessageSize(1, this.getDerivedKeyTemplate());
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
      } else if (!(obj instanceof PrfBasedDeriverParams)) {
         return super.equals(obj);
      } else {
         PrfBasedDeriverParams other = (PrfBasedDeriverParams)obj;
         if (this.hasDerivedKeyTemplate() != other.hasDerivedKeyTemplate()) {
            return false;
         } else {
            return this.hasDerivedKeyTemplate() && !this.getDerivedKeyTemplate().equals(other.getDerivedKeyTemplate())
               ? false
               : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasDerivedKeyTemplate()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getDerivedKeyTemplate().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static PrfBasedDeriverParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static PrfBasedDeriverParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static PrfBasedDeriverParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static PrfBasedDeriverParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static PrfBasedDeriverParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static PrfBasedDeriverParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static PrfBasedDeriverParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static PrfBasedDeriverParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static PrfBasedDeriverParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static PrfBasedDeriverParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static PrfBasedDeriverParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static PrfBasedDeriverParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public PrfBasedDeriverParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static PrfBasedDeriverParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static PrfBasedDeriverParams.Builder newBuilder(PrfBasedDeriverParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public PrfBasedDeriverParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new PrfBasedDeriverParams.Builder() : new PrfBasedDeriverParams.Builder().mergeFrom(this);
   }

   protected PrfBasedDeriverParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new PrfBasedDeriverParams.Builder(parent);
   }

   public static PrfBasedDeriverParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<PrfBasedDeriverParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<PrfBasedDeriverParams> getParserForType() {
      return PARSER;
   }

   public PrfBasedDeriverParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", PrfBasedDeriverParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<PrfBasedDeriverParams.Builder> implements PrfBasedDeriverParamsOrBuilder {
      private int bitField0_;
      private KeyTemplate derivedKeyTemplate_;
      private SingleFieldBuilder<KeyTemplate, KeyTemplate.Builder, KeyTemplateOrBuilder> derivedKeyTemplateBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PrfBasedDeriverParams.class, PrfBasedDeriverParams.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (PrfBasedDeriverParams.alwaysUseFieldBuilders) {
            this.internalGetDerivedKeyTemplateFieldBuilder();
         }
      }

      public PrfBasedDeriverParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.derivedKeyTemplate_ = null;
         if (this.derivedKeyTemplateBuilder_ != null) {
            this.derivedKeyTemplateBuilder_.dispose();
            this.derivedKeyTemplateBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return PrfBasedDeriver.internal_static_google_crypto_tink_PrfBasedDeriverParams_descriptor;
      }

      public PrfBasedDeriverParams getDefaultInstanceForType() {
         return PrfBasedDeriverParams.getDefaultInstance();
      }

      public PrfBasedDeriverParams build() {
         PrfBasedDeriverParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public PrfBasedDeriverParams buildPartial() {
         PrfBasedDeriverParams result = new PrfBasedDeriverParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(PrfBasedDeriverParams result) {
         int from_bitField0_ = this.bitField0_;
         int to_bitField0_ = 0;
         if ((from_bitField0_ & 1) != 0) {
            result.derivedKeyTemplate_ = this.derivedKeyTemplateBuilder_ == null ? this.derivedKeyTemplate_ : this.derivedKeyTemplateBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public PrfBasedDeriverParams.Builder mergeFrom(Message other) {
         if (other instanceof PrfBasedDeriverParams) {
            return this.mergeFrom((PrfBasedDeriverParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public PrfBasedDeriverParams.Builder mergeFrom(PrfBasedDeriverParams other) {
         if (other == PrfBasedDeriverParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.hasDerivedKeyTemplate()) {
               this.mergeDerivedKeyTemplate(other.getDerivedKeyTemplate());
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

      public PrfBasedDeriverParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 10:
                        input.readMessage(this.internalGetDerivedKeyTemplateFieldBuilder().getBuilder(), extensionRegistry);
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
      public boolean hasDerivedKeyTemplate() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public KeyTemplate getDerivedKeyTemplate() {
         if (this.derivedKeyTemplateBuilder_ == null) {
            return this.derivedKeyTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.derivedKeyTemplate_;
         } else {
            return this.derivedKeyTemplateBuilder_.getMessage();
         }
      }

      public PrfBasedDeriverParams.Builder setDerivedKeyTemplate(KeyTemplate value) {
         if (this.derivedKeyTemplateBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.derivedKeyTemplate_ = value;
         } else {
            this.derivedKeyTemplateBuilder_.setMessage(value);
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverParams.Builder setDerivedKeyTemplate(KeyTemplate.Builder builderForValue) {
         if (this.derivedKeyTemplateBuilder_ == null) {
            this.derivedKeyTemplate_ = builderForValue.build();
         } else {
            this.derivedKeyTemplateBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public PrfBasedDeriverParams.Builder mergeDerivedKeyTemplate(KeyTemplate value) {
         if (this.derivedKeyTemplateBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0 && this.derivedKeyTemplate_ != null && this.derivedKeyTemplate_ != KeyTemplate.getDefaultInstance()) {
               this.getDerivedKeyTemplateBuilder().mergeFrom(value);
            } else {
               this.derivedKeyTemplate_ = value;
            }
         } else {
            this.derivedKeyTemplateBuilder_.mergeFrom(value);
         }

         if (this.derivedKeyTemplate_ != null) {
            this.bitField0_ |= 1;
            this.onChanged();
         }

         return this;
      }

      public PrfBasedDeriverParams.Builder clearDerivedKeyTemplate() {
         this.bitField0_ &= -2;
         this.derivedKeyTemplate_ = null;
         if (this.derivedKeyTemplateBuilder_ != null) {
            this.derivedKeyTemplateBuilder_.dispose();
            this.derivedKeyTemplateBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public KeyTemplate.Builder getDerivedKeyTemplateBuilder() {
         this.bitField0_ |= 1;
         this.onChanged();
         return this.internalGetDerivedKeyTemplateFieldBuilder().getBuilder();
      }

      @Override
      public KeyTemplateOrBuilder getDerivedKeyTemplateOrBuilder() {
         if (this.derivedKeyTemplateBuilder_ != null) {
            return this.derivedKeyTemplateBuilder_.getMessageOrBuilder();
         } else {
            return this.derivedKeyTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.derivedKeyTemplate_;
         }
      }

      private SingleFieldBuilder<KeyTemplate, KeyTemplate.Builder, KeyTemplateOrBuilder> internalGetDerivedKeyTemplateFieldBuilder() {
         if (this.derivedKeyTemplateBuilder_ == null) {
            this.derivedKeyTemplateBuilder_ = new SingleFieldBuilder<>(this.getDerivedKeyTemplate(), this.getParentForChildren(), this.isClean());
            this.derivedKeyTemplate_ = null;
         }

         return this.derivedKeyTemplateBuilder_;
      }
   }
}
