package org.bouncycastle.its.bc;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSImplicitCertificateBuilder;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;

public class BcITSImplicitCertificateBuilder extends ITSImplicitCertificateBuilder {
   public BcITSImplicitCertificateBuilder(ITSCertificate var1, ToBeSignedCertificate.Builder var2) {
      super(var1, new BcDigestCalculatorProvider(), var2);
   }
}
