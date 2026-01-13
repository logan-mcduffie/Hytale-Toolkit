package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCECGOST3410_2012PublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
   static final long serialVersionUID = 7026240464295649314L;
   private String algorithm = "ECGOST3410-2012";
   private boolean withCompression;
   private transient ECPublicKeyParameters ecPublicKey;
   private transient ECParameterSpec ecSpec;
   private transient GOST3410PublicKeyAlgParameters gostParams;

   public BCECGOST3410_2012PublicKey(BCECGOST3410_2012PublicKey var1) {
      this.ecPublicKey = var1.ecPublicKey;
      this.ecSpec = var1.ecSpec;
      this.withCompression = var1.withCompression;
      this.gostParams = var1.gostParams;
   }

   public BCECGOST3410_2012PublicKey(ECPublicKeySpec var1) {
      this.ecSpec = var1.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var1.getW()), EC5Util.getDomainParameters(null, var1.getParams()));
   }

   public BCECGOST3410_2012PublicKey(org.bouncycastle.jce.spec.ECPublicKeySpec var1, ProviderConfiguration var2) {
      if (var1.getParams() != null) {
         ECCurve var3 = var1.getParams().getCurve();
         EllipticCurve var4 = EC5Util.convertCurve(var3, var1.getParams().getSeed());
         this.ecPublicKey = new ECPublicKeyParameters(var1.getQ(), ECUtil.getDomainParameters(var2, var1.getParams()));
         this.ecSpec = EC5Util.convertSpec(var4, var1.getParams());
      } else {
         org.bouncycastle.jce.spec.ECParameterSpec var5 = var2.getEcImplicitlyCa();
         this.ecPublicKey = new ECPublicKeyParameters(
            var5.getCurve().createPoint(var1.getQ().getAffineXCoord().toBigInteger(), var1.getQ().getAffineYCoord().toBigInteger()),
            EC5Util.getDomainParameters(var2, (ECParameterSpec)null)
         );
         this.ecSpec = null;
      }
   }

   public BCECGOST3410_2012PublicKey(String var1, ECPublicKeyParameters var2, ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      this.algorithm = var1;
      this.ecPublicKey = var2;
      if (var4 instanceof ECGOST3410Parameters) {
         ECGOST3410Parameters var5 = (ECGOST3410Parameters)var4;
         this.gostParams = new GOST3410PublicKeyAlgParameters(var5.getPublicKeyParamSet(), var5.getDigestParamSet(), var5.getEncryptionParamSet());
      }

      if (var3 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var6, var4);
      } else {
         this.ecSpec = var3;
      }
   }

   public BCECGOST3410_2012PublicKey(String var1, ECPublicKeyParameters var2, org.bouncycastle.jce.spec.ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      this.algorithm = var1;
      this.ecPublicKey = var2;
      if (var3 == null) {
         EllipticCurve var5 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var5, var4);
      } else {
         EllipticCurve var6 = EC5Util.convertCurve(var3.getCurve(), var3.getSeed());
         this.ecSpec = EC5Util.convertSpec(var6, var3);
      }
   }

   public BCECGOST3410_2012PublicKey(String var1, ECPublicKeyParameters var2) {
      this.algorithm = var1;
      this.ecPublicKey = var2;
      this.ecSpec = null;
   }

   private ECParameterSpec createSpec(EllipticCurve var1, ECDomainParameters var2) {
      return new ECParameterSpec(var1, EC5Util.convertPoint(var2.getG()), var2.getN(), var2.getH().intValue());
   }

   public BCECGOST3410_2012PublicKey(ECPublicKey var1) {
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var1.getW()), EC5Util.getDomainParameters(null, var1.getParams()));
   }

   BCECGOST3410_2012PublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPubKeyInfo(var1);
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      ASN1BitString var3 = var1.getPublicKeyData();
      this.algorithm = "ECGOST3410-2012";

      ASN1OctetString var4;
      try {
         var4 = (ASN1OctetString)ASN1Primitive.fromByteArray(var3.getBytes());
      } catch (IOException var12) {
         throw new IllegalArgumentException("error recovering public key");
      }

      byte[] var5 = var4.getOctets();
      byte var6 = 32;
      if (var2.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)) {
         var6 = 64;
      }

      int var7 = 2 * var6;
      byte[] var8 = new byte[1 + var7];
      var8[0] = 4;

      for (int var9 = 1; var9 <= var6; var9++) {
         var8[var9] = var5[var6 - var9];
         var8[var9 + var6] = var5[var7 - var9];
      }

      this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(var1.getAlgorithm().getParameters());
      ECNamedCurveParameterSpec var13 = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
      ECCurve var10 = var13.getCurve();
      EllipticCurve var11 = EC5Util.convertCurve(var10, var13.getSeed());
      this.ecPublicKey = new ECPublicKeyParameters(var10.decodePoint(var8), ECUtil.getDomainParameters(null, var13));
      this.ecSpec = new ECNamedCurveSpec(
         ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), var11, EC5Util.convertPoint(var13.getG()), var13.getN(), var13.getH()
      );
   }

   @Override
   public String getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      BigInteger var3 = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
      BigInteger var4 = this.ecPublicKey.getQ().getAffineYCoord().toBigInteger();
      boolean var5 = var3.bitLength() > 256;
      Object var1 = this.getGostParams();
      if (var1 == null) {
         if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier var6 = ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName());
            if (var5) {
               var1 = new GOST3410PublicKeyAlgParameters(var6, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
            } else if (!var6.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetB)
               && !var6.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetC)
               && !var6.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetD)) {
               var1 = new GOST3410PublicKeyAlgParameters(var6, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
            } else {
               var1 = new GOST3410PublicKeyAlgParameters(var6, null);
            }
         } else {
            ECCurve var12 = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters var7 = new X9ECParameters(
               var12,
               new X9ECPoint(EC5Util.convertPoint(var12, this.ecSpec.getGenerator()), this.withCompression),
               this.ecSpec.getOrder(),
               BigInteger.valueOf(this.ecSpec.getCofactor()),
               this.ecSpec.getCurve().getSeed()
            );
            var1 = new X962Parameters(var7);
         }
      }

      ASN1ObjectIdentifier var8;
      short var13;
      byte var14;
      if (var5) {
         var13 = 128;
         var14 = 64;
         var8 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
      } else {
         var13 = 64;
         var14 = 32;
         var8 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
      }

      byte[] var9 = new byte[var13];
      this.extractBytes(var9, var13 / 2, 0, var3);
      this.extractBytes(var9, var13 / 2, var14, var4);

      SubjectPublicKeyInfo var2;
      try {
         var2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(var8, (ASN1Encodable)var1), new DEROctetString(var9));
      } catch (IOException var11) {
         return null;
      }

      return KeyUtil.getEncodedSubjectPublicKeyInfo(var2);
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

   @Override
   public ECPoint getW() {
      return EC5Util.convertPoint(this.ecPublicKey.getQ());
   }

   @Override
   public org.bouncycastle.math.ec.ECPoint getQ() {
      return this.ecSpec == null ? this.ecPublicKey.getQ().getDetachedPoint() : this.ecPublicKey.getQ();
   }

   ECPublicKeyParameters engineGetKeyParameters() {
      return this.ecPublicKey;
   }

   org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
      return this.ecSpec != null ? EC5Util.convertSpec(this.ecSpec) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
   }

   @Override
   public String toString() {
      return ECUtil.publicKeyToString(this.algorithm, this.ecPublicKey.getQ(), this.engineGetSpec());
   }

   @Override
   public void setPointFormat(String var1) {
      this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof BCECGOST3410_2012PublicKey)) {
         return false;
      } else {
         BCECGOST3410_2012PublicKey var2 = (BCECGOST3410_2012PublicKey)var1;
         return this.ecPublicKey.getQ().equals(var2.ecPublicKey.getQ()) && this.engineGetSpec().equals(var2.engineGetSpec());
      }
   }

   @Override
   public int hashCode() {
      return this.ecPublicKey.getQ().hashCode() ^ this.engineGetSpec().hashCode();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }

   public GOST3410PublicKeyAlgParameters getGostParams() {
      if (this.gostParams == null && this.ecSpec instanceof ECNamedCurveSpec) {
         BigInteger var1 = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
         boolean var2 = var1.bitLength() > 256;
         ASN1ObjectIdentifier var3 = ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName());
         if (var2) {
            this.gostParams = new GOST3410PublicKeyAlgParameters(var3, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
         } else if (!var3.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetB)
            && !var3.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetC)
            && !var3.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetD)) {
            this.gostParams = new GOST3410PublicKeyAlgParameters(var3, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
         } else {
            this.gostParams = new GOST3410PublicKeyAlgParameters(var3, null);
         }
      }

      return this.gostParams;
   }
}
