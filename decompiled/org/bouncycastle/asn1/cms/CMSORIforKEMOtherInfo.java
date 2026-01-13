package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSORIforKEMOtherInfo extends ASN1Object {
   private final AlgorithmIdentifier wrap;
   private final int kekLength;
   private final byte[] ukm;

   public CMSORIforKEMOtherInfo(AlgorithmIdentifier var1, int var2) {
      this(var1, var2, null);
   }

   public CMSORIforKEMOtherInfo(AlgorithmIdentifier var1, int var2, byte[] var3) {
      if (var2 > 65535) {
         throw new IllegalArgumentException("kekLength must be <= 65535");
      } else {
         this.wrap = var1;
         this.kekLength = var2;
         this.ukm = var3;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(this.wrap);
      var1.add(new ASN1Integer(this.kekLength));
      if (this.ukm != null) {
         var1.add(new DERTaggedObject(true, 0, new DEROctetString(this.ukm)));
      }

      return new DERSequence(var1);
   }
}
