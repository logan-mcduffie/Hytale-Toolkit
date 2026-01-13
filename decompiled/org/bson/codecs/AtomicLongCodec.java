package org.bson.codecs;

import java.util.concurrent.atomic.AtomicLong;
import org.bson.BsonReader;
import org.bson.BsonWriter;

public class AtomicLongCodec implements Codec<AtomicLong> {
   public void encode(BsonWriter writer, AtomicLong value, EncoderContext encoderContext) {
      writer.writeInt64(value.longValue());
   }

   public AtomicLong decode(BsonReader reader, DecoderContext decoderContext) {
      return new AtomicLong(NumberCodecHelper.decodeLong(reader));
   }

   @Override
   public Class<AtomicLong> getEncoderClass() {
      return AtomicLong.class;
   }
}
