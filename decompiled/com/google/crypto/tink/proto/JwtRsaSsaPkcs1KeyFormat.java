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

public final class JwtRsaSsaPkcs1KeyFormat extends GeneratedMessage implements JwtRsaSsaPkcs1KeyFormatOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int ALGORITHM_FIELD_NUMBER = 2;
   private int algorithm_ = 0;
   public static final int MODULUS_SIZE_IN_BITS_FIELD_NUMBER = 3;
   private int modulusSizeInBits_ = 0;
   public static final int PUBLIC_EXPONENT_FIELD_NUMBER = 4;
   private ByteString publicExponent_ = ByteString.EMPTY;
   private byte memoizedIsInitialized = -1;
   private static final JwtRsaSsaPkcs1KeyFormat DEFAULT_INSTANCE = new JwtRsaSsaPkcs1KeyFormat();
   private static final Parser<JwtRsaSsaPkcs1KeyFormat> PARSER = new AbstractParser<JwtRsaSsaPkcs1KeyFormat>() {
      public JwtRsaSsaPkcs1KeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         JwtRsaSsaPkcs1KeyFormat.Builder builder = JwtRsaSsaPkcs1KeyFormat.newBuilder();

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

   private JwtRsaSsaPkcs1KeyFormat(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private JwtRsaSsaPkcs1KeyFormat() {
      this.algorithm_ = 0;
      this.publicExponent_ = ByteString.EMPTY;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_fieldAccessorTable
         .ensureFieldAccessorsInitialized(JwtRsaSsaPkcs1KeyFormat.class, JwtRsaSsaPkcs1KeyFormat.Builder.class);
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
   public int getModulusSizeInBits() {
      return this.modulusSizeInBits_;
   }

   @Override
   public ByteString getPublicExponent() {
      return this.publicExponent_;
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

      if (this.modulusSizeInBits_ != 0) {
         output.writeUInt32(3, this.modulusSizeInBits_);
      }

      if (!this.publicExponent_.isEmpty()) {
         output.writeBytes(4, this.publicExponent_);
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

         if (this.modulusSizeInBits_ != 0) {
            size += CodedOutputStream.computeUInt32Size(3, this.modulusSizeInBits_);
         }

         if (!this.publicExponent_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.publicExponent_);
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
      } else if (!(obj instanceof JwtRsaSsaPkcs1KeyFormat)) {
         return super.equals(obj);
      } else {
         JwtRsaSsaPkcs1KeyFormat other = (JwtRsaSsaPkcs1KeyFormat)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (this.algorithm_ != other.algorithm_) {
            return false;
         } else if (this.getModulusSizeInBits() != other.getModulusSizeInBits()) {
            return false;
         } else {
            return !this.getPublicExponent().equals(other.getPublicExponent()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getModulusSizeInBits();
         hash = 37 * hash + 4;
         hash = 53 * hash + this.getPublicExponent().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtRsaSsaPkcs1KeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public JwtRsaSsaPkcs1KeyFormat.Builder newBuilderForType() {
      return newBuilder();
   }

   public static JwtRsaSsaPkcs1KeyFormat.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static JwtRsaSsaPkcs1KeyFormat.Builder newBuilder(JwtRsaSsaPkcs1KeyFormat prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public JwtRsaSsaPkcs1KeyFormat.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new JwtRsaSsaPkcs1KeyFormat.Builder() : new JwtRsaSsaPkcs1KeyFormat.Builder().mergeFrom(this);
   }

   protected JwtRsaSsaPkcs1KeyFormat.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new JwtRsaSsaPkcs1KeyFormat.Builder(parent);
   }

   public static JwtRsaSsaPkcs1KeyFormat getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<JwtRsaSsaPkcs1KeyFormat> parser() {
      return PARSER;
   }

   @Override
   public Parser<JwtRsaSsaPkcs1KeyFormat> getParserForType() {
      return PARSER;
   }

   public JwtRsaSsaPkcs1KeyFormat getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPkcs1KeyFormat.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<JwtRsaSsaPkcs1KeyFormat.Builder> implements JwtRsaSsaPkcs1KeyFormatOrBuilder {
      private int bitField0_;
      private int version_;
      private int algorithm_ = 0;
      private int modulusSizeInBits_;
      private ByteString publicExponent_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtRsaSsaPkcs1KeyFormat.class, JwtRsaSsaPkcs1KeyFormat.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.algorithm_ = 0;
         this.modulusSizeInBits_ = 0;
         this.publicExponent_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return JwtRsaSsaPkcs1.internal_static_google_crypto_tink_JwtRsaSsaPkcs1KeyFormat_descriptor;
      }

      public JwtRsaSsaPkcs1KeyFormat getDefaultInstanceForType() {
         return JwtRsaSsaPkcs1KeyFormat.getDefaultInstance();
      }

      public JwtRsaSsaPkcs1KeyFormat build() {
         JwtRsaSsaPkcs1KeyFormat result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public JwtRsaSsaPkcs1KeyFormat buildPartial() {
         JwtRsaSsaPkcs1KeyFormat result = new JwtRsaSsaPkcs1KeyFormat(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(JwtRsaSsaPkcs1KeyFormat result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.algorithm_ = this.algorithm_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.modulusSizeInBits_ = this.modulusSizeInBits_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.publicExponent_ = this.publicExponent_;
         }
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder mergeFrom(Message other) {
         if (other instanceof JwtRsaSsaPkcs1KeyFormat) {
            return this.mergeFrom((JwtRsaSsaPkcs1KeyFormat)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder mergeFrom(JwtRsaSsaPkcs1KeyFormat other) {
         if (other == JwtRsaSsaPkcs1KeyFormat.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.algorithm_ != 0) {
               this.setAlgorithmValue(other.getAlgorithmValue());
            }

            if (other.getModulusSizeInBits() != 0) {
               this.setModulusSizeInBits(other.getModulusSizeInBits());
            }

            if (!other.getPublicExponent().isEmpty()) {
               this.setPublicExponent(other.getPublicExponent());
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

      public JwtRsaSsaPkcs1KeyFormat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 24:
                        this.modulusSizeInBits_ = input.readUInt32();
                        this.bitField0_ |= 4;
                        break;
                     case 34:
                        this.publicExponent_ = input.readBytes();
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

      public JwtRsaSsaPkcs1KeyFormat.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getAlgorithmValue() {
         return this.algorithm_;
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder setAlgorithmValue(int value) {
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

      public JwtRsaSsaPkcs1KeyFormat.Builder setAlgorithm(JwtRsaSsaPkcs1Algorithm value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 2;
            this.algorithm_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder clearAlgorithm() {
         this.bitField0_ &= -3;
         this.algorithm_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getModulusSizeInBits() {
         return this.modulusSizeInBits_;
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder setModulusSizeInBits(int value) {
         this.modulusSizeInBits_ = value;
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder clearModulusSizeInBits() {
         this.bitField0_ &= -5;
         this.modulusSizeInBits_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public ByteString getPublicExponent() {
         return this.publicExponent_;
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder setPublicExponent(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.publicExponent_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }
      }

      public JwtRsaSsaPkcs1KeyFormat.Builder clearPublicExponent() {
         this.bitField0_ &= -9;
         this.publicExponent_ = JwtRsaSsaPkcs1KeyFormat.getDefaultInstance().getPublicExponent();
         this.onChanged();
         return this;
      }
   }
}
