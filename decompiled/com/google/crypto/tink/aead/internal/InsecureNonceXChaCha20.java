package com.google.crypto.tink.aead.internal;

import java.security.InvalidKeyException;

public class InsecureNonceXChaCha20 extends InsecureNonceChaCha20Base {
   public static final int NONCE_SIZE_IN_BYTES = 24;

   public InsecureNonceXChaCha20(byte[] key, int initialCounter) throws InvalidKeyException {
      super(key, initialCounter);
   }

   @Override
   int[] createInitialState(final int[] nonce, int counter) {
      if (nonce.length != this.nonceSizeInBytes() / 4) {
         throw new IllegalArgumentException(String.format("XChaCha20 uses 192-bit nonces, but got a %d-bit nonce", nonce.length * 32));
      } else {
         int[] state = new int[16];
         ChaCha20Util.setSigmaAndKey(state, ChaCha20Util.hChaCha20(this.key, nonce));
         state[12] = counter;
         state[13] = 0;
         state[14] = nonce[4];
         state[15] = nonce[5];
         return state;
      }
   }

   @Override
   int nonceSizeInBytes() {
      return 24;
   }
}
