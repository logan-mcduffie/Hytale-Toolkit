package org.bson.codecs.jsr310;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class LocalTimeCodec extends DateTimeBasedCodec<LocalTime> {
   public LocalTime decode(BsonReader reader, DecoderContext decoderContext) {
      return Instant.ofEpochMilli(this.validateAndReadDateTime(reader)).atOffset(ZoneOffset.UTC).toLocalTime();
   }

   public void encode(BsonWriter writer, LocalTime value, EncoderContext encoderContext) {
      writer.writeDateTime(value.atDate(LocalDate.ofEpochDay(0L)).toInstant(ZoneOffset.UTC).toEpochMilli());
   }

   @Override
   public Class<LocalTime> getEncoderClass() {
      return LocalTime.class;
   }
}
