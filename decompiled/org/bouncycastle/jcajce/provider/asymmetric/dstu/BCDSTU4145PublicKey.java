package org.bouncycastle.jcajce.provider.asymmetric.dstu;

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
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
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
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

public class BCDSTU4145PublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
   static final long serialVersionUID = 7026240464295649314L;
   private String algorithm = "DSTU4145";
   private boolean withCompression;
   private transient ECPublicKeyParameters ecPublicKey;
   private transient ECParameterSpec ecSpec;
   private transient DSTU4145Params dstuParams;

   public BCDSTU4145PublicKey(BCDSTU4145PublicKey var1) {
      this.ecPublicKey = var1.ecPublicKey;
      this.ecSpec = var1.ecSpec;
      this.withCompression = var1.withCompression;
      this.dstuParams = var1.dstuParams;
   }

   public BCDSTU4145PublicKey(ECPublicKeySpec var1) {
      this.ecSpec = var1.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var1.getW()), EC5Util.getDomainParameters(null, this.ecSpec));
   }

   public BCDSTU4145PublicKey(org.bouncycastle.jce.spec.ECPublicKeySpec var1, ProviderConfiguration var2) {
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

   public BCDSTU4145PublicKey(String var1, ECPublicKeyParameters var2, ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      this.algorithm = var1;
      this.ecPublicKey = var2;
      if (var3 == null) {
         EllipticCurve var5 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var5, var4);
      } else {
         this.ecSpec = var3;
      }
   }

   public BCDSTU4145PublicKey(String var1, ECPublicKeyParameters var2, org.bouncycastle.jce.spec.ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      this.algorithm = var1;
      if (var3 == null) {
         EllipticCurve var5 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var5, var4);
      } else {
         EllipticCurve var6 = EC5Util.convertCurve(var3.getCurve(), var3.getSeed());
         this.ecSpec = EC5Util.convertSpec(var6, var3);
      }

      this.ecPublicKey = var2;
   }

   public BCDSTU4145PublicKey(String var1, ECPublicKeyParameters var2) {
      this.algorithm = var1;
      this.ecPublicKey = var2;
      this.ecSpec = null;
   }

   private ECParameterSpec createSpec(EllipticCurve var1, ECDomainParameters var2) {
      return new ECParameterSpec(var1, EC5Util.convertPoint(var2.getG()), var2.getN(), var2.getH().intValue());
   }

   BCDSTU4145PublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPubKeyInfo(var1);
   }

   private void reverseBytes(byte[] var1) {
      for (int var3 = 0; var3 < var1.length / 2; var3++) {
         byte var2 = var1[var3];
         var1[var3] = var1[var1.length - 1 - var3];
         var1[var1.length - 1 - var3] = var2;
      }
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      ASN1BitString var2 = var1.getPublicKeyData();
      this.algorithm = "DSTU4145";

      ASN1OctetString var3;
      try {
         var3 = (ASN1OctetString)ASN1Primitive.fromByteArray(var2.getBytes());
      } catch (IOException var13) {
         throw new IllegalArgumentException("error recovering public key");
      }

      byte[] var4 = var3.getOctets();
      if (var1.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
         this.reverseBytes(var4);
      }

      ASN1Sequence var5 = ASN1Sequence.getInstance(var1.getAlgorithm().getParameters());
      Object var6 = null;
      X9ECParameters var7 = null;
      if (var5.getObjectAt(0) instanceof ASN1Integer) {
         var7 = X9ECParameters.getInstance(var5);
         var6 = new org.bouncycastle.jce.spec.ECParameterSpec(var7.getCurve(), var7.getG(), var7.getN(), var7.getH(), var7.getSeed());
      } else {
         this.dstuParams = DSTU4145Params.getInstance(var5);
         if (this.dstuParams.isNamedCurve()) {
            ASN1ObjectIdentifier var8 = this.dstuParams.getNamedCurve();
            ECDomainParameters var9 = DSTU4145NamedCurves.getByOID(var8);
            var6 = new ECNamedCurveParameterSpec(var8.getId(), var9.getCurve(), var9.getG(), var9.getN(), var9.getH(), var9.getSeed());
         } else {
            DSTU4145ECBinary var15 = this.dstuParams.getECBinary();
            byte[] var17 = var15.getB();
            if (var1.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
               this.reverseBytes(var17);
            }

            DSTU4145BinaryField var10 = var15.getField();
            ECCurve.F2m var11 = new ECCurve.F2m(var10.getM(), var10.getK1(), var10.getK2(), var10.getK3(), var15.getA(), new BigInteger(1, var17), null, null);
            byte[] var12 = var15.getG();
            if (var1.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
               this.reverseBytes(var12);
            }

            var6 = new org.bouncycastle.jce.spec.ECParameterSpec(var11, DSTU4145PointEncoder.decodePoint(var11, var12), var15.getN());
         }
      }

      ECCurve var16 = ((org.bouncycastle.jce.spec.ECParameterSpec)var6).getCurve();
      EllipticCurve var18 = EC5Util.convertCurve(var16, ((org.bouncycastle.jce.spec.ECParameterSpec)var6).getSeed());
      if (this.dstuParams != null) {
         ECPoint var19 = EC5Util.convertPoint(((org.bouncycastle.jce.spec.ECParameterSpec)var6).getG());
         if (this.dstuParams.isNamedCurve()) {
            String var20 = this.dstuParams.getNamedCurve().getId();
            this.ecSpec = new ECNamedCurveSpec(
               var20, var18, var19, ((org.bouncycastle.jce.spec.ECParameterSpec)var6).getN(), ((org.bouncycastle.jce.spec.ECParameterSpec)var6).getH()
            );
         } else {
            this.ecSpec = new ECParameterSpec(
               var18, var19, ((org.bouncycastle.jce.spec.ECParameterSpec)var6).getN(), ((org.bouncycastle.jce.spec.ECParameterSpec)var6).getH().intValue()
            );
         }
      } else {
         this.ecSpec = EC5Util.convertToSpec(var7);
      }

      this.ecPublicKey = new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(var16, var4), EC5Util.getDomainParameters(null, this.ecSpec));
   }

   public byte[] getSbox() {
      return null != this.dstuParams ? this.dstuParams.getDKE() : DSTU4145Params.getDefaultDKE();
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
      Object var1;
      if (this.dstuParams != null) {
         var1 = this.dstuParams;
      } else if (this.ecSpec instanceof ECNamedCurveSpec) {
         var1 = new DSTU4145Params(new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()));
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

      byte[] var6 = DSTU4145PointEncoder.encodePoint(this.ecPublicKey.getQ());

      SubjectPublicKeyInfo var2;
      try {
         var2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, (ASN1Encodable)var1), new DEROctetString(var6));
      } catch (IOException var5) {
         return null;
      }

      return KeyUtil.getEncodedSubjectPublicKeyInfo(var2);
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
      org.bouncycastle.math.ec.ECPoint var1 = this.ecPublicKey.getQ();
      return this.ecSpec == null ? var1.getDetachedPoint() : var1;
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
      if (!(var1 instanceof BCDSTU4145PublicKey)) {
         return false;
      } else {
         BCDSTU4145PublicKey var2 = (BCDSTU4145PublicKey)var1;
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
}
