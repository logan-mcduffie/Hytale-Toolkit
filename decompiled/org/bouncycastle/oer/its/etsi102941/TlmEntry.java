package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

public class TlmEntry extends ASN1Object {
   private final EtsiTs103097Certificate selfSignedTLMCertificate;
   private final EtsiTs103097Certificate successorTo;
   private final Url accessPoint;

   public TlmEntry(EtsiTs103097Certificate var1, EtsiTs103097Certificate var2, Url var3) {
      this.selfSignedTLMCertificate = var1;
      this.successorTo = var2;
      this.accessPoint = var3;
   }

   private TlmEntry(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.selfSignedTLMCertificate = EtsiTs103097Certificate.getInstance(var1.getObjectAt(0));
         this.successorTo = OEROptional.getValue(EtsiTs103097Certificate.class, var1.getObjectAt(1));
         this.accessPoint = Url.getInstance(var1.getObjectAt(2));
      }
   }

   public static TlmEntry getInstance(Object var0) {
      if (var0 instanceof TlmEntry) {
         return (TlmEntry)var0;
      } else {
         return var0 != null ? new TlmEntry(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EtsiTs103097Certificate getSelfSignedTLMCertificate() {
      return this.selfSignedTLMCertificate;
   }

   public EtsiTs103097Certificate getSuccessorTo() {
      return this.successorTo;
   }

   public Url getAccessPoint() {
      return this.accessPoint;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.selfSignedTLMCertificate, OEROptional.getInstance(this.successorTo), this.accessPoint});
   }

   public static TlmEntry.Builder builder() {
      return new TlmEntry.Builder();
   }

   public static class Builder {
      private EtsiTs103097Certificate selfSignedTLMCertificate;
      private EtsiTs103097Certificate successorTo;
      private Url accessPoint;

      public TlmEntry.Builder setSelfSignedTLMCertificate(EtsiTs103097Certificate var1) {
         this.selfSignedTLMCertificate = var1;
         return this;
      }

      public TlmEntry.Builder setSuccessorTo(EtsiTs103097Certificate var1) {
         this.successorTo = var1;
         return this;
      }

      public TlmEntry.Builder setAccessPoint(Url var1) {
         this.accessPoint = var1;
         return this;
      }

      public TlmEntry createTlmEntry() {
         return new TlmEntry(this.selfSignedTLMCertificate, this.successorTo, this.accessPoint);
      }
   }
}
