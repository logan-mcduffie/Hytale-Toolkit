package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class CMCPublicationInfo extends ASN1Object {
   private final AlgorithmIdentifier hashAlg;
   private final ASN1Sequence certHashes;
   private final PKIPublicationInfo pubInfo;

   public CMCPublicationInfo(AlgorithmIdentifier var1, byte[][] var2, PKIPublicationInfo var3) {
      this.hashAlg = var1;
      ASN1EncodableVector var4 = new ASN1EncodableVector(var2.length);

      for (int var5 = 0; var5 != var2.length; var5++) {
         var4.add(new DEROctetString(Arrays.clone(var2[var5])));
      }

      this.certHashes = new DERSequence(var4);
      this.pubInfo = var3;
   }

   private CMCPublicationInfo(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.hashAlg = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.certHashes = ASN1Sequence.getInstance(var1.getObjectAt(1));
         this.pubInfo = PKIPublicationInfo.getInstance(var1.getObjectAt(2));
      }
   }

   public static CMCPublicationInfo getInstance(Object var0) {
      if (var0 instanceof CMCPublicationInfo) {
         return (CMCPublicationInfo)var0;
      } else {
         return var0 != null ? new CMCPublicationInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getHashAlg() {
      return this.hashAlg;
   }

   public byte[][] getCertHashes() {
      byte[][] var1 = new byte[this.certHashes.size()][];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Arrays.clone(ASN1OctetString.getInstance(this.certHashes.getObjectAt(var2)).getOctets());
      }

      return var1;
   }

   public PKIPublicationInfo getPubInfo() {
      return this.pubInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.hashAlg);
      var1.add(this.certHashes);
      var1.add(this.pubInfo);
      return new DERSequence(var1);
   }
}
