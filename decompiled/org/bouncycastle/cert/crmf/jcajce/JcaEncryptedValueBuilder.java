package org.bouncycastle.cert.crmf.jcajce;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OutputEncryptor;

public class JcaEncryptedValueBuilder extends EncryptedValueBuilder {
   public JcaEncryptedValueBuilder(KeyWrapper var1, OutputEncryptor var2) {
      super(var1, var2);
   }

   public EncryptedValue build(X509Certificate var1) throws CertificateEncodingException, CRMFException {
      return this.build(new JcaX509CertificateHolder(var1));
   }

   public EncryptedValue build(PrivateKey var1) throws CertificateEncodingException, CRMFException {
      return this.build(PrivateKeyInfo.getInstance(var1.getEncoded()));
   }
}
