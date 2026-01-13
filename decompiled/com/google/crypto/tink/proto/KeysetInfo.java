package com.google.crypto.tink.proto;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
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
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KeysetInfo extends GeneratedMessage implements KeysetInfoOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int PRIMARY_KEY_ID_FIELD_NUMBER = 1;
   private int primaryKeyId_ = 0;
   public static final int KEY_INFO_FIELD_NUMBER = 2;
   private List<KeysetInfo.KeyInfo> keyInfo_;
   private byte memoizedIsInitialized = -1;
   private static final KeysetInfo DEFAULT_INSTANCE = new KeysetInfo();
   private static final Parser<KeysetInfo> PARSER = new AbstractParser<KeysetInfo>() {
      public KeysetInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         KeysetInfo.Builder builder = KeysetInfo.newBuilder();

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

   private KeysetInfo(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private KeysetInfo() {
      this.keyInfo_ = Collections.emptyList();
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return Tink.internal_static_google_crypto_tink_KeysetInfo_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return Tink.internal_static_google_crypto_tink_KeysetInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(KeysetInfo.class, KeysetInfo.Builder.class);
   }

   @Override
   public int getPrimaryKeyId() {
      return this.primaryKeyId_;
   }

   @Override
   public List<KeysetInfo.KeyInfo> getKeyInfoList() {
      return this.keyInfo_;
   }

   @Override
   public List<? extends KeysetInfo.KeyInfoOrBuilder> getKeyInfoOrBuilderList() {
      return this.keyInfo_;
   }

   @Override
   public int getKeyInfoCount() {
      return this.keyInfo_.size();
   }

   @Override
   public KeysetInfo.KeyInfo getKeyInfo(int index) {
      return this.keyInfo_.get(index);
   }

   @Override
   public KeysetInfo.KeyInfoOrBuilder getKeyInfoOrBuilder(int index) {
      return this.keyInfo_.get(index);
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
      if (this.primaryKeyId_ != 0) {
         output.writeUInt32(1, this.primaryKeyId_);
      }

      for (int i = 0; i < this.keyInfo_.size(); i++) {
         output.writeMessage(2, this.keyInfo_.get(i));
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
         if (this.primaryKeyId_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.primaryKeyId_);
         }

         for (int i = 0; i < this.keyInfo_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(2, this.keyInfo_.get(i));
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
      } else if (!(obj instanceof KeysetInfo)) {
         return super.equals(obj);
      } else {
         KeysetInfo other = (KeysetInfo)obj;
         if (this.getPrimaryKeyId() != other.getPrimaryKeyId()) {
            return false;
         } else {
            return !this.getKeyInfoList().equals(other.getKeyInfoList()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getPrimaryKeyId();
         if (this.getKeyInfoCount() > 0) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getKeyInfoList().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static KeysetInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeysetInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeysetInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeysetInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeysetInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static KeysetInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static KeysetInfo parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static KeysetInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static KeysetInfo parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static KeysetInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static KeysetInfo parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static KeysetInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public KeysetInfo.Builder newBuilderForType() {
      return newBuilder();
   }

   public static KeysetInfo.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static KeysetInfo.Builder newBuilder(KeysetInfo prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public KeysetInfo.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new KeysetInfo.Builder() : new KeysetInfo.Builder().mergeFrom(this);
   }

   protected KeysetInfo.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new KeysetInfo.Builder(parent);
   }

   public static KeysetInfo getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<KeysetInfo> parser() {
      return PARSER;
   }

   @Override
   public Parser<KeysetInfo> getParserForType() {
      return PARSER;
   }

   public KeysetInfo getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeysetInfo.class.getName());
   }

   public static final class Builder extends GeneratedMessage.Builder<KeysetInfo.Builder> implements KeysetInfoOrBuilder {
      private int bitField0_;
      private int primaryKeyId_;
      private List<KeysetInfo.KeyInfo> keyInfo_ = Collections.emptyList();
      private RepeatedFieldBuilder<KeysetInfo.KeyInfo, KeysetInfo.KeyInfo.Builder, KeysetInfo.KeyInfoOrBuilder> keyInfoBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return Tink.internal_static_google_crypto_tink_KeysetInfo_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Tink.internal_static_google_crypto_tink_KeysetInfo_fieldAccessorTable
            .ensureFieldAccessorsInitialized(KeysetInfo.class, KeysetInfo.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public KeysetInfo.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.primaryKeyId_ = 0;
         if (this.keyInfoBuilder_ == null) {
            this.keyInfo_ = Collections.emptyList();
         } else {
            this.keyInfo_ = null;
            this.keyInfoBuilder_.clear();
         }

         this.bitField0_ &= -3;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return Tink.internal_static_google_crypto_tink_KeysetInfo_descriptor;
      }

      public KeysetInfo getDefaultInstanceForType() {
         return KeysetInfo.getDefaultInstance();
      }

      public KeysetInfo build() {
         KeysetInfo result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public KeysetInfo buildPartial() {
         KeysetInfo result = new KeysetInfo(this);
         this.buildPartialRepeatedFields(result);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartialRepeatedFields(KeysetInfo result) {
         if (this.keyInfoBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0) {
               this.keyInfo_ = Collections.unmodifiableList(this.keyInfo_);
               this.bitField0_ &= -3;
            }

            result.keyInfo_ = this.keyInfo_;
         } else {
            result.keyInfo_ = this.keyInfoBuilder_.build();
         }
      }

      private void buildPartial0(KeysetInfo result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.primaryKeyId_ = this.primaryKeyId_;
         }
      }

      public KeysetInfo.Builder mergeFrom(Message other) {
         if (other instanceof KeysetInfo) {
            return this.mergeFrom((KeysetInfo)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public KeysetInfo.Builder mergeFrom(KeysetInfo other) {
         if (other == KeysetInfo.getDefaultInstance()) {
            return this;
         } else {
            if (other.getPrimaryKeyId() != 0) {
               this.setPrimaryKeyId(other.getPrimaryKeyId());
            }

            if (this.keyInfoBuilder_ == null) {
               if (!other.keyInfo_.isEmpty()) {
                  if (this.keyInfo_.isEmpty()) {
                     this.keyInfo_ = other.keyInfo_;
                     this.bitField0_ &= -3;
                  } else {
                     this.ensureKeyInfoIsMutable();
                     this.keyInfo_.addAll(other.keyInfo_);
                  }

                  this.onChanged();
               }
            } else if (!other.keyInfo_.isEmpty()) {
               if (this.keyInfoBuilder_.isEmpty()) {
                  this.keyInfoBuilder_.dispose();
                  this.keyInfoBuilder_ = null;
                  this.keyInfo_ = other.keyInfo_;
                  this.bitField0_ &= -3;
                  this.keyInfoBuilder_ = KeysetInfo.alwaysUseFieldBuilders ? this.internalGetKeyInfoFieldBuilder() : null;
               } else {
                  this.keyInfoBuilder_.addAllMessages(other.keyInfo_);
               }
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

      public KeysetInfo.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.primaryKeyId_ = input.readUInt32();
                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        KeysetInfo.KeyInfo m = input.readMessage(KeysetInfo.KeyInfo.parser(), extensionRegistry);
                        if (this.keyInfoBuilder_ == null) {
                           this.ensureKeyInfoIsMutable();
                           this.keyInfo_.add(m);
                        } else {
                           this.keyInfoBuilder_.addMessage(m);
                        }
                        break;
                     default:
                        if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var9) {
               throw var9.unwrapIOException();
            } finally {
               this.onChanged();
            }

            return this;
         }
      }

      @Override
      public int getPrimaryKeyId() {
         return this.primaryKeyId_;
      }

      public KeysetInfo.Builder setPrimaryKeyId(int value) {
         this.primaryKeyId_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public KeysetInfo.Builder clearPrimaryKeyId() {
         this.bitField0_ &= -2;
         this.primaryKeyId_ = 0;
         this.onChanged();
         return this;
      }

      private void ensureKeyInfoIsMutable() {
         if ((this.bitField0_ & 2) == 0) {
            this.keyInfo_ = new ArrayList<>(this.keyInfo_);
            this.bitField0_ |= 2;
         }
      }

      @Override
      public List<KeysetInfo.KeyInfo> getKeyInfoList() {
         return this.keyInfoBuilder_ == null ? Collections.unmodifiableList(this.keyInfo_) : this.keyInfoBuilder_.getMessageList();
      }

      @Override
      public int getKeyInfoCount() {
         return this.keyInfoBuilder_ == null ? this.keyInfo_.size() : this.keyInfoBuilder_.getCount();
      }

      @Override
      public KeysetInfo.KeyInfo getKeyInfo(int index) {
         return this.keyInfoBuilder_ == null ? this.keyInfo_.get(index) : this.keyInfoBuilder_.getMessage(index);
      }

      public KeysetInfo.Builder setKeyInfo(int index, KeysetInfo.KeyInfo value) {
         if (this.keyInfoBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureKeyInfoIsMutable();
            this.keyInfo_.set(index, value);
            this.onChanged();
         } else {
            this.keyInfoBuilder_.setMessage(index, value);
         }

         return this;
      }

      public KeysetInfo.Builder setKeyInfo(int index, KeysetInfo.KeyInfo.Builder builderForValue) {
         if (this.keyInfoBuilder_ == null) {
            this.ensureKeyInfoIsMutable();
            this.keyInfo_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.keyInfoBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public KeysetInfo.Builder addKeyInfo(KeysetInfo.KeyInfo value) {
         if (this.keyInfoBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureKeyInfoIsMutable();
            this.keyInfo_.add(value);
            this.onChanged();
         } else {
            this.keyInfoBuilder_.addMessage(value);
         }

         return this;
      }

      public KeysetInfo.Builder addKeyInfo(int index, KeysetInfo.KeyInfo value) {
         if (this.keyInfoBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureKeyInfoIsMutable();
            this.keyInfo_.add(index, value);
            this.onChanged();
         } else {
            this.keyInfoBuilder_.addMessage(index, value);
         }

         return this;
      }

      public KeysetInfo.Builder addKeyInfo(KeysetInfo.KeyInfo.Builder builderForValue) {
         if (this.keyInfoBuilder_ == null) {
            this.ensureKeyInfoIsMutable();
            this.keyInfo_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.keyInfoBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public KeysetInfo.Builder addKeyInfo(int index, KeysetInfo.KeyInfo.Builder builderForValue) {
         if (this.keyInfoBuilder_ == null) {
            this.ensureKeyInfoIsMutable();
            this.keyInfo_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.keyInfoBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public KeysetInfo.Builder addAllKeyInfo(Iterable<? extends KeysetInfo.KeyInfo> values) {
         if (this.keyInfoBuilder_ == null) {
            this.ensureKeyInfoIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.keyInfo_);
            this.onChanged();
         } else {
            this.keyInfoBuilder_.addAllMessages(values);
         }

         return this;
      }

      public KeysetInfo.Builder clearKeyInfo() {
         if (this.keyInfoBuilder_ == null) {
            this.keyInfo_ = Collections.emptyList();
            this.bitField0_ &= -3;
            this.onChanged();
         } else {
            this.keyInfoBuilder_.clear();
         }

         return this;
      }

      public KeysetInfo.Builder removeKeyInfo(int index) {
         if (this.keyInfoBuilder_ == null) {
            this.ensureKeyInfoIsMutable();
            this.keyInfo_.remove(index);
            this.onChanged();
         } else {
            this.keyInfoBuilder_.remove(index);
         }

         return this;
      }

      public KeysetInfo.KeyInfo.Builder getKeyInfoBuilder(int index) {
         return this.internalGetKeyInfoFieldBuilder().getBuilder(index);
      }

      @Override
      public KeysetInfo.KeyInfoOrBuilder getKeyInfoOrBuilder(int index) {
         return this.keyInfoBuilder_ == null ? this.keyInfo_.get(index) : this.keyInfoBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends KeysetInfo.KeyInfoOrBuilder> getKeyInfoOrBuilderList() {
         return this.keyInfoBuilder_ != null ? this.keyInfoBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.keyInfo_);
      }

      public KeysetInfo.KeyInfo.Builder addKeyInfoBuilder() {
         return this.internalGetKeyInfoFieldBuilder().addBuilder(KeysetInfo.KeyInfo.getDefaultInstance());
      }

      public KeysetInfo.KeyInfo.Builder addKeyInfoBuilder(int index) {
         return this.internalGetKeyInfoFieldBuilder().addBuilder(index, KeysetInfo.KeyInfo.getDefaultInstance());
      }

      public List<KeysetInfo.KeyInfo.Builder> getKeyInfoBuilderList() {
         return this.internalGetKeyInfoFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<KeysetInfo.KeyInfo, KeysetInfo.KeyInfo.Builder, KeysetInfo.KeyInfoOrBuilder> internalGetKeyInfoFieldBuilder() {
         if (this.keyInfoBuilder_ == null) {
            this.keyInfoBuilder_ = new RepeatedFieldBuilder<>(this.keyInfo_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
            this.keyInfo_ = null;
         }

         return this.keyInfoBuilder_;
      }
   }

   public static final class KeyInfo extends GeneratedMessage implements KeysetInfo.KeyInfoOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int TYPE_URL_FIELD_NUMBER = 1;
      private volatile Object typeUrl_ = "";
      public static final int STATUS_FIELD_NUMBER = 2;
      private int status_ = 0;
      public static final int KEY_ID_FIELD_NUMBER = 3;
      private int keyId_ = 0;
      public static final int OUTPUT_PREFIX_TYPE_FIELD_NUMBER = 4;
      private int outputPrefixType_ = 0;
      private byte memoizedIsInitialized = -1;
      private static final KeysetInfo.KeyInfo DEFAULT_INSTANCE = new KeysetInfo.KeyInfo();
      private static final Parser<KeysetInfo.KeyInfo> PARSER = new AbstractParser<KeysetInfo.KeyInfo>() {
         public KeysetInfo.KeyInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            KeysetInfo.KeyInfo.Builder builder = KeysetInfo.KeyInfo.newBuilder();

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

      private KeyInfo(GeneratedMessage.Builder<?> builder) {
         super(builder);
      }

      private KeyInfo() {
         this.typeUrl_ = "";
         this.status_ = 0;
         this.outputPrefixType_ = 0;
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_fieldAccessorTable
            .ensureFieldAccessorsInitialized(KeysetInfo.KeyInfo.class, KeysetInfo.KeyInfo.Builder.class);
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
      public int getStatusValue() {
         return this.status_;
      }

      @Override
      public KeyStatusType getStatus() {
         KeyStatusType result = KeyStatusType.forNumber(this.status_);
         return result == null ? KeyStatusType.UNRECOGNIZED : result;
      }

      @Override
      public int getKeyId() {
         return this.keyId_;
      }

      @Override
      public int getOutputPrefixTypeValue() {
         return this.outputPrefixType_;
      }

      @Override
      public OutputPrefixType getOutputPrefixType() {
         OutputPrefixType result = OutputPrefixType.forNumber(this.outputPrefixType_);
         return result == null ? OutputPrefixType.UNRECOGNIZED : result;
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
         if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
            GeneratedMessage.writeString(output, 1, this.typeUrl_);
         }

         if (this.status_ != KeyStatusType.UNKNOWN_STATUS.getNumber()) {
            output.writeEnum(2, this.status_);
         }

         if (this.keyId_ != 0) {
            output.writeUInt32(3, this.keyId_);
         }

         if (this.outputPrefixType_ != OutputPrefixType.UNKNOWN_PREFIX.getNumber()) {
            output.writeEnum(4, this.outputPrefixType_);
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
            if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
               size += GeneratedMessage.computeStringSize(1, this.typeUrl_);
            }

            if (this.status_ != KeyStatusType.UNKNOWN_STATUS.getNumber()) {
               size += CodedOutputStream.computeEnumSize(2, this.status_);
            }

            if (this.keyId_ != 0) {
               size += CodedOutputStream.computeUInt32Size(3, this.keyId_);
            }

            if (this.outputPrefixType_ != OutputPrefixType.UNKNOWN_PREFIX.getNumber()) {
               size += CodedOutputStream.computeEnumSize(4, this.outputPrefixType_);
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
         } else if (!(obj instanceof KeysetInfo.KeyInfo)) {
            return super.equals(obj);
         } else {
            KeysetInfo.KeyInfo other = (KeysetInfo.KeyInfo)obj;
            if (!this.getTypeUrl().equals(other.getTypeUrl())) {
               return false;
            } else if (this.status_ != other.status_) {
               return false;
            } else if (this.getKeyId() != other.getKeyId()) {
               return false;
            } else {
               return this.outputPrefixType_ != other.outputPrefixType_ ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
            hash = 53 * hash + this.getTypeUrl().hashCode();
            hash = 37 * hash + 2;
            hash = 53 * hash + this.status_;
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getKeyId();
            hash = 37 * hash + 4;
            hash = 53 * hash + this.outputPrefixType_;
            hash = 29 * hash + this.getUnknownFields().hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static KeysetInfo.KeyInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static KeysetInfo.KeyInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static KeysetInfo.KeyInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static KeysetInfo.KeyInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static KeysetInfo.KeyInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static KeysetInfo.KeyInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static KeysetInfo.KeyInfo parseFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static KeysetInfo.KeyInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static KeysetInfo.KeyInfo parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
      }

      public static KeysetInfo.KeyInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static KeysetInfo.KeyInfo parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input);
      }

      public static KeysetInfo.KeyInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public KeysetInfo.KeyInfo.Builder newBuilderForType() {
         return newBuilder();
      }

      public static KeysetInfo.KeyInfo.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static KeysetInfo.KeyInfo.Builder newBuilder(KeysetInfo.KeyInfo prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public KeysetInfo.KeyInfo.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new KeysetInfo.KeyInfo.Builder() : new KeysetInfo.KeyInfo.Builder().mergeFrom(this);
      }

      protected KeysetInfo.KeyInfo.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
         return new KeysetInfo.KeyInfo.Builder(parent);
      }

      public static KeysetInfo.KeyInfo getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<KeysetInfo.KeyInfo> parser() {
         return PARSER;
      }

      @Override
      public Parser<KeysetInfo.KeyInfo> getParserForType() {
         return PARSER;
      }

      public KeysetInfo.KeyInfo getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      static {
         RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeysetInfo.KeyInfo.class.getName());
      }

      public static final class Builder extends GeneratedMessage.Builder<KeysetInfo.KeyInfo.Builder> implements KeysetInfo.KeyInfoOrBuilder {
         private int bitField0_;
         private Object typeUrl_ = "";
         private int status_ = 0;
         private int keyId_;
         private int outputPrefixType_ = 0;

         public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor;
         }

         @Override
         protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_fieldAccessorTable
               .ensureFieldAccessorsInitialized(KeysetInfo.KeyInfo.class, KeysetInfo.KeyInfo.Builder.class);
         }

         private Builder() {
         }

         private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
         }

         public KeysetInfo.KeyInfo.Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.typeUrl_ = "";
            this.status_ = 0;
            this.keyId_ = 0;
            this.outputPrefixType_ = 0;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor;
         }

         public KeysetInfo.KeyInfo getDefaultInstanceForType() {
            return KeysetInfo.KeyInfo.getDefaultInstance();
         }

         public KeysetInfo.KeyInfo build() {
            KeysetInfo.KeyInfo result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public KeysetInfo.KeyInfo buildPartial() {
            KeysetInfo.KeyInfo result = new KeysetInfo.KeyInfo(this);
            if (this.bitField0_ != 0) {
               this.buildPartial0(result);
            }

            this.onBuilt();
            return result;
         }

         private void buildPartial0(KeysetInfo.KeyInfo result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
               result.typeUrl_ = this.typeUrl_;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.status_ = this.status_;
            }

            if ((from_bitField0_ & 4) != 0) {
               result.keyId_ = this.keyId_;
            }

            if ((from_bitField0_ & 8) != 0) {
               result.outputPrefixType_ = this.outputPrefixType_;
            }
         }

         public KeysetInfo.KeyInfo.Builder mergeFrom(Message other) {
            if (other instanceof KeysetInfo.KeyInfo) {
               return this.mergeFrom((KeysetInfo.KeyInfo)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public KeysetInfo.KeyInfo.Builder mergeFrom(KeysetInfo.KeyInfo other) {
            if (other == KeysetInfo.KeyInfo.getDefaultInstance()) {
               return this;
            } else {
               if (!other.getTypeUrl().isEmpty()) {
                  this.typeUrl_ = other.typeUrl_;
                  this.bitField0_ |= 1;
                  this.onChanged();
               }

               if (other.status_ != 0) {
                  this.setStatusValue(other.getStatusValue());
               }

               if (other.getKeyId() != 0) {
                  this.setKeyId(other.getKeyId());
               }

               if (other.outputPrefixType_ != 0) {
                  this.setOutputPrefixTypeValue(other.getOutputPrefixTypeValue());
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

         public KeysetInfo.KeyInfo.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                           this.typeUrl_ = input.readStringRequireUtf8();
                           this.bitField0_ |= 1;
                           break;
                        case 16:
                           this.status_ = input.readEnum();
                           this.bitField0_ |= 2;
                           break;
                        case 24:
                           this.keyId_ = input.readUInt32();
                           this.bitField0_ |= 4;
                           break;
                        case 32:
                           this.outputPrefixType_ = input.readEnum();
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

         public KeysetInfo.KeyInfo.Builder setTypeUrl(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.typeUrl_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         public KeysetInfo.KeyInfo.Builder clearTypeUrl() {
            this.typeUrl_ = KeysetInfo.KeyInfo.getDefaultInstance().getTypeUrl();
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public KeysetInfo.KeyInfo.Builder setTypeUrlBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               KeysetInfo.KeyInfo.checkByteStringIsUtf8(value);
               this.typeUrl_ = value;
               this.bitField0_ |= 1;
               this.onChanged();
               return this;
            }
         }

         @Override
         public int getStatusValue() {
            return this.status_;
         }

         public KeysetInfo.KeyInfo.Builder setStatusValue(int value) {
            this.status_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
         }

         @Override
         public KeyStatusType getStatus() {
            KeyStatusType result = KeyStatusType.forNumber(this.status_);
            return result == null ? KeyStatusType.UNRECOGNIZED : result;
         }

         public KeysetInfo.KeyInfo.Builder setStatus(KeyStatusType value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.status_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public KeysetInfo.KeyInfo.Builder clearStatus() {
            this.bitField0_ &= -3;
            this.status_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public int getKeyId() {
            return this.keyId_;
         }

         public KeysetInfo.KeyInfo.Builder setKeyId(int value) {
            this.keyId_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }

         public KeysetInfo.KeyInfo.Builder clearKeyId() {
            this.bitField0_ &= -5;
            this.keyId_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public int getOutputPrefixTypeValue() {
            return this.outputPrefixType_;
         }

         public KeysetInfo.KeyInfo.Builder setOutputPrefixTypeValue(int value) {
            this.outputPrefixType_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
         }

         @Override
         public OutputPrefixType getOutputPrefixType() {
            OutputPrefixType result = OutputPrefixType.forNumber(this.outputPrefixType_);
            return result == null ? OutputPrefixType.UNRECOGNIZED : result;
         }

         public KeysetInfo.KeyInfo.Builder setOutputPrefixType(OutputPrefixType value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.outputPrefixType_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public KeysetInfo.KeyInfo.Builder clearOutputPrefixType() {
            this.bitField0_ &= -9;
            this.outputPrefixType_ = 0;
            this.onChanged();
            return this;
         }
      }
   }

   public interface KeyInfoOrBuilder extends MessageOrBuilder {
      String getTypeUrl();

      ByteString getTypeUrlBytes();

      int getStatusValue();

      KeyStatusType getStatus();

      int getKeyId();

      int getOutputPrefixTypeValue();

      OutputPrefixType getOutputPrefixType();
   }
}
