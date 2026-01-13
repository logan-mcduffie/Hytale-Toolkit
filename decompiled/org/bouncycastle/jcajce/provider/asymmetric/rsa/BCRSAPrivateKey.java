package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Strings;

public class BCRSAPrivateKey implements RSAPrivateKey, PKCS12BagAttributeCarrier, BCKey {
   static final long serialVersionUID = 5110188922551353628L;
   private static BigInteger ZERO = BigInteger.valueOf(0L);
   protected BigInteger modulus;
   protected BigInteger privateExponent;
   private byte[] algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
   protected transient AlgorithmIdentifier algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
   protected transient RSAKeyParameters rsaPrivateKey;
   protected transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   BCRSAPrivateKey(RSAKeyParameters var1) {
      this.modulus = var1.getModulus();
      this.privateExponent = var1.getExponent();
      this.rsaPrivateKey = var1;
   }

   BCRSAPrivateKey(AlgorithmIdentifier var1, RSAKeyParameters var2) {
      this.algorithmIdentifier = var1;
      this.algorithmIdentifierEnc = getEncoding(var1);
      this.modulus = var2.getModulus();
      this.privateExponent = var2.getExponent();
      this.rsaPrivateKey = var2;
   }

   BCRSAPrivateKey(RSAPrivateKeySpec var1) {
      this.modulus = var1.getModulus();
      this.privateExponent = var1.getPrivateExponent();
      this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
   }

   BCRSAPrivateKey(RSAPrivateKey var1) {
      this.modulus = var1.getModulus();
      this.privateExponent = var1.getPrivateExponent();
      this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
   }

   BCRSAPrivateKey(AlgorithmIdentifier var1, org.bouncycastle.asn1.pkcs.RSAPrivateKey var2) {
      this.algorithmIdentifier = var1;
      this.algorithmIdentifierEnc = getEncoding(var1);
      this.modulus = var2.getModulus();
      this.privateExponent = var2.getPrivateExponent();
      this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
   }

   @Override
   public BigInteger getModulus() {
      return this.modulus;
   }

   @Override
   public BigInteger getPrivateExponent() {
      return this.privateExponent;
   }

   @Override
   public String getAlgorithm() {
      return this.algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS) ? "RSASSA-PSS" : "RSA";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   RSAKeyParameters engineGetKeyParameters() {
      return this.rsaPrivateKey;
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedPrivateKeyInfo(
         this.algorithmIdentifier,
         new org.bouncycastle.asn1.pkcs.RSAPrivateKey(this.getModulus(), ZERO, this.getPrivateExponent(), ZERO, ZERO, ZERO, ZERO, ZERO)
      );
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof RSAPrivateKey)) {
         return false;
      } else if (var1 == this) {
         return true;
      } else {
         RSAPrivateKey var2 = (RSAPrivateKey)var1;
         return this.getModulus().equals(var2.getModulus()) && this.getPrivateExponent().equals(var2.getPrivateExponent());
      }
   }

   @Override
   public int hashCode() {
      return this.getModulus().hashCode() ^ this.getPrivateExponent().hashCode();
   }

   @Override
   public void setBagAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.attrCarrier.setBagAttribute(var1, var2);
   }

   @Override
   public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier var1) {
      return this.attrCarrier.getBagAttribute(var1);
   }

   @Override
   public Enumeration getBagAttributeKeys() {
      return this.attrCarrier.getBagAttributeKeys();
   }

   @Override
   public boolean hasFriendlyName() {
      return this.attrCarrier.hasFriendlyName();
   }

   @Override
   public void setFriendlyName(String var1) {
      this.attrCarrier.setFriendlyName(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.algorithmIdentifierEnc == null) {
         this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
      }

      this.algorithmIdentifier = AlgorithmIdentifier.getInstance(this.algorithmIdentifierEnc);
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
      this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("RSA Private Key [").append(RSAUtil.generateKeyFingerprint(this.getModulus())).append("],[]").append(var2);
      var1.append("            modulus: ").append(this.getModulus().toString(16)).append(var2);
      return var1.toString();
   }

   private static byte[] getEncoding(AlgorithmIdentifier var0) {
      try {
         return var0.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }
}
