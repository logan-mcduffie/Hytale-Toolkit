package org.bouncycastle.dvcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.dvcs.CertEtcToken;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;

public class VPKCRequestBuilder extends DVCSRequestBuilder {
   private List chains = new ArrayList();

   public VPKCRequestBuilder() {
      super(new DVCSRequestInformationBuilder(ServiceType.VPKC));
   }

   public void addTargetChain(X509CertificateHolder var1) {
      this.chains.add(new TargetEtcChain(new CertEtcToken(0, var1.toASN1Structure())));
   }

   public void addTargetChain(Extension var1) {
      this.chains.add(new TargetEtcChain(new CertEtcToken(var1)));
   }

   public void addTargetChain(TargetChain var1) {
      this.chains.add(var1.toASN1Structure());
   }

   public void setRequestTime(Date var1) {
      this.requestInformationBuilder.setRequestTime(new DVCSTime(var1));
   }

   public DVCSRequest build() throws DVCSException {
      Data var1 = new Data(this.chains.toArray(new TargetEtcChain[this.chains.size()]));
      return this.createDVCRequest(var1);
   }
}
