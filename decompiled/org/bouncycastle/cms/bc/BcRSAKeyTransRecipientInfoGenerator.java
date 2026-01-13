package org.bouncycastle.cms.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.bc.BcRSAAsymmetricKeyWrapper;

public class BcRSAKeyTransRecipientInfoGenerator extends BcKeyTransRecipientInfoGenerator {
   public BcRSAKeyTransRecipientInfoGenerator(byte[] var1, AlgorithmIdentifier var2, AsymmetricKeyParameter var3) {
      super(var1, new BcRSAAsymmetricKeyWrapper(var2, var3));
   }

   public BcRSAKeyTransRecipientInfoGenerator(X509CertificateHolder var1) throws IOException {
      super(var1, new BcRSAAsymmetricKeyWrapper(var1.getSubjectPublicKeyInfo().getAlgorithm(), var1.getSubjectPublicKeyInfo()));
   }
}
