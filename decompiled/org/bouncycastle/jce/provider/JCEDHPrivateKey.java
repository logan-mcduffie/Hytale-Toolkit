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
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class JCEDHPrivateKey implements DHPrivateKey, PKCS12BagAttributeCarrier {
   static final long serialVersionUID = 311058815616901812L;
   BigInteger x;
   private DHParameterSpec dhSpec;
   private PrivateKeyInfo info;
   private PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected JCEDHPrivateKey() {
   }

   JCEDHPrivateKey(DHPrivateKey var1) {
      this.x = var1.getX();
      this.dhSpec = var1.getParams();
   }

   JCEDHPrivateKey(DHPrivateKeySpec var1) {
      this.x = var1.getX();
      this.dhSpec = new DHParameterSpec(var1.getP(), var1.getG());
   }

   JCEDHPrivateKey(PrivateKeyInfo var1) throws IOException {
      ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      ASN1Integer var3 = ASN1Integer.getInstance(var1.parsePrivateKey());
      ASN1ObjectIdentifier var4 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      this.info = var1;
      this.x = var3.getValue();
      if (var4.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         DHParameter var5 = DHParameter.getInstance(var2);
         if (var5.getL() != null) {
            this.dhSpec = new DHParameterSpec(var5.getP(), var5.getG(), var5.getL().intValue());
         } else {
            this.dhSpec = new DHParameterSpec(var5.getP(), var5.getG());
         }
      } else {
         if (!var4.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            throw new IllegalArgumentException("unknown algorithm type: " + var4);
         }

         DomainParameters var6 = DomainParameters.getInstance(var2);
         this.dhSpec = new DHParameterSpec(var6.getP(), var6.getG());
      }
   }

   JCEDHPrivateKey(DHPrivateKeyParameters var1) {
      this.x = var1.getX();
      this.dhSpec = new DHParameterSpec(var1.getParameters().getP(), var1.getParameters().getG(), var1.getParameters().getL());
   }

   @Override
   public String getAlgorithm() {
      return "DH";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      try {
         if (this.info != null) {
            return this.info.getEncoded("DER");
         } else {
            PrivateKeyInfo var1 = new PrivateKeyInfo(
               new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL())),
               new ASN1Integer(this.getX())
            );
            return var1.getEncoded("DER");
         }
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public DHParameterSpec getParams() {
      return this.dhSpec;
   }

   @Override
   public BigInteger getX() {
      return this.x;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.x = (BigInteger)var1.readObject();
      this.dhSpec = new DHParameterSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject(), var1.readInt());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.getX());
      var1.writeObject(this.dhSpec.getP());
      var1.writeObject(this.dhSpec.getG());
      var1.writeInt(this.dhSpec.getL());
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
