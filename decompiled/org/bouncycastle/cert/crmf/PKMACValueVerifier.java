package org.bouncycastle.cert.crmf;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Arrays;

class PKMACValueVerifier {
   private final PKMACBuilder builder;

   public PKMACValueVerifier(PKMACBuilder var1) {
      this.builder = var1;
   }

   public boolean isValid(PKMACValue var1, char[] var2, SubjectPublicKeyInfo var3) throws CRMFException {
      this.builder.setParameters(PBMParameter.getInstance(var1.getAlgId().getParameters()));
      MacCalculator var4 = this.builder.build(var2);
      OutputStream var5 = var4.getOutputStream();

      try {
         var5.write(var3.getEncoded("DER"));
         var5.close();
      } catch (IOException var7) {
         throw new CRMFException("exception encoding mac input: " + var7.getMessage(), var7);
      }

      return Arrays.constantTimeAreEqual(var4.getMac(), var1.getValue().getOctets());
   }
}
