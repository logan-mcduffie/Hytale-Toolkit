package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.KeysetHandle;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface KeysetDeriver {
   KeysetHandle deriveKeyset(byte[] salt) throws GeneralSecurityException;
}
