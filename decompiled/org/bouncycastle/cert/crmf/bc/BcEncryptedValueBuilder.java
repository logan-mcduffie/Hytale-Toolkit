package org.bouncycastle.cert.crmf.bc;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OutputEncryptor;

public class BcEncryptedValueBuilder extends EncryptedValueBuilder {
   public BcEncryptedValueBuilder(KeyWrapper var1, OutputEncryptor var2) {
      super(var1, var2);
   }

   public EncryptedValue build(X509Certificate var1) throws CertificateEncodingException, CRMFException {
      return this.build(new JcaX509CertificateHolder(var1));
   }

   public EncryptedValue build(AsymmetricKeyParameter var1) throws CRMFException, IOException {
      return this.build(PrivateKeyInfoFactory.createPrivateKeyInfo(var1));
   }
}
