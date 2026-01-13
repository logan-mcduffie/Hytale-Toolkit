package org.bson.codecs;

import org.bson.BsonJavaScript;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class BsonJavaScriptCodec implements Codec<BsonJavaScript> {
   public BsonJavaScript decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonJavaScript(reader.readJavaScript());
   }

   public void encode(BsonWriter writer, BsonJavaScript value, EncoderContext encoderContext) {
      writer.writeJavaScript(value.getCode());
   }

   @Override
   public Class<BsonJavaScript> getEncoderClass() {
      return BsonJavaScript.class;
   }
}
