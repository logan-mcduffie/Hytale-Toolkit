package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CompressedData extends ASN1Object {
   private ASN1Integer version;
   private AlgorithmIdentifier compressionAlgorithm;
   private ContentInfo encapContentInfo;

   public CompressedData(AlgorithmIdentifier var1, ContentInfo var2) {
      this.version = new ASN1Integer(0L);
      this.compressionAlgorithm = var1;
      this.encapContentInfo = var2;
   }

   private CompressedData(ASN1Sequence var1) {
      this.version = (ASN1Integer)var1.getObjectAt(0);
      this.compressionAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
      this.encapContentInfo = ContentInfo.getInstance(var1.getObjectAt(2));
   }

   public static CompressedData getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static CompressedData getInstance(Object var0) {
      if (var0 instanceof CompressedData) {
         return (CompressedData)var0;
      } else {
         return var0 != null ? new CompressedData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
      return this.compressionAlgorithm;
   }

   public ContentInfo getEncapContentInfo() {
      return this.encapContentInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.version);
      var1.add(this.compressionAlgorithm);
      var1.add(this.encapContentInfo);
      return new BERSequence(var1);
   }
}
