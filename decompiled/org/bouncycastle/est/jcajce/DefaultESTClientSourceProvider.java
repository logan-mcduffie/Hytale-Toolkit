package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Strings;

class DefaultESTClientSourceProvider implements ESTClientSourceProvider {
   private final SSLSocketFactory sslSocketFactory;
   private final JsseHostnameAuthorizer hostNameAuthorizer;
   private final int timeout;
   private final ChannelBindingProvider bindingProvider;
   private final Set<String> cipherSuites;
   private final Long absoluteLimit;
   private final boolean filterSupportedSuites;

   public DefaultESTClientSourceProvider(
      SSLSocketFactory var1, JsseHostnameAuthorizer var2, int var3, ChannelBindingProvider var4, Set<String> var5, Long var6, boolean var7
   ) throws GeneralSecurityException {
      this.sslSocketFactory = var1;
      this.hostNameAuthorizer = var2;
      this.timeout = var3;
      this.bindingProvider = var4;
      this.cipherSuites = var5;
      this.absoluteLimit = var6;
      this.filterSupportedSuites = var7;
   }

   @Override
   public Source makeSource(String var1, int var2) throws IOException {
      SSLSocket var3 = (SSLSocket)this.sslSocketFactory.createSocket(var1, var2);
      var3.setSoTimeout(this.timeout);
      if (this.cipherSuites != null && !this.cipherSuites.isEmpty()) {
         if (this.filterSupportedSuites) {
            HashSet var4 = new HashSet();
            String[] var5 = var3.getSupportedCipherSuites();

            for (int var6 = 0; var6 != var5.length; var6++) {
               var4.add(var5[var6]);
            }

            ArrayList var11 = new ArrayList();

            for (String var8 : this.cipherSuites) {
               if (var4.contains(var8)) {
                  var11.add(var8);
               }
            }

            if (var11.isEmpty()) {
               throw new IllegalStateException("No supplied cipher suite is supported by the provider.");
            }

            var3.setEnabledCipherSuites(var11.toArray(new String[var11.size()]));
         } else {
            var3.setEnabledCipherSuites(this.cipherSuites.toArray(new String[this.cipherSuites.size()]));
         }
      }

      var3.startHandshake();
      if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(var1, var3.getSession())) {
         throw new IOException("Host name could not be verified.");
      } else {
         String var10 = Strings.toLowerCase(var3.getSession().getCipherSuite());
         if (!var10.contains("_des_") && !var10.contains("_des40_") && !var10.contains("_3des_")) {
            if (Strings.toLowerCase(var3.getSession().getCipherSuite()).contains("null")) {
               throw new IOException("EST clients must not use NULL ciphers");
            } else if (Strings.toLowerCase(var3.getSession().getCipherSuite()).contains("anon")) {
               throw new IOException("EST clients must not use anon ciphers");
            } else if (Strings.toLowerCase(var3.getSession().getCipherSuite()).contains("export")) {
               throw new IOException("EST clients must not use export ciphers");
            } else if (var3.getSession().getProtocol().equalsIgnoreCase("tlsv1")) {
               try {
                  var3.close();
               } catch (Exception var9) {
               }

               throw new IOException("EST clients must not use TLSv1");
            } else if (this.hostNameAuthorizer != null && !this.hostNameAuthorizer.verified(var1, var3.getSession())) {
               throw new IOException("Hostname was not verified: " + var1);
            } else {
               return new LimitedSSLSocketSource(var3, this.bindingProvider, this.absoluteLimit);
            }
         } else {
            throw new IOException("EST clients must not use DES ciphers");
         }
      }
   }
}
