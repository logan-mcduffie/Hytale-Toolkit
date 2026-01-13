package com.google.crypto.tink.proto;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Deprecated
public final class KeyTypeEntry extends GeneratedMessage implements KeyTypeEntryOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int PRIMITIVE_NAME_FIELD_NUMBER = 1;
   private volatile Object primitiveName_ = "";
   public static final int TYPE_URL_FIELD_NUMBER = 2;
   private volatile Object typeUrl_ = "";
   public static final int KEY_MANAGER_VERSION_FIELD_NUMBER = 3;
   private int keyManagerVersion_ = 0;
   public static final int NEW_KEY_ALLOWED_FIELD_NUMBER = 4;
   private boolean newKeyAllowed_ = false;
   public static final int CATALOGUE_NAME_FIELD_NUMBER = 5;
   private volatile Object catalogueName_ = "";
   private byte memoizedIsInitialized = -1;
   private static final KeyTypeEntry DEFAULT_INSTANCE = new KeyTypeEntry();
   private static final Parser<KeyTypeEntry> PARSER = new AbstractParser<KeyTypeEntry>() {
      public KeyTypeEntry parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         KeyTypeEntry.Builder builder = KeyTypeEntry.newBuilder();

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

   private KeyTypeEntry(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private KeyTypeEntry() {
      this.primitiveName_ = "";
      this.typeUrl_ = "";
      this.catalogueName_ = "";
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Config.internal_static_google_crypto_tink_KeyTypeEntry_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Config.internal_static_google_crypto_tink_KeyTypeEntry_fieldAccessorTable
         .ensureFieldAccessorsInitialized(KeyTypeEntry.class, KeyTypeEntry.Builder.class);
   }

   @Override
   public String getPrimitiveName() {
      Object ref = this.primitiveName_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.primitiveName_ = s;
         return s;
      }
   }

   @Override
   public ByteString getPrimitiveNameBytes() {
      Object ref = this.primitiveName_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.primitiveName_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
   }

   @Override
   public String getTypeUrl() {
      Object ref = this.typeUrl_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.typeUrl_ = s;
         return s;
      }
   }

   @Override
   public ByteString getTypeUrlBytes() {
      Object ref = this.typeUrl_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.typeUrl_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
   }

   @Override
   public int getKeyManagerVersion() {
      return this.keyManagerVersion_;
   }

   @Override
   public boolean getNewKeyAllowed() {
      return this.newKeyAllowed_;
   }

   @Override
   public String getCatalogueName() {
      Object ref = this.catalogueName_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.catalogueName_ = s;
         return s;
      }
   }

   @Override
   public ByteString getCatalogueNameBytes() {
      Object ref = this.catalogueName_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.catalogueName_ = b;
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
      if (!GeneratedMessage.isStringEmpty(this.primitiveName_)) {
         GeneratedMessage.writeString(output, 1, this.primitiveName_);
      }

      if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
         GeneratedMessage.writeString(output, 2, this.typeUrl_);
      }

      if (this.keyManagerVersion_ != 0) {
         output.writeUInt32(3, this.keyManagerVersion_);
      }

      if (this.newKeyAllowed_) {
         output.writeBool(4, this.newKeyAllowed_);
      }

      if (!GeneratedMessage.isStringEmpty(this.catalogueName_)) {
         GeneratedMessage.writeString(output, 5, this.catalogueName_);
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
         if (!GeneratedMessage.isStringEmpty(this.primitiveName_)) {
            size += GeneratedMessage.computeStringSize(1, this.primitiveName_);
         }

         if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
            size += GeneratedMessage.computeStringSize(2, this.typeUrl_);
         }

         if (this.keyManagerVersion_ != 0) {
            size += CodedOutputStream.computeUInt32Size(3, this.keyManagerVersion_);
         }

         if (this.newKeyAllowed_) {
            size += CodedOutputStream.computeBoolSize(4, this.newKeyAllowed_);
         }

         if (!GeneratedMessage.isStringEmpty(this.catalogueName_)) {
            size += GeneratedMessage.computeStringSize(5, this.catalogueName_);
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
      } else if (!(obj instanceof KeyTypeEntry)) {
         return super.equals(obj);
      } else {
         KeyTypeEntry other = (KeyTypeEntry)obj;
         if (!this.getPrimitiveName().equals(other.getPrimitiveName())) {
            return false;
         } else if (!this.getTypeUrl().equals(other.getTypeUrl())) {
            return false;
         } else if (this.getKeyManagerVersion() != other.getKeyManagerVersion()) {
            return false;
         } else if (this.getNewKeyAllowed() != other.getNewKeyAllowed()) {
            return false;
         } else {
            return !this.getCatalogueName().equals(other.getCatalogueName()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getPrimitiveName().hashCode();
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getTypeUrl().hashCode();
         hash = 37 * hash + 3;
         hash = 53 * hash + this.getKeyManagerVersion();
         hash = 37 * hash + 4;
         hash = 53 * hash + Internal.hashBoolean(this.getNewKeyAllowed());
         hash = 37 * hash + 5;
         hash = 53 * hash + this.getCatalogueName().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static KeyTypeEntry parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeyTypeEntry parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeyTypeEntry parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeyTypeEntry parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeyTypeEntry parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeyTypeEntry parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeyTypeEntry parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static KeyTypeEntry parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static KeyTypeEntry parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static KeyTypeEntry parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static KeyTypeEntry parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static KeyTypeEntry parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public KeyTypeEntry.Builder newBuilderForType() {
      return newBuilder();
   }

   public static KeyTypeEntry.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static KeyTypeEntry.Builder newBuilder(KeyTypeEntry prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public KeyTypeEntry.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new KeyTypeEntry.Builder() : new KeyTypeEntry.Builder().mergeFrom(this);
   }

   protected KeyTypeEntry.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new KeyTypeEntry.Builder(parent);
   }

   public static KeyTypeEntry getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<KeyTypeEntry> parser() {
      return PARSER;
   }

   @Override
   public Parser<KeyTypeEntry> getParserForType() {
      return PARSER;
   }

   public KeyTypeEntry getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeyTypeEntry.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<KeyTypeEntry.Builder> implements KeyTypeEntryOrBuilder {
      private int bitField0_;
      private Object primitiveName_ = "";
      private Object typeUrl_ = "";
      private int keyManagerVersion_;
      private boolean newKeyAllowed_;
      private Object catalogueName_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return Config.internal_static_google_crypto_tink_KeyTypeEntry_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Config.internal_static_google_crypto_tink_KeyTypeEntry_fieldAccessorTable
            .ensureFieldAccessorsInitialized(KeyTypeEntry.class, KeyTypeEntry.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public KeyTypeEntry.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.primitiveName_ = "";
         this.typeUrl_ = "";
         this.keyManagerVersion_ = 0;
         this.newKeyAllowed_ = false;
         this.catalogueName_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Config.internal_static_google_crypto_tink_KeyTypeEntry_descriptor;
      }

      public KeyTypeEntry getDefaultInstanceForType() {
         return KeyTypeEntry.getDefaultInstance();
      }

      public KeyTypeEntry build() {
         KeyTypeEntry result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public KeyTypeEntry buildPartial() {
         KeyTypeEntry result = new KeyTypeEntry(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(KeyTypeEntry result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.primitiveName_ = this.primitiveName_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.typeUrl_ = this.typeUrl_;
         }

         if ((from_bitField0_ & 4) != 0) {
            result.keyManagerVersion_ = this.keyManagerVersion_;
         }

         if ((from_bitField0_ & 8) != 0) {
            result.newKeyAllowed_ = this.newKeyAllowed_;
         }

         if ((from_bitField0_ & 16) != 0) {
            result.catalogueName_ = this.catalogueName_;
         }
      }

      public KeyTypeEntry.Builder mergeFrom(Message other) {
         if (other instanceof KeyTypeEntry) {
            return this.mergeFrom((KeyTypeEntry)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public KeyTypeEntry.Builder mergeFrom(KeyTypeEntry other) {
         if (other == KeyTypeEntry.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getPrimitiveName().isEmpty()) {
               this.primitiveName_ = other.primitiveName_;
               this.bitField0_ |= 1;
               this.onChanged();
            }

            if (!other.getTypeUrl().isEmpty()) {
               this.typeUrl_ = other.typeUrl_;
               this.bitField0_ |= 2;
               this.onChanged();
            }

            if (other.getKeyManagerVersion() != 0) {
               this.setKeyManagerVersion(other.getKeyManagerVersion());
            }

            if (other.getNewKeyAllowed()) {
               this.setNewKeyAllowed(other.getNewKeyAllowed());
            }

            if (!other.getCatalogueName().isEmpty()) {
               this.catalogueName_ = other.catalogueName_;
               this.bitField0_ |= 16;
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

      public KeyTypeEntry.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.primitiveName_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        this.typeUrl_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 2;
                        break;
                     case 24:
                        this.keyManagerVersion_ = input.readUInt32();
                        this.bitField0_ |= 4;
                        break;
                     case 32:
                        this.newKeyAllowed_ = input.readBool();
                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        this.catalogueName_ = input.readStringRequireUtf8();
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
      public String getPrimitiveName() {
         Object ref = this.primitiveName_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.primitiveName_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getPrimitiveNameBytes() {
         Object ref = this.primitiveName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.primitiveName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public KeyTypeEntry.Builder setPrimitiveName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.primitiveName_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public KeyTypeEntry.Builder clearPrimitiveName() {
         this.primitiveName_ = KeyTypeEntry.getDefaultInstance().getPrimitiveName();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public KeyTypeEntry.Builder setPrimitiveNameBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            KeyTypeEntry.checkByteStringIsUtf8(value);
            this.primitiveName_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public String getTypeUrl() {
         Object ref = this.typeUrl_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.typeUrl_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getTypeUrlBytes() {
         Object ref = this.typeUrl_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.typeUrl_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public KeyTypeEntry.Builder setTypeUrl(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.typeUrl_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }
      }

      public KeyTypeEntry.Builder clearTypeUrl() {
         this.typeUrl_ = KeyTypeEntry.getDefaultInstance().getTypeUrl();
         this.bitField0_ &= -3;
         this.onChanged();
         return this;
      }

      public KeyTypeEntry.Builder setTypeUrlBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            KeyTypeEntry.checkByteStringIsUtf8(value);
            this.typeUrl_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }
      }

      @Override
      public int getKeyManagerVersion() {
         return this.keyManagerVersion_;
      }

      public KeyTypeEntry.Builder setKeyManagerVersion(int value) {
         this.keyManagerVersion_ = value;
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public KeyTypeEntry.Builder clearKeyManagerVersion() {
         this.bitField0_ &= -5;
         this.keyManagerVersion_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean getNewKeyAllowed() {
         return this.newKeyAllowed_;
      }

      public KeyTypeEntry.Builder setNewKeyAllowed(boolean value) {
         this.newKeyAllowed_ = value;
         this.bitField0_ |= 8;
         this.onChanged();
         return this;
      }

      public KeyTypeEntry.Builder clearNewKeyAllowed() {
         this.bitField0_ &= -9;
         this.newKeyAllowed_ = false;
         this.onChanged();
         return this;
      }

      @Override
      public String getCatalogueName() {
         Object ref = this.catalogueName_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.catalogueName_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getCatalogueNameBytes() {
         Object ref = this.catalogueName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.catalogueName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public KeyTypeEntry.Builder setCatalogueName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.catalogueName_ = value;
            this.bitField0_ |= 16;
            this.onChanged();
            return this;
         }
      }

      public KeyTypeEntry.Builder clearCatalogueName() {
         this.catalogueName_ = KeyTypeEntry.getDefaultInstance().getCatalogueName();
         this.bitField0_ &= -17;
         this.onChanged();
         return this;
      }

      public KeyTypeEntry.Builder setCatalogueNameBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            KeyTypeEntry.checkByteStringIsUtf8(value);
            this.catalogueName_ = value;
            this.bitField0_ |= 16;
            this.onChanged();
            return this;
         }
      }
   }
}
