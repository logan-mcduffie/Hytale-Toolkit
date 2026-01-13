package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Value extends GeneratedMessage implements ValueOrBuilder {
   private static final long serialVersionUID = 0L;
   private int kindCase_ = 0;
   private Object kind_;
   public static final int NULL_VALUE_FIELD_NUMBER = 1;
   public static final int NUMBER_VALUE_FIELD_NUMBER = 2;
   public static final int STRING_VALUE_FIELD_NUMBER = 3;
   public static final int BOOL_VALUE_FIELD_NUMBER = 4;
   public static final int STRUCT_VALUE_FIELD_NUMBER = 5;
   public static final int LIST_VALUE_FIELD_NUMBER = 6;
   private byte memoizedIsInitialized = -1;
   private static final Value DEFAULT_INSTANCE = new Value();
   private static final Parser<Value> PARSER = new AbstractParser<Value>() {
      public Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Value.Builder builder = Value.newBuilder();

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

   private Value(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Value() {
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return StructProto.internal_static_google_protobuf_Value_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable.ensureFieldAccessorsInitialized(Value.class, Value.Builder.class);
   }

   @Override
   public Value.KindCase getKindCase() {
      return Value.KindCase.forNumber(this.kindCase_);
   }

   @Override
   public boolean hasNullValue() {
      return this.kindCase_ == 1;
   }

   @Override
   public int getNullValueValue() {
      return this.kindCase_ == 1 ? (Integer)this.kind_ : 0;
   }

   @Override
   public NullValue getNullValue() {
      if (this.kindCase_ == 1) {
         NullValue result = NullValue.forNumber((Integer)this.kind_);
         return result == null ? NullValue.UNRECOGNIZED : result;
      } else {
         return NullValue.NULL_VALUE;
      }
   }

   @Override
   public boolean hasNumberValue() {
      return this.kindCase_ == 2;
   }

   @Override
   public double getNumberValue() {
      return this.kindCase_ == 2 ? (Double)this.kind_ : 0.0;
   }

   @Override
   public boolean hasStringValue() {
      return this.kindCase_ == 3;
   }

   @Override
   public String getStringValue() {
      Object ref = "";
      if (this.kindCase_ == 3) {
         ref = this.kind_;
      }

      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         if (this.kindCase_ == 3) {
            this.kind_ = s;
         }

         return s;
      }
   }

   @Override
   public ByteString getStringValueBytes() {
      Object ref = "";
      if (this.kindCase_ == 3) {
         ref = this.kind_;
      }

      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         if (this.kindCase_ == 3) {
            this.kind_ = b;
         }

         return b;
      } else {
         return (ByteString)ref;
      }
   }

   @Override
   public boolean hasBoolValue() {
      return this.kindCase_ == 4;
   }

   @Override
   public boolean getBoolValue() {
      return this.kindCase_ == 4 ? (Boolean)this.kind_ : false;
   }

   @Override
   public boolean hasStructValue() {
      return this.kindCase_ == 5;
   }

   @Override
   public Struct getStructValue() {
      return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
   }

   @Override
   public StructOrBuilder getStructValueOrBuilder() {
      return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
   }

   @Override
   public boolean hasListValue() {
      return this.kindCase_ == 6;
   }

   @Override
   public ListValue getListValue() {
      return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
   }

   @Override
   public ListValueOrBuilder getListValueOrBuilder() {
      return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
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
      if (this.kindCase_ == 1) {
         output.writeEnum(1, (Integer)this.kind_);
      }

      if (this.kindCase_ == 2) {
         output.writeDouble(2, (Double)this.kind_);
      }

      if (this.kindCase_ == 3) {
         GeneratedMessage.writeString(output, 3, this.kind_);
      }

      if (this.kindCase_ == 4) {
         output.writeBool(4, (Boolean)this.kind_);
      }

      if (this.kindCase_ == 5) {
         output.writeMessage(5, (Struct)this.kind_);
      }

      if (this.kindCase_ == 6) {
         output.writeMessage(6, (ListValue)this.kind_);
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
         if (this.kindCase_ == 1) {
            size += CodedOutputStream.computeEnumSize(1, (Integer)this.kind_);
         }

         if (this.kindCase_ == 2) {
            size += CodedOutputStream.computeDoubleSize(2, (Double)this.kind_);
         }

         if (this.kindCase_ == 3) {
            size += GeneratedMessage.computeStringSize(3, this.kind_);
         }

         if (this.kindCase_ == 4) {
            size += CodedOutputStream.computeBoolSize(4, (Boolean)this.kind_);
         }

         if (this.kindCase_ == 5) {
            size += CodedOutputStream.computeMessageSize(5, (Struct)this.kind_);
         }

         if (this.kindCase_ == 6) {
            size += CodedOutputStream.computeMessageSize(6, (ListValue)this.kind_);
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
      } else if (!(obj instanceof Value)) {
         return super.equals(obj);
      } else {
         Value other = (Value)obj;
         if (!this.getKindCase().equals(other.getKindCase())) {
            return false;
         } else {
            switch (this.kindCase_) {
               case 0:
               default:
                  break;
               case 1:
                  if (this.getNullValueValue() != other.getNullValueValue()) {
                     return false;
                  }
                  break;
               case 2:
                  if (Double.doubleToLongBits(this.getNumberValue()) != Double.doubleToLongBits(other.getNumberValue())) {
                     return false;
                  }
                  break;
               case 3:
                  if (!this.getStringValue().equals(other.getStringValue())) {
                     return false;
                  }
                  break;
               case 4:
                  if (this.getBoolValue() != other.getBoolValue()) {
                     return false;
                  }
                  break;
               case 5:
                  if (!this.getStructValue().equals(other.getStructValue())) {
                     return false;
                  }
                  break;
               case 6:
                  if (!this.getListValue().equals(other.getListValue())) {
                     return false;
                  }
            }

            return this.getUnknownFields().equals(other.getUnknownFields());
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
         switch (this.kindCase_) {
            case 0:
            default:
               break;
            case 1:
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getNullValueValue();
               break;
            case 2:
               hash = 37 * hash + 2;
               hash = 53 * hash + Internal.hashLong(Double.doubleToLongBits(this.getNumberValue()));
               break;
            case 3:
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getStringValue().hashCode();
               break;
            case 4:
               hash = 37 * hash + 4;
               hash = 53 * hash + Internal.hashBoolean(this.getBoolValue());
               break;
            case 5:
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getStructValue().hashCode();
               break;
            case 6:
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getListValue().hashCode();
         }

         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Value parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Value parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Value parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Value.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Value.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Value.Builder newBuilder(Value prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Value.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Value.Builder() : new Value.Builder().mergeFrom(this);
   }

   protected Value.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Value.Builder(parent);
   }

   public static Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Value> parser() {
      return PARSER;
   }

   @Override
   public Parser<Value> getParserForType() {
      return PARSER;
   }

   public Value getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Value");
   }

   public static final class Builder extends GeneratedMessage.Builder<Value.Builder> implements ValueOrBuilder {
      private int kindCase_ = 0;
      private Object kind_;
      private int bitField0_;
      private SingleFieldBuilder<Struct, Struct.Builder, StructOrBuilder> structValueBuilder_;
      private SingleFieldBuilder<ListValue, ListValue.Builder, ListValueOrBuilder> listValueBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_Value_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable.ensureFieldAccessorsInitialized(Value.class, Value.Builder.class);
      }

      private Builder() {
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
      }

      public Value.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         if (this.structValueBuilder_ != null) {
            this.structValueBuilder_.clear();
         }

         if (this.listValueBuilder_ != null) {
            this.listValueBuilder_.clear();
         }

         this.kindCase_ = 0;
         this.kind_ = null;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_Value_descriptor;
      }

      public Value getDefaultInstanceForType() {
         return Value.getDefaultInstance();
      }

      public Value build() {
         Value result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Value buildPartial() {
         Value result = new Value(this);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.buildPartialOneofs(result);
         this.onBuilt();
         return result;
      }

      private void buildPartial0(Value result) {
         int from_bitField0_ = this.bitField0_;
      }

      private void buildPartialOneofs(Value result) {
         result.kindCase_ = this.kindCase_;
         result.kind_ = this.kind_;
         if (this.kindCase_ == 5 && this.structValueBuilder_ != null) {
            result.kind_ = this.structValueBuilder_.build();
         }

         if (this.kindCase_ == 6 && this.listValueBuilder_ != null) {
            result.kind_ = this.listValueBuilder_.build();
         }
      }

      public Value.Builder mergeFrom(Message other) {
         if (other instanceof Value) {
            return this.mergeFrom((Value)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Value.Builder mergeFrom(Value other) {
         if (other == Value.getDefaultInstance()) {
            return this;
         } else {
            switch (other.getKindCase()) {
               case NULL_VALUE:
                  this.setNullValueValue(other.getNullValueValue());
                  break;
               case NUMBER_VALUE:
                  this.setNumberValue(other.getNumberValue());
                  break;
               case STRING_VALUE:
                  this.kindCase_ = 3;
                  this.kind_ = other.kind_;
                  this.onChanged();
                  break;
               case BOOL_VALUE:
                  this.setBoolValue(other.getBoolValue());
                  break;
               case STRUCT_VALUE:
                  this.mergeStructValue(other.getStructValue());
                  break;
               case LIST_VALUE:
                  this.mergeListValue(other.getListValue());
               case KIND_NOT_SET:
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

      public Value.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        int rawValue = input.readEnum();
                        this.kindCase_ = 1;
                        this.kind_ = rawValue;
                        break;
                     case 17:
                        this.kind_ = input.readDouble();
                        this.kindCase_ = 2;
                        break;
                     case 26:
                        String s = input.readStringRequireUtf8();
                        this.kindCase_ = 3;
                        this.kind_ = s;
                        break;
                     case 32:
                        this.kind_ = input.readBool();
                        this.kindCase_ = 4;
                        break;
                     case 42:
                        input.readMessage(this.internalGetStructValueFieldBuilder().getBuilder(), extensionRegistry);
                        this.kindCase_ = 5;
                        break;
                     case 50:
                        input.readMessage(this.internalGetListValueFieldBuilder().getBuilder(), extensionRegistry);
                        this.kindCase_ = 6;
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
      public Value.KindCase getKindCase() {
         return Value.KindCase.forNumber(this.kindCase_);
      }

      public Value.Builder clearKind() {
         this.kindCase_ = 0;
         this.kind_ = null;
         this.onChanged();
         return this;
      }

      @Override
      public boolean hasNullValue() {
         return this.kindCase_ == 1;
      }

      @Override
      public int getNullValueValue() {
         return this.kindCase_ == 1 ? (Integer)this.kind_ : 0;
      }

      public Value.Builder setNullValueValue(int value) {
         this.kindCase_ = 1;
         this.kind_ = value;
         this.onChanged();
         return this;
      }

      @Override
      public NullValue getNullValue() {
         if (this.kindCase_ == 1) {
            NullValue result = NullValue.forNumber((Integer)this.kind_);
            return result == null ? NullValue.UNRECOGNIZED : result;
         } else {
            return NullValue.NULL_VALUE;
         }
      }

      public Value.Builder setNullValue(NullValue value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.kindCase_ = 1;
            this.kind_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Value.Builder clearNullValue() {
         if (this.kindCase_ == 1) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      @Override
      public boolean hasNumberValue() {
         return this.kindCase_ == 2;
      }

      @Override
      public double getNumberValue() {
         return this.kindCase_ == 2 ? (Double)this.kind_ : 0.0;
      }

      public Value.Builder setNumberValue(double value) {
         this.kindCase_ = 2;
         this.kind_ = value;
         this.onChanged();
         return this;
      }

      public Value.Builder clearNumberValue() {
         if (this.kindCase_ == 2) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      @Override
      public boolean hasStringValue() {
         return this.kindCase_ == 3;
      }

      @Override
      public String getStringValue() {
         Object ref = "";
         if (this.kindCase_ == 3) {
            ref = this.kind_;
         }

         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (this.kindCase_ == 3) {
               this.kind_ = s;
            }

            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getStringValueBytes() {
         Object ref = "";
         if (this.kindCase_ == 3) {
            ref = this.kind_;
         }

         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            if (this.kindCase_ == 3) {
               this.kind_ = b;
            }

            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Value.Builder setStringValue(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.kindCase_ = 3;
            this.kind_ = value;
            this.onChanged();
            return this;
         }
      }

      public Value.Builder clearStringValue() {
         if (this.kindCase_ == 3) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      public Value.Builder setStringValueBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.kindCase_ = 3;
            this.kind_ = value;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean hasBoolValue() {
         return this.kindCase_ == 4;
      }

      @Override
      public boolean getBoolValue() {
         return this.kindCase_ == 4 ? (Boolean)this.kind_ : false;
      }

      public Value.Builder setBoolValue(boolean value) {
         this.kindCase_ = 4;
         this.kind_ = value;
         this.onChanged();
         return this;
      }

      public Value.Builder clearBoolValue() {
         if (this.kindCase_ == 4) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      @Override
      public boolean hasStructValue() {
         return this.kindCase_ == 5;
      }

      @Override
      public Struct getStructValue() {
         if (this.structValueBuilder_ == null) {
            return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
         } else {
            return this.kindCase_ == 5 ? this.structValueBuilder_.getMessage() : Struct.getDefaultInstance();
         }
      }

      public Value.Builder setStructValue(Struct value) {
         if (this.structValueBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.kind_ = value;
            this.onChanged();
         } else {
            this.structValueBuilder_.setMessage(value);
         }

         this.kindCase_ = 5;
         return this;
      }

      public Value.Builder setStructValue(Struct.Builder builderForValue) {
         if (this.structValueBuilder_ == null) {
            this.kind_ = builderForValue.build();
            this.onChanged();
         } else {
            this.structValueBuilder_.setMessage(builderForValue.build());
         }

         this.kindCase_ = 5;
         return this;
      }

      public Value.Builder mergeStructValue(Struct value) {
         if (this.structValueBuilder_ == null) {
            if (this.kindCase_ == 5 && this.kind_ != Struct.getDefaultInstance()) {
               this.kind_ = Struct.newBuilder((Struct)this.kind_).mergeFrom(value).buildPartial();
            } else {
               this.kind_ = value;
            }

            this.onChanged();
         } else if (this.kindCase_ == 5) {
            this.structValueBuilder_.mergeFrom(value);
         } else {
            this.structValueBuilder_.setMessage(value);
         }

         this.kindCase_ = 5;
         return this;
      }

      public Value.Builder clearStructValue() {
         if (this.structValueBuilder_ == null) {
            if (this.kindCase_ == 5) {
               this.kindCase_ = 0;
               this.kind_ = null;
               this.onChanged();
            }
         } else {
            if (this.kindCase_ == 5) {
               this.kindCase_ = 0;
               this.kind_ = null;
            }

            this.structValueBuilder_.clear();
         }

         return this;
      }

      public Struct.Builder getStructValueBuilder() {
         return this.internalGetStructValueFieldBuilder().getBuilder();
      }

      @Override
      public StructOrBuilder getStructValueOrBuilder() {
         if (this.kindCase_ == 5 && this.structValueBuilder_ != null) {
            return this.structValueBuilder_.getMessageOrBuilder();
         } else {
            return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
         }
      }

      private SingleFieldBuilder<Struct, Struct.Builder, StructOrBuilder> internalGetStructValueFieldBuilder() {
         if (this.structValueBuilder_ == null) {
            if (this.kindCase_ != 5) {
               this.kind_ = Struct.getDefaultInstance();
            }

            this.structValueBuilder_ = new SingleFieldBuilder<>((Struct)this.kind_, this.getParentForChildren(), this.isClean());
            this.kind_ = null;
         }

         this.kindCase_ = 5;
         this.onChanged();
         return this.structValueBuilder_;
      }

      @Override
      public boolean hasListValue() {
         return this.kindCase_ == 6;
      }

      @Override
      public ListValue getListValue() {
         if (this.listValueBuilder_ == null) {
            return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
         } else {
            return this.kindCase_ == 6 ? this.listValueBuilder_.getMessage() : ListValue.getDefaultInstance();
         }
      }

      public Value.Builder setListValue(ListValue value) {
         if (this.listValueBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.kind_ = value;
            this.onChanged();
         } else {
            this.listValueBuilder_.setMessage(value);
         }

         this.kindCase_ = 6;
         return this;
      }

      public Value.Builder setListValue(ListValue.Builder builderForValue) {
         if (this.listValueBuilder_ == null) {
            this.kind_ = builderForValue.build();
            this.onChanged();
         } else {
            this.listValueBuilder_.setMessage(builderForValue.build());
         }

         this.kindCase_ = 6;
         return this;
      }

      public Value.Builder mergeListValue(ListValue value) {
         if (this.listValueBuilder_ == null) {
            if (this.kindCase_ == 6 && this.kind_ != ListValue.getDefaultInstance()) {
               this.kind_ = ListValue.newBuilder((ListValue)this.kind_).mergeFrom(value).buildPartial();
            } else {
               this.kind_ = value;
            }

            this.onChanged();
         } else if (this.kindCase_ == 6) {
            this.listValueBuilder_.mergeFrom(value);
         } else {
            this.listValueBuilder_.setMessage(value);
         }

         this.kindCase_ = 6;
         return this;
      }

      public Value.Builder clearListValue() {
         if (this.listValueBuilder_ == null) {
            if (this.kindCase_ == 6) {
               this.kindCase_ = 0;
               this.kind_ = null;
               this.onChanged();
            }
         } else {
            if (this.kindCase_ == 6) {
               this.kindCase_ = 0;
               this.kind_ = null;
            }

            this.listValueBuilder_.clear();
         }

         return this;
      }

      public ListValue.Builder getListValueBuilder() {
         return this.internalGetListValueFieldBuilder().getBuilder();
      }

      @Override
      public ListValueOrBuilder getListValueOrBuilder() {
         if (this.kindCase_ == 6 && this.listValueBuilder_ != null) {
            return this.listValueBuilder_.getMessageOrBuilder();
         } else {
            return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
         }
      }

      private SingleFieldBuilder<ListValue, ListValue.Builder, ListValueOrBuilder> internalGetListValueFieldBuilder() {
         if (this.listValueBuilder_ == null) {
            if (this.kindCase_ != 6) {
               this.kind_ = ListValue.getDefaultInstance();
            }

            this.listValueBuilder_ = new SingleFieldBuilder<>((ListValue)this.kind_, this.getParentForChildren(), this.isClean());
            this.kind_ = null;
         }

         this.kindCase_ = 6;
         this.onChanged();
         return this.listValueBuilder_;
      }
   }

   public static enum KindCase implements Internal.EnumLite, AbstractMessageLite.InternalOneOfEnum {
      NULL_VALUE(1),
      NUMBER_VALUE(2),
      STRING_VALUE(3),
      BOOL_VALUE(4),
      STRUCT_VALUE(5),
      LIST_VALUE(6),
      KIND_NOT_SET(0);

      private final int value;

      private KindCase(int value) {
         this.value = value;
      }

      @Deprecated
      public static Value.KindCase valueOf(int value) {
         return forNumber(value);
      }

      public static Value.KindCase forNumber(int value) {
         switch (value) {
            case 0:
               return KIND_NOT_SET;
            case 1:
               return NULL_VALUE;
            case 2:
               return NUMBER_VALUE;
            case 3:
               return STRING_VALUE;
            case 4:
               return BOOL_VALUE;
            case 5:
               return STRUCT_VALUE;
            case 6:
               return LIST_VALUE;
            default:
               return null;
         }
      }

      @Override
      public int getNumber() {
         return this.value;
      }
   }
}
