package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.types.Symbol;

public class SymbolCodec implements Codec<Symbol> {
   public Symbol decode(BsonReader reader, DecoderContext decoderContext) {
      return new Symbol(reader.readSymbol());
   }

   public void encode(BsonWriter writer, Symbol value, EncoderContext encoderContext) {
      writer.writeSymbol(value.getSymbol());
   }

   @Override
   public Class<Symbol> getEncoderClass() {
      return Symbol.class;
   }
}
