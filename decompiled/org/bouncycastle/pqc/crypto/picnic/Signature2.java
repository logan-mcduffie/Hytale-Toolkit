package org.bouncycastle.pqc.crypto.picnic;

class Signature2 {
   byte[] salt;
   byte[] iSeedInfo;
   int iSeedInfoLen;
   byte[] cvInfo;
   int cvInfoLen;
   byte[] challengeHash;
   int[] challengeC;
   int[] challengeP;
   Signature2.Proof2[] proofs;

   public Signature2(PicnicEngine var1) {
      this.challengeHash = new byte[var1.digestSizeBytes];
      this.salt = new byte[32];
      this.challengeC = new int[var1.numOpenedRounds];
      this.challengeP = new int[var1.numOpenedRounds];
      this.proofs = new Signature2.Proof2[var1.numMPCRounds];
   }

   public static class Proof2 {
      byte[] seedInfo = null;
      int seedInfoLen = 0;
      byte[] aux;
      byte[] C;
      byte[] input;
      byte[] msgs;

      public Proof2(PicnicEngine var1) {
         this.C = new byte[var1.digestSizeBytes];
         this.input = new byte[var1.stateSizeBytes];
         this.aux = new byte[var1.andSizeBytes];
         this.msgs = new byte[var1.andSizeBytes];
      }
   }
}
