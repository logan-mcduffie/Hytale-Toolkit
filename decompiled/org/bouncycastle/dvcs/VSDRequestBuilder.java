package org.bouncycastle.dvcs;

import java.io.IOException;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.cms.CMSSignedData;

public class VSDRequestBuilder extends DVCSRequestBuilder {
   public VSDRequestBuilder() {
      super(new DVCSRequestInformationBuilder(ServiceType.VSD));
   }

   public void setRequestTime(Date var1) {
      this.requestInformationBuilder.setRequestTime(new DVCSTime(var1));
   }

   public DVCSRequest build(CMSSignedData var1) throws DVCSException {
      try {
         Data var2 = new Data(var1.getEncoded());
         return this.createDVCRequest(var2);
      } catch (IOException var3) {
         throw new DVCSException("Failed to encode CMS signed data", var3);
      }
   }
}
