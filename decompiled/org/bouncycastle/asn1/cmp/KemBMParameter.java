package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KemBMParameter extends ASN1Object {
   private final AlgorithmIdentifier kdf;
   private final ASN1Integer len;
   private final AlgorithmIdentifier mac;

   private KemBMParameter(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("sequence size should 3");
      } else {
         this.kdf = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.len = ASN1Integer.getInstance(var1.getObjectAt(1));
         this.mac = AlgorithmIdentifier.getInstance(var1.getObjectAt(2));
      }
   }

   public KemBMParameter(AlgorithmIdentifier var1, ASN1Integer var2, AlgorithmIdentifier var3) {
      this.kdf = var1;
      this.len = var2;
      this.mac = var3;
   }

   public KemBMParameter(AlgorithmIdentifier var1, long var2, AlgorithmIdentifier var4) {
      this(var1, new ASN1Integer(var2), var4);
   }

   public static KemBMParameter getInstance(Object var0) {
      if (var0 instanceof KemBMParameter) {
         return (KemBMParameter)var0;
      } else {
         return var0 != null ? new KemBMParameter(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getKdf() {
      return this.kdf;
   }

   public ASN1Integer getLen() {
      return this.len;
   }

   public AlgorithmIdentifier getMac() {
      return this.mac;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.kdf);
      var1.add(this.len);
      var1.add(this.mac);
      return new DERSequence(var1);
   }
}
