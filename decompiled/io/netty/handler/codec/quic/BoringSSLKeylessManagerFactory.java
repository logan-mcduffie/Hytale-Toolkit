package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import org.jetbrains.annotations.Nullable;

public final class BoringSSLKeylessManagerFactory extends KeyManagerFactory {
   final BoringSSLAsyncPrivateKeyMethod privateKeyMethod;

   private BoringSSLKeylessManagerFactory(KeyManagerFactory keyManagerFactory, BoringSSLAsyncPrivateKeyMethod privateKeyMethod) {
      super(new BoringSSLKeylessManagerFactory.KeylessManagerFactorySpi(keyManagerFactory), keyManagerFactory.getProvider(), keyManagerFactory.getAlgorithm());
      this.privateKeyMethod = Objects.requireNonNull(privateKeyMethod, "privateKeyMethod");
   }

   public static BoringSSLKeylessManagerFactory newKeyless(BoringSSLAsyncPrivateKeyMethod privateKeyMethod, File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      InputStream chainInputStream = Files.newInputStream(chain.toPath());

      BoringSSLKeylessManagerFactory var3;
      try {
         var3 = newKeyless(privateKeyMethod, chainInputStream);
      } catch (Throwable var6) {
         if (chainInputStream != null) {
            try {
               chainInputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (chainInputStream != null) {
         chainInputStream.close();
      }

      return var3;
   }

   public static BoringSSLKeylessManagerFactory newKeyless(BoringSSLAsyncPrivateKeyMethod privateKeyMethod, InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newKeyless(privateKeyMethod, QuicSslContext.toX509Certificates0(chain));
   }

   public static BoringSSLKeylessManagerFactory newKeyless(BoringSSLAsyncPrivateKeyMethod privateKeyMethod, X509Certificate... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      ObjectUtil.checkNotNull(certificateChain, "certificateChain");
      KeyStore store = new BoringSSLKeylessManagerFactory.KeylessKeyStore((X509Certificate[])certificateChain.clone());
      store.load(null, null);
      BoringSSLKeylessManagerFactory factory = new BoringSSLKeylessManagerFactory(
         KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()), privateKeyMethod
      );
      factory.init(store, null);
      return factory;
   }

   private static final class KeylessKeyStore extends KeyStore {
      private static final String ALIAS = "key";

      private KeylessKeyStore(final X509Certificate[] certificateChain) {
         super(new KeyStoreSpi() {
            private final Date creationDate = new Date();

            @Nullable
            @Override
            public Key engineGetKey(String alias, char[] password) {
               return this.engineContainsAlias(alias) ? BoringSSLKeylessPrivateKey.INSTANCE : null;
            }

            @Override
            public Certificate @Nullable [] engineGetCertificateChain(String alias) {
               return this.engineContainsAlias(alias) ? (Certificate[])certificateChain.clone() : null;
            }

            @Nullable
            @Override
            public Certificate engineGetCertificate(String alias) {
               return this.engineContainsAlias(alias) ? certificateChain[0] : null;
            }

            @Nullable
            @Override
            public Date engineGetCreationDate(String alias) {
               return this.engineContainsAlias(alias) ? this.creationDate : null;
            }

            @Override
            public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public void engineDeleteEntry(String alias) throws KeyStoreException {
               throw new KeyStoreException("Not supported");
            }

            @Override
            public Enumeration<String> engineAliases() {
               return Collections.enumeration(Collections.singleton("key"));
            }

            @Override
            public boolean engineContainsAlias(String alias) {
               return "key".equals(alias);
            }

            @Override
            public int engineSize() {
               return 1;
            }

            @Override
            public boolean engineIsKeyEntry(String alias) {
               return this.engineContainsAlias(alias);
            }

            @Override
            public boolean engineIsCertificateEntry(String alias) {
               return this.engineContainsAlias(alias);
            }

            @Nullable
            @Override
            public String engineGetCertificateAlias(Certificate cert) {
               if (cert instanceof X509Certificate) {
                  for (X509Certificate x509Certificate : certificateChain) {
                     if (x509Certificate.equals(cert)) {
                        return "key";
                     }
                  }
               }

               return null;
            }

            @Override
            public void engineStore(OutputStream stream, char[] password) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void engineLoad(@Nullable InputStream stream, char @Nullable [] password) {
               if (stream != null && password != null) {
                  throw new UnsupportedOperationException();
               }
            }
         }, null, "keyless");
      }
   }

   private static final class KeylessManagerFactorySpi extends KeyManagerFactorySpi {
      private final KeyManagerFactory keyManagerFactory;

      KeylessManagerFactorySpi(KeyManagerFactory keyManagerFactory) {
         this.keyManagerFactory = Objects.requireNonNull(keyManagerFactory, "keyManagerFactory");
      }

      @Override
      protected void engineInit(KeyStore ks, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
         this.keyManagerFactory.init(ks, password);
      }

      @Override
      protected void engineInit(ManagerFactoryParameters spec) {
         throw new UnsupportedOperationException("Not supported");
      }

      @Override
      protected KeyManager[] engineGetKeyManagers() {
         return this.keyManagerFactory.getKeyManagers();
      }
   }
}
