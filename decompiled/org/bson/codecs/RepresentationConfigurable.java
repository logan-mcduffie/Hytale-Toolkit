package org.bson.codecs;

import org.bson.BsonType;

public interface RepresentationConfigurable<T> {
   BsonType getRepresentation();

   Codec<T> withRepresentation(BsonType var1);
}
