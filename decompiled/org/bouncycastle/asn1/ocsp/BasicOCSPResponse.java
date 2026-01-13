package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BasicOCSPResponse extends ASN1Object {
   private ResponseData tbsResponseData;
   private AlgorithmIdentifier signatureAlgorithm;
   private DERBitString signature;
   private ASN1Sequence certs;

   public BasicOCSPResponse(ResponseData var1, AlgorithmIdentifier var2, DERBitString var3, ASN1Sequence var4) {
      this.tbsResponseData = var1;
      this.signatureAlgorithm = var2;
      this.signature = var3;
      this.certs = var4;
   }

   private BasicOCSPResponse(ASN1Sequence var1) {
      this.tbsResponseData = ResponseData.getInstance(var1.getObjectAt(0));
      this.signatureAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      this.signature = (DERBitString)var1.getObjectAt(2);
      if (var1.size() > 3) {
         this.certs = ASN1Sequence.getInstance((ASN1TaggedObject)var1.getObjectAt(3), true);
      }
   }

   public static BasicOCSPResponse getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static BasicOCSPResponse getInstance(Object var0) {
      if (var0 instanceof BasicOCSPResponse) {
         return (BasicOCSPResponse)var0;
      } else {
         return var0 != null ? new BasicOCSPResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ResponseData getTbsResponseData() {
      return this.tbsResponseData;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   public DERBitString getSignature() {
      return this.signature;
   }

   public ASN1Sequence getCerts() {
      return this.certs;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.tbsResponseData);
      var1.add(this.signatureAlgorithm);
      var1.add(this.signature);
      if (this.certs != null) {
         var1.add(new DERTaggedObject(true, 0, this.certs));
      }

      return new DERSequence(var1);
   }
}
