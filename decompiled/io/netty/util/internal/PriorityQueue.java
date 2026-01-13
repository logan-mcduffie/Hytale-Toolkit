package io.netty.util.internal;

import java.util.Queue;

public interface PriorityQueue<T> extends Queue<T> {
   boolean removeTyped(T var1);

   boolean containsTyped(T var1);

   void priorityChanged(T var1);

   void clearIgnoringIndexes();
}
