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

public final class ChaCha20Poly1305KeyFormat extends GeneratedMessage implements ChaCha20Poly1305KeyFormatOrBuilder {
   private static final long serialVersionUID = 0L;
   private byte memoizedIsInitialized = -1;
   private static final ChaCha20Poly1305KeyFormat DEFAULT_INSTANCE = new ChaCha20Poly1305KeyFormat();
   private static final Parser<ChaCha20Poly1305KeyFormat> PARSER = new AbstractParser<ChaCha20Poly1305KeyFormat>() {
      public ChaCha20Poly1305KeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         ChaCha20Poly1305KeyFormat.Builder builder = ChaCha20Poly1305KeyFormat.newBuilder();

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

   private ChaCha20Poly1305KeyFormat(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private ChaCha20Poly1305KeyFormat() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Chacha20Poly1305.internal_static_google_crypto_tink_ChaCha20Poly1305KeyFormat_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Chacha20Poly1305.internal_static_google_crypto_tink_ChaCha20Poly1305KeyFormat_fieldAccessorTable
         .ensureFieldAccessorsInitialized(ChaCha20Poly1305KeyFormat.class, ChaCha20Poly1305KeyFormat.Builder.class);
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
      this.getUnknownFields().writeTo(output);
   }

   @Override
   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         int var2 = 0;
         var2 += this.getUnknownFields().getSerializedSize();
         this.memoizedSize = var2;
         return var2;
      }
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ChaCha20Poly1305KeyFormat)) {
         return super.equals(obj);
      } else {
         ChaCha20Poly1305KeyFormat other = (ChaCha20Poly1305KeyFormat)obj;
         return this.getUnknownFields().equals(other.getUnknownFields());
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int hash = 41;
         hash = 19 * hash + getDescriptor().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static ChaCha20Poly1305KeyFormat parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static ChaCha20Poly1305KeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static ChaCha20Poly1305KeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public ChaCha20Poly1305KeyFormat.Builder newBuilderForType() {
      return newBuilder();
   }

   public static ChaCha20Poly1305KeyFormat.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static ChaCha20Poly1305KeyFormat.Builder newBuilder(ChaCha20Poly1305KeyFormat prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public ChaCha20Poly1305KeyFormat.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new ChaCha20Poly1305KeyFormat.Builder() : new ChaCha20Poly1305KeyFormat.Builder().mergeFrom(this);
   }

   protected ChaCha20Poly1305KeyFormat.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new ChaCha20Poly1305KeyFormat.Builder(parent);
   }

   public static ChaCha20Poly1305KeyFormat getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<ChaCha20Poly1305KeyFormat> parser() {
      return PARSER;
   }

   @Override
   public Parser<ChaCha20Poly1305KeyFormat> getParserForType() {
      return PARSER;
   }

   public ChaCha20Poly1305KeyFormat getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", ChaCha20Poly1305KeyFormat.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<ChaCha20Poly1305KeyFormat.Builder> implements ChaCha20Poly1305KeyFormatOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
         return Chacha20Poly1305.internal_static_google_crypto_tink_ChaCha20Poly1305KeyFormat_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Chacha20Poly1305.internal_static_google_crypto_tink_ChaCha20Poly1305KeyFormat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(ChaCha20Poly1305KeyFormat.class, ChaCha20Poly1305KeyFormat.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public ChaCha20Poly1305KeyFormat.Builder clear() {
         super.clear();
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Chacha20Poly1305.internal_static_google_crypto_tink_ChaCha20Poly1305KeyFormat_descriptor;
      }

      public ChaCha20Poly1305KeyFormat getDefaultInstanceForType() {
         return ChaCha20Poly1305KeyFormat.getDefaultInstance();
      }

      public ChaCha20Poly1305KeyFormat build() {
         ChaCha20Poly1305KeyFormat result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public ChaCha20Poly1305KeyFormat buildPartial() {
         ChaCha20Poly1305KeyFormat result = new ChaCha20Poly1305KeyFormat(this);
         this.onBuilt();
         return result;
      }

      public ChaCha20Poly1305KeyFormat.Builder mergeFrom(Message other) {
         if (other instanceof ChaCha20Poly1305KeyFormat) {
            return this.mergeFrom((ChaCha20Poly1305KeyFormat)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public ChaCha20Poly1305KeyFormat.Builder mergeFrom(ChaCha20Poly1305KeyFormat other) {
         if (other == ChaCha20Poly1305KeyFormat.getDefaultInstance()) {
            return this;
         } else {
            this.mergeUnknownFields(other.getUnknownFields());
            this.onChanged();
            return this;
         }
      }

      @Override
      public final boolean isInitialized() {
         return true;
      }

      public ChaCha20Poly1305KeyFormat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
   }
}
