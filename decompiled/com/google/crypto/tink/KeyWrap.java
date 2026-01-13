package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface KeyWrap {
   byte[] wrap(final byte[] data) throws GeneralSecurityException;

   byte[] unwrap(final byte[] data) throws GeneralSecurityException;
}
