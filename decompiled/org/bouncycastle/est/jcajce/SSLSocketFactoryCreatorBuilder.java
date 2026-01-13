package org.bouncycastle.est.jcajce;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

class SSLSocketFactoryCreatorBuilder {
   protected String tlsVersion = "TLS";
   protected Provider tlsProvider;
   protected KeyManager[] keyManagers;
   protected X509TrustManager[] trustManagers;
   protected SecureRandom secureRandom;

   public SSLSocketFactoryCreatorBuilder(X509TrustManager var1) {
      if (var1 == null) {
         throw new NullPointerException("Trust managers can not be null");
      } else {
         this.trustManagers = new X509TrustManager[]{var1};
      }
   }

   public SSLSocketFactoryCreatorBuilder(X509TrustManager[] var1) {
      if (var1 == null) {
         throw new NullPointerException("Trust managers can not be null");
      } else {
         this.trustManagers = var1;
      }
   }

   public SSLSocketFactoryCreatorBuilder withTLSVersion(String var1) {
      this.tlsVersion = var1;
      return this;
   }

   public SSLSocketFactoryCreatorBuilder withSecureRandom(SecureRandom var1) {
      this.secureRandom = var1;
      return this;
   }

   public SSLSocketFactoryCreatorBuilder withProvider(String var1) throws NoSuchProviderException {
      this.tlsProvider = Security.getProvider(var1);
      if (this.tlsProvider == null) {
         throw new NoSuchProviderException("JSSE provider not found: " + var1);
      } else {
         return this;
      }
   }

   public SSLSocketFactoryCreatorBuilder withProvider(Provider var1) {
      this.tlsProvider = var1;
      return this;
   }

   public SSLSocketFactoryCreatorBuilder withKeyManager(KeyManager var1) {
      if (var1 == null) {
         this.keyManagers = null;
      } else {
         this.keyManagers = new KeyManager[]{var1};
      }

      return this;
   }

   public SSLSocketFactoryCreatorBuilder withKeyManagers(KeyManager[] var1) {
      this.keyManagers = var1;
      return this;
   }

   public SSLSocketFactoryCreator build() {
      return new SSLSocketFactoryCreator() {
         @Override
         public boolean isTrusted() {
            for (int var1 = 0; var1 != SSLSocketFactoryCreatorBuilder.this.trustManagers.length; var1++) {
               X509TrustManager var2 = SSLSocketFactoryCreatorBuilder.this.trustManagers[var1];
               if (var2.getAcceptedIssuers().length > 0) {
                  return true;
               }
            }

            return false;
         }

         @Override
         public SSLSocketFactory createFactory() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
            SSLContext var1;
            if (SSLSocketFactoryCreatorBuilder.this.tlsProvider != null) {
               var1 = SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion, SSLSocketFactoryCreatorBuilder.this.tlsProvider);
            } else {
               var1 = SSLContext.getInstance(SSLSocketFactoryCreatorBuilder.this.tlsVersion);
            }

            var1.init(
               SSLSocketFactoryCreatorBuilder.this.keyManagers,
               SSLSocketFactoryCreatorBuilder.this.trustManagers,
               SSLSocketFactoryCreatorBuilder.this.secureRandom
            );
            return var1.getSocketFactory();
         }
      };
   }
}
