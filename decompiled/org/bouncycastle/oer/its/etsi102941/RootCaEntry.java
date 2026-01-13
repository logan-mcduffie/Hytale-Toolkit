package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class RootCaEntry extends ASN1Object {
   private final EtsiTs103097Certificate selfsignedRootCa;
   private final EtsiTs103097Certificate successorTo;

   public RootCaEntry(EtsiTs103097Certificate var1, EtsiTs103097Certificate var2) {
      this.selfsignedRootCa = var1;
      this.successorTo = var2;
   }

   private RootCaEntry(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.selfsignedRootCa = EtsiTs103097Certificate.getInstance(var1.getObjectAt(0));
         this.successorTo = OEROptional.getValue(EtsiTs103097Certificate.class, var1.getObjectAt(1));
      }
   }

   public static RootCaEntry getInstance(Object var0) {
      if (var0 instanceof RootCaEntry) {
         return (RootCaEntry)var0;
      } else {
         return var0 != null ? new RootCaEntry(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EtsiTs103097Certificate getSelfsignedRootCa() {
      return this.selfsignedRootCa;
   }

   public EtsiTs103097Certificate getSuccessorTo() {
      return this.successorTo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.selfsignedRootCa, OEROptional.getInstance(this.successorTo)});
   }

   public static RootCaEntry.Builder builder() {
      return new RootCaEntry.Builder();
   }

   public static class Builder {
      private EtsiTs103097Certificate selfsignedRootCa;
      private EtsiTs103097Certificate successorTo;

      public RootCaEntry.Builder setSelfsignedRootCa(EtsiTs103097Certificate var1) {
         this.selfsignedRootCa = var1;
         return this;
      }

      public RootCaEntry.Builder setSuccessorTo(EtsiTs103097Certificate var1) {
         this.successorTo = var1;
         return this;
      }

      public RootCaEntry createRootCaEntry() {
         return new RootCaEntry(this.selfsignedRootCa, this.successorTo);
      }
   }
}
