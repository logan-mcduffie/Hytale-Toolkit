package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class DoubleValue extends GeneratedMessage implements DoubleValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private double value_ = 0.0;
   private byte memoizedIsInitialized = -1;
   private static final DoubleValue DEFAULT_INSTANCE = new DoubleValue();
   private static final Parser<DoubleValue> PARSER = new AbstractParser<DoubleValue>() {
      public DoubleValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         DoubleValue.Builder builder = DoubleValue.newBuilder();

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

   private DoubleValue(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private DoubleValue() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_DoubleValue_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_DoubleValue_fieldAccessorTable
         .ensureFieldAccessorsInitialized(DoubleValue.class, DoubleValue.Builder.class);
   }

   @Override
   public double getValue() {
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
      if (Double.doubleToRawLongBits(this.value_) != 0L) {
         output.writeDouble(1, this.value_);
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
         if (Double.doubleToRawLongBits(this.value_) != 0L) {
            size += CodedOutputStream.computeDoubleSize(1, this.value_);
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
      } else if (!(obj instanceof DoubleValue)) {
         return super.equals(obj);
      } else {
         DoubleValue other = (DoubleValue)obj;
         return Double.doubleToLongBits(this.getValue()) != Double.doubleToLongBits(other.getValue())
            ? false
            : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + Internal.hashLong(Double.doubleToLongBits(this.getValue()));
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static DoubleValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static DoubleValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static DoubleValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static DoubleValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static DoubleValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static DoubleValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static DoubleValue parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static DoubleValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static DoubleValue parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static DoubleValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static DoubleValue parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static DoubleValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public DoubleValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static DoubleValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static DoubleValue.Builder newBuilder(DoubleValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public DoubleValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new DoubleValue.Builder() : new DoubleValue.Builder().mergeFrom(this);
   }

   protected DoubleValue.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new DoubleValue.Builder(parent);
   }

   public static DoubleValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static DoubleValue of(double value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<DoubleValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<DoubleValue> getParserForType() {
      return PARSER;
   }

   public DoubleValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "DoubleValue");
   }

   public static final class Builder extends GeneratedMessage.Builder<DoubleValue.Builder> implements DoubleValueOrBuilder {
      private int bitField0_;
      private double value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_DoubleValue_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_DoubleValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(DoubleValue.class, DoubleValue.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public DoubleValue.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.value_ = 0.0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_DoubleValue_descriptor;
      }

      public DoubleValue getDefaultInstanceForType() {
         return DoubleValue.getDefaultInstance();
      }

      public DoubleValue build() {
         DoubleValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public DoubleValue buildPartial() {
         DoubleValue result = new DoubleValue(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(DoubleValue result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.value_ = this.value_;
         }
      }

      public DoubleValue.Builder mergeFrom(Message other) {
         if (other instanceof DoubleValue) {
            return this.mergeFrom((DoubleValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public DoubleValue.Builder mergeFrom(DoubleValue other) {
         if (other == DoubleValue.getDefaultInstance()) {
            return this;
         } else {
            if (Double.doubleToRawLongBits(other.getValue()) != 0L) {
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

      public DoubleValue.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 9:
                        this.value_ = input.readDouble();
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
      public double getValue() {
         return this.value_;
      }

      public DoubleValue.Builder setValue(double value) {
         this.value_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public DoubleValue.Builder clearValue() {
         this.bitField0_ &= -2;
         this.value_ = 0.0;
         this.onChanged();
         return this;
      }
   }
}
