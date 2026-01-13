package com.google.protobuf;

import java.util.List;

final class ListFieldSchemaLite implements ListFieldSchema {
   @Override
   public <L> List<L> mutableListAt(Object message, long offset) {
      Internal.ProtobufList<L> list = getProtobufList(message, offset);
      if (!list.isModifiable()) {
         int size = list.size();
         list = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
         UnsafeUtil.putObject(message, offset, list);
      }

      return list;
   }

   @Override
   public void makeImmutableListAt(Object message, long offset) {
      Internal.ProtobufList<?> list = getProtobufList(message, offset);
      list.makeImmutable();
   }

   @Override
   public <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
      Internal.ProtobufList<E> mine = getProtobufList(msg, offset);
      Internal.ProtobufList<E> other = getProtobufList(otherMsg, offset);
      int size = mine.size();
      int otherSize = other.size();
      if (size > 0 && otherSize > 0) {
         if (!mine.isModifiable()) {
            mine = mine.mutableCopyWithCapacity(size + otherSize);
         }

         mine.addAll(other);
      }

      Internal.ProtobufList<E> merged = size > 0 ? mine : other;
      UnsafeUtil.putObject(msg, offset, merged);
   }

   static <E> Internal.ProtobufList<E> getProtobufList(Object message, long offset) {
      return (Internal.ProtobufList<E>)UnsafeUtil.getObject(message, offset);
   }
}
