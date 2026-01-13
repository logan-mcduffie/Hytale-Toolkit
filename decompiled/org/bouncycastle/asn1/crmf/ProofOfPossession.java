package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;

public class ProofOfPossession extends ASN1Object implements ASN1Choice {
   public static final int TYPE_RA_VERIFIED = 0;
   public static final int TYPE_SIGNING_KEY = 1;
   public static final int TYPE_KEY_ENCIPHERMENT = 2;
   public static final int TYPE_KEY_AGREEMENT = 3;
   private int tagNo;
   private ASN1Encodable obj;

   private ProofOfPossession(ASN1TaggedObject var1) {
      this.tagNo = var1.getTagNo();
      switch (this.tagNo) {
         case 0:
            this.obj = DERNull.INSTANCE;
            break;
         case 1:
            this.obj = POPOSigningKey.getInstance(var1, false);
            break;
         case 2:
         case 3:
            this.obj = POPOPrivKey.getInstance(var1, true);
            break;
         default:
            throw new IllegalArgumentException("unknown tag: " + this.tagNo);
      }
   }

   public static ProofOfPossession getInstance(Object var0) {
      if (var0 == null || var0 instanceof ProofOfPossession) {
         return (ProofOfPossession)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new ProofOfPossession((ASN1TaggedObject)var0);
      } else {
         throw new IllegalArgumentException("Invalid object: " + var0.getClass().getName());
      }
   }

   public ProofOfPossession() {
      this.tagNo = 0;
      this.obj = DERNull.INSTANCE;
   }

   public ProofOfPossession(POPOSigningKey var1) {
      this.tagNo = 1;
      this.obj = var1;
   }

   public ProofOfPossession(int var1, POPOPrivKey var2) {
      this.tagNo = var1;
      this.obj = var2;
   }

   public int getType() {
      return this.tagNo;
   }

   public ASN1Encodable getObject() {
      return this.obj;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(false, this.tagNo, this.obj);
   }
}
