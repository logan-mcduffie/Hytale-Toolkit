package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface HybridEncrypt {
   byte[] encrypt(final byte[] plaintext, final byte[] contextInfo) throws GeneralSecurityException;
}
