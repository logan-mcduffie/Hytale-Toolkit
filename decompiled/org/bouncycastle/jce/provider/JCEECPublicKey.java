package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;

public class JCEECPublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
   private String algorithm = "EC";
   private ECPoint q;
   private ECParameterSpec ecSpec;
   private boolean withCompression;
   private GOST3410PublicKeyAlgParameters gostParams;

   public JCEECPublicKey(String var1, JCEECPublicKey var2) {
      this.algorithm = var1;
      this.q = var2.q;
      this.ecSpec = var2.ecSpec;
      this.withCompression = var2.withCompression;
      this.gostParams = var2.gostParams;
   }

   public JCEECPublicKey(String var1, ECPublicKeySpec var2) {
      this.algorithm = var1;
      this.ecSpec = var2.getParams();
      this.q = EC5Util.convertPoint(this.ecSpec, var2.getW());
   }

   public JCEECPublicKey(String var1, org.bouncycastle.jce.spec.ECPublicKeySpec var2) {
      this.algorithm = var1;
      this.q = var2.getQ();
      if (var2.getParams() != null) {
         ECCurve var3 = var2.getParams().getCurve();
         EllipticCurve var4 = EC5Util.convertCurve(var3, var2.getParams().getSeed());
         this.ecSpec = EC5Util.convertSpec(var4, var2.getParams());
      } else {
         if (this.q.getCurve() == null) {
            org.bouncycastle.jce.spec.ECParameterSpec var5 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            this.q = var5.getCurve().createPoint(this.q.getAffineXCoord().toBigInteger(), this.q.getAffineYCoord().toBigInteger());
         }

         this.ecSpec = null;
      }
   }

   public JCEECPublicKey(String var1, ECPublicKeyParameters var2, ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      this.algorithm = var1;
      this.q = var2.getQ();
      if (var3 == null) {
         EllipticCurve var5 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var5, var4);
      } else {
         this.ecSpec = var3;
      }
   }

   public JCEECPublicKey(String var1, ECPublicKeyParameters var2, org.bouncycastle.jce.spec.ECParameterSpec var3) {
      ECDomainParameters var4 = var2.getParameters();
      this.algorithm = var1;
      this.q = var2.getQ();
      if (var3 == null) {
         EllipticCurve var5 = EC5Util.convertCurve(var4.getCurve(), var4.getSeed());
         this.ecSpec = this.createSpec(var5, var4);
      } else {
         EllipticCurve var6 = EC5Util.convertCurve(var3.getCurve(), var3.getSeed());
         this.ecSpec = EC5Util.convertSpec(var6, var3);
      }
   }

   public JCEECPublicKey(String var1, ECPublicKeyParameters var2) {
      this.algorithm = var1;
      this.q = var2.getQ();
      this.ecSpec = null;
   }

   private ECParameterSpec createSpec(EllipticCurve var1, ECDomainParameters var2) {
      return new ECParameterSpec(var1, EC5Util.convertPoint(var2.getG()), var2.getN(), var2.getH().intValue());
   }

   public JCEECPublicKey(ECPublicKey var1) {
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
      this.q = EC5Util.convertPoint(this.ecSpec, var1.getW());
   }

   JCEECPublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPubKeyInfo(var1);
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      AlgorithmIdentifier var2 = var1.getAlgorithm();
      if (var2.getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
         ASN1BitString var3 = var1.getPublicKeyData();
         this.algorithm = "ECGOST3410";

         ASN1OctetString var4;
         try {
            var4 = (ASN1OctetString)ASN1Primitive.fromByteArray(var3.getBytes());
         } catch (IOException var12) {
            throw new IllegalArgumentException("error recovering public key");
         }

         byte[] var5 = var4.getOctets();
         byte[] var6 = new byte[65];
         var6[0] = 4;

         for (int var7 = 1; var7 <= 32; var7++) {
            var6[var7] = var5[32 - var7];
            var6[var7 + 32] = var5[64 - var7];
         }

         this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(var2.getParameters());
         ECNamedCurveParameterSpec var20 = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
         ECCurve var8 = var20.getCurve();
         EllipticCurve var9 = EC5Util.convertCurve(var8, var20.getSeed());
         this.q = var8.decodePoint(var6);
         this.ecSpec = new ECNamedCurveSpec(
            ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), var9, EC5Util.convertPoint(var20.getG()), var20.getN(), var20.getH()
         );
      } else {
         X962Parameters var13 = X962Parameters.getInstance(var2.getParameters());
         ECCurve var14;
         if (var13.isNamedCurve()) {
            ASN1ObjectIdentifier var17 = (ASN1ObjectIdentifier)var13.getParameters();
            X9ECParameters var21 = ECUtil.getNamedCurveByOid(var17);
            var14 = var21.getCurve();
            EllipticCurve var15 = EC5Util.convertCurve(var14, var21.getSeed());
            this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(var17), var15, EC5Util.convertPoint(var21.getG()), var21.getN(), var21.getH());
         } else if (var13.isImplicitlyCA()) {
            this.ecSpec = null;
            var14 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve();
         } else {
            X9ECParameters var18 = X9ECParameters.getInstance(var13.getParameters());
            var14 = var18.getCurve();
            EllipticCurve var16 = EC5Util.convertCurve(var14, var18.getSeed());
            this.ecSpec = new ECParameterSpec(var16, EC5Util.convertPoint(var18.getG()), var18.getN(), var18.getH().intValue());
         }

         ASN1BitString var19 = var1.getPublicKeyData();
         byte[] var22 = var19.getBytes();
         Object var23 = new DEROctetString(var22);
         if (var22[0] == 4 && var22[1] == var22.length - 2 && (var22[2] == 2 || var22[2] == 3)) {
            int var24 = new X9IntegerConverter().getByteLength(var14);
            if (var24 >= var22.length - 3) {
               try {
                  var23 = (ASN1OctetString)ASN1Primitive.fromByteArray(var22);
               } catch (IOException var11) {
                  throw new IllegalArgumentException("error recovering public key");
               }
            }
         }

         X9ECPoint var25 = new X9ECPoint(var14, (ASN1OctetString)var23);
         this.q = var25.getPoint();
      }
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
      SubjectPublicKeyInfo var2;
      if (this.algorithm.equals("ECGOST3410")) {
         Object var1;
         if (this.gostParams != null) {
            var1 = this.gostParams;
         } else if (this.ecSpec instanceof ECNamedCurveSpec) {
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

         BigInteger var9 = this.q.getAffineXCoord().toBigInteger();
         BigInteger var13 = this.q.getAffineYCoord().toBigInteger();
         byte[] var5 = new byte[64];
         this.extractBytes(var5, 0, var9);
         this.extractBytes(var5, 32, var13);

         try {
            var2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, (ASN1Encodable)var1), new DEROctetString(var5));
         } catch (IOException var7) {
            return null;
         }
      } else {
         X962Parameters var8;
         if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier var10 = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (var10 == null) {
               var10 = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }

            var8 = new X962Parameters(var10);
         } else if (this.ecSpec == null) {
            var8 = new X962Parameters(DERNull.INSTANCE);
         } else {
            ECCurve var11 = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters var14 = new X9ECParameters(
               var11,
               new X9ECPoint(EC5Util.convertPoint(var11, this.ecSpec.getGenerator()), this.withCompression),
               this.ecSpec.getOrder(),
               BigInteger.valueOf(this.ecSpec.getCofactor()),
               this.ecSpec.getCurve().getSeed()
            );
            var8 = new X962Parameters(var14);
         }

         byte[] var12 = this.getQ().getEncoded(this.withCompression);
         var2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var8), var12);
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
   public java.security.spec.ECPoint getW() {
      return EC5Util.convertPoint(this.q);
   }

   @Override
   public ECPoint getQ() {
      return this.ecSpec == null ? this.q.getDetachedPoint() : this.q;
   }

   public ECPoint engineGetQ() {
      return this.q;
   }

   org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
      return this.ecSpec != null ? EC5Util.convertSpec(this.ecSpec) : BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("EC Public Key").append(var2);
      var1.append("            X: ").append(this.q.getAffineXCoord().toBigInteger().toString(16)).append(var2);
      var1.append("            Y: ").append(this.q.getAffineYCoord().toBigInteger().toString(16)).append(var2);
      return var1.toString();
   }

   @Override
   public void setPointFormat(String var1) {
      this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof JCEECPublicKey)) {
         return false;
      } else {
         JCEECPublicKey var2 = (JCEECPublicKey)var1;
         return this.engineGetQ().equals(var2.engineGetQ()) && this.engineGetSpec().equals(var2.engineGetSpec());
      }
   }

   @Override
   public int hashCode() {
      return this.engineGetQ().hashCode() ^ this.engineGetSpec().hashCode();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      byte[] var2 = (byte[])var1.readObject();
      this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
      this.algorithm = (String)var1.readObject();
      this.withCompression = var1.readBoolean();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.getEncoded());
      var1.writeObject(this.algorithm);
      var1.writeBoolean(this.withCompression);
   }
}
