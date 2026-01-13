package org.bouncycastle.crypto.signers;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.ECCSIPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECCSIPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECCSISigner implements Signer {
   private final BigInteger q;
   private final ECPoint G;
   private final Digest digest;
   private BigInteger j;
   private BigInteger r;
   private ECPoint Y;
   private final ECPoint kpak;
   private final byte[] id;
   private CipherParameters param;
   private ByteArrayOutputStream stream;
   private boolean forSigning;
   private final int N;

   public ECCSISigner(ECPoint var1, X9ECParameters var2, Digest var3, byte[] var4) {
      this.kpak = var1;
      this.id = var4;
      this.q = var2.getCurve().getOrder();
      this.G = var2.getG();
      this.digest = var3;
      this.digest.reset();
      this.N = var2.getCurve().getOrder().bitLength() + 7 >> 3;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forSigning = var1;
      this.param = var2;
      this.reset();
   }

   @Override
   public void update(byte var1) {
      if (this.forSigning) {
         this.digest.update(var1);
      } else {
         this.stream.write(var1);
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (this.forSigning) {
         this.digest.update(var1, var2, var3);
      } else {
         this.stream.write(var1, var2, var3);
      }
   }

   @Override
   public byte[] generateSignature() throws CryptoException, DataLengthException {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      ECCSIPrivateKeyParameters var2 = (ECCSIPrivateKeyParameters)((ParametersWithRandom)this.param).getParameters();
      BigInteger var3 = var2.getSSK();
      BigInteger var4 = new BigInteger(1, var1).add(this.r.multiply(var3)).mod(this.q);
      if (var4.equals(BigInteger.ZERO)) {
         throw new IllegalArgumentException("Invalid j, retry");
      } else {
         BigInteger var5 = var4.modInverse(this.q).multiply(this.j).mod(this.q);
         return Arrays.concatenate(
            BigIntegers.asUnsignedByteArray(this.N, this.r),
            BigIntegers.asUnsignedByteArray(this.N, var5),
            var2.getPublicKeyParameters().getPVT().getEncoded(false)
         );
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      byte[] var2 = Arrays.copyOf(var1, this.N);
      BigInteger var3 = new BigInteger(1, Arrays.copyOfRange(var1, this.N, this.N << 1));
      this.r = new BigInteger(1, var2).mod(this.q);
      this.digest.update(var2, 0, this.N);
      var2 = this.stream.toByteArray();
      this.digest.update(var2, 0, var2.length);
      var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);
      BigInteger var4 = new BigInteger(1, var2).mod(this.q);
      ECPoint var5 = this.G.multiply(var4).normalize();
      ECPoint var6 = this.Y.multiply(this.r).normalize();
      ECPoint var7 = var5.add(var6).normalize();
      ECPoint var8 = var7.multiply(var3).normalize();
      BigInteger var9 = var8.getAffineXCoord().toBigInteger();
      return var9.mod(this.q).equals(this.r.mod(this.q));
   }

   @Override
   public void reset() {
      this.digest.reset();
      CipherParameters var1 = this.param;
      SecureRandom var2 = null;
      if (var1 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var1).getRandom();
         var1 = ((ParametersWithRandom)var1).getParameters();
      }

      ECPoint var3 = null;
      ECPoint var4;
      if (this.forSigning) {
         ECCSIPrivateKeyParameters var5 = (ECCSIPrivateKeyParameters)var1;
         var4 = var5.getPublicKeyParameters().getPVT();
         this.j = BigIntegers.createRandomBigInteger(this.q.bitLength(), var2);
         ECPoint var6 = this.G.multiply(this.j).normalize();
         this.r = var6.getAffineXCoord().toBigInteger().mod(this.q);
         var3 = this.G.multiply(var5.getSSK());
      } else {
         ECCSIPublicKeyParameters var9 = (ECCSIPublicKeyParameters)var1;
         var4 = var9.getPVT();
         this.stream = new ByteArrayOutputStream();
      }

      byte[] var10 = this.G.getEncoded(false);
      this.digest.update(var10, 0, var10.length);
      var10 = this.kpak.getEncoded(false);
      this.digest.update(var10, 0, var10.length);
      this.digest.update(this.id, 0, this.id.length);
      var10 = var4.getEncoded(false);
      this.digest.update(var10, 0, var10.length);
      var10 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var10, 0);
      BigInteger var14 = new BigInteger(1, var10).mod(this.q);
      this.digest.update(var10, 0, var10.length);
      if (this.forSigning) {
         var3 = var3.subtract(var4.multiply(var14)).normalize();
         if (!var3.equals(this.kpak)) {
            throw new IllegalArgumentException("Invalid KPAK");
         }

         byte[] var7 = BigIntegers.asUnsignedByteArray(this.N, this.r);
         this.digest.update(var7, 0, var7.length);
      } else {
         this.Y = var4.multiply(var14).add(this.kpak).normalize();
      }
   }
}
