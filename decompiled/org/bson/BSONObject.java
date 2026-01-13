package org.bson;

import java.util.Map;
import java.util.Set;

public interface BSONObject {
   Object put(String var1, Object var2);

   void putAll(BSONObject var1);

   void putAll(Map var1);

   Object get(String var1);

   Map toMap();

   Object removeField(String var1);

   boolean containsField(String var1);

   Set<String> keySet();
}
