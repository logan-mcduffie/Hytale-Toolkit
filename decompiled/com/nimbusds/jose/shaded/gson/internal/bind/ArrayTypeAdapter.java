package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.internal.GsonTypes;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public final class ArrayTypeAdapter<E> extends TypeAdapter<Object> {
   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      @Override
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
         Type type = typeToken.getType();
         if (type instanceof GenericArrayType || type instanceof Class && ((Class)type).isArray()) {
            Type componentType = GsonTypes.getArrayComponentType(type);
            TypeAdapter<?> componentTypeAdapter = gson.getAdapter(TypeToken.get(componentType));
            TypeAdapter<T> arrayAdapter = new ArrayTypeAdapter(gson, (TypeAdapter<E>)componentTypeAdapter, (Class<E>)GsonTypes.getRawType(componentType));
            return arrayAdapter;
         } else {
            return null;
         }
      }
   };
   private final Class<E> componentType;
   private final TypeAdapter<E> componentTypeAdapter;

   public ArrayTypeAdapter(Gson context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
      this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(context, componentTypeAdapter, componentType);
      this.componentType = componentType;
   }

   @Override
   public Object read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
         in.nextNull();
         return null;
      } else {
         ArrayList<E> list = new ArrayList<>();
         in.beginArray();

         while (in.hasNext()) {
            E instance = this.componentTypeAdapter.read(in);
            list.add(instance);
         }

         in.endArray();
         int size = list.size();
         if (!this.componentType.isPrimitive()) {
            E[] array = (E[])((Object[])Array.newInstance(this.componentType, size));
            return list.toArray(array);
         } else {
            Object array = Array.newInstance(this.componentType, size);

            for (int i = 0; i < size; i++) {
               Array.set(array, i, list.get(i));
            }

            return array;
         }
      }
   }

   @Override
   public void write(JsonWriter out, Object array) throws IOException {
      if (array == null) {
         out.nullValue();
      } else {
         out.beginArray();
         int i = 0;

         for (int length = Array.getLength(array); i < length; i++) {
            E value = (E)Array.get(array, i);
            this.componentTypeAdapter.write(out, value);
         }

         out.endArray();
      }
   }
}
