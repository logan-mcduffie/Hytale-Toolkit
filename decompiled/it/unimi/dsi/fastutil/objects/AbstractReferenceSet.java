package it.unimi.dsi.fastutil.objects;

import java.util.Set;

public abstract class AbstractReferenceSet<K> extends AbstractReferenceCollection<K> implements Cloneable, ReferenceSet<K> {
   protected AbstractReferenceSet() {
   }

   @Override
   public abstract ObjectIterator<K> iterator();

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Set)) {
         return false;
      } else {
         Set<?> s = (Set<?>)o;
         return s.size() != this.size() ? false : this.containsAll(s);
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int n = this.size();
      ObjectIterator<K> i = this.iterator();

      while (n-- != 0) {
         K k = i.next();
         h += System.identityHashCode(k);
      }

      return h;
   }
}
