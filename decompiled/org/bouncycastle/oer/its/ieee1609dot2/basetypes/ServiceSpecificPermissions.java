package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;

public class ServiceSpecificPermissions extends ASN1Object implements ASN1Choice {
   public static final int opaque = 0;
   public static final int bitmapSsp = 1;
   private final int choice;
   private final ASN1Encodable serviceSpecificPermissions;

   public ServiceSpecificPermissions(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.serviceSpecificPermissions = var2;
   }

   private ServiceSpecificPermissions(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.serviceSpecificPermissions = Opaque.getInstance(var1.getExplicitBaseObject());
            return;
         case 1:
            this.serviceSpecificPermissions = BitmapSsp.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static ServiceSpecificPermissions getInstance(Object var0) {
      if (var0 instanceof ServiceSpecificPermissions) {
         return (ServiceSpecificPermissions)var0;
      } else {
         return var0 != null ? new ServiceSpecificPermissions(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static ServiceSpecificPermissions opaque(ASN1OctetString var0) {
      return new ServiceSpecificPermissions(0, var0);
   }

   public static ServiceSpecificPermissions opaque(byte[] var0) {
      return new ServiceSpecificPermissions(0, new DEROctetString(var0));
   }

   public static ServiceSpecificPermissions bitmapSsp(BitmapSsp var0) {
      return new ServiceSpecificPermissions(1, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getServiceSpecificPermissions() {
      return this.serviceSpecificPermissions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.serviceSpecificPermissions);
   }
}
