package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;

public interface DoubleIndirectPriorityQueue extends IndirectPriorityQueue<Double> {
   DoubleComparator comparator();
}
