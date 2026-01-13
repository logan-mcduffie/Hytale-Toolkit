package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.util.Strings;

public class BCRSAPrivateCrtKey extends BCRSAPrivateKey implements RSAPrivateCrtKey {
   static final long serialVersionUID = 7834723820638524718L;
   private BigInteger publicExponent;
   private BigInteger primeP;
   private BigInteger primeQ;
   private BigInteger primeExponentP;
   private BigInteger primeExponentQ;
   private BigInteger crtCoefficient;

   BCRSAPrivateCrtKey(RSAPrivateCrtKeyParameters var1) {
      super(var1);
      this.publicExponent = var1.getPublicExponent();
      this.primeP = var1.getP();
      this.primeQ = var1.getQ();
      this.primeExponentP = var1.getDP();
      this.primeExponentQ = var1.getDQ();
      this.crtCoefficient = var1.getQInv();
   }

   BCRSAPrivateCrtKey(AlgorithmIdentifier var1, RSAPrivateCrtKeyParameters var2) {
      super(var1, var2);
      this.publicExponent = var2.getPublicExponent();
      this.primeP = var2.getP();
      this.primeQ = var2.getQ();
      this.primeExponentP = var2.getDP();
      this.primeExponentQ = var2.getDQ();
      this.crtCoefficient = var2.getQInv();
   }

   BCRSAPrivateCrtKey(RSAPrivateCrtKeySpec var1) {
      super(
         new RSAPrivateCrtKeyParameters(
            var1.getModulus(),
            var1.getPublicExponent(),
            var1.getPrivateExponent(),
            var1.getPrimeP(),
            var1.getPrimeQ(),
            var1.getPrimeExponentP(),
            var1.getPrimeExponentQ(),
            var1.getCrtCoefficient()
         )
      );
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.privateExponent = var1.getPrivateExponent();
      this.primeP = var1.getPrimeP();
      this.primeQ = var1.getPrimeQ();
      this.primeExponentP = var1.getPrimeExponentP();
      this.primeExponentQ = var1.getPrimeExponentQ();
      this.crtCoefficient = var1.getCrtCoefficient();
   }

   BCRSAPrivateCrtKey(RSAPrivateCrtKey var1) {
      super(
         new RSAPrivateCrtKeyParameters(
            var1.getModulus(),
            var1.getPublicExponent(),
            var1.getPrivateExponent(),
            var1.getPrimeP(),
            var1.getPrimeQ(),
            var1.getPrimeExponentP(),
            var1.getPrimeExponentQ(),
            var1.getCrtCoefficient()
         )
      );
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.privateExponent = var1.getPrivateExponent();
      this.primeP = var1.getPrimeP();
      this.primeQ = var1.getPrimeQ();
      this.primeExponentP = var1.getPrimeExponentP();
      this.primeExponentQ = var1.getPrimeExponentQ();
      this.crtCoefficient = var1.getCrtCoefficient();
   }

   BCRSAPrivateCrtKey(PrivateKeyInfo var1) throws IOException {
      this(var1.getPrivateKeyAlgorithm(), RSAPrivateKey.getInstance(var1.parsePrivateKey()));
   }

   BCRSAPrivateCrtKey(RSAPrivateKey var1) {
      this(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER, var1);
   }

   BCRSAPrivateCrtKey(AlgorithmIdentifier var1, RSAPrivateKey var2) {
      super(
         var1,
         new RSAPrivateCrtKeyParameters(
            var2.getModulus(),
            var2.getPublicExponent(),
            var2.getPrivateExponent(),
            var2.getPrime1(),
            var2.getPrime2(),
            var2.getExponent1(),
            var2.getExponent2(),
            var2.getCoefficient()
         )
      );
      this.modulus = var2.getModulus();
      this.publicExponent = var2.getPublicExponent();
      this.privateExponent = var2.getPrivateExponent();
      this.primeP = var2.getPrime1();
      this.primeQ = var2.getPrime2();
      this.primeExponentP = var2.getExponent1();
      this.primeExponentQ = var2.getExponent2();
      this.crtCoefficient = var2.getCoefficient();
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedPrivateKeyInfo(
         this.algorithmIdentifier,
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

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
      this.rsaPrivateKey = new RSAPrivateCrtKeyParameters(
         this.getModulus(),
         this.getPublicExponent(),
         this.getPrivateExponent(),
         this.getPrimeP(),
         this.getPrimeQ(),
         this.getPrimeExponentP(),
         this.getPrimeExponentQ(),
         this.getCrtCoefficient()
      );
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("RSA Private CRT Key [")
         .append(RSAUtil.generateKeyFingerprint(this.getModulus()))
         .append("]")
         .append(",[")
         .append(RSAUtil.generateExponentFingerprint(this.getPublicExponent()))
         .append("]")
         .append(var2);
      var1.append("             modulus: ").append(this.getModulus().toString(16)).append(var2);
      var1.append("     public exponent: ").append(this.getPublicExponent().toString(16)).append(var2);
      return var1.toString();
   }
}
