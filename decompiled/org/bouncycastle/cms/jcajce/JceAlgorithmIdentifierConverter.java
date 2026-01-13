package org.bouncycastle.cms.jcajce;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;

public class JceAlgorithmIdentifierConverter {
   private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   private SecureRandom random;

   public JceAlgorithmIdentifierConverter setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JceAlgorithmIdentifierConverter setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   public AlgorithmParameters getAlgorithmParameters(AlgorithmIdentifier var1) throws CMSException {
      ASN1Encodable var2 = var1.getParameters();
      if (var2 == null) {
         return null;
      } else {
         try {
            AlgorithmParameters var3 = this.helper.createAlgorithmParameters(var1.getAlgorithm());
            CMSUtils.loadParameters(var3, var1.getParameters());
            return var3;
         } catch (NoSuchAlgorithmException var4) {
            throw new CMSException("can't find parameters for algorithm", var4);
         } catch (NoSuchProviderException var5) {
            throw new CMSException("can't find provider for algorithm", var5);
         }
      }
   }
}
