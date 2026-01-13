package org.bouncycastle.jcajce.provider.asymmetric.ec;

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
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;

public class BCECPrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder, BCKey {
   static final long serialVersionUID = 994553197664784084L;
   private String algorithm = "EC";
   private boolean withCompression;
   private transient BigInteger d;
   private transient ECParameterSpec ecSpec;
   private transient ProviderConfiguration configuration;
   private transient ASN1BitString publicKey;
   private transient PrivateKeyInfo privateKeyInfo;
   private transient byte[] encoding;
   private transient ECPrivateKeyParameters baseKey;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCECPrivateKey() {
   }

   public BCECPrivateKey(ECPrivateKey var1, ProviderConfiguration var2) {
      this.d = var1.getS();
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
      this.configuration = var2;
      this.baseKey = convertToBaseKey(this);
   }

   public BCECPrivateKey(String var1, ECPrivateKeySpec var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      this.d = var2.getD();
      if (var2.getParams() != null) {
         ECCurve var4 = var2.getParams().getCurve();
         EllipticCurve var5 = EC5Util.convertCurve(var4, var2.getParams().getSeed());
         this.ecSpec = EC5Util.convertSpec(var5, var2.getParams());
      } else {
         this.ecSpec = null;
      }

      this.configuration = var3;
      this.baseKey = convertToBaseKey(this);
   }

   public BCECPrivateKey(String var1, java.security.spec.ECPrivateKeySpec var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      this.d = var2.getS();
      this.ecSpec = var2.getParams();
      this.configuration = var3;
      this.baseKey = convertToBaseKey(this);
   }

   public BCECPrivateKey(String var1, BCECPrivateKey var2) {
      this.algorithm = var1;
      this.d = var2.d;
      this.ecSpec = var2.ecSpec;
      this.withCompression = var2.withCompression;
      this.attrCarrier = var2.attrCarrier;
      this.publicKey = var2.publicKey;
      this.configuration = var2.configuration;
      this.baseKey = var2.baseKey;
   }

   public BCECPrivateKey(String var1, ECPrivateKeyParameters var2, BCECPublicKey var3, ECParameterSpec var4, ProviderConfiguration var5) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.configuration = var5;
      this.baseKey = var2;
      if (var4 == null) {
         ECDomainParameters var6 = var2.getParameters();
         EllipticCurve var7 = EC5Util.convertCurve(var6.getCurve(), var6.getSeed());
         this.ecSpec = new ECParameterSpec(var7, EC5Util.convertPoint(var6.getG()), var6.getN(), var6.getH().intValue());
      } else {
         this.ecSpec = var4;
      }

      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCECPrivateKey(
      String var1, ECPrivateKeyParameters var2, BCECPublicKey var3, org.bouncycastle.jce.spec.ECParameterSpec var4, ProviderConfiguration var5
   ) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.configuration = var5;
      this.baseKey = var2;
      if (var4 == null) {
         ECDomainParameters var6 = var2.getParameters();
         EllipticCurve var7 = EC5Util.convertCurve(var6.getCurve(), var6.getSeed());
         this.ecSpec = new ECParameterSpec(var7, EC5Util.convertPoint(var6.getG()), var6.getN(), var6.getH().intValue());
      } else {
         EllipticCurve var9 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = EC5Util.convertSpec(var9, var4);
      }

