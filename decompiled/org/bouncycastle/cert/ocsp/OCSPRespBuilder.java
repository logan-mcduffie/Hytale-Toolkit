package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;
import org.bouncycastle.asn1.ocsp.ResponseBytes;

public class OCSPRespBuilder {
   public static final int SUCCESSFUL = 0;
   public static final int MALFORMED_REQUEST = 1;
   public static final int INTERNAL_ERROR = 2;
   public static final int TRY_LATER = 3;
   public static final int SIG_REQUIRED = 5;
   public static final int UNAUTHORIZED = 6;

   public OCSPResp build(int var1, Object var2) throws OCSPException {
      if (var2 == null) {
         return new OCSPResp(new OCSPResponse(new OCSPResponseStatus(var1), null));
      } else if (var2 instanceof BasicOCSPResp) {
         BasicOCSPResp var3 = (BasicOCSPResp)var2;

         DEROctetString var4;
         try {
            var4 = new DEROctetString(var3.getEncoded());
         } catch (IOException var6) {
            throw new OCSPException("can't encode object.", var6);
         }

         ResponseBytes var5 = new ResponseBytes(OCSPObjectIdentifiers.id_pkix_ocsp_basic, var4);
         return new OCSPResp(new OCSPResponse(new OCSPResponseStatus(var1), var5));
      } else {
         throw new OCSPException("unknown response object");
      }
   }
}
