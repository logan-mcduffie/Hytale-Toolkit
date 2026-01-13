package org.bouncycastle.cert.ocsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;

class OCSPUtils {
   static final X509CertificateHolder[] EMPTY_CERTS = new X509CertificateHolder[0];
   static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
   static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());

   static Date extractDate(ASN1GeneralizedTime var0) {
      try {
         return var0.getDate();
      } catch (Exception var2) {
         throw new IllegalStateException("exception processing GeneralizedTime: " + var2.getMessage());
      }
   }

   static Set getCriticalExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(var0.getCriticalExtensionOIDs())));
   }

   static Set getNonCriticalExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(var0.getNonCriticalExtensionOIDs())));
   }

   static List getExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_LIST : Collections.unmodifiableList(Arrays.asList(var0.getExtensionOIDs()));
   }
}
