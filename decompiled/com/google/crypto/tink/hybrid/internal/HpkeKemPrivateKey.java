package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;

@Immutable
public class HpkeKemPrivateKey {
   private final Bytes serializedPrivate;
   private final Bytes serializedPublic;

   public HpkeKemPrivateKey(Bytes serializedPrivate, Bytes serializedPublic) {
      this.serializedPrivate = serializedPrivate;
      this.serializedPublic = serializedPublic;
   }

   Bytes getSerializedPrivate() {
      return this.serializedPrivate;
   }

   Bytes getSerializedPublic() {
      return this.serializedPublic;
   }
}
