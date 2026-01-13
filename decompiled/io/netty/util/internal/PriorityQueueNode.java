package io.netty.util.internal;

public interface PriorityQueueNode {
   int INDEX_NOT_IN_QUEUE = -1;

   int priorityQueueIndex(DefaultPriorityQueue<?> var1);

   void priorityQueueIndex(DefaultPriorityQueue<?> var1, int var2);
}
