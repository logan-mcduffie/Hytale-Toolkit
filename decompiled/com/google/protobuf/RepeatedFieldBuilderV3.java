package com.google.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

@Deprecated
public class RepeatedFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
   implements AbstractMessage.BuilderParent {
   private AbstractMessage.BuilderParent parent;
   private List<MType> messages;
   private boolean isMessagesListMutable;
   private List<SingleFieldBuilderV3<MType, BType, IType>> builders;
   private boolean isClean;
   private RepeatedFieldBuilderV3.MessageExternalList<MType, BType, IType> externalMessageList;
   private RepeatedFieldBuilderV3.BuilderExternalList<MType, BType, IType> externalBuilderList;
   private RepeatedFieldBuilderV3.MessageOrBuilderExternalList<MType, BType, IType> externalMessageOrBuilderList;

   @Deprecated
   public RepeatedFieldBuilderV3(List<MType> messages, boolean isMessagesListMutable, AbstractMessage.BuilderParent parent, boolean isClean) {
      this.messages = messages;
      this.isMessagesListMutable = isMessagesListMutable;
      this.parent = parent;
      this.isClean = isClean;
   }

   @Deprecated
   public void dispose() {
      this.parent = null;
   }

   private void ensureMutableMessageList() {
      if (!this.isMessagesListMutable) {
         this.messages = new ArrayList<>(this.messages);
         this.isMessagesListMutable = true;
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

   @Deprecated
   public int getCount() {
      return this.messages.size();
   }

   @Deprecated
   public boolean isEmpty() {
      return this.messages.isEmpty();
   }

   @Deprecated
   public MType getMessage(int index) {
      return this.getMessage(index, false);
   }

   private MType getMessage(int index, boolean forBuild) {
      if (this.builders == null) {
         return this.messages.get(index);
      } else {
         SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(index);
         if (builder == null) {
            return this.messages.get(index);
         } else {
            return forBuild ? builder.build() : builder.getMessage();
         }
      }
   }

   @Deprecated
   public BType getBuilder(int index) {
      this.ensureBuilders();
      SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(index);
      if (builder == null) {
         MType message = this.messages.get(index);
         builder = new SingleFieldBuilderV3<>(message, this, this.isClean);
         this.builders.set(index, builder);
      }

      return builder.getBuilder();
   }

   @Deprecated
   public IType getMessageOrBuilder(int index) {
      if (this.builders == null) {
         return (IType)this.messages.get(index);
      } else {
         SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(index);
         return (IType)(builder == null ? this.messages.get(index) : builder.getMessageOrBuilder());
      }
   }

   @Deprecated
   @CanIgnoreReturnValue
   public RepeatedFieldBuilderV3<MType, BType, IType> setMessage(int index, MType message) {
      Internal.checkNotNull(message);
      this.ensureMutableMessageList();
      this.messages.set(index, message);
      if (this.builders != null) {
         SingleFieldBuilderV3<MType, BType, IType> entry = this.builders.set(index, null);
         if (entry != null) {
            entry.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   @Deprecated
   @CanIgnoreReturnValue
   public RepeatedFieldBuilderV3<MType, BType, IType> addMessage(MType message) {
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

   @Deprecated
   @CanIgnoreReturnValue
   public RepeatedFieldBuilderV3<MType, BType, IType> addMessage(int index, MType message) {
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

   @Deprecated
   @CanIgnoreReturnValue
   public RepeatedFieldBuilderV3<MType, BType, IType> addAllMessages(Iterable<? extends MType> values) {
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

   @Deprecated
   public BType addBuilder(MType message) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilderV3<MType, BType, IType> builder = new SingleFieldBuilderV3<>(message, this, this.isClean);
      this.messages.add(null);
      this.builders.add(builder);
      this.onChanged();
      this.incrementModCounts();
      return builder.getBuilder();
   }

   @Deprecated
   public BType addBuilder(int index, MType message) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilderV3<MType, BType, IType> builder = new SingleFieldBuilderV3<>(message, this, this.isClean);
      this.messages.add(index, null);
      this.builders.add(index, builder);
      this.onChanged();
      this.incrementModCounts();
      return builder.getBuilder();
   }

   @Deprecated
   public void remove(int index) {
      this.ensureMutableMessageList();
      this.messages.remove(index);
      if (this.builders != null) {
         SingleFieldBuilderV3<MType, BType, IType> entry = this.builders.remove(index);
         if (entry != null) {
            entry.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
   }

   @Deprecated
   public void clear() {
      this.messages = Collections.emptyList();
      this.isMessagesListMutable = false;
      if (this.builders != null) {
         for (SingleFieldBuilderV3<MType, BType, IType> entry : this.builders) {
            if (entry != null) {
               entry.dispose();
            }
         }

         this.builders = null;
      }

      this.onChanged();
      this.incrementModCounts();
   }

   @Deprecated
   public List<MType> build() {
      this.isClean = true;
      if (!this.isMessagesListMutable && this.builders == null) {
         return this.messages;
      } else {
         boolean allMessagesInSync = true;
         if (!this.isMessagesListMutable) {
            for (int i = 0; i < this.messages.size(); i++) {
               Message message = this.messages.get(i);
               SingleFieldBuilderV3<MType, BType, IType> builder = this.builders.get(i);
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

         this.messages = Collections.unmodifiableList(this.messages);
         this.isMessagesListMutable = false;
         return this.messages;
      }
   }

   @Deprecated
   public List<MType> getMessageList() {
      if (this.externalMessageList == null) {
         this.externalMessageList = new RepeatedFieldBuilderV3.MessageExternalList<>(this);
      }

      return this.externalMessageList;
   }

   @Deprecated
   public List<BType> getBuilderList() {
      if (this.externalBuilderList == null) {
         this.externalBuilderList = new RepeatedFieldBuilderV3.BuilderExternalList<>(this);
      }

      return this.externalBuilderList;
   }

   @Deprecated
   public List<IType> getMessageOrBuilderList() {
      if (this.externalMessageOrBuilderList == null) {
         this.externalMessageOrBuilderList = new RepeatedFieldBuilderV3.MessageOrBuilderExternalList<>(this);
      }

      return this.externalMessageOrBuilderList;
   }

   private void onChanged() {
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

   private static class BuilderExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<BType>
      implements List<BType>,
      RandomAccess {
      RepeatedFieldBuilderV3<MType, BType, IType> builder;

      @Deprecated
      BuilderExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
         this.builder = builder;
      }

      @Deprecated
      @Override
      public int size() {
         return this.builder.getCount();
      }

      @Deprecated
      public BType get(int index) {
         return this.builder.getBuilder(index);
      }

      @Deprecated
      void incrementModCount() {
         this.modCount++;
      }
   }

   private static class MessageExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<MType>
      implements List<MType>,
      RandomAccess {
      RepeatedFieldBuilderV3<MType, BType, IType> builder;

      @Deprecated
      MessageExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
         this.builder = builder;
      }

      @Deprecated
      @Override
      public int size() {
         return this.builder.getCount();
      }

      @Deprecated
      public MType get(int index) {
         return this.builder.getMessage(index);
      }

      @Deprecated
      void incrementModCount() {
         this.modCount++;
      }
   }

   private static class MessageOrBuilderExternalList<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<IType>
      implements List<IType>,
      RandomAccess {
      RepeatedFieldBuilderV3<MType, BType, IType> builder;

      MessageOrBuilderExternalList(RepeatedFieldBuilderV3<MType, BType, IType> builder) {
         this.builder = builder;
      }

      @Deprecated
      @Override
      public int size() {
         return this.builder.getCount();
      }

      @Deprecated
      public IType get(int index) {
         return this.builder.getMessageOrBuilder(index);
      }

      @Deprecated
      void incrementModCount() {
         this.modCount++;
      }
   }
}
