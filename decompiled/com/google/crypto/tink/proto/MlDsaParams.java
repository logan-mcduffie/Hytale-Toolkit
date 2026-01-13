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

public final class MlDsaParams extends GeneratedMessage implements MlDsaParamsOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int ML_DSA_INSTANCE_FIELD_NUMBER = 1;
   private int mlDsaInstance_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final MlDsaParams DEFAULT_INSTANCE = new MlDsaParams();
   private static final Parser<MlDsaParams> PARSER = new AbstractParser<MlDsaParams>() {
      public MlDsaParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         MlDsaParams.Builder builder = MlDsaParams.newBuilder();

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

   private MlDsaParams(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private MlDsaParams() {
      this.mlDsaInstance_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return MlDsa.internal_static_google_crypto_tink_MlDsaParams_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return MlDsa.internal_static_google_crypto_tink_MlDsaParams_fieldAccessorTable
         .ensureFieldAccessorsInitialized(MlDsaParams.class, MlDsaParams.Builder.class);
   }

   @Override
   public int getMlDsaInstanceValue() {
      return this.mlDsaInstance_;
   }

   @Override
   public MlDsaInstance getMlDsaInstance() {
      MlDsaInstance result = MlDsaInstance.forNumber(this.mlDsaInstance_);
      return result == null ? MlDsaInstance.UNRECOGNIZED : result;
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
      if (this.mlDsaInstance_ != MlDsaInstance.ML_DSA_UNKNOWN_INSTANCE.getNumber()) {
         output.writeEnum(1, this.mlDsaInstance_);
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
         if (this.mlDsaInstance_ != MlDsaInstance.ML_DSA_UNKNOWN_INSTANCE.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.mlDsaInstance_);
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
      } else if (!(obj instanceof MlDsaParams)) {
         return super.equals(obj);
      } else {
         MlDsaParams other = (MlDsaParams)obj;
         return this.mlDsaInstance_ != other.mlDsaInstance_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.mlDsaInstance_;
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static MlDsaParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static MlDsaParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static MlDsaParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static MlDsaParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static MlDsaParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static MlDsaParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static MlDsaParams parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static MlDsaParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static MlDsaParams parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static MlDsaParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static MlDsaParams parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static MlDsaParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public MlDsaParams.Builder newBuilderForType() {
      return newBuilder();
   }

   public static MlDsaParams.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static MlDsaParams.Builder newBuilder(MlDsaParams prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public MlDsaParams.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new MlDsaParams.Builder() : new MlDsaParams.Builder().mergeFrom(this);
   }

   protected MlDsaParams.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new MlDsaParams.Builder(parent);
   }

   public static MlDsaParams getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<MlDsaParams> parser() {
      return PARSER;
   }

   @Override
   public Parser<MlDsaParams> getParserForType() {
      return PARSER;
   }

   public MlDsaParams getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", MlDsaParams.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<MlDsaParams.Builder> implements MlDsaParamsOrBuilder {
      private int bitField0_;
      private int mlDsaInstance_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return MlDsa.internal_static_google_crypto_tink_MlDsaParams_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return MlDsa.internal_static_google_crypto_tink_MlDsaParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MlDsaParams.class, MlDsaParams.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public MlDsaParams.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.mlDsaInstance_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return MlDsa.internal_static_google_crypto_tink_MlDsaParams_descriptor;
      }

      public MlDsaParams getDefaultInstanceForType() {
         return MlDsaParams.getDefaultInstance();
      }

      public MlDsaParams build() {
         MlDsaParams result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public MlDsaParams buildPartial() {
         MlDsaParams result = new MlDsaParams(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(MlDsaParams result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.mlDsaInstance_ = this.mlDsaInstance_;
         }
      }

      public MlDsaParams.Builder mergeFrom(Message other) {
         if (other instanceof MlDsaParams) {
            return this.mergeFrom((MlDsaParams)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public MlDsaParams.Builder mergeFrom(MlDsaParams other) {
         if (other == MlDsaParams.getDefaultInstance()) {
            return this;
         } else {
            if (other.mlDsaInstance_ != 0) {
               this.setMlDsaInstanceValue(other.getMlDsaInstanceValue());
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

      public MlDsaParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.mlDsaInstance_ = input.readEnum();
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
      public int getMlDsaInstanceValue() {
         return this.mlDsaInstance_;
      }

      public MlDsaParams.Builder setMlDsaInstanceValue(int value) {
         this.mlDsaInstance_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      @Override
      public MlDsaInstance getMlDsaInstance() {
         MlDsaInstance result = MlDsaInstance.forNumber(this.mlDsaInstance_);
         return result == null ? MlDsaInstance.UNRECOGNIZED : result;
      }

      public MlDsaParams.Builder setMlDsaInstance(MlDsaInstance value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 1;
            this.mlDsaInstance_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public MlDsaParams.Builder clearMlDsaInstance() {
         this.bitField0_ &= -2;
         this.mlDsaInstance_ = 0;
         this.onChanged();
         return this;
      }
   }
}
