package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class Extension extends ASN1Object {
   public static final ExtId etsiTs102941CrlRequestId = new ExtId(1L);
   public static final ExtId etsiTs102941DeltaCtlRequestId = new ExtId(2L);
   private final ExtId id;
   private final ASN1Encodable content;

   protected Extension(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.id = ExtId.getInstance(var1.getObjectAt(0));
         if (this.id.equals(etsiTs102941CrlRequestId)) {
            this.content = EtsiTs102941CrlRequest.getInstance(var1.getObjectAt(1));
         } else {
            if (!this.id.equals(etsiTs102941DeltaCtlRequestId)) {
               throw new IllegalArgumentException("id not 1 (EtsiTs102941CrlRequest) or 2 (EtsiTs102941DeltaCtlRequest)");
            }

            this.content = EtsiTs102941DeltaCtlRequest.getInstance(var1.getObjectAt(1));
         }
      }
   }

   public Extension(ExtId var1, ASN1Encodable var2) {
      this.id = var1;
      if (var1.getExtId().intValue() != 1 && var1.getExtId().intValue() != 2) {
         throw new IllegalArgumentException("id not 1 (EtsiTs102941CrlRequest) or 2 (EtsiTs102941DeltaCtlRequest)");
      } else {
         this.content = var2;
      }
   }

   public static Extension etsiTs102941CrlRequest(EtsiTs102941CrlRequest var0) {
      return new Extension(etsiTs102941CrlRequestId, var0);
   }

   public static Extension etsiTs102941DeltaCtlRequest(EtsiTs102941DeltaCtlRequest var0) {
      return new Extension(etsiTs102941DeltaCtlRequestId, var0);
   }

   public static Extension getInstance(Object var0) {
      if (var0 instanceof Extension) {
         return (Extension)var0;
      } else {
         return var0 != null ? new Extension(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.id, this.content});
   }

   public ExtId getId() {
      return this.id;
   }

   public ASN1Encodable getContent() {
      return this.content;
   }
}
