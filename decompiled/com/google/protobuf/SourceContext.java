package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class SourceContext extends GeneratedMessage implements SourceContextOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int FILE_NAME_FIELD_NUMBER = 1;
   private volatile Object fileName_ = "";
   private byte memoizedIsInitialized = -1;
   private static final SourceContext DEFAULT_INSTANCE = new SourceContext();
   private static final Parser<SourceContext> PARSER = new AbstractParser<SourceContext>() {
      public SourceContext parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         SourceContext.Builder builder = SourceContext.newBuilder();

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

   private SourceContext(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private SourceContext() {
      this.fileName_ = "";
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable
         .ensureFieldAccessorsInitialized(SourceContext.class, SourceContext.Builder.class);
   }

   @Override
   public String getFileName() {
      Object ref = this.fileName_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.fileName_ = s;
         return s;
      }
   }

   @Override
   public ByteString getFileNameBytes() {
      Object ref = this.fileName_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.fileName_ = b;
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
      if (!GeneratedMessage.isStringEmpty(this.fileName_)) {
         GeneratedMessage.writeString(output, 1, this.fileName_);
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
         if (!GeneratedMessage.isStringEmpty(this.fileName_)) {
            size += GeneratedMessage.computeStringSize(1, this.fileName_);
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
      } else if (!(obj instanceof SourceContext)) {
         return super.equals(obj);
      } else {
         SourceContext other = (SourceContext)obj;
         return !this.getFileName().equals(other.getFileName()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getFileName().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static SourceContext parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static SourceContext parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static SourceContext parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static SourceContext parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static SourceContext parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static SourceContext parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static SourceContext parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static SourceContext parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static SourceContext parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static SourceContext parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static SourceContext parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static SourceContext parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public SourceContext.Builder newBuilderForType() {
      return newBuilder();
   }

   public static SourceContext.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static SourceContext.Builder newBuilder(SourceContext prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public SourceContext.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new SourceContext.Builder() : new SourceContext.Builder().mergeFrom(this);
   }

   protected SourceContext.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new SourceContext.Builder(parent);
   }

   public static SourceContext getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<SourceContext> parser() {
      return PARSER;
   }

   @Override
   public Parser<SourceContext> getParserForType() {
      return PARSER;
   }

   public SourceContext getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "SourceContext");
   }

   public static final class Builder extends GeneratedMessage.Builder<SourceContext.Builder> implements SourceContextOrBuilder {
      private int bitField0_;
      private Object fileName_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable
            .ensureFieldAccessorsInitialized(SourceContext.class, SourceContext.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public SourceContext.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.fileName_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
      }

      public SourceContext getDefaultInstanceForType() {
         return SourceContext.getDefaultInstance();
      }

      public SourceContext build() {
         SourceContext result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public SourceContext buildPartial() {
         SourceContext result = new SourceContext(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartial0(SourceContext result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.fileName_ = this.fileName_;
         }
      }

      public SourceContext.Builder mergeFrom(Message other) {
         if (other instanceof SourceContext) {
            return this.mergeFrom((SourceContext)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public SourceContext.Builder mergeFrom(SourceContext other) {
         if (other == SourceContext.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getFileName().isEmpty()) {
               this.fileName_ = other.fileName_;
               this.bitField0_ |= 1;
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

      public SourceContext.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.fileName_ = input.readStringRequireUtf8();
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
      public String getFileName() {
         Object ref = this.fileName_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.fileName_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getFileNameBytes() {
         Object ref = this.fileName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.fileName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public SourceContext.Builder setFileName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.fileName_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public SourceContext.Builder clearFileName() {
         this.fileName_ = SourceContext.getDefaultInstance().getFileName();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public SourceContext.Builder setFileNameBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.fileName_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }
   }
}
