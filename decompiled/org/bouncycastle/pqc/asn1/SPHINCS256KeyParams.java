package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SPHINCS256KeyParams extends ASN1Object {
   private final ASN1Integer version;
   private final AlgorithmIdentifier treeDigest;

   public SPHINCS256KeyParams(AlgorithmIdentifier var1) {
      this.version = new ASN1Integer(0L);
      this.treeDigest = var1;
   }

   private SPHINCS256KeyParams(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0));
      this.treeDigest = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
   }

   public static final SPHINCS256KeyParams getInstance(Object var0) {
      if (var0 instanceof SPHINCS256KeyParams) {
         return (SPHINCS256KeyParams)var0;
      } else {
         return var0 != null ? new SPHINCS256KeyParams(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getTreeDigest() {
      return this.treeDigest;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(this.version);
      var1.add(this.treeDigest);
      return new DERSequence(var1);
   }
}
