package org.bson.codecs.pojo;

import org.bson.codecs.Codec;

public interface PropertyCodecRegistry {
   <T> Codec<T> get(TypeWithTypeParameters<T> var1);
}
