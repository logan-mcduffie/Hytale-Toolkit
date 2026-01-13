package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.internal.asn1.oiw.ElGamalParameter;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jce.spec.ElGamalPrivateKeySpec;

public class JCEElGamalPrivateKey implements ElGamalPrivateKey, DHPrivateKey, PKCS12BagAttributeCarrier {
   static final long serialVersionUID = 4819350091141529678L;
   BigInteger x;
   ElGamalParameterSpec elSpec;
   private PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected JCEElGamalPrivateKey() {
   }

   JCEElGamalPrivateKey(ElGamalPrivateKey var1) {
      this.x = var1.getX();
      this.elSpec = var1.getParameters();
   }

   JCEElGamalPrivateKey(DHPrivateKey var1) {
      this.x = var1.getX();
      this.elSpec = new ElGamalParameterSpec(var1.getParams().getP(), var1.getParams().getG());
   }

   JCEElGamalPrivateKey(ElGamalPrivateKeySpec var1) {
      this.x = var1.getX();
      this.elSpec = new ElGamalParameterSpec(var1.getParams().getP(), var1.getParams().getG());
   }

   JCEElGamalPrivateKey(DHPrivateKeySpec var1) {
      this.x = var1.getX();
      this.elSpec = new ElGamalParameterSpec(var1.getP(), var1.getG());
   }

   JCEElGamalPrivateKey(PrivateKeyInfo var1) throws IOException {
      ElGamalParameter var2 = ElGamalParameter.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      ASN1Integer var3 = ASN1Integer.getInstance(var1.parsePrivateKey());
      this.x = var3.getValue();
      this.elSpec = new ElGamalParameterSpec(var2.getP(), var2.getG());
   }

   JCEElGamalPrivateKey(ElGamalPrivateKeyParameters var1) {
      this.x = var1.getX();
      this.elSpec = new ElGamalParameterSpec(var1.getParameters().getP(), var1.getParameters().getG());
   }

   @Override
   public String getAlgorithm() {
      return "ElGamal";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedPrivateKeyInfo(
         new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())),
         new ASN1Integer(this.getX())
      );
   }

   @Override
   public ElGamalParameterSpec getParameters() {
      return this.elSpec;
   }

   @Override
   public DHParameterSpec getParams() {
      return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
   }

   @Override
   public BigInteger getX() {
      return this.x;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.x = (BigInteger)var1.readObject();
      this.elSpec = new ElGamalParameterSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.getX());
      var1.writeObject(this.elSpec.getP());
      var1.writeObject(this.elSpec.getG());
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
}
