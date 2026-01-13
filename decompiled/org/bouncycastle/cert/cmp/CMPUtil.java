package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

class CMPUtil {
   static byte[] calculateCertHash(ASN1Object var0, AlgorithmIdentifier var1, DigestCalculatorProvider var2, DigestAlgorithmIdentifierFinder var3) throws CMPException {
      AlgorithmIdentifier var4 = var3.find(var1);
      if (var4 == null) {
         throw new CMPException("cannot find digest algorithm from signature algorithm");
      } else {
         return calculateDigest(var0, var4, var2);
      }
   }

   static byte[] calculateDigest(ASN1Object var0, AlgorithmIdentifier var1, DigestCalculatorProvider var2) throws CMPException {
      DigestCalculator var3 = getDigestCalculator(var1, var2);
      derEncodeToStream(var0, var3.getOutputStream());
      return var3.getDigest();
   }

   static void derEncodeToStream(ASN1Object var0, OutputStream var1) {
      try {
         var0.encodeTo(var1, "DER");
         var1.close();
      } catch (IOException var3) {
         throw new CMPRuntimeException("unable to DER encode object: " + var3.getMessage(), var3);
      }
   }

   static DigestCalculator getDigestCalculator(AlgorithmIdentifier var0, DigestCalculatorProvider var1) throws CMPException {
      try {
         return var1.get(var0);
      } catch (OperatorCreationException var3) {
         throw new CMPException("unable to create digester: " + var3.getMessage(), var3);
      }
   }
}
