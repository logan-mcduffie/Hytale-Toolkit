package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.engines.PhotonBeetleEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class PhotonBeetleDigest extends BufferBaseDigest {
   private final byte[] state;
   private static final int SQUEEZE_RATE_INBYTES = 16;
   private static final int D = 8;
   private int blockCount;

   public PhotonBeetleDigest() {
      super(BufferBaseDigest.ProcessingBufferType.Buffered, 4);
      this.DigestSize = 32;
      this.state = new byte[this.DigestSize];
      this.algorithmName = "Photon-Beetle Hash";
      this.blockCount = 0;
   }

   @Override
   protected void processBytes(byte[] var1, int var2) {
      if (this.blockCount < 4) {
         System.arraycopy(var1, var2, this.state, this.blockCount << 2, this.BlockSize);
      } else {
         PhotonBeetleEngine.photonPermutation(PhotonBeetleDigest.Friend.INSTANCE, this.state);
         Bytes.xorTo(this.BlockSize, var1, var2, this.state);
      }

      this.blockCount++;
   }

   @Override
   protected void finish(byte[] var1, int var2) {
      byte var3 = 5;
      if (this.m_bufPos == 0 && this.blockCount == 0) {
         this.state[this.DigestSize - 1] = (byte)(this.state[this.DigestSize - 1] ^ 1 << var3);
      } else if (this.blockCount < 4) {
         System.arraycopy(this.m_buf, 0, this.state, this.blockCount << 2, this.m_bufPos);
         this.state[(this.blockCount << 2) + this.m_bufPos] = (byte)(this.state[(this.blockCount << 2) + this.m_bufPos] ^ 1);
         this.state[this.DigestSize - 1] = (byte)(this.state[this.DigestSize - 1] ^ 1 << var3);
      } else if (this.blockCount == 4 && this.m_bufPos == 0) {
         this.state[this.DigestSize - 1] = (byte)(this.state[this.DigestSize - 1] ^ 2 << var3);
      } else {
         PhotonBeetleEngine.photonPermutation(PhotonBeetleDigest.Friend.INSTANCE, this.state);
         Bytes.xorTo(this.m_bufPos, this.m_buf, this.state);
         if (this.m_bufPos < this.BlockSize) {
            this.state[this.m_bufPos] = (byte)(this.state[this.m_bufPos] ^ 1);
         }

         this.state[this.DigestSize - 1] = (byte)(this.state[this.DigestSize - 1] ^ (this.m_bufPos % this.BlockSize == 0 ? 1 : 2) << var3);
      }

      PhotonBeetleEngine.photonPermutation(PhotonBeetleDigest.Friend.INSTANCE, this.state);
      System.arraycopy(this.state, 0, var1, var2, 16);
      PhotonBeetleEngine.photonPermutation(PhotonBeetleDigest.Friend.INSTANCE, this.state);
      System.arraycopy(this.state, 0, var1, var2 + 16, 16);
   }

   @Override
   public void reset() {
      super.reset();
      Arrays.fill(this.state, (byte)0);
      this.blockCount = 0;
   }

   public static class Friend {
      private static final PhotonBeetleDigest.Friend INSTANCE = new PhotonBeetleDigest.Friend();

      private Friend() {
      }
   }
}
