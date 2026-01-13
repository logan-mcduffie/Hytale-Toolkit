package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SRP6Client {
   protected BigInteger N;
   protected BigInteger g;
   protected BigInteger a;
   protected BigInteger A;
   protected BigInteger B;
   protected BigInteger x;
   protected BigInteger u;
   protected BigInteger S;
   protected BigInteger M1;
   protected BigInteger M2;
   protected BigInteger Key;
   protected Digest digest;
   protected SecureRandom random;

   public void init(BigInteger var1, BigInteger var2, Digest var3, SecureRandom var4) {
      this.N = var1;
      this.g = var2;
      this.digest = var3;
      this.random = var4;
   }

   public void init(SRP6GroupParameters var1, Digest var2, SecureRandom var3) {
      this.init(var1.getN(), var1.getG(), var2, var3);
   }

   public BigInteger generateClientCredentials(byte[] var1, byte[] var2, byte[] var3) {
      this.x = SRP6Util.calculateX(this.digest, this.N, var1, var2, var3);
      this.a = this.selectPrivateValue();
      this.A = this.g.modPow(this.a, this.N);
      return this.A;
   }

   public BigInteger calculateSecret(BigInteger var1) throws CryptoException {
      this.B = SRP6Util.validatePublicValue(this.N, var1);
      this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
      this.S = this.calculateS();
      return this.S;
   }

   protected BigInteger selectPrivateValue() {
      return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
   }

   private BigInteger calculateS() {
      BigInteger var1 = SRP6Util.calculateK(this.digest, this.N, this.g);
      BigInteger var2 = this.u.multiply(this.x).add(this.a);
      BigInteger var3 = this.g.modPow(this.x, this.N).multiply(var1).mod(this.N);
      return this.B.subtract(var3).mod(this.N).modPow(var2, this.N);
   }

   public BigInteger calculateClientEvidenceMessage() throws CryptoException {
      if (this.A != null && this.B != null && this.S != null) {
         this.M1 = SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S);
         return this.M1;
      } else {
         throw new CryptoException("Impossible to compute M1: some data are missing from the previous operations (A,B,S)");
      }
   }

   public boolean verifyServerEvidenceMessage(BigInteger var1) throws CryptoException {
      if (this.A != null && this.M1 != null && this.S != null) {
         BigInteger var2 = SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S);
         if (var2.equals(var1)) {
            this.M2 = var1;
            return true;
         } else {
            return false;
         }
      } else {
         throw new CryptoException("Impossible to compute and verify M2: some data are missing from the previous operations (A,M1,S)");
      }
   }

   public BigInteger calculateSessionKey() throws CryptoException {
      if (this.S != null && this.M1 != null && this.M2 != null) {
         this.Key = SRP6Util.calculateKey(this.digest, this.N, this.S);
         return this.Key;
      } else {
         throw new CryptoException("Impossible to compute Key: some data are missing from the previous operations (S,M1,M2)");
      }
   }
}
