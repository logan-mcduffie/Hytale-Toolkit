package org.bouncycastle.pkcs.jcajce;

import java.security.Provider;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.PBEMacCalculatorProvider;

public class JcePBMac1CalculatorProviderBuilder {
   private JcaJceHelper helper = new DefaultJcaJceHelper();

   public JcePBMac1CalculatorProviderBuilder setProvider(Provider var1) {
      this.helper = new ProviderJcaJceHelper(var1);
      return this;
   }

   public JcePBMac1CalculatorProviderBuilder setProvider(String var1) {
      this.helper = new NamedJcaJceHelper(var1);
      return this;
   }

   public PBEMacCalculatorProvider build() {
      return new PBEMacCalculatorProvider() {
         @Override
         public MacCalculator get(AlgorithmIdentifier var1, char[] var2) throws OperatorCreationException {
            if (!PKCSObjectIdentifiers.id_PBMAC1.equals(var1.getAlgorithm())) {
               throw new OperatorCreationException("protection algorithm not PB mac based");
            } else {
               JcePBMac1CalculatorBuilder var3 = new JcePBMac1CalculatorBuilder(PBMAC1Params.getInstance(var1.getParameters()))
                  .setHelper(JcePBMac1CalculatorProviderBuilder.this.helper);
               return var3.build(var2);
            }
         }
      };
   }
}
