package com.nimbusds.jose.shaded.gson;

public interface ExclusionStrategy {
   boolean shouldSkipField(FieldAttributes var1);

   boolean shouldSkipClass(Class<?> var1);
}
