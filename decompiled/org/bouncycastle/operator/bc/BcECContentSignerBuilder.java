package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.operator.OperatorCreationException;

public class BcECContentSignerBuilder extends BcContentSignerBuilder {
   public BcECContentSignerBuilder(AlgorithmIdentifier var1, AlgorithmIdentifier var2) {
      super(var1, var2);
   }

   @Override
   protected Signer createSigner(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws OperatorCreationException {
      ExtendedDigest var3 = this.digestProvider.get(var2);
      return new DSADigestSigner(new ECDSASigner(), var3);
   }
}
