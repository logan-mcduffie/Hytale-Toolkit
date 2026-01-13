package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.Strings;

public class PKMACBuilder implements PBEMacCalculatorProvider {
   private AlgorithmIdentifier owf;
   private int iterationCount;
   private AlgorithmIdentifier mac;
   private int saltLength = 20;
   private SecureRandom random;
   private PKMACValuesCalculator calculator;
   private PBMParameter parameters;
   private int maxIterations;

   public PKMACBuilder(PKMACValuesCalculator var1) {
      this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, DERNull.INSTANCE), var1);
   }

   public PKMACBuilder(PKMACValuesCalculator var1, int var2) {
      this.maxIterations = var2;
      this.calculator = var1;
   }

   private PKMACBuilder(AlgorithmIdentifier var1, int var2, AlgorithmIdentifier var3, PKMACValuesCalculator var4) {
      this.owf = var1;
      this.iterationCount = var2;
      this.mac = var3;
      this.calculator = var4;
   }

   public PKMACBuilder setSaltLength(int var1) {
      if (var1 < 8) {
         throw new IllegalArgumentException("salt length must be at least 8 bytes");
      } else {
         this.saltLength = var1;
         return this;
      }
   }

   public PKMACBuilder setIterationCount(int var1) {
      if (var1 < 100) {
         throw new IllegalArgumentException("iteration count must be at least 100");
      } else {
         this.checkIterationCountCeiling(var1);
         this.iterationCount = var1;
         return this;
      }
   }

   public PKMACBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public PKMACBuilder setParameters(PBMParameter var1) {
      this.checkIterationCountCeiling(var1.getIterationCount().intValueExact());
      this.parameters = var1;
      return this;
   }

   @Override
   public MacCalculator get(AlgorithmIdentifier var1, char[] var2) throws OperatorCreationException {
      if (!CMPObjectIdentifiers.passwordBasedMac.equals(var1.getAlgorithm())) {
         throw new OperatorCreationException("protection algorithm not mac based");
      } else {
         this.setParameters(PBMParameter.getInstance(var1.getParameters()));

         try {
            return this.build(var2);
         } catch (CRMFException var4) {
            throw new OperatorCreationException(var4.getMessage(), var4.getCause());
         }
      }
   }

   public MacCalculator build(char[] var1) throws CRMFException {
      PBMParameter var2 = this.parameters;
      if (var2 == null) {
         var2 = this.genParameters();
      }

      return this.genCalculator(var2, var1);
   }

   private void checkIterationCountCeiling(int var1) {
      if (this.maxIterations > 0 && var1 > this.maxIterations) {
         throw new IllegalArgumentException("iteration count exceeds limit (" + var1 + " > " + this.maxIterations + ")");
      }
   }

   private MacCalculator genCalculator(final PBMParameter var1, char[] var2) throws CRMFException {
      byte[] var3 = Strings.toUTF8ByteArray(var2);
      byte[] var4 = var1.getSalt().getOctets();
      final byte[] var5 = new byte[var3.length + var4.length];
      System.arraycopy(var3, 0, var5, 0, var3.length);
      System.arraycopy(var4, 0, var5, var3.length, var4.length);
      this.calculator.setup(var1.getOwf(), var1.getMac());
      int var6 = var1.getIterationCount().intValueExact();

      do {
         var5 = this.calculator.calculateDigest(var5);
      } while (--var6 > 0);

      return new MacCalculator() {
         ByteArrayOutputStream bOut = new ByteArrayOutputStream();

         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, var1);
         }

         @Override
         public GenericKey getKey() {
            return new GenericKey(this.getAlgorithmIdentifier(), var5);
         }

         @Override
         public OutputStream getOutputStream() {
            return this.bOut;
         }

         @Override
         public byte[] getMac() {
            try {
               return PKMACBuilder.this.calculator.calculateMac(var5, this.bOut.toByteArray());
            } catch (CRMFException var2x) {
               throw new RuntimeOperatorException("exception calculating mac: " + var2x.getMessage(), var2x);
            }
         }
      };
   }

   private PBMParameter genParameters() {
      byte[] var1 = new byte[this.saltLength];
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      this.random.nextBytes(var1);
      return new PBMParameter(var1, this.owf, this.iterationCount, this.mac);
   }
}
