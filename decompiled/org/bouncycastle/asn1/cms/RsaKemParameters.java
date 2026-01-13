package org.bouncycastle.asn1.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RsaKemParameters extends ASN1Object {
   private final AlgorithmIdentifier keyDerivationFunction;
   private final BigInteger keyLength;

   private RsaKemParameters(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
      } else {
         this.keyDerivationFunction = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.keyLength = ASN1Integer.getInstance(var1.getObjectAt(1)).getValue();
      }
   }

   public static RsaKemParameters getInstance(Object var0) {
      if (var0 instanceof RsaKemParameters) {
         return (RsaKemParameters)var0;
      } else {
         return var0 != null ? new RsaKemParameters(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RsaKemParameters(AlgorithmIdentifier var1, int var2) {
      this.keyDerivationFunction = var1;
      this.keyLength = BigInteger.valueOf(var2);
   }

   public AlgorithmIdentifier getKeyDerivationFunction() {
      return this.keyDerivationFunction;
   }

   public BigInteger getKeyLength() {
      return this.keyLength;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.keyDerivationFunction, new ASN1Integer(this.keyLength));
   }
}
