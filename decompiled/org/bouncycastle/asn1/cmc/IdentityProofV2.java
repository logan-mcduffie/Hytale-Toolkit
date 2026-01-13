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

public class IdentityProofV2 extends ASN1Object {
   private final AlgorithmIdentifier proofAlgID;
   private final AlgorithmIdentifier macAlgId;
   private final byte[] witness;

   public IdentityProofV2(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) {
      this.proofAlgID = var1;
      this.macAlgId = var2;
      this.witness = Arrays.clone(var3);
   }

   private IdentityProofV2(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.proofAlgID = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.macAlgId = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.witness = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(2)).getOctets());
      }
   }

   public static IdentityProofV2 getInstance(Object var0) {
      if (var0 instanceof IdentityProofV2) {
         return (IdentityProofV2)var0;
      } else {
         return var0 != null ? new IdentityProofV2(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getProofAlgID() {
      return this.proofAlgID;
   }

   public AlgorithmIdentifier getMacAlgId() {
      return this.macAlgId;
   }

   public byte[] getWitness() {
      return Arrays.clone(this.witness);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.proofAlgID);
      var1.add(this.macAlgId);
      var1.add(new DEROctetString(this.getWitness()));
      return new DERSequence(var1);
   }
}
