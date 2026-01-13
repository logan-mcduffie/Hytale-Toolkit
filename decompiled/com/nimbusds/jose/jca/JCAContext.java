package com.nimbusds.jose.jca;

import java.security.Provider;
import java.security.SecureRandom;

public class JCAContext {
   private Provider provider;
   private SecureRandom randomGen;

   public JCAContext() {
      this(null, null);
   }

   public JCAContext(Provider provider, SecureRandom randomGen) {
      this.provider = provider;
      this.randomGen = randomGen;
   }

   public Provider getProvider() {
      return this.provider;
   }

   public void setProvider(Provider provider) {
      this.provider = provider;
   }

   public SecureRandom getSecureRandom() {
      return this.randomGen != null ? this.randomGen : new SecureRandom();
   }

   public void setSecureRandom(SecureRandom randomGen) {
      this.randomGen = randomGen;
   }
}
