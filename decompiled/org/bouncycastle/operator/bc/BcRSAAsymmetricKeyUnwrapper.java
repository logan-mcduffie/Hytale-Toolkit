package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class BcRSAAsymmetricKeyUnwrapper extends BcAsymmetricKeyUnwrapper {
   public BcRSAAsymmetricKeyUnwrapper(AlgorithmIdentifier var1, AsymmetricKeyParameter var2) {
      super(var1, var2);
   }

   @Override
   protected AsymmetricBlockCipher createAsymmetricUnwrapper(ASN1ObjectIdentifier var1) {
      return new PKCS1Encoding(new RSABlindedEngine());
   }
}
