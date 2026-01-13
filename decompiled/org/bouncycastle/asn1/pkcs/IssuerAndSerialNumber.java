package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509Name;

public class IssuerAndSerialNumber extends ASN1Object {
   X500Name name;
   ASN1Integer certSerialNumber;

   public static IssuerAndSerialNumber getInstance(Object var0) {
      if (var0 instanceof IssuerAndSerialNumber) {
         return (IssuerAndSerialNumber)var0;
      } else {
         return var0 != null ? new IssuerAndSerialNumber(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private IssuerAndSerialNumber(ASN1Sequence var1) {
      this.name = X500Name.getInstance(var1.getObjectAt(0));
      this.certSerialNumber = (ASN1Integer)var1.getObjectAt(1);
   }

   public IssuerAndSerialNumber(X509Name var1, BigInteger var2) {
      this.name = X500Name.getInstance(var1.toASN1Primitive());
      this.certSerialNumber = new ASN1Integer(var2);
   }

   public IssuerAndSerialNumber(X509Name var1, ASN1Integer var2) {
      this.name = X500Name.getInstance(var1.toASN1Primitive());
      this.certSerialNumber = var2;
   }

   public IssuerAndSerialNumber(X500Name var1, BigInteger var2) {
      this.name = var1;
      this.certSerialNumber = new ASN1Integer(var2);
   }

   public X500Name getName() {
      return this.name;
   }

   public ASN1Integer getCertificateSerialNumber() {
      return this.certSerialNumber;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.name, this.certSerialNumber);
   }
}
