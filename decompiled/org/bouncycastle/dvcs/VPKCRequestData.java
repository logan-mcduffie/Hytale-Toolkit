package org.bouncycastle.dvcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;

public class VPKCRequestData extends DVCSRequestData {
   private List chains;

   VPKCRequestData(Data var1) throws DVCSConstructionException {
      super(var1);
      TargetEtcChain[] var2 = var1.getCerts();
      if (var2 == null) {
         throw new DVCSConstructionException("DVCSRequest.data.certs should be specified for VPKC service");
      } else {
         this.chains = new ArrayList(var2.length);

         for (int var3 = 0; var3 != var2.length; var3++) {
            this.chains.add(new TargetChain(var2[var3]));
         }
      }
   }

   public List getCerts() {
      return Collections.unmodifiableList(this.chains);
   }
}
