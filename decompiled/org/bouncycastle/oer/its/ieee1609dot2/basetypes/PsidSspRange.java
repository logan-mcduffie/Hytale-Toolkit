package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;

public class PsidSspRange extends ASN1Object {
   private final Psid psid;
   private final SspRange sspRange;

   public PsidSspRange(Psid var1, SspRange var2) {
      this.psid = var1;
      this.sspRange = var2;
   }

   private PsidSspRange(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.psid = Psid.getInstance(var1.getObjectAt(0));
         this.sspRange = OEROptional.getValue(SspRange.class, var1.getObjectAt(1));
      }
   }

   public static PsidSspRange getInstance(Object var0) {
      if (var0 instanceof PsidSspRange) {
         return (PsidSspRange)var0;
      } else {
         return var0 != null ? new PsidSspRange(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Psid getPsid() {
      return this.psid;
   }

   public SspRange getSspRange() {
      return this.sspRange;
   }

   public static PsidSspRange.Builder builder() {
      return new PsidSspRange.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance(this.sspRange)});
   }

   public static class Builder {
      private Psid psid;
      private SspRange sspRange;

      public PsidSspRange.Builder setPsid(Psid var1) {
         this.psid = var1;
         return this;
      }

      public PsidSspRange.Builder setPsid(long var1) {
         this.psid = new Psid(var1);
         return this;
      }

      public PsidSspRange.Builder setSspRange(SspRange var1) {
         this.sspRange = var1;
         return this;
      }

      public PsidSspRange createPsidSspRange() {
         return new PsidSspRange(this.psid, this.sspRange);
      }
   }
}
