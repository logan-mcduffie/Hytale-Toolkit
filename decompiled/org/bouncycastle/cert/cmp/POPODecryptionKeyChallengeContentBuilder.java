package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.Challenge;
import org.bouncycastle.asn1.cmp.POPODecKeyChallContent;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class POPODecryptionKeyChallengeContentBuilder {
   private final DigestCalculator owfCalculator;
   private final ASN1ObjectIdentifier challengeEncAlg;
   private ASN1EncodableVector challenges = new ASN1EncodableVector();

   public POPODecryptionKeyChallengeContentBuilder(DigestCalculator var1, ASN1ObjectIdentifier var2) {
      this.owfCalculator = var1;
      this.challengeEncAlg = var2;
   }

   public POPODecryptionKeyChallengeContentBuilder addChallenge(RecipientInfoGenerator var1, GeneralName var2, byte[] var3) throws CMPException {
      byte[] var4 = Arrays.clone(var3);

      try {
         OutputStream var5 = this.owfCalculator.getOutputStream();
         var5.write(new ASN1Integer(var4).getEncoded());
         var5.close();
      } catch (IOException var8) {
         throw new CMPException("unable to calculate witness", var8);
      }

      CMSEnvelopedData var9;
      try {
         CMSEnvelopedDataGenerator var6 = new CMSEnvelopedDataGenerator();
         var6.addRecipientInfoGenerator(var1);
         var9 = var6.generate(
            new CMSProcessableByteArray(new Challenge.Rand(var3, var2).getEncoded()),
            new JceCMSContentEncryptorBuilder(this.challengeEncAlg).setProvider("BC").build()
         );
      } catch (Exception var7) {
         throw new CMPException("unable to encrypt challenge", var7);
      }

      EnvelopedData var10 = EnvelopedData.getInstance(var9.toASN1Structure().getContent());
      if (this.challenges.size() == 0) {
         this.challenges.add(new Challenge(this.owfCalculator.getAlgorithmIdentifier(), this.owfCalculator.getDigest(), var10));
      } else {
         this.challenges.add(new Challenge(this.owfCalculator.getDigest(), var10));
      }

      return this;
   }

   public POPODecryptionKeyChallengeContent build() {
      return new POPODecryptionKeyChallengeContent(POPODecKeyChallContent.getInstance(new DERSequence(this.challenges)), new DigestCalculatorProvider() {
         @Override
         public DigestCalculator get(AlgorithmIdentifier var1) throws OperatorCreationException {
            return POPODecryptionKeyChallengeContentBuilder.this.owfCalculator;
         }
      });
   }
}
