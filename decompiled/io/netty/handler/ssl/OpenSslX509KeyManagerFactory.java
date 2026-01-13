package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.SSL;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509KeyManager;

public final class OpenSslX509KeyManagerFactory extends KeyManagerFactory {
   private final OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi spi;

   public OpenSslX509KeyManagerFactory() {
      this(newOpenSslKeyManagerFactorySpi(null));
   }

   public OpenSslX509KeyManagerFactory(Provider provider) {
      this(newOpenSslKeyManagerFactorySpi(provider));
   }

   public OpenSslX509KeyManagerFactory(String algorithm, Provider provider) throws NoSuchAlgorithmException {
      this(newOpenSslKeyManagerFactorySpi(algorithm, provider));
   }

   private OpenSslX509KeyManagerFactory(OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi spi) {
      super(spi, spi.kmf.getProvider(), spi.kmf.getAlgorithm());
      this.spi = spi;
   }

   private static OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(Provider provider) {
      try {
         return newOpenSslKeyManagerFactorySpi(null, provider);
      } catch (NoSuchAlgorithmException var2) {
         throw new IllegalStateException(var2);
      }
   }

   private static OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(String algorithm, Provider provider) throws NoSuchAlgorithmException {
      if (algorithm == null) {
         algorithm = KeyManagerFactory.getDefaultAlgorithm();
      }

      return new OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi(
         provider == null ? KeyManagerFactory.getInstance(algorithm) : KeyManagerFactory.getInstance(algorithm, provider)
      );
   }

   OpenSslKeyMaterialProvider newProvider() {
      return this.spi.newProvider();
   }

   public static OpenSslX509KeyManagerFactory newEngineBased(File certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newEngineBased(SslContext.toX509Certificates(certificateChain), password);
   }

   public static OpenSslX509KeyManagerFactory newEngineBased(X509Certificate[] certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      ObjectUtil.checkNotNull(certificateChain, "certificateChain");
      KeyStore store = new OpenSslX509KeyManagerFactory.OpenSslKeyStore((X509Certificate[])certificateChain.clone(), false);
      store.load(null, null);
      OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
      factory.init(store, password == null ? null : password.toCharArray());
      return factory;
   }

   public static OpenSslX509KeyManagerFactory newKeyless(File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newKeyless(SslContext.toX509Certificates(chain));
   }

   public static OpenSslX509KeyManagerFactory newKeyless(InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      return newKeyless(SslContext.toX509Certificates(chain));
   }

   public static OpenSslX509KeyManagerFactory newKeyless(X509Certificate... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      ObjectUtil.checkNotNull(certificateChain, "certificateChain");
      KeyStore store = new OpenSslX509KeyManagerFactory.OpenSslKeyStore((X509Certificate[])certificateChain.clone(), true);
      store.load(null, null);
      OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
      factory.init(store, null);
      return factory;
   }

   private static final class OpenSslKeyManagerFactorySpi extends KeyManagerFactorySpi {
      final KeyManagerFactory kmf;
      private volatile OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory providerFactory;

      OpenSslKeyManagerFactorySpi(KeyManagerFactory kmf) {
         this.kmf = ObjectUtil.checkNotNull(kmf, "kmf");
      }

      @Override
      protected synchronized void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
         if (this.providerFactory != null) {
            throw new KeyStoreException("Already initialized");
         } else if (!keyStore.aliases().hasMoreElements()) {
            throw new KeyStoreException("No aliases found");
         } else {
            this.kmf.init(keyStore, chars);
            this.providerFactory = new OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory(
               ReferenceCountedOpenSslContext.chooseX509KeyManager(this.kmf.getKeyManagers()), password(chars), Collections.list(keyStore.aliases())
            );
         }
      }

      private static String password(char[] password) {
         return password != null && password.length != 0 ? new String(password) : null;
      }

