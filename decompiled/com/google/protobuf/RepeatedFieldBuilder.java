package com.google.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public class RepeatedFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
   implements AbstractMessage.BuilderParent {
   private AbstractMessage.BuilderParent parent;
   private Internal.ProtobufList<MType> messages;
   private List<SingleFieldBuilder<MType, BType, IType>> builders;
   private boolean isClean;
   private RepeatedFieldBuilder.MessageExternalList<MType, BType, IType> externalMessageList;
   private RepeatedFieldBuilder.BuilderExternalList<MType, BType, IType> externalBuilderList;
   private RepeatedFieldBuilder.MessageOrBuilderExternalList<MType, BType, IType> externalMessageOrBuilderList;

   private static <MsgT extends GeneratedMessage> Internal.ProtobufList<MsgT> passthroughOrCopyToProtobufList(List<MsgT> messages) {
      if (messages instanceof Internal.ProtobufList) {
         return (Internal.ProtobufList<MsgT>)messages;
      } else {
         ProtobufArrayList<MsgT> copy = ProtobufArrayList.<MsgT>emptyList().mutableCopyWithCapacity(messages.size());
         copy.addAll(messages);
         return copy;
      }
   }

   public RepeatedFieldBuilder(List<MType> messages, boolean isMessagesListMutable, AbstractMessage.BuilderParent parent, boolean isClean) {
      this.messages = passthroughOrCopyToProtobufList(messages);
      this.parent = parent;
      this.isClean = isClean;
   }

   public void dispose() {
      this.parent = null;
   }

   private void ensureMutableMessageList() {
      if (!this.messages.isModifiable()) {
         this.messages = this.messages.mutableCopyWithCapacity(this.messages.size());
      }
   }

   private void ensureBuilders() {
      if (this.builders == null) {
         this.builders = new ArrayList<>(this.messages.size());

         for (int i = 0; i < this.messages.size(); i++) {
            this.builders.add(null);
         }
      }
   }

   public int getCount() {
      return this.messages.size();
   }

   public boolean isEmpty() {
      return this.messages.isEmpty();
   }

   public MType getMessage(int index) {
      return this.getMessage(index, false);
   }

   private MType getMessage(int index, boolean forBuild) {
      if (this.builders == null) {
         return this.messages.get(index);
      } else {
         SingleFieldBuilder<MType, BType, IType> builder = this.builders.get(index);
         if (builder == null) {
            return this.messages.get(index);
         } else {
            return forBuild ? builder.build() : builder.getMessage();
         }
      }
   }

   public BType getBuilder(int index) {
      this.ensureBuilders();
      SingleFieldBuilder<MType, BType, IType> builder = this.builders.get(index);
      if (builder == null) {
         MType message = this.messages.get(index);
         builder = new SingleFieldBuilder<>(message, this, this.isClean);
         this.builders.set(index, builder);
      }

      return builder.getBuilder();
   }

   public IType getMessageOrBuilder(int index) {
      if (this.builders == null) {
         return (IType)this.messages.get(index);
      } else {
         SingleFieldBuilder<MType, BType, IType> builder = this.builders.get(index);
         return (IType)(builder == null ? this.messages.get(index) : builder.getMessageOrBuilder());
      }
   }

   @CanIgnoreReturnValue
   public RepeatedFieldBuilder<MType, BType, IType> setMessage(int index, MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.set(index, message);
      if (this.builders != null) {
         SingleFieldBuilder<MType, BType, IType> entry = this.builders.set(index, null);
         if (entry != null) {
            entry.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   @CanIgnoreReturnValue
   public RepeatedFieldBuilder<MType, BType, IType> addMessage(MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.add(message);
      if (this.builders != null) {
         this.builders.add(null);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   @CanIgnoreReturnValue
   public RepeatedFieldBuilder<MType, BType, IType> addMessage(int index, MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.add(index, message);
      if (this.builders != null) {
         this.builders.add(index, null);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   @CanIgnoreReturnValue
   public RepeatedFieldBuilder<MType, BType, IType> addAllMessages(Iterable<? extends MType> values) {
      for (MType value : values) {
         Internal.checkNotNull(value);
      }

      int size = -1;
      if (values instanceof Collection) {
         Collection<?> collection = (Collection<?>)values;
         if (collection.isEmpty()) {
            return this;
         }

         size = collection.size();
      }

      this.ensureMutableMessageList();
      if (size >= 0 && this.messages instanceof ArrayList) {
         ((ArrayList)this.messages).ensureCapacity(this.messages.size() + size);
      }

      for (MType value : values) {
         this.addMessage(value);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public BType addBuilder(MType message) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilder<MType, BType, IType> builder = new SingleFieldBuilder<>(message, this, this.isClean);
      this.messages.add(null);
      this.builders.add(builder);
      this.onChanged();
      this.incrementModCounts();
      return builder.getBuilder();
   }

   public BType addBuilder(int index, MType message) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilder<MType, BType, IType> builder = new SingleFieldBuilder<>(message, this, this.isClean);
      this.messages.add(index, null);
      this.builders.add(index, builder);
      this.onChanged();
      this.incrementModCounts();
      return builder.getBuilder();
   }

   public void remove(int index) {
      this.ensureMutableMessageList();
      this.messages.remove(index);
      if (this.builders != null) {
         SingleFieldBuilder<MType, BType, IType> entry = this.builders.remove(index);
         if (entry != null) {
            entry.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
   }

   public void clear() {
      this.messages = ProtobufArrayList.emptyList();
      if (this.builders != null) {
         for (SingleFieldBuilder<MType, BType, IType> entry : this.builders) {
            if (entry != null) {
               entry.dispose();
            }
         }

         this.builders = null;
      }

      this.onChanged();
      this.incrementModCounts();
   }

   public List<MType> build() {
      this.isClean = true;
      if (!this.messages.isModifiable() && this.builders == null) {
         return this.messages;
      } else {
         boolean allMessagesInSync = true;
         if (!this.messages.isModifiable()) {
            for (int i = 0; i < this.messages.size(); i++) {
               Message message = this.messages.get(i);
               SingleFieldBuilder<MType, BType, IType> builder = this.builders.get(i);
               if (builder != null && builder.build() != message) {
                  allMessagesInSync = false;
                  break;
               }
            }

            if (allMessagesInSync) {
               return this.messages;
            }
         }

         this.ensureMutableMessageList();

         for (int ix = 0; ix < this.messages.size(); ix++) {
            this.messages.set(ix, this.getMessage(ix, true));
         }

         this.messages.makeImmutable();
         return this.messages;
      }
   }

   public List<MType> getMessageList() {
      if (this.externalMessageList == null) {
         this.externalMessageList = new RepeatedFieldBuilder.MessageExternalList<>(this);
      }

      return this.externalMessageList;
   }

   public List<BType> getBuilderList() {
      if (this.externalBuilderList == null) {
         this.externalBuilderList = new RepeatedFieldBuilder.BuilderExternalList<>(this);
      }

      return this.externalBuilderList;
   }

   public List<IType> getMessageOrBuilderList() {
      if (this.externalMessageOrBuilderList == null) {
         this.externalMessageOrBuilderList = new RepeatedFieldBuilder.MessageOrBuilderExternalList<>(this);
      }

      return this.externalMessageOrBuilderList;
   }

   private void onChanged() {
      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }
   }

   @Override
   public void markDirty() {
      this.onChanged();
   }

   private void incrementModCounts() {
      if (this.externalMessageList != null) {
         this.externalMessageList.incrementModCount();
      }

      if (this.externalBuilderList != null) {
         this.externalBuilderList.incrementModCount();
      }

      if (this.externalMessageOrBuilderList != null) {
         this.externalMessageOrBuilderList.incrementModCount();
      }
   }

   private static class BuilderExternalList<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<BType>
      implements List<BType>,
      RandomAccess {
      RepeatedFieldBuilder<MType, BType, IType> builder;

      BuilderExternalList(RepeatedFieldBuilder<MType, BType, IType> builder) {
         this.builder = builder;
      }

      @Override
      public int size() {
         return this.builder.getCount();
      }

      public BType get(int index) {
         return this.builder.getBuilder(index);
      }

      void incrementModCount() {
         this.modCount++;
      }
   }

   private static class MessageExternalList<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<MType>
      implements List<MType>,
      RandomAccess {
      RepeatedFieldBuilder<MType, BType, IType> builder;

      MessageExternalList(RepeatedFieldBuilder<MType, BType, IType> builder) {
         this.builder = builder;
      }

      @Override
      public int size() {
         return this.builder.getCount();
      }

      public MType get(int index) {
         return this.builder.getMessage(index);
      }

      void incrementModCount() {
         this.modCount++;
      }
   }

   private static class MessageOrBuilderExternalList<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<IType>
      implements List<IType>,
      RandomAccess {
      RepeatedFieldBuilder<MType, BType, IType> builder;

      MessageOrBuilderExternalList(RepeatedFieldBuilder<MType, BType, IType> builder) {
         this.builder = builder;
      }

      @Override
      public int size() {
         return this.builder.getCount();
      }

      public IType get(int index) {
         return this.builder.getMessageOrBuilder(index);
      }

      void incrementModCount() {
         this.modCount++;
      }
   }
}
