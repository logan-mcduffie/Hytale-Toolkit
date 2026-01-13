package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class SRP6Server {
   protected BigInteger N;
   protected BigInteger g;
   protected BigInteger v;
   protected SecureRandom random;
   protected Digest digest;
   protected BigInteger A;
   protected BigInteger b;
   protected BigInteger B;
   protected BigInteger u;
   protected BigInteger S;
   protected BigInteger M1;
   protected BigInteger M2;
   protected BigInteger Key;

   public void init(BigInteger var1, BigInteger var2, BigInteger var3, Digest var4, SecureRandom var5) {
      this.N = var1;
      this.g = var2;
      this.v = var3;
      this.random = var5;
      this.digest = var4;
   }

   public void init(SRP6GroupParameters var1, BigInteger var2, Digest var3, SecureRandom var4) {
      this.init(var1.getN(), var1.getG(), var2, var3, var4);
   }

   public BigInteger generateServerCredentials() {
      BigInteger var1 = SRP6Util.calculateK(this.digest, this.N, this.g);
      this.b = this.selectPrivateValue();
      this.B = var1.multiply(this.v).mod(this.N).add(this.g.modPow(this.b, this.N)).mod(this.N);
      return this.B;
   }

   public BigInteger calculateSecret(BigInteger var1) throws CryptoException {
      this.A = SRP6Util.validatePublicValue(this.N, var1);
      this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
      this.S = this.calculateS();
      return this.S;
   }

   protected BigInteger selectPrivateValue() {
      return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
   }

   private BigInteger calculateS() {
      return this.v.modPow(this.u, this.N).multiply(this.A).mod(this.N).modPow(this.b, this.N);
   }

   public boolean verifyClientEvidenceMessage(BigInteger var1) throws CryptoException {
      if (this.A != null && this.B != null && this.S != null) {
         BigInteger var2 = SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S);
         if (var2.equals(var1)) {
            this.M1 = var1;
            return true;
         } else {
            return false;
         }
      } else {
         throw new CryptoException("Impossible to compute and verify M1: some data are missing from the previous operations (A,B,S)");
      }
   }

   public BigInteger calculateServerEvidenceMessage() throws CryptoException {
      if (this.A != null && this.M1 != null && this.S != null) {
         this.M2 = SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S);
         return this.M2;
      } else {
         throw new CryptoException("Impossible to compute M2: some data are missing from the previous operations (A,M1,S)");
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
