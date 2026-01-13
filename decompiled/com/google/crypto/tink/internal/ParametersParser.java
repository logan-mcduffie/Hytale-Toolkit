package com.google.crypto.tink.internal;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;

public abstract class ParametersParser<SerializationT extends Serialization> {
   private final Bytes objectIdentifier;
   private final Class<SerializationT> serializationClass;

   private ParametersParser(Bytes objectIdentifier, Class<SerializationT> serializationClass) {
      this.objectIdentifier = objectIdentifier;
      this.serializationClass = serializationClass;
   }

   public abstract Parameters parseParameters(SerializationT serialization) throws GeneralSecurityException;

   public final Bytes getObjectIdentifier() {
      return this.objectIdentifier;
   }

   public final Class<SerializationT> getSerializationClass() {
      return this.serializationClass;
   }

   public static <SerializationT extends Serialization> ParametersParser<SerializationT> create(
      ParametersParser.ParametersParsingFunction<SerializationT> function, Bytes objectIdentifier, Class<SerializationT> serializationClass
   ) {
      return new ParametersParser<SerializationT>(objectIdentifier, serializationClass) {
         @Override
         public Parameters parseParameters(SerializationT serialization) throws GeneralSecurityException {
            return function.parseParameters(serialization);
         }
      };
   }

   public interface ParametersParsingFunction<SerializationT extends Serialization> {
      Parameters parseParameters(SerializationT serialization) throws GeneralSecurityException;
   }
}
