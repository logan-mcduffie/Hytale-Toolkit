package org.bson.codecs;

import org.bson.BsonReader;

public interface Decoder<T> {
   T decode(BsonReader var1, DecoderContext var2);
}
