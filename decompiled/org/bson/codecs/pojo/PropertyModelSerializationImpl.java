package org.bson.codecs.pojo;

class PropertyModelSerializationImpl<T> implements PropertySerialization<T> {
   @Override
   public boolean shouldSerialize(T value) {
      return value != null;
   }
}
