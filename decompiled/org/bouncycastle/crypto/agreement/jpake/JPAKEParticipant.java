package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

public class JPAKEParticipant {
   public static final int STATE_INITIALIZED = 0;
   public static final int STATE_ROUND_1_CREATED = 10;
   public static final int STATE_ROUND_1_VALIDATED = 20;
   public static final int STATE_ROUND_2_CREATED = 30;
   public static final int STATE_ROUND_2_VALIDATED = 40;
   public static final int STATE_KEY_CALCULATED = 50;
   public static final int STATE_ROUND_3_CREATED = 60;
   public static final int STATE_ROUND_3_VALIDATED = 70;
   private final String participantId;
   private char[] password;
   private final Digest digest;
   private final SecureRandom random;
   private final BigInteger p;
   private final BigInteger q;
   private final BigInteger g;
   private String partnerParticipantId;
   private BigInteger x1;
   private BigInteger x2;
   private BigInteger gx1;
   private BigInteger gx2;
   private BigInteger gx3;
   private BigInteger gx4;
   private BigInteger b;
   private int state;

   public JPAKEParticipant(String var1, char[] var2) {
      this(var1, var2, JPAKEPrimeOrderGroups.NIST_3072);
   }

   public JPAKEParticipant(String var1, char[] var2, JPAKEPrimeOrderGroup var3) {
      this(var1, var2, var3, SHA256Digest.newInstance(), CryptoServicesRegistrar.getSecureRandom());
   }

   public JPAKEParticipant(String var1, char[] var2, JPAKEPrimeOrderGroup var3, Digest var4, SecureRandom var5) {
      JPAKEUtil.validateNotNull(var1, "participantId");
      JPAKEUtil.validateNotNull(var2, "password");
      JPAKEUtil.validateNotNull(var3, "p");
      JPAKEUtil.validateNotNull(var4, "digest");
      JPAKEUtil.validateNotNull(var5, "random");
      if (var2.length == 0) {
         throw new IllegalArgumentException("Password must not be empty.");
      } else {
         this.participantId = var1;
         this.password = Arrays.copyOf(var2, var2.length);
         this.p = var3.getP();
         this.q = var3.getQ();
         this.g = var3.getG();
         this.digest = var4;
         this.random = var5;
         this.state = 0;
      }
   }

   public int getState() {
      return this.state;
   }

