package org.bouncycastle.crypto.commitments;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Commitment;
import org.bouncycastle.crypto.Committer;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;

public class HashCommitter implements Committer {
   private final Digest digest;
   private final int byteLength;
   private final SecureRandom random;

   public HashCommitter(ExtendedDigest var1, SecureRandom var2) {
      this.digest = var1;
      this.byteLength = var1.getByteLength();
      this.random = var2;
   }

   @Override
   public Commitment commit(byte[] var1) {
      if (var1.length > this.byteLength / 2) {
         throw new DataLengthException("Message to be committed to too large for digest.");
      } else {
         byte[] var2 = new byte[this.byteLength - var1.length];
         this.random.nextBytes(var2);
         return new Commitment(var2, this.calculateCommitment(var2, var1));
      }
   }

   @Override
   public boolean isRevealed(Commitment var1, byte[] var2) {
      if (var2.length + var1.getSecret().length != this.byteLength) {
         throw new DataLengthException("Message and witness secret lengths do not match.");
      } else {
         byte[] var3 = this.calculateCommitment(var1.getSecret(), var2);
         return Arrays.constantTimeAreEqual(var1.getCommitment(), var3);
      }
   }

   private byte[] calculateCommitment(byte[] var1, byte[] var2) {
      byte[] var3 = new byte[this.digest.getDigestSize()];
      this.digest.update(var1, 0, var1.length);
      this.digest.update(var2, 0, var2.length);
      this.digest.doFinal(var3, 0);
      return var3;
   }
}
