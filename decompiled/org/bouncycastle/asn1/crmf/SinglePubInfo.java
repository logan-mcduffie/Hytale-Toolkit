package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class SinglePubInfo extends ASN1Object {
   public static final ASN1Integer dontCare = new ASN1Integer(0L);
   public static final ASN1Integer x500 = new ASN1Integer(1L);
   public static final ASN1Integer web = new ASN1Integer(2L);
   public static final ASN1Integer ldap = new ASN1Integer(3L);
   private ASN1Integer pubMethod;
   private GeneralName pubLocation;

   private SinglePubInfo(ASN1Sequence var1) {
      this.pubMethod = ASN1Integer.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.pubLocation = GeneralName.getInstance(var1.getObjectAt(1));
      }
   }

   public static SinglePubInfo getInstance(Object var0) {
      if (var0 instanceof SinglePubInfo) {
         return (SinglePubInfo)var0;
      } else {
         return var0 != null ? new SinglePubInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public SinglePubInfo(ASN1Integer var1, GeneralName var2) {
      this.pubMethod = var1;
      this.pubLocation = var2;
   }

   public ASN1Integer getPubMethod() {
      return this.pubMethod;
   }

   public GeneralName getPubLocation() {
      return this.pubLocation;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.pubMethod);
      if (this.pubLocation != null) {
         var1.add(this.pubLocation);
      }

      return new DERSequence(var1);
   }
}
