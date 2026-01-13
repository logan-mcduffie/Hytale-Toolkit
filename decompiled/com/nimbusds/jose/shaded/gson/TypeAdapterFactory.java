package com.nimbusds.jose.shaded.gson;

import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

public interface TypeAdapterFactory {
   <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2);
}
