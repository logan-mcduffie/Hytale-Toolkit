package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.CertIOException;

class CRMFUtil {
   static void derEncodeToStream(ASN1Object var0, OutputStream var1) {
      try {
         var0.encodeTo(var1, "DER");
         var1.close();
      } catch (IOException var3) {
         throw new CRMFRuntimeException("unable to DER encode object: " + var3.getMessage(), var3);
      }
   }

   static void addExtension(ExtensionsGenerator var0, ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      try {
         var0.addExtension(var1, var2, var3);
      } catch (IOException var5) {
         throw new CertIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }
}
