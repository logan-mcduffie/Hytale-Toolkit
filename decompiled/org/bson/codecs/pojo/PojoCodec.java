package org.bson.codecs.pojo;

import org.bson.codecs.Codec;

abstract class PojoCodec<T> implements Codec<T> {
   abstract ClassModel<T> getClassModel();

   abstract DiscriminatorLookup getDiscriminatorLookup();
}
