package org.bouncycastle.openssl.jcajce;

import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemGenerationException;

public class JcaPKCS8Generator extends PKCS8Generator {
   public JcaPKCS8Generator(PrivateKey var1, OutputEncryptor var2) throws PemGenerationException {
      super(PrivateKeyInfo.getInstance(var1.getEncoded()), var2);
   }
}
