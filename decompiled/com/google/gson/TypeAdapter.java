package com.google.gson;

import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

public abstract class TypeAdapter<T> {
   public abstract void write(JsonWriter var1, T var2) throws IOException;

   public final void toJson(Writer out, T value) throws IOException {
      JsonWriter writer = new JsonWriter(out);
      this.write(writer, value);
   }

   public final String toJson(T value) {
      StringBuilder stringBuilder = new StringBuilder();

      try {
         this.toJson(Streams.writerForAppendable(stringBuilder), value);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }

      return stringBuilder.toString();
   }

   public final JsonElement toJsonTree(T value) {
      try {
         JsonTreeWriter jsonWriter = new JsonTreeWriter();
         this.write(jsonWriter, value);
         return jsonWriter.get();
      } catch (IOException var3) {
         throw new JsonIOException(var3);
      }
   }

   public abstract T read(JsonReader var1) throws IOException;

   public final T fromJson(Reader in) throws IOException {
      JsonReader reader = new JsonReader(in);
      return this.read(reader);
   }

   public final T fromJson(String json) throws IOException {
      return this.fromJson(new StringReader(json));
   }

   public final T fromJsonTree(JsonElement jsonTree) {
      try {
         JsonReader jsonReader = new JsonTreeReader(jsonTree);
         return this.read(jsonReader);
      } catch (IOException var3) {
         throw new JsonIOException(var3);
      }
   }

   public final TypeAdapter<T> nullSafe() {
      return (TypeAdapter<T>)(!(this instanceof TypeAdapter.NullSafeTypeAdapter) ? new TypeAdapter.NullSafeTypeAdapter() : this);
   }

   private final class NullSafeTypeAdapter extends TypeAdapter<T> {
      private NullSafeTypeAdapter() {
      }

      @Override
      public void write(JsonWriter out, T value) throws IOException {
         if (value == null) {
            out.nullValue();
         } else {
            TypeAdapter.this.write(out, value);
         }
      }

      @Override
      public T read(JsonReader reader) throws IOException {
         if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
         } else {
            return TypeAdapter.this.read(reader);
         }
      }

      @Override
      public String toString() {
         return "NullSafeTypeAdapter[" + TypeAdapter.this + "]";
      }
   }
}
