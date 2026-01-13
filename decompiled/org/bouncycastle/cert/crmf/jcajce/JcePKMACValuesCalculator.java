package org.bouncycastle.cert.crmf.jcajce;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.PKMACValuesCalculator;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JcePKMACValuesCalculator implements PKMACValuesCalculator {
   private MessageDigest digest;
   private Mac mac;
   private CRMFHelper helper = new CRMFHelper(new DefaultJcaJceHelper());

   public JcePKMACValuesCalculator setProvider(Provider var1) {
      this.helper = new CRMFHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JcePKMACValuesCalculator setProvider(String var1) {
      this.helper = new CRMFHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   @Override
   public void setup(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws CRMFException {
      this.digest = this.helper.createDigest(var1.getAlgorithm());
      this.mac = this.helper.createMac(var2.getAlgorithm());
   }

   @Override
   public byte[] calculateDigest(byte[] var1) {
      return this.digest.digest(var1);
   }

   @Override
   public byte[] calculateMac(byte[] var1, byte[] var2) throws CRMFException {
      try {
         this.mac.init(new SecretKeySpec(var1, this.mac.getAlgorithm()));
         return this.mac.doFinal(var2);
      } catch (GeneralSecurityException var4) {
         throw new CRMFException("failure in setup: " + var4.getMessage(), var4);
      }
   }
}
