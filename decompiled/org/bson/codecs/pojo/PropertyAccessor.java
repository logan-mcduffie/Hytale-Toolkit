package org.bson.codecs.pojo;

public interface PropertyAccessor<T> {
   <S> T get(S var1);

   <S> void set(S var1, T var2);
}
