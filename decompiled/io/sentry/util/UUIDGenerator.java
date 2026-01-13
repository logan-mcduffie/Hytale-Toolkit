package io.sentry.util;

import java.util.UUID;

public final class UUIDGenerator {
   public static long randomHalfLengthUUID() {
      Random ng = SentryRandom.current();
      byte[] randomBytes = new byte[8];
      ng.nextBytes(randomBytes);
      randomBytes[6] = (byte)(randomBytes[6] & 15);
      randomBytes[6] = (byte)(randomBytes[6] | 64);
      long msb = 0L;

      for (int i = 0; i < 8; i++) {
         msb = msb << 8 | randomBytes[i] & 255;
      }

      return msb;
   }

   public static UUID randomUUID() {
      Random ng = SentryRandom.current();
      byte[] randomBytes = new byte[16];
      ng.nextBytes(randomBytes);
      randomBytes[6] = (byte)(randomBytes[6] & 15);
      randomBytes[6] = (byte)(randomBytes[6] | 64);
      randomBytes[8] = (byte)(randomBytes[8] & 63);
      randomBytes[8] = (byte)(randomBytes[8] | 128);
      long msb = 0L;
      long lsb = 0L;

      for (int i = 0; i < 8; i++) {
         msb = msb << 8 | randomBytes[i] & 255;
      }

      for (int i = 8; i < 16; i++) {
         lsb = lsb << 8 | randomBytes[i] & 255;
      }

      return new UUID(msb, lsb);
   }
}
