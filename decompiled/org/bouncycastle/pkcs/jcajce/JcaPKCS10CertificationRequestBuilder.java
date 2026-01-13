package org.bouncycastle.pkcs.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

public class JcaPKCS10CertificationRequestBuilder extends PKCS10CertificationRequestBuilder {
   public JcaPKCS10CertificationRequestBuilder(X500Name var1, PublicKey var2) {
      super(var1, SubjectPublicKeyInfo.getInstance(var2.getEncoded()));
   }

   public JcaPKCS10CertificationRequestBuilder(X500Principal var1, PublicKey var2) {
      super(X500Name.getInstance(var1.getEncoded()), SubjectPublicKeyInfo.getInstance(var2.getEncoded()));
   }

   public PKCS10CertificationRequest build(ContentSigner var1, PublicKey var2, ContentSigner var3) {
      return super.build(var1, SubjectPublicKeyInfo.getInstance(var2.getEncoded()), var3);
   }
}
