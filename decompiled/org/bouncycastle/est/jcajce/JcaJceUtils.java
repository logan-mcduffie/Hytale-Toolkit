package org.bouncycastle.est.jcajce;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;

public class JcaJceUtils {
   public static X509TrustManager getTrustAllTrustManager() {
      return new X509TrustManager() {
         @Override
         public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
            throw new IllegalStateException("attempt to authenticate client connection");
         }

         @Override
         public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         }

         @Override
         public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
         }
      };
   }

   public static X509TrustManager[] getCertPathTrustManager(final Set<TrustAnchor> var0, final CRL[] var1) {
      final X509Certificate[] var2 = new X509Certificate[var0.size()];
      int var3 = 0;

      for (TrustAnchor var5 : var0) {
         var2[var3++] = var5.getTrustedCert();
      }

      return new X509TrustManager[]{new X509TrustManager() {
         @Override
         public void checkClientTrusted(X509Certificate[] var1x, String var2x) throws CertificateException {
            throw new IllegalStateException("attempt to authenticate client connection");
         }

         @Override
         public void checkServerTrusted(X509Certificate[] var1x, String var2x) throws CertificateException {
            try {
               CertStore var3x = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(var1x)), "BC");
               CertPathBuilder var4 = CertPathBuilder.getInstance("PKIX", "BC");
               X509CertSelector var5 = new X509CertSelector();
               var5.setCertificate(var1x[0]);
               PKIXBuilderParameters var6 = new PKIXBuilderParameters(var0, var5);
               var6.addCertStore(var3x);
               if (var1 != null) {
                  var6.setRevocationEnabled(true);
                  var6.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(var1))));
               } else {
                  var6.setRevocationEnabled(false);
               }

               PKIXCertPathValidatorResult var7 = (PKIXCertPathValidatorResult)var4.build(var6);
               JcaJceUtils.validateServerCertUsage(var1x[0]);
            } catch (CertificateException var8) {
               throw var8;
            } catch (GeneralSecurityException var9) {
               throw new CertificateException("unable to process certificates: " + var9.getMessage(), var9);
            }
         }

         @Override
         public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] var1x = new X509Certificate[var2.length];
            System.arraycopy(var2, 0, var1x, 0, var1x.length);
            return var1x;
         }
      }};
   }

   public static void validateServerCertUsage(X509Certificate var0) throws CertificateException {
      try {
         X509CertificateHolder var1 = new X509CertificateHolder(var0.getEncoded());
         KeyUsage var2 = KeyUsage.fromExtensions(var1.getExtensions());
         if (var2 != null) {
            if (var2.hasUsages(4)) {
               throw new CertificateException("Key usage must not contain keyCertSign");
            }

            if (!var2.hasUsages(128) && !var2.hasUsages(32)) {
               throw new CertificateException("Key usage must be none, digitalSignature or keyEncipherment");
            }
         }

         ExtendedKeyUsage var3 = ExtendedKeyUsage.fromExtensions(var1.getExtensions());
         if (var3 != null
            && !var3.hasKeyPurposeId(KeyPurposeId.id_kp_serverAuth)
            && !var3.hasKeyPurposeId(KeyPurposeId.id_kp_msSGC)
            && !var3.hasKeyPurposeId(KeyPurposeId.id_kp_nsSGC)) {
            throw new CertificateException("Certificate extended key usage must include serverAuth, msSGC or nsSGC");
         }
      } catch (CertificateException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new CertificateException(var5.getMessage(), var5);
      }
   }

   public static KeyManagerFactory createKeyManagerFactory(String var0, String var1, KeyStore var2, char[] var3) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
      KeyManagerFactory var4 = null;
      if (var0 == null && var1 == null) {
         var4 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      } else if (var1 == null) {
         var4 = KeyManagerFactory.getInstance(var0);
      } else {
         var4 = KeyManagerFactory.getInstance(var0, var1);
      }

      var4.init(var2, var3);
      return var4;
   }
}
