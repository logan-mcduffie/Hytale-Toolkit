package org.bouncycastle.its.bc;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

public class BcITSExplicitCertificateBuilder extends ITSExplicitCertificateBuilder {
   public BcITSExplicitCertificateBuilder(ITSContentSigner var1, ToBeSignedCertificate.Builder var2) {
      super(var1, var2);
   }

   public ITSCertificate build(CertificateId var1, ECPublicKeyParameters var2) {
      return this.build(var1, var2, null);
   }

   public ITSCertificate build(CertificateId var1, ECPublicKeyParameters var2, ECPublicKeyParameters var3) {
      BcITSPublicEncryptionKey var4 = null;
      if (var3 != null) {
         var4 = new BcITSPublicEncryptionKey(var3);
      }

      return super.build(var1, new BcITSPublicVerificationKey(var2), var4);
   }
}
