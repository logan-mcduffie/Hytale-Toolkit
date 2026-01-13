package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class AaEntry extends ASN1Object {
   private final EtsiTs103097Certificate aaCertificate;
   private final Url accessPoint;

   public AaEntry(EtsiTs103097Certificate var1, Url var2) {
      this.aaCertificate = var1;
      this.accessPoint = var2;
   }

   private AaEntry(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.aaCertificate = EtsiTs103097Certificate.getInstance(var1.getObjectAt(0));
         this.accessPoint = Url.getInstance(var1.getObjectAt(1));
      }
   }

   public static AaEntry getInstance(Object var0) {
      if (var0 instanceof AaEntry) {
         return (AaEntry)var0;
      } else {
         return var0 != null ? new AaEntry(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EtsiTs103097Certificate getAaCertificate() {
      return this.aaCertificate;
   }

   public Url getAccessPoint() {
      return this.accessPoint;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.aaCertificate, this.accessPoint});
   }

   public static AaEntry.Builder builder() {
      return new AaEntry.Builder();
   }

   public static class Builder {
      private EtsiTs103097Certificate aaCertificate;
      private Url accessPoint;

      public AaEntry.Builder setAaCertificate(EtsiTs103097Certificate var1) {
         this.aaCertificate = var1;
         return this;
      }

      public AaEntry.Builder setAccessPoint(Url var1) {
         this.accessPoint = var1;
         return this;
      }

      public AaEntry createAaEntry() {
         return new AaEntry(this.aaCertificate, this.accessPoint);
      }
   }
}
