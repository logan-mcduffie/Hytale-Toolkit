package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class JCEECPrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
   private String algorithm = "EC";
   private BigInteger d;
   private ECParameterSpec ecSpec;
   private boolean withCompression;
   private ASN1BitString publicKey;
   private PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected JCEECPrivateKey() {
   }

   public JCEECPrivateKey(ECPrivateKey var1) {
      this.d = var1.getS();
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
   }

   public JCEECPrivateKey(String var1, ECPrivateKeySpec var2) {
      this.algorithm = var1;
      this.d = var2.getD();
      if (var2.getParams() != null) {
         ECCurve var3 = var2.getParams().getCurve();
         EllipticCurve var4 = EC5Util.convertCurve(var3, var2.getParams().getSeed());
         this.ecSpec = EC5Util.convertSpec(var4, var2.getParams());
      } else {
         this.ecSpec = null;
      }
   }

   public JCEECPrivateKey(String var1, java.security.spec.ECPrivateKeySpec var2) {
      this.algorithm = var1;
      this.d = var2.getS();
      this.ecSpec = var2.getParams();
   }

   public JCEECPrivateKey(String var1, JCEECPrivateKey var2) {
      this.algorithm = var1;
      this.d = var2.d;
      this.ecSpec = var2.ecSpec;
      this.withCompression = var2.withCompression;
      this.attrCarrier = var2.attrCarrier;
      this.publicKey = var2.publicKey;
   }

   public JCEECPrivateKey(String var1, ECPrivateKeyParameters var2, JCEECPublicKey var3, ECParameterSpec var4) {
      this.algorithm = var1;
      this.d = var2.getD();
      if (var4 == null) {
         ECDomainParameters var5 = var2.getParameters();
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = new ECParameterSpec(var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH().intValue());
      } else {
         this.ecSpec = var4;
      }

      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public JCEECPrivateKey(String var1, ECPrivateKeyParameters var2, JCEECPublicKey var3, org.bouncycastle.jce.spec.ECParameterSpec var4) {
      this.algorithm = var1;
      this.d = var2.getD();
      if (var4 == null) {
         ECDomainParameters var5 = var2.getParameters();
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = new ECParameterSpec(var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH().intValue());
      } else {
         EllipticCurve var7 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = new ECParameterSpec(var7, EC5Util.convertPoint(var4.getG()), var4.getN(), var4.getH().intValue());
      }

      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public JCEECPrivateKey(String var1, ECPrivateKeyParameters var2) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.ecSpec = null;
   }

   JCEECPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.populateFromPrivKeyInfo(var1);
   }

   private void populateFromPrivKeyInfo(PrivateKeyInfo var1) throws IOException {
      X962Parameters var2 = X962Parameters.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      if (var2.isNamedCurve()) {
         ASN1ObjectIdentifier var3 = ASN1ObjectIdentifier.getInstance(var2.getParameters());
         X9ECParameters var4 = ECUtil.getNamedCurveByOid(var3);
         if (var4 != null) {
            EllipticCurve var5 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
            this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(var3), var5, EC5Util.convertPoint(var4.getG()), var4.getN(), var4.getH());
         }
      } else if (var2.isImplicitlyCA()) {
         this.ecSpec = null;
      } else {
         X9ECParameters var6 = X9ECParameters.getInstance(var2.getParameters());
         EllipticCurve var8 = EC5Util.convertCurve(var6.getCurve(), var6.getSeed());
         this.ecSpec = new ECParameterSpec(var8, EC5Util.convertPoint(var6.getG()), var6.getN(), var6.getH().intValue());
      }

      ASN1Encodable var7 = var1.parsePrivateKey();
      if (var7 instanceof ASN1Integer) {
         ASN1Integer var9 = ASN1Integer.getInstance(var7);
         this.d = var9.getValue();
      } else {
         org.bouncycastle.asn1.sec.ECPrivateKey var10 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(var7);
         this.d = var10.getKey();
         this.publicKey = var10.getPublicKey();
      }
   }

   @Override
   public String getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      X962Parameters var1;
      if (this.ecSpec instanceof ECNamedCurveSpec) {
         ASN1ObjectIdentifier var2 = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
         if (var2 == null) {
            var2 = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
         }

         var1 = new X962Parameters(var2);
      } else if (this.ecSpec == null) {
         var1 = new X962Parameters(DERNull.INSTANCE);
      } else {
         ECCurve var7 = EC5Util.convertCurve(this.ecSpec.getCurve());
         X9ECParameters var3 = new X9ECParameters(
            var7,
            new X9ECPoint(EC5Util.convertPoint(var7, this.ecSpec.getGenerator()), this.withCompression),
            this.ecSpec.getOrder(),
            BigInteger.valueOf(this.ecSpec.getCofactor()),
            this.ecSpec.getCurve().getSeed()
         );
         var1 = new X962Parameters(var3);
      }

      int var4;
      if (this.ecSpec == null) {
         var4 = ECUtil.getOrderBitLength(null, null, this.getS());
      } else {
         var4 = ECUtil.getOrderBitLength(null, this.ecSpec.getOrder(), this.getS());
      }

      org.bouncycastle.asn1.sec.ECPrivateKey var9;
      if (this.publicKey != null) {
         var9 = new org.bouncycastle.asn1.sec.ECPrivateKey(var4, this.getS(), this.publicKey, var1);
      } else {
         var9 = new org.bouncycastle.asn1.sec.ECPrivateKey(var4, this.getS(), var1);
      }

      try {
         PrivateKeyInfo var8;
         if (this.algorithm.equals("ECGOST3410")) {
            var8 = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, var1.toASN1Primitive()), var9.toASN1Primitive());
         } else {
            var8 = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var1.toASN1Primitive()), var9.toASN1Primitive());
         }

         return var8.getEncoded("DER");
      } catch (IOException var6) {
         return null;
      }
   }

   @Override
   public ECParameterSpec getParams() {
      return this.ecSpec;
   }

   @Override
   public org.bouncycastle.jce.spec.ECParameterSpec getParameters() {
      return this.ecSpec == null ? null : EC5Util.convertSpec(this.ecSpec);
   }

   org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
      return this.ecSpec != null ? EC5Util.convertSpec(this.ecSpec) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
   }

   @Override
   public BigInteger getS() {
      return this.d;
   }

   @Override
   public BigInteger getD() {
      return this.d;
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

   @Override
   public void setPointFormat(String var1) {
      this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof JCEECPrivateKey)) {
         return false;
      } else {
         JCEECPrivateKey var2 = (JCEECPrivateKey)var1;
         return this.getD().equals(var2.getD()) && this.engineGetSpec().equals(var2.engineGetSpec());
      }
   }

   @Override
   public int hashCode() {
      return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("EC Private Key").append(var2);
      var1.append("             S: ").append(this.d.toString(16)).append(var2);
      return var1.toString();
   }

   private ASN1BitString getPublicKeyDetails(JCEECPublicKey var1) {
      try {
         SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var1.getEncoded()));
         return var2.getPublicKeyData();
      } catch (IOException var3) {
         return null;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      byte[] var2 = (byte[])var1.readObject();
      this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
      this.algorithm = (String)var1.readObject();
      this.withCompression = var1.readBoolean();
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
      this.attrCarrier.readObject(var1);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.getEncoded());
      var1.writeObject(this.algorithm);
      var1.writeBoolean(this.withCompression);
      this.attrCarrier.writeObject(var1);
   }
}
