package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class ResponseBytes extends ASN1Object {
   ASN1ObjectIdentifier responseType;
   ASN1OctetString response;

   public ResponseBytes(ASN1ObjectIdentifier var1, ASN1OctetString var2) {
      this.responseType = var1;
      this.response = var2;
   }

   private ResponseBytes(ASN1Sequence var1) {
      this.responseType = (ASN1ObjectIdentifier)var1.getObjectAt(0);
      this.response = (ASN1OctetString)var1.getObjectAt(1);
   }

   public static ResponseBytes getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static ResponseBytes getInstance(Object var0) {
      if (var0 instanceof ResponseBytes) {
         return (ResponseBytes)var0;
      } else {
         return var0 != null ? new ResponseBytes(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getResponseType() {
      return this.responseType;
   }

   public ASN1OctetString getResponse() {
      return this.response;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.responseType, this.response);
   }
}
