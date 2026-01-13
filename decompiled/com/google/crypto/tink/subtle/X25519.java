package com.google.crypto.tink.subtle;

import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.internal.Curve25519;
import com.google.crypto.tink.internal.Field25519;
import java.security.InvalidKeyException;
import java.util.Arrays;

@Alpha
public final class X25519 {
   public static byte[] generatePrivateKey() {
      byte[] privateKey = Random.randBytes(32);
      privateKey[0] = (byte)(privateKey[0] | 7);
      privateKey[31] = (byte)(privateKey[31] & 63);
      privateKey[31] = (byte)(privateKey[31] | 128);
      return privateKey;
   }

   public static byte[] computeSharedSecret(byte[] privateKey, byte[] peersPublicValue) throws InvalidKeyException {
      if (privateKey.length != 32) {
         throw new InvalidKeyException("Private key must have 32 bytes.");
      } else {
         long[] x = new long[11];
         byte[] e = Arrays.copyOf(privateKey, 32);
         e[0] = (byte)(e[0] & 248);
         e[31] = (byte)(e[31] & 127);
         e[31] = (byte)(e[31] | 64);
         Curve25519.curveMult(x, e, peersPublicValue);
         return Field25519.contract(x);
      }
   }

   public static byte[] publicFromPrivate(byte[] privateKey) throws InvalidKeyException {
      if (privateKey.length != 32) {
         throw new InvalidKeyException("Private key must have 32 bytes.");
      } else {
         byte[] base = new byte[32];
         base[0] = 9;
         return computeSharedSecret(privateKey, base);
      }
   }

   private X25519() {
   }
}
