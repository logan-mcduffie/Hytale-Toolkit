package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;

public class BcHssLmsContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder {
   @Override
   protected Signer createSigner(AlgorithmIdentifier var1) throws OperatorCreationException {
      return new BcHssLmsContentSignerBuilder.HssSigner();
   }

   @Override
   protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo var1) throws IOException {
      return PublicKeyFactory.createKey(var1);
   }
}
