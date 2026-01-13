package org.bson.codecs.jsr310;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

public class LocalDateCodec extends DateTimeBasedCodec<LocalDate> {
   public LocalDate decode(BsonReader reader, DecoderContext decoderContext) {
      return Instant.ofEpochMilli(this.validateAndReadDateTime(reader)).atZone(ZoneOffset.UTC).toLocalDate();
   }

   public void encode(BsonWriter writer, LocalDate value, EncoderContext encoderContext) {
      try {
         writer.writeDateTime(value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
      } catch (ArithmeticException var5) {
         throw new CodecConfigurationException(
            String.format("Unsupported LocalDate '%s' could not be converted to milliseconds: %s", value, var5.getMessage()), var5
         );
      }
   }

   @Override
   public Class<LocalDate> getEncoderClass() {
      return LocalDate.class;
   }
}
