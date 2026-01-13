package org.bouncycastle.its.jcajce;

import java.security.Provider;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSImplicitCertificateBuilder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class JcaITSImplicitCertificateBuilderBuilder {
   private final JcaDigestCalculatorProviderBuilder digestCalculatorProviderBuilder = new JcaDigestCalculatorProviderBuilder();

   public JcaITSImplicitCertificateBuilderBuilder setProvider(Provider var1) {
      this.digestCalculatorProviderBuilder.setProvider(var1);
      return this;
   }

   public JcaITSImplicitCertificateBuilderBuilder setProvider(String var1) {
      this.digestCalculatorProviderBuilder.setProvider(var1);
      return this;
   }

   public ITSImplicitCertificateBuilder build(ITSCertificate var1, ToBeSignedCertificate.Builder var2) throws OperatorCreationException {
      return new ITSImplicitCertificateBuilder(var1, this.digestCalculatorProviderBuilder.build(), var2);
   }
}
