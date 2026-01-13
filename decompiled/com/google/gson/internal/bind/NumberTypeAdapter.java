package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public final class NumberTypeAdapter extends TypeAdapter<Number> {
   private static final TypeAdapterFactory LAZILY_PARSED_NUMBER_FACTORY = newFactory(ToNumberPolicy.LAZILY_PARSED_NUMBER);
   private final ToNumberStrategy toNumberStrategy;

   private NumberTypeAdapter(ToNumberStrategy toNumberStrategy) {
      this.toNumberStrategy = toNumberStrategy;
   }

   private static TypeAdapterFactory newFactory(ToNumberStrategy toNumberStrategy) {
      final NumberTypeAdapter adapter = new NumberTypeAdapter(toNumberStrategy);
      return new TypeAdapterFactory() {
         @Override
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return type.getRawType() == Number.class ? adapter : null;
         }
      };
   }

   public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy) {
      return toNumberStrategy == ToNumberPolicy.LAZILY_PARSED_NUMBER ? LAZILY_PARSED_NUMBER_FACTORY : newFactory(toNumberStrategy);
   }

   public Number read(JsonReader in) throws IOException {
      JsonToken jsonToken = in.peek();
      switch (jsonToken) {
         case NULL:
            in.nextNull();
            return null;
         case NUMBER:
         case STRING:
            return this.toNumberStrategy.readNumber(in);
         default:
            throw new JsonSyntaxException("Expecting number, got: " + jsonToken + "; at path " + in.getPath());
      }
   }

   public void write(JsonWriter out, Number value) throws IOException {
      out.value(value);
   }
}
