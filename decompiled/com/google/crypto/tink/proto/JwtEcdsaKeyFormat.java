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

public final class JwtEcdsaKeyFormat extends GeneratedMessage implements JwtEcdsaKeyFormatOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VERSION_FIELD_NUMBER = 1;
   private int version_ = 0;
   public static final int ALGORITHM_FIELD_NUMBER = 2;
   private int algorithm_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final JwtEcdsaKeyFormat DEFAULT_INSTANCE = new JwtEcdsaKeyFormat();
   private static final Parser<JwtEcdsaKeyFormat> PARSER = new AbstractParser<JwtEcdsaKeyFormat>() {
      public JwtEcdsaKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         JwtEcdsaKeyFormat.Builder builder = JwtEcdsaKeyFormat.newBuilder();

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

   private JwtEcdsaKeyFormat(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private JwtEcdsaKeyFormat() {
      this.algorithm_ = 0;
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaKeyFormat_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaKeyFormat_fieldAccessorTable
         .ensureFieldAccessorsInitialized(JwtEcdsaKeyFormat.class, JwtEcdsaKeyFormat.Builder.class);
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

         size += this.getUnknownFields().getSerializedSize();
         this.memoizedSize = size;
         return size;
      }
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof JwtEcdsaKeyFormat)) {
         return super.equals(obj);
      } else {
         JwtEcdsaKeyFormat other = (JwtEcdsaKeyFormat)obj;
         if (this.getVersion() != other.getVersion()) {
            return false;
         } else {
            return this.algorithm_ != other.algorithm_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static JwtEcdsaKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtEcdsaKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtEcdsaKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtEcdsaKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtEcdsaKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static JwtEcdsaKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static JwtEcdsaKeyFormat parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtEcdsaKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtEcdsaKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static JwtEcdsaKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static JwtEcdsaKeyFormat parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static JwtEcdsaKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public JwtEcdsaKeyFormat.Builder newBuilderForType() {
      return newBuilder();
   }

   public static JwtEcdsaKeyFormat.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static JwtEcdsaKeyFormat.Builder newBuilder(JwtEcdsaKeyFormat prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public JwtEcdsaKeyFormat.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new JwtEcdsaKeyFormat.Builder() : new JwtEcdsaKeyFormat.Builder().mergeFrom(this);
   }

   protected JwtEcdsaKeyFormat.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new JwtEcdsaKeyFormat.Builder(parent);
   }

   public static JwtEcdsaKeyFormat getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<JwtEcdsaKeyFormat> parser() {
      return PARSER;
   }

   @Override
   public Parser<JwtEcdsaKeyFormat> getParserForType() {
      return PARSER;
   }

   public JwtEcdsaKeyFormat getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtEcdsaKeyFormat.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<JwtEcdsaKeyFormat.Builder> implements JwtEcdsaKeyFormatOrBuilder {
      private int bitField0_;
      private int version_;
      private int algorithm_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaKeyFormat_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaKeyFormat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(JwtEcdsaKeyFormat.class, JwtEcdsaKeyFormat.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public JwtEcdsaKeyFormat.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.version_ = 0;
         this.algorithm_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return JwtEcdsa.internal_static_google_crypto_tink_JwtEcdsaKeyFormat_descriptor;
      }

      public JwtEcdsaKeyFormat getDefaultInstanceForType() {
         return JwtEcdsaKeyFormat.getDefaultInstance();
      }

      public JwtEcdsaKeyFormat build() {
         JwtEcdsaKeyFormat result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public JwtEcdsaKeyFormat buildPartial() {
         JwtEcdsaKeyFormat result = new JwtEcdsaKeyFormat(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(JwtEcdsaKeyFormat result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.version_ = this.version_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.algorithm_ = this.algorithm_;
         }
      }

      public JwtEcdsaKeyFormat.Builder mergeFrom(Message other) {
         if (other instanceof JwtEcdsaKeyFormat) {
            return this.mergeFrom((JwtEcdsaKeyFormat)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public JwtEcdsaKeyFormat.Builder mergeFrom(JwtEcdsaKeyFormat other) {
         if (other == JwtEcdsaKeyFormat.getDefaultInstance()) {
            return this;
         } else {
            if (other.getVersion() != 0) {
               this.setVersion(other.getVersion());
            }

            if (other.algorithm_ != 0) {
               this.setAlgorithmValue(other.getAlgorithmValue());
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

      public JwtEcdsaKeyFormat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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

      public JwtEcdsaKeyFormat.Builder setVersion(int value) {
         this.version_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public JwtEcdsaKeyFormat.Builder clearVersion() {
         this.bitField0_ &= -2;
         this.version_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getAlgorithmValue() {
         return this.algorithm_;
      }

      public JwtEcdsaKeyFormat.Builder setAlgorithmValue(int value) {
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

      public JwtEcdsaKeyFormat.Builder setAlgorithm(JwtEcdsaAlgorithm value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 2;
            this.algorithm_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public JwtEcdsaKeyFormat.Builder clearAlgorithm() {
         this.bitField0_ &= -3;
         this.algorithm_ = 0;
         this.onChanged();
         return this;
      }
   }
}
