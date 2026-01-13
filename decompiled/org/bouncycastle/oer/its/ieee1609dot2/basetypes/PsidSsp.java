package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;

public class PsidSsp extends ASN1Object {
   private final Psid psid;
   private final ServiceSpecificPermissions ssp;

   public PsidSsp(Psid var1, ServiceSpecificPermissions var2) {
      this.psid = var1;
      this.ssp = var2;
   }

   private PsidSsp(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.psid = Psid.getInstance(var1.getObjectAt(0));
         this.ssp = OEROptional.getValue(ServiceSpecificPermissions.class, var1.getObjectAt(1));
      }
   }

   public static PsidSsp getInstance(Object var0) {
      if (var0 instanceof PsidSsp) {
         return (PsidSsp)var0;
      } else {
         return var0 != null ? new PsidSsp(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static PsidSsp.Builder builder() {
      return new PsidSsp.Builder();
   }

   public Psid getPsid() {
      return this.psid;
   }

   public ServiceSpecificPermissions getSsp() {
      return this.ssp;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.psid, OEROptional.getInstance(this.ssp));
   }

   public static class Builder {
      private Psid psid;
      private ServiceSpecificPermissions ssp;

      public PsidSsp.Builder setPsid(Psid var1) {
         this.psid = var1;
         return this;
      }

      public PsidSsp.Builder setSsp(ServiceSpecificPermissions var1) {
         this.ssp = var1;
         return this;
      }

      public PsidSsp createPsidSsp() {
         return new PsidSsp(this.psid, this.ssp);
      }
   }
}
