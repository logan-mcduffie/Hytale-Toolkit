package io.netty.handler.ssl.util;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

public abstract class SimpleKeyManagerFactory extends KeyManagerFactory {
   private static final Provider PROVIDER = new Provider("", 0.0, "") {
      private static final long serialVersionUID = -2680540247105807895L;
   };
   private static final FastThreadLocal<SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi>() {
      protected SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi initialValue() {
         return new SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi();
      }
   };

   protected SimpleKeyManagerFactory() {
      this("");
   }

   protected SimpleKeyManagerFactory(String name) {
      super(CURRENT_SPI.get(), PROVIDER, ObjectUtil.checkNotNull(name, "name"));
      CURRENT_SPI.get().init(this);
      CURRENT_SPI.remove();
   }

   protected abstract void engineInit(KeyStore var1, char[] var2) throws Exception;

   protected abstract void engineInit(ManagerFactoryParameters var1) throws Exception;

   protected abstract KeyManager[] engineGetKeyManagers();

   private static final class SimpleKeyManagerFactorySpi extends KeyManagerFactorySpi {
      private SimpleKeyManagerFactory parent;
      private volatile KeyManager[] keyManagers;

      private SimpleKeyManagerFactorySpi() {
      }

      void init(SimpleKeyManagerFactory parent) {
         this.parent = parent;
      }

      @Override
      protected void engineInit(KeyStore keyStore, char[] pwd) throws KeyStoreException {
         try {
            this.parent.engineInit(keyStore, pwd);
         } catch (KeyStoreException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new KeyStoreException(var5);
         }
      }

      @Override
      protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
         try {
            this.parent.engineInit(managerFactoryParameters);
         } catch (InvalidAlgorithmParameterException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new InvalidAlgorithmParameterException(var4);
         }
      }

      @Override
      protected KeyManager[] engineGetKeyManagers() {
         KeyManager[] keyManagers = this.keyManagers;
         if (keyManagers == null) {
            keyManagers = this.parent.engineGetKeyManagers();
            wrapIfNeeded(keyManagers);
            this.keyManagers = keyManagers;
         }

         return (KeyManager[])keyManagers.clone();
      }

      private static void wrapIfNeeded(KeyManager[] keyManagers) {
         for (int i = 0; i < keyManagers.length; i++) {
            KeyManager tm = keyManagers[i];
            if (tm instanceof X509KeyManager && !(tm instanceof X509ExtendedKeyManager)) {
               keyManagers[i] = new X509KeyManagerWrapper((X509KeyManager)tm);
            }
         }
      }
   }
}
