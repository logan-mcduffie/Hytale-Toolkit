package org.bouncycastle.crypto.agreement.ecjpake;

import org.bouncycastle.math.ec.ECPoint;

public class ECJPAKERound2Payload {
   private final String participantId;
   private final ECPoint a;
   private final ECSchnorrZKP knowledgeProofForX2s;

   public ECJPAKERound2Payload(String var1, ECPoint var2, ECSchnorrZKP var3) {
      ECJPAKEUtil.validateNotNull(var1, "participantId");
      ECJPAKEUtil.validateNotNull(var2, "a");
      ECJPAKEUtil.validateNotNull(var3, "knowledgeProofForX2s");
      this.participantId = var1;
      this.a = var2;
      this.knowledgeProofForX2s = var3;
   }

   public String getParticipantId() {
      return this.participantId;
   }

   public ECPoint getA() {
      return this.a;
   }

   public ECSchnorrZKP getKnowledgeProofForX2s() {
      return this.knowledgeProofForX2s;
   }
}
