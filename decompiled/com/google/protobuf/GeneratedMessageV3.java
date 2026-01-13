package com.google.protobuf;

import java.util.List;

@Deprecated
public abstract class GeneratedMessageV3 extends GeneratedMessage.ExtendableMessage<GeneratedMessageV3> {
   private static final long serialVersionUID = 1L;

   @Deprecated
   protected GeneratedMessageV3() {
   }

   @Deprecated
   protected GeneratedMessageV3(GeneratedMessageV3.Builder<?> builder) {
      super(builder);
   }

   protected static Internal.IntList newIntList() {
      return new IntArrayList();
   }

   protected static Internal.LongList newLongList() {
      return new LongArrayList();
   }

   protected static Internal.FloatList newFloatList() {
      return new FloatArrayList();
   }

   protected static Internal.DoubleList newDoubleList() {
      return new DoubleArrayList();
   }

   protected static Internal.BooleanList newBooleanList() {
      return new BooleanArrayList();
   }

   @Deprecated
   protected static Internal.IntList mutableCopy(Internal.IntList list) {
      return makeMutableCopy(list);
   }

   @Deprecated
   protected static Internal.LongList mutableCopy(Internal.LongList list) {
      return makeMutableCopy(list);
   }

   @Deprecated
   protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
      return makeMutableCopy(list);
   }

   @Deprecated
   protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
      return makeMutableCopy(list);
   }

   @Deprecated
   protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
      return makeMutableCopy(list);
   }

   @Deprecated
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      throw new UnsupportedOperationException("Should be overridden in gencode.");
   }

   @Deprecated
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
      throw new UnsupportedOperationException("This method must be overridden by the subclass.");
   }

   @Deprecated
   protected abstract Message.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent);

   @Deprecated
   @Override
   protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
      return this.newBuilderForType(new GeneratedMessageV3.BuilderParent() {
         @Override
         public void markDirty() {
            parent.markDirty();
         }
      });
   }

   @Deprecated
   public abstract static class Builder<BuilderT extends GeneratedMessageV3.Builder<BuilderT>>
      extends GeneratedMessage.ExtendableBuilder<GeneratedMessageV3, BuilderT> {
      private GeneratedMessageV3.Builder<BuilderT>.BuilderParentImpl meAsParent;

      @Deprecated
      protected Builder() {
         super(null);
      }

      @Deprecated
      protected Builder(GeneratedMessageV3.BuilderParent builderParent) {
         super(builderParent);
      }

      @Deprecated
      public BuilderT clone() {
         return super.clone();
      }

      @Deprecated
      public BuilderT clear() {
         return super.clear();
      }

      @Deprecated
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         throw new UnsupportedOperationException("Should be overridden in gencode.");
      }

      @Deprecated
      public BuilderT setField(final Descriptors.FieldDescriptor field, final Object value) {
         return super.setField(field, value);
      }

      @Deprecated
      public BuilderT clearField(final Descriptors.FieldDescriptor field) {
         return super.clearField(field);
      }

      @Deprecated
      public BuilderT clearOneof(final Descriptors.OneofDescriptor oneof) {
         return super.clearOneof(oneof);
      }

      @Deprecated
      public BuilderT setRepeatedField(final Descriptors.FieldDescriptor field, final int index, final Object value) {
         return super.setRepeatedField(field, index, value);
      }

      @Deprecated
      public BuilderT addRepeatedField(final Descriptors.FieldDescriptor field, final Object value) {
         return super.addRepeatedField(field, value);
      }

      @Deprecated
      public BuilderT setUnknownFields(final UnknownFieldSet unknownFields) {
         return super.setUnknownFields(unknownFields);
      }

      @Deprecated
      public BuilderT mergeUnknownFields(final UnknownFieldSet unknownFields) {
         return super.mergeUnknownFields(unknownFields);
      }

      @Deprecated
      protected GeneratedMessageV3.BuilderParent getParentForChildren() {
         if (this.meAsParent == null) {
            this.meAsParent = new GeneratedMessageV3.Builder.BuilderParentImpl();
         }

         return this.meAsParent;
      }

      @Deprecated
      private class BuilderParentImpl implements GeneratedMessageV3.BuilderParent {
         private BuilderParentImpl() {
         }

         @Override
         public void markDirty() {
            Builder.this.onChanged();
         }
      }
   }

   @Deprecated
   protected interface BuilderParent extends AbstractMessage.BuilderParent {
   }

   @Deprecated
   public abstract static class ExtendableBuilder<MessageT extends GeneratedMessageV3.ExtendableMessage<MessageT>, BuilderT extends GeneratedMessageV3.ExtendableBuilder<MessageT, BuilderT>>
      extends GeneratedMessageV3.Builder<BuilderT>
      implements GeneratedMessageV3.ExtendableMessageOrBuilder<MessageT> {
      @Deprecated
      protected ExtendableBuilder() {
      }

      @Deprecated
      protected ExtendableBuilder(GeneratedMessageV3.BuilderParent parent) {
         super(parent);
      }

      @Deprecated
      public <T> BuilderT setExtension(final GeneratedMessage.GeneratedExtension<MessageT, T> extension, final T value) {
         return this.setExtension(extension, value);
      }

      @Deprecated
      public <T> BuilderT setExtension(final GeneratedMessage.GeneratedExtension<MessageT, List<T>> extension, final int index, final T value) {
         return this.setExtension(extension, index, value);
      }

      @Deprecated
      public <T> BuilderT addExtension(final GeneratedMessage.GeneratedExtension<MessageT, List<T>> extension, final T value) {
         return this.addExtension(extension, value);
      }

      @Deprecated
      public <T> BuilderT clearExtension(final GeneratedMessage.GeneratedExtension<MessageT, T> extension) {
         return this.clearExtension(extension);
      }

      @Deprecated
      public BuilderT setField(final Descriptors.FieldDescriptor field, final Object value) {
         return super.setField(field, value);
      }

      @Deprecated
      public BuilderT clearField(final Descriptors.FieldDescriptor field) {
         return super.clearField(field);
      }

      @Deprecated
      public BuilderT clearOneof(final Descriptors.OneofDescriptor oneof) {
         return super.clearOneof(oneof);
      }

      @Deprecated
      public BuilderT setRepeatedField(final Descriptors.FieldDescriptor field, final int index, final Object value) {
         return super.setRepeatedField(field, index, value);
      }

      @Deprecated
      public BuilderT addRepeatedField(final Descriptors.FieldDescriptor field, final Object value) {
         return super.addRepeatedField(field, value);
      }

      @Deprecated
      protected final void mergeExtensionFields(final GeneratedMessageV3.ExtendableMessage<?> other) {
         super.mergeExtensionFields(other);
      }
   }

   @Deprecated
   public abstract static class ExtendableMessage<MessageT extends GeneratedMessageV3.ExtendableMessage<MessageT>>
      extends GeneratedMessageV3
      implements GeneratedMessageV3.ExtendableMessageOrBuilder<MessageT> {
      @Deprecated
      protected ExtendableMessage() {
      }

      @Deprecated
      protected ExtendableMessage(GeneratedMessageV3.ExtendableBuilder<MessageT, ?> builder) {
         super(builder);
      }

      @Deprecated
      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         throw new UnsupportedOperationException("Should be overridden in gencode.");
      }

      @Deprecated
      protected GeneratedMessageV3.ExtendableMessage<MessageT>.ExtensionWriter newExtensionWriter() {
         return new GeneratedMessageV3.ExtendableMessage.ExtensionWriter(false);
      }

      @Deprecated
      protected GeneratedMessageV3.ExtendableMessage<MessageT>.ExtensionWriter newMessageSetExtensionWriter() {
         return new GeneratedMessageV3.ExtendableMessage.ExtensionWriter(true);
      }

      @Deprecated
      protected class ExtensionWriter extends GeneratedMessage.ExtendableMessage.ExtensionWriter {
         private ExtensionWriter(final boolean messageSetWireFormat) {
            super(messageSetWireFormat);
         }
      }
   }

   @Deprecated
   public interface ExtendableMessageOrBuilder<MessageT extends GeneratedMessageV3.ExtendableMessage<MessageT>>
      extends GeneratedMessage.ExtendableMessageOrBuilder<GeneratedMessageV3> {
   }

   @Deprecated
   public static final class FieldAccessorTable extends GeneratedMessage.FieldAccessorTable {
      @Deprecated
      public FieldAccessorTable(
         final Descriptors.Descriptor descriptor,
         final String[] camelCaseNames,
         final Class<? extends GeneratedMessageV3> messageClass,
         final Class<? extends GeneratedMessageV3.Builder<?>> builderClass
      ) {
         super(descriptor, camelCaseNames, messageClass, builderClass);
      }

      @Deprecated
      public FieldAccessorTable(final Descriptors.Descriptor descriptor, final String[] camelCaseNames) {
         super(descriptor, camelCaseNames);
      }

      @Deprecated
      public GeneratedMessageV3.FieldAccessorTable ensureFieldAccessorsInitialized(
         Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder<?>> builderClass
      ) {
         return (GeneratedMessageV3.FieldAccessorTable)super.ensureFieldAccessorsInitialized(messageClass, builderClass);
      }
   }

   @Deprecated
   protected static final class UnusedPrivateParameter {
      static final GeneratedMessageV3.UnusedPrivateParameter INSTANCE = new GeneratedMessageV3.UnusedPrivateParameter();

      private UnusedPrivateParameter() {
      }
   }
}
