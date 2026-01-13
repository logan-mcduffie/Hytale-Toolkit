package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class JCERSAPrivateCrtKey extends JCERSAPrivateKey implements RSAPrivateCrtKey {
   static final long serialVersionUID = 7834723820638524718L;
   private BigInteger publicExponent;
   private BigInteger primeP;
   private BigInteger primeQ;
   private BigInteger primeExponentP;
   private BigInteger primeExponentQ;
   private BigInteger crtCoefficient;

   JCERSAPrivateCrtKey(RSAPrivateCrtKeyParameters var1) {
      super(var1);
      this.publicExponent = var1.getPublicExponent();
      this.primeP = var1.getP();
      this.primeQ = var1.getQ();
      this.primeExponentP = var1.getDP();
      this.primeExponentQ = var1.getDQ();
      this.crtCoefficient = var1.getQInv();
   }

   JCERSAPrivateCrtKey(RSAPrivateCrtKeySpec var1) {
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.privateExponent = var1.getPrivateExponent();
      this.primeP = var1.getPrimeP();
      this.primeQ = var1.getPrimeQ();
      this.primeExponentP = var1.getPrimeExponentP();
      this.primeExponentQ = var1.getPrimeExponentQ();
      this.crtCoefficient = var1.getCrtCoefficient();
   }

   JCERSAPrivateCrtKey(RSAPrivateCrtKey var1) {
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.privateExponent = var1.getPrivateExponent();
      this.primeP = var1.getPrimeP();
      this.primeQ = var1.getPrimeQ();
      this.primeExponentP = var1.getPrimeExponentP();
      this.primeExponentQ = var1.getPrimeExponentQ();
      this.crtCoefficient = var1.getCrtCoefficient();
   }

   JCERSAPrivateCrtKey(PrivateKeyInfo var1) throws IOException {
      this(RSAPrivateKey.getInstance(var1.parsePrivateKey()));
   }

   JCERSAPrivateCrtKey(RSAPrivateKey var1) {
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.privateExponent = var1.getPrivateExponent();
      this.primeP = var1.getPrime1();
      this.primeQ = var1.getPrime2();
      this.primeExponentP = var1.getExponent1();
      this.primeExponentQ = var1.getExponent2();
      this.crtCoefficient = var1.getCoefficient();
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedPrivateKeyInfo(
         new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
         new RSAPrivateKey(
            this.getModulus(),
            this.getPublicExponent(),
            this.getPrivateExponent(),
            this.getPrimeP(),
            this.getPrimeQ(),
            this.getPrimeExponentP(),
            this.getPrimeExponentQ(),
            this.getCrtCoefficient()
         )
      );
   }

   @Override
   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   @Override
   public BigInteger getPrimeP() {
      return this.primeP;
   }

   @Override
   public BigInteger getPrimeQ() {
      return this.primeQ;
   }

   @Override
   public BigInteger getPrimeExponentP() {
      return this.primeExponentP;
   }

   @Override
   public BigInteger getPrimeExponentQ() {
      return this.primeExponentQ;
   }

   @Override
   public BigInteger getCrtCoefficient() {
      return this.crtCoefficient;
   }

   @Override
   public int hashCode() {
      return this.getModulus().hashCode() ^ this.getPublicExponent().hashCode() ^ this.getPrivateExponent().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof RSAPrivateCrtKey)) {
         return false;
      } else {
         RSAPrivateCrtKey var2 = (RSAPrivateCrtKey)var1;
         return this.getModulus().equals(var2.getModulus())
            && this.getPublicExponent().equals(var2.getPublicExponent())
            && this.getPrivateExponent().equals(var2.getPrivateExponent())
            && this.getPrimeP().equals(var2.getPrimeP())
            && this.getPrimeQ().equals(var2.getPrimeQ())
            && this.getPrimeExponentP().equals(var2.getPrimeExponentP())
            && this.getPrimeExponentQ().equals(var2.getPrimeExponentQ())
            && this.getCrtCoefficient().equals(var2.getCrtCoefficient());
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("RSA Private CRT Key").append(var2);
      var1.append("            modulus: ").append(this.getModulus().toString(16)).append(var2);
      var1.append("    public exponent: ").append(this.getPublicExponent().toString(16)).append(var2);
      var1.append("   private exponent: ").append(this.getPrivateExponent().toString(16)).append(var2);
      var1.append("             primeP: ").append(this.getPrimeP().toString(16)).append(var2);
      var1.append("             primeQ: ").append(this.getPrimeQ().toString(16)).append(var2);
      var1.append("     primeExponentP: ").append(this.getPrimeExponentP().toString(16)).append(var2);
      var1.append("     primeExponentQ: ").append(this.getPrimeExponentQ().toString(16)).append(var2);
      var1.append("     crtCoefficient: ").append(this.getCrtCoefficient().toString(16)).append(var2);
      return var1.toString();
   }
}
