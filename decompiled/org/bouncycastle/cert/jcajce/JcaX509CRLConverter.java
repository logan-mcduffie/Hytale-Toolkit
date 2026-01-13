package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import org.bouncycastle.cert.X509CRLHolder;

public class JcaX509CRLConverter {
   private CertHelper helper = new DefaultCertHelper();

   public JcaX509CRLConverter() {
      this.helper = new DefaultCertHelper();
   }

   public JcaX509CRLConverter setProvider(Provider var1) {
      this.helper = new ProviderCertHelper(var1);
      return this;
   }

   public JcaX509CRLConverter setProvider(String var1) {
      this.helper = new NamedCertHelper(var1);
      return this;
   }

   public X509CRL getCRL(X509CRLHolder var1) throws CRLException {
      try {
         CertificateFactory var2 = this.helper.getCertificateFactory("X.509");
         return (X509CRL)var2.generateCRL(new ByteArrayInputStream(var1.getEncoded()));
      } catch (IOException var3) {
         throw new JcaX509CRLConverter.ExCRLException("exception parsing certificate: " + var3.getMessage(), var3);
      } catch (NoSuchProviderException var4) {
         throw new JcaX509CRLConverter.ExCRLException("cannot find required provider:" + var4.getMessage(), var4);
      } catch (CertificateException var5) {
         throw new JcaX509CRLConverter.ExCRLException("cannot create factory: " + var5.getMessage(), var5);
      }
   }

   private static class ExCRLException extends CRLException {
      private Throwable cause;

      public ExCRLException(String var1, Throwable var2) {
         super(var1);
         this.cause = var2;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }
}
