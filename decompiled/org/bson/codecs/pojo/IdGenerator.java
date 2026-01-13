package org.bson.codecs.pojo;

public interface IdGenerator<T> {
   T generate();

   Class<T> getType();
}
