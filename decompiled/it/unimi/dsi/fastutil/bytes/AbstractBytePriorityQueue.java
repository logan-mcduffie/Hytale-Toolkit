package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.AbstractPriorityQueue;
import java.io.Serializable;

@Deprecated
public abstract class AbstractBytePriorityQueue extends AbstractPriorityQueue<Byte> implements Serializable, BytePriorityQueue {
   private static final long serialVersionUID = 1L;
}
