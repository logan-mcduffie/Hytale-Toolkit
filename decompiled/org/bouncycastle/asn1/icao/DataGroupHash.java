package org.bouncycastle.asn1.icao;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DataGroupHash extends ASN1Object {
   ASN1Integer dataGroupNumber;
   ASN1OctetString dataGroupHashValue;

   public static DataGroupHash getInstance(Object var0) {
      if (var0 instanceof DataGroupHash) {
         return (DataGroupHash)var0;
      } else {
         return var0 != null ? new DataGroupHash(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private DataGroupHash(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.dataGroupNumber = ASN1Integer.getInstance(var2.nextElement());
      this.dataGroupHashValue = ASN1OctetString.getInstance(var2.nextElement());
   }

   public DataGroupHash(int var1, ASN1OctetString var2) {
      this.dataGroupNumber = new ASN1Integer(var1);
      this.dataGroupHashValue = var2;
   }

   public int getDataGroupNumber() {
      return this.dataGroupNumber.intValueExact();
   }

   public ASN1OctetString getDataGroupHashValue() {
      return this.dataGroupHashValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.dataGroupNumber, this.dataGroupHashValue);
   }
}
