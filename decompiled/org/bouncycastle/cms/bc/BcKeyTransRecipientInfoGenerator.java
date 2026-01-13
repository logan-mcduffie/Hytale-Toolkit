package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.bc.BcAsymmetricKeyWrapper;

public abstract class BcKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
   public BcKeyTransRecipientInfoGenerator(X509CertificateHolder var1, BcAsymmetricKeyWrapper var2) {
      super(new IssuerAndSerialNumber(var1.toASN1Structure()), var2);
   }

   public BcKeyTransRecipientInfoGenerator(byte[] var1, BcAsymmetricKeyWrapper var2) {
      super(var1, var2);
   }
}
