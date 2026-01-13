package org.bson.codecs;

import java.util.UUID;
import org.bson.UuidRepresentation;

public class OverridableUuidRepresentationUuidCodec extends UuidCodec implements OverridableUuidRepresentationCodec<UUID> {
   public OverridableUuidRepresentationUuidCodec() {
   }

   public OverridableUuidRepresentationUuidCodec(UuidRepresentation uuidRepresentation) {
      super(uuidRepresentation);
   }

   @Override
   public Codec<UUID> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
      return new OverridableUuidRepresentationUuidCodec(uuidRepresentation);
   }
}
