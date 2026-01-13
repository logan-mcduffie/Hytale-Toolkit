package org.bouncycastle.cert.bc;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

public class BcX509v3CertificateBuilder extends X509v3CertificateBuilder {
   public BcX509v3CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, X500Name var5, AsymmetricKeyParameter var6) throws IOException {
      super(var1, var2, var3, var4, var5, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(var6));
   }

   public BcX509v3CertificateBuilder(X509CertificateHolder var1, BigInteger var2, Date var3, Date var4, X500Name var5, AsymmetricKeyParameter var6) throws IOException {
      super(var1.getSubject(), var2, var3, var4, var5, SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(var6));
   }
}
