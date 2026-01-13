package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEMRecipient;
import org.bouncycastle.operator.OperatorException;

public abstract class JceKEMRecipient implements KEMRecipient {
   private PrivateKey recipientKey;
   protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   protected EnvelopedDataHelper contentHelper = this.helper;
   protected Map extraMappings = new HashMap();
   protected boolean validateKeySize = false;
   protected boolean unwrappedKeyMustBeEncodable;

   public JceKEMRecipient(PrivateKey var1) {
      this.recipientKey = CMSUtils.cleanPrivateKey(var1);
   }

   public JceKEMRecipient setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKEMRecipient setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKEMRecipient setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.extraMappings.put(var1, var2);
      return this;
   }

   public JceKEMRecipient setContentProvider(Provider var1) {
      this.contentHelper = CMSUtils.createContentHelper(var1);
      return this;
   }

   public JceKEMRecipient setMustProduceEncodableUnwrappedKey(boolean var1) {
      this.unwrappedKeyMustBeEncodable = var1;
      return this;
   }

   public JceKEMRecipient setContentProvider(String var1) {
      this.contentHelper = CMSUtils.createContentHelper(var1);
      return this;
   }

   public JceKEMRecipient setKeySizeValidation(boolean var1) {
      this.validateKeySize = var1;
      return this;
   }

   protected Key extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      KEMRecipientInfo var4 = KEMRecipientInfo.getInstance(var1.getParameters());
      JceCMSKEMKeyUnwrapper var5 = (JceCMSKEMKeyUnwrapper)this.helper.createKEMUnwrapper(var1, this.recipientKey);
      if (!this.extraMappings.isEmpty()) {
         for (ASN1ObjectIdentifier var7 : this.extraMappings.keySet()) {
            var5.setAlgorithmMapping(var7, (String)this.extraMappings.get(var7));
         }
      }

      try {
         Key var9 = this.helper.getJceKey(var2, var5.generateUnwrappedKey(var2, var3));
         if (this.validateKeySize) {
            this.helper.keySizeCheck(var2, var9);
         }

         return var9;
      } catch (OperatorException var8) {
         throw new CMSException("exception unwrapping key: " + var8.getMessage(), var8);
      }
   }
}
