package com.google.protobuf;

public class SingleFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
   implements AbstractMessage.BuilderParent {
   private AbstractMessage.BuilderParent parent;
   private BType builder;
   private MType message;
   private boolean isClean;

   public SingleFieldBuilder(MType message, AbstractMessage.BuilderParent parent, boolean isClean) {
      this.message = Internal.checkNotNull(message);
      this.parent = parent;
      this.isClean = isClean;
   }

   public void dispose() {
      this.parent = null;
   }

   public MType getMessage() {
      if (this.message == null) {
         this.message = (MType)this.builder.buildPartial();
      }

      return this.message;
   }

   public MType build() {
      this.isClean = true;
      return this.getMessage();
   }

   public BType getBuilder() {
      BType builder = this.builder;
      if (builder == null) {
         this.builder = builder = (BType)this.message.newBuilderForType(this);
         builder.mergeFrom(this.message);
         builder.markClean();
      }

      return builder;
   }

   public IType getMessageOrBuilder() {
      return (IType)(this.builder != null ? this.builder : this.message);
   }

   @CanIgnoreReturnValue
   public SingleFieldBuilder<MType, BType, IType> setMessage(MType message) {
      this.message = Internal.checkNotNull(message);
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      return this;
   }

   @CanIgnoreReturnValue
   public SingleFieldBuilder<MType, BType, IType> mergeFrom(MType value) {
      if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
         this.message = value;
      } else {
         this.getBuilder().mergeFrom(value);
      }

      this.onChanged();
      return this;
   }

   @CanIgnoreReturnValue
   public SingleFieldBuilder<MType, BType, IType> clear() {
      this.message = (MType)(this.message != null ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      this.isClean = true;
      return this;
   }

   private void onChanged() {
      if (this.builder != null) {
         this.message = null;
      }

      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }
   }

   @Override
   public void markDirty() {
      this.onChanged();
   }
}
