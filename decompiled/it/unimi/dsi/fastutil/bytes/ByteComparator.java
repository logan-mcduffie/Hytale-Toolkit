package it.unimi.dsi.fastutil.bytes;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface ByteComparator extends Comparator<Byte> {
   int compare(byte var1, byte var2);

   default ByteComparator reversed() {
      return ByteComparators.oppositeComparator(this);
   }

   @Deprecated
   default int compare(Byte ok1, Byte ok2) {
      return this.compare(ok1.byteValue(), ok2.byteValue());
   }

   default ByteComparator thenComparing(ByteComparator second) {
      return (ByteComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   @Override
   default Comparator<Byte> thenComparing(Comparator<? super Byte> second) {
      return (Comparator<Byte>)(second instanceof ByteComparator ? this.thenComparing((ByteComparator)second) : Comparator.super.thenComparing(second));
   }
}
