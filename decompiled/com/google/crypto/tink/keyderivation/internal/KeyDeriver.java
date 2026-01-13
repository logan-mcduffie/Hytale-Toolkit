package com.google.crypto.tink.keyderivation.internal;

import com.google.crypto.tink.Key;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface KeyDeriver {
   Key deriveKey(byte[] salt) throws GeneralSecurityException;
}
