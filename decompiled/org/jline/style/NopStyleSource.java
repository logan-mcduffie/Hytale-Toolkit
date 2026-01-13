package org.jline.style;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public class NopStyleSource implements StyleSource {
   @Nullable
   @Override
   public String get(String group, String name) {
      Objects.requireNonNull(group);
      return null;
   }

   @Override
   public void set(String group, String name, String style) {
      Objects.requireNonNull(group);
      Objects.requireNonNull(name);
      Objects.requireNonNull(style);
   }

   @Override
   public void remove(String group) {
      Objects.requireNonNull(group);
   }

   @Override
   public void remove(String group, String name) {
      Objects.requireNonNull(group);
      Objects.requireNonNull(name);
   }

   @Override
   public void clear() {
   }

   @Override
   public Iterable<String> groups() {
      return Collections.unmodifiableList(Collections.emptyList());
   }

   @Override
   public Map<String, String> styles(String group) {
      return Collections.unmodifiableMap(Collections.emptyMap());
   }
}
