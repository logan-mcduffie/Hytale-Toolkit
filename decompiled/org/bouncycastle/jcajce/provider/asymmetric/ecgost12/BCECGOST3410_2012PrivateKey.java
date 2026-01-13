package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

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
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
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
import org.bouncycastle.util.Arrays;

public class BCECGOST3410_2012PrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
   static final long serialVersionUID = 7245981689601667138L;
   private String algorithm = "ECGOST3410-2012";
   private boolean withCompression;
   private transient GOST3410PublicKeyAlgParameters gostParams;
   private transient BigInteger d;
   private transient ECParameterSpec ecSpec;
   private transient ASN1BitString publicKey;
   private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

   protected BCECGOST3410_2012PrivateKey() {
   }

   public BCECGOST3410_2012PrivateKey(ECPrivateKey var1) {
      this.d = var1.getS();
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
   }

   public BCECGOST3410_2012PrivateKey(ECPrivateKeySpec var1) {
      this.d = var1.getD();
      if (var1.getParams() != null) {
         ECCurve var2 = var1.getParams().getCurve();
         EllipticCurve var3 = EC5Util.convertCurve(var2, var1.getParams().getSeed());
         this.ecSpec = EC5Util.convertSpec(var3, var1.getParams());
      } else {
         this.ecSpec = null;
      }
   }

   public BCECGOST3410_2012PrivateKey(java.security.spec.ECPrivateKeySpec var1) {
      this.d = var1.getS();
      this.ecSpec = var1.getParams();
   }

   public BCECGOST3410_2012PrivateKey(BCECGOST3410_2012PrivateKey var1) {
      this.d = var1.d;
      this.ecSpec = var1.ecSpec;
      this.withCompression = var1.withCompression;
      this.attrCarrier = var1.attrCarrier;
      this.publicKey = var1.publicKey;
      this.gostParams = var1.gostParams;
   }

   public BCECGOST3410_2012PrivateKey(String var1, ECPrivateKeyParameters var2, BCECGOST3410_2012PublicKey var3, ECParameterSpec var4) {
      ECDomainParameters var5 = var2.getParameters();
      this.algorithm = var1;
      this.d = var2.getD();
      if (var4 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = new ECParameterSpec(var6, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH().intValue());
      } else {
         this.ecSpec = var4;
      }

      this.gostParams = var3.getGostParams();
      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCECGOST3410_2012PrivateKey(String var1, ECPrivateKeyParameters var2, BCECGOST3410_2012PublicKey var3, org.bouncycastle.jce.spec.ECParameterSpec var4) {
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

      this.gostParams = var3.getGostParams();
      this.publicKey = this.getPublicKeyDetails(var3);
   }

   public BCECGOST3410_2012PrivateKey(String var1, ECPrivateKeyParameters var2) {
      this.algorithm = var1;
      this.d = var2.getD();
      this.ecSpec = null;
   }

   BCECGOST3410_2012PrivateKey(PrivateKeyInfo var1) throws IOException {
      this.populateFromPrivKeyInfo(var1);
   }

   private void populateFromPrivKeyInfo(PrivateKeyInfo var1) throws IOException {
      ASN1Primitive var2 = var1.getPrivateKeyAlgorithm().getParameters().toASN1Primitive();
      if (var2 instanceof ASN1Sequence && ASN1Sequence.getInstance(var2).size() <= 3) {
         this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
         ECNamedCurveParameterSpec var9 = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
         ECCurve var12 = var9.getCurve();
         EllipticCurve var16 = EC5Util.convertCurve(var12, var9.getSeed());
         this.ecSpec = new ECNamedCurveSpec(
            ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), var16, EC5Util.convertPoint(var9.getG()), var9.getN(), var9.getH()
         );
         ASN1OctetString var18 = var1.getPrivateKey();
         if (var18.getOctets().length != 32 && var18.getOctets().length != 64) {
            ASN1Encodable var19 = var1.parsePrivateKey();
            if (var19 instanceof ASN1Integer) {
               this.d = ASN1Integer.getInstance(var19).getPositiveValue();
            } else {
               byte[] var8 = ASN1OctetString.getInstance(var19).getOctets();
               this.d = new BigInteger(1, Arrays.reverse(var8));
            }
         } else {
            this.d = new BigInteger(1, Arrays.reverse(var18.getOctets()));
         }
      } else {
         X962Parameters var3 = X962Parameters.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
         if (var3.isNamedCurve()) {
            ASN1ObjectIdentifier var4 = ASN1ObjectIdentifier.getInstance(var3.getParameters());
            X9ECParameters var5 = ECUtil.getNamedCurveByOid(var4);
            if (var5 == null) {
               X9ECParameters var6 = ECGOST3410NamedCurves.getByOIDX9(var4);
               EllipticCurve var7 = EC5Util.convertCurve(var6.getCurve(), var6.getSeed());
               this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(var4), var7, EC5Util.convertPoint(var6.getG()), var6.getN(), var6.getH());
            } else {
               EllipticCurve var17 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
               this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(var4), var17, EC5Util.convertPoint(var5.getG()), var5.getN(), var5.getH());
            }
         } else if (var3.isImplicitlyCA()) {
            this.ecSpec = null;
         } else {
            X9ECParameters var10 = X9ECParameters.getInstance(var3.getParameters());
            EllipticCurve var13 = EC5Util.convertCurve(var10.getCurve(), var10.getSeed());
            this.ecSpec = new ECParameterSpec(var13, EC5Util.convertPoint(var10.getG()), var10.getN(), var10.getH().intValue());
         }

         ASN1Encodable var11 = var1.parsePrivateKey();
         if (var11 instanceof ASN1Integer) {
            ASN1Integer var14 = ASN1Integer.getInstance(var11);
            this.d = var14.getValue();
         } else {
            org.bouncycastle.asn1.sec.ECPrivateKey var15 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(var11);
            this.d = var15.getKey();
            this.publicKey = var15.getPublicKey();
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
      boolean var1 = this.d.bitLength() > 256;
      ASN1ObjectIdentifier var2 = var1 ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
      int var3 = var1 ? 64 : 32;
      if (this.gostParams != null) {
         byte[] var11 = new byte[var3];
         this.extractBytes(var11, var3, 0, this.getS());

         try {
            PrivateKeyInfo var12 = new PrivateKeyInfo(new AlgorithmIdentifier(var2, this.gostParams), new DEROctetString(var11));
            return var12.getEncoded("DER");
         } catch (IOException var9) {
            return null;
         }
      } else {
         X962Parameters var4;
         int var5;
         if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier var6 = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (var6 == null) {
               var6 = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }

            var4 = new X962Parameters(var6);
            var5 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
         } else if (this.ecSpec == null) {
            var4 = new X962Parameters(DERNull.INSTANCE);
            var5 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, this.getS());
         } else {
            ECCurve var13 = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters var7 = new X9ECParameters(
               var13,
               new X9ECPoint(EC5Util.convertPoint(var13, this.ecSpec.getGenerator()), this.withCompression),
               this.ecSpec.getOrder(),
               BigInteger.valueOf(this.ecSpec.getCofactor()),
               this.ecSpec.getCurve().getSeed()
            );
            var4 = new X962Parameters(var7);
            var5 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
         }

         org.bouncycastle.asn1.sec.ECPrivateKey var15;
         if (this.publicKey != null) {
            var15 = new org.bouncycastle.asn1.sec.ECPrivateKey(var5, this.getS(), this.publicKey, var4);
         } else {
            var15 = new org.bouncycastle.asn1.sec.ECPrivateKey(var5, this.getS(), var4);
         }

         try {
            PrivateKeyInfo var14 = new PrivateKeyInfo(new AlgorithmIdentifier(var2, var4.toASN1Primitive()), var15.toASN1Primitive());
            return var14.getEncoded("DER");
         } catch (IOException var10) {
            return null;
         }
      }
   }

   private void extractBytes(byte[] var1, int var2, int var3, BigInteger var4) {
      byte[] var5 = var4.toByteArray();
      if (var5.length < var2) {
         byte[] var6 = new byte[var2];
         System.arraycopy(var5, 0, var6, var6.length - var5.length, var5.length);
         var5 = var6;
      }

      for (int var7 = 0; var7 != var2; var7++) {
         var1[var3 + var7] = var5[var5.length - 1 - var7];
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
      if (!(var1 instanceof BCECGOST3410_2012PrivateKey)) {
         return false;
      } else {
         BCECGOST3410_2012PrivateKey var2 = (BCECGOST3410_2012PrivateKey)var1;
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

   private ASN1BitString getPublicKeyDetails(BCECGOST3410_2012PublicKey var1) {
      SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(var1.getEncoded());
      return var2.getPublicKeyData();
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
