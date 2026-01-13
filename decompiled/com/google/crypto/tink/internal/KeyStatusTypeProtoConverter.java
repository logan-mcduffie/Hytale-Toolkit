package com.google.crypto.tink.internal;

import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.tinkkey.KeyHandle;

public final class KeyStatusTypeProtoConverter {
   public static KeyHandle.KeyStatusType fromProto(KeyStatusType keyStatusTypeProto) {
      switch (keyStatusTypeProto) {
         case ENABLED:
            return KeyHandle.KeyStatusType.ENABLED;
         case DISABLED:
            return KeyHandle.KeyStatusType.DISABLED;
         case DESTROYED:
            return KeyHandle.KeyStatusType.DESTROYED;
         default:
            throw new IllegalArgumentException("Unknown key status type.");
      }
   }

   public static KeyStatusType toProto(KeyHandle.KeyStatusType status) {
      switch (status) {
         case ENABLED:
            return KeyStatusType.ENABLED;
         case DISABLED:
            return KeyStatusType.DISABLED;
         case DESTROYED:
            return KeyStatusType.DESTROYED;
         default:
            throw new IllegalArgumentException("Unknown key status type.");
      }
   }

   private KeyStatusTypeProtoConverter() {
   }
}
