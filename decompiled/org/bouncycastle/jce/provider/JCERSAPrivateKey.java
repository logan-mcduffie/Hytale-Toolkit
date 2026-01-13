package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class JCERSAPrivateKey implements RSAPrivateKey, PKCS12BagAttributeCarrier {
   static final long serialVersionUID = 5110188922551353628L;
   private static BigInteger ZERO = BigInteger.valueOf(0L);
   protected BigInteger modulus;
   protected BigInteger privateExponent;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected JCERSAPrivateKey() {
   }

   JCERSAPrivateKey(RSAKeyParameters var1) {
      this.modulus = var1.getModulus();
      this.privateExponent = var1.getExponent();
   }

   JCERSAPrivateKey(RSAPrivateKeySpec var1) {
      this.modulus = var1.getModulus();
      this.privateExponent = var1.getPrivateExponent();
   }

   JCERSAPrivateKey(RSAPrivateKey var1) {
      this.modulus = var1.getModulus();
      this.privateExponent = var1.getPrivateExponent();
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
      return "RSA";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedPrivateKeyInfo(
         new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
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
      this.modulus = (BigInteger)var1.readObject();
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
      this.attrCarrier.readObject(var1);
      this.privateExponent = (BigInteger)var1.readObject();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.modulus);
      this.attrCarrier.writeObject(var1);
      var1.writeObject(this.privateExponent);
   }
}
