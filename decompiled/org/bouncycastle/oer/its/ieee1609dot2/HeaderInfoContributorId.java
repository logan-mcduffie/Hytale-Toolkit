package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class HeaderInfoContributorId extends ASN1Object {
   private final BigInteger contributorId;
   private static final BigInteger MAX = BigInteger.valueOf(255L);

   public HeaderInfoContributorId(long var1) {
      this(BigInteger.valueOf(var1));
   }

   public HeaderInfoContributorId(BigInteger var1) {
      if (var1.signum() < 0 && var1.compareTo(MAX) > 0) {
         throw new IllegalArgumentException("contributor id " + var1 + " is out of range 0..255");
      } else {
         this.contributorId = var1;
      }
   }

   private HeaderInfoContributorId(ASN1Integer var1) {
      this(var1.getValue());
   }

   public static HeaderInfoContributorId getInstance(Object var0) {
      if (var0 instanceof HeaderInfoContributorId) {
         return (HeaderInfoContributorId)var0;
      } else {
         return var0 != null ? new HeaderInfoContributorId(ASN1Integer.getInstance(var0)) : null;
      }
   }

   public BigInteger getContributorId() {
      return this.contributorId;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.contributorId);
   }
}
