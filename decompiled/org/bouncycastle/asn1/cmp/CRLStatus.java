package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Time;

public class CRLStatus extends ASN1Object {
   private final CRLSource source;
   private final Time thisUpdate;

   private CRLStatus(ASN1Sequence var1) {
      if (var1.size() != 1 && var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 1 or 2, got " + var1.size());
      } else {
         this.source = CRLSource.getInstance(var1.getObjectAt(0));
         if (var1.size() == 2) {
            this.thisUpdate = Time.getInstance(var1.getObjectAt(1));
         } else {
            this.thisUpdate = null;
         }
      }
   }

   public CRLStatus(CRLSource var1, Time var2) {
      this.source = var1;
      this.thisUpdate = var2;
   }

   public static CRLStatus getInstance(Object var0) {
      if (var0 instanceof CRLStatus) {
         return (CRLStatus)var0;
      } else {
         return var0 != null ? new CRLStatus(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CRLSource getSource() {
      return this.source;
   }

   public Time getThisUpdate() {
      return this.thisUpdate;
   }

   /** @deprecated */
   public Time getTime() {
      return this.thisUpdate;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.source);
      if (this.thisUpdate != null) {
         var1.add(this.thisUpdate);
      }

      return new DERSequence(var1);
   }
}
