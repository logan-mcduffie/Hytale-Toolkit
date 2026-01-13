package org.bson.codecs.pojo;

import java.util.List;

public interface TypeWithTypeParameters<T> {
   Class<T> getType();

   List<? extends TypeWithTypeParameters<?>> getTypeParameters();
}
