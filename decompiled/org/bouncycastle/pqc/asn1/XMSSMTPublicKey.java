package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class XMSSMTPublicKey extends ASN1Object {
   private final byte[] publicSeed;
   private final byte[] root;

   public XMSSMTPublicKey(byte[] var1, byte[] var2) {
      this.publicSeed = Arrays.clone(var1);
      this.root = Arrays.clone(var2);
   }

   private XMSSMTPublicKey(ASN1Sequence var1) {
      if (!ASN1Integer.getInstance(var1.getObjectAt(0)).hasValue(0)) {
         throw new IllegalArgumentException("unknown version of sequence");
      } else {
         this.publicSeed = Arrays.clone(DEROctetString.getInstance(var1.getObjectAt(1)).getOctets());
         this.root = Arrays.clone(DEROctetString.getInstance(var1.getObjectAt(2)).getOctets());
      }
   }

   public static XMSSMTPublicKey getInstance(Object var0) {
      if (var0 instanceof XMSSMTPublicKey) {
         return (XMSSMTPublicKey)var0;
      } else {
         return var0 != null ? new XMSSMTPublicKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public byte[] getPublicSeed() {
      return Arrays.clone(this.publicSeed);
   }

   public byte[] getRoot() {
      return Arrays.clone(this.root);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new ASN1Integer(0L));
      var1.add(new DEROctetString(this.publicSeed));
      var1.add(new DEROctetString(this.root));
      return new DERSequence(var1);
   }
}
