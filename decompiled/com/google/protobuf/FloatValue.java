package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FloatValue extends GeneratedMessage implements FloatValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private float value_ = 0.0F;
   private byte memoizedIsInitialized = -1;
   private static final FloatValue DEFAULT_INSTANCE = new FloatValue();
   private static final Parser<FloatValue> PARSER = new AbstractParser<FloatValue>() {
      public FloatValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         FloatValue.Builder builder = FloatValue.newBuilder();

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

   private FloatValue(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private FloatValue() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable
         .ensureFieldAccessorsInitialized(FloatValue.class, FloatValue.Builder.class);
   }

   @Override
   public float getValue() {
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
      if (Float.floatToRawIntBits(this.value_) != 0) {
         output.writeFloat(1, this.value_);
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
         if (Float.floatToRawIntBits(this.value_) != 0) {
            size += CodedOutputStream.computeFloatSize(1, this.value_);
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
      } else if (!(obj instanceof FloatValue)) {
         return super.equals(obj);
      } else {
         FloatValue other = (FloatValue)obj;
         return Float.floatToIntBits(this.getValue()) != Float.floatToIntBits(other.getValue())
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
         hash = 53 * hash + Float.floatToIntBits(this.getValue());
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static FloatValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static FloatValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static FloatValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static FloatValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static FloatValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static FloatValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static FloatValue parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static FloatValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static FloatValue parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static FloatValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static FloatValue parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static FloatValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public FloatValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static FloatValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static FloatValue.Builder newBuilder(FloatValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public FloatValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new FloatValue.Builder() : new FloatValue.Builder().mergeFrom(this);
   }

   protected FloatValue.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new FloatValue.Builder(parent);
   }

   public static FloatValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static FloatValue of(float value) {
      return newBuilder().setValue(value).build();
   }

   public static Parser<FloatValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<FloatValue> getParserForType() {
      return PARSER;
   }

   public FloatValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "FloatValue");
   }

   public static final class Builder extends GeneratedMessage.Builder<FloatValue.Builder> implements FloatValueOrBuilder {
      private int bitField0_;
      private float value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(FloatValue.class, FloatValue.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public FloatValue.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.value_ = 0.0F;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
      }

      public FloatValue getDefaultInstanceForType() {
         return FloatValue.getDefaultInstance();
      }

      public FloatValue build() {
         FloatValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public FloatValue buildPartial() {
         FloatValue result = new FloatValue(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(FloatValue result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.value_ = this.value_;
         }
      }

      public FloatValue.Builder mergeFrom(Message other) {
         if (other instanceof FloatValue) {
            return this.mergeFrom((FloatValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public FloatValue.Builder mergeFrom(FloatValue other) {
         if (other == FloatValue.getDefaultInstance()) {
            return this;
         } else {
            if (Float.floatToRawIntBits(other.getValue()) != 0) {
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

      public FloatValue.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                     case 13:
                        this.value_ = input.readFloat();
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
      public float getValue() {
         return this.value_;
      }

      public FloatValue.Builder setValue(float value) {
         this.value_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public FloatValue.Builder clearValue() {
         this.bitField0_ &= -2;
         this.value_ = 0.0F;
         this.onChanged();
         return this;
      }
   }
}
