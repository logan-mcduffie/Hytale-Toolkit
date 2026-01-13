package org.bouncycastle.dvcs;

import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

public class SignedDVCSMessageGenerator {
   private final CMSSignedDataGenerator signedDataGen;

   public SignedDVCSMessageGenerator(CMSSignedDataGenerator var1) {
      this.signedDataGen = var1;
   }

   public CMSSignedData build(DVCSMessage var1) throws DVCSException {
      try {
         byte[] var2 = var1.getContent().toASN1Primitive().getEncoded("DER");
         return this.signedDataGen.generate(new CMSProcessableByteArray(var1.getContentType(), var2), true);
      } catch (CMSException var3) {
         throw new DVCSException("Could not sign DVCS request", var3);
      } catch (IOException var4) {
         throw new DVCSException("Could not encode DVCS request", var4);
      }
   }
}
