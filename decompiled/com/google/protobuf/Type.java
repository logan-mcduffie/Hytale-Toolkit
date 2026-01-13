package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Type extends GeneratedMessage implements TypeOrBuilder {
   private static final long serialVersionUID = 0L;
   private int bitField0_;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_ = "";
   public static final int FIELDS_FIELD_NUMBER = 2;
   private List<Field> fields_;
   public static final int ONEOFS_FIELD_NUMBER = 3;
   private LazyStringArrayList oneofs_ = LazyStringArrayList.emptyList();
   public static final int OPTIONS_FIELD_NUMBER = 4;
   private List<Option> options_;
   public static final int SOURCE_CONTEXT_FIELD_NUMBER = 5;
   private SourceContext sourceContext_;
   public static final int SYNTAX_FIELD_NUMBER = 6;
   private int syntax_ = 0;
   public static final int EDITION_FIELD_NUMBER = 7;
   private volatile Object edition_ = "";
   private byte memoizedIsInitialized = -1;
   private static final Type DEFAULT_INSTANCE = new Type();
   private static final Parser<Type> PARSER = new AbstractParser<Type>() {
      public Type parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         Type.Builder builder = Type.newBuilder();

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

   private Type(GeneratedMessage.Builder<?> builder) {
      super(builder);
   }

   private Type() {
      this.name_ = "";
      this.fields_ = Collections.emptyList();
      this.oneofs_ = LazyStringArrayList.emptyList();
      this.options_ = Collections.emptyList();
      this.syntax_ = 0;
      this.edition_ = "";
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_Type_descriptor;
   }

   @Override
   protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Type_fieldAccessorTable.ensureFieldAccessorsInitialized(Type.class, Type.Builder.class);
   }

   @Override
   public String getName() {
      Object ref = this.name_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.name_ = s;
         return s;
      }
   }

   @Override
   public ByteString getNameBytes() {
      Object ref = this.name_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.name_ = b;
         return b;
      } else {
         return (ByteString)ref;
      }
   }

   @Override
   public List<Field> getFieldsList() {
      return this.fields_;
   }

   @Override
   public List<? extends FieldOrBuilder> getFieldsOrBuilderList() {
      return this.fields_;
   }

   @Override
   public int getFieldsCount() {
      return this.fields_.size();
   }

   @Override
   public Field getFields(int index) {
      return this.fields_.get(index);
   }

   @Override
   public FieldOrBuilder getFieldsOrBuilder(int index) {
      return this.fields_.get(index);
   }

   public ProtocolStringList getOneofsList() {
      return this.oneofs_;
   }

   @Override
   public int getOneofsCount() {
      return this.oneofs_.size();
   }

   @Override
   public String getOneofs(int index) {
      return this.oneofs_.get(index);
   }

   @Override
   public ByteString getOneofsBytes(int index) {
      return this.oneofs_.getByteString(index);
   }

   @Override
   public List<Option> getOptionsList() {
      return this.options_;
   }

   @Override
   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      return this.options_;
   }

   @Override
   public int getOptionsCount() {
      return this.options_.size();
   }

   @Override
   public Option getOptions(int index) {
      return this.options_.get(index);
   }

   @Override
   public OptionOrBuilder getOptionsOrBuilder(int index) {
      return this.options_.get(index);
   }

   @Override
   public boolean hasSourceContext() {
      return (this.bitField0_ & 1) != 0;
   }

   @Override
   public SourceContext getSourceContext() {
      return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
   }

   @Override
   public SourceContextOrBuilder getSourceContextOrBuilder() {
      return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
   }

   @Override
   public int getSyntaxValue() {
      return this.syntax_;
   }

   @Override
   public Syntax getSyntax() {
      Syntax result = Syntax.forNumber(this.syntax_);
      return result == null ? Syntax.UNRECOGNIZED : result;
   }

   @Override
   public String getEdition() {
      Object ref = this.edition_;
      if (ref instanceof String) {
         return (String)ref;
      } else {
         ByteString bs = (ByteString)ref;
         String s = bs.toStringUtf8();
         this.edition_ = s;
         return s;
      }
   }

   @Override
   public ByteString getEditionBytes() {
      Object ref = this.edition_;
      if (ref instanceof String) {
         ByteString b = ByteString.copyFromUtf8((String)ref);
         this.edition_ = b;
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
      if (!GeneratedMessage.isStringEmpty(this.name_)) {
         GeneratedMessage.writeString(output, 1, this.name_);
      }

      for (int i = 0; i < this.fields_.size(); i++) {
         output.writeMessage(2, this.fields_.get(i));
      }

      for (int i = 0; i < this.oneofs_.size(); i++) {
         GeneratedMessage.writeString(output, 3, this.oneofs_.getRaw(i));
      }

      for (int i = 0; i < this.options_.size(); i++) {
         output.writeMessage(4, this.options_.get(i));
      }

      if ((this.bitField0_ & 1) != 0) {
         output.writeMessage(5, this.getSourceContext());
      }

      if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
         output.writeEnum(6, this.syntax_);
      }

      if (!GeneratedMessage.isStringEmpty(this.edition_)) {
         GeneratedMessage.writeString(output, 7, this.edition_);
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
         if (!GeneratedMessage.isStringEmpty(this.name_)) {
            size += GeneratedMessage.computeStringSize(1, this.name_);
         }

         for (int i = 0; i < this.fields_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(2, this.fields_.get(i));
         }

         int dataSize = 0;

         for (int i = 0; i < this.oneofs_.size(); i++) {
            dataSize += computeStringSizeNoTag(this.oneofs_.getRaw(i));
         }

         size += dataSize;
         size += 1 * this.getOneofsList().size();

         for (int i = 0; i < this.options_.size(); i++) {
            size += CodedOutputStream.computeMessageSize(4, this.options_.get(i));
         }

         if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(5, this.getSourceContext());
         }

         if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            size += CodedOutputStream.computeEnumSize(6, this.syntax_);
         }

         if (!GeneratedMessage.isStringEmpty(this.edition_)) {
            size += GeneratedMessage.computeStringSize(7, this.edition_);
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
      } else if (!(obj instanceof Type)) {
         return super.equals(obj);
      } else {
         Type other = (Type)obj;
         if (!this.getName().equals(other.getName())) {
            return false;
         } else if (!this.getFieldsList().equals(other.getFieldsList())) {
            return false;
         } else if (!this.getOneofsList().equals(other.getOneofsList())) {
            return false;
         } else if (!this.getOptionsList().equals(other.getOptionsList())) {
            return false;
         } else if (this.hasSourceContext() != other.hasSourceContext()) {
            return false;
         } else if (this.hasSourceContext() && !this.getSourceContext().equals(other.getSourceContext())) {
            return false;
         } else if (this.syntax_ != other.syntax_) {
            return false;
         } else {
            return !this.getEdition().equals(other.getEdition()) ? false : this.getUnknownFields().equals(other.getUnknownFields());
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
         hash = 53 * hash + this.getName().hashCode();
         if (this.getFieldsCount() > 0) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getFieldsList().hashCode();
         }

         if (this.getOneofsCount() > 0) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getOneofsList().hashCode();
         }

         if (this.getOptionsCount() > 0) {
            hash = 37 * hash + 4;
            hash = 53 * hash + this.getOptionsList().hashCode();
         }

         if (this.hasSourceContext()) {
            hash = 37 * hash + 5;
            hash = 53 * hash + this.getSourceContext().hashCode();
         }

         hash = 37 * hash + 6;
         hash = 53 * hash + this.syntax_;
         hash = 37 * hash + 7;
         hash = 53 * hash + this.getEdition().hashCode();
         hash = 29 * hash + this.getUnknownFields().hashCode();
         this.memoizedHashCode = hash;
         return hash;
      }
   }

   public static Type parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Type parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Type parseFrom(ByteString data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Type parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Type parseFrom(byte[] data) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
   }

   public static Type parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
   }

   public static Type parseFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Type parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public static Type parseDelimitedFrom(InputStream input) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
   }

   public static Type parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
   }

   public static Type parseFrom(CodedInputStream input) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input);
   }

   public static Type parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
   }

   public Type.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Type.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Type.Builder newBuilder(Type prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
   }

   public Type.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Type.Builder() : new Type.Builder().mergeFrom(this);
   }

   protected Type.Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
      return new Type.Builder(parent);
   }

   public static Type getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Type> parser() {
      return PARSER;
   }

   @Override
   public Parser<Type> getParserForType() {
      return PARSER;
   }

   public Type getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   static {
      RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Type");
   }

   public static final class Builder extends GeneratedMessage.Builder<Type.Builder> implements TypeOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private List<Field> fields_ = Collections.emptyList();
      private RepeatedFieldBuilder<Field, Field.Builder, FieldOrBuilder> fieldsBuilder_;
      private LazyStringArrayList oneofs_ = LazyStringArrayList.emptyList();
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilder<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
      private SourceContext sourceContext_;
      private SingleFieldBuilder<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
      private int syntax_ = 0;
      private Object edition_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_Type_descriptor;
      }

      @Override
      protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_Type_fieldAccessorTable.ensureFieldAccessorsInitialized(Type.class, Type.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(AbstractMessage.BuilderParent parent) {
         super(parent);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessage.alwaysUseFieldBuilders) {
            this.internalGetFieldsFieldBuilder();
            this.internalGetOptionsFieldBuilder();
            this.internalGetSourceContextFieldBuilder();
         }
      }

      public Type.Builder clear() {
         super.clear();
         this.bitField0_ = 0;
         this.name_ = "";
         if (this.fieldsBuilder_ == null) {
            this.fields_ = Collections.emptyList();
         } else {
            this.fields_ = null;
            this.fieldsBuilder_.clear();
         }

         this.bitField0_ &= -3;
         this.oneofs_ = LazyStringArrayList.emptyList();
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
         } else {
            this.options_ = null;
            this.optionsBuilder_.clear();
         }

         this.bitField0_ &= -9;
         this.sourceContext_ = null;
         if (this.sourceContextBuilder_ != null) {
            this.sourceContextBuilder_.dispose();
            this.sourceContextBuilder_ = null;
         }

         this.syntax_ = 0;
         this.edition_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_Type_descriptor;
      }

      public Type getDefaultInstanceForType() {
         return Type.getDefaultInstance();
      }

      public Type build() {
         Type result = this.buildPartial();
         if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
         } else {
            return result;
         }
      }

      public Type buildPartial() {
         Type result = new Type(this);
         this.buildPartialRepeatedFields(result);
         if (this.bitField0_ != 0) {
            this.buildPartial0(result);
         }

         this.onBuilt();
         return result;
      }

      private void buildPartialRepeatedFields(Type result) {
         if (this.fieldsBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0) {
               this.fields_ = Collections.unmodifiableList(this.fields_);
               this.bitField0_ &= -3;
            }

            result.fields_ = this.fields_;
         } else {
            result.fields_ = this.fieldsBuilder_.build();
         }

         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 8) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -9;
            }

            result.options_ = this.options_;
         } else {
            result.options_ = this.optionsBuilder_.build();
         }
      }

      private void buildPartial0(Type result) {
         int from_bitField0_ = this.bitField0_;
         if ((from_bitField0_ & 1) != 0) {
            result.name_ = this.name_;
         }

         if ((from_bitField0_ & 4) != 0) {
            this.oneofs_.makeImmutable();
            result.oneofs_ = this.oneofs_;
         }

         int to_bitField0_ = 0;
         if ((from_bitField0_ & 16) != 0) {
            result.sourceContext_ = this.sourceContextBuilder_ == null ? this.sourceContext_ : this.sourceContextBuilder_.build();
            to_bitField0_ |= 1;
         }

         if ((from_bitField0_ & 32) != 0) {
            result.syntax_ = this.syntax_;
         }

         if ((from_bitField0_ & 64) != 0) {
            result.edition_ = this.edition_;
         }

         result.bitField0_ |= to_bitField0_;
      }

      public Type.Builder mergeFrom(Message other) {
         if (other instanceof Type) {
            return this.mergeFrom((Type)other);
         } else {
            super.mergeFrom(other);
            return this;
         }
      }

      public Type.Builder mergeFrom(Type other) {
         if (other == Type.getDefaultInstance()) {
            return this;
         } else {
            if (!other.getName().isEmpty()) {
               this.name_ = other.name_;
               this.bitField0_ |= 1;
               this.onChanged();
            }

            if (this.fieldsBuilder_ == null) {
               if (!other.fields_.isEmpty()) {
                  if (this.fields_.isEmpty()) {
                     this.fields_ = other.fields_;
                     this.bitField0_ &= -3;
                  } else {
                     this.ensureFieldsIsMutable();
                     this.fields_.addAll(other.fields_);
                  }

                  this.onChanged();
               }
            } else if (!other.fields_.isEmpty()) {
               if (this.fieldsBuilder_.isEmpty()) {
                  this.fieldsBuilder_.dispose();
                  this.fieldsBuilder_ = null;
                  this.fields_ = other.fields_;
                  this.bitField0_ &= -3;
                  this.fieldsBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetFieldsFieldBuilder() : null;
               } else {
                  this.fieldsBuilder_.addAllMessages(other.fields_);
               }
            }

            if (!other.oneofs_.isEmpty()) {
               if (this.oneofs_.isEmpty()) {
                  this.oneofs_ = other.oneofs_;
                  this.bitField0_ |= 4;
               } else {
                  this.ensureOneofsIsMutable();
                  this.oneofs_.addAll(other.oneofs_);
               }

               this.onChanged();
            }

            if (this.optionsBuilder_ == null) {
               if (!other.options_.isEmpty()) {
                  if (this.options_.isEmpty()) {
                     this.options_ = other.options_;
                     this.bitField0_ &= -9;
                  } else {
                     this.ensureOptionsIsMutable();
                     this.options_.addAll(other.options_);
                  }

                  this.onChanged();
               }
            } else if (!other.options_.isEmpty()) {
               if (this.optionsBuilder_.isEmpty()) {
                  this.optionsBuilder_.dispose();
                  this.optionsBuilder_ = null;
                  this.options_ = other.options_;
                  this.bitField0_ &= -9;
                  this.optionsBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetOptionsFieldBuilder() : null;
               } else {
                  this.optionsBuilder_.addAllMessages(other.options_);
               }
            }

            if (other.hasSourceContext()) {
               this.mergeSourceContext(other.getSourceContext());
            }

            if (other.syntax_ != 0) {
               this.setSyntaxValue(other.getSyntaxValue());
            }

            if (!other.getEdition().isEmpty()) {
               this.edition_ = other.edition_;
               this.bitField0_ |= 64;
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

      public Type.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
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
                        this.name_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        Field mx = input.readMessage(Field.parser(), extensionRegistry);
                        if (this.fieldsBuilder_ == null) {
                           this.ensureFieldsIsMutable();
                           this.fields_.add(mx);
                        } else {
                           this.fieldsBuilder_.addMessage(mx);
                        }
                        break;
                     case 26:
                        String s = input.readStringRequireUtf8();
                        this.ensureOneofsIsMutable();
                        this.oneofs_.add(s);
                        break;
                     case 34:
                        Option m = input.readMessage(Option.parser(), extensionRegistry);
                        if (this.optionsBuilder_ == null) {
                           this.ensureOptionsIsMutable();
                           this.options_.add(m);
                        } else {
                           this.optionsBuilder_.addMessage(m);
                        }
                        break;
                     case 42:
                        input.readMessage(this.internalGetSourceContextFieldBuilder().getBuilder(), extensionRegistry);
                        this.bitField0_ |= 16;
                        break;
                     case 48:
                        this.syntax_ = input.readEnum();
                        this.bitField0_ |= 32;
                        break;
                     case 58:
                        this.edition_ = input.readStringRequireUtf8();
                        this.bitField0_ |= 64;
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
      public String getName() {
         Object ref = this.name_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.name_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getNameBytes() {
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Type.Builder setName(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.name_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      public Type.Builder clearName() {
         this.name_ = Type.getDefaultInstance().getName();
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public Type.Builder setNameBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.name_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
         }
      }

      private void ensureFieldsIsMutable() {
         if ((this.bitField0_ & 2) == 0) {
            this.fields_ = new ArrayList<>(this.fields_);
            this.bitField0_ |= 2;
         }
      }

      @Override
      public List<Field> getFieldsList() {
         return this.fieldsBuilder_ == null ? Collections.unmodifiableList(this.fields_) : this.fieldsBuilder_.getMessageList();
      }

      @Override
      public int getFieldsCount() {
         return this.fieldsBuilder_ == null ? this.fields_.size() : this.fieldsBuilder_.getCount();
      }

      @Override
      public Field getFields(int index) {
         return this.fieldsBuilder_ == null ? this.fields_.get(index) : this.fieldsBuilder_.getMessage(index);
      }

      public Type.Builder setFields(int index, Field value) {
         if (this.fieldsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureFieldsIsMutable();
            this.fields_.set(index, value);
            this.onChanged();
         } else {
            this.fieldsBuilder_.setMessage(index, value);
         }

         return this;
      }

      public Type.Builder setFields(int index, Field.Builder builderForValue) {
         if (this.fieldsBuilder_ == null) {
            this.ensureFieldsIsMutable();
            this.fields_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.fieldsBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public Type.Builder addFields(Field value) {
         if (this.fieldsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureFieldsIsMutable();
            this.fields_.add(value);
            this.onChanged();
         } else {
            this.fieldsBuilder_.addMessage(value);
         }

         return this;
      }

      public Type.Builder addFields(int index, Field value) {
         if (this.fieldsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureFieldsIsMutable();
            this.fields_.add(index, value);
            this.onChanged();
         } else {
            this.fieldsBuilder_.addMessage(index, value);
         }

         return this;
      }

      public Type.Builder addFields(Field.Builder builderForValue) {
         if (this.fieldsBuilder_ == null) {
            this.ensureFieldsIsMutable();
            this.fields_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.fieldsBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public Type.Builder addFields(int index, Field.Builder builderForValue) {
         if (this.fieldsBuilder_ == null) {
            this.ensureFieldsIsMutable();
            this.fields_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.fieldsBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public Type.Builder addAllFields(Iterable<? extends Field> values) {
         if (this.fieldsBuilder_ == null) {
            this.ensureFieldsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.fields_);
            this.onChanged();
         } else {
            this.fieldsBuilder_.addAllMessages(values);
         }

         return this;
      }

      public Type.Builder clearFields() {
         if (this.fieldsBuilder_ == null) {
            this.fields_ = Collections.emptyList();
            this.bitField0_ &= -3;
            this.onChanged();
         } else {
            this.fieldsBuilder_.clear();
         }

         return this;
      }

      public Type.Builder removeFields(int index) {
         if (this.fieldsBuilder_ == null) {
            this.ensureFieldsIsMutable();
            this.fields_.remove(index);
            this.onChanged();
         } else {
            this.fieldsBuilder_.remove(index);
         }

         return this;
      }

      public Field.Builder getFieldsBuilder(int index) {
         return this.internalGetFieldsFieldBuilder().getBuilder(index);
      }

      @Override
      public FieldOrBuilder getFieldsOrBuilder(int index) {
         return this.fieldsBuilder_ == null ? this.fields_.get(index) : this.fieldsBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends FieldOrBuilder> getFieldsOrBuilderList() {
         return this.fieldsBuilder_ != null ? this.fieldsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.fields_);
      }

      public Field.Builder addFieldsBuilder() {
         return this.internalGetFieldsFieldBuilder().addBuilder(Field.getDefaultInstance());
      }

      public Field.Builder addFieldsBuilder(int index) {
         return this.internalGetFieldsFieldBuilder().addBuilder(index, Field.getDefaultInstance());
      }

      public List<Field.Builder> getFieldsBuilderList() {
         return this.internalGetFieldsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<Field, Field.Builder, FieldOrBuilder> internalGetFieldsFieldBuilder() {
         if (this.fieldsBuilder_ == null) {
            this.fieldsBuilder_ = new RepeatedFieldBuilder<>(this.fields_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
            this.fields_ = null;
         }

         return this.fieldsBuilder_;
      }

      private void ensureOneofsIsMutable() {
         if (!this.oneofs_.isModifiable()) {
            this.oneofs_ = new LazyStringArrayList(this.oneofs_);
         }

         this.bitField0_ |= 4;
      }

      public ProtocolStringList getOneofsList() {
         this.oneofs_.makeImmutable();
         return this.oneofs_;
      }

      @Override
      public int getOneofsCount() {
         return this.oneofs_.size();
      }

      @Override
      public String getOneofs(int index) {
         return this.oneofs_.get(index);
      }

      @Override
      public ByteString getOneofsBytes(int index) {
         return this.oneofs_.getByteString(index);
      }

      public Type.Builder setOneofs(int index, String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.ensureOneofsIsMutable();
            this.oneofs_.set(index, value);
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public Type.Builder addOneofs(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.ensureOneofsIsMutable();
            this.oneofs_.add(value);
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      public Type.Builder addAllOneofs(Iterable<String> values) {
         this.ensureOneofsIsMutable();
         AbstractMessageLite.Builder.addAll(values, this.oneofs_);
         this.bitField0_ |= 4;
         this.onChanged();
         return this;
      }

      public Type.Builder clearOneofs() {
         this.oneofs_ = LazyStringArrayList.emptyList();
         this.bitField0_ &= -5;
         this.onChanged();
         return this;
      }

      public Type.Builder addOneofsBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.ensureOneofsIsMutable();
            this.oneofs_.add(value);
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
         }
      }

      private void ensureOptionsIsMutable() {
         if ((this.bitField0_ & 8) == 0) {
            this.options_ = new ArrayList<>(this.options_);
            this.bitField0_ |= 8;
         }
      }

      @Override
      public List<Option> getOptionsList() {
         return this.optionsBuilder_ == null ? Collections.unmodifiableList(this.options_) : this.optionsBuilder_.getMessageList();
      }

      @Override
      public int getOptionsCount() {
         return this.optionsBuilder_ == null ? this.options_.size() : this.optionsBuilder_.getCount();
      }

      @Override
      public Option getOptions(int index) {
         return this.optionsBuilder_ == null ? this.options_.get(index) : this.optionsBuilder_.getMessage(index);
      }

      public Type.Builder setOptions(int index, Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.set(index, value);
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(index, value);
         }

         return this;
      }

      public Type.Builder setOptions(int index, Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(index, builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(index, builderForValue.build());
         }

         return this;
      }

      public Type.Builder addOptions(Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(value);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(value);
         }

         return this;
      }

      public Type.Builder addOptions(int index, Option value) {
         if (this.optionsBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(index, value);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(index, value);
         }

         return this;
      }

      public Type.Builder addOptions(Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(builderForValue.build());
         }

         return this;
      }

      public Type.Builder addOptions(int index, Option.Builder builderForValue) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(index, builderForValue.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(index, builderForValue.build());
         }

         return this;
      }

      public Type.Builder addAllOptions(Iterable<? extends Option> values) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(values);
         }

         return this;
      }

      public Type.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -9;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Type.Builder removeOptions(int index) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.remove(index);
            this.onChanged();
         } else {
            this.optionsBuilder_.remove(index);
         }

         return this;
      }

      public Option.Builder getOptionsBuilder(int index) {
         return this.internalGetOptionsFieldBuilder().getBuilder(index);
      }

      @Override
      public OptionOrBuilder getOptionsOrBuilder(int index) {
         return this.optionsBuilder_ == null ? this.options_.get(index) : this.optionsBuilder_.getMessageOrBuilder(index);
      }

      @Override
      public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
         return this.optionsBuilder_ != null ? this.optionsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.options_);
      }

      public Option.Builder addOptionsBuilder() {
         return this.internalGetOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
      }

      public Option.Builder addOptionsBuilder(int index) {
         return this.internalGetOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
      }

      public List<Option.Builder> getOptionsBuilderList() {
         return this.internalGetOptionsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilder<Option, Option.Builder, OptionOrBuilder> internalGetOptionsFieldBuilder() {
         if (this.optionsBuilder_ == null) {
            this.optionsBuilder_ = new RepeatedFieldBuilder<>(this.options_, (this.bitField0_ & 8) != 0, this.getParentForChildren(), this.isClean());
            this.options_ = null;
         }

         return this.optionsBuilder_;
      }

      @Override
      public boolean hasSourceContext() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public SourceContext getSourceContext() {
         if (this.sourceContextBuilder_ == null) {
            return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
         } else {
            return this.sourceContextBuilder_.getMessage();
         }
      }

      public Type.Builder setSourceContext(SourceContext value) {
         if (this.sourceContextBuilder_ == null) {
            if (value == null) {
               throw new NullPointerException();
            }

            this.sourceContext_ = value;
         } else {
            this.sourceContextBuilder_.setMessage(value);
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public Type.Builder setSourceContext(SourceContext.Builder builderForValue) {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = builderForValue.build();
         } else {
            this.sourceContextBuilder_.setMessage(builderForValue.build());
         }

         this.bitField0_ |= 16;
         this.onChanged();
         return this;
      }

      public Type.Builder mergeSourceContext(SourceContext value) {
         if (this.sourceContextBuilder_ == null) {
            if ((this.bitField0_ & 16) != 0 && this.sourceContext_ != null && this.sourceContext_ != SourceContext.getDefaultInstance()) {
               this.getSourceContextBuilder().mergeFrom(value);
            } else {
               this.sourceContext_ = value;
            }
         } else {
            this.sourceContextBuilder_.mergeFrom(value);
         }

         if (this.sourceContext_ != null) {
            this.bitField0_ |= 16;
            this.onChanged();
         }

         return this;
      }

      public Type.Builder clearSourceContext() {
         this.bitField0_ &= -17;
         this.sourceContext_ = null;
         if (this.sourceContextBuilder_ != null) {
            this.sourceContextBuilder_.dispose();
            this.sourceContextBuilder_ = null;
         }

         this.onChanged();
         return this;
      }

      public SourceContext.Builder getSourceContextBuilder() {
         this.bitField0_ |= 16;
         this.onChanged();
         return this.internalGetSourceContextFieldBuilder().getBuilder();
      }

      @Override
      public SourceContextOrBuilder getSourceContextOrBuilder() {
         if (this.sourceContextBuilder_ != null) {
            return this.sourceContextBuilder_.getMessageOrBuilder();
         } else {
            return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
         }
      }

      private SingleFieldBuilder<SourceContext, SourceContext.Builder, SourceContextOrBuilder> internalGetSourceContextFieldBuilder() {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContextBuilder_ = new SingleFieldBuilder<>(this.getSourceContext(), this.getParentForChildren(), this.isClean());
            this.sourceContext_ = null;
         }

         return this.sourceContextBuilder_;
      }

      @Override
      public int getSyntaxValue() {
         return this.syntax_;
      }

      public Type.Builder setSyntaxValue(int value) {
         this.syntax_ = value;
         this.bitField0_ |= 32;
         this.onChanged();
         return this;
      }

      @Override
      public Syntax getSyntax() {
         Syntax result = Syntax.forNumber(this.syntax_);
         return result == null ? Syntax.UNRECOGNIZED : result;
      }

      public Type.Builder setSyntax(Syntax value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.bitField0_ |= 32;
            this.syntax_ = value.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Type.Builder clearSyntax() {
         this.bitField0_ &= -33;
         this.syntax_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public String getEdition() {
         Object ref = this.edition_;
         if (!(ref instanceof String)) {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.edition_ = s;
            return s;
         } else {
            return (String)ref;
         }
      }

      @Override
      public ByteString getEditionBytes() {
         Object ref = this.edition_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.edition_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      public Type.Builder setEdition(String value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            this.edition_ = value;
            this.bitField0_ |= 64;
            this.onChanged();
            return this;
         }
      }

      public Type.Builder clearEdition() {
         this.edition_ = Type.getDefaultInstance().getEdition();
         this.bitField0_ &= -65;
         this.onChanged();
         return this;
      }

      public Type.Builder setEditionBytes(ByteString value) {
         if (value == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(value);
            this.edition_ = value;
            this.bitField0_ |= 64;
            this.onChanged();
            return this;
         }
      }
   }
}
