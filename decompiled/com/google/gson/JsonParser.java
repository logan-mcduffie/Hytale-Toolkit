package com.google.gson;

import com.google.errorprone.annotations.InlineMe;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {
   public static JsonElement parseString(String json) throws JsonSyntaxException {
      return parseReader(new StringReader(json));
   }

   public static JsonElement parseReader(Reader reader) throws JsonIOException, JsonSyntaxException {
      try {
         JsonReader jsonReader = new JsonReader(reader);
         JsonElement element = parseReader(jsonReader);
         if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonSyntaxException("Did not consume the entire document.");
         } else {
            return element;
         }
      } catch (NumberFormatException | MalformedJsonException var3) {
         throw new JsonSyntaxException(var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }
   }

   public static JsonElement parseReader(JsonReader reader) throws JsonIOException, JsonSyntaxException {
      Strictness strictness = reader.getStrictness();
      if (strictness == Strictness.LEGACY_STRICT) {
         reader.setStrictness(Strictness.LENIENT);
      }

      JsonElement e;
      try {
         e = Streams.parse(reader);
      } catch (OutOfMemoryError | StackOverflowError var6) {
         throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", var6);
      } finally {
         reader.setStrictness(strictness);
      }

      return e;
   }

   @Deprecated
   @InlineMe(replacement = "JsonParser.parseString(json)", imports = "com.google.gson.JsonParser")
   public JsonElement parse(String json) throws JsonSyntaxException {
      return parseString(json);
   }

   @Deprecated
   @InlineMe(replacement = "JsonParser.parseReader(json)", imports = "com.google.gson.JsonParser")
   public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
      return parseReader(json);
   }

   @Deprecated
   @InlineMe(replacement = "JsonParser.parseReader(json)", imports = "com.google.gson.JsonParser")
   public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
      return parseReader(json);
   }
}
