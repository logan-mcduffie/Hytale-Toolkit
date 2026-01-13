package org.bouncycastle.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.Attribute;

/** @deprecated */
public class X509Attribute extends ASN1Object {
   Attribute attr;

   X509Attribute(ASN1Encodable var1) {
      this.attr = Attribute.getInstance(var1);
   }

   public X509Attribute(String var1, ASN1Encodable var2) {
      this.attr = new Attribute(new ASN1ObjectIdentifier(var1), new DERSet(var2));
   }

   public X509Attribute(String var1, ASN1EncodableVector var2) {
      this.attr = new Attribute(new ASN1ObjectIdentifier(var1), new DERSet(var2));
   }

   public String getOID() {
      return this.attr.getAttrType().getId();
   }

   public ASN1Encodable[] getValues() {
      ASN1Set var1 = this.attr.getAttrValues();
      ASN1Encodable[] var2 = new ASN1Encodable[var1.size()];

      for (int var3 = 0; var3 != var1.size(); var3++) {
         var2[var3] = var1.getObjectAt(var3);
      }

      return var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.attr.toASN1Primitive();
   }
}
