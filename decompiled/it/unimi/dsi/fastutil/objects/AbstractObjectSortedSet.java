package it.unimi.dsi.fastutil.objects;

public abstract class AbstractObjectSortedSet<K> extends AbstractObjectSet<K> implements ObjectSortedSet<K> {
   protected AbstractObjectSortedSet() {
   }

   @Override
   public abstract ObjectBidirectionalIterator<K> iterator();
}
