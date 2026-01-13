package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class AlgorithmIdentifier extends ASN1Object {
   private ASN1ObjectIdentifier algorithm;
   private ASN1Encodable parameters;

   public static AlgorithmIdentifier getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static AlgorithmIdentifier getInstance(Object var0) {
      if (var0 instanceof AlgorithmIdentifier) {
         return (AlgorithmIdentifier)var0;
      } else {
         return var0 != null ? new AlgorithmIdentifier(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier(ASN1ObjectIdentifier var1) {
      this.algorithm = var1;
   }

   public AlgorithmIdentifier(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.algorithm = var1;
      this.parameters = var2;
   }

   private AlgorithmIdentifier(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 2) {
         this.algorithm = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         if (var1.size() == 2) {
            this.parameters = var1.getObjectAt(1);
         } else {
            this.parameters = null;
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public ASN1ObjectIdentifier getAlgorithm() {
      return this.algorithm;
   }

   public ASN1Encodable getParameters() {
      return this.parameters;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.algorithm);
      if (this.parameters != null) {
         var1.add(this.parameters);
      }

      return new DERSequence(var1);
   }
}
