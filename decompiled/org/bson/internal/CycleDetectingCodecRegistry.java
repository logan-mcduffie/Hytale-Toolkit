package org.bson.internal;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

interface CycleDetectingCodecRegistry extends CodecRegistry {
   <T> Codec<T> get(ChildCodecRegistry<T> var1);
}
