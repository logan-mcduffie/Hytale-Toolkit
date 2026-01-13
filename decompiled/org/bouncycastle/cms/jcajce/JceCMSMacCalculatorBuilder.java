package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceCMSMacCalculatorBuilder {
   private final ASN1ObjectIdentifier macOID;
   private final int keySize;
   private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   private AlgorithmParameters algorithmParameters;
   private SecureRandom random;

   public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier var1) {
      this(var1, -1);
   }

   public JceCMSMacCalculatorBuilder(ASN1ObjectIdentifier var1, int var2) {
      this.macOID = var1;
      this.keySize = var2;
   }

   public JceCMSMacCalculatorBuilder setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JceCMSMacCalculatorBuilder setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   public JceCMSMacCalculatorBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JceCMSMacCalculatorBuilder setAlgorithmParameters(AlgorithmParameters var1) {
      this.algorithmParameters = var1;
      return this;
   }

   public MacCalculator build() throws CMSException {
      return new JceCMSMacCalculatorBuilder.CMSMacCalculator(this.macOID, this.keySize, this.algorithmParameters, this.random);
   }

   private class CMSMacCalculator implements MacCalculator {
      private SecretKey encKey;
      private AlgorithmIdentifier algorithmIdentifier;
      private Mac mac;

      CMSMacCalculator(ASN1ObjectIdentifier nullx, int nullxx, AlgorithmParameters nullxxx, SecureRandom nullxxxx) throws CMSException {
         KeyGenerator var6 = JceCMSMacCalculatorBuilder.this.helper.createKeyGenerator(nullx);
         if (nullxxxx == null) {
            nullxxxx = new SecureRandom();
         }

         if (nullxx < 0) {
            var6.init(nullxxxx);
         } else {
            var6.init(nullxx, nullxxxx);
         }

         this.encKey = var6.generateKey();
         if (nullxxx == null) {
            nullxxx = JceCMSMacCalculatorBuilder.this.helper.generateParameters(nullx, this.encKey, nullxxxx);
         }

         this.algorithmIdentifier = JceCMSMacCalculatorBuilder.this.helper.getAlgorithmIdentifier(nullx, nullxxx);
         this.mac = JceCMSMacCalculatorBuilder.this.helper.createContentMac(this.encKey, this.algorithmIdentifier);
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithmIdentifier;
      }

      @Override
      public OutputStream getOutputStream() {
         return new MacOutputStream(this.mac);
      }

      @Override
      public byte[] getMac() {
         return this.mac.doFinal();
      }

      @Override
      public GenericKey getKey() {
         return new JceGenericKey(this.algorithmIdentifier, this.encKey);
      }
   }
}
