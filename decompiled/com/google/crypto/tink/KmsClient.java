package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface KmsClient {
   boolean doesSupport(String keyUri);

   KmsClient withCredentials(String credentialPath) throws GeneralSecurityException;

   KmsClient withDefaultCredentials() throws GeneralSecurityException;

   Aead getAead(String keyUri) throws GeneralSecurityException;
}
