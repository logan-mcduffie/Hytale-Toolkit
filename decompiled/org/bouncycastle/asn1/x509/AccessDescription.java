package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AccessDescription extends ASN1Object {
   public static final ASN1ObjectIdentifier id_ad_caIssuers = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.2");
   public static final ASN1ObjectIdentifier id_ad_ocsp = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.1");
   ASN1ObjectIdentifier accessMethod = null;
   GeneralName accessLocation = null;

   public static AccessDescription getInstance(Object var0) {
      if (var0 instanceof AccessDescription) {
         return (AccessDescription)var0;
      } else {
         return var0 != null ? new AccessDescription(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private AccessDescription(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("wrong number of elements in sequence");
      } else {
         this.accessMethod = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.accessLocation = GeneralName.getInstance(var1.getObjectAt(1));
      }
   }

   public AccessDescription(ASN1ObjectIdentifier var1, GeneralName var2) {
      this.accessMethod = var1;
      this.accessLocation = var2;
   }

   public ASN1ObjectIdentifier getAccessMethod() {
      return this.accessMethod;
   }

   public GeneralName getAccessLocation() {
      return this.accessLocation;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.accessMethod, this.accessLocation);
   }

   @Override
   public String toString() {
      return "AccessDescription: Oid(" + this.accessMethod.getId() + ")";
   }
}
