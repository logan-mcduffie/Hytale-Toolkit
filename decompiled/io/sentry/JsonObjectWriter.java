package io.sentry;

import io.sentry.vendor.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonObjectWriter implements ObjectWriter {
   @NotNull
   private final JsonWriter jsonWriter;
   @NotNull
   private final JsonObjectSerializer jsonObjectSerializer;

   public JsonObjectWriter(@NotNull Writer out, int maxDepth) {
      this.jsonWriter = new JsonWriter(out);
      this.jsonObjectSerializer = new JsonObjectSerializer(maxDepth);
   }

   public JsonObjectWriter beginArray() throws IOException {
      this.jsonWriter.beginArray();
      return this;
   }

   public JsonObjectWriter endArray() throws IOException {
      this.jsonWriter.endArray();
      return this;
   }

   public JsonObjectWriter beginObject() throws IOException {
      this.jsonWriter.beginObject();
      return this;
   }

   public JsonObjectWriter endObject() throws IOException {
      this.jsonWriter.endObject();
      return this;
   }

   public JsonObjectWriter name(@NotNull String name) throws IOException {
      this.jsonWriter.name(name);
      return this;
   }

   public JsonObjectWriter value(@Nullable String value) throws IOException {
      this.jsonWriter.value(value);
      return this;
   }

   @Override
   public ObjectWriter jsonValue(@Nullable String value) throws IOException {
      this.jsonWriter.jsonValue(value);
      return this;
   }

   public JsonObjectWriter nullValue() throws IOException {
      this.jsonWriter.nullValue();
      return this;
   }

   public JsonObjectWriter value(boolean value) throws IOException {
      this.jsonWriter.value(value);
      return this;
   }

   public JsonObjectWriter value(@Nullable Boolean value) throws IOException {
      this.jsonWriter.value(value);
      return this;
   }

   public JsonObjectWriter value(double value) throws IOException {
      this.jsonWriter.value(value);
      return this;
   }

   public JsonObjectWriter value(long value) throws IOException {
      this.jsonWriter.value(value);
      return this;
   }

   public JsonObjectWriter value(@Nullable Number value) throws IOException {
      this.jsonWriter.value(value);
      return this;
   }

   public JsonObjectWriter value(@NotNull ILogger logger, @Nullable Object object) throws IOException {
      this.jsonObjectSerializer.serialize(this, logger, object);
      return this;
   }

   @Override
   public void setLenient(boolean lenient) {
      this.jsonWriter.setLenient(lenient);
   }

   @Override
   public void setIndent(@Nullable String indent) {
      this.jsonWriter.setIndent(indent);
   }

   @Nullable
   @Override
   public String getIndent() {
      return this.jsonWriter.getIndent();
   }
}
