package org.bouncycastle.crypto.prng.drbg;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class DualECSP800DRBG implements SP80090DRBG {
   private static final BigInteger p256_Px = new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16);
   private static final BigInteger p256_Py = new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16);
   private static final BigInteger p256_Qx = new BigInteger("c97445f45cdef9f0d3e05e1e585fc297235b82b5be8ff3efca67c59852018192", 16);
   private static final BigInteger p256_Qy = new BigInteger("b28ef557ba31dfcbdd21ac46e2a91e3c304f44cb87058ada2cb815151e610046", 16);
   private static final BigInteger p384_Px = new BigInteger(
      "aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7", 16
   );
   private static final BigInteger p384_Py = new BigInteger(
      "3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", 16
   );
   private static final BigInteger p384_Qx = new BigInteger(
      "8e722de3125bddb05580164bfe20b8b432216a62926c57502ceede31c47816edd1e89769124179d0b695106428815065", 16
   );
   private static final BigInteger p384_Qy = new BigInteger(
      "023b1660dd701d0839fd45eec36f9ee7b32e13b315dc02610aa1b636e346df671f790f84c5e09b05674dbb7e45c803dd", 16
   );
   private static final BigInteger p521_Px = new BigInteger(
      "c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16
   );
   private static final BigInteger p521_Py = new BigInteger(
      "11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16
   );
   private static final BigInteger p521_Qx = new BigInteger(
      "1b9fa3e518d683c6b65763694ac8efbaec6fab44f2276171a42726507dd08add4c3b3f4c1ebc5b1222ddba077f722943b24c3edfa0f85fe24d0c8c01591f0be6f63", 16
   );
   private static final BigInteger p521_Qy = new BigInteger(
      "1f3bdba585295d9a1110d1df1f9430ef8442c5018976ff3437ef91b81dc0b8132c8d5c39c32d0e004a3092b7d327c0e7a4d26d2c7b69b58f9066652911e457779de", 16
   );
   private static final DualECPoints[] nistPoints = new DualECPoints[]{
      createDualECPoints("P-256", 128, p256_Px, p256_Py, p256_Qx, p256_Qy, 1),
      createDualECPoints("P-384", 192, p384_Px, p384_Py, p384_Qx, p384_Qy, 1),
      createDualECPoints("P-521", 256, p521_Px, p521_Py, p521_Qx, p521_Qy, 1)
   };
   private static final long RESEED_MAX = 2147483648L;
   private static final int MAX_ADDITIONAL_INPUT = 4096;
   private static final int MAX_ENTROPY_LENGTH = 4096;
   private static final int MAX_PERSONALIZATION_STRING = 4096;
   private Digest _digest;
   private long _reseedCounter;
   private EntropySource _entropySource;
   private int _securityStrength;
   private int _seedlen;
   private int _outlen;
   private ECPoint _P;
   private ECPoint _Q;
   private byte[] _s;
   private int _sLength;
   private ECMultiplier _fixedPointMultiplier = new FixedPointCombMultiplier();

   private static DualECPoints createDualECPoints(String var0, int var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, int var6) {
      ECCurve.AbstractFp var7 = (ECCurve.AbstractFp)CustomNamedCurves.getByNameLazy(var0).getCurve();
      return new DualECPoints(var1, var7.createPoint(var2, var3), var7.createPoint(var4, var5), var6);
   }

   public DualECSP800DRBG(Digest var1, int var2, EntropySource var3, byte[] var4, byte[] var5) {
      this(nistPoints, var1, var2, var3, var4, var5);
   }

   public DualECSP800DRBG(DualECPoints[] var1, Digest var2, int var3, EntropySource var4, byte[] var5, byte[] var6) {
      this._digest = var2;
      this._entropySource = var4;
      this._securityStrength = var3;
      if (Utils.isTooLarge(var5, 512)) {
         throw new IllegalArgumentException("Personalization string too large");
      } else if (var4.entropySize() >= var3 && var4.entropySize() <= 4096) {
         byte[] var7 = this.getEntropy();
         byte[] var8 = Arrays.concatenate(var7, var6, var5);

         for (int var9 = 0; var9 != var1.length; var9++) {
            if (var3 <= var1[var9].getSecurityStrength()) {
               if (Utils.getMaxSecurityStrength(var2) < var1[var9].getSecurityStrength()) {
                  throw new IllegalArgumentException("Requested security strength is not supported by digest");
               }

               this._seedlen = var1[var9].getSeedLen();
               this._outlen = var1[var9].getMaxOutlen() / 8;
               this._P = var1[var9].getP();
               this._Q = var1[var9].getQ();
               break;
            }
         }

         if (this._P == null) {
            throw new IllegalArgumentException("security strength cannot be greater than 256 bits");
         } else {
            this._s = Utils.hash_df(this._digest, var8, this._seedlen);
            this._sLength = this._s.length;
            this._reseedCounter = 0L;
         }
      } else {
         throw new IllegalArgumentException("EntropySource must provide between " + var3 + " and " + 4096 + " bits");
      }
   }

   @Override
   public int getBlockSize() {
      return this._outlen * 8;
   }

   @Override
   public int generate(byte[] var1, byte[] var2, boolean var3) {
      int var4 = var1.length * 8;
      int var5 = var1.length / this._outlen;
      if (Utils.isTooLarge(var2, 512)) {
         throw new IllegalArgumentException("Additional input too large");
      } else if (this._reseedCounter + var5 > 2147483648L) {
         return -1;
      } else {
         if (var3) {
            this.reseed(var2);
            var2 = null;
         }

         BigInteger var6;
         if (var2 != null) {
            var2 = Utils.hash_df(this._digest, var2, this._seedlen);
            var6 = new BigInteger(1, this.xor(this._s, var2));
         } else {
            var6 = new BigInteger(1, this._s);
         }

         Arrays.fill(var1, (byte)0);
         int var7 = 0;

         for (int var8 = 0; var8 < var5; var8++) {
            var6 = this.getScalarMultipleXCoord(this._P, var6);
            byte[] var9 = this.getScalarMultipleXCoord(this._Q, var6).toByteArray();
            if (var9.length >= this._outlen) {
               System.arraycopy(var9, var9.length - this._outlen, var1, var7, this._outlen);
            } else {
               System.arraycopy(var9, 0, var1, var7 + (this._outlen - var9.length), var9.length);
            }

            var7 += this._outlen;
            this._reseedCounter++;
         }

         if (var7 < var1.length) {
            var6 = this.getScalarMultipleXCoord(this._P, var6);
            byte[] var12 = this.getScalarMultipleXCoord(this._Q, var6).toByteArray();
            int var13 = var1.length - var7;
            if (var12.length >= this._outlen) {
               System.arraycopy(var12, var12.length - this._outlen, var1, var7, var13);
            } else {
               int var10 = this._outlen - var12.length;
               if (var10 < var13) {
                  System.arraycopy(var12, 0, var1, var7 + var10, var13 - var10);
               }
            }

            this._reseedCounter++;
         }

         this._s = BigIntegers.asUnsignedByteArray(this._sLength, this.getScalarMultipleXCoord(this._P, var6));
         return var4;
      }
   }

   @Override
   public void reseed(byte[] var1) {
      if (Utils.isTooLarge(var1, 512)) {
         throw new IllegalArgumentException("Additional input string too large");
      } else {
         byte[] var2 = this.getEntropy();
         byte[] var3 = Arrays.concatenate(this.pad8(this._s, this._seedlen), var2, var1);
         this._s = Utils.hash_df(this._digest, var3, this._seedlen);
         this._reseedCounter = 0L;
      }
   }

   private byte[] getEntropy() {
      byte[] var1 = this._entropySource.getEntropy();
      if (var1.length < (this._securityStrength + 7) / 8) {
         throw new IllegalStateException("Insufficient entropy provided by entropy source");
      } else {
         return var1;
      }
   }

   private byte[] xor(byte[] var1, byte[] var2) {
      if (var2 == null) {
         return var1;
      } else {
         byte[] var3 = new byte[var1.length];

         for (int var4 = 0; var4 != var3.length; var4++) {
            var3[var4] = (byte)(var1[var4] ^ var2[var4]);
         }

         return var3;
      }
   }

   private byte[] pad8(byte[] var1, int var2) {
      if (var2 % 8 == 0) {
         return var1;
      } else {
         int var3 = 8 - var2 % 8;
         int var4 = 0;

         for (int var5 = var1.length - 1; var5 >= 0; var5--) {
            int var6 = var1[var5] & 255;
            var1[var5] = (byte)(var6 << var3 | var4 >> 8 - var3);
            var4 = var6;
         }

         return var1;
      }
   }

   private BigInteger getScalarMultipleXCoord(ECPoint var1, BigInteger var2) {
      return this._fixedPointMultiplier.multiply(var1, var2).normalize().getAffineXCoord().toBigInteger();
   }
}
