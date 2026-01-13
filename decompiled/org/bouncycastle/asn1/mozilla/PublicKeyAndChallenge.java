package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class PublicKeyAndChallenge extends ASN1Object {
   private ASN1Sequence pkacSeq;
   private SubjectPublicKeyInfo spki;
   private ASN1IA5String challenge;

   public static PublicKeyAndChallenge getInstance(Object var0) {
      if (var0 instanceof PublicKeyAndChallenge) {
         return (PublicKeyAndChallenge)var0;
      } else {
         return var0 != null ? new PublicKeyAndChallenge(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private PublicKeyAndChallenge(ASN1Sequence var1) {
      this.pkacSeq = var1;
      this.spki = SubjectPublicKeyInfo.getInstance(var1.getObjectAt(0));
      this.challenge = ASN1IA5String.getInstance(var1.getObjectAt(1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.pkacSeq;
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.spki;
   }

   /** @deprecated */
   public DERIA5String getChallenge() {
      return null != this.challenge && !(this.challenge instanceof DERIA5String)
         ? new DERIA5String(this.challenge.getString(), false)
         : (DERIA5String)this.challenge;
   }

   public ASN1IA5String getChallengeIA5() {
      return this.challenge;
   }
}
