package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PublishTrustAnchors extends ASN1Object {
   private final ASN1Integer seqNumber;
   private final AlgorithmIdentifier hashAlgorithm;
   private final ASN1Sequence anchorHashes;

   public PublishTrustAnchors(BigInteger var1, AlgorithmIdentifier var2, byte[][] var3) {
      this.seqNumber = new ASN1Integer(var1);
      this.hashAlgorithm = var2;
      ASN1EncodableVector var4 = new ASN1EncodableVector(var3.length);

      for (int var5 = 0; var5 != var3.length; var5++) {
         var4.add(new DEROctetString(Arrays.clone(var3[var5])));
      }

      this.anchorHashes = new DERSequence(var4);
   }

   private PublishTrustAnchors(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.seqNumber = ASN1Integer.getInstance(var1.getObjectAt(0));
         this.hashAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.anchorHashes = ASN1Sequence.getInstance(var1.getObjectAt(2));
      }
   }

   public static PublishTrustAnchors getInstance(Object var0) {
      if (var0 instanceof PublishTrustAnchors) {
         return (PublishTrustAnchors)var0;
      } else {
         return var0 != null ? new PublishTrustAnchors(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public BigInteger getSeqNumber() {
      return this.seqNumber.getValue();
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public byte[][] getAnchorHashes() {
      byte[][] var1 = new byte[this.anchorHashes.size()][];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Arrays.clone(ASN1OctetString.getInstance(this.anchorHashes.getObjectAt(var2)).getOctets());
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.seqNumber);
      var1.add(this.hashAlgorithm);
      var1.add(this.anchorHashes);
      return new DERSequence(var1);
   }
}
