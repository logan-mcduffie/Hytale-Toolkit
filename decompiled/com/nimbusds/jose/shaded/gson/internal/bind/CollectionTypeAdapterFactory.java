package com.nimbusds.jose.shaded.gson.internal.bind;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.TypeAdapter;
import com.nimbusds.jose.shaded.gson.TypeAdapterFactory;
import com.nimbusds.jose.shaded.gson.internal.ConstructorConstructor;
import com.nimbusds.jose.shaded.gson.internal.GsonTypes;
import com.nimbusds.jose.shaded.gson.internal.ObjectConstructor;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.gson.stream.JsonToken;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;

   public CollectionTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
      this.constructorConstructor = constructorConstructor;
   }

   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Type type = typeToken.getType();
      Class<? super T> rawType = typeToken.getRawType();
      if (!Collection.class.isAssignableFrom(rawType)) {
         return null;
      } else {
         Type elementType = GsonTypes.getCollectionElementType(type, rawType);
         TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
         TypeAdapter<?> wrappedTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, elementTypeAdapter, elementType);
         boolean allowUnsafe = false;
         ObjectConstructor<T> constructor = this.constructorConstructor.get(typeToken, allowUnsafe);
         TypeAdapter<T> result = (TypeAdapter<T>)(new CollectionTypeAdapterFactory.Adapter<>(wrappedTypeAdapter, constructor));
         return result;
      }
   }

   private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
      private final TypeAdapter<E> elementTypeAdapter;
      private final ObjectConstructor<? extends Collection<E>> constructor;

      public Adapter(TypeAdapter<E> elementTypeAdapter, ObjectConstructor<? extends Collection<E>> constructor) {
         this.elementTypeAdapter = elementTypeAdapter;
         this.constructor = constructor;
      }

      public Collection<E> read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            Collection<E> collection = (Collection<E>)this.constructor.construct();
            in.beginArray();

            while (in.hasNext()) {
               E instance = this.elementTypeAdapter.read(in);
               collection.add(instance);
            }

            in.endArray();
            return collection;
         }
      }

      public void write(JsonWriter out, Collection<E> collection) throws IOException {
         if (collection == null) {
            out.nullValue();
         } else {
            out.beginArray();

            for (E element : collection) {
               this.elementTypeAdapter.write(out, element);
            }

            out.endArray();
         }
      }
   }
}
