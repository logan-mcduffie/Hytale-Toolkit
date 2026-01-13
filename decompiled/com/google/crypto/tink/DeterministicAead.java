package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface DeterministicAead {
   byte[] encryptDeterministically(final byte[] plaintext, final byte[] associatedData) throws GeneralSecurityException;

   byte[] decryptDeterministically(final byte[] ciphertext, final byte[] associatedData) throws GeneralSecurityException;
}
