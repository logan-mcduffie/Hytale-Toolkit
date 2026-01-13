package org.bson.codecs.configuration;

import org.bson.codecs.Codec;

public interface CodecProvider {
   <T> Codec<T> get(Class<T> var1, CodecRegistry var2);
}
