package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECIESKEMExtractor implements EncapsulatedSecretExtractor {
   private final ECPrivateKeyParameters decKey;
   private int keyLen;
   private DerivationFunction kdf;
   private boolean CofactorMode;
   private boolean OldCofactorMode;
   private boolean SingleHashMode;

   public ECIESKEMExtractor(ECPrivateKeyParameters var1, int var2, DerivationFunction var3) {
      this.decKey = var1;
      this.keyLen = var2;
      this.kdf = var3;
      this.CofactorMode = false;
      this.OldCofactorMode = false;
      this.SingleHashMode = false;
   }

   public ECIESKEMExtractor(ECPrivateKeyParameters var1, int var2, DerivationFunction var3, boolean var4, boolean var5, boolean var6) {
      this.decKey = var1;
      this.keyLen = var2;
      this.kdf = var3;
      this.CofactorMode = var4;
      if (var4) {
         this.OldCofactorMode = false;
      } else {
         this.OldCofactorMode = var5;
      }

      this.SingleHashMode = var6;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "ECIESKem", ConstraintUtils.bitsOfSecurityFor(this.decKey.getParameters().getCurve()), var1, CryptoServicePurpose.DECRYPTION
         )
      );
   }

   @Override
   public byte[] extractSecret(byte[] var1) {
      ECPrivateKeyParameters var2 = this.decKey;
      ECDomainParameters var3 = var2.getParameters();
      ECCurve var4 = var3.getCurve();
      BigInteger var5 = var3.getN();
      BigInteger var6 = var3.getH();
      ECPoint var7 = var4.decodePoint(var1);
      if (this.CofactorMode || this.OldCofactorMode) {
         var7 = var7.multiply(var6);
      }

      BigInteger var8 = var2.getD();
      if (this.CofactorMode) {
         var8 = var8.multiply(var3.getHInv()).mod(var5);
      }

      ECPoint var9 = var7.multiply(var8).normalize();
      byte[] var10 = var9.getAffineXCoord().getEncoded();
      return ECIESKEMGenerator.deriveKey(this.SingleHashMode, this.kdf, this.keyLen, var1, var10);
   }

   @Override
   public int getEncapsulationLength() {
      return this.decKey.getParameters().getCurve().getAffinePointEncodingLength(false);
   }
}
