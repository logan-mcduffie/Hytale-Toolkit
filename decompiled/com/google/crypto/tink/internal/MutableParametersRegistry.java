package com.google.crypto.tink.internal;

import com.google.crypto.tink.Parameters;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class MutableParametersRegistry {
   private final Map<String, Parameters> parametersMap = new HashMap<>();
   private static final MutableParametersRegistry globalInstance = new MutableParametersRegistry();

   MutableParametersRegistry() {
   }

   public static MutableParametersRegistry globalInstance() {
      return globalInstance;
   }

   public synchronized void put(String name, Parameters value) throws GeneralSecurityException {
      if (this.parametersMap.containsKey(name)) {
         if (!this.parametersMap.get(name).equals(value)) {
            throw new GeneralSecurityException(
               "Parameters object with name " + name + " already exists (" + this.parametersMap.get(name) + "), cannot insert " + value
            );
         }
      } else {
         this.parametersMap.put(name, value);
      }
   }

   public synchronized Parameters get(String name) throws GeneralSecurityException {
      if (this.parametersMap.containsKey(name)) {
         return this.parametersMap.get(name);
      } else {
         throw new GeneralSecurityException("Name " + name + " does not exist");
      }
   }

   public synchronized void putAll(Map<String, Parameters> values) throws GeneralSecurityException {
      for (Entry<String, Parameters> entry : values.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }
   }

   public synchronized List<String> getNames() {
      List<String> results = new ArrayList<>();
      results.addAll(this.parametersMap.keySet());
      return Collections.unmodifiableList(results);
   }
}
