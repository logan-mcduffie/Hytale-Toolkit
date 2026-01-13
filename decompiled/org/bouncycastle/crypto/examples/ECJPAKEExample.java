package org.bouncycastle.crypto.examples;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKECurve;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKECurves;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKEParticipant;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKERound1Payload;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKERound2Payload;
import org.bouncycastle.crypto.agreement.ecjpake.ECJPAKERound3Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.math.ec.ECPoint;

public class ECJPAKEExample {
   public static void main(String[] var0) throws CryptoException {
      ECJPAKECurve var1 = ECJPAKECurves.NIST_P256;
      BigInteger var2 = var1.getA();
      BigInteger var3 = var1.getB();
      ECPoint var4 = var1.getG();
      BigInteger var5 = var1.getH();
      BigInteger var6 = var1.getN();
      BigInteger var7 = var1.getQ();
      String var8 = "password";
      String var9 = "password";
      System.out.println("********* Initialization **********");
      System.out.println("Public parameters for the elliptic curve over prime field:");
      System.out.println("Curve param a (" + var2.bitLength() + " bits): " + var2.toString(16));
      System.out.println("Curve param b (" + var3.bitLength() + " bits): " + var3.toString(16));
      System.out.println("Co-factor h (" + var5.bitLength() + " bits): " + var5.toString(16));
      System.out.println("Base point G (" + var4.getEncoded(true).length + " bytes): " + new BigInteger(var4.getEncoded(true)).toString(16));
      System.out
         .println("X coord of G (G not normalised) (" + var4.getXCoord().toBigInteger().bitLength() + " bits): " + var4.getXCoord().toBigInteger().toString(16));
      System.out
         .println("y coord of G (G not normalised) (" + var4.getYCoord().toBigInteger().bitLength() + " bits): " + var4.getYCoord().toBigInteger().toString(16));
      System.out.println("Order of the base point n (" + var6.bitLength() + " bits): " + var6.toString(16));
      System.out.println("Prime field q (" + var7.bitLength() + " bits): " + var7.toString(16));
      System.out.println("");
      System.out.println("(Secret passwords used by Alice and Bob: \"" + var8 + "\" and \"" + var9 + "\")\n");
      SavableDigest var10 = SHA256Digest.newInstance();
      SecureRandom var11 = new SecureRandom();
      ECJPAKEParticipant var12 = new ECJPAKEParticipant("alice", var8.toCharArray(), var1, var10, var11);
      ECJPAKEParticipant var13 = new ECJPAKEParticipant("bob", var9.toCharArray(), var1, var10, var11);
      ECJPAKERound1Payload var14 = var12.createRound1PayloadToSend();
      ECJPAKERound1Payload var15 = var13.createRound1PayloadToSend();
      System.out.println("************ Round 1 **************");
      System.out.println("Alice sends to Bob: ");
      System.out.println("g^{x1}=" + new BigInteger(var14.getGx1().getEncoded(true)).toString(16));
      System.out.println("g^{x2}=" + new BigInteger(var14.getGx2().getEncoded(true)).toString(16));
      System.out
         .println(
            "KP{x1}: {V="
               + new BigInteger(var14.getKnowledgeProofForX1().getV().getEncoded(true)).toString(16)
               + "; r="
               + var14.getKnowledgeProofForX1().getr().toString(16)
               + "}"
         );
      System.out
         .println(
            "KP{x2}: {V="
               + new BigInteger(var14.getKnowledgeProofForX2().getV().getEncoded(true)).toString(16)
               + "; r="
               + var14.getKnowledgeProofForX2().getr().toString(16)
               + "}"
         );
      System.out.println("");
      System.out.println("Bob sends to Alice: ");
      System.out.println("g^{x3}=" + new BigInteger(var15.getGx1().getEncoded(true)).toString(16));
      System.out.println("g^{x4}=" + new BigInteger(var15.getGx2().getEncoded(true)).toString(16));
      System.out
         .println(
            "KP{x3}: {V="
               + new BigInteger(var15.getKnowledgeProofForX1().getV().getEncoded(true)).toString(16)
               + "; r="
               + var15.getKnowledgeProofForX1().getr().toString(16)
               + "}"
         );
      System.out
         .println(
            "KP{x4}: {V="
               + new BigInteger(var15.getKnowledgeProofForX2().getV().getEncoded(true)).toString(16)
               + "; r="
               + var15.getKnowledgeProofForX2().getr().toString(16)
               + "}"
         );
      System.out.println("");
      var12.validateRound1PayloadReceived(var15);
      System.out.println("Alice checks g^{x4}!=1: OK");
      System.out.println("Alice checks KP{x3}: OK");
      System.out.println("Alice checks KP{x4}: OK");
      System.out.println("");
      var13.validateRound1PayloadReceived(var14);
      System.out.println("Bob checks g^{x2}!=1: OK");
      System.out.println("Bob checks KP{x1},: OK");
      System.out.println("Bob checks KP{x2},: OK");
      System.out.println("");
      ECJPAKERound2Payload var16 = var12.createRound2PayloadToSend();
      ECJPAKERound2Payload var17 = var13.createRound2PayloadToSend();
      System.out.println("************ Round 2 **************");
      System.out.println("Alice sends to Bob: ");
      System.out.println("A=" + new BigInteger(var16.getA().getEncoded(true)).toString(16));
      System.out
         .println(
            "KP{x2*s}: {V="
               + new BigInteger(var16.getKnowledgeProofForX2s().getV().getEncoded(true)).toString(16)
               + ", r="
               + var16.getKnowledgeProofForX2s().getr().toString(16)
               + "}"
         );
      System.out.println("");
      System.out.println("Bob sends to Alice");
      System.out.println("B=" + new BigInteger(var17.getA().getEncoded(true)).toString(16));
      System.out
         .println(
            "KP{x4*s}: {V="
               + new BigInteger(var17.getKnowledgeProofForX2s().getV().getEncoded(true)).toString(16)
               + ", r="
               + var17.getKnowledgeProofForX2s().getr().toString(16)
               + "}"
         );
      System.out.println("");
      var12.validateRound2PayloadReceived(var17);
      System.out.println("Alice checks KP{x4*s}: OK\n");
      var13.validateRound2PayloadReceived(var16);
      System.out.println("Bob checks KP{x2*s}: OK\n");
      BigInteger var18 = var12.calculateKeyingMaterial();
      BigInteger var19 = var13.calculateKeyingMaterial();
      System.out.println("********* After round 2 ***********");
      System.out.println("Alice computes key material \t K=" + var18.toString(16));
      System.out.println("Bob computes key material \t K=" + var19.toString(16));
      System.out.println();
      BigInteger var20 = deriveSessionKey(var18);
      BigInteger var21 = deriveSessionKey(var19);
      ECJPAKERound3Payload var22 = var12.createRound3PayloadToSend(var18);
      ECJPAKERound3Payload var23 = var13.createRound3PayloadToSend(var19);
      System.out.println("************ Round 3 **************");
      System.out.println("Alice sends to Bob: ");
      System.out.println("MacTag=" + var22.getMacTag().toString(16));
      System.out.println("");
      System.out.println("Bob sends to Alice: ");
      System.out.println("MacTag=" + var23.getMacTag().toString(16));
      System.out.println("");
      var12.validateRound3PayloadReceived(var23, var18);
      System.out.println("Alice checks MacTag: OK\n");
      var13.validateRound3PayloadReceived(var22, var19);
      System.out.println("Bob checks MacTag: OK\n");
      System.out.println();
      System.out.println("MacTags validated, therefore the keying material matches.");
   }

   private static BigInteger deriveSessionKey(BigInteger var0) {
      SavableDigest var1 = SHA256Digest.newInstance();
      byte[] var2 = var0.toByteArray();
      byte[] var3 = new byte[var1.getDigestSize()];
      var1.update(var2, 0, var2.length);
      var1.doFinal(var3, 0);
      return new BigInteger(var3);
   }
}
