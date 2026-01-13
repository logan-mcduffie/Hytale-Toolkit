package com.hypixel.hytale.builtin.hytalegenerator;

import java.util.HashMap;
import java.util.Map;

public class Indexer {
   private Map<Object, Integer> ids = new HashMap<>();

   public int getIdFor(Object o) {
      return this.ids.computeIfAbsent(o, k -> this.ids.size());
   }

   public int size() {
      return this.ids.size();
   }
}
