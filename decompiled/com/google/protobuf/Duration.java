package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Duration extends GeneratedMessage implements DurationOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int SECONDS_FIELD_NUMBER = 1;
   private long seconds_ = 0L;
   public static final int NANOS_FIELD_NUMBER = 2;
   private int nanos_ = 0;
   private byte memoizedIsInitialized = -1;
   private static final Duration DEFAULT_INSTANCE = new Duration();
   private static final Parser<Duration> PARSER = new AbstractParser<Duration>() {
      public Duration parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Duration.Builder builder = Duration.newBuilder();

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

   private Duration(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Duration() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return DurationProto.internal_static_google_protobuf_Duration_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable.ensureFieldAccessorsInitialized(Duration.class, Duration.Builder.class);
   }

   @Override
   public long getSeconds() {
      return this.seconds_;
   }

   @Override
   public int getNanos() {
      return this.nanos_;
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
      if (this.seconds_ != 0L) {
         output.writeInt64(1, this.seconds_);
      }

      if (this.nanos_ != 0) {
         output.writeInt32(2, this.nanos_);
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
         if (this.seconds_ != 0L) {
            size += CodedOutputStream.computeInt64Size(1, this.seconds_);
         }

         if (this.nanos_ != 0) {
            size += CodedOutputStream.computeInt32Size(2, this.nanos_);
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
      } else if (!(obj instanceof Duration)) {
         return super.equals(obj);
      } else {
         Duration other = (Duration)obj;
         if (this.getSeconds() != other.getSeconds()) {
            return false;
         } else {
            return this.getNanos() != other.getNanos() ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + Internal.hashLong(this.getSeconds());
         hash = 37 * hash + 2;
         hash = 53 * hash + this.getNanos();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Duration parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Duration parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Duration parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Duration parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Duration parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Duration parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Duration parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Duration parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Duration parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Duration parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Duration parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Duration parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Duration.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Duration.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Duration.Builder newBuilder(Duration prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Duration.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Duration.Builder() : new Duration.Builder().mergeFrom(this);
   }

   protected Duration.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Duration.Builder(parent);
   }

   public static Duration getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Duration> parser() {
      return PARSER;
   }

   @Override
   public Parser<Duration> getParserForType() {
      return PARSER;
   }

   public Duration getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Duration");
   }

   public static final class Builder extends GeneratedMessage.Builder<Duration.Builder> implements DurationOrBuilder {
      private int bitField0_;
      private long seconds_;
      private int nanos_;

      public static final Descriptors.Descriptor getDescriptor() {
         return DurationProto.internal_static_google_protobuf_Duration_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable
            .ensureFieldAccessorsInitialized(Duration.class, Duration.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public Duration.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.seconds_ = 0L;
         this.nanos_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return DurationProto.internal_static_google_protobuf_Duration_descriptor;
      }

      public Duration getDefaultInstanceForType() {
         return Duration.getDefaultInstance();
      }

      public Duration build() {
         Duration result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Duration buildPartial() {
         Duration result = new Duration(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(Duration result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.seconds_ = this.seconds_;
         }

         if ((from_bitField0_ & 2) != 0) {
            result.nanos_ = this.nanos_;
         }
      }

      public Duration.Builder mergeFrom(Message other) {
         if (other instanceof Duration) {
            return this.mergeFrom((Duration)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Duration.Builder mergeFrom(Duration other) {
         if (other == Duration.getDefaultInstance()) {
            return this;
         } else {
            if (other.getSeconds() != 0L) {
               this.setSeconds(other.getSeconds());
            }

            if (other.getNanos() != 0) {
               this.setNanos(other.getNanos());
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

      public Duration.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.seconds_ = input.readInt64();
                        this.bitField0_ |= 1;
                        break;
                     case 16:
                        this.nanos_ = input.readInt32();
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
      public long getSeconds() {
         return this.seconds_;
      }

      public Duration.Builder setSeconds(long value) {
         this.seconds_ = value;
         this.bitField0_ |= 1;
         this.onChanged();
         return this;
      }

      public Duration.Builder clearSeconds() {
         this.bitField0_ &= -2;
         this.seconds_ = 0L;
         this.onChanged();
         return this;
      }

      @Override
      public int getNanos() {
         return this.nanos_;
      }

      public Duration.Builder setNanos(int value) {
         this.nanos_ = value;
         this.bitField0_ |= 2;
         this.onChanged();
         return this;
      }

      public Duration.Builder clearNanos() {
         this.bitField0_ &= -3;
         this.nanos_ = 0;
         this.onChanged();
         return this;
      }
   }
}
