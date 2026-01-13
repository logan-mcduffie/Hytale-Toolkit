package org.bson.codecs.pojo;

public interface InstanceCreator<T> {
   <S> void set(S var1, PropertyModel<S> var2);

   T getInstance();
}
