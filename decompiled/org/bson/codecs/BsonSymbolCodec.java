package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonSymbol;
import org.bson.BsonWriter;

public class BsonSymbolCodec implements Codec<BsonSymbol> {
   public BsonSymbol decode(BsonReader reader, DecoderContext decoderContext) {
      return new BsonSymbol(reader.readSymbol());
   }

   public void encode(BsonWriter writer, BsonSymbol value, EncoderContext encoderContext) {
      writer.writeSymbol(value.getSymbol());
   }

   @Override
   public Class<BsonSymbol> getEncoderClass() {
      return BsonSymbol.class;
   }
}
