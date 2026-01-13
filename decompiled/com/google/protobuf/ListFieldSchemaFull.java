package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CheckReturnValue
final class ListFieldSchemaFull implements ListFieldSchema {
   private static final Class<?> UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(Collections.emptyList()).getClass();

   @Override
   public <L> List<L> mutableListAt(Object message, long offset) {
      return mutableListAt(message, offset, 10);
   }

   private static <L> List<L> mutableListAt(Object message, long offset, int additionalCapacity) {
      List<L> list = getList(message, offset);
      if (list.isEmpty()) {
         if (list instanceof LazyStringList) {
            list = new LazyStringArrayList(additionalCapacity);
         } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
            list = ((Internal.ProtobufList)list).mutableCopyWithCapacity(additionalCapacity);
         } else {
            list = new ArrayList<>(additionalCapacity);
         }

         UnsafeUtil.putObject(message, offset, list);
      } else if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
         ArrayList<L> newList = new ArrayList<>(list.size() + additionalCapacity);
         newList.addAll(list);
         list = newList;
         UnsafeUtil.putObject(message, offset, newList);
      } else if (list instanceof UnmodifiableLazyStringList) {
         LazyStringArrayList newList = new LazyStringArrayList(list.size() + additionalCapacity);
         newList.addAll(list);
         list = newList;
         UnsafeUtil.putObject(message, offset, newList);
      } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList && !((Internal.ProtobufList)list).isModifiable()) {
         list = ((Internal.ProtobufList)list).mutableCopyWithCapacity(list.size() + additionalCapacity);
         UnsafeUtil.putObject(message, offset, list);
      }

      return list;
   }

   @Override
   public void makeImmutableListAt(Object message, long offset) {
      List<?> list = (List<?>)UnsafeUtil.getObject(message, offset);
      Object immutable = null;
      if (list instanceof LazyStringList) {
         immutable = ((LazyStringList)list).getUnmodifiableView();
      } else {
         if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
            return;
         }

         if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
            if (((Internal.ProtobufList)list).isModifiable()) {
               ((Internal.ProtobufList)list).makeImmutable();
            }

            return;
         }

         immutable = Collections.unmodifiableList(list);
      }

      UnsafeUtil.putObject(message, offset, immutable);
   }

   @Override
   public <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
      List<E> other = getList(otherMsg, offset);
      List<E> mine = mutableListAt(msg, offset, other.size());
      int size = mine.size();
      int otherSize = other.size();
      if (size > 0 && otherSize > 0) {
         mine.addAll(other);
      }

      List<E> merged = size > 0 ? mine : other;
      UnsafeUtil.putObject(msg, offset, merged);
   }

   static <E> List<E> getList(Object message, long offset) {
      return (List<E>)UnsafeUtil.getObject(message, offset);
   }
}
