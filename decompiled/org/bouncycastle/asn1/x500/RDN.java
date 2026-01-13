package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSet;

public class RDN extends ASN1Object {
   private ASN1Set values;

   private RDN(ASN1Set var1) {
      this.values = var1;
   }

   public static RDN getInstance(Object var0) {
      if (var0 instanceof RDN) {
         return (RDN)var0;
      } else {
         return var0 != null ? new RDN(ASN1Set.getInstance(var0)) : null;
      }
   }

   public static RDN getInstance(ASN1TaggedObject var0, boolean var1) {
      return new RDN(ASN1Set.getInstance(var0, var1));
   }

   public RDN(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this(new AttributeTypeAndValue(var1, var2));
   }

   public RDN(AttributeTypeAndValue var1) {
      this.values = new DERSet(var1);
   }

   public RDN(AttributeTypeAndValue[] var1) {
      this.values = new DERSet(var1);
   }

   public boolean isMultiValued() {
      return this.values.size() > 1;
   }

   public int size() {
      return this.values.size();
   }

   public AttributeTypeAndValue getFirst() {
      return this.values.size() == 0 ? null : AttributeTypeAndValue.getInstance(this.values.getObjectAt(0));
   }

   public AttributeTypeAndValue[] getTypesAndValues() {
      AttributeTypeAndValue[] var1 = new AttributeTypeAndValue[this.values.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(var2));
      }

      return var1;
   }

   int collectAttributeTypes(ASN1ObjectIdentifier[] var1, int var2) {
      int var3 = this.values.size();

      for (int var4 = 0; var4 < var3; var4++) {
         AttributeTypeAndValue var5 = AttributeTypeAndValue.getInstance(this.values.getObjectAt(var4));
         var1[var2 + var4] = var5.getType();
      }

      return var3;
   }

   boolean containsAttributeType(ASN1ObjectIdentifier var1) {
      int var2 = this.values.size();

      for (int var3 = 0; var3 < var2; var3++) {
         AttributeTypeAndValue var4 = AttributeTypeAndValue.getInstance(this.values.getObjectAt(var3));
         if (var4.getType().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.values;
   }
}
