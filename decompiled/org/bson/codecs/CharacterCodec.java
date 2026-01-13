package org.bson.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;

public class CharacterCodec implements Codec<Character> {
   public void encode(BsonWriter writer, Character value, EncoderContext encoderContext) {
      Assertions.notNull("value", value);
      writer.writeString(value.toString());
   }

   public Character decode(BsonReader reader, DecoderContext decoderContext) {
      String string = reader.readString();
      if (string.length() != 1) {
         throw new BsonInvalidOperationException(
            String.format("Attempting to decode the string '%s' to a character, but its length is not equal to one", string)
         );
      } else {
         return string.charAt(0);
      }
   }

   @Override
   public Class<Character> getEncoderClass() {
      return Character.class;
   }
}
