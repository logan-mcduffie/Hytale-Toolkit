package org.bson.codecs;

import org.bson.BsonValue;

public interface CollectibleCodec<T> extends Codec<T> {
   T generateIdIfAbsentFromDocument(T var1);

   boolean documentHasId(T var1);

   BsonValue getDocumentId(T var1);
}
