package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class DVCSRequest extends ASN1Object {
   private DVCSRequestInformation requestInformation;
   private Data data;
   private GeneralName transactionIdentifier;

   public DVCSRequest(DVCSRequestInformation var1, Data var2) {
      this(var1, var2, null);
   }

   public DVCSRequest(DVCSRequestInformation var1, Data var2, GeneralName var3) {
      this.requestInformation = var1;
      this.data = var2;
      this.transactionIdentifier = var3;
   }

   private DVCSRequest(ASN1Sequence var1) {
      this.requestInformation = DVCSRequestInformation.getInstance(var1.getObjectAt(0));
      this.data = Data.getInstance(var1.getObjectAt(1));
      if (var1.size() > 2) {
         this.transactionIdentifier = GeneralName.getInstance(var1.getObjectAt(2));
      }
   }

   public static DVCSRequest getInstance(Object var0) {
      if (var0 instanceof DVCSRequest) {
         return (DVCSRequest)var0;
      } else {
         return var0 != null ? new DVCSRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static DVCSRequest getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.requestInformation);
      var1.add(this.data);
      if (this.transactionIdentifier != null) {
         var1.add(this.transactionIdentifier);
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      return "DVCSRequest {\nrequestInformation: "
         + this.requestInformation
         + "\ndata: "
         + this.data
         + "\n"
         + (this.transactionIdentifier != null ? "transactionIdentifier: " + this.transactionIdentifier + "\n" : "")
         + "}\n";
   }

   public Data getData() {
      return this.data;
   }

   public DVCSRequestInformation getRequestInformation() {
      return this.requestInformation;
   }

   public GeneralName getTransactionIdentifier() {
      return this.transactionIdentifier;
   }
}
