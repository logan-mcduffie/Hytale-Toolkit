package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public abstract class KeyParser<SerializationT extends Serialization> {
   private final Bytes objectIdentifier;
   private final Class<SerializationT> serializationClass;

   private KeyParser(Bytes objectIdentifier, Class<SerializationT> serializationClass) {
      this.objectIdentifier = objectIdentifier;
      this.serializationClass = serializationClass;
   }

   public abstract Key parseKey(SerializationT serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException;

   public final Bytes getObjectIdentifier() {
      return this.objectIdentifier;
   }

   public final Class<SerializationT> getSerializationClass() {
      return this.serializationClass;
   }

   public static <SerializationT extends Serialization> KeyParser<SerializationT> create(
      KeyParser.KeyParsingFunction<SerializationT> function, Bytes objectIdentifier, Class<SerializationT> serializationClass
   ) {
      return new KeyParser<SerializationT>(objectIdentifier, serializationClass) {
         @Override
         public Key parseKey(SerializationT serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
            return function.parseKey(serialization, access);
         }
      };
   }

   public interface KeyParsingFunction<SerializationT extends Serialization> {
      Key parseKey(SerializationT serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException;
   }
}
