package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class BsonValueCodec implements Codec<BsonValue> {
   private final CodecRegistry codecRegistry;

   public BsonValueCodec() {
      this(CodecRegistries.fromProviders(new BsonValueCodecProvider()));
   }

   public BsonValueCodec(CodecRegistry codecRegistry) {
      this.codecRegistry = codecRegistry;
   }

   public BsonValue decode(BsonReader reader, DecoderContext decoderContext) {
      return this.codecRegistry.get(BsonValueCodecProvider.getClassForBsonType(reader.getCurrentBsonType())).decode(reader, decoderContext);
   }

   public void encode(BsonWriter writer, BsonValue value, EncoderContext encoderContext) {
      Codec codec = this.codecRegistry.get(value.getClass());
      encoderContext.encodeWithChildContext(codec, writer, value);
   }

   @Override
   public Class<BsonValue> getEncoderClass() {
      return BsonValue.class;
   }
}
