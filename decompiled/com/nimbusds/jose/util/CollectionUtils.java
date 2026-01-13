package com.nimbusds.jose.util;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {
   public static <T> boolean containsNull(Set<T> set) {
      HashSet<T> defensiveCopy = new HashSet<>(set);
      return defensiveCopy.contains(null);
   }

   private CollectionUtils() {
   }
}
