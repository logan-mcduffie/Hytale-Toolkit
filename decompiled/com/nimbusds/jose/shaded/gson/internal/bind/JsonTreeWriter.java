package com.nimbusds.jose.shaded.gson.internal.bind;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonNull;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonPrimitive;
import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JsonTreeWriter extends JsonWriter {
   private static final Writer UNWRITABLE_WRITER = new Writer() {
      @Override
      public void write(char[] buffer, int offset, int counter) {
         throw new AssertionError();
      }

      @Override
      public void flush() {
         throw new AssertionError();
      }

      @Override
      public void close() {
         throw new AssertionError();
      }
   };
   private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
   private final List<JsonElement> stack = new ArrayList<>();
   private String pendingName;
   private JsonElement product = JsonNull.INSTANCE;

   public JsonTreeWriter() {
      super(UNWRITABLE_WRITER);
   }

   public JsonElement get() {
      if (!this.stack.isEmpty()) {
         throw new IllegalStateException("Expected one JSON element but was " + this.stack);
      } else {
         return this.product;
      }
   }

   private JsonElement peek() {
      return this.stack.get(this.stack.size() - 1);
   }

   private void put(JsonElement value) {
      if (this.pendingName != null) {
         if (!value.isJsonNull() || this.getSerializeNulls()) {
            JsonObject object = (JsonObject)this.peek();
            object.add(this.pendingName, value);
         }

         this.pendingName = null;
      } else if (this.stack.isEmpty()) {
         this.product = value;
      } else {
         JsonElement element = this.peek();
         if (!(element instanceof JsonArray)) {
            throw new IllegalStateException();
         }

         ((JsonArray)element).add(value);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter beginArray() throws IOException {
      JsonArray array = new JsonArray();
      this.put(array);
      this.stack.add(array);
      return this;
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter endArray() throws IOException {
      if (!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement element = this.peek();
         if (element instanceof JsonArray) {
            this.stack.remove(this.stack.size() - 1);
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter beginObject() throws IOException {
      JsonObject object = new JsonObject();
      this.put(object);
      this.stack.add(object);
      return this;
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter endObject() throws IOException {
      if (!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement element = this.peek();
         if (element instanceof JsonObject) {
            this.stack.remove(this.stack.size() - 1);
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter name(String name) throws IOException {
      Objects.requireNonNull(name, "name == null");
      if (!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement element = this.peek();
         if (element instanceof JsonObject) {
            this.pendingName = name;
            return this;
         } else {
            throw new IllegalStateException("Please begin an object before writing a name.");
         }
      } else {
         throw new IllegalStateException("Did not expect a name");
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(String value) throws IOException {
      if (value == null) {
         return this.nullValue();
      } else {
         this.put(new JsonPrimitive(value));
         return this;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(boolean value) throws IOException {
      this.put(new JsonPrimitive(value));
      return this;
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(Boolean value) throws IOException {
      if (value == null) {
         return this.nullValue();
      } else {
         this.put(new JsonPrimitive(value));
         return this;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(float value) throws IOException {
      if (this.isLenient() || !Float.isNaN(value) && !Float.isInfinite(value)) {
         this.put(new JsonPrimitive(value));
         return this;
      } else {
         throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(double value) throws IOException {
      if (this.isLenient() || !Double.isNaN(value) && !Double.isInfinite(value)) {
         this.put(new JsonPrimitive(value));
         return this;
      } else {
         throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(long value) throws IOException {
      this.put(new JsonPrimitive(value));
      return this;
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter value(Number value) throws IOException {
      if (value == null) {
         return this.nullValue();
      } else {
         if (!this.isLenient()) {
            double d = value.doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
               throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
            }
         }

         this.put(new JsonPrimitive(value));
         return this;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public JsonWriter nullValue() throws IOException {
      this.put(JsonNull.INSTANCE);
      return this;
   }

   @Override
   public JsonWriter jsonValue(String value) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public void close() throws IOException {
      if (!this.stack.isEmpty()) {
         throw new IOException("Incomplete document");
      } else {
         this.stack.add(SENTINEL_CLOSED);
      }
   }
}
