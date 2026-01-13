package org.bouncycastle.cert.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.X509v3CertificateBuilder;

public class JcaX509v3CertificateBuilder extends X509v3CertificateBuilder {
   public JcaX509v3CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, X500Name var5, SubjectPublicKeyInfo var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public JcaX509v3CertificateBuilder(X500Name var1, BigInteger var2, Date var3, Date var4, X500Name var5, PublicKey var6) {
      super(var1, var2, var3, var4, var5, getSubjectPublicKeyInfo(var6));
   }

   public JcaX509v3CertificateBuilder(X500Name var1, BigInteger var2, Time var3, Time var4, X500Name var5, PublicKey var6) {
      super(var1, var2, var3, var4, var5, getSubjectPublicKeyInfo(var6));
   }

   public JcaX509v3CertificateBuilder(X500Principal var1, BigInteger var2, Date var3, Date var4, X500Principal var5, PublicKey var6) {
      super(X500Name.getInstance(var1.getEncoded()), var2, var3, var4, X500Name.getInstance(var5.getEncoded()), getSubjectPublicKeyInfo(var6));
   }

   public JcaX509v3CertificateBuilder(X509Certificate var1, BigInteger var2, Date var3, Date var4, X500Principal var5, PublicKey var6) {
      this(var1.getSubjectX500Principal(), var2, var3, var4, var5, var6);
   }

   public JcaX509v3CertificateBuilder(X509Certificate var1, BigInteger var2, Date var3, Date var4, X500Name var5, PublicKey var6) {
      this(X500Name.getInstance(var1.getSubjectX500Principal().getEncoded()), var2, var3, var4, var5, var6);
   }

   public JcaX509v3CertificateBuilder(X509Certificate var1) throws CertificateEncodingException {
      super(new JcaX509CertificateHolder(var1));
   }

   public JcaX509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier var1, boolean var2, X509Certificate var3) throws CertificateEncodingException {
      this.copyAndAddExtension(var1, var2, new JcaX509CertificateHolder(var3));
      return this;
   }

   private static SubjectPublicKeyInfo getSubjectPublicKeyInfo(PublicKey var0) {
      return SubjectPublicKeyInfo.getInstance(var0.getEncoded());
   }
}
