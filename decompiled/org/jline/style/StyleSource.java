package org.jline.style;

import java.util.Map;
import javax.annotation.Nullable;

public interface StyleSource {
   @Nullable
   String get(String var1, String var2);

   void set(String var1, String var2, String var3);

   void remove(String var1);

   void remove(String var1, String var2);

   void clear();

   Iterable<String> groups();

   Map<String, String> styles(String var1);
}
