package com.nimbusds.jose.shaded.gson;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public interface FieldNamingStrategy {
   String translateName(Field var1);

   default List<String> alternateNames(Field f) {
      return Collections.emptyList();
   }
}