   public JPAKERound1Payload createRound1PayloadToSend() {
      if (this.state >= 10) {
         throw new IllegalStateException("Round1 payload already created for " + this.participantId);
      } else {
         this.x1 = JPAKEUtil.generateX1(this.q, this.random);
         this.x2 = JPAKEUtil.generateX2(this.q, this.random);
         this.gx1 = JPAKEUtil.calculateGx(this.p, this.g, this.x1);
         this.gx2 = JPAKEUtil.calculateGx(this.p, this.g, this.x2);
         BigInteger[] var1 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx1, this.x1, this.participantId, this.digest, this.random);
         BigInteger[] var2 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx2, this.x2, this.participantId, this.digest, this.random);
         this.state = 10;
         return new JPAKERound1Payload(this.participantId, this.gx1, this.gx2, var1, var2);
      }
   }

   public void validateRound1PayloadReceived(JPAKERound1Payload var1) throws CryptoException {
      if (this.state >= 20) {
         throw new IllegalStateException("Validation already attempted for round1 payload for" + this.participantId);
      } else {
         this.partnerParticipantId = var1.getParticipantId();
         this.gx3 = var1.getGx1();
         this.gx4 = var1.getGx2();
         BigInteger[] var2 = var1.getKnowledgeProofForX1();
         BigInteger[] var3 = var1.getKnowledgeProofForX2();
         JPAKEUtil.validateParticipantIdsDiffer(this.participantId, var1.getParticipantId());
         JPAKEUtil.validateGx4(this.gx4);
         JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx3, var2, var1.getParticipantId(), this.digest);
         JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx4, var3, var1.getParticipantId(), this.digest);
         this.state = 20;
      }
   }

   public JPAKERound2Payload createRound2PayloadToSend() {
      if (this.state >= 30) {
         throw new IllegalStateException("Round2 payload already created for " + this.participantId);
      } else if (this.state < 20) {
         throw new IllegalStateException("Round1 payload must be validated prior to creating Round2 payload for " + this.participantId);
      } else {
         BigInteger var1 = JPAKEUtil.calculateGA(this.p, this.gx1, this.gx3, this.gx4);
         BigInteger var2 = this.calculateS();
         BigInteger var3 = JPAKEUtil.calculateX2s(this.q, this.x2, var2);
         BigInteger var4 = JPAKEUtil.calculateA(this.p, this.q, var1, var3);
         BigInteger[] var5 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, var1, var4, var3, this.participantId, this.digest, this.random);
         this.state = 30;
         return new JPAKERound2Payload(this.participantId, var4, var5);
      }
   }

   public void validateRound2PayloadReceived(JPAKERound2Payload var1) throws CryptoException {
      if (this.state >= 40) {
         throw new IllegalStateException("Validation already attempted for round2 payload for" + this.participantId);
      } else if (this.state < 20) {
         throw new IllegalStateException("Round1 payload must be validated prior to validating Round2 payload for " + this.participantId);
      } else {
         BigInteger var2 = JPAKEUtil.calculateGA(this.p, this.gx3, this.gx1, this.gx2);
         this.b = var1.getA();
         BigInteger[] var3 = var1.getKnowledgeProofForX2s();
         JPAKEUtil.validateParticipantIdsDiffer(this.participantId, var1.getParticipantId());
         JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, var1.getParticipantId());
         JPAKEUtil.validateGa(var2);
         JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, var2, this.b, var3, var1.getParticipantId(), this.digest);
         this.state = 40;
      }
   }

   public BigInteger calculateKeyingMaterial() {
      if (this.state >= 50) {
         throw new IllegalStateException("Key already calculated for " + this.participantId);
      } else if (this.state < 40) {
         throw new IllegalStateException("Round2 payload must be validated prior to creating key for " + this.participantId);
      } else {
         BigInteger var1 = this.calculateS();
         Arrays.fill(this.password, '\u0000');
         this.password = null;
         BigInteger var2 = JPAKEUtil.calculateKeyingMaterial(this.p, this.q, this.gx4, this.x2, var1, this.b);
         this.x1 = null;
         this.x2 = null;
         this.b = null;
         this.state = 50;
         return var2;
      }
   }

   public JPAKERound3Payload createRound3PayloadToSend(BigInteger var1) {
      if (this.state >= 60) {
         throw new IllegalStateException("Round3 payload already created for " + this.participantId);
      } else if (this.state < 50) {
         throw new IllegalStateException("Keying material must be calculated prior to creating Round3 payload for " + this.participantId);
      } else {
         BigInteger var2 = JPAKEUtil.calculateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, var1, this.digest);
         this.state = 60;
         return new JPAKERound3Payload(this.participantId, var2);
      }
   }

   public void validateRound3PayloadReceived(JPAKERound3Payload var1, BigInteger var2) throws CryptoException {
      if (this.state >= 70) {
         throw new IllegalStateException("Validation already attempted for round3 payload for" + this.participantId);
      } else if (this.state < 50) {
         throw new IllegalStateException("Keying material must be calculated validated prior to validating Round3 payload for " + this.participantId);
      } else {
         JPAKEUtil.validateParticipantIdsDiffer(this.participantId, var1.getParticipantId());
         JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, var1.getParticipantId());
         JPAKEUtil.validateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, var2, this.digest, var1.getMacTag());
         this.gx1 = null;
         this.gx2 = null;
         this.gx3 = null;
         this.gx4 = null;
         this.state = 70;
      }
   }

   private BigInteger calculateS() {
      try {
         return JPAKEUtil.calculateS(this.q, this.password);
      } catch (CryptoException var2) {
         throw Exceptions.illegalStateException(var2.getMessage(), var2);
      }
   }
}
