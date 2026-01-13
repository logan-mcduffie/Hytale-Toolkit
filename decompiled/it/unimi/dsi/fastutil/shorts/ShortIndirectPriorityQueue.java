package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;

public interface ShortIndirectPriorityQueue extends IndirectPriorityQueue<Short> {
   ShortComparator comparator();
}
