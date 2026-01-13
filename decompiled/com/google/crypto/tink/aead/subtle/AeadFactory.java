package com.google.crypto.tink.aead.subtle;

import com.google.crypto.tink.Aead;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface AeadFactory {
   int getKeySizeInBytes();

   Aead createAead(final byte[] symmetricKey) throws GeneralSecurityException;
}
