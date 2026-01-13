package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public abstract class BcPasswordRecipient implements PasswordRecipient {
   private final char[] password;
   private int schemeID = 1;

   BcPasswordRecipient(char[] var1) {
      this.password = var1;
   }

   public BcPasswordRecipient setPasswordConversionScheme(int var1) {
      this.schemeID = var1;
      return this;
   }

   protected KeyParameter extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3, byte[] var4) throws CMSException {
      Wrapper var5 = EnvelopedDataHelper.createRFC3211Wrapper(var1.getAlgorithm());
      var5.init(false, new ParametersWithIV(new KeyParameter(var3), ASN1OctetString.getInstance(var1.getParameters()).getOctets()));

      try {
         return new KeyParameter(var5.unwrap(var4, 0, var4.length));
      } catch (InvalidCipherTextException var7) {
         throw new CMSException("unable to unwrap key: " + var7.getMessage(), var7);
      }
   }

   @Override
   public byte[] calculateDerivedKey(int var1, AlgorithmIdentifier var2, int var3) throws CMSException {
      PBKDF2Params var4 = PBKDF2Params.getInstance(var2.getParameters());
      byte[] var5 = var1 == 0 ? PBEParametersGenerator.PKCS5PasswordToBytes(this.password) : PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password);

      try {
         PKCS5S2ParametersGenerator var6 = new PKCS5S2ParametersGenerator(EnvelopedDataHelper.getPRF(var4.getPrf()));
         var6.init(var5, var4.getSalt(), var4.getIterationCount().intValue());
         return ((KeyParameter)var6.generateDerivedParameters(var3)).getKey();
      } catch (Exception var7) {
         throw new CMSException("exception creating derived key: " + var7.getMessage(), var7);
      }
   }

   @Override
   public int getPasswordConversionScheme() {
      return this.schemeID;
   }

   @Override
   public char[] getPassword() {
      return this.password;
   }
}
