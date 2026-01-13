package org.bouncycastle.asn1.x509.qualified;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class MonetaryValue extends ASN1Object {
   private Iso4217CurrencyCode currency;
   private ASN1Integer amount;
   private ASN1Integer exponent;

   public static MonetaryValue getInstance(Object var0) {
      if (var0 instanceof MonetaryValue) {
         return (MonetaryValue)var0;
      } else {
         return var0 != null ? new MonetaryValue(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private MonetaryValue(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.currency = Iso4217CurrencyCode.getInstance(var2.nextElement());
      this.amount = ASN1Integer.getInstance(var2.nextElement());
      this.exponent = ASN1Integer.getInstance(var2.nextElement());
   }

   public MonetaryValue(Iso4217CurrencyCode var1, int var2, int var3) {
      this.currency = var1;
      this.amount = new ASN1Integer(var2);
      this.exponent = new ASN1Integer(var3);
   }

   public Iso4217CurrencyCode getCurrency() {
      return this.currency;
   }

   public BigInteger getAmount() {
      return this.amount.getValue();
   }

   public BigInteger getExponent() {
      return this.exponent.getValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.currency);
      var1.add(this.amount);
      var1.add(this.exponent);
      return new DERSequence(var1);
   }
}
