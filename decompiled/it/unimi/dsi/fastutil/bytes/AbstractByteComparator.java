package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;

@Deprecated
public abstract class AbstractByteComparator implements ByteComparator, Serializable {
   private static final long serialVersionUID = 0L;

   protected AbstractByteComparator() {
   }
}
