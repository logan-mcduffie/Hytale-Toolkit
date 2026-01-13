package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListValue extends GeneratedMessage implements ListValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUES_FIELD_NUMBER = 1;
   private List<Value> values_;
   private byte memoizedIsInitialized = -1;
   private static final ListValue DEFAULT_INSTANCE = new ListValue();
   private static final Parser<ListValue> PARSER = new AbstractParser<ListValue>() {
      public ListValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         ListValue.Builder builder = ListValue.newBuilder();

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

   private ListValue(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private ListValue() {
      this.values_ = Collections.emptyList();
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return StructProto.internal_static_google_protobuf_ListValue_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable.ensureFieldAccessorsInitialized(ListValue.class, ListValue.Builder.class);
   }

   @Override
   public List<Value> getValuesList() {
      return this.values_;
   }

   @Override
   public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
      return this.values_;
   }

   @Override
   public int getValuesCount() {
      return this.values_.size();
   }

   @Override
   public Value getValues(int index) {
      return this.values_.get(index);
   }

   @Override
   public ValueOrBuilder getValuesOrBuilder(int index) {
      return this.values_.get(index);
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
      for (int i = 0; i < this.values_.size(); i++) {
         output.writeMessage(1, this.values_.get(i));
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

         for (int i = 0; i < this.values_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(1, this.values_.get(i));
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
      } else if (!(obj instanceof ListValue)) {
         return super.equals(obj);
      } else {
         ListValue other = (ListValue)obj;
         return !this.getValuesList().equals(other.getValuesList()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int hash = 41;
         hash = 19 * hash + getDescriptor().hashCode();
         if (this.getValuesCount() > 0) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getValuesList().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static ListValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static ListValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static ListValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static ListValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static ListValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static ListValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static ListValue parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static ListValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static ListValue parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static ListValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static ListValue parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static ListValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public ListValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static ListValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static ListValue.Builder newBuilder(ListValue prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public ListValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new ListValue.Builder() : new ListValue.Builder().mergeFrom(this);
   }

   protected ListValue.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new ListValue.Builder(parent);
   }

   public static ListValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<ListValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<ListValue> getParserForType() {
      return PARSER;
   }

   public ListValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "ListValue");
   }

   public static final class Builder extends GeneratedMessage.Builder<ListValue.Builder> implements ListValueOrBuilder {
      private int bitField0_;
      private List<Value> values_ = Collections.emptyList();
      private RepeatedFieldBuilder<Value, Value.Builder, ValueOrBuilder> valuesBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_ListValue_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(ListValue.class, ListValue.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public ListValue.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         if (this.valuesBuilder_ == null) {
            this.values_ = Collections.emptyList();
         } else {
            this.values_ = null;
            this.valuesBuilder_.clear();
         }

         this.bitField0_ &= -2;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_ListValue_descriptor;
      }

      public ListValue getDefaultInstanceForType() {
         return ListValue.getDefaultInstance();
      }

      public ListValue build() {
         ListValue result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public ListValue buildPartial() {
         ListValue result = new ListValue(this);
         this.buildPartialRepeatedFields(result);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartialRepeatedFields(ListValue result) {
         if (this.valuesBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.values_ = Collections.unmodifiableList(this.values_);
               this.bitField0_ &= -2;
            }

            result.values_ = this.values_;
         } else {
            result.values_ = this.valuesBuilder_.build();
         }
      }

      private void buildPartial0(ListValue result) {
         int from_bitField0_ = this.bitField0_;
      }

      public ListValue.Builder mergeFrom(Message other) {
         if (other instanceof ListValue) {
            return this.mergeFrom((ListValue)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public ListValue.Builder mergeFrom(ListValue other) {
         if (other == ListValue.getDefaultInstance()) {
            return this;
         } else {
            if (this.valuesBuilder_ == null) {
               if (!other.values_.isEmpty()) {
                  if (this.values_.isEmpty()) {
                     this.values_ = other.values_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureValuesIsMutable();
                     this.values_.addAll(other.values_);
                  }

                  this.onChanged();
               }
            } else if (!other.values_.isEmpty()) {
               if (this.valuesBuilder_.isEmpty()) {
                  this.valuesBuilder_.dispose();
                  this.valuesBuilder_ = null;
                  this.values_ = other.values_;
                  this.bitField0_ &= -2;
                  this.valuesBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetValuesFieldBuilder() : null;
               } else {
                  this.valuesBuilder_.addAllMessages(other.values_);
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

      public ListValue.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        Value m = input.readMessage(Value.parser(), extensionRegistry);
                        if (this.valuesBuilder_ == null) {
                           this.ensureValuesIsMutable();
                           this.values_.add(m);
                        } else {
                           this.valuesBuilder_.addMessage(m);
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

      private void ensureValuesIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.values_ = new ArrayList<>(this.values_);
            this.bitField0_ |= 1;
         }
      }

      @Override
      public List<Value> getValuesList() {
         return this.valuesBuilder_ == null ? Collections.unmodifiableList(this.values_) : this.valuesBuilder_.getMessageList();
      }

      @Override
      public int getValuesCount() {
         return this.valuesBuilder_ == null ? this.values_.size() : this.valuesBuilder_.getCount();
      }

      @Override
      public Value getValues(int index) {
         return this.valuesBuilder_ == null ? this.values_.get(index) : this.valuesBuilder_.getMessage(index);
      }

      public ListValue.Builder setValues(int index, Value value) {
         if (this.valuesBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureValuesIsMutable();
            this.values_.set(index, value);
            this.onChanged();
         } else {
            this.valuesBuilder_.setMessage(index, value);
         }

         return this;
      }

      public ListValue.Builder setValues(int index, Value.Builder builderForValue) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.valuesBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public ListValue.Builder addValues(Value value) {
         if (this.valuesBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureValuesIsMutable();
            this.values_.add(value);
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(value);
         }

         return this;
      }

      public ListValue.Builder addValues(int index, Value value) {
         if (this.valuesBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureValuesIsMutable();
            this.values_.add(index, value);
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(index, value);
         }

         return this;
      }

      public ListValue.Builder addValues(Value.Builder builderForValue) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public ListValue.Builder addValues(int index, Value.Builder builderForValue) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public ListValue.Builder addAllValues(Iterable<? extends Value> values) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.values_);
            this.onChanged();
         } else {
            this.valuesBuilder_.addAllMessages(values);
         }

         return this;
      }

      public ListValue.Builder clearValues() {
         if (this.valuesBuilder_ == null) {
            this.values_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.valuesBuilder_.clear();
         }

         return this;
      }

      public ListValue.Builder removeValues(int index) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.remove(index);
            this.onChanged();
         } else {
            this.valuesBuilder_.remove(index);
         }

         return this;
      }

      public Value.Builder getValuesBuilder(int index) {
         return this.internalGetValuesFieldBuilder().getBuilder(index);
      }

      @Override
      public ValueOrBuilder getValuesOrBuilder(int index) {
         return this.valuesBuilder_ == null ? this.values_.get(index) : this.valuesBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
         return this.valuesBuilder_ != null ? this.valuesBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.values_);
      }

      public Value.Builder addValuesBuilder() {
         return this.internalGetValuesFieldBuilder().addBuilder(Value.getDefaultInstance());
      }

      public Value.Builder addValuesBuilder(int index) {
         return this.internalGetValuesFieldBuilder().addBuilder(index, Value.getDefaultInstance());
      }

      public List<Value.Builder> getValuesBuilderList() {
         return this.internalGetValuesFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<Value, Value.Builder, ValueOrBuilder> internalGetValuesFieldBuilder() {
         if (this.valuesBuilder_ == null) {
            this.valuesBuilder_ = new RepeatedFieldBuilder<>(this.values_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.values_ = null;
         }

         return this.valuesBuilder_;
      }
   }
}
