package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;

public class MissingCrlIdentifier extends ASN1Object {
   private final HashedId3 cracaId;
   private final CrlSeries crlSeries;

   public MissingCrlIdentifier(HashedId3 var1, CrlSeries var2) {
      this.cracaId = var1;
      this.crlSeries = var2;
   }

   private MissingCrlIdentifier(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.cracaId = HashedId3.getInstance(var1.getObjectAt(0));
         this.crlSeries = CrlSeries.getInstance(var1.getObjectAt(1));
      }
   }

   public static MissingCrlIdentifier getInstance(Object var0) {
      if (var0 instanceof MissingCrlIdentifier) {
         return (MissingCrlIdentifier)var0;
      } else {
         return var0 != null ? new MissingCrlIdentifier(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.cracaId, this.crlSeries);
   }

   public HashedId3 getCracaId() {
      return this.cracaId;
   }

   public CrlSeries getCrlSeries() {
      return this.crlSeries;
   }

   public static MissingCrlIdentifier.Builder builder() {
      return new MissingCrlIdentifier.Builder();
   }

   public static class Builder {
      private HashedId3 cracaId;
      private CrlSeries crlSeries;

      public MissingCrlIdentifier.Builder setCracaId(HashedId3 var1) {
         this.cracaId = var1;
         return this;
      }

      public MissingCrlIdentifier.Builder setCrlSeries(CrlSeries var1) {
         this.crlSeries = var1;
         return this;
      }

      public MissingCrlIdentifier createMissingCrlIdentifier() {
         return new MissingCrlIdentifier(this.cracaId, this.crlSeries);
      }
   }
}
