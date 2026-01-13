package com.google.crypto.tink.daead.subtle;

import com.google.crypto.tink.DeterministicAead;
import java.security.GeneralSecurityException;

public interface DeterministicAeads extends DeterministicAead {
   byte[] encryptDeterministicallyWithAssociatedDatas(final byte[] plaintext, final byte[]... associatedDatas) throws GeneralSecurityException;

   byte[] decryptDeterministicallyWithAssociatedDatas(final byte[] ciphertext, final byte[]... associatedDatas) throws GeneralSecurityException;
}
