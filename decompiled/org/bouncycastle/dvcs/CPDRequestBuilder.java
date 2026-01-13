package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;

public class CPDRequestBuilder extends DVCSRequestBuilder {
   public CPDRequestBuilder() {
      super(new DVCSRequestInformationBuilder(ServiceType.CPD));
   }

   public DVCSRequest build(byte[] var1) throws DVCSException {
      Data var2 = new Data(var1);
      return this.createDVCRequest(var2);
   }
}
