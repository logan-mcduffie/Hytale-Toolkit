package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

public class ECJPAKEParticipant {
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
   private String partnerParticipantId;
   private ECCurve.AbstractFp ecCurve;
   private BigInteger q;
   private BigInteger h;
   private BigInteger n;
   private ECPoint g;
   private BigInteger x1;
   private BigInteger x2;
   private ECPoint gx1;
   private ECPoint gx2;
   private ECPoint gx3;
   private ECPoint gx4;
   private ECPoint b;
   private int state;

   public ECJPAKEParticipant(String var1, char[] var2) {
      this(var1, var2, ECJPAKECurves.NIST_P256);
   }

   public ECJPAKEParticipant(String var1, char[] var2, ECJPAKECurve var3) {
      this(var1, var2, var3, SHA256Digest.newInstance(), CryptoServicesRegistrar.getSecureRandom());
   }

   public ECJPAKEParticipant(String var1, char[] var2, ECJPAKECurve var3, Digest var4, SecureRandom var5) {
      ECJPAKEUtil.validateNotNull(var1, "participantId");
      ECJPAKEUtil.validateNotNull(var2, "password");
      ECJPAKEUtil.validateNotNull(var3, "curve params");
      ECJPAKEUtil.validateNotNull(var4, "digest");
      ECJPAKEUtil.validateNotNull(var5, "random");
      if (var2.length == 0) {
         throw new IllegalArgumentException("Password must not be empty.");
      } else {
         this.participantId = var1;
         this.password = Arrays.copyOf(var2, var2.length);
         this.ecCurve = var3.getCurve();
         this.g = var3.getG();
         this.h = var3.getH();
         this.n = var3.getN();
         this.q = var3.getQ();
         this.digest = var4;
         this.random = var5;
         this.state = 0;
      }
   }

   public int getState() {
      return this.state;
   }

   public ECJPAKERound1Payload createRound1PayloadToSend() {
      if (this.state >= 10) {
         throw new IllegalStateException("Round1 payload already created for " + this.participantId);
      } else {
         this.x1 = ECJPAKEUtil.generateX1(this.n, this.random);
         this.x2 = ECJPAKEUtil.generateX1(this.n, this.random);
         this.gx1 = ECJPAKEUtil.calculateGx(this.g, this.x1);
         this.gx2 = ECJPAKEUtil.calculateGx(this.g, this.x2);
         ECSchnorrZKP var1 = ECJPAKEUtil.calculateZeroKnowledgeProof(this.g, this.n, this.x1, this.gx1, this.digest, this.participantId, this.random);
         ECSchnorrZKP var2 = ECJPAKEUtil.calculateZeroKnowledgeProof(this.g, this.n, this.x2, this.gx2, this.digest, this.participantId, this.random);
         this.state = 10;
         return new ECJPAKERound1Payload(this.participantId, this.gx1, this.gx2, var1, var2);
      }
   }

   public void validateRound1PayloadReceived(ECJPAKERound1Payload var1) throws CryptoException {
      if (this.state >= 20) {
         throw new IllegalStateException("Validation already attempted for round1 payload for" + this.participantId);
      } else {
         this.partnerParticipantId = var1.getParticipantId();
         this.gx3 = var1.getGx1();
         this.gx4 = var1.getGx2();
         ECSchnorrZKP var2 = var1.getKnowledgeProofForX1();
         ECSchnorrZKP var3 = var1.getKnowledgeProofForX2();
         ECJPAKEUtil.validateParticipantIdsDiffer(this.participantId, var1.getParticipantId());
         ECJPAKEUtil.validateZeroKnowledgeProof(this.g, this.gx3, var2, this.q, this.n, this.ecCurve, this.h, var1.getParticipantId(), this.digest);
         ECJPAKEUtil.validateZeroKnowledgeProof(this.g, this.gx4, var3, this.q, this.n, this.ecCurve, this.h, var1.getParticipantId(), this.digest);
         this.state = 20;
      }
   }

   public ECJPAKERound2Payload createRound2PayloadToSend() {
      if (this.state >= 30) {
         throw new IllegalStateException("Round2 payload already created for " + this.participantId);
      } else if (this.state < 20) {
         throw new IllegalStateException("Round1 payload must be validated prior to creating Round2 payload for " + this.participantId);
      } else {
         ECPoint var1 = ECJPAKEUtil.calculateGA(this.gx1, this.gx3, this.gx4);
         BigInteger var2 = this.calculateS();
         BigInteger var3 = ECJPAKEUtil.calculateX2s(this.n, this.x2, var2);
         ECPoint var4 = ECJPAKEUtil.calculateA(var1, var3);
         ECSchnorrZKP var5 = ECJPAKEUtil.calculateZeroKnowledgeProof(var1, this.n, var3, var4, this.digest, this.participantId, this.random);
         this.state = 30;
         return new ECJPAKERound2Payload(this.participantId, var4, var5);
      }
   }

   public void validateRound2PayloadReceived(ECJPAKERound2Payload var1) throws CryptoException {
      if (this.state >= 40) {
         throw new IllegalStateException("Validation already attempted for round2 payload for" + this.participantId);
      } else if (this.state < 20) {
         throw new IllegalStateException("Round1 payload must be validated prior to validating Round2 payload for " + this.participantId);
      } else {
         ECPoint var2 = ECJPAKEUtil.calculateGA(this.gx3, this.gx1, this.gx2);
         this.b = var1.getA();
         ECSchnorrZKP var3 = var1.getKnowledgeProofForX2s();
         ECJPAKEUtil.validateParticipantIdsDiffer(this.participantId, var1.getParticipantId());
         ECJPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, var1.getParticipantId());
         ECJPAKEUtil.validateZeroKnowledgeProof(var2, this.b, var3, this.q, this.n, this.ecCurve, this.h, var1.getParticipantId(), this.digest);
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
         BigInteger var2 = ECJPAKEUtil.calculateKeyingMaterial(this.n, this.gx4, this.x2, var1, this.b);
         this.x1 = null;
         this.x2 = null;
         this.b = null;
         this.state = 50;
         return var2;
      }
   }

   public ECJPAKERound3Payload createRound3PayloadToSend(BigInteger var1) {
      if (this.state >= 60) {
         throw new IllegalStateException("Round3 payload already created for " + this.participantId);
      } else if (this.state < 50) {
         throw new IllegalStateException("Keying material must be calculated prior to creating Round3 payload for " + this.participantId);
      } else {
         BigInteger var2 = ECJPAKEUtil.calculateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, var1, this.digest);
         this.state = 60;
         return new ECJPAKERound3Payload(this.participantId, var2);
      }
   }

   public void validateRound3PayloadReceived(ECJPAKERound3Payload var1, BigInteger var2) throws CryptoException {
      if (this.state >= 70) {
         throw new IllegalStateException("Validation already attempted for round3 payload for" + this.participantId);
      } else if (this.state < 50) {
         throw new IllegalStateException("Keying material must be calculated validated prior to validating Round3 payload for " + this.participantId);
      } else {
         ECJPAKEUtil.validateParticipantIdsDiffer(this.participantId, var1.getParticipantId());
         ECJPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, var1.getParticipantId());
         ECJPAKEUtil.validateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, var2, this.digest, var1.getMacTag());
         this.gx1 = null;
         this.gx2 = null;
         this.gx3 = null;
         this.gx4 = null;
         this.state = 70;
      }
   }

   private BigInteger calculateS() {
      try {
         return ECJPAKEUtil.calculateS(this.n, this.password);
      } catch (CryptoException var2) {
         throw Exceptions.illegalStateException(var2.getMessage(), var2);
      }
   }
}
