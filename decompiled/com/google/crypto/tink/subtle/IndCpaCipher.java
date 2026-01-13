package com.google.crypto.tink.subtle;

import java.security.GeneralSecurityException;

public interface IndCpaCipher {
   byte[] encrypt(final byte[] plaintext) throws GeneralSecurityException;

   byte[] decrypt(final byte[] ciphertext) throws GeneralSecurityException;
}
