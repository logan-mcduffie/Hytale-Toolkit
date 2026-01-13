package org.bson.codecs.pojo;

public interface InstanceCreatorFactory<T> {
   InstanceCreator<T> create();
}
