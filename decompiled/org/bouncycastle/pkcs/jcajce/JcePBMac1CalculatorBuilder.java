package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultMacAlgorithmIdentifierFinder;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacAlgorithmIdentifierFinder;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.BigIntegers;

public class JcePBMac1CalculatorBuilder {
   public static final AlgorithmIdentifier PRF_SHA224 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, DERNull.INSTANCE);
   public static final AlgorithmIdentifier PRF_SHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
   public static final AlgorithmIdentifier PRF_SHA384 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, DERNull.INSTANCE);
   public static final AlgorithmIdentifier PRF_SHA512 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
   public static final AlgorithmIdentifier PRF_SHA3_224 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224);
   public static final AlgorithmIdentifier PRF_SHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256);
   public static final AlgorithmIdentifier PRF_SHA3_384 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384);
   public static final AlgorithmIdentifier PRF_SHA3_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512);
   private static final DefaultMacAlgorithmIdentifierFinder defaultFinder = new DefaultMacAlgorithmIdentifierFinder();
   private JcaJceHelper helper = new DefaultJcaJceHelper();
   private AlgorithmIdentifier macAlgorithm;
   private SecureRandom random;
   private int saltLength = -1;
   private int iterationCount = 8192;
   private int keySize;
   private PBKDF2Params pbeParams = null;
   private AlgorithmIdentifier prf = PRF_SHA256;
   private byte[] salt = null;

   public JcePBMac1CalculatorBuilder(String var1, int var2) {
      this(var1, var2, defaultFinder);
   }

   public JcePBMac1CalculatorBuilder(String var1, int var2, MacAlgorithmIdentifierFinder var3) {
      this.macAlgorithm = var3.find(var1);
      this.keySize = var2;
   }

   public JcePBMac1CalculatorBuilder(PBMAC1Params var1) {
      this.macAlgorithm = var1.getMessageAuthScheme();
      this.pbeParams = PBKDF2Params.getInstance(var1.getKeyDerivationFunc().getParameters());
   }

   public JcePBMac1CalculatorBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcePBMac1CalculatorBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   JcePBMac1CalculatorBuilder setHelper(JcaJceHelper var1) {
      this.helper = var1;
      return this;
   }

   public JcePBMac1CalculatorBuilder setIterationCount(int var1) {
      this.iterationCount = var1;
      return this;
   }

   public JcePBMac1CalculatorBuilder setSaltLength(int var1) {
      this.saltLength = var1;
      return this;
   }

   public JcePBMac1CalculatorBuilder setSalt(byte[] var1) {
      this.salt = var1;
      return this;
   }

   public JcePBMac1CalculatorBuilder setRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JcePBMac1CalculatorBuilder setPrf(AlgorithmIdentifier var1) {
      this.prf = var1;
      return this;
   }

   public MacCalculator build(char[] var1) throws OperatorCreationException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      try {
         final Mac var2 = this.helper.createMac(this.macAlgorithm.getAlgorithm().getId());
         if (this.pbeParams == null) {
            if (this.salt == null) {
               if (this.saltLength < 0) {
                  this.saltLength = var2.getMacLength();
               }

               this.salt = new byte[this.saltLength];
               this.random.nextBytes(this.salt);
            }
         } else {
            this.salt = this.pbeParams.getSalt();
            this.iterationCount = BigIntegers.intValueExact(this.pbeParams.getIterationCount());
            this.keySize = BigIntegers.intValueExact(this.pbeParams.getKeyLength()) * 8;
            this.prf = this.pbeParams.getPrf();
         }

         SecretKeyFactory var3 = this.helper.createSecretKeyFactory("PBKDF2");
         final SecretKey var4 = var3.generateSecret(new PBKDF2KeySpec(var1, this.salt, this.iterationCount, this.keySize, this.prf));
         var2.init(var4);
         return new MacCalculator() {
            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
               return new AlgorithmIdentifier(
                  PKCSObjectIdentifiers.id_PBMAC1,
                  new PBMAC1Params(
                     new AlgorithmIdentifier(
                        PKCSObjectIdentifiers.id_PBES2,
                        new PBKDF2Params(
                           JcePBMac1CalculatorBuilder.this.salt,
                           JcePBMac1CalculatorBuilder.this.iterationCount,
                           (JcePBMac1CalculatorBuilder.this.keySize + 7) / 8,
                           JcePBMac1CalculatorBuilder.this.prf
                        )
                     ),
                     JcePBMac1CalculatorBuilder.this.macAlgorithm
                  )
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
               return new GenericKey(this.getAlgorithmIdentifier(), var4.getEncoded());
            }
         };
      } catch (Exception var5) {
         throw new OperatorCreationException("unable to create MAC calculator: " + var5.getMessage(), var5);
      }
   }
}
