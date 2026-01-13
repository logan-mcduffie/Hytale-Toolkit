package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FieldMask extends GeneratedMessage implements FieldMaskOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int PATHS_FIELD_NUMBER = 1;
   private LazyStringArrayList paths_ = LazyStringArrayList.emptyList();
   private byte memoizedIsInitialized = -1;
   private static final FieldMask DEFAULT_INSTANCE = new FieldMask();
   private static final Parser<FieldMask> PARSER = new AbstractParser<FieldMask>() {
      public FieldMask parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         FieldMask.Builder builder = FieldMask.newBuilder();

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

   private FieldMask(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private FieldMask() {
      this.paths_ = LazyStringArrayList.emptyList();
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable
         .ensureFieldAccessorsInitialized(FieldMask.class, FieldMask.Builder.class);
   }

   public ProtocolStringList getPathsList() {
      return this.paths_;
   }

   @Override
   public int getPathsCount() {
      return this.paths_.size();
   }

   @Override
   public String getPaths(int index) {
      return this.paths_.get(index);
   }

   @Override
   public ByteString getPathsBytes(int index) {
      return this.paths_.getByteString(index);
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
      for (int i = 0; i < this.paths_.size(); i++) {
         GeneratedMessage.writeString(output, 1, this.paths_.getRaw(i));
      }

      this.getUnknownFields().writeTo(output);
   }

   @Override
   public int getSerializedSize() {
      int size = this.memoizedSize;
      if (size != -1) {
         return size;
      } else {
         int var4 = 0;
         int dataSize = 0;

         for (int i = 0; i < this.paths_.size(); i++) {
            dataSize += computeStringSizeNoTag(this.paths_.getRaw(i));
         }

         var4 += dataSize;
         var4 += 1 * this.getPathsList().size();
         var4 += this.getUnknownFields().getSerializedSize();
         this.memoizedSize = var4;
         return var4;
      }
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof FieldMask)) {
         return super.equals(obj);
      } else {
         FieldMask other = (FieldMask)obj;
         return !this.getPathsList().equals(other.getPathsList()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int hash = 41;
         hash = 19 * hash + getDescriptor().hashCode();
         if (this.getPathsCount() > 0) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getPathsList().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static FieldMask parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static FieldMask parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static FieldMask parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static FieldMask parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static FieldMask parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static FieldMask parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static FieldMask parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static FieldMask parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static FieldMask parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static FieldMask parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static FieldMask parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static FieldMask parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public FieldMask.Builder newBuilderForType() {
      return newBuilder();
   }

   public static FieldMask.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static FieldMask.Builder newBuilder(FieldMask prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public FieldMask.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new FieldMask.Builder() : new FieldMask.Builder().mergeFrom(this);
   }

   protected FieldMask.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new FieldMask.Builder(parent);
   }

   public static FieldMask getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<FieldMask> parser() {
      return PARSER;
   }

   @Override
   public Parser<FieldMask> getParserForType() {
      return PARSER;
   }

   public FieldMask getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "FieldMask");
   }

   public static final class Builder extends GeneratedMessage.Builder<FieldMask.Builder> implements FieldMaskOrBuilder {
      private int bitField0_;
      private LazyStringArrayList paths_ = LazyStringArrayList.emptyList();

      public static final Descriptors.Descriptor getDescriptor() {
         return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable
            .ensureFieldAccessorsInitialized(FieldMask.class, FieldMask.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public FieldMask.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.paths_ = LazyStringArrayList.emptyList();
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
      }

      public FieldMask getDefaultInstanceForType() {
         return FieldMask.getDefaultInstance();
      }

      public FieldMask build() {
         FieldMask result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public FieldMask buildPartial() {
         FieldMask result = new FieldMask(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(FieldMask result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            this.paths_.makeImmutable();
            result.paths_ = this.paths_;
         }
      }

      public FieldMask.Builder mergeFrom(Message other) {
         if (other instanceof FieldMask) {
            return this.mergeFrom((FieldMask)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public FieldMask.Builder mergeFrom(FieldMask other) {
         if (other == FieldMask.getDefaultInstance()) {
            return this;
         } else {
            if (!other.paths_.isEmpty()) {
               if (this.paths_.isEmpty()) {
                  this.paths_ = other.paths_;
                  this.bitField0_ |= 1;
               } else {
                  this.ensurePathsIsMutable();
                  this.paths_.addAll(other.paths_);
               }

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

      public FieldMask.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        String s = input.readStringRequireUtf8();
                        this.ensurePathsIsMutable();
                        this.paths_.add(s);
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

      private void ensurePathsIsMutable() {
         if (!this.paths_.isModifiable()) {
            this.paths_ = new LazyStringArrayList(this.paths_);
         }

         this.bitField0_ |= 1;
      }

      public ProtocolStringList getPathsList() {
         this.paths_.makeImmutable();
         return this.paths_;
      }

      @Override
      public int getPathsCount() {
         return this.paths_.size();
      }

      @Override
      public String getPaths(int index) {
         return this.paths_.get(index);
      }

      @Override
      public ByteString getPathsBytes(int index) {
         return this.paths_.getByteString(index);
      }

      public FieldMask.Builder setPaths(int index, String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.ensurePathsIsMutable();
            this.paths_.set(index, value);
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public FieldMask.Builder addPaths(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.ensurePathsIsMutable();
            this.paths_.add(value);
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public FieldMask.Builder addAllPaths(Iterable<String> values) {
         this.ensurePathsIsMutable();
         AbstractMessageLite.Builder.addAll(values, this.paths_);
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public FieldMask.Builder clearPaths() {
         this.paths_ = LazyStringArrayList.emptyList();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public FieldMask.Builder addPathsBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.ensurePathsIsMutable();
            this.paths_.add(value);
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }
   }
}
