package com.nimbusds.jose.proc;

public interface JOSEProcessorConfiguration<C extends SecurityContext> {
   JOSEObjectTypeVerifier<C> getJWSTypeVerifier();

   void setJWSTypeVerifier(JOSEObjectTypeVerifier<C> var1);

   JWSKeySelector<C> getJWSKeySelector();

   void setJWSKeySelector(JWSKeySelector<C> var1);

   JOSEObjectTypeVerifier<C> getJWETypeVerifier();

   void setJWETypeVerifier(JOSEObjectTypeVerifier<C> var1);

   JWEKeySelector<C> getJWEKeySelector();

   void setJWEKeySelector(JWEKeySelector<C> var1);

   JWSVerifierFactory getJWSVerifierFactory();

   void setJWSVerifierFactory(JWSVerifierFactory var1);

   JWEDecrypterFactory getJWEDecrypterFactory();

   void setJWEDecrypterFactory(JWEDecrypterFactory var1);
}
