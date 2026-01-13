package it.unimi.dsi.fastutil.objects;

public interface ObjectBidirectionalIterable<K> extends ObjectIterable<K> {
   ObjectBidirectionalIterator<K> iterator();
}
