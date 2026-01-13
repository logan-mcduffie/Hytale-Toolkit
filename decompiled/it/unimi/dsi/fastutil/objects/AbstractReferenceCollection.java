package it.unimi.dsi.fastutil.objects;

import java.util.AbstractCollection;

public abstract class AbstractReferenceCollection<K> extends AbstractCollection<K> implements ReferenceCollection<K> {
   protected AbstractReferenceCollection() {
   }

   @Override
   public abstract ObjectIterator<K> iterator();

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<K> i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("{");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Object k = i.next();
         if (this == k) {
            s.append("(this collection)");
         } else {
            s.append(String.valueOf(k));
         }
      }

      s.append("}");
      return s.toString();
   }
}
