package com.google.crypto.tink;

import com.google.crypto.tink.proto.KeyData;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;

public interface KeyManager<P> {
   P getPrimitive(ByteString serializedKey) throws GeneralSecurityException;

   @Deprecated
   default P getPrimitive(MessageLite key) throws GeneralSecurityException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default MessageLite newKey(ByteString serializedKeyFormat) throws GeneralSecurityException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default MessageLite newKey(MessageLite keyFormat) throws GeneralSecurityException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default boolean doesSupport(String typeUrl) {
      throw new UnsupportedOperationException();
   }

   String getKeyType();

   @Deprecated
   default int getVersion() {
      throw new UnsupportedOperationException();
   }

   Class<P> getPrimitiveClass();

   KeyData newKeyData(ByteString serializedKeyFormat) throws GeneralSecurityException;
}
