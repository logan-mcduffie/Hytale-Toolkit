package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

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
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCECGOST3410PrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
   static final long serialVersionUID = 7245981689601667138L;
   private String algorithm = "ECGOST3410";
   private boolean withCompression;
   private transient ASN1Encodable gostParams;
   private transient BigInteger d;
   private transient ECParameterSpec ecSpec;
   private transient ASN1BitString publicKey;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCECGOST3410PrivateKey() {
   }

   public BCECGOST3410PrivateKey(ECPrivateKey var1) {
      this.d = var1.getS();
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
   }

   public BCECGOST3410PrivateKey(ECPrivateKeySpec var1) {
      this.d = var1.getD();
      if (var1.getParams() != null) {
         ECCurve var2 = var1.getParams().getCurve();
         EllipticCurve var3 = EC5Util.convertCurve(var2, var1.getParams().getSeed());
         this.ecSpec = EC5Util.convertSpec(var3, var1.getParams());
      } else {
         this.ecSpec = null;
      }
   }

   public BCECGOST3410PrivateKey(java.security.spec.ECPrivateKeySpec var1) {
      this.d = var1.getS();
      this.ecSpec = var1.getParams();
   }

   public BCECGOST3410PrivateKey(BCECGOST3410PrivateKey var1) {
      this.d = var1.d;
      this.ecSpec = var1.ecSpec;
      this.withCompression = var1.withCompression;
      this.attrCarrier = var1.attrCarrier;
      this.publicKey = var1.publicKey;
      this.gostParams = var1.gostParams;
   }

   public BCECGOST3410PrivateKey(String var1, ECPrivateKeyParameters var2, BCECGOST3410PublicKey var3, ECParameterSpec var4) {
      this.algorithm = var1;
      this.d = var2.getD();
      if (var4 == null) {
         ECDomainParameters var5 = var2.getParameters();
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = new ECParameterSpec(var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH().intValue());
      } else {
         this.ecSpec = var4;
      }

      this.gostParams = var3.getGostParams();
      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCECGOST3410PrivateKey(String var1, ECPrivateKeyParameters var2, BCECGOST3410PublicKey var3, org.bouncycastle.jce.spec.ECParameterSpec var4) {
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

      this.gostParams = var3.getGostParams();
      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCECGOST3410PrivateKey(String var1, ECPrivateKeyParameters var2) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.ecSpec = null;
   }

   BCECGOST3410PrivateKey(PrivateKeyInfo var1) throws IOException {
      this.populateFromPrivKeyInfo(var1);
   }

   private void populateFromPrivKeyInfo(PrivateKeyInfo var1) throws IOException {
      AlgorithmIdentifier var2 = var1.getPrivateKeyAlgorithm();
      ASN1Encodable var3 = var2.getParameters();
      ASN1Primitive var4 = var3.toASN1Primitive();
      if (var4 instanceof ASN1Sequence && (ASN1Sequence.getInstance(var4).size() == 2 || ASN1Sequence.getInstance(var4).size() == 3)) {
         GOST3410PublicKeyAlgParameters var13 = GOST3410PublicKeyAlgParameters.getInstance(var3);
         this.gostParams = var13;
         ECNamedCurveParameterSpec var16 = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(var13.getPublicKeyParamSet()));
         ECCurve var20 = var16.getCurve();
         EllipticCurve var21 = EC5Util.convertCurve(var20, var16.getSeed());
         this.ecSpec = new ECNamedCurveSpec(
            ECGOST3410NamedCurves.getName(var13.getPublicKeyParamSet()), var21, EC5Util.convertPoint(var16.getG()), var16.getN(), var16.getH()
         );
         ASN1Encodable var22 = var1.parsePrivateKey();
         if (var22 instanceof ASN1Integer) {
            this.d = ASN1Integer.getInstance(var22).getPositiveValue();
         } else {
            byte[] var10 = ASN1OctetString.getInstance(var22).getOctets();
            byte[] var11 = new byte[var10.length];

            for (int var12 = 0; var12 != var10.length; var12++) {
               var11[var12] = var10[var10.length - 1 - var12];
            }

            this.d = new BigInteger(1, var11);
         }
      } else {
         X962Parameters var5 = X962Parameters.getInstance(var3);
         if (var5.isNamedCurve()) {
            ASN1ObjectIdentifier var6 = ASN1ObjectIdentifier.getInstance(var5.getParameters());
            X9ECParameters var7 = ECUtil.getNamedCurveByOid(var6);
            if (var7 == null) {
               throw new IllegalStateException();
            }

            String var8 = ECUtil.getCurveName(var6);
            EllipticCurve var9 = EC5Util.convertCurve(var7.getCurve(), var7.getSeed());
            this.ecSpec = new ECNamedCurveSpec(var8, var9, EC5Util.convertPoint(var7.getG()), var7.getN(), var7.getH());
         } else if (var5.isImplicitlyCA()) {
            this.ecSpec = null;
         } else {
            X9ECParameters var14 = X9ECParameters.getInstance(var5.getParameters());
            EllipticCurve var17 = EC5Util.convertCurve(var14.getCurve(), var14.getSeed());
            this.ecSpec = new ECParameterSpec(var17, EC5Util.convertPoint(var14.getG()), var14.getN(), var14.getH().intValue());
         }

         ASN1Encodable var15 = var1.parsePrivateKey();
         if (var15 instanceof ASN1Integer) {
            ASN1Integer var18 = ASN1Integer.getInstance(var15);
            this.d = var18.getValue();
         } else {
            org.bouncycastle.asn1.sec.ECPrivateKey var19 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(var15);
            this.d = var19.getKey();
            this.publicKey = var19.getPublicKey();
         }
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
      if (this.gostParams != null) {
         byte[] var8 = new byte[32];
         this.extractBytes(var8, 0, this.getS());

         try {
            PrivateKeyInfo var9 = new PrivateKeyInfo(
               new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, this.gostParams), new DEROctetString(var8)
            );
            return var9.getEncoded("DER");
         } catch (IOException var6) {
            return null;
         }
      } else {
         X962Parameters var1;
         int var2;
         if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier var3 = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (var3 == null) {
               var3 = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }

            var1 = new X962Parameters(var3);
            var2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
         } else if (this.ecSpec == null) {
            var1 = new X962Parameters(DERNull.INSTANCE);
            var2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, this.getS());
         } else {
            ECCurve var10 = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters var4 = new X9ECParameters(
               var10,
               new X9ECPoint(EC5Util.convertPoint(var10, this.ecSpec.getGenerator()), this.withCompression),
               this.ecSpec.getOrder(),
               BigInteger.valueOf(this.ecSpec.getCofactor()),
               this.ecSpec.getCurve().getSeed()
            );
            var1 = new X962Parameters(var4);
            var2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
         }

         org.bouncycastle.asn1.sec.ECPrivateKey var12;
         if (this.publicKey != null) {
            var12 = new org.bouncycastle.asn1.sec.ECPrivateKey(var2, this.getS(), this.publicKey, var1);
         } else {
            var12 = new org.bouncycastle.asn1.sec.ECPrivateKey(var2, this.getS(), var1);
         }

         try {
            PrivateKeyInfo var11 = new PrivateKeyInfo(
               new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, var1.toASN1Primitive()), var12.toASN1Primitive()
            );
            return var11.getEncoded("DER");
         } catch (IOException var7) {
            return null;
         }
      }
   }

   private void extractBytes(byte[] var1, int var2, BigInteger var3) {
      byte[] var4 = var3.toByteArray();
      if (var4.length < 32) {
         byte[] var5 = new byte[32];
         System.arraycopy(var4, 0, var5, var5.length - var4.length, var4.length);
         var4 = var5;
      }

      for (int var6 = 0; var6 != 32; var6++) {
         var1[var2 + var6] = var4[var4.length - 1 - var6];
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
      if (!(var1 instanceof BCECGOST3410PrivateKey)) {
         return false;
      } else {
         BCECGOST3410PrivateKey var2 = (BCECGOST3410PrivateKey)var1;
         return this.getD().equals(var2.getD()) && this.engineGetSpec().equals(var2.engineGetSpec());
      }
   }

   @Override
   public int hashCode() {
      return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
   }

   @Override
   public String toString() {
      return ECUtil.privateKeyToString(this.algorithm, this.d, this.engineGetSpec());
   }

   private ASN1BitString getPublicKeyDetails(BCECGOST3410PublicKey var1) {
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
      this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
      this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
