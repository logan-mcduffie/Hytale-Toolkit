package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;

public interface IntIndirectPriorityQueue extends IndirectPriorityQueue<Integer> {
   IntComparator comparator();
}
