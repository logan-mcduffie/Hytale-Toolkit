package com.google.crypto.tink.hybrid.internal;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface HpkeKem {
   HpkeKemEncapOutput encapsulate(byte[] recipientPublicKey) throws GeneralSecurityException;

   byte[] decapsulate(byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey) throws GeneralSecurityException;

   HpkeKemEncapOutput authEncapsulate(byte[] recipientPublicKey, HpkeKemPrivateKey senderPrivateKey) throws GeneralSecurityException;

   byte[] authDecapsulate(byte[] encapsulatedKey, HpkeKemPrivateKey recipientPrivateKey, byte[] senderPublicKey) throws GeneralSecurityException;

   byte[] getKemId() throws GeneralSecurityException;
}
