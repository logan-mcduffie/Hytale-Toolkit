package org.bouncycastle.jcajce.provider.asymmetric.dstu;

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
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCDSTU4145PrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
   static final long serialVersionUID = 7245981689601667138L;
   private String algorithm = "DSTU4145";
   private boolean withCompression;
   private transient BigInteger d;
   private transient ECParameterSpec ecSpec;
   private transient ASN1BitString publicKey;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCDSTU4145PrivateKey() {
   }

   public BCDSTU4145PrivateKey(ECPrivateKey var1) {
      this.d = var1.getS();
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
   }

   public BCDSTU4145PrivateKey(ECPrivateKeySpec var1) {
      this.d = var1.getD();
      if (var1.getParams() != null) {
         ECCurve var2 = var1.getParams().getCurve();
         EllipticCurve var3 = EC5Util.convertCurve(var2, var1.getParams().getSeed());
         this.ecSpec = EC5Util.convertSpec(var3, var1.getParams());
      } else {
         this.ecSpec = null;
      }
   }

   public BCDSTU4145PrivateKey(java.security.spec.ECPrivateKeySpec var1) {
      this.d = var1.getS();
      this.ecSpec = var1.getParams();
   }

   public BCDSTU4145PrivateKey(BCDSTU4145PrivateKey var1) {
      this.d = var1.d;
      this.ecSpec = var1.ecSpec;
      this.withCompression = var1.withCompression;
      this.attrCarrier = var1.attrCarrier;
      this.publicKey = var1.publicKey;
   }

   public BCDSTU4145PrivateKey(String var1, ECPrivateKeyParameters var2, BCDSTU4145PublicKey var3, ECParameterSpec var4) {
      ECDomainParameters var5 = var2.getParameters();
      this.algorithm = var1;
      this.d = var2.getD();
      if (var4 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = new ECParameterSpec(var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH().intValue());
      } else {
         this.ecSpec = var4;
      }

      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCDSTU4145PrivateKey(String var1, ECPrivateKeyParameters var2, BCDSTU4145PublicKey var3, org.bouncycastle.jce.spec.ECParameterSpec var4) {
      ECDomainParameters var5 = var2.getParameters();
      this.algorithm = var1;
      this.d = var2.getD();
      if (var4 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = new ECParameterSpec(var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH().intValue());
      } else {
         EllipticCurve var7 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = new ECParameterSpec(var7, EC5Util.convertPoint(var4.getG()), var4.getN(), var4.getH().intValue());
      }

      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCDSTU4145PrivateKey(String var1, ECPrivateKeyParameters var2) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.ecSpec = null;
   }

   BCDSTU4145PrivateKey(PrivateKeyInfo var1) throws IOException {
      this.populateFromPrivKeyInfo(var1);
   }

   private void populateFromPrivKeyInfo(PrivateKeyInfo var1) throws IOException {
      X962Parameters var2 = X962Parameters.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      if (var2.isNamedCurve()) {
         ASN1ObjectIdentifier var3 = ASN1ObjectIdentifier.getInstance(var2.getParameters());
         X9ECParameters var4 = ECUtil.getNamedCurveByOid(var3);
         if (var4 == null) {
            ECDomainParameters var5 = DSTU4145NamedCurves.getByOID(var3);
            EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
            this.ecSpec = new ECNamedCurveSpec(var3.getId(), var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH());
         } else {
            EllipticCurve var17 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
            this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(var3), var17, EC5Util.convertPoint(var4.getG()), var4.getN(), var4.getH());
         }
      } else if (var2.isImplicitlyCA()) {
         this.ecSpec = null;
      } else {
         ASN1Sequence var11 = ASN1Sequence.getInstance(var2.getParameters());
         if (var11.getObjectAt(0) instanceof ASN1Integer) {
            X9ECParameters var13 = X9ECParameters.getInstance(var2.getParameters());
            EllipticCurve var18 = EC5Util.convertCurve(var13.getCurve(), var13.getSeed());
            this.ecSpec = new ECParameterSpec(var18, EC5Util.convertPoint(var13.getG()), var13.getN(), var13.getH().intValue());
         } else {
            DSTU4145Params var14 = DSTU4145Params.getInstance(var11);
            Object var19;
            if (var14.isNamedCurve()) {
               ASN1ObjectIdentifier var20 = var14.getNamedCurve();
               ECDomainParameters var7 = DSTU4145NamedCurves.getByOID(var20);
               var19 = new ECNamedCurveParameterSpec(var20.getId(), var7.getCurve(), var7.getG(), var7.getN(), var7.getH(), var7.getSeed());
            } else {
               DSTU4145ECBinary var21 = var14.getECBinary();
               byte[] var23 = var21.getB();
               if (var1.getPrivateKeyAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                  this.reverseBytes(var23);
               }

               DSTU4145BinaryField var8 = var21.getField();
               ECCurve.F2m var9 = new ECCurve.F2m(var8.getM(), var8.getK1(), var8.getK2(), var8.getK3(), var21.getA(), new BigInteger(1, var23), null, null);
               byte[] var10 = var21.getG();
               if (var1.getPrivateKeyAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                  this.reverseBytes(var10);
               }

               var19 = new org.bouncycastle.jce.spec.ECParameterSpec(var9, DSTU4145PointEncoder.decodePoint(var9, var10), var21.getN());
            }

            EllipticCurve var22 = EC5Util.convertCurve(
               ((org.bouncycastle.jce.spec.ECParameterSpec)var19).getCurve(), ((org.bouncycastle.jce.spec.ECParameterSpec)var19).getSeed()
            );
            this.ecSpec = new ECParameterSpec(
               var22,
               EC5Util.convertPoint(((org.bouncycastle.jce.spec.ECParameterSpec)var19).getG()),
               ((org.bouncycastle.jce.spec.ECParameterSpec)var19).getN(),
               ((org.bouncycastle.jce.spec.ECParameterSpec)var19).getH().intValue()
            );
         }
      }

      ASN1Encodable var12 = var1.parsePrivateKey();
      if (var12 instanceof ASN1Integer) {
         ASN1Integer var15 = ASN1Integer.getInstance(var12);
         this.d = var15.getValue();
      } else {
         org.bouncycastle.asn1.sec.ECPrivateKey var16 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(var12);
         this.d = var16.getKey();
         this.publicKey = var16.getPublicKey();
      }
   }

   private void reverseBytes(byte[] var1) {
      for (int var3 = 0; var3 < var1.length / 2; var3++) {
         byte var2 = var1[var3];
         var1[var3] = var1[var1.length - 1 - var3];
         var1[var1.length - 1 - var3] = var2;
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
         ECCurve var7 = EC5Util.convertCurve(this.ecSpec.getCurve());
         X9ECParameters var4 = new X9ECParameters(
            var7,
            new X9ECPoint(EC5Util.convertPoint(var7, this.ecSpec.getGenerator()), this.withCompression),
            this.ecSpec.getOrder(),
            BigInteger.valueOf(this.ecSpec.getCofactor()),
            this.ecSpec.getCurve().getSeed()
         );
         var1 = new X962Parameters(var4);
         var2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
      }

      org.bouncycastle.asn1.sec.ECPrivateKey var9;
      if (this.publicKey != null) {
         var9 = new org.bouncycastle.asn1.sec.ECPrivateKey(var2, this.getS(), this.publicKey, var1);
      } else {
         var9 = new org.bouncycastle.asn1.sec.ECPrivateKey(var2, this.getS(), var1);
      }

      try {
         PrivateKeyInfo var8;
         if (this.algorithm.equals("DSTU4145")) {
            var8 = new PrivateKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, var1.toASN1Primitive()), var9.toASN1Primitive());
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
      if (!(var1 instanceof BCDSTU4145PrivateKey)) {
         return false;
      } else {
         BCDSTU4145PrivateKey var2 = (BCDSTU4145PrivateKey)var1;
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

   private ASN1BitString getPublicKeyDetails(BCDSTU4145PublicKey var1) {
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
