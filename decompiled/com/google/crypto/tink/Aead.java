package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface Aead {
   byte[] encrypt(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException;

   byte[] decrypt(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException;
}
