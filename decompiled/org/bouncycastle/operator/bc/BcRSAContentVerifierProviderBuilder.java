package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;

public class BcRSAContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder {
   private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;

   public BcRSAContentVerifierProviderBuilder(DigestAlgorithmIdentifierFinder var1) {
      this.digestAlgorithmFinder = var1;
   }

   @Override
   protected Signer createSigner(AlgorithmIdentifier var1) throws OperatorCreationException {
      AlgorithmIdentifier var2 = this.digestAlgorithmFinder.find(var1);
      ExtendedDigest var3 = this.digestProvider.get(var2);
      return new RSADigestSigner(var3);
   }

   @Override
   protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo var1) throws IOException {
      return PublicKeyFactory.createKey(var1);
   }
}
