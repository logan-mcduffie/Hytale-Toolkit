package org.bson.codecs;

import org.bson.UuidRepresentation;

public interface OverridableUuidRepresentationCodec<T> {
   Codec<T> withUuidRepresentation(UuidRepresentation var1);
}
