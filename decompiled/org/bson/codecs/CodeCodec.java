package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.Code;

public class CodeCodec implements Codec<Code> {
   public void encode(BsonWriter writer, Code value, EncoderContext encoderContext) {
      writer.writeJavaScript(value.getCode());
   }

   public Code decode(BsonReader bsonReader, DecoderContext decoderContext) {
      return new Code(bsonReader.readJavaScript());
   }

   @Override
   public Class<Code> getEncoderClass() {
      return Code.class;
   }
}
