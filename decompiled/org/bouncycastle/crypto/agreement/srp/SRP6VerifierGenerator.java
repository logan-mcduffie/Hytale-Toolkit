package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SRP6VerifierGenerator {
   protected BigInteger N;
   protected BigInteger g;
   protected Digest digest;

   public void init(BigInteger var1, BigInteger var2, Digest var3) {
      this.N = var1;
      this.g = var2;
      this.digest = var3;
   }

   public void init(SRP6GroupParameters var1, Digest var2) {
      this.N = var1.getN();
      this.g = var1.getG();
      this.digest = var2;
   }

   public BigInteger generateVerifier(byte[] var1, byte[] var2, byte[] var3) {
      BigInteger var4 = SRP6Util.calculateX(this.digest, this.N, var1, var2, var3);
      return this.g.modPow(var4, this.N);
   }
}
