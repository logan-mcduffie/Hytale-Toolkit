package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class JcePKCS12MacCalculatorBuilder implements PKCS12MacCalculatorBuilder {
   private JcaJceHelper helper = new DefaultJcaJceHelper();
   private ASN1ObjectIdentifier algorithm;
   private SecureRandom random;
   private int saltLength;
   private int iterationCount = 1024;

   public JcePKCS12MacCalculatorBuilder() {
      this(OIWObjectIdentifiers.idSHA1);
   }

   public JcePKCS12MacCalculatorBuilder(ASN1ObjectIdentifier var1) {
      this.algorithm = var1;
   }

   public JcePKCS12MacCalculatorBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcePKCS12MacCalculatorBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public JcePKCS12MacCalculatorBuilder setIterationCount(int var1) {
      this.iterationCount = var1;
      return this;
   }

   @Override
   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return new AlgorithmIdentifier(this.algorithm, DERNull.INSTANCE);
   }

   @Override
   public MacCalculator build(char[] var1) throws OperatorCreationException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      try {
         final Mac var2 = this.helper.createMac(this.algorithm.getId());
         this.saltLength = var2.getMacLength();
         final byte[] var3 = new byte[this.saltLength];
         this.random.nextBytes(var3);
         PBEParameterSpec var4 = new PBEParameterSpec(var3, this.iterationCount);
         final PKCS12Key var5 = new PKCS12Key(var1);
         var2.init(var5, var4);
         return new MacCalculator() {
            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
               return new AlgorithmIdentifier(
                  JcePKCS12MacCalculatorBuilder.this.algorithm, new PKCS12PBEParams(var3, JcePKCS12MacCalculatorBuilder.this.iterationCount)
               );
            }

            @Override
            public OutputStream getOutputStream() {
               return new MacOutputStream(var2);
            }

            @Override
            public byte[] getMac() {
               return var2.doFinal();
            }

            @Override
            public GenericKey getKey() {
               return new GenericKey(this.getAlgorithmIdentifier(), var5.getEncoded());
            }
         };
      } catch (Exception var6) {
         throw new OperatorCreationException("unable to create MAC calculator: " + var6.getMessage(), var6);
      }
   }
}