      try {
         this.publicKey = this.getPublicKeyDetails(var3);
      } catch (Exception var8) {
         this.publicKey = null;
      }
   }

   public BCECPrivateKey(String var1, ECPrivateKeyParameters var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.ecSpec = null;
      this.configuration = var3;
      this.baseKey = var2;
   }

   BCECPrivateKey(String var1, PrivateKeyInfo var2, ProviderConfiguration var3) throws IOException {
      this.algorithm = var1;
      this.configuration = var3;
      this.populateFromPrivKeyInfo(var2);
   }

   private void populateFromPrivKeyInfo(PrivateKeyInfo var1) throws IOException {
      X962Parameters var2 = X962Parameters.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      ECCurve var3 = EC5Util.getCurve(this.configuration, var2);
      this.ecSpec = EC5Util.convertToSpec(var2, var3);
      ASN1Encodable var4 = var1.parsePrivateKey();
      if (var4 instanceof ASN1Integer) {
         ASN1Integer var5 = ASN1Integer.getInstance(var4);
         this.d = var5.getValue();
      } else {
         org.bouncycastle.asn1.sec.ECPrivateKey var6 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(var4);
         this.d = var6.getKey();
         this.publicKey = var6.getPublicKey();
      }

      this.baseKey = convertToBaseKey(this);
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
      if (this.encoding == null) {
         PrivateKeyInfo var1 = this.getPrivateKeyInfo();
         if (var1 == null) {
            return null;
         }

         try {
            this.encoding = var1.getEncoded("DER");
         } catch (IOException var3) {
            return null;
         }
      }

      return Arrays.clone(this.encoding);
   }

   private PrivateKeyInfo getPrivateKeyInfo() {
      if (this.privateKeyInfo == null) {
         X962Parameters var1 = ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression);
         int var2;
         if (this.ecSpec == null) {
            var2 = ECUtil.getOrderBitLength(this.configuration, null, this.getS());
         } else {
            var2 = ECUtil.getOrderBitLength(this.configuration, this.ecSpec.getOrder(), this.getS());
         }

         org.bouncycastle.asn1.sec.ECPrivateKey var3;
         if (this.publicKey != null) {
            var3 = new org.bouncycastle.asn1.sec.ECPrivateKey(var2, this.getS(), this.publicKey, var1);
         } else {
            var3 = new org.bouncycastle.asn1.sec.ECPrivateKey(var2, this.getS(), var1);
         }

         try {
            this.privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var1), var3);
         } catch (IOException var5) {
            return null;
         }
      }

      return this.privateKeyInfo;
   }

   public ECPrivateKeyParameters engineGetKeyParameters() {
      return this.baseKey;
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
      return this.ecSpec != null ? EC5Util.convertSpec(this.ecSpec) : this.configuration.getEcImplicitlyCa();
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
      if (var1 instanceof ECPrivateKey) {
         ECPrivateKey var2 = (ECPrivateKey)var1;
         PrivateKeyInfo var3 = this.getPrivateKeyInfo();
         PrivateKeyInfo var4 = var2 instanceof BCECPrivateKey ? ((BCECPrivateKey)var2).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(var2.getEncoded());
         if (var3 != null && var4 != null) {
            try {
               boolean var5 = Arrays.constantTimeAreEqual(var3.getPrivateKeyAlgorithm().getEncoded(), var4.getPrivateKeyAlgorithm().getEncoded());
               boolean var6 = Arrays.constantTimeAreEqual(this.getS().toByteArray(), var2.getS().toByteArray());
               return var5 & var6;
            } catch (IOException var7) {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
   }

   @Override
   public String toString() {
      return ECUtil.privateKeyToString("EC", this.d, this.engineGetSpec());
   }

   private ASN1BitString getPublicKeyDetails(BCECPublicKey var1) {
      try {
         SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var1.getEncoded()));
         return var2.getPublicKeyData();
      } catch (IOException var3) {
         return null;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.configuration = BouncyCastleProvider.CONFIGURATION;
      this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }

   private static ECPrivateKeyParameters convertToBaseKey(BCECPrivateKey var0) {
      org.bouncycastle.jce.spec.ECParameterSpec var2 = var0.getParameters();
      if (var2 == null) {
         var2 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
      }

      if (var0.getParameters() instanceof ECNamedCurveParameterSpec) {
         String var3 = ((ECNamedCurveParameterSpec)var0.getParameters()).getName();
         if (var3 != null) {
            return new ECPrivateKeyParameters(
               var0.getD(), new ECNamedDomainParameters(ECNamedCurveTable.getOID(var3), var2.getCurve(), var2.getG(), var2.getN(), var2.getH(), var2.getSeed())
            );
         }
      }

      return new ECPrivateKeyParameters(var0.getD(), new ECDomainParameters(var2.getCurve(), var2.getG(), var2.getN(), var2.getH(), var2.getSeed()));
   }
}
