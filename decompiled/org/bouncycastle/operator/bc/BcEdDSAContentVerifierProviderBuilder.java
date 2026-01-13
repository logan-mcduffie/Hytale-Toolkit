package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.OperatorCreationException;

public class BcEdDSAContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder {
   public static final byte[] DEFAULT_CONTEXT = new byte[0];

   @Override
   protected Signer createSigner(AlgorithmIdentifier var1) throws OperatorCreationException {
      return (Signer)(var1.getAlgorithm().equals(EdECObjectIdentifiers.id_Ed448) ? new Ed448Signer(DEFAULT_CONTEXT) : new Ed25519Signer());
   }

   @Override
   protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo var1) throws IOException {
      return PublicKeyFactory.createKey(var1);
   }
}
