package org.bouncycastle.asn1.esf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevRefs extends ASN1Object {
   private ASN1ObjectIdentifier otherRevRefType;
   private ASN1Encodable otherRevRefs;

   public static OtherRevRefs getInstance(Object var0) {
      if (var0 instanceof OtherRevRefs) {
         return (OtherRevRefs)var0;
      } else {
         return var0 != null ? new OtherRevRefs(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OtherRevRefs(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.otherRevRefType = new ASN1ObjectIdentifier(((ASN1ObjectIdentifier)var1.getObjectAt(0)).getId());

         try {
            this.otherRevRefs = ASN1Primitive.fromByteArray(var1.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
         } catch (IOException var3) {
            throw new IllegalStateException();
         }
      }
   }

   public OtherRevRefs(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.otherRevRefType = var1;
      this.otherRevRefs = var2;
   }

   public ASN1ObjectIdentifier getOtherRevRefType() {
      return this.otherRevRefType;
   }

   public ASN1Encodable getOtherRevRefs() {
      return this.otherRevRefs;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.otherRevRefType, this.otherRevRefs);
   }
}
