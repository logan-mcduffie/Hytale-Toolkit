package org.bson.codecs.jsr310;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

public class LocalDateTimeCodec extends DateTimeBasedCodec<LocalDateTime> {
   public LocalDateTime decode(BsonReader reader, DecoderContext decoderContext) {
      return Instant.ofEpochMilli(this.validateAndReadDateTime(reader)).atZone(ZoneOffset.UTC).toLocalDateTime();
   }

   public void encode(BsonWriter writer, LocalDateTime value, EncoderContext encoderContext) {
      try {
         writer.writeDateTime(value.toInstant(ZoneOffset.UTC).toEpochMilli());
      } catch (ArithmeticException var5) {
         throw new CodecConfigurationException(
            String.format("Unsupported LocalDateTime value '%s' could not be converted to milliseconds: %s", value, var5.getMessage()), var5
         );
      }
   }

   @Override
   public Class<LocalDateTime> getEncoderClass() {
      return LocalDateTime.class;
   }
}
