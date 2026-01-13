package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.GOST3410Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class BCGOST3410PrivateKey implements GOST3410PrivateKey, PKCS12BagAttributeCarrier {
   static final long serialVersionUID = 8581661527592305464L;
   private BigInteger x;
   private transient GOST3410Params gost3410Spec;
   private transient PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCGOST3410PrivateKey() {
   }

   BCGOST3410PrivateKey(GOST3410PrivateKey var1) {
      this.x = var1.getX();
      this.gost3410Spec = var1.getParameters();
   }

   BCGOST3410PrivateKey(GOST3410PrivateKeySpec var1) {
      this.x = var1.getX();
      this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(var1.getP(), var1.getQ(), var1.getA()));
   }

   BCGOST3410PrivateKey(PrivateKeyInfo var1) throws IOException {
      GOST3410PublicKeyAlgParameters var2 = GOST3410PublicKeyAlgParameters.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      ASN1Encodable var3 = var1.parsePrivateKey();
      if (var3 instanceof ASN1Integer) {
         this.x = ASN1Integer.getInstance(var3).getPositiveValue();
      } else {
         ASN1OctetString var4 = ASN1OctetString.getInstance(var1.parsePrivateKey());
         byte[] var5 = var4.getOctets();
         byte[] var6 = new byte[var5.length];

         for (int var7 = 0; var7 != var5.length; var7++) {
            var6[var7] = var5[var5.length - 1 - var7];
         }

         this.x = new BigInteger(1, var6);
      }

      this.gost3410Spec = GOST3410ParameterSpec.fromPublicKeyAlg(var2);
   }

   BCGOST3410PrivateKey(GOST3410PrivateKeyParameters var1, GOST3410ParameterSpec var2) {
      this.x = var1.getX();
      this.gost3410Spec = var2;
      if (var2 == null) {
         throw new IllegalArgumentException("spec is null");
      }
   }

   @Override
   public String getAlgorithm() {
      return "GOST3410";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      byte[] var2 = this.getX().toByteArray();
      byte[] var3;
      if (var2[0] == 0) {
         var3 = new byte[var2.length - 1];
      } else {
         var3 = new byte[var2.length];
      }

      for (int var4 = 0; var4 != var3.length; var4++) {
         var3[var4] = var2[var2.length - 1 - var4];
      }

      try {
         PrivateKeyInfo var1;
         if (this.gost3410Spec instanceof GOST3410ParameterSpec) {
            var1 = new PrivateKeyInfo(
               new AlgorithmIdentifier(
                  CryptoProObjectIdentifiers.gostR3410_94,
                  new GOST3410PublicKeyAlgParameters(
                     new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID())
                  )
               ),
               new DEROctetString(var3)
            );
         } else {
            var1 = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), new DEROctetString(var3));
         }

         return var1.getEncoded("DER");
      } catch (IOException var5) {
         return null;
      }
   }

   @Override
   public GOST3410Params getParameters() {
      return this.gost3410Spec;
   }

   @Override
   public BigInteger getX() {
      return this.x;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof GOST3410PrivateKey)) {
         return false;
      } else {
         GOST3410PrivateKey var2 = (GOST3410PrivateKey)var1;
         return this.getX().equals(var2.getX())
            && this.getParameters().getPublicKeyParameters().equals(var2.getParameters().getPublicKeyParameters())
            && this.compareObj(this.getParameters().getDigestParamSetOID(), var2.getParameters().getDigestParamSetOID())
            && this.compareObj(this.getParameters().getEncryptionParamSetOID(), var2.getParameters().getEncryptionParamSetOID());
      }
   }

   private boolean compareObj(Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else {
         return var1 == null ? false : var1.equals(var2);
      }
   }

   @Override
   public int hashCode() {
      return this.getX().hashCode() ^ this.gost3410Spec.hashCode();
   }

   @Override
   public String toString() {
      try {
         return GOSTUtil.privateKeyToString("GOST3410", this.x, ((GOST3410PrivateKeyParameters)GOST3410Util.generatePrivateKeyParameter(this)).getParameters());
      } catch (InvalidKeyException var2) {
         throw new IllegalStateException(var2.getMessage());
      }
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
      String var2 = (String)var1.readObject();
      if (var2 != null) {
         this.gost3410Spec = new GOST3410ParameterSpec(var2, (String)var1.readObject(), (String)var1.readObject());
      } else {
         this.gost3410Spec = new GOST3410ParameterSpec(
            new GOST3410PublicKeyParameterSetSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject(), (BigInteger)var1.readObject())
         );
         var1.readObject();
         var1.readObject();
      }

      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.gost3410Spec.getPublicKeyParamSetOID() != null) {
         var1.writeObject(this.gost3410Spec.getPublicKeyParamSetOID());
         var1.writeObject(this.gost3410Spec.getDigestParamSetOID());
         var1.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
      } else {
         var1.writeObject(null);
         var1.writeObject(this.gost3410Spec.getPublicKeyParameters().getP());
         var1.writeObject(this.gost3410Spec.getPublicKeyParameters().getQ());
         var1.writeObject(this.gost3410Spec.getPublicKeyParameters().getA());
         var1.writeObject(this.gost3410Spec.getDigestParamSetOID());
         var1.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
      }
   }
}
