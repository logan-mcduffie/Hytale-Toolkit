package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class JPAKERound1Payload {
   private final String participantId;
   private final BigInteger gx1;
   private final BigInteger gx2;
   private final BigInteger[] knowledgeProofForX1;
   private final BigInteger[] knowledgeProofForX2;

   public JPAKERound1Payload(String var1, BigInteger var2, BigInteger var3, BigInteger[] var4, BigInteger[] var5) {
      JPAKEUtil.validateNotNull(var1, "participantId");
      JPAKEUtil.validateNotNull(var2, "gx1");
      JPAKEUtil.validateNotNull(var3, "gx2");
      JPAKEUtil.validateNotNull(var4, "knowledgeProofForX1");
      JPAKEUtil.validateNotNull(var5, "knowledgeProofForX2");
      this.participantId = var1;
      this.gx1 = var2;
      this.gx2 = var3;
      this.knowledgeProofForX1 = Arrays.copyOf(var4, var4.length);
      this.knowledgeProofForX2 = Arrays.copyOf(var5, var5.length);
   }

   public String getParticipantId() {
      return this.participantId;
   }

   public BigInteger getGx1() {
      return this.gx1;
   }

   public BigInteger getGx2() {
      return this.gx2;
   }

   public BigInteger[] getKnowledgeProofForX1() {
      return Arrays.copyOf(this.knowledgeProofForX1, this.knowledgeProofForX1.length);
   }

   public BigInteger[] getKnowledgeProofForX2() {
      return Arrays.copyOf(this.knowledgeProofForX2, this.knowledgeProofForX2.length);
   }
}
