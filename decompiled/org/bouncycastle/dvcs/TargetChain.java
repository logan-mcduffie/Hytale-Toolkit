package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.TargetEtcChain;

public class TargetChain {
   private final TargetEtcChain certs;

   public TargetChain(TargetEtcChain var1) {
      this.certs = var1;
   }

   public TargetEtcChain toASN1Structure() {
      return this.certs;
   }
}
