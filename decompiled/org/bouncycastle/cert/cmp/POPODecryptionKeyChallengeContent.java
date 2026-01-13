package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cmp.Challenge;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.POPODecKeyChallContent;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class POPODecryptionKeyChallengeContent {
   private final ASN1Sequence content;
   private final DigestCalculatorProvider owfCalcProvider;

   POPODecryptionKeyChallengeContent(POPODecKeyChallContent var1, DigestCalculatorProvider var2) {
      this.content = ASN1Sequence.getInstance(var1.toASN1Primitive());
      this.owfCalcProvider = var2;
   }

   public ChallengeContent[] toChallengeArray() throws CMPException {
      ChallengeContent[] var1 = new ChallengeContent[this.content.size()];
      DigestCalculator var2 = null;

      for (int var3 = 0; var3 != var1.length; var3++) {
         Challenge var4 = Challenge.getInstance(this.content.getObjectAt(var3));
         if (var4.getOwf() != null) {
            try {
               var2 = this.owfCalcProvider.get(var4.getOwf());
            } catch (OperatorCreationException var6) {
               throw new CMPException(var6.getMessage(), var6);
            }
         }

         var1[var3] = new ChallengeContent(Challenge.getInstance(this.content.getObjectAt(var3)), var2);
      }

      return var1;
   }

   public static POPODecryptionKeyChallengeContent fromPKIBody(PKIBody var0, DigestCalculatorProvider var1) {
      if (var0.getType() != 5) {
         throw new IllegalArgumentException("content of PKIBody wrong type: " + var0.getType());
      } else {
         return new POPODecryptionKeyChallengeContent(POPODecKeyChallContent.getInstance(var0.getContent()), var1);
      }
   }

   public POPODecKeyChallContent toASN1Structure() {
      return POPODecKeyChallContent.getInstance(this.content);
   }
}
