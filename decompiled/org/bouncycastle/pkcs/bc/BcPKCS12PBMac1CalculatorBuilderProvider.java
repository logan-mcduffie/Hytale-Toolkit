package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

public class BcPKCS12PBMac1CalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider {
   @Override
   public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier var1) {
      return new PKCS12MacCalculatorBuilder() {
         @Override
         public MacCalculator build(char[] var1x) throws OperatorCreationException {
            if (!PKCSObjectIdentifiers.id_PBMAC1.equals(var1.getAlgorithm())) {
               throw new OperatorCreationException("protection algorithm not PB mac based");
            } else {
               BcPKCS12PBMac1CalculatorBuilder var2;
               try {
                  var2 = new BcPKCS12PBMac1CalculatorBuilder(PBMAC1Params.getInstance(var1.getParameters()));
               } catch (IOException var4) {
                  throw new OperatorCreationException("invalid parameters in protection algorithm: " + var4.getMessage());
               }

               return var2.build(var1x);
            }
         }

         @Override
         public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
            return new AlgorithmIdentifier(var1.getAlgorithm(), var1.getParameters());
         }
      };
   }
}
