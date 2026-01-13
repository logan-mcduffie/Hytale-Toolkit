package org.bouncycastle.asn1.esf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevVals extends ASN1Object {
   private ASN1ObjectIdentifier otherRevValType;
   private ASN1Encodable otherRevVals;

   public static OtherRevVals getInstance(Object var0) {
      if (var0 instanceof OtherRevVals) {
         return (OtherRevVals)var0;
      } else {
         return var0 != null ? new OtherRevVals(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OtherRevVals(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.otherRevValType = (ASN1ObjectIdentifier)var1.getObjectAt(0);

         try {
            this.otherRevVals = ASN1Primitive.fromByteArray(var1.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
         } catch (IOException var3) {
            throw new IllegalStateException();
         }
      }
   }

   public OtherRevVals(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.otherRevValType = var1;
      this.otherRevVals = var2;
   }

   public ASN1ObjectIdentifier getOtherRevValType() {
      return this.otherRevValType;
   }

   public ASN1Encodable getOtherRevVals() {
      return this.otherRevVals;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.otherRevValType, this.otherRevVals);
   }
}
