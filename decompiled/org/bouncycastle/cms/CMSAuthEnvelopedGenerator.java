package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.OriginatorInfo;

public class CMSAuthEnvelopedGenerator extends CMSEnvelopedGenerator {
   public static final String AES128_CCM = CMSAlgorithm.AES128_CCM.getId();
   public static final String AES192_CCM = CMSAlgorithm.AES192_CCM.getId();
   public static final String AES256_CCM = CMSAlgorithm.AES256_CCM.getId();
   public static final String AES128_GCM = CMSAlgorithm.AES128_GCM.getId();
   public static final String AES192_GCM = CMSAlgorithm.AES192_GCM.getId();
   public static final String AES256_GCM = CMSAlgorithm.AES256_GCM.getId();
   public static final String ChaCha20Poly1305 = CMSAlgorithm.ChaCha20Poly1305.getId();
   protected CMSAttributeTableGenerator authAttrsGenerator = null;
   protected CMSAttributeTableGenerator unauthAttrsGenerator = null;
   protected OriginatorInfo originatorInfo;

   protected CMSAuthEnvelopedGenerator() {
   }

   public void setAuthenticatedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.authAttrsGenerator = var1;
   }

   public void setUnauthenticatedAttributeGenerator(CMSAttributeTableGenerator var1) {
      this.unauthAttrsGenerator = var1;
   }

   @Override
   public void setOriginatorInfo(OriginatorInformation var1) {
      this.originatorInfo = var1.toASN1Structure();
   }

   @Override
   public void addRecipientInfoGenerator(RecipientInfoGenerator var1) {
      this.recipientInfoGenerators.add(var1);
   }
}
