package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class ECCSIKeyGenerationParameters extends KeyGenerationParameters {
   private final BigInteger q;
   private final ECPoint G;
   private final Digest digest;
   private final byte[] id;
   private final BigInteger ksak;
   private final ECPoint kpak;
   private final int n;

   public ECCSIKeyGenerationParameters(SecureRandom var1, X9ECParameters var2, Digest var3, byte[] var4) {
      super(var1, var2.getCurve().getA().bitLength());
      this.q = var2.getCurve().getOrder();
      this.G = var2.getG();
      this.digest = var3;
      this.id = Arrays.clone(var4);
      this.n = var2.getCurve().getA().bitLength();
      this.ksak = BigIntegers.createRandomBigInteger(this.n, var1).mod(this.q);
      this.kpak = this.G.multiply(this.ksak).normalize();
   }

   public byte[] getId() {
      return Arrays.clone(this.id);
   }

   public ECPoint getKPAK() {
      return this.kpak;
   }

   public BigInteger computeSSK(BigInteger var1) {
      return this.ksak.add(var1).mod(this.q);
   }

   public BigInteger getQ() {
      return this.q;
   }

   public ECPoint getG() {
      return this.G;
   }

   public Digest getDigest() {
      return this.digest;
   }

   public int getN() {
      return this.n;
   }
}
