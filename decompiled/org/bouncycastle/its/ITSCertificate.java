package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.util.Encodable;

public class ITSCertificate implements Encodable {
   private final CertificateBase certificate;

   public ITSCertificate(CertificateBase var1) {
      this.certificate = var1;
   }

   public IssuerIdentifier getIssuer() {
      return this.certificate.getIssuer();
   }

   public ITSValidityPeriod getValidityPeriod() {
      return new ITSValidityPeriod(this.certificate.getToBeSigned().getValidityPeriod());
   }

   public ITSPublicEncryptionKey getPublicEncryptionKey() {
      PublicEncryptionKey var1 = this.certificate.getToBeSigned().getEncryptionKey();
      return var1 != null ? new ITSPublicEncryptionKey(var1) : null;
   }

   public boolean isSignatureValid(ITSContentVerifierProvider var1) throws Exception {
      ContentVerifier var2 = var1.get(this.certificate.getSignature().getChoice());
      OutputStream var3 = var2.getOutputStream();
      var3.write(OEREncoder.toByteArray(this.certificate.getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build()));
      var3.close();
      Signature var4 = this.certificate.getSignature();
      return var2.verify(ECDSAEncoder.toX962(var4));
   }

   public CertificateBase toASN1Structure() {
      return this.certificate;
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return OEREncoder.toByteArray(this.certificate, IEEE1609dot2.CertificateBase.build());
   }
}
