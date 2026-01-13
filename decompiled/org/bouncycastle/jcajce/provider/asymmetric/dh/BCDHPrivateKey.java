package org.bouncycastle.jcajce.provider.asymmetric.dh;

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
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import org.bouncycastle.jcajce.spec.DHExtendedPrivateKeySpec;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class BCDHPrivateKey implements DHPrivateKey, PKCS12BagAttributeCarrier {
   static final long serialVersionUID = 311058815616901812L;
   private BigInteger x;
   private transient DHParameterSpec dhSpec;
   private transient PrivateKeyInfo info;
   private transient DHPrivateKeyParameters dhPrivateKey;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCDHPrivateKey() {
   }

   BCDHPrivateKey(DHPrivateKey var1) {
      this.x = var1.getX();
      this.dhSpec = var1.getParams();
   }

   BCDHPrivateKey(DHPrivateKeySpec var1) {
      this.x = var1.getX();
      if (var1 instanceof DHExtendedPrivateKeySpec) {
         this.dhSpec = ((DHExtendedPrivateKeySpec)var1).getParams();
      } else {
         this.dhSpec = new DHParameterSpec(var1.getP(), var1.getG());
      }
   }

   public BCDHPrivateKey(PrivateKeyInfo var1) throws IOException {
      ASN1Sequence var2 = ASN1Sequence.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      ASN1Integer var3 = (ASN1Integer)var1.parsePrivateKey();
      ASN1ObjectIdentifier var4 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      this.info = var1;
      this.x = var3.getValue();
      if (var4.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
         DHParameter var5 = DHParameter.getInstance(var2);
         if (var5.getL() != null) {
            this.dhSpec = new DHParameterSpec(var5.getP(), var5.getG(), var5.getL().intValue());
            this.dhPrivateKey = new DHPrivateKeyParameters(this.x, new DHParameters(var5.getP(), var5.getG(), null, var5.getL().intValue()));
         } else {
            this.dhSpec = new DHParameterSpec(var5.getP(), var5.getG());
            this.dhPrivateKey = new DHPrivateKeyParameters(this.x, new DHParameters(var5.getP(), var5.getG()));
         }
      } else {
         if (!var4.equals(X9ObjectIdentifiers.dhpublicnumber)) {
            throw new IllegalArgumentException("unknown algorithm type: " + var4);
         }

         DomainParameters var6 = DomainParameters.getInstance(var2);
         this.dhSpec = new DHDomainParameterSpec(var6.getP(), var6.getQ(), var6.getG(), var6.getJ(), 0);
         this.dhPrivateKey = new DHPrivateKeyParameters(this.x, new DHParameters(var6.getP(), var6.getG(), var6.getQ(), var6.getJ(), null));
      }
   }

   BCDHPrivateKey(DHPrivateKeyParameters var1) {
      this.x = var1.getX();
      this.dhSpec = new DHDomainParameterSpec(var1.getParameters());
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
            PrivateKeyInfo var1;
            if (this.dhSpec instanceof DHDomainParameterSpec && ((DHDomainParameterSpec)this.dhSpec).getQ() != null) {
               DHParameters var2 = ((DHDomainParameterSpec)this.dhSpec).getDomainParameters();
               DHValidationParameters var3 = var2.getValidationParameters();
               ValidationParams var4 = null;
               if (var3 != null) {
                  var4 = new ValidationParams(var3.getSeed(), var3.getCounter());
               }

               var1 = new PrivateKeyInfo(
                  new AlgorithmIdentifier(
                     X9ObjectIdentifiers.dhpublicnumber, new DomainParameters(var2.getP(), var2.getG(), var2.getQ(), var2.getJ(), var4).toASN1Primitive()
                  ),
                  new ASN1Integer(this.getX())
               );
            } else {
               var1 = new PrivateKeyInfo(
                  new AlgorithmIdentifier(
                     PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()
                  ),
                  new ASN1Integer(this.getX())
               );
            }

            return var1.getEncoded("DER");
         }
      } catch (Exception var5) {
         return null;
      }
   }

   @Override
   public String toString() {
      return DHUtil.privateKeyToString("DH", this.x, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
   }

   @Override
   public DHParameterSpec getParams() {
      return this.dhSpec;
   }

   @Override
   public BigInteger getX() {
      return this.x;
   }

   DHPrivateKeyParameters engineGetKeyParameters() {
      if (this.dhPrivateKey != null) {
         return this.dhPrivateKey;
      } else {
         return this.dhSpec instanceof DHDomainParameterSpec
            ? new DHPrivateKeyParameters(this.x, ((DHDomainParameterSpec)this.dhSpec).getDomainParameters())
            : new DHPrivateKeyParameters(this.x, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG(), null, this.dhSpec.getL()));
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DHPrivateKey)) {
         return false;
      } else {
         DHPrivateKey var2 = (DHPrivateKey)var1;
         return this.getX().equals(var2.getX())
            && this.getParams().getG().equals(var2.getParams().getG())
            && this.getParams().getP().equals(var2.getParams().getP())
            && this.getParams().getL() == var2.getParams().getL();
      }
   }

   @Override
   public int hashCode() {
      return this.getX().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
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
      this.dhSpec = new DHParameterSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject(), var1.readInt());
      this.info = null;
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.dhSpec.getP());
      var1.writeObject(this.dhSpec.getG());
      var1.writeInt(this.dhSpec.getL());
   }
}
