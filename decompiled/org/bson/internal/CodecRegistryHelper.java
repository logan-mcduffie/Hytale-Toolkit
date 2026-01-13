package org.bson.internal;

import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;

public final class CodecRegistryHelper {
   public static CodecRegistry createRegistry(CodecRegistry codecRegistry, UuidRepresentation uuidRepresentation) {
      return (CodecRegistry)(uuidRepresentation == UuidRepresentation.UNSPECIFIED
         ? codecRegistry
         : new OverridableUuidRepresentationCodecRegistry(codecRegistry, uuidRepresentation));
   }

   private CodecRegistryHelper() {
   }
}
