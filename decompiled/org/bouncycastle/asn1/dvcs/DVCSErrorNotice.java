package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.GeneralName;

public class DVCSErrorNotice extends ASN1Object {
   private PKIStatusInfo transactionStatus;
   private GeneralName transactionIdentifier;

   public DVCSErrorNotice(PKIStatusInfo var1) {
      this(var1, null);
   }

   public DVCSErrorNotice(PKIStatusInfo var1, GeneralName var2) {
      this.transactionStatus = var1;
      this.transactionIdentifier = var2;
   }

   private DVCSErrorNotice(ASN1Sequence var1) {
      this.transactionStatus = PKIStatusInfo.getInstance(var1.getObjectAt(0));
      if (var1.size() > 1) {
         this.transactionIdentifier = GeneralName.getInstance(var1.getObjectAt(1));
      }
   }

   public static DVCSErrorNotice getInstance(Object var0) {
      if (var0 instanceof DVCSErrorNotice) {
         return (DVCSErrorNotice)var0;
      } else {
         return var0 != null ? new DVCSErrorNotice(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static DVCSErrorNotice getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.transactionStatus);
      if (this.transactionIdentifier != null) {
         var1.add(this.transactionIdentifier);
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      return "DVCSErrorNotice {\ntransactionStatus: "
         + this.transactionStatus
         + "\n"
         + (this.transactionIdentifier != null ? "transactionIdentifier: " + this.transactionIdentifier + "\n" : "")
         + "}\n";
   }

   public PKIStatusInfo getTransactionStatus() {
      return this.transactionStatus;
   }

   public GeneralName getTransactionIdentifier() {
      return this.transactionIdentifier;
   }
}
