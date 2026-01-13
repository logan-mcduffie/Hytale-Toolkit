package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.operator.OperatorCreationException;

public class BcEdECContentSignerBuilder extends BcContentSignerBuilder {
   public BcEdECContentSignerBuilder(AlgorithmIdentifier var1) {
      super(var1, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
   }

   @Override
   protected Signer createSigner(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws OperatorCreationException {
      if (var1.getAlgorithm().equals(EdECObjectIdentifiers.id_Ed25519)) {
         return new Ed25519Signer();
      } else {
         throw new IllegalStateException("unknown signature type");
      }
   }
}
