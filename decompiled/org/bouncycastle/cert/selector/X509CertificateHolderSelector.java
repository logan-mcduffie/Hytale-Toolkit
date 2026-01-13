package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class X509CertificateHolderSelector implements Selector {
   private byte[] subjectKeyId;
   private X500Name issuer;
   private BigInteger serialNumber;

   public X509CertificateHolderSelector(byte[] var1) {
      this(null, null, var1);
   }

   public X509CertificateHolderSelector(X500Name var1, BigInteger var2) {
      this(var1, var2, null);
   }

   public X509CertificateHolderSelector(X500Name var1, BigInteger var2, byte[] var3) {
      this.issuer = var1;
      this.serialNumber = var2;
      this.subjectKeyId = Arrays.clone(var3);
   }

   public X500Name getIssuer() {
      return this.issuer;
   }

   public BigInteger getSerialNumber() {
      return this.serialNumber;
   }

   public byte[] getSubjectKeyIdentifier() {
      return Arrays.clone(this.subjectKeyId);
   }

   @Override
   public int hashCode() {
      int var1 = Arrays.hashCode(this.subjectKeyId);
      if (this.serialNumber != null) {
         var1 ^= this.serialNumber.hashCode();
      }

      if (this.issuer != null) {
         var1 ^= this.issuer.hashCode();
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof X509CertificateHolderSelector)) {
         return false;
      } else {
         X509CertificateHolderSelector var2 = (X509CertificateHolderSelector)var1;
         return Arrays.areEqual(this.subjectKeyId, var2.subjectKeyId)
            && this.equalsObj(this.serialNumber, var2.serialNumber)
            && this.equalsObj(this.issuer, var2.issuer);
      }
   }

   private boolean equalsObj(Object var1, Object var2) {
      return var1 != null ? var1.equals(var2) : var2 == null;
   }

   @Override
   public boolean match(Object var1) {
      if (var1 instanceof X509CertificateHolder) {
         X509CertificateHolder var2 = (X509CertificateHolder)var1;
         if (this.getSerialNumber() != null) {
            IssuerAndSerialNumber var5 = new IssuerAndSerialNumber(var2.toASN1Structure());
            return var5.getName().equals(this.issuer) && var5.getSerialNumber().hasValue(this.serialNumber);
         }

         if (this.subjectKeyId != null) {
            Extension var3 = var2.getExtension(Extension.subjectKeyIdentifier);
            if (var3 == null) {
               return Arrays.areEqual(this.subjectKeyId, MSOutlookKeyIdCalculator.calculateKeyId(var2.getSubjectPublicKeyInfo()));
            }

            byte[] var4 = ASN1OctetString.getInstance(var3.getParsedValue()).getOctets();
            return Arrays.areEqual(this.subjectKeyId, var4);
         }
      } else if (var1 instanceof byte[]) {
         return Arrays.areEqual(this.subjectKeyId, (byte[])var1);
      }

      return false;
   }

   @Override
   public Object clone() {
      return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
   }
}
