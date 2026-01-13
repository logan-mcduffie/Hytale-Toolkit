package joptsimple.internal;

import java.util.Map;

public interface OptionNameMap<V> {
   boolean contains(String var1);

   V get(String var1);

   void put(String var1, V var2);

   void putAll(Iterable<String> var1, V var2);

   void remove(String var1);

   Map<String, V> toJavaUtilMap();
}
