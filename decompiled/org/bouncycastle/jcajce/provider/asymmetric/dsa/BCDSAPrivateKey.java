package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPrivateKeySpec;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Strings;

public class BCDSAPrivateKey implements DSAPrivateKey, PKCS12BagAttributeCarrier {
   private static final long serialVersionUID = -4677259546958385734L;
   private BigInteger x;
   private transient DSAParams dsaSpec;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCDSAPrivateKey() {
   }

   BCDSAPrivateKey(DSAPrivateKey var1) {
      this.x = var1.getX();
      this.dsaSpec = var1.getParams();
   }

   BCDSAPrivateKey(DSAPrivateKeySpec var1) {
      this.x = var1.getX();
      this.dsaSpec = new DSAParameterSpec(var1.getP(), var1.getQ(), var1.getG());
   }

   public BCDSAPrivateKey(PrivateKeyInfo var1) throws IOException {
      DSAParameter var2 = DSAParameter.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      ASN1Integer var3 = (ASN1Integer)var1.parsePrivateKey();
      this.x = var3.getValue();
      this.dsaSpec = new DSAParameterSpec(var2.getP(), var2.getQ(), var2.getG());
   }

   BCDSAPrivateKey(DSAPrivateKeyParameters var1) {
      this.x = var1.getX();
      this.dsaSpec = new DSAParameterSpec(var1.getParameters().getP(), var1.getParameters().getQ(), var1.getParameters().getG());
   }

   @Override
   public String getAlgorithm() {
      return "DSA";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedPrivateKeyInfo(
         new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()),
         new ASN1Integer(this.getX())
      );
   }

   @Override
   public DSAParams getParams() {
      return this.dsaSpec;
   }

   @Override
   public BigInteger getX() {
      return this.x;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DSAPrivateKey)) {
         return false;
      } else {
         DSAPrivateKey var2 = (DSAPrivateKey)var1;
         return this.getX().equals(var2.getX())
            && this.getParams().getG().equals(var2.getParams().getG())
            && this.getParams().getP().equals(var2.getParams().getP())
            && this.getParams().getQ().equals(var2.getParams().getQ());
      }
   }

   @Override
   public int hashCode() {
      return this.getX().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode();
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
      this.dsaSpec = new DSAParameterSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject(), (BigInteger)var1.readObject());
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.dsaSpec.getP());
      var1.writeObject(this.dsaSpec.getQ());
      var1.writeObject(this.dsaSpec.getG());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      BigInteger var3 = this.getParams().getG().modPow(this.x, this.getParams().getP());
      var1.append("DSA Private Key [").append(DSAUtil.generateKeyFingerprint(var3, this.getParams())).append("]").append(var2);
      var1.append("            Y: ").append(var3.toString(16)).append(var2);
      return var1.toString();
   }
}