      @Override
      protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
         throw new InvalidAlgorithmParameterException("Not supported");
      }

      @Override
      protected KeyManager[] engineGetKeyManagers() {
         OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory providerFactory = this.providerFactory;
         if (providerFactory == null) {
            throw new IllegalStateException("engineInit(...) not called yet");
         } else {
            return new KeyManager[]{providerFactory.keyManager};
         }
      }

      OpenSslKeyMaterialProvider newProvider() {
         OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory providerFactory = this.providerFactory;
         if (providerFactory == null) {
            throw new IllegalStateException("engineInit(...) not called yet");
         } else {
            return providerFactory.newProvider();
         }
      }

      private static final class ProviderFactory {
         private final X509KeyManager keyManager;
         private final String password;
         private final Iterable<String> aliases;

         ProviderFactory(X509KeyManager keyManager, String password, Iterable<String> aliases) {
            this.keyManager = keyManager;
            this.password = password;
            this.aliases = aliases;
         }

         OpenSslKeyMaterialProvider newProvider() {
            return new OpenSslX509KeyManagerFactory.OpenSslKeyManagerFactorySpi.ProviderFactory.OpenSslPopulatedKeyMaterialProvider(
               this.keyManager, this.password, this.aliases
            );
         }

         private static final class OpenSslPopulatedKeyMaterialProvider extends OpenSslKeyMaterialProvider {
            private final Map<String, Object> materialMap = new HashMap<>();

            OpenSslPopulatedKeyMaterialProvider(X509KeyManager keyManager, String password, Iterable<String> aliases) {
               super(keyManager, password);
               boolean initComplete = false;

               try {
                  for (String alias : aliases) {
                     if (alias != null && !this.materialMap.containsKey(alias)) {
                        try {
                           this.materialMap.put(alias, super.chooseKeyMaterial(UnpooledByteBufAllocator.DEFAULT, alias));
                        } catch (Exception var11) {
                           this.materialMap.put(alias, var11);
                        }
                     }
                  }

                  initComplete = true;
               } finally {
                  if (!initComplete) {
                     this.destroy();
                  }
               }

               ObjectUtil.checkNonEmpty(this.materialMap, "materialMap");
            }

            @Override
            OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
               Object value = this.materialMap.get(alias);
               if (value == null) {
                  return null;
               } else if (value instanceof OpenSslKeyMaterial) {
                  return ((OpenSslKeyMaterial)value).retain();
               } else {
                  throw (Exception)value;
               }
            }

            @Override
            void destroy() {
               for (Object material : this.materialMap.values()) {
                  ReferenceCountUtil.release(material);
               }

               this.materialMap.clear();
            }
         }
      }
   }

   private static final class OpenSslKeyStore extends KeyStore {
      private OpenSslKeyStore(final X509Certificate[] certificateChain, final boolean keyless) {
         super(new KeyStoreSpi() {
            private final Date creationDate = new Date();

            @Override
            public Key engineGetKey(String alias, char[] password) throws UnrecoverableKeyException {
               if (this.engineContainsAlias(alias)) {
                  long privateKeyAddress;
                  if (keyless) {
                     privateKeyAddress = 0L;
                  } else {
                     try {
                        privateKeyAddress = SSL.loadPrivateKeyFromEngine(alias, password == null ? null : new String(password));
                     } catch (Exception var7) {
                        UnrecoverableKeyException keyException = new UnrecoverableKeyException("Unable to load key from engine");
                        keyException.initCause(var7);
                        throw keyException;
                     }
                  }

                  return new OpenSslPrivateKey(privateKeyAddress);
               } else {
                  return null;
               }
            }

            @Override
            public Certificate[] engineGetCertificateChain(String alias) {
               return this.engineContainsAlias(alias) ? (Certificate[])certificateChain.clone() : null;
            }

            @Override
            public Certificate engineGetCertificate(String alias) {
               return this.engineContainsAlias(alias) ? certificateChain[0] : null;
            }

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
            public void engineLoad(InputStream stream, char[] password) {
               if (stream != null && password != null) {
                  throw new UnsupportedOperationException();
               }
            }
         }, null, "native");
         OpenSsl.ensureAvailability();
      }
   }
}
