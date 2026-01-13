package org.bouncycastle.est.jcajce;

import java.net.Socket;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.est.ESTClientProvider;
import org.bouncycastle.est.ESTService;
import org.bouncycastle.est.ESTServiceBuilder;

public class JsseESTServiceBuilder extends ESTServiceBuilder {
   protected SSLSocketFactoryCreator socketFactoryCreator;
   protected JsseHostnameAuthorizer hostNameAuthorizer = new JsseDefaultHostnameAuthorizer(null);
   protected int timeoutMillis = 0;
   protected ChannelBindingProvider bindingProvider;
   protected Set<String> supportedSuites = new HashSet<>();
   protected Long absoluteLimit;
   protected SSLSocketFactoryCreatorBuilder sslSocketFactoryCreatorBuilder;
   protected boolean filterCipherSuites = true;

   public JsseESTServiceBuilder(String var1, int var2, SSLSocketFactoryCreator var3) {
      super(var1 + ":" + var2);
      if (var3 == null) {
         throw new NullPointerException("No socket factory creator.");
      } else {
         this.socketFactoryCreator = var3;
      }
   }

   public JsseESTServiceBuilder(String var1, SSLSocketFactoryCreator var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("No socket factory creator.");
      } else {
         this.socketFactoryCreator = var2;
      }
   }

   public JsseESTServiceBuilder(String var1) {
      super(var1);
      this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(JcaJceUtils.getTrustAllTrustManager());
   }

   public JsseESTServiceBuilder(String var1, int var2, X509TrustManager var3) {
      super(var1 + ":" + var2);
      this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(var3);
   }

   public JsseESTServiceBuilder(String var1, X509TrustManager var2) {
      super(var1);
      this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(var2);
   }

   public JsseESTServiceBuilder(String var1, int var2, X509TrustManager[] var3) {
      this(var1 + ":" + var2, var3);
   }

   public JsseESTServiceBuilder(String var1, X509TrustManager[] var2) {
      super(var1);
      this.sslSocketFactoryCreatorBuilder = new SSLSocketFactoryCreatorBuilder(var2);
   }

   public JsseESTServiceBuilder withHostNameAuthorizer(JsseHostnameAuthorizer var1) {
      this.hostNameAuthorizer = var1;
      return this;
   }

   public JsseESTServiceBuilder withClientProvider(ESTClientProvider var1) {
      this.clientProvider = var1;
      return this;
   }

   public JsseESTServiceBuilder withTimeout(int var1) {
      this.timeoutMillis = var1;
      return this;
   }

   public JsseESTServiceBuilder withReadLimit(long var1) {
      this.absoluteLimit = var1;
      return this;
   }

   public JsseESTServiceBuilder withChannelBindingProvider(ChannelBindingProvider var1) {
      this.bindingProvider = var1;
      return this;
   }

   public JsseESTServiceBuilder addCipherSuites(String var1) {
      this.supportedSuites.add(var1);
      return this;
   }

   public JsseESTServiceBuilder addCipherSuites(String[] var1) {
      this.supportedSuites.addAll(Arrays.asList(var1));
      return this;
   }

   public JsseESTServiceBuilder withTLSVersion(String var1) {
      if (this.socketFactoryCreator != null) {
         throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
      } else {
         this.sslSocketFactoryCreatorBuilder.withTLSVersion(var1);
         return this;
      }
   }

   public JsseESTServiceBuilder withSecureRandom(SecureRandom var1) {
      if (this.socketFactoryCreator != null) {
         throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
      } else {
         this.sslSocketFactoryCreatorBuilder.withSecureRandom(var1);
         return this;
      }
   }

   public JsseESTServiceBuilder withProvider(String var1) throws NoSuchProviderException {
      if (this.socketFactoryCreator != null) {
         throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
      } else {
         this.sslSocketFactoryCreatorBuilder.withProvider(var1);
         return this;
      }
   }

   public JsseESTServiceBuilder withProvider(Provider var1) {
      if (this.socketFactoryCreator != null) {
         throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
      } else {
         this.sslSocketFactoryCreatorBuilder.withProvider(var1);
         return this;
      }
   }

   public JsseESTServiceBuilder withKeyManager(KeyManager var1) {
      if (this.socketFactoryCreator != null) {
         throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
      } else {
         this.sslSocketFactoryCreatorBuilder.withKeyManager(var1);
         return this;
      }
   }

   public JsseESTServiceBuilder withKeyManagers(KeyManager[] var1) {
      if (this.socketFactoryCreator != null) {
         throw new IllegalStateException("Socket Factory Creator was defined in the constructor.");
      } else {
         this.sslSocketFactoryCreatorBuilder.withKeyManagers(var1);
         return this;
      }
   }

   public JsseESTServiceBuilder withFilterCipherSuites(boolean var1) {
      this.filterCipherSuites = var1;
      return this;
   }

   @Override
   public ESTService build() {
      if (this.bindingProvider == null) {
         this.bindingProvider = new ChannelBindingProvider() {
            @Override
            public boolean canAccessChannelBinding(Socket var1) {
               return false;
            }

            @Override
            public byte[] getChannelBinding(Socket var1, String var2) {
               return null;
            }
         };
      }

      if (this.socketFactoryCreator == null) {
         this.socketFactoryCreator = this.sslSocketFactoryCreatorBuilder.build();
      }

      if (this.clientProvider == null) {
         this.clientProvider = new DefaultESTHttpClientProvider(
            this.hostNameAuthorizer,
            this.socketFactoryCreator,
            this.timeoutMillis,
            this.bindingProvider,
            this.supportedSuites,
            this.absoluteLimit,
            this.filterCipherSuites
         );
      }

      return super.build();
   }
}
