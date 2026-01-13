package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;

public class DHDomainParameterSpec extends DHParameterSpec {
   private final BigInteger q;
   private final BigInteger j;
   private final int m;
   private DHValidationParameters validationParameters;

   public DHDomainParameterSpec(DHParameters var1) {
      this(var1.getP(), var1.getQ(), var1.getG(), var1.getJ(), var1.getM(), var1.getL());
      this.validationParameters = var1.getValidationParameters();
   }

   public DHDomainParameterSpec(BigInteger var1, BigInteger var2, BigInteger var3) {
      this(var1, var2, var3, null, 0);
   }

   public DHDomainParameterSpec(BigInteger var1, BigInteger var2, BigInteger var3, int var4) {
      this(var1, var2, var3, null, var4);
   }

   public DHDomainParameterSpec(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, int var5) {
      this(var1, var2, var3, var4, 0, var5);
   }

   public DHDomainParameterSpec(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, int var5, int var6) {
      super(var1, var3, var6);
      this.q = var2;
      this.j = var4;
      this.m = var5;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getJ() {
      return this.j;
   }

   public int getM() {
      return this.m;
   }

   public DHParameters getDomainParameters() {
      return new DHParameters(this.getP(), this.getG(), this.q, this.m, this.getL(), this.j, this.validationParameters);
   }
}
