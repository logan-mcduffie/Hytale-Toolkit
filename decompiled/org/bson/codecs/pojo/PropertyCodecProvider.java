package org.bson.codecs.pojo;

import org.bson.codecs.Codec;

public interface PropertyCodecProvider {
   <T> Codec<T> get(TypeWithTypeParameters<T> var1, PropertyCodecRegistry var2);
}
