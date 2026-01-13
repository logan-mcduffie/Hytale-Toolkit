package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class POPODecKeyChallContent extends ASN1Object {
   private final ASN1Sequence content;

   private POPODecKeyChallContent(ASN1Sequence var1) {
      this.content = var1;
   }

   public static POPODecKeyChallContent getInstance(Object var0) {
      if (var0 instanceof POPODecKeyChallContent) {
         return (POPODecKeyChallContent)var0;
      } else {
         return var0 != null ? new POPODecKeyChallContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public POPODecKeyChallContent(Challenge... var1) {
      this.content = new DERSequence(var1);
   }

   public Challenge[] toChallengeArray() {
      Challenge[] var1 = new Challenge[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Challenge.getInstance(this.content.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
