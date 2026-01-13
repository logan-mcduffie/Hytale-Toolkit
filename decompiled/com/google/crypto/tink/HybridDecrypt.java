package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface HybridDecrypt {
   byte[] decrypt(final byte[] ciphertext, final byte[] contextInfo) throws GeneralSecurityException;
}
