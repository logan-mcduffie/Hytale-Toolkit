package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class XMSSMTKeyParams extends ASN1Object {
   private final ASN1Integer version;
   private final int height;
   private final int layers;
   private final AlgorithmIdentifier treeDigest;

   public XMSSMTKeyParams(int var1, int var2, AlgorithmIdentifier var3) {
      this.version = new ASN1Integer(0L);
      this.height = var1;
      this.layers = var2;
      this.treeDigest = var3;
   }

   private XMSSMTKeyParams(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0));
      this.height = ASN1Integer.getInstance(var1.getObjectAt(1)).intValueExact();
      this.layers = ASN1Integer.getInstance(var1.getObjectAt(2)).intValueExact();
      this.treeDigest = AlgorithmIdentifier.getInstance(var1.getObjectAt(3));
   }

   public static XMSSMTKeyParams getInstance(Object var0) {
      if (var0 instanceof XMSSMTKeyParams) {
         return (XMSSMTKeyParams)var0;
      } else {
         return var0 != null ? new XMSSMTKeyParams(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public int getHeight() {
      return this.height;
   }

   public int getLayers() {
      return this.layers;
   }

   public AlgorithmIdentifier getTreeDigest() {
      return this.treeDigest;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(this.version);
      var1.add(new ASN1Integer(this.height));
      var1.add(new ASN1Integer(this.layers));
      var1.add(this.treeDigest);
      return new DERSequence(var1);
   }
}
