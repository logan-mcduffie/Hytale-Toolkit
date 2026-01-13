package com.nimbusds.jose.jca;

import java.security.Provider;
import java.security.SecureRandom;

public final class JWEJCAContext extends JCAContext {
   private Provider keProvider;
   private Provider ceProvider;
   private Provider macProvider;

   public JWEJCAContext() {
      this(null, null, null, null, null);
   }

   public JWEJCAContext(Provider generalProvider, Provider keProvider, Provider ceProvider, Provider macProvider, SecureRandom randomGen) {
      super(generalProvider, randomGen);
      this.keProvider = keProvider;
      this.ceProvider = ceProvider;
      this.macProvider = macProvider;
   }

   public void setKeyEncryptionProvider(Provider keProvider) {
      this.keProvider = keProvider;
   }

   public Provider getKeyEncryptionProvider() {
      return this.keProvider != null ? this.keProvider : this.getProvider();
   }

   public void setContentEncryptionProvider(Provider ceProvider) {
      this.ceProvider = ceProvider;
   }

   public Provider getContentEncryptionProvider() {
      return this.ceProvider != null ? this.ceProvider : this.getProvider();
   }

   public void setMACProvider(Provider macProvider) {
      this.macProvider = macProvider;
   }

   public Provider getMACProvider() {
      return this.macProvider != null ? this.macProvider : this.getProvider();
   }
}
