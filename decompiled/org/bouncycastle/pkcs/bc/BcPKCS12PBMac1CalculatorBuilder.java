package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class BcPKCS12PBMac1CalculatorBuilder implements PKCS12MacCalculatorBuilder {
   private final PBMAC1Params pbmac1Params;
   private PBKDF2Params pbkdf2Params = null;

   public BcPKCS12PBMac1CalculatorBuilder(PBMAC1Params var1) throws IOException {
      this.pbmac1Params = var1;
      if (PKCSObjectIdentifiers.id_PBKDF2.equals(var1.getKeyDerivationFunc().getAlgorithm())) {
         this.pbkdf2Params = PBKDF2Params.getInstance(var1.getKeyDerivationFunc().getParameters());
         if (this.pbkdf2Params.getKeyLength() == null) {
            throw new IOException("Key length must be present when using PBMAC1.");
         }
      } else {
         throw new IllegalArgumentException("unrecognised PBKDF");
      }
   }

   @Override
   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, this.pbmac1Params);
   }

   @Override
   public MacCalculator build(char[] var1) throws OperatorCreationException {
      return PKCS12PBEUtils.createPBMac1Calculator(this.pbmac1Params, this.pbkdf2Params, var1);
   }
}
