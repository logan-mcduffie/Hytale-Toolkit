package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

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
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
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

public class BCECGOST3410PublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
   static final long serialVersionUID = 7026240464295649314L;
   private String algorithm = "ECGOST3410";
   private boolean withCompression;
   private transient ECPublicKeyParameters ecPublicKey;
   private transient ECParameterSpec ecSpec;
   private transient ASN1Encodable gostParams;

   public BCECGOST3410PublicKey(BCECGOST3410PublicKey var1) {
      this.ecPublicKey = var1.ecPublicKey;
      this.ecSpec = var1.ecSpec;
      this.withCompression = var1.withCompression;
      this.gostParams = var1.gostParams;
   }

   public BCECGOST3410PublicKey(ECPublicKeySpec var1) {
      this.ecSpec = var1.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var1.getW()), EC5Util.getDomainParameters(null, var1.getParams()));
   }

   public BCECGOST3410PublicKey(org.bouncycastle.jce.spec.ECPublicKeySpec var1, ProviderConfiguration var2) {
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

   public BCECGOST3410PublicKey(String var1, ECPublicKeyParameters var2, ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      if (var4 instanceof ECGOST3410Parameters) {
         ECGOST3410Parameters var5 = (ECGOST3410Parameters)var4;
         this.gostParams = new GOST3410PublicKeyAlgParameters(var5.getPublicKeyParamSet(), var5.getDigestParamSet(), var5.getEncryptionParamSet());
      }

      this.algorithm = var1;
      this.ecPublicKey = var2;
      if (var3 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var6, var4);
      } else {
         this.ecSpec = var3;
      }
   }

   public BCECGOST3410PublicKey(String var1, ECPublicKeyParameters var2, org.bouncycastle.jce.spec.ECParameterSpec var3) {
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

   public BCECGOST3410PublicKey(String var1, ECPublicKeyParameters var2) {
      this.algorithm = var1;
      this.ecPublicKey = var2;
      this.ecSpec = null;
   }

   private ECParameterSpec createSpec(EllipticCurve var1, ECDomainParameters var2) {
      return new ECParameterSpec(var1, EC5Util.convertPoint(var2.getG()), var2.getN(), var2.getH().intValue());
   }

   public BCECGOST3410PublicKey(ECPublicKey var1) {
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var1.getW()), EC5Util.getDomainParameters(null, var1.getParams()));
   }

   BCECGOST3410PublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPubKeyInfo(var1);
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      ASN1BitString var2 = var1.getPublicKeyData();
      this.algorithm = "ECGOST3410";

      ASN1OctetString var3;
      try {
         var3 = (ASN1OctetString)ASN1Primitive.fromByteArray(var2.getBytes());
      } catch (IOException var10) {
         throw new IllegalArgumentException("error recovering public key");
      }

      byte[] var4 = var3.getOctets();
      byte[] var5 = new byte[65];
      var5[0] = 4;

      for (int var6 = 1; var6 <= 32; var6++) {
         var5[var6] = var4[32 - var6];
         var5[var6 + 32] = var4[64 - var6];
      }

      ASN1ObjectIdentifier var11;
      if (var1.getAlgorithm().getParameters() instanceof ASN1ObjectIdentifier) {
         var11 = ASN1ObjectIdentifier.getInstance(var1.getAlgorithm().getParameters());
         this.gostParams = var11;
      } else {
         GOST3410PublicKeyAlgParameters var7 = GOST3410PublicKeyAlgParameters.getInstance(var1.getAlgorithm().getParameters());
         this.gostParams = var7;
         var11 = var7.getPublicKeyParamSet();
      }

      ECNamedCurveParameterSpec var12 = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(var11));
      ECCurve var8 = var12.getCurve();
      EllipticCurve var9 = EC5Util.convertCurve(var8, var12.getSeed());
      this.ecPublicKey = new ECPublicKeyParameters(var8.decodePoint(var5), ECUtil.getDomainParameters(null, var12));
      this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(var11), var9, EC5Util.convertPoint(var12.getG()), var12.getN(), var12.getH());
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
      Object var1 = this.getGostParams();
      if (var1 == null) {
         if (this.ecSpec instanceof ECNamedCurveSpec) {
            var1 = new GOST3410PublicKeyAlgParameters(
               ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet
            );
         } else {
            ECCurve var3 = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters var4 = new X9ECParameters(
               var3,
               new X9ECPoint(EC5Util.convertPoint(var3, this.ecSpec.getGenerator()), this.withCompression),
               this.ecSpec.getOrder(),
               BigInteger.valueOf(this.ecSpec.getCofactor()),
               this.ecSpec.getCurve().getSeed()
            );
            var1 = new X962Parameters(var4);
         }
      }

      BigInteger var8 = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
      BigInteger var9 = this.ecPublicKey.getQ().getAffineYCoord().toBigInteger();
      byte[] var5 = new byte[64];
      this.extractBytes(var5, 0, var8);
      this.extractBytes(var5, 32, var9);

      SubjectPublicKeyInfo var2;
      try {
         var2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, (ASN1Encodable)var1), new DEROctetString(var5));
      } catch (IOException var7) {
         return null;
      }

      return KeyUtil.getEncodedSubjectPublicKeyInfo(var2);
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
      if (!(var1 instanceof BCECGOST3410PublicKey)) {
         return false;
      } else {
         BCECGOST3410PublicKey var2 = (BCECGOST3410PublicKey)var1;
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

   ASN1Encodable getGostParams() {
      if (this.gostParams == null && this.ecSpec instanceof ECNamedCurveSpec) {
         this.gostParams = new GOST3410PublicKeyAlgParameters(
            ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet
         );
      }

      return this.gostParams;
   }
}
