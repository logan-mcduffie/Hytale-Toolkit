package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class EtsiTs102941CrlRequest extends ASN1Object {
   private final HashedId8 issuerId;
   private final Time32 lastKnownUpdate;

   public EtsiTs102941CrlRequest(HashedId8 var1, Time32 var2) {
      this.issuerId = var1;
      this.lastKnownUpdate = var2;
   }

   private EtsiTs102941CrlRequest(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.issuerId = HashedId8.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.lastKnownUpdate = OEROptional.getValue(Time32.class, var1.getObjectAt(1));
         } else {
            this.lastKnownUpdate = null;
         }
      }
   }

   public static EtsiTs102941CrlRequest getInstance(Object var0) {
      if (var0 instanceof EtsiTs102941CrlRequest) {
         return (EtsiTs102941CrlRequest)var0;
      } else {
         return var0 != null ? new EtsiTs102941CrlRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static EtsiTs102941CrlRequest.Builder builder() {
      return new EtsiTs102941CrlRequest.Builder();
   }

   public HashedId8 getIssuerId() {
      return this.issuerId;
   }

   public Time32 getLastKnownUpdate() {
      return this.lastKnownUpdate;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.issuerId, OEROptional.getInstance(this.lastKnownUpdate)});
   }

   public static class Builder {
      private HashedId8 issuerId;
      private Time32 lastKnownUpdate;

      public EtsiTs102941CrlRequest.Builder setIssuerId(HashedId8 var1) {
         this.issuerId = var1;
         return this;
      }

      public EtsiTs102941CrlRequest.Builder setLastKnownUpdate(Time32 var1) {
         this.lastKnownUpdate = var1;
         return this;
      }

      public EtsiTs102941CrlRequest createEtsiTs102941CrlRequest() {
         return new EtsiTs102941CrlRequest(this.issuerId, this.lastKnownUpdate);
      }
   }
}
