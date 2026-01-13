package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class DigestedData extends ASN1Object {
   private ASN1Integer version;
   private AlgorithmIdentifier digestAlgorithm;
   private ContentInfo encapContentInfo;
   private ASN1OctetString digest;

   public DigestedData(AlgorithmIdentifier var1, ContentInfo var2, byte[] var3) {
      this.version = new ASN1Integer(0L);
      this.digestAlgorithm = var1;
      this.encapContentInfo = var2;
      this.digest = new DEROctetString(Arrays.clone(var3));
   }

   private DigestedData(ASN1Sequence var1) {
      this.version = (ASN1Integer)var1.getObjectAt(0);
      this.digestAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      this.encapContentInfo = ContentInfo.getInstance(var1.getObjectAt(2));
      this.digest = ASN1OctetString.getInstance(var1.getObjectAt(3));
   }

   public static DigestedData getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static DigestedData getInstance(Object var0) {
      if (var0 instanceof DigestedData) {
         return (DigestedData)var0;
      } else {
         return var0 != null ? new DigestedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   public ContentInfo getEncapContentInfo() {
      return this.encapContentInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.version);
      var1.add(this.digestAlgorithm);
      var1.add(this.encapContentInfo);
      var1.add(this.digest);
      return new BERSequence(var1);
   }

   public byte[] getDigest() {
      return Arrays.clone(this.digest.getOctets());
   }
}
