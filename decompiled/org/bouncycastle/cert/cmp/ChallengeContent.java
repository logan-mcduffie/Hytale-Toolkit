package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import org.bouncycastle.asn1.cmp.Challenge;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;

public class ChallengeContent {
   private final Challenge challenge;
   private final DigestCalculator owfCalc;

   ChallengeContent(Challenge var1, DigestCalculator var2) {
      this.challenge = var1;
      this.owfCalc = var2;
   }

   public byte[] extractChallenge(PKIHeader var1, Recipient var2) throws CMPException {
      try {
         CMSEnvelopedData var3 = new CMSEnvelopedData(new ContentInfo(PKCSObjectIdentifiers.envelopedData, this.challenge.getEncryptedRand()));
         Collection var4 = var3.getRecipientInfos().getRecipients();
         RecipientInformation var5 = (RecipientInformation)var4.iterator().next();
         byte[] var6 = var5.getContent(var2);
         Challenge.Rand var7 = Challenge.Rand.getInstance(var6);
         if (!Arrays.constantTimeAreEqual(var7.getSender().getEncoded(), var1.getSender().getEncoded())) {
            throw new CMPChallengeFailedException("incorrect sender found");
         } else {
            OutputStream var8 = this.owfCalc.getOutputStream();
            var8.write(var7.getInt().getEncoded());
            var8.close();
            if (!Arrays.constantTimeAreEqual(this.challenge.getWitness(), this.owfCalc.getDigest())) {
               throw new CMPChallengeFailedException("corrupted challenge found");
            } else {
               return var7.getInt().getValue().toByteArray();
            }
         }
      } catch (CMSException var9) {
         throw new CMPException(var9.getMessage(), var9);
      } catch (IOException var10) {
         throw new CMPException(var10.getMessage(), var10);
      }
   }
}
