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

public final class JwtRsaSsaPssPrivateKey extends GeneratedMessage implements JwtRsaSsaPssPrivateKeyOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int PUBLIC_KEY_FIELD_NUMBER = 2;
   private JwtRsaSsaPssPublicKey publicKey_;
   public static final int D_FIELD_NUMBER = 3;
   private ByteString d_ = ByteString.EMPTY;
   public static final int P_FIELD_NUMBER = 4;
   private ByteString p_ = ByteString.EMPTY;
   public static final int Q_FIELD_NUMBER = 5;
   private ByteString q_ = ByteString.EMPTY;
   public static final int DP_FIELD_NUMBER = 6;
   private ByteString dp_ = ByteString.EMPTY;
   public static final int DQ_FIELD_NUMBER = 7;
   private ByteString dq_ = ByteString.EMPTY;
   public static final int CRT_FIELD_NUMBER = 8;
   private ByteString crt_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final JwtRsaSsaPssPrivateKey DEFAULT_INSTANCE = new JwtRsaSsaPssPrivateKey();
   private static final Parser<JwtRsaSsaPssPrivateKey> PARSER = new AbstractParser<JwtRsaSsaPssPrivateKey>() {
      public JwtRsaSsaPssPrivateKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         JwtRsaSsaPssPrivateKey.Builder builder = JwtRsaSsaPssPrivateKey.newBuilder();

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

   private JwtRsaSsaPssPrivateKey(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private JwtRsaSsaPssPrivateKey() {
      this.d_ = ByteString.EMPTY;
      this.p_ = ByteString.EMPTY;
      this.q_ = ByteString.EMPTY;
      this.dp_ = ByteString.EMPTY;
      this.dq_ = ByteString.EMPTY;
      this.crt_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPrivateKey_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPrivateKey_fieldAccessorTable
         .ensureFieldAccessorsInitialized(JwtRsaSsaPssPrivateKey.class, JwtRsaSsaPssPrivateKey.Builder.class);
   }

   @Override
   public int getVersion() {
      return this.version_;
   }

   @Override
   public boolean hasPublicKey() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public JwtRsaSsaPssPublicKey getPublicKey() {
      return this.publicKey_ == null ? JwtRsaSsaPssPublicKey.getDefaultInstance() : this.publicKey_;
   }

   @Override
   public JwtRsaSsaPssPublicKeyOrBuilder getPublicKeyOrBuilder() {
      return this.publicKey_ == null ? JwtRsaSsaPssPublicKey.getDefaultInstance() : this.publicKey_;
   }

   @Override
   public ByteString getD() {
      return this.d_;
   }

   @Override
   public ByteString getP() {
      return this.p_;
   }

   @Override
   public ByteString getQ() {
      return this.q_;
   }

   @Override
   public ByteString getDp() {
      return this.dp_;
   }

   @Override
   public ByteString getDq() {
      return this.dq_;
   }

   @Override
   public ByteString getCrt() {
      return this.crt_;
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
         output.writeMessage(2, this.getPublicKey());
      }

      if (!this.d_.isEmpty()) {
         output.writeBytes(3, this.d_);
      }

      if (!this.p_.isEmpty()) {
         output.writeBytes(4, this.p_);
      }

      if (!this.q_.isEmpty()) {
         output.writeBytes(5, this.q_);
      }

      if (!this.dp_.isEmpty()) {
         output.writeBytes(6, this.dp_);
      }

      if (!this.dq_.isEmpty()) {
         output.writeBytes(7, this.dq_);
      }

      if (!this.crt_.isEmpty()) {
         output.writeBytes(8, this.crt_);
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
            size += CodedOutputStream.computeMessageSize(2, this.getPublicKey());
         }

         if (!this.d_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.d_);
         }

         if (!this.p_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.p_);
         }

         if (!this.q_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(5, this.q_);
         }

         if (!this.dp_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(6, this.dp_);
         }

         if (!this.dq_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(7, this.dq_);
         }

         if (!this.crt_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(8, this.crt_);
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
      } else if (!(obj instanceof JwtRsaSsaPssPrivateKey)) {
         return super.equals(obj);
      } else {
         JwtRsaSsaPssPrivateKey other = (JwtRsaSsaPssPrivateKey)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.hasPublicKey() != other.hasPublicKey()) {
            return false;
         } else if (this.hasPublicKey() && !this.getPublicKey().equals(other.getPublicKey())) {
            return false;
         } else if (!this.getD().equals(other.getD())) {
            return false;
         } else if (!this.getP().equals(other.getP())) {
            return false;
         } else if (!this.getQ().equals(other.getQ())) {
            return false;
         } else if (!this.getDp().equals(other.getDp())) {
            return false;
         } else if (!this.getDq().equals(other.getDq())) {
            return false;
         } else {
            return !this.getCrt().equals(other.getCrt()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         if (this.hasPublicKey()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getPublicKey().hashCode();
         }

         hash = 37 * hash + 3;
         hash = 53 * hash + this.getD().hashCode();
         hash = 37 * hash + 4;
         hash = 53 * hash + this.getP().hashCode();
         hash = 37 * hash + 5;
         hash = 53 * hash + this.getQ().hashCode();
         hash = 37 * hash + 6;
         hash = 53 * hash + this.getDp().hashCode();
         hash = 37 * hash + 7;
         hash = 53 * hash + this.getDq().hashCode();
         hash = 37 * hash + 8;
         hash = 53 * hash + this.getCrt().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtRsaSsaPssPrivateKey parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPssPrivateKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPssPrivateKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public JwtRsaSsaPssPrivateKey.Builder newBuilderForType() {
      return newBuilder();
   }

   public static JwtRsaSsaPssPrivateKey.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static JwtRsaSsaPssPrivateKey.Builder newBuilder(JwtRsaSsaPssPrivateKey prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public JwtRsaSsaPssPrivateKey.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new JwtRsaSsaPssPrivateKey.Builder() : new JwtRsaSsaPssPrivateKey.Builder().mergeFrom(this);
   }

   protected JwtRsaSsaPssPrivateKey.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new JwtRsaSsaPssPrivateKey.Builder(parent);
   }

   public static JwtRsaSsaPssPrivateKey getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<JwtRsaSsaPssPrivateKey> parser() {
      return PARSER;
   }

   @Override
   public Parser<JwtRsaSsaPssPrivateKey> getParserForType() {
      return PARSER;
   }

   public JwtRsaSsaPssPrivateKey getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPssPrivateKey.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<JwtRsaSsaPssPrivateKey.Builder> implements JwtRsaSsaPssPrivateKeyOrBuilder {
      private int bitField0_;
      private int version_;
      private JwtRsaSsaPssPublicKey publicKey_;
      private SingleFieldBuilder<JwtRsaSsaPssPublicKey, JwtRsaSsaPssPublicKey.Builder, JwtRsaSsaPssPublicKeyOrBuilder> publicKeyBuilder_;
      private ByteString d_ = ByteString.EMPTY;
      private ByteString p_ = ByteString.EMPTY;
      private ByteString q_ = ByteString.EMPTY;
      private ByteString dp_ = ByteString.EMPTY;
      private ByteString dq_ = ByteString.EMPTY;
      private ByteString crt_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPrivateKey_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPrivateKey_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtRsaSsaPssPrivateKey.class, JwtRsaSsaPssPrivateKey.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (JwtRsaSsaPssPrivateKey.alwaysUseFieldBuilders) {
            this.internalGetPublicKeyFieldBuilder();
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.publicKey_ = null;
         if (this.publicKeyBuilder_ != null) {
            this.publicKeyBuilder_.dispose();
            this.publicKeyBuilder_ = null;
         }

         this.d_ = ByteString.EMPTY;
         this.p_ = ByteString.EMPTY;
         this.q_ = ByteString.EMPTY;
         this.dp_ = ByteString.EMPTY;
         this.dq_ = ByteString.EMPTY;
         this.crt_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPrivateKey_descriptor;
      }

      public JwtRsaSsaPssPrivateKey getDefaultInstanceForType() {
         return JwtRsaSsaPssPrivateKey.getDefaultInstance();
      }

      public JwtRsaSsaPssPrivateKey build() {
         JwtRsaSsaPssPrivateKey result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public JwtRsaSsaPssPrivateKey buildPartial() {
         JwtRsaSsaPssPrivateKey result = new JwtRsaSsaPssPrivateKey(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(JwtRsaSsaPssPrivateKey result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 2) != 0) {
            result.publicKey_ = this.publicKeyBuilder_ == null ? this.publicKey_ : this.publicKeyBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.d_ = this.d_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.p_ = this.p_;
         }

         if ((from_bitField0_ & 16) != 0) {
            result.q_ = this.q_;
         }

         if ((from_bitField0_ & 32) != 0) {
            result.dp_ = this.dp_;
         }

         if ((from_bitField0_ & 64) != 0) {
            result.dq_ = this.dq_;
         }

         if ((from_bitField0_ & 128) != 0) {
            result.crt_ = this.crt_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public JwtRsaSsaPssPrivateKey.Builder mergeFrom(Message other) {
         if (other instanceof JwtRsaSsaPssPrivateKey) {
            return this.mergeFrom((JwtRsaSsaPssPrivateKey)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder mergeFrom(JwtRsaSsaPssPrivateKey other) {
         if (other == JwtRsaSsaPssPrivateKey.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.hasPublicKey()) {
               this.mergePublicKey(other.getPublicKey());
            }

            if (!other.getD().isEmpty()) {
               this.setD(other.getD());
            }

            if (!other.getP().isEmpty()) {
               this.setP(other.getP());
            }

            if (!other.getQ().isEmpty()) {
               this.setQ(other.getQ());
            }

            if (!other.getDp().isEmpty()) {
               this.setDp(other.getDp());
            }

            if (!other.getDq().isEmpty()) {
               this.setDq(other.getDq());
            }

            if (!other.getCrt().isEmpty()) {
               this.setCrt(other.getCrt());
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

      public JwtRsaSsaPssPrivateKey.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        input.readMessage(this.internalGetPublicKeyFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        this.d_ = input.readBytes();
                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        this.p_ = input.readBytes();
                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        this.q_ = input.readBytes();
                        this.bitField0_ |= 16;
                        break;
                     case 50:
                        this.dp_ = input.readBytes();
                        this.bitField0_ |= 32;
                        break;
                     case 58:
                        this.dq_ = input.readBytes();
                        this.bitField0_ |= 64;
                        break;
                     case 66:
                        this.crt_ = input.readBytes();
                        this.bitField0_ |= 128;
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

      public JwtRsaSsaPssPrivateKey.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPssPrivateKey.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasPublicKey() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public JwtRsaSsaPssPublicKey getPublicKey() {
         if (this.publicKeyBuilder_ == null) {
            return this.publicKey_ == null ? JwtRsaSsaPssPublicKey.getDefaultInstance() : this.publicKey_;
         } else {
            return this.publicKeyBuilder_.getMessage();
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder setPublicKey(JwtRsaSsaPssPublicKey value) {
         if (this.publicKeyBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.publicKey_ = value;
         } else {
            this.publicKeyBuilder_.setMessage(value);
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPssPrivateKey.Builder setPublicKey(JwtRsaSsaPssPublicKey.Builder builderForValue) {
         if (this.publicKeyBuilder_ == null) {
            this.publicKey_ = builderForValue.build();
         } else {
            this.publicKeyBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPssPrivateKey.Builder mergePublicKey(JwtRsaSsaPssPublicKey value) {
         if (this.publicKeyBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0 && this.publicKey_ != null && this.publicKey_ != JwtRsaSsaPssPublicKey.getDefaultInstance()) {
               this.getPublicKeyBuilder().mergeFrom(value);
            } else {
               this.publicKey_ = value;
            }
         } else {
            this.publicKeyBuilder_.mergeFrom(value);
         }

         if (this.publicKey_ != null) {
            this.bitField0_ |= 2;
            this.onChanged();
         }

         return this;
      }

      public JwtRsaSsaPssPrivateKey.Builder clearPublicKey() {
         this.bitField0_ &= -3;
         this.publicKey_ = null;
         if (this.publicKeyBuilder_ != null) {
            this.publicKeyBuilder_.dispose();
            this.publicKeyBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public JwtRsaSsaPssPublicKey.Builder getPublicKeyBuilder() {
         this.bitField0_ |= 2;
         this.onChanged();
         return this.internalGetPublicKeyFieldBuilder().getBuilder();
      }

      @Override
      public JwtRsaSsaPssPublicKeyOrBuilder getPublicKeyOrBuilder() {
         if (this.publicKeyBuilder_ != null) {
            return this.publicKeyBuilder_.getMessageOrBuilder();
         } else {
            return this.publicKey_ == null ? JwtRsaSsaPssPublicKey.getDefaultInstance() : this.publicKey_;
         }
      }

      private SingleFieldBuilder<JwtRsaSsaPssPublicKey, JwtRsaSsaPssPublicKey.Builder, JwtRsaSsaPssPublicKeyOrBuilder> internalGetPublicKeyFieldBuilder() {
         if (this.publicKeyBuilder_ == null) {
            this.publicKeyBuilder_ = new SingleFieldBuilder<>(this.getPublicKey(), this.getParentForChildren(), this.isClean());
            this.publicKey_ = null;
         }

         return this.publicKeyBuilder_;
      }

      @Override
      public ByteString getD() {
         return this.d_;
      }

      public JwtRsaSsaPssPrivateKey.Builder setD(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.d_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clearD() {
         this.bitField0_ &= -5;
         this.d_ = JwtRsaSsaPssPrivateKey.getDefaultInstance().getD();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getP() {
         return this.p_;
      }

      public JwtRsaSsaPssPrivateKey.Builder setP(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.p_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clearP() {
         this.bitField0_ &= -9;
         this.p_ = JwtRsaSsaPssPrivateKey.getDefaultInstance().getP();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getQ() {
         return this.q_;
      }

      public JwtRsaSsaPssPrivateKey.Builder setQ(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.q_ = value;
            this.bitField0_ |= 16;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clearQ() {
         this.bitField0_ &= -17;
         this.q_ = JwtRsaSsaPssPrivateKey.getDefaultInstance().getQ();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getDp() {
         return this.dp_;
      }

      public JwtRsaSsaPssPrivateKey.Builder setDp(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.dp_ = value;
            this.bitField0_ |= 32;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clearDp() {
         this.bitField0_ &= -33;
         this.dp_ = JwtRsaSsaPssPrivateKey.getDefaultInstance().getDp();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getDq() {
         return this.dq_;
      }

      public JwtRsaSsaPssPrivateKey.Builder setDq(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.dq_ = value;
            this.bitField0_ |= 64;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clearDq() {
         this.bitField0_ &= -65;
         this.dq_ = JwtRsaSsaPssPrivateKey.getDefaultInstance().getDq();
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getCrt() {
         return this.crt_;
      }

      public JwtRsaSsaPssPrivateKey.Builder setCrt(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.crt_ = value;
            this.bitField0_ |= 128;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPssPrivateKey.Builder clearCrt() {
         this.bitField0_ &= -129;
         this.crt_ = JwtRsaSsaPssPrivateKey.getDefaultInstance().getCrt();
         this.onChanged();
         return this;
      }
   }
}
