package org.bouncycastle.cert.path;

import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.cert.X509CertificateHolder;

class CertPathUtils {
   static Set getCriticalExtensionsOIDs(X509CertificateHolder[] var0) {
      HashSet var1 = new HashSet();

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1.addAll(var0[var2].getCriticalExtensionOIDs());
      }

      return var1;
   }
}
