package org.bouncycastle.cms;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface PasswordRecipient extends Recipient {
   int PKCS5_SCHEME2 = 0;
   int PKCS5_SCHEME2_UTF8 = 1;

   byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException;

   RecipientOperator getRecipientOperator(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4) throws CMSException;

   int getPasswordConversionScheme();

   char[] getPassword();

   public static final class PRF {
      public static final PasswordRecipient.PRF HMacSHA1 = new PasswordRecipient.PRF(
         "HMacSHA1", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE)
      );
      public static final PasswordRecipient.PRF HMacSHA224 = new PasswordRecipient.PRF(
         "HMacSHA224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, DERNull.INSTANCE)
      );
      public static final PasswordRecipient.PRF HMacSHA256 = new PasswordRecipient.PRF(
         "HMacSHA256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE)
      );
      public static final PasswordRecipient.PRF HMacSHA384 = new PasswordRecipient.PRF(
         "HMacSHA384", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, DERNull.INSTANCE)
      );
      public static final PasswordRecipient.PRF HMacSHA512 = new PasswordRecipient.PRF(
         "HMacSHA512", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE)
      );
      private final String hmac;
      final AlgorithmIdentifier prfAlgID;

      private PRF(String var1, AlgorithmIdentifier var2) {
         this.hmac = var1;
         this.prfAlgID = var2;
      }

      public String getName() {
         return this.hmac;
      }

      public AlgorithmIdentifier getAlgorithmID() {
         return this.prfAlgID;
      }
   }
}
