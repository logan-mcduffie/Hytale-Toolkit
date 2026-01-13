package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;

public interface LongIndirectPriorityQueue extends IndirectPriorityQueue<Long> {
   LongComparator comparator();
}
