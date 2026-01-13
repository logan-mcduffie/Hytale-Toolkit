package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class BodyPartID extends ASN1Object {
   public static final long bodyIdMax = 4294967295L;
   private final long id;

   public BodyPartID(long var1) {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.id = var1;
      } else {
         throw new IllegalArgumentException("id out of range");
      }
   }

   private static long convert(BigInteger var0) {
      if (var0.bitLength() > 32) {
         throw new IllegalArgumentException("id out of range");
      } else {
         return var0.longValue();
      }
   }

   private BodyPartID(ASN1Integer var1) {
      this(convert(var1.getValue()));
   }

   public static BodyPartID getInstance(Object var0) {
      if (var0 instanceof BodyPartID) {
         return (BodyPartID)var0;
      } else {
         return var0 != null ? new BodyPartID(ASN1Integer.getInstance(var0)) : null;
      }
   }

   public long getID() {
      return this.id;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new ASN1Integer(this.id);
   }
}
