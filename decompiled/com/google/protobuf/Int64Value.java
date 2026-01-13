package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Int64Value extends GeneratedMessage implements Int64ValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private long value_ = 0L;
   private byte memoizedIsInitialized = -1;
   private static final Int64Value DEFAULT_INSTANCE = new Int64Value();
   private static final Parser<Int64Value> PARSER = new AbstractParser<Int64Value>() {
      public Int64Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Int64Value.Builder builder = Int64Value.newBuilder();

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

   private Int64Value(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Int64Value() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_Int64Value_fieldAccessorTable
         .ensureFieldAccessorsInitialized(Int64Value.class, Int64Value.Builder.class);
   }

   @Override
   public long getValue() {
      return this.value_;
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
      if (this.value_ != 0L) {
         output.writeInt64(1, this.value_);
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
         if (this.value_ != 0L) {
            size += CodedOutputStream.computeInt64Size(1, this.value_);
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
      } else if (!(obj instanceof Int64Value)) {
         return super.equals(obj);
      } else {
         Int64Value other = (Int64Value)obj;
         return this.getValue() != other.getValue() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + Internal.hashLong(this.getValue());
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Int64Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Int64Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Int64Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Int64Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Int64Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Int64Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Int64Value parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Int64Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Int64Value parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Int64Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Int64Value parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Int64Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Int64Value.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Int64Value.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Int64Value.Builder newBuilder(Int64Value prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Int64Value.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Int64Value.Builder() : new Int64Value.Builder().mergeFrom(this);
   }

   protected Int64Value.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Int64Value.Builder(parent);
   }

   public static Int64Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Int64Value of(long value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<Int64Value> parser() {
      return PARSER;
   }

   @Override
   public Parser<Int64Value> getParserForType() {
      return PARSER;
   }

   public Int64Value getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Int64Value");
   }

   public static final class Builder extends GeneratedMessage.Builder<Int64Value.Builder> implements Int64ValueOrBuilder {
      private int bitField0_;
      private long value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_Int64Value_fieldAccessorTable
            .ensureFieldAccessorsInitialized(Int64Value.class, Int64Value.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public Int64Value.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.value_ = 0L;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
      }

      public Int64Value getDefaultInstanceForType() {
         return Int64Value.getDefaultInstance();
      }

      public Int64Value build() {
         Int64Value result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Int64Value buildPartial() {
         Int64Value result = new Int64Value(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(Int64Value result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.value_ = this.value_;
         }
      }

      public Int64Value.Builder mergeFrom(Message other) {
         if (other instanceof Int64Value) {
            return this.mergeFrom((Int64Value)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Int64Value.Builder mergeFrom(Int64Value other) {
         if (other == Int64Value.getDefaultInstance()) {
            return this;
         } else {
            if (other.getValue() != 0L) {
               this.setValue(other.getValue());
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

      public Int64Value.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.value_ = input.readInt64();
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
      public long getValue() {
         return this.value_;
      }

      public Int64Value.Builder setValue(long value) {
         this.value_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public Int64Value.Builder clearValue() {
         this.bitField0_ &= -2;
         this.value_ = 0L;
         this.onChanged();
         return this;
      }
   }
}
