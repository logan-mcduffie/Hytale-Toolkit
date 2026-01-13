package org.bouncycastle.oer;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;

public abstract class SwitchIndexer {
   public abstract ASN1Encodable get(int var1);

   public static class Asn1EncodableVectorIndexer extends SwitchIndexer {
      private final ASN1EncodableVector asn1EncodableVector;

      public Asn1EncodableVectorIndexer(ASN1EncodableVector var1) {
         this.asn1EncodableVector = var1;
      }

      @Override
      public ASN1Encodable get(int var1) {
         return this.asn1EncodableVector.get(var1);
      }
   }

   public static class Asn1SequenceIndexer extends SwitchIndexer {
      private final ASN1Sequence sequence;

      public Asn1SequenceIndexer(ASN1Sequence var1) {
         this.sequence = var1;
      }

      @Override
      public ASN1Encodable get(int var1) {
         return this.sequence.getObjectAt(var1);
      }
   }

   public static class FixedValueIndexer extends SwitchIndexer {
      private final ASN1Encodable returnValue;

      public FixedValueIndexer(ASN1Encodable var1) {
         this.returnValue = var1;
      }

      @Override
      public ASN1Encodable get(int var1) {
         return this.returnValue;
      }
   }
}
