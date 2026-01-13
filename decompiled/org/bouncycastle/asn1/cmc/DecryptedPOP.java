package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class DecryptedPOP extends ASN1Object {
   private final BodyPartID bodyPartID;
   private final AlgorithmIdentifier thePOPAlgID;
   private final byte[] thePOP;

   public DecryptedPOP(BodyPartID var1, AlgorithmIdentifier var2, byte[] var3) {
      this.bodyPartID = var1;
      this.thePOPAlgID = var2;
      this.thePOP = Arrays.clone(var3);
   }

   private DecryptedPOP(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.bodyPartID = BodyPartID.getInstance(var1.getObjectAt(0));
         this.thePOPAlgID = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.thePOP = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
      }
   }

   public static DecryptedPOP getInstance(Object var0) {
      if (var0 instanceof DecryptedPOP) {
         return (DecryptedPOP)var0;
      } else {
         return var0 != null ? new DecryptedPOP(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public BodyPartID getBodyPartID() {
      return this.bodyPartID;
   }

   public AlgorithmIdentifier getThePOPAlgID() {
      return this.thePOPAlgID;
   }

   public byte[] getThePOP() {
      return Arrays.clone(this.thePOP);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.bodyPartID);
      var1.add(this.thePOPAlgID);
      var1.add(new DEROctetString(this.thePOP));
      return new DERSequence(var1);
   }
}
