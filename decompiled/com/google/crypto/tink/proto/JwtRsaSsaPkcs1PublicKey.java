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
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class JwtRsaSsaPkcs1PublicKey extends GeneratedMessage implements JwtRsaSsaPkcs1PublicKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int ALGORITHM_FIELD_NUMBER = 2;
   private int algorithm_ = 0;
   public static final int N_FIELD_NUMBER = 3;
   private ByteString n_ = ByteString.EMPTY;
   public static final int E_FIELD_NUMBER = 4;
   private ByteString e_ = ByteString.EMPTY;
   public static final int CUSTOM_KID_FIELD_NUMBER = 5;
   private JwtRsaSsaPkcs1PublicKey.CustomKid customKid_;
   private byte memoizedIsInitialized = -1;
   private static final JwtRsaSsaPkcs1PublicKey DEFAULT_INSTANCE = new JwtRsaSsaPkcs1PublicKey();
   private static final Parser<JwtRsaSsaPkcs1PublicKey> PARSER = new AbstractParser<JwtRsaSsaPkcs1PublicKey>() {
      public JwtRsaSsaPkcs1PublicKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         JwtRsaSsaPkcs1PublicKey.Builder builder = JwtRsaSsaPkcs1PublicKey.newBuilder();

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

   private JwtRsaSsaPkcs1PublicKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private JwtRsaSsaPkcs1PublicKey() {
      this.algorithm_ = 0;
      this.n_ = ByteString.EMPTY;
      this.e_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(JwtRsaSsaPkcs1PublicKey.class, JwtRsaSsaPkcs1PublicKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public int getAlgorithmValue() {
      return this.algorithm_;
   }

   @Override
   public JwtRsaSsaPkcs1Algorithm getAlgorithm() {
      JwtRsaSsaPkcs1Algorithm result = JwtRsaSsaPkcs1Algorithm.forNumber(this.algorithm_);
      return result == null ? JwtRsaSsaPkcs1Algorithm.UNRECOGNIZED : result;
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
   public boolean hasCustomKid() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public JwtRsaSsaPkcs1PublicKey.CustomKid getCustomKid() {
      return this.customKid_ == null ? JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance() : this.customKid_;
   }

   @Override
   public JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder getCustomKidOrBuilder() {
      return this.customKid_ == null ? JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance() : this.customKid_;
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

      if (this.algorithm_ != JwtRsaSsaPkcs1Algorithm.RS_UNKNOWN.getNumber()) {
         output.writeEnum(2, this.algorithm_);
      }

      if (!this.n_.isEmpty()) {
         output.writeBytes(3, this.n_);
      }

      if (!this.e_.isEmpty()) {
         output.writeBytes(4, this.e_);
      }

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(5, this.getCustomKid());
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

         if (this.algorithm_ != JwtRsaSsaPkcs1Algorithm.RS_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.algorithm_);
         }

         if (!this.n_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.n_);
         }

         if (!this.e_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.e_);
         }

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(5, this.getCustomKid());
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
      } else if (!(obj instanceof JwtRsaSsaPkcs1PublicKey)) {
         return super.equals(obj);
      } else {
         JwtRsaSsaPkcs1PublicKey other = (JwtRsaSsaPkcs1PublicKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.algorithm_ != other.algorithm_) {
            return false;
         } else if (!this.getN().equals(other.getN())) {
            return false;
         } else if (!this.getE().equals(other.getE())) {
            return false;
         } else if (this.hasCustomKid() != other.hasCustomKid()) {
            return false;
         } else {
            return this.hasCustomKid() && !this.getCustomKid().equals(other.getCustomKid()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 37 * hash + 2;
         hash = 53 * hash + this.algorithm_;
         hash = 37 * hash + 3;
         hash = 53 * hash + this.getN().hashCode();
         hash = 37 * hash + 4;
         hash = 53 * hash + this.getE().hashCode();
         if (this.hasCustomKid()) {
            hash = 37 * hash + 5;
            hash = 53 * hash + this.getCustomKid().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1PublicKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPkcs1PublicKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPkcs1PublicKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public JwtRsaSsaPkcs1PublicKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static JwtRsaSsaPkcs1PublicKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static JwtRsaSsaPkcs1PublicKey.Builder newBuilder(JwtRsaSsaPkcs1PublicKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public JwtRsaSsaPkcs1PublicKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new JwtRsaSsaPkcs1PublicKey.Builder() : new JwtRsaSsaPkcs1PublicKey.Builder().mergeFrom(this);
   }

   protected JwtRsaSsaPkcs1PublicKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new JwtRsaSsaPkcs1PublicKey.Builder(parent);
   }

   public static JwtRsaSsaPkcs1PublicKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<JwtRsaSsaPkcs1PublicKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<JwtRsaSsaPkcs1PublicKey> getParserForType() {
      return PARSER;
   }

   public JwtRsaSsaPkcs1PublicKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPkcs1PublicKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<JwtRsaSsaPkcs1PublicKey.Builder> implements JwtRsaSsaPkcs1PublicKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private int algorithm_ = 0;
      private ByteString n_ = ByteString.EMPTY;
      private ByteString e_ = ByteString.EMPTY;
      private JwtRsaSsaPkcs1PublicKey.CustomKid customKid_;
      private SingleFieldBuilder<JwtRsaSsaPkcs1PublicKey.CustomKid, JwtRsaSsaPkcs1PublicKey.CustomKid.Builder, JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder> customKidBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtRsaSsaPkcs1PublicKey.class, JwtRsaSsaPkcs1PublicKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (JwtRsaSsaPkcs1PublicKey.alwaysUseFieldBuilders) {
            this.internalGetCustomKidFieldBuilder();
         }
      }

      public JwtRsaSsaPkcs1PublicKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.algorithm_ = 0;
         this.n_ = ByteString.EMPTY;
         this.e_ = ByteString.EMPTY;
         this.customKid_ = null;
         if (this.customKidBuilder_ != null) {
            this.customKidBuilder_.dispose();
            this.customKidBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_descriptor;
      }

      public JwtRsaSsaPkcs1PublicKey getDefaultInstanceForType() {
         return JwtRsaSsaPkcs1PublicKey.getDefaultInstance();
      }

      public JwtRsaSsaPkcs1PublicKey build() {
         JwtRsaSsaPkcs1PublicKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public JwtRsaSsaPkcs1PublicKey buildPartial() {
         JwtRsaSsaPkcs1PublicKey result = new JwtRsaSsaPkcs1PublicKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(JwtRsaSsaPkcs1PublicKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.algorithm_ = this.algorithm_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.n_ = this.n_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.e_ = this.e_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 16) != 0) {
            result.customKid_ = this.customKidBuilder_ == null ? this.customKid_ : this.customKidBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder mergeFrom(Message other) {
         if (other instanceof JwtRsaSsaPkcs1PublicKey) {
            return this.mergeFrom((JwtRsaSsaPkcs1PublicKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public JwtRsaSsaPkcs1PublicKey.Builder mergeFrom(JwtRsaSsaPkcs1PublicKey other) {
         if (other == JwtRsaSsaPkcs1PublicKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.algorithm_ != 0) {
               this.setAlgorithmValue(other.getAlgorithmValue());
            }

            if (!other.getN().isEmpty()) {
               this.setN(other.getN());
            }

            if (!other.getE().isEmpty()) {
               this.setE(other.getE());
            }

            if (other.hasCustomKid()) {
               this.mergeCustomKid(other.getCustomKid());
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

      public JwtRsaSsaPkcs1PublicKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 16:
                        this.algorithm_ = input.readEnum();
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
                     case 42:
                        input.readMessage(this.internalGetCustomKidFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 16;
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

      public JwtRsaSsaPkcs1PublicKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getAlgorithmValue() {
         return this.algorithm_;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder setAlgorithmValue(int value) {
         this.algorithm_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      @Override
      public JwtRsaSsaPkcs1Algorithm getAlgorithm() {
         JwtRsaSsaPkcs1Algorithm result = JwtRsaSsaPkcs1Algorithm.forNumber(this.algorithm_);
         return result == null ? JwtRsaSsaPkcs1Algorithm.UNRECOGNIZED : result;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder setAlgorithm(JwtRsaSsaPkcs1Algorithm value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 2;
            this.algorithm_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPkcs1PublicKey.Builder clearAlgorithm() {
         this.bitField0_ &= -3;
         this.algorithm_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getN() {
         return this.n_;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder setN(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.n_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPkcs1PublicKey.Builder clearN() {
         this.bitField0_ &= -5;
         this.n_ = JwtRsaSsaPkcs1PublicKey.getDefaultInstance().getN();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getE() {
         return this.e_;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder setE(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.e_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPkcs1PublicKey.Builder clearE() {
         this.bitField0_ &= -9;
         this.e_ = JwtRsaSsaPkcs1PublicKey.getDefaultInstance().getE();
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasCustomKid() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public JwtRsaSsaPkcs1PublicKey.CustomKid getCustomKid() {
         if (this.customKidBuilder_ == null) {
            return this.customKid_ == null ? JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance() : this.customKid_;
         } else {
            return this.customKidBuilder_.getMessage();
         }
      }

      public JwtRsaSsaPkcs1PublicKey.Builder setCustomKid(JwtRsaSsaPkcs1PublicKey.CustomKid value) {
         if (this.customKidBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.customKid_ = value;
         } else {
            this.customKidBuilder_.setMessage(value);
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder setCustomKid(JwtRsaSsaPkcs1PublicKey.CustomKid.Builder builderForValue) {
         if (this.customKidBuilder_ == null) {
            this.customKid_ = builderForValue.build();
         } else {
            this.customKidBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder mergeCustomKid(JwtRsaSsaPkcs1PublicKey.CustomKid value) {
         if (this.customKidBuilder_ == null) {
            if ((this.bitField0_ & 16) != 0 && this.customKid_ != null && this.customKid_ != JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance()) {
               this.getCustomKidBuilder().mergeFrom(value);
            } else {
               this.customKid_ = value;
            }
         } else {
            this.customKidBuilder_.mergeFrom(value);
         }

         if (this.customKid_ != null) {
            this.bitField0_ |= 16;
            this.onChanged();
         }

         return this;
      }

      public JwtRsaSsaPkcs1PublicKey.Builder clearCustomKid() {
         this.bitField0_ &= -17;
         this.customKid_ = null;
         if (this.customKidBuilder_ != null) {
            this.customKidBuilder_.dispose();
            this.customKidBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder getCustomKidBuilder() {
         this.bitField0_ |= 16;
         this.onChanged();
         return this.internalGetCustomKidFieldBuilder().getBuilder();
      }

      @Override
      public JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder getCustomKidOrBuilder() {
         if (this.customKidBuilder_ != null) {
            return this.customKidBuilder_.getMessageOrBuilder();
         } else {
            return this.customKid_ == null ? JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance() : this.customKid_;
         }
      }

      private SingleFieldBuilder<JwtRsaSsaPkcs1PublicKey.CustomKid, JwtRsaSsaPkcs1PublicKey.CustomKid.Builder, JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder> internalGetCustomKidFieldBuilder() {
         if (this.customKidBuilder_ == null) {
            this.customKidBuilder_ = new SingleFieldBuilder<>(this.getCustomKid(), this.getParentForChildren(), this.isClean());
            this.customKid_ = null;
         }

         return this.customKidBuilder_;
      }
   }

   public static final class CustomKid extends GeneratedMessage implements JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int VALUE_FIELD_NUMBER = 1;
      private volatile Object value_ = "";
      private byte memoizedIsInitialized = -1;
      private static final JwtRsaSsaPkcs1PublicKey.CustomKid DEFAULT_INSTANCE = new JwtRsaSsaPkcs1PublicKey.CustomKid();
      private static final Parser<JwtRsaSsaPkcs1PublicKey.CustomKid> PARSER = new AbstractParser<JwtRsaSsaPkcs1PublicKey.CustomKid>() {
         public JwtRsaSsaPkcs1PublicKey.CustomKid parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            JwtRsaSsaPkcs1PublicKey.CustomKid.Builder builder = JwtRsaSsaPkcs1PublicKey.CustomKid.newBuilder();

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

      private CustomKid(GeneratedMessage.Builder<?> builder) {
         super(builder);
      }

      private CustomKid() {
         this.value_ = "";
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtRsaSsaPkcs1PublicKey.CustomKid.class, JwtRsaSsaPkcs1PublicKey.CustomKid.Builder.class);
      }

      @Override
      public String getValue() {
         Object ref = this.value_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.value_ = s;
            return s;
         }
      }

      @Override
      public ByteString getValueBytes() {
         Object ref = this.value_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.value_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
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
         if (!GeneratedMessage.isStringEmpty(this.value_)) {
            GeneratedMessage.writeString(output, 1, this.value_);
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
            if (!GeneratedMessage.isStringEmpty(this.value_)) {
               size += GeneratedMessage.computeStringSize(1, this.value_);
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
         } else if (!(obj instanceof JwtRsaSsaPkcs1PublicKey.CustomKid)) {
            return super.equals(obj);
         } else {
            JwtRsaSsaPkcs1PublicKey.CustomKid other = (JwtRsaSsaPkcs1PublicKey.CustomKid)obj;
            return !this.getValue().equals(other.getValue()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
            hash = 53 * hash + this.getValue().hashCode();
            hash = 29 * hash + this.getUnknownFields().hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder newBuilderForType() {
         return newBuilder();
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid.Builder newBuilder(JwtRsaSsaPkcs1PublicKey.CustomKid prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new JwtRsaSsaPkcs1PublicKey.CustomKid.Builder() : new JwtRsaSsaPkcs1PublicKey.CustomKid.Builder().mergeFrom(this);
      }

      protected JwtRsaSsaPkcs1PublicKey.CustomKid.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new JwtRsaSsaPkcs1PublicKey.CustomKid.Builder(parent);
      }

      public static JwtRsaSsaPkcs1PublicKey.CustomKid getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<JwtRsaSsaPkcs1PublicKey.CustomKid> parser() {
         return PARSER;
      }

      @Override
      public Parser<JwtRsaSsaPkcs1PublicKey.CustomKid> getParserForType() {
         return PARSER;
      }

      public JwtRsaSsaPkcs1PublicKey.CustomKid getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPkcs1PublicKey.CustomKid.class.getName());
      }

      public static final class Builder
         extends GeneratedMessage.Builder<JwtRsaSsaPkcs1PublicKey.CustomKid.Builder>
         implements JwtRsaSsaPkcs1PublicKey.CustomKidOrBuilder {
         private int bitField0_;
         private Object value_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_fieldAccessorTable
               .ensureFieldAccessorsInitialized(JwtRsaSsaPkcs1PublicKey.CustomKid.class, JwtRsaSsaPkcs1PublicKey.CustomKid.Builder.class);
         }

         private Builder() {
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.value_ = "";
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1PublicKey_CustomKid_descriptor;
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid getDefaultInstanceForType() {
            return JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance();
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid build() {
            JwtRsaSsaPkcs1PublicKey.CustomKid result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid buildPartial() {
            JwtRsaSsaPkcs1PublicKey.CustomKid result = new JwtRsaSsaPkcs1PublicKey.CustomKid(this);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartial0(JwtRsaSsaPkcs1PublicKey.CustomKid result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
               result.value_ = this.value_;
            }
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder mergeFrom(Message other) {
            if (other instanceof JwtRsaSsaPkcs1PublicKey.CustomKid) {
               return this.mergeFrom((JwtRsaSsaPkcs1PublicKey.CustomKid)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder mergeFrom(JwtRsaSsaPkcs1PublicKey.CustomKid other) {
            if (other == JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance()) {
               return this;
            } else {
               if (!other.getValue().isEmpty()) {
                  this.value_ = other.value_;
                  this.bitField0_ |= 1;
                  this.onChanged();
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

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                           this.value_ = input.readStringRequireUtf8();
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
         public String getValue() {
            Object ref = this.value_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               this.value_ = s;
               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getValueBytes() {
            Object ref = this.value_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.value_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder setValue(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.value_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder clearValue() {
            this.value_ = JwtRsaSsaPkcs1PublicKey.CustomKid.getDefaultInstance().getValue();
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public JwtRsaSsaPkcs1PublicKey.CustomKid.Builder setValueBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               JwtRsaSsaPkcs1PublicKey.CustomKid.checkByteStringIsUtf8(value);
               this.value_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }
      }
   }

   public interface CustomKidOrBuilder extends MessageOrBuilder {
      String getValue();

      ByteString getValueBytes();
   }
}
