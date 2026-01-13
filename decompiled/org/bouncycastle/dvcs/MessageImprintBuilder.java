package org.bouncycastle.dvcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.DigestCalculator;

public class MessageImprintBuilder {
   private final DigestCalculator digestCalculator;

   public MessageImprintBuilder(DigestCalculator var1) {
      this.digestCalculator = var1;
   }

   public MessageImprint build(byte[] var1) throws DVCSException {
      try {
         OutputStream var2 = this.digestCalculator.getOutputStream();
         var2.write(var1);
         var2.close();
         return new MessageImprint(new DigestInfo(this.digestCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
      } catch (Exception var3) {
         throw new DVCSException("unable to build MessageImprint: " + var3.getMessage(), var3);
      }
   }
}
