package com.google.protobuf;

@Deprecated
public class SingleFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
   implements AbstractMessage.BuilderParent {
   private AbstractMessage.BuilderParent parent;
   private BType builder;
   private MType message;
   private boolean isClean;

   @Deprecated
   public SingleFieldBuilderV3(MType message, AbstractMessage.BuilderParent parent, boolean isClean) {
      this.message = Internal.checkNotNull(message);
      this.parent = parent;
      this.isClean = isClean;
   }

   @Deprecated
   public void dispose() {
      this.parent = null;
   }

   @Deprecated
   public MType getMessage() {
      if (this.message == null) {
         this.message = (MType)this.builder.buildPartial();
      }

      return this.message;
   }

   @Deprecated
   public MType build() {
      this.isClean = true;
      return this.getMessage();
   }

   @Deprecated
   public BType getBuilder() {
      if (this.builder == null) {
         this.builder = (BType)this.message.newBuilderForType(this);
         this.builder.mergeFrom(this.message);
         this.builder.markClean();
      }

      return this.builder;
   }

   @Deprecated
   public IType getMessageOrBuilder() {
      return (IType)(this.builder != null ? this.builder : this.message);
   }

   @Deprecated
   @CanIgnoreReturnValue
   public SingleFieldBuilderV3<MType, BType, IType> setMessage(MType message) {
      this.message = Internal.checkNotNull(message);
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      return this;
   }

   @Deprecated
   @CanIgnoreReturnValue
   public SingleFieldBuilderV3<MType, BType, IType> mergeFrom(MType value) {
      if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
         this.message = value;
      } else {
         this.getBuilder().mergeFrom(value);
      }

      this.onChanged();
      return this;
   }

   @Deprecated
   @CanIgnoreReturnValue
   public SingleFieldBuilderV3<MType, BType, IType> clear() {
      this.message = (MType)(this.message != null ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      this.isClean = true;
      return this;
   }

   @Deprecated
   private void onChanged() {
      if (this.builder != null) {
         this.message = null;
      }

      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }
   }

   @Deprecated
   @Override
   public void markDirty() {
      this.onChanged();
   }
}
