package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class EtsiTs102941CtlRequest extends ASN1Object {
   private final HashedId8 issuerId;
   private final ASN1Integer lastKnownCtlSequence;

   protected EtsiTs102941CtlRequest(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.issuerId = HashedId8.getInstance(var1.getObjectAt(0));
         if (var1.size() == 2) {
            this.lastKnownCtlSequence = OEROptional.getValue(ASN1Integer.class, var1.getObjectAt(1));
         } else {
            this.lastKnownCtlSequence = null;
         }
      }
   }

   public EtsiTs102941CtlRequest(HashedId8 var1, ASN1Integer var2) {
      this.issuerId = var1;
      this.lastKnownCtlSequence = var2;
   }

   public static EtsiTs102941CtlRequest getInstance(Object var0) {
      if (var0 instanceof EtsiTs102941CtlRequest) {
         return (EtsiTs102941CtlRequest)var0;
      } else {
         return var0 != null ? new EtsiTs102941CtlRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public HashedId8 getIssuerId() {
      return this.issuerId;
   }

   public ASN1Integer getLastKnownCtlSequence() {
      return this.lastKnownCtlSequence;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.issuerId, OEROptional.getInstance(this.lastKnownCtlSequence)});
   }

   public static EtsiTs102941CtlRequest.Builder builder() {
      return new EtsiTs102941CtlRequest.Builder();
   }

   public static class Builder {
      private HashedId8 issuerId;
      private ASN1Integer lastKnownCtlSequence;

      public EtsiTs102941CtlRequest.Builder setIssuerId(HashedId8 var1) {
         this.issuerId = var1;
         return this;
      }

      public EtsiTs102941CtlRequest.Builder setLastKnownCtlSequence(ASN1Integer var1) {
         this.lastKnownCtlSequence = var1;
         return this;
      }

      public EtsiTs102941CtlRequest createEtsiTs102941CtlRequest() {
         return new EtsiTs102941CtlRequest(this.issuerId, this.lastKnownCtlSequence);
      }

      public EtsiTs102941DeltaCtlRequest createEtsiTs102941DeltaCtlRequest() {
         return new EtsiTs102941DeltaCtlRequest(this.issuerId, this.lastKnownCtlSequence);
      }
   }
}
