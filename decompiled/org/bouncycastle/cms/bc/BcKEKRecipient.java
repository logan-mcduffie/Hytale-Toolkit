package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEKRecipient;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.bc.BcSymmetricKeyUnwrapper;

public abstract class BcKEKRecipient implements KEKRecipient {
   private SymmetricKeyUnwrapper unwrapper;

   public BcKEKRecipient(BcSymmetricKeyUnwrapper var1) {
      this.unwrapper = var1;
   }

   protected CipherParameters extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      try {
         return CMSUtils.getBcKey(this.unwrapper.generateUnwrappedKey(var2, var3));
      } catch (OperatorException var5) {
         throw new CMSException("exception unwrapping key: " + var5.getMessage(), var5);
      }
   }
}
