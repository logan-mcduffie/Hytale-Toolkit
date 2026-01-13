package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;

public class ServiceLocator extends ASN1Object {
   private final X500Name issuer;
   private final AuthorityInformationAccess locator;

   private ServiceLocator(ASN1Sequence var1) {
      this.issuer = X500Name.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.locator = AuthorityInformationAccess.getInstance(var1.getObjectAt(1));
      } else {
         this.locator = null;
      }
   }

   public static ServiceLocator getInstance(Object var0) {
      if (var0 instanceof ServiceLocator) {
         return (ServiceLocator)var0;
      } else {
         return var0 != null ? new ServiceLocator(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public X500Name getIssuer() {
      return this.issuer;
   }

   public AuthorityInformationAccess getLocator() {
      return this.locator;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.issuer);
      if (this.locator != null) {
         var1.add(this.locator);
      }

      return new DERSequence(var1);
   }
}
