package com.google.protobuf;

import java.util.List;

@CheckReturnValue
interface ListFieldSchema {
   <L> List<L> mutableListAt(Object msg, long offset);

   void makeImmutableListAt(Object msg, long offset);

   <L> void mergeListsAt(Object msg, Object otherMsg, long offset);
}
