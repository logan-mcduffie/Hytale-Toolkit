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

public final class JwtEcdsaPublicKey extends GeneratedMessage implements JwtEcdsaPublicKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int ALGORITHM_FIELD_NUMBER = 2;
   private int algorithm_ = 0;
   public static final int X_FIELD_NUMBER = 3;
   private ByteString x_ = ByteString.EMPTY;
   public static final int Y_FIELD_NUMBER = 4;
   private ByteString y_ = ByteString.EMPTY;
   public static final int CUSTOM_KID_FIELD_NUMBER = 5;
   private JwtEcdsaPublicKey.CustomKid customKid_;
   private byte memoizedIsInitialized = -1;
   private static final JwtEcdsaPublicKey DEFAULT_INSTANCE = new JwtEcdsaPublicKey();
   private static final Parser<JwtEcdsaPublicKey> PARSER = new AbstractParser<JwtEcdsaPublicKey>() {
      public JwtEcdsaPublicKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         JwtEcdsaPublicKey.Builder builder = JwtEcdsaPublicKey.newBuilder();

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

   private JwtEcdsaPublicKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private JwtEcdsaPublicKey() {
      this.algorithm_ = 0;
      this.x_ = ByteString.EMPTY;
      this.y_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(JwtEcdsaPublicKey.class, JwtEcdsaPublicKey.Builder.class);
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
   public JwtEcdsaAlgorithm getAlgorithm() {
      JwtEcdsaAlgorithm result = JwtEcdsaAlgorithm.forNumber(this.algorithm_);
      return result == null ? JwtEcdsaAlgorithm.UNRECOGNIZED : result;
   }

   @Override
   public ByteString getX() {
      return this.x_;
   }

   @Override
   public ByteString getY() {
      return this.y_;
   }

   @Override
   public boolean hasCustomKid() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public JwtEcdsaPublicKey.CustomKid getCustomKid() {
      return this.customKid_ == null ? JwtEcdsaPublicKey.CustomKid.getDefaultInstance() : this.customKid_;
   }

   @Override
   public JwtEcdsaPublicKey.CustomKidOrBuilder getCustomKidOrBuilder() {
      return this.customKid_ == null ? JwtEcdsaPublicKey.CustomKid.getDefaultInstance() : this.customKid_;
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

      if (this.algorithm_ != JwtEcdsaAlgorithm.ES_UNKNOWN.getNumber()) {
         output.writeEnum(2, this.algorithm_);
      }

      if (!this.x_.isEmpty()) {
         output.writeBytes(3, this.x_);
      }

      if (!this.y_.isEmpty()) {
         output.writeBytes(4, this.y_);
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

         if (this.algorithm_ != JwtEcdsaAlgorithm.ES_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.algorithm_);
         }

         if (!this.x_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.x_);
         }

         if (!this.y_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.y_);
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
      } else if (!(obj instanceof JwtEcdsaPublicKey)) {
         return super.equals(obj);
      } else {
         JwtEcdsaPublicKey other = (JwtEcdsaPublicKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.algorithm_ != other.algorithm_) {
            return false;
         } else if (!this.getX().equals(other.getX())) {
            return false;
         } else if (!this.getY().equals(other.getY())) {
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
         hash = 53 * hash + this.getX().hashCode();
         hash = 37 * hash + 4;
         hash = 53 * hash + this.getY().hashCode();
         if (this.hasCustomKid()) {
            hash = 37 * hash + 5;
            hash = 53 * hash + this.getCustomKid().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static JwtEcdsaPublicKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtEcdsaPublicKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtEcdsaPublicKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtEcdsaPublicKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtEcdsaPublicKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtEcdsaPublicKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtEcdsaPublicKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtEcdsaPublicKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtEcdsaPublicKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static JwtEcdsaPublicKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtEcdsaPublicKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtEcdsaPublicKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public JwtEcdsaPublicKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static JwtEcdsaPublicKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static JwtEcdsaPublicKey.Builder newBuilder(JwtEcdsaPublicKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public JwtEcdsaPublicKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new JwtEcdsaPublicKey.Builder() : new JwtEcdsaPublicKey.Builder().mergeFrom(this);
   }

   protected JwtEcdsaPublicKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new JwtEcdsaPublicKey.Builder(parent);
   }

   public static JwtEcdsaPublicKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<JwtEcdsaPublicKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<JwtEcdsaPublicKey> getParserForType() {
      return PARSER;
   }

   public JwtEcdsaPublicKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtEcdsaPublicKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<JwtEcdsaPublicKey.Builder> implements JwtEcdsaPublicKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private int algorithm_ = 0;
      private ByteString x_ = ByteString.EMPTY;
      private ByteString y_ = ByteString.EMPTY;
      private JwtEcdsaPublicKey.CustomKid customKid_;
      private SingleFieldBuilder<JwtEcdsaPublicKey.CustomKid, JwtEcdsaPublicKey.CustomKid.Builder, JwtEcdsaPublicKey.CustomKidOrBuilder> customKidBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtEcdsaPublicKey.class, JwtEcdsaPublicKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (JwtEcdsaPublicKey.alwaysUseFieldBuilders) {
            this.internalGetCustomKidFieldBuilder();
         }
      }

      public JwtEcdsaPublicKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.algorithm_ = 0;
         this.x_ = ByteString.EMPTY;
         this.y_ = ByteString.EMPTY;
         this.customKid_ = null;
         if (this.customKidBuilder_ != null) {
            this.customKidBuilder_.dispose();
            this.customKidBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_descriptor;
      }

      public JwtEcdsaPublicKey getDefaultInstanceForType() {
         return JwtEcdsaPublicKey.getDefaultInstance();
      }

      public JwtEcdsaPublicKey build() {
         JwtEcdsaPublicKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public JwtEcdsaPublicKey buildPartial() {
         JwtEcdsaPublicKey result = new JwtEcdsaPublicKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(JwtEcdsaPublicKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.algorithm_ = this.algorithm_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.x_ = this.x_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.y_ = this.y_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 16) != 0) {
            result.customKid_ = this.customKidBuilder_ == null ? this.customKid_ : this.customKidBuilder_.build();
            to_bitField0_ |= 1;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public JwtEcdsaPublicKey.Builder mergeFrom(Message other) {
         if (other instanceof JwtEcdsaPublicKey) {
            return this.mergeFrom((JwtEcdsaPublicKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public JwtEcdsaPublicKey.Builder mergeFrom(JwtEcdsaPublicKey other) {
         if (other == JwtEcdsaPublicKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.algorithm_ != 0) {
               this.setAlgorithmValue(other.getAlgorithmValue());
            }

            if (!other.getX().isEmpty()) {
               this.setX(other.getX());
            }

            if (!other.getY().isEmpty()) {
               this.setY(other.getY());
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

      public JwtEcdsaPublicKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.x_ = input.readBytes();
                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        this.y_ = input.readBytes();
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

      public JwtEcdsaPublicKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public JwtEcdsaPublicKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getAlgorithmValue() {
         return this.algorithm_;
      }

      public JwtEcdsaPublicKey.Builder setAlgorithmValue(int value) {
         this.algorithm_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      @Override
      public JwtEcdsaAlgorithm getAlgorithm() {
         JwtEcdsaAlgorithm result = JwtEcdsaAlgorithm.forNumber(this.algorithm_);
         return result == null ? JwtEcdsaAlgorithm.UNRECOGNIZED : result;
      }

      public JwtEcdsaPublicKey.Builder setAlgorithm(JwtEcdsaAlgorithm value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 2;
            this.algorithm_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public JwtEcdsaPublicKey.Builder clearAlgorithm() {
         this.bitField0_ &= -3;
         this.algorithm_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getX() {
         return this.x_;
      }

      public JwtEcdsaPublicKey.Builder setX(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.x_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public JwtEcdsaPublicKey.Builder clearX() {
         this.bitField0_ &= -5;
         this.x_ = JwtEcdsaPublicKey.getDefaultInstance().getX();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getY() {
         return this.y_;
      }

      public JwtEcdsaPublicKey.Builder setY(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.y_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public JwtEcdsaPublicKey.Builder clearY() {
         this.bitField0_ &= -9;
         this.y_ = JwtEcdsaPublicKey.getDefaultInstance().getY();
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasCustomKid() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public JwtEcdsaPublicKey.CustomKid getCustomKid() {
         if (this.customKidBuilder_ == null) {
            return this.customKid_ == null ? JwtEcdsaPublicKey.CustomKid.getDefaultInstance() : this.customKid_;
         } else {
            return this.customKidBuilder_.getMessage();
         }
      }

      public JwtEcdsaPublicKey.Builder setCustomKid(JwtEcdsaPublicKey.CustomKid value) {
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

      public JwtEcdsaPublicKey.Builder setCustomKid(JwtEcdsaPublicKey.CustomKid.Builder builderForValue) {
         if (this.customKidBuilder_ == null) {
            this.customKid_ = builderForValue.build();
         } else {
            this.customKidBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public JwtEcdsaPublicKey.Builder mergeCustomKid(JwtEcdsaPublicKey.CustomKid value) {
         if (this.customKidBuilder_ == null) {
            if ((this.bitField0_ & 16) != 0 && this.customKid_ != null && this.customKid_ != JwtEcdsaPublicKey.CustomKid.getDefaultInstance()) {
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

      public JwtEcdsaPublicKey.Builder clearCustomKid() {
         this.bitField0_ &= -17;
         this.customKid_ = null;
         if (this.customKidBuilder_ != null) {
            this.customKidBuilder_.dispose();
            this.customKidBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public JwtEcdsaPublicKey.CustomKid.Builder getCustomKidBuilder() {
         this.bitField0_ |= 16;
         this.onChanged();
         return this.internalGetCustomKidFieldBuilder().getBuilder();
      }

      @Override
      public JwtEcdsaPublicKey.CustomKidOrBuilder getCustomKidOrBuilder() {
         if (this.customKidBuilder_ != null) {
            return this.customKidBuilder_.getMessageOrBuilder();
         } else {
            return this.customKid_ == null ? JwtEcdsaPublicKey.CustomKid.getDefaultInstance() : this.customKid_;
         }
      }

      private SingleFieldBuilder<JwtEcdsaPublicKey.CustomKid, JwtEcdsaPublicKey.CustomKid.Builder, JwtEcdsaPublicKey.CustomKidOrBuilder> internalGetCustomKidFieldBuilder() {
         if (this.customKidBuilder_ == null) {
            this.customKidBuilder_ = new SingleFieldBuilder<>(this.getCustomKid(), this.getParentForChildren(), this.isClean());
            this.customKid_ = null;
         }

         return this.customKidBuilder_;
      }
   }

   public static final class CustomKid extends GeneratedMessage implements JwtEcdsaPublicKey.CustomKidOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int VALUE_FIELD_NUMBER = 1;
      private volatile Object value_ = "";
      private byte memoizedIsInitialized = -1;
      private static final JwtEcdsaPublicKey.CustomKid DEFAULT_INSTANCE = new JwtEcdsaPublicKey.CustomKid();
      private static final Parser<JwtEcdsaPublicKey.CustomKid> PARSER = new AbstractParser<JwtEcdsaPublicKey.CustomKid>() {
         public JwtEcdsaPublicKey.CustomKid parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            JwtEcdsaPublicKey.CustomKid.Builder builder = JwtEcdsaPublicKey.CustomKid.newBuilder();

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
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_CustomKid_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_CustomKid_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtEcdsaPublicKey.CustomKid.class, JwtEcdsaPublicKey.CustomKid.Builder.class);
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
         } else if (!(obj instanceof JwtEcdsaPublicKey.CustomKid)) {
            return super.equals(obj);
         } else {
            JwtEcdsaPublicKey.CustomKid other = (JwtEcdsaPublicKey.CustomKid)obj;
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

      public static JwtEcdsaPublicKey.CustomKid parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static JwtEcdsaPublicKey.CustomKid parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static JwtEcdsaPublicKey.CustomKid parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static JwtEcdsaPublicKey.CustomKid parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public JwtEcdsaPublicKey.CustomKid.Builder newBuilderForType() {
         return newBuilder();
      }

      public static JwtEcdsaPublicKey.CustomKid.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static JwtEcdsaPublicKey.CustomKid.Builder newBuilder(JwtEcdsaPublicKey.CustomKid prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public JwtEcdsaPublicKey.CustomKid.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new JwtEcdsaPublicKey.CustomKid.Builder() : new JwtEcdsaPublicKey.CustomKid.Builder().mergeFrom(this);
      }

      protected JwtEcdsaPublicKey.CustomKid.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new JwtEcdsaPublicKey.CustomKid.Builder(parent);
      }

      public static JwtEcdsaPublicKey.CustomKid getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<JwtEcdsaPublicKey.CustomKid> parser() {
         return PARSER;
      }

      @Override
      public Parser<JwtEcdsaPublicKey.CustomKid> getParserForType() {
         return PARSER;
      }

      public JwtEcdsaPublicKey.CustomKid getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtEcdsaPublicKey.CustomKid.class.getName());
      }

      public static final class Builder extends GeneratedMessage.Builder<JwtEcdsaPublicKey.CustomKid.Builder> implements JwtEcdsaPublicKey.CustomKidOrBuilder {
         private int bitField0_;
         private Object value_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_CustomKid_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_CustomKid_fieldAccessorTable
               .ensureFieldAccessorsInitialized(JwtEcdsaPublicKey.CustomKid.class, JwtEcdsaPublicKey.CustomKid.Builder.class);
         }

         private Builder() {
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
         }

         public JwtEcdsaPublicKey.CustomKid.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.value_ = "";
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaPublicKey_CustomKid_descriptor;
         }

         public JwtEcdsaPublicKey.CustomKid getDefaultInstanceForType() {
            return JwtEcdsaPublicKey.CustomKid.getDefaultInstance();
         }

         public JwtEcdsaPublicKey.CustomKid build() {
            JwtEcdsaPublicKey.CustomKid result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public JwtEcdsaPublicKey.CustomKid buildPartial() {
            JwtEcdsaPublicKey.CustomKid result = new JwtEcdsaPublicKey.CustomKid(this);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartial0(JwtEcdsaPublicKey.CustomKid result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
               result.value_ = this.value_;
            }
         }

         public JwtEcdsaPublicKey.CustomKid.Builder mergeFrom(Message other) {
            if (other instanceof JwtEcdsaPublicKey.CustomKid) {
               return this.mergeFrom((JwtEcdsaPublicKey.CustomKid)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public JwtEcdsaPublicKey.CustomKid.Builder mergeFrom(JwtEcdsaPublicKey.CustomKid other) {
            if (other == JwtEcdsaPublicKey.CustomKid.getDefaultInstance()) {
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

         public JwtEcdsaPublicKey.CustomKid.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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

         public JwtEcdsaPublicKey.CustomKid.Builder setValue(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.value_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         public JwtEcdsaPublicKey.CustomKid.Builder clearValue() {
            this.value_ = JwtEcdsaPublicKey.CustomKid.getDefaultInstance().getValue();
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public JwtEcdsaPublicKey.CustomKid.Builder setValueBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               JwtEcdsaPublicKey.CustomKid.checkByteStringIsUtf8(value);
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
