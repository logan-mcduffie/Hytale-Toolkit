package io.netty.util;

public interface AttributeMap {
   <T> Attribute<T> attr(AttributeKey<T> var1);

   <T> boolean hasAttr(AttributeKey<T> var1);
}
