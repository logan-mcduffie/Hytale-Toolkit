package com.google.crypto.tink.internal;

import com.google.crypto.tink.Parameters;
import java.security.GeneralSecurityException;

public abstract class ParametersSerializer<ParametersT extends Parameters, SerializationT extends Serialization> {
   private final Class<ParametersT> parametersClass;
   private final Class<SerializationT> serializationClass;

   private ParametersSerializer(Class<ParametersT> parametersClass, Class<SerializationT> serializationClass) {
      this.parametersClass = parametersClass;
      this.serializationClass = serializationClass;
   }

   public abstract SerializationT serializeParameters(ParametersT parameters) throws GeneralSecurityException;

   public Class<ParametersT> getParametersClass() {
      return this.parametersClass;
   }

   public Class<SerializationT> getSerializationClass() {
      return this.serializationClass;
   }

   public static <ParametersT extends Parameters, SerializationT extends Serialization> ParametersSerializer<ParametersT, SerializationT> create(
      ParametersSerializer.ParametersSerializationFunction<ParametersT, SerializationT> function,
      Class<ParametersT> parametersClass,
      Class<SerializationT> serializationClass
   ) {
      return new ParametersSerializer<ParametersT, SerializationT>(parametersClass, serializationClass) {
         @Override
         public SerializationT serializeParameters(ParametersT parameters) throws GeneralSecurityException {
            return function.serializeParameters(parameters);
         }
      };
   }

   public interface ParametersSerializationFunction<ParametersT extends Parameters, SerializationT extends Serialization> {
      SerializationT serializeParameters(ParametersT key) throws GeneralSecurityException;
   }
}
