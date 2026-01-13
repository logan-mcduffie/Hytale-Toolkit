package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertReqMsg;

public class TaggedRequest extends ASN1Object implements ASN1Choice {
   public static final int TCR = 0;
   public static final int CRM = 1;
   public static final int ORM = 2;
   private final int tagNo;
   private final ASN1Encodable value;

   public TaggedRequest(TaggedCertificationRequest var1) {
      this.tagNo = 0;
      this.value = var1;
   }

   public TaggedRequest(CertReqMsg var1) {
      this.tagNo = 1;
      this.value = var1;
   }

   private TaggedRequest(ASN1Sequence var1) {
      this.tagNo = 2;
      this.value = var1;
   }

   public static TaggedRequest getInstance(Object var0) {
      if (var0 instanceof TaggedRequest) {
         return (TaggedRequest)var0;
      } else if (var0 != null) {
         if (var0 instanceof ASN1Encodable) {
            ASN1TaggedObject var1 = ASN1TaggedObject.getInstance(((ASN1Encodable)var0).toASN1Primitive());
            switch (var1.getTagNo()) {
               case 0:
                  return new TaggedRequest(TaggedCertificationRequest.getInstance(var1, false));
               case 1:
                  return new TaggedRequest(CertReqMsg.getInstance(var1, false));
               case 2:
                  return new TaggedRequest(ASN1Sequence.getInstance(var1, false));
               default:
                  throw new IllegalArgumentException("unknown tag in getInstance(): " + var1.getTagNo());
            }
         } else if (var0 instanceof byte[]) {
            try {
               return getInstance(ASN1Primitive.fromByteArray((byte[])var0));
            } catch (IOException var2) {
               throw new IllegalArgumentException("unknown encoding in getInstance()");
            }
         } else {
            throw new IllegalArgumentException("unknown object in getInstance(): " + var0.getClass().getName());
         }
      } else {
         return null;
      }
   }

   public int getTagNo() {
      return this.tagNo;
   }

   public ASN1Encodable getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(false, this.tagNo, this.value);
   }
}
