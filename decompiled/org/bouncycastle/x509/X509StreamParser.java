package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import org.bouncycastle.x509.util.StreamParser;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509StreamParser implements StreamParser {
   private Provider _provider;
   private X509StreamParserSpi _spi;

   public static X509StreamParser getInstance(String var0) throws NoSuchParserException {
      try {
         X509Util.Implementation var1 = X509Util.getImplementation("X509StreamParser", var0);
         return createParser(var1);
      } catch (NoSuchAlgorithmException var2) {
         throw new NoSuchParserException(var2.getMessage());
      }
   }

   public static X509StreamParser getInstance(String var0, String var1) throws NoSuchParserException, NoSuchProviderException {
      return getInstance(var0, X509Util.getProvider(var1));
   }

   public static X509StreamParser getInstance(String var0, Provider var1) throws NoSuchParserException {
      try {
         X509Util.Implementation var2 = X509Util.getImplementation("X509StreamParser", var0, var1);
         return createParser(var2);
      } catch (NoSuchAlgorithmException var3) {
         throw new NoSuchParserException(var3.getMessage());
      }
   }

   private static X509StreamParser createParser(X509Util.Implementation var0) {
      X509StreamParserSpi var1 = (X509StreamParserSpi)var0.getEngine();
      return new X509StreamParser(var0.getProvider(), var1);
   }

   private X509StreamParser(Provider var1, X509StreamParserSpi var2) {
      this._provider = var1;
      this._spi = var2;
   }

   public Provider getProvider() {
      return this._provider;
   }

   public void init(InputStream var1) {
      this._spi.engineInit(var1);
   }

   public void init(byte[] var1) {
      this._spi.engineInit(new ByteArrayInputStream(var1));
   }

   @Override
   public Object read() throws StreamParsingException {
      return this._spi.engineRead();
   }

   @Override
   public Collection readAll() throws StreamParsingException {
      return this._spi.engineReadAll();
   }
}
