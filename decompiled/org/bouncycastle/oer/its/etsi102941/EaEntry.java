package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class EaEntry extends ASN1Object {
   private final EtsiTs103097Certificate eaCertificate;
   private final Url aaAccessPoint;
   private final Url itsAccessPoint;

   public EaEntry(EtsiTs103097Certificate var1, Url var2, Url var3) {
      this.eaCertificate = var1;
      this.aaAccessPoint = var2;
      this.itsAccessPoint = var3;
   }

   private EaEntry(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.eaCertificate = EtsiTs103097Certificate.getInstance(var1.getObjectAt(0));
         this.aaAccessPoint = Url.getInstance(var1.getObjectAt(1));
         this.itsAccessPoint = OEROptional.getValue(Url.class, var1.getObjectAt(2));
      }
   }

   public static EaEntry getInstance(Object var0) {
      if (var0 instanceof EaEntry) {
         return (EaEntry)var0;
      } else {
         return var0 != null ? new EaEntry(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EtsiTs103097Certificate getEaCertificate() {
      return this.eaCertificate;
   }

   public Url getAaAccessPoint() {
      return this.aaAccessPoint;
   }

   public Url getItsAccessPoint() {
      return this.itsAccessPoint;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.eaCertificate, this.aaAccessPoint, OEROptional.getInstance(this.itsAccessPoint)});
   }

   public static EaEntry.Builder builder() {
      return new EaEntry.Builder();
   }

   public static class Builder {
      private EtsiTs103097Certificate eaCertificate;
      private Url aaAccessPoint;
      private Url itsAccessPoint;

      public EaEntry.Builder setEaCertificate(EtsiTs103097Certificate var1) {
         this.eaCertificate = var1;
         return this;
      }

      public EaEntry.Builder setAaAccessPoint(Url var1) {
         this.aaAccessPoint = var1;
         return this;
      }

      public EaEntry.Builder setItsAccessPoint(Url var1) {
         this.itsAccessPoint = var1;
         return this;
      }

      public EaEntry createEaEntry() {
         return new EaEntry(this.eaCertificate, this.aaAccessPoint, this.itsAccessPoint);
      }
   }
}
