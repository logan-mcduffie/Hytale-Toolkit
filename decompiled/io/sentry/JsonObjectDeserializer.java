package io.sentry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class JsonObjectDeserializer {
   private final ArrayList<JsonObjectDeserializer.Token> tokens = new ArrayList<>();

   @Nullable
   public Object deserialize(@NotNull JsonObjectReader reader) throws IOException {
      this.parse(reader);
      JsonObjectDeserializer.Token root = this.getCurrentToken();
      return root != null ? root.getValue() : null;
   }

   private void parse(@NotNull JsonObjectReader reader) throws IOException {
      boolean done = false;
      switch (reader.peek()) {
         case BEGIN_ARRAY:
            reader.beginArray();
            this.pushCurrentToken(new JsonObjectDeserializer.TokenArray());
            break;
         case END_ARRAY:
            reader.endArray();
            done = this.handleArrayOrMapEnd();
            break;
         case BEGIN_OBJECT:
            reader.beginObject();
            this.pushCurrentToken(new JsonObjectDeserializer.TokenMap());
            break;
         case END_OBJECT:
            reader.endObject();
            done = this.handleArrayOrMapEnd();
            break;
         case NAME:
            this.pushCurrentToken(new JsonObjectDeserializer.TokenName(reader.nextName()));
            break;
         case STRING:
            done = this.handlePrimitive(() -> reader.nextString());
            break;
         case NUMBER:
            done = this.handlePrimitive(() -> this.nextNumber(reader));
            break;
         case BOOLEAN:
            done = this.handlePrimitive(() -> reader.nextBoolean());
            break;
         case NULL:
            reader.nextNull();
            done = this.handlePrimitive(() -> null);
            break;
         case END_DOCUMENT:
            done = true;
      }

      if (!done) {
         this.parse(reader);
      }
   }

   private boolean handleArrayOrMapEnd() {
      if (this.hasOneToken()) {
         return true;
      } else {
         JsonObjectDeserializer.Token arrayOrMapToken = this.getCurrentToken();
         this.popCurrentToken();
         if (this.getCurrentToken() instanceof JsonObjectDeserializer.TokenName) {
            JsonObjectDeserializer.TokenName tokenName = (JsonObjectDeserializer.TokenName)this.getCurrentToken();
            this.popCurrentToken();
            JsonObjectDeserializer.TokenMap tokenMap = (JsonObjectDeserializer.TokenMap)this.getCurrentToken();
            if (tokenName != null && arrayOrMapToken != null && tokenMap != null) {
               tokenMap.value.put(tokenName.value, arrayOrMapToken.getValue());
            }
         } else if (this.getCurrentToken() instanceof JsonObjectDeserializer.TokenArray) {
            JsonObjectDeserializer.TokenArray tokenArray = (JsonObjectDeserializer.TokenArray)this.getCurrentToken();
            if (arrayOrMapToken != null && tokenArray != null) {
               tokenArray.value.add(arrayOrMapToken.getValue());
            }
         }

         return false;
      }
   }

   private boolean handlePrimitive(JsonObjectDeserializer.NextValue callback) throws IOException {
      Object primitive = callback.nextValue();
      if (this.getCurrentToken() == null && primitive != null) {
         this.pushCurrentToken(new JsonObjectDeserializer.TokenPrimitive(primitive));
         return true;
      } else {
         if (this.getCurrentToken() instanceof JsonObjectDeserializer.TokenName) {
            JsonObjectDeserializer.TokenName tokenNameNumber = (JsonObjectDeserializer.TokenName)this.getCurrentToken();
            this.popCurrentToken();
            JsonObjectDeserializer.TokenMap tokenMapNumber = (JsonObjectDeserializer.TokenMap)this.getCurrentToken();
            tokenMapNumber.value.put(tokenNameNumber.value, primitive);
         } else if (this.getCurrentToken() instanceof JsonObjectDeserializer.TokenArray) {
            JsonObjectDeserializer.TokenArray tokenArrayNumber = (JsonObjectDeserializer.TokenArray)this.getCurrentToken();
            tokenArrayNumber.value.add(primitive);
         }

         return false;
      }
   }

   private Object nextNumber(JsonObjectReader reader) throws IOException {
      try {
         return reader.nextInt();
      } catch (Exception var4) {
         try {
            return reader.nextDouble();
         } catch (Exception var3) {
            return reader.nextLong();
         }
      }
   }

   @Nullable
   private JsonObjectDeserializer.Token getCurrentToken() {
      return this.tokens.isEmpty() ? null : this.tokens.get(this.tokens.size() - 1);
   }

   private void pushCurrentToken(JsonObjectDeserializer.Token token) {
      this.tokens.add(token);
   }

   private void popCurrentToken() {
      if (!this.tokens.isEmpty()) {
         this.tokens.remove(this.tokens.size() - 1);
      }
   }

   private boolean hasOneToken() {
      return this.tokens.size() == 1;
   }

   private interface NextValue {
      @Nullable
      Object nextValue() throws IOException;
   }

   private interface Token {
      @NotNull
      Object getValue();
   }

   private static final class TokenArray implements JsonObjectDeserializer.Token {
      final ArrayList<Object> value = new ArrayList<>();

      private TokenArray() {
      }

      @NotNull
      @Override
      public Object getValue() {
         return this.value;
      }
   }

   private static final class TokenMap implements JsonObjectDeserializer.Token {
      final HashMap<String, Object> value = new HashMap<>();

      private TokenMap() {
      }

      @NotNull
      @Override
      public Object getValue() {
         return this.value;
      }
   }

   private static final class TokenName implements JsonObjectDeserializer.Token {
      final String value;

      TokenName(@NotNull String value) {
         this.value = value;
      }

      @NotNull
      @Override
      public Object getValue() {
         return this.value;
      }
   }

   private static final class TokenPrimitive implements JsonObjectDeserializer.Token {
      final Object value;

      TokenPrimitive(@NotNull Object value) {
         this.value = value;
      }

      @NotNull
      @Override
      public Object getValue() {
         return this.value;
      }
   }
}
