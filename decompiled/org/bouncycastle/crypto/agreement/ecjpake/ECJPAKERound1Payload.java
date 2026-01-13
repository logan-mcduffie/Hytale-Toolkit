package org.bouncycastle.crypto.agreement.ecjpake;

import org.bouncycastle.math.ec.ECPoint;

public class ECJPAKERound1Payload {
   private final String participantId;
   private final ECPoint gx1;
   private final ECPoint gx2;
   private final ECSchnorrZKP knowledgeProofForX1;
   private final ECSchnorrZKP knowledgeProofForX2;

   public ECJPAKERound1Payload(String var1, ECPoint var2, ECPoint var3, ECSchnorrZKP var4, ECSchnorrZKP var5) {
      ECJPAKEUtil.validateNotNull(var1, "participantId");
      ECJPAKEUtil.validateNotNull(var2, "gx1");
      ECJPAKEUtil.validateNotNull(var3, "gx2");
      ECJPAKEUtil.validateNotNull(var4, "knowledgeProofForX1");
      ECJPAKEUtil.validateNotNull(var5, "knowledgeProofForX2");
      this.participantId = var1;
      this.gx1 = var2;
      this.gx2 = var3;
      this.knowledgeProofForX1 = var4;
      this.knowledgeProofForX2 = var5;
   }

   public String getParticipantId() {
      return this.participantId;
   }

   public ECPoint getGx1() {
      return this.gx1;
   }

   public ECPoint getGx2() {
      return this.gx2;
   }

   public ECSchnorrZKP getKnowledgeProofForX1() {
      return this.knowledgeProofForX1;
   }

   public ECSchnorrZKP getKnowledgeProofForX2() {
      return this.knowledgeProofForX2;
   }
}
