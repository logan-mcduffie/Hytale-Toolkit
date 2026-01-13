package com.google.crypto.tink.aead.internal;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public final class InsecureNonceXChaCha20Poly1305 extends InsecureNonceChaCha20Poly1305Base {
   public InsecureNonceXChaCha20Poly1305(final byte[] key) throws GeneralSecurityException {
      super(key);
   }

   @Override
   InsecureNonceChaCha20Base newChaCha20Instance(final byte[] key, int initialCounter) throws InvalidKeyException {
      return new InsecureNonceXChaCha20(key, initialCounter);
   }
}
