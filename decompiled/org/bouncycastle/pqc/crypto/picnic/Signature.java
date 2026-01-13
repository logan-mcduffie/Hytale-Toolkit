package org.bouncycastle.pqc.crypto.picnic;

class Signature {
   final byte[] challengeBits;
   final byte[] salt = new byte[32];
   final Signature.Proof[] proofs;

   Signature(PicnicEngine var1) {
      this.challengeBits = new byte[Utils.numBytes(var1.numMPCRounds * 2)];
      this.proofs = new Signature.Proof[var1.numMPCRounds];

      for (int var2 = 0; var2 < this.proofs.length; var2++) {
         this.proofs[var2] = new Signature.Proof(var1);
      }
   }

   public static class Proof {
      final byte[] seed1;
      final byte[] seed2;
      final int[] inputShare;
      final byte[] communicatedBits;
      final byte[] view3Commitment;
      final byte[] view3UnruhG;

      Proof(PicnicEngine var1) {
         this.seed1 = new byte[var1.seedSizeBytes];
         this.seed2 = new byte[var1.seedSizeBytes];
         this.inputShare = new int[var1.stateSizeWords];
         this.communicatedBits = new byte[var1.andSizeBytes];
         this.view3Commitment = new byte[var1.digestSizeBytes];
         if (var1.UnruhGWithInputBytes > 0) {
            this.view3UnruhG = new byte[var1.UnruhGWithInputBytes];
         } else {
            this.view3UnruhG = null;
         }
      }
   }
}
