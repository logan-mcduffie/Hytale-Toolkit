package org.bouncycastle.pkcs.bc;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

public class BcPKCS12MacCalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider {
   private BcDigestProvider digestProvider;

   public BcPKCS12MacCalculatorBuilderProvider(BcDigestProvider var1) {
      this.digestProvider = var1;
   }

   @Override
   public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier var1) {
      return new PKCS12MacCalculatorBuilder() {
         @Override
         public MacCalculator build(char[] var1x) throws OperatorCreationException {
            PKCS12PBEParams var2 = PKCS12PBEParams.getInstance(var1.getParameters());
            return PKCS12PBEUtils.createMacCalculator(var1.getAlgorithm(), BcPKCS12MacCalculatorBuilderProvider.this.digestProvider.get(var1), var2, var1x);
         }

         @Override
         public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
            return new AlgorithmIdentifier(var1.getAlgorithm(), DERNull.INSTANCE);
         }
      };
   }
}
