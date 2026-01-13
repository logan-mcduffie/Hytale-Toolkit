package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ErrorMsgContent extends ASN1Object {
   private final PKIStatusInfo pkiStatusInfo;
   private ASN1Integer errorCode;
   private PKIFreeText errorDetails;

   private ErrorMsgContent(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.pkiStatusInfo = PKIStatusInfo.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (var3 instanceof ASN1Integer) {
            this.errorCode = ASN1Integer.getInstance(var3);
         } else {
            this.errorDetails = PKIFreeText.getInstance(var3);
         }
      }
   }

   public ErrorMsgContent(PKIStatusInfo var1) {
      this(var1, null, null);
   }

   public ErrorMsgContent(PKIStatusInfo var1, ASN1Integer var2, PKIFreeText var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("'pkiStatusInfo' cannot be null");
      } else {
         this.pkiStatusInfo = var1;
         this.errorCode = var2;
         this.errorDetails = var3;
      }
   }

   public static ErrorMsgContent getInstance(Object var0) {
      if (var0 instanceof ErrorMsgContent) {
         return (ErrorMsgContent)var0;
      } else {
         return var0 != null ? new ErrorMsgContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PKIStatusInfo getPKIStatusInfo() {
      return this.pkiStatusInfo;
   }

   public ASN1Integer getErrorCode() {
      return this.errorCode;
   }

   public PKIFreeText getErrorDetails() {
      return this.errorDetails;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.pkiStatusInfo);
      this.addOptional(var1, this.errorCode);
      this.addOptional(var1, this.errorDetails);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, ASN1Encodable var2) {
      if (var2 != null) {
         var1.add(var2);
      }
   }
}
