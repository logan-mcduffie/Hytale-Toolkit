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

public final class HpkeParams extends GeneratedMessage implements HpkeParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int KEM_FIELD_NUMBER = 1;
   private int kem_ = 0;
   public static final int KDF_FIELD_NUMBER = 2;
   private int kdf_ = 0;
   public static final int AEAD_FIELD_NUMBER = 3;
   private int aead_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final HpkeParams DEFAULT_INSTANCE = new HpkeParams();
   private static final Parser<HpkeParams> PARSER = new AbstractParser<HpkeParams>() {
      public HpkeParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         HpkeParams.Builder builder = HpkeParams.newBuilder();

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

   private HpkeParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private HpkeParams() {
      this.kem_ = 0;
      this.kdf_ = 0;
      this.aead_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Hpke.internal_static_google_crypto_tink_HpkeParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Hpke.internal_static_google_crypto_tink_HpkeParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HpkeParams.class, HpkeParams.Builder.class);
   }

   @Override
   public int getKemValue() {
      return this.kem_;
   }

   @Override
   public HpkeKem getKem() {
      HpkeKem result = HpkeKem.forNumber(this.kem_);
      return result == null ? HpkeKem.UNRECOGNIZED : result;
   }

   @Override
   public int getKdfValue() {
      return this.kdf_;
   }

   @Override
   public HpkeKdf getKdf() {
      HpkeKdf result = HpkeKdf.forNumber(this.kdf_);
      return result == null ? HpkeKdf.UNRECOGNIZED : result;
   }

   @Override
   public int getAeadValue() {
      return this.aead_;
   }

   @Override
   public HpkeAead getAead() {
      HpkeAead result = HpkeAead.forNumber(this.aead_);
      return result == null ? HpkeAead.UNRECOGNIZED : result;
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
      if (this.kem_ != HpkeKem.KEM_UNKNOWN.getNumber()) {
         output.writeEnum(1, this.kem_);
      }

      if (this.kdf_ != HpkeKdf.KDF_UNKNOWN.getNumber()) {
         output.writeEnum(2, this.kdf_);
      }

      if (this.aead_ != HpkeAead.AEAD_UNKNOWN.getNumber()) {
         output.writeEnum(3, this.aead_);
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
         if (this.kem_ != HpkeKem.KEM_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.kem_);
         }

         if (this.kdf_ != HpkeKdf.KDF_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.kdf_);
         }

         if (this.aead_ != HpkeAead.AEAD_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.aead_);
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
      } else if (!(obj instanceof HpkeParams)) {
         return super.equals(obj);
      } else {
         HpkeParams other = (HpkeParams)obj;
         if (this.kem_ != other.kem_) {
            return false;
         } else if (this.kdf_ != other.kdf_) {
            return false;
         } else {
            return this.aead_ != other.aead_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.kem_;
         hash = 37 * hash + 2;
         hash = 53 * hash + this.kdf_;
         hash = 37 * hash + 3;
         hash = 53 * hash + this.aead_;
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static HpkeParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static HpkeParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static HpkeParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static HpkeParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static HpkeParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static HpkeParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static HpkeParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static HpkeParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static HpkeParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static HpkeParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static HpkeParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static HpkeParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public HpkeParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static HpkeParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static HpkeParams.Builder newBuilder(HpkeParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public HpkeParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new HpkeParams.Builder() : new HpkeParams.Builder().mergeFrom(this);
   }

   protected HpkeParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new HpkeParams.Builder(parent);
   }

   public static HpkeParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<HpkeParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<HpkeParams> getParserForType() {
      return PARSER;
   }

   public HpkeParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HpkeParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<HpkeParams.Builder> implements HpkeParamsOrBuilder {
      private int bitField0_;
      private int kem_ = 0;
      private int kdf_ = 0;
      private int aead_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return Hpke.internal_static_google_crypto_tink_HpkeParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Hpke.internal_static_google_crypto_tink_HpkeParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(HpkeParams.class, HpkeParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public HpkeParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.kem_ = 0;
         this.kdf_ = 0;
         this.aead_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Hpke.internal_static_google_crypto_tink_HpkeParams_descriptor;
      }

      public HpkeParams getDefaultInstanceForType() {
         return HpkeParams.getDefaultInstance();
      }

      public HpkeParams build() {
         HpkeParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public HpkeParams buildPartial() {
         HpkeParams result = new HpkeParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(HpkeParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.kem_ = this.kem_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.kdf_ = this.kdf_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.aead_ = this.aead_;
         }
      }

      public HpkeParams.Builder mergeFrom(Message other) {
         if (other instanceof HpkeParams) {
            return this.mergeFrom((HpkeParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public HpkeParams.Builder mergeFrom(HpkeParams other) {
         if (other == HpkeParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.kem_ != 0) {
               this.setKemValue(other.getKemValue());
            }

            if (other.kdf_ != 0) {
               this.setKdfValue(other.getKdfValue());
            }

            if (other.aead_ != 0) {
               this.setAeadValue(other.getAeadValue());
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

      public HpkeParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.kem_ = input.readEnum();
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.kdf_ = input.readEnum();
                        this.bitField0_ |= 2;
                        break;
                     case 24:
                        this.aead_ = input.readEnum();
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
      public int getKemValue() {
         return this.kem_;
      }

      public HpkeParams.Builder setKemValue(int value) {
         this.kem_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      @Override
      public HpkeKem getKem() {
         HpkeKem result = HpkeKem.forNumber(this.kem_);
         return result == null ? HpkeKem.UNRECOGNIZED : result;
      }

      public HpkeParams.Builder setKem(HpkeKem value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 1;
            this.kem_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public HpkeParams.Builder clearKem() {
         this.bitField0_ &= -2;
         this.kem_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getKdfValue() {
         return this.kdf_;
      }

      public HpkeParams.Builder setKdfValue(int value) {
         this.kdf_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      @Override
      public HpkeKdf getKdf() {
         HpkeKdf result = HpkeKdf.forNumber(this.kdf_);
         return result == null ? HpkeKdf.UNRECOGNIZED : result;
      }

      public HpkeParams.Builder setKdf(HpkeKdf value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 2;
            this.kdf_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public HpkeParams.Builder clearKdf() {
         this.bitField0_ &= -3;
         this.kdf_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getAeadValue() {
         return this.aead_;
      }

      public HpkeParams.Builder setAeadValue(int value) {
         this.aead_ = value;
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      @Override
      public HpkeAead getAead() {
         HpkeAead result = HpkeAead.forNumber(this.aead_);
         return result == null ? HpkeAead.UNRECOGNIZED : result;
      }

      public HpkeParams.Builder setAead(HpkeAead value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 4;
            this.aead_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public HpkeParams.Builder clearAead() {
         this.bitField0_ &= -5;
         this.aead_ = 0;
         this.onChanged();
         return this;
      }
   }
}
