package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface PublicKeyVerify {
   void verify(final byte[] signature, final byte[] data) throws GeneralSecurityException;
}
