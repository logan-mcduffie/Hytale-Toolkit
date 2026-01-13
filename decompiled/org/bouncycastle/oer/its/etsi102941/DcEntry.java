package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfHashedId8;

public class DcEntry extends ASN1Object {
   private final Url url;
   private final SequenceOfHashedId8 cert;

   public DcEntry(Url var1, SequenceOfHashedId8 var2) {
      this.url = var1;
      this.cert = var2;
   }

   private DcEntry(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.url = Url.getInstance(var1.getObjectAt(0));
         this.cert = SequenceOfHashedId8.getInstance(var1.getObjectAt(1));
      }
   }

   public static DcEntry getInstance(Object var0) {
      if (var0 instanceof DcEntry) {
         return (DcEntry)var0;
      } else {
         return var0 != null ? new DcEntry(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Url getUrl() {
      return this.url;
   }

   public SequenceOfHashedId8 getCert() {
      return this.cert;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.url, this.cert});
   }

   public static DcEntry.Builder builder() {
      return new DcEntry.Builder();
   }

   public static class Builder {
      private Url url;
      private SequenceOfHashedId8 cert;

      public DcEntry.Builder setUrl(Url var1) {
         this.url = var1;
         return this;
      }

      public DcEntry.Builder setCert(SequenceOfHashedId8 var1) {
         this.cert = var1;
         return this;
      }

      public DcEntry createDcEntry() {
         return new DcEntry(this.url, this.cert);
      }
   }
}
