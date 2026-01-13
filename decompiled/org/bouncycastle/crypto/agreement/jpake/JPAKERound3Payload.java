package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;

public class JPAKERound3Payload {
   private final String participantId;
   private final BigInteger macTag;

   public JPAKERound3Payload(String var1, BigInteger var2) {
      this.participantId = var1;
      this.macTag = var2;
   }

   public String getParticipantId() {
      return this.participantId;
   }

   public BigInteger getMacTag() {
      return this.macTag;
   }
}
