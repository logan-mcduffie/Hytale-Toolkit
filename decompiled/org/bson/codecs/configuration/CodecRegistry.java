package org.bson.codecs.configuration;

import org.bson.codecs.Codec;

public interface CodecRegistry extends CodecProvider {
   <T> Codec<T> get(Class<T> var1);
}
