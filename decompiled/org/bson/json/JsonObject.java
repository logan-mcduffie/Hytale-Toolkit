package org.bson.json;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class JsonObject implements Bson {
   private final String json;

   public JsonObject(String json) {
      Assertions.notNull("Json", json);
      boolean foundBrace = false;

      for (int i = 0; i < json.length(); i++) {
         char c = json.charAt(i);
         if (c == '{') {
            foundBrace = true;
            break;
         }

         Assertions.isTrueArgument("json is a valid JSON object", Character.isWhitespace(c));
      }

      Assertions.isTrueArgument("json is a valid JSON object", foundBrace);
      this.json = json;
   }

   public String getJson() {
      return this.json;
   }

   @Override
   public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry registry) {
      return new BsonDocumentWrapper<>(this, registry.get(JsonObject.class));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         JsonObject that = (JsonObject)o;
         return this.json.equals(that.getJson());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.json.hashCode();
   }

   @Override
   public String toString() {
      return this.json;
   }
}
