package org.bouncycastle.cert.jcajce;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;

public class JcaCertStore extends CollectionStore {
   public JcaCertStore(Collection var1) throws CertificateEncodingException {
      super(convertCerts(var1));
   }

   private static Collection convertCerts(Collection var0) throws CertificateEncodingException {
      ArrayList var1 = new ArrayList(var0.size());

      for (Object var3 : var0) {
         if (var3 instanceof X509Certificate) {
            X509Certificate var4 = (X509Certificate)var3;

            try {
               var1.add(new X509CertificateHolder(var4.getEncoded()));
            } catch (IOException var6) {
               throw new CertificateEncodingException("unable to read encoding: " + var6.getMessage());
            }
         } else {
            var1.add((X509CertificateHolder)var3);
         }
      }

      return var1;
   }
}
