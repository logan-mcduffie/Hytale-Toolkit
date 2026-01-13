package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SignedPublicKeyAndChallenge extends ASN1Object {
   private final PublicKeyAndChallenge pubKeyAndChal;
   private final ASN1Sequence pkacSeq;

   public static SignedPublicKeyAndChallenge getInstance(Object var0) {
      if (var0 instanceof SignedPublicKeyAndChallenge) {
         return (SignedPublicKeyAndChallenge)var0;
      } else {
         return var0 != null ? new SignedPublicKeyAndChallenge(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SignedPublicKeyAndChallenge(ASN1Sequence var1) {
      this.pkacSeq = var1;
      this.pubKeyAndChal = PublicKeyAndChallenge.getInstance(var1.getObjectAt(0));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.pkacSeq;
   }

   public PublicKeyAndChallenge getPublicKeyAndChallenge() {
      return this.pubKeyAndChal;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return AlgorithmIdentifier.getInstance(this.pkacSeq.getObjectAt(1));
   }

   public ASN1BitString getSignature() {
      return ASN1BitString.getInstance(this.pkacSeq.getObjectAt(2));
   }
}
