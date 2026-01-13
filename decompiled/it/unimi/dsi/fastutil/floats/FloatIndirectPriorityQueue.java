package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;

public interface FloatIndirectPriorityQueue extends IndirectPriorityQueue<Float> {
   FloatComparator comparator();
}
