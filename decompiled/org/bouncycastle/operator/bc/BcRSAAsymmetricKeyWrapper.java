package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;

public class BcRSAAsymmetricKeyWrapper extends BcAsymmetricKeyWrapper {
   public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier var1, AsymmetricKeyParameter var2) {
      super(var1, var2);
   }

   public BcRSAAsymmetricKeyWrapper(AlgorithmIdentifier var1, SubjectPublicKeyInfo var2) throws IOException {
      super(var1, PublicKeyFactory.createKey(var2));
   }

   @Override
   protected AsymmetricBlockCipher createAsymmetricWrapper(ASN1ObjectIdentifier var1) {
      return new PKCS1Encoding(new RSABlindedEngine());
   }
}
