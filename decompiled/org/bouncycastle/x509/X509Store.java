package org.bouncycastle.x509;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

/** @deprecated */
public class X509Store implements Store {
   private Provider _provider;
   private X509StoreSpi _spi;

   public static X509Store getInstance(String var0, X509StoreParameters var1) throws NoSuchStoreException {
      try {
         X509Util.Implementation var2 = X509Util.getImplementation("X509Store", var0);
         return createStore(var2, var1);
      } catch (NoSuchAlgorithmException var3) {
         throw new NoSuchStoreException(var3.getMessage());
      }
   }

   public static X509Store getInstance(String var0, X509StoreParameters var1, String var2) throws NoSuchStoreException, NoSuchProviderException {
      return getInstance(var0, var1, X509Util.getProvider(var2));
   }

   public static X509Store getInstance(String var0, X509StoreParameters var1, Provider var2) throws NoSuchStoreException {
      try {
         X509Util.Implementation var3 = X509Util.getImplementation("X509Store", var0, var2);
         return createStore(var3, var1);
      } catch (NoSuchAlgorithmException var4) {
         throw new NoSuchStoreException(var4.getMessage());
      }
   }

   private static X509Store createStore(X509Util.Implementation var0, X509StoreParameters var1) {
      X509StoreSpi var2 = (X509StoreSpi)var0.getEngine();
      var2.engineInit(var1);
      return new X509Store(var0.getProvider(), var2);
   }

   private X509Store(Provider var1, X509StoreSpi var2) {
      this._provider = var1;
      this._spi = var2;
   }

   public Provider getProvider() {
      return this._provider;
   }

   @Override
   public Collection getMatches(Selector var1) {
      return this._spi.engineGetMatches(var1);
   }
}
