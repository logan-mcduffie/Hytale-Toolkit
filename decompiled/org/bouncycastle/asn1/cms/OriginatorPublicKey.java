package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OriginatorPublicKey extends ASN1Object {
   private AlgorithmIdentifier algorithm;
   private ASN1BitString publicKey;

   public OriginatorPublicKey(AlgorithmIdentifier var1, byte[] var2) {
      this.algorithm = var1;
      this.publicKey = new DERBitString(var2);
   }

   public OriginatorPublicKey(AlgorithmIdentifier var1, ASN1BitString var2) {
      this.algorithm = var1;
      this.publicKey = var2;
   }

   private OriginatorPublicKey(ASN1Sequence var1) {
      this.algorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      this.publicKey = (DERBitString)var1.getObjectAt(1);
   }

   public static OriginatorPublicKey getInstance(ASN1TaggedObject var0, boolean var1) {
      return new OriginatorPublicKey(ASN1Sequence.getInstance(var0, var1));
   }

   public static OriginatorPublicKey getInstance(Object var0) {
      if (var0 instanceof OriginatorPublicKey) {
         return (OriginatorPublicKey)var0;
      } else {
         return var0 != null ? new OriginatorPublicKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getAlgorithm() {
      return this.algorithm;
   }

   /** @deprecated */
   public DERBitString getPublicKey() {
      return DERBitString.convert(this.publicKey);
   }

   public ASN1BitString getPublicKeyData() {
      return this.publicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algorithm, this.publicKey);
   }
}
