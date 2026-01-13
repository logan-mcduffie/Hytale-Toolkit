package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.cms.KEKRecipientInfoGenerator;
import org.bouncycastle.operator.bc.BcSymmetricKeyWrapper;

public class BcKEKRecipientInfoGenerator extends KEKRecipientInfoGenerator {
   public BcKEKRecipientInfoGenerator(KEKIdentifier var1, BcSymmetricKeyWrapper var2) {
      super(var1, var2);
   }

   public BcKEKRecipientInfoGenerator(byte[] var1, BcSymmetricKeyWrapper var2) {
      this(new KEKIdentifier(var1, null, null), var2);
   }
}
