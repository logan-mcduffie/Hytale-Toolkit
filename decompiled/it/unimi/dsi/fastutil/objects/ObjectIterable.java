package it.unimi.dsi.fastutil.objects;

public interface ObjectIterable<K> extends Iterable<K> {
   ObjectIterator<K> iterator();

   default ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
   }
}
