package io.netty.handler.codec.quic;

import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class SipHash {
   static final int SEED_LENGTH = 16;
   private final int compressionRounds;
   private final int finalizationRounds;
   private static final long INITIAL_STATE_V0 = 8317987319222330741L;
   private static final long INITIAL_STATE_V1 = 7237128888997146477L;
   private static final long INITIAL_STATE_V2 = 7816392313619706465L;
   private static final long INITIAL_STATE_V3 = 8387220255154660723L;
   private final long initialStateV0;
   private final long initialStateV1;
   private final long initialStateV2;
   private final long initialStateV3;
   private long v0;
   private long v1;
   private long v2;
   private long v3;

   SipHash(int compressionRounds, int finalizationRounds, byte[] seed) {
      if (seed.length != 16) {
         throw new IllegalArgumentException("seed must be of length 16");
      } else {
         this.compressionRounds = ObjectUtil.checkPositive(compressionRounds, "compressionRounds");
         this.finalizationRounds = ObjectUtil.checkPositive(finalizationRounds, "finalizationRounds");
         ByteBuffer keyBuffer = ByteBuffer.wrap(seed).order(ByteOrder.LITTLE_ENDIAN);
         long k0 = keyBuffer.getLong();
         long k1 = keyBuffer.getLong();
         this.initialStateV0 = 8317987319222330741L ^ k0;
         this.initialStateV1 = 7237128888997146477L ^ k1;
         this.initialStateV2 = 7816392313619706465L ^ k0;
         this.initialStateV3 = 8387220255154660723L ^ k1;
      }
   }

   long macHash(ByteBuffer input) {
      this.v0 = this.initialStateV0;
      this.v1 = this.initialStateV1;
      this.v2 = this.initialStateV2;
      this.v3 = this.initialStateV3;
      int remaining = input.remaining();
      int position = input.position();
      int len = remaining - remaining % 8;
      boolean needsReverse = input.order() == ByteOrder.BIG_ENDIAN;

      for (int offset = position; offset < len; offset += 8) {
         long m = input.getLong(offset);
         if (needsReverse) {
            m = Long.reverseBytes(m);
         }

         this.v3 ^= m;

         for (int i = 0; i < this.compressionRounds; i++) {
            this.sipround();
         }

         this.v0 ^= m;
      }

      int left = remaining & 7;
      long b = (long)remaining << 56;

      assert left < 8;

      switch (left) {
         case 7:
            b |= (long)input.get(position + len + 6) << 48;
         case 6:
            b |= (long)input.get(position + len + 5) << 40;
         case 5:
            b |= (long)input.get(position + len + 4) << 32;
         case 4:
            b |= (long)input.get(position + len + 3) << 24;
         case 3:
            b |= (long)input.get(position + len + 2) << 16;
         case 2:
            b |= (long)input.get(position + len + 1) << 8;
         case 1:
            b |= input.get(position + len);
         case 0:
            this.v3 ^= b;

            for (int i = 0; i < this.compressionRounds; i++) {
               this.sipround();
            }

            this.v0 ^= b;
            this.v2 ^= 255L;

            for (int i = 0; i < this.finalizationRounds; i++) {
               this.sipround();
            }

            return this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
         default:
            throw new IllegalStateException("Unexpected value: " + left);
      }
   }

   private void sipround() {
      this.v0 = this.v0 + this.v1;
      this.v2 = this.v2 + this.v3;
      this.v1 = Long.rotateLeft(this.v1, 13);
      this.v3 = Long.rotateLeft(this.v3, 16);
      this.v1 = this.v1 ^ this.v0;
      this.v3 = this.v3 ^ this.v2;
      this.v0 = Long.rotateLeft(this.v0, 32);
      this.v2 = this.v2 + this.v1;
      this.v0 = this.v0 + this.v3;
      this.v1 = Long.rotateLeft(this.v1, 17);
      this.v3 = Long.rotateLeft(this.v3, 21);
      this.v1 = this.v1 ^ this.v2;
      this.v3 = this.v3 ^ this.v0;
      this.v2 = Long.rotateLeft(this.v2, 32);
   }
}
