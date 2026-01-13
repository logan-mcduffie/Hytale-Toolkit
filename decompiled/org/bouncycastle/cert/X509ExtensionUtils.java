package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DigestCalculator;

public class X509ExtensionUtils {
   private DigestCalculator calculator;

   public X509ExtensionUtils(DigestCalculator var1) {
      this.calculator = var1;
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509CertificateHolder var1) {
      GeneralName var2 = new GeneralName(var1.getIssuer());
      return new AuthorityKeyIdentifier(this.getSubjectKeyIdentifier(var1), new GeneralNames(var2), var1.getSerialNumber());
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo var1) {
      return new AuthorityKeyIdentifier(this.calculateIdentifier(var1));
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo var1, GeneralNames var2, BigInteger var3) {
      return new AuthorityKeyIdentifier(this.calculateIdentifier(var1), var2, var3);
   }

   public SubjectKeyIdentifier createSubjectKeyIdentifier(SubjectPublicKeyInfo var1) {
      return new SubjectKeyIdentifier(this.calculateIdentifier(var1));
   }

   public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo var1) {
      byte[] var2 = this.calculateIdentifier(var1);
      byte[] var3 = new byte[8];
      System.arraycopy(var2, var2.length - 8, var3, 0, var3.length);
      var3[0] = (byte)(var3[0] & 15);
      var3[0] = (byte)(var3[0] | 64);
      return new SubjectKeyIdentifier(var3);
   }

   private byte[] getSubjectKeyIdentifier(X509CertificateHolder var1) {
      if (var1.getVersionNumber() == 3) {
         Extension var2 = var1.getExtension(Extension.subjectKeyIdentifier);
         if (var2 != null) {
            return ASN1OctetString.getInstance(var2.getParsedValue()).getOctets();
         }
      }

      return this.calculateIdentifier(var1.getSubjectPublicKeyInfo());
   }

   private byte[] calculateIdentifier(SubjectPublicKeyInfo var1) {
      byte[] var2 = var1.getPublicKeyData().getBytes();
      OutputStream var3 = this.calculator.getOutputStream();

      try {
         var3.write(var2);
         var3.close();
      } catch (IOException var5) {
         throw new CertRuntimeException("unable to calculate identifier: " + var5.getMessage(), var5);
      }

      return this.calculator.getDigest();
   }
}
