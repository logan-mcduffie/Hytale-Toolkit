package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCECPublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder, BCKey {
   static final long serialVersionUID = 2422789860422731812L;
   private String algorithm = "EC";
   private boolean withCompression;
   private transient ECPublicKeyParameters ecPublicKey;
   private transient ECParameterSpec ecSpec;
   private transient ProviderConfiguration configuration;
   private transient byte[] encoding;
   private transient boolean oldPcSet;

   public BCECPublicKey(String var1, BCECPublicKey var2) {
      this.algorithm = var1;
      this.ecPublicKey = var2.ecPublicKey;
      this.ecSpec = var2.ecSpec;
      this.withCompression = var2.withCompression;
      this.configuration = var2.configuration;
   }

   public BCECPublicKey(String var1, ECPublicKeySpec var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      this.ecSpec = var2.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var2.getW()), EC5Util.getDomainParameters(var3, var2.getParams()));
      this.configuration = var3;
   }

   public BCECPublicKey(String var1, org.bouncycastle.jce.spec.ECPublicKeySpec var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      if (var2.getParams() != null) {
         ECCurve var4 = var2.getParams().getCurve();
         EllipticCurve var5 = EC5Util.convertCurve(var4, var2.getParams().getSeed());
         this.ecPublicKey = new ECPublicKeyParameters(var2.getQ(), ECUtil.getDomainParameters(var3, var2.getParams()));
         this.ecSpec = EC5Util.convertSpec(var5, var2.getParams());
      } else {
         org.bouncycastle.jce.spec.ECParameterSpec var6 = var3.getEcImplicitlyCa();
         this.ecPublicKey = new ECPublicKeyParameters(
            var6.getCurve().createPoint(var2.getQ().getAffineXCoord().toBigInteger(), var2.getQ().getAffineYCoord().toBigInteger()),
            EC5Util.getDomainParameters(var3, (ECParameterSpec)null)
         );
         this.ecSpec = null;
      }

      this.configuration = var3;
   }

   public BCECPublicKey(String var1, ECPublicKeyParameters var2, ECParameterSpec var3, ProviderConfiguration var4) {
      ECDomainParameters var5 = var2.getParameters();
      this.algorithm = var1;
      this.ecPublicKey = var2;
      if (var3 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = this.createSpec(var6, var5);
      } else {
         this.ecSpec = var3;
      }

      this.configuration = var4;
   }

   public BCECPublicKey(String var1, ECPublicKeyParameters var2, org.bouncycastle.jce.spec.ECParameterSpec var3, ProviderConfiguration var4) {
      ECDomainParameters var5 = var2.getParameters();
      this.algorithm = var1;
      if (var3 == null) {
         EllipticCurve var6 = EC5Util.convertCurve(var5.getCurve(), var5.getSeed());
         this.ecSpec = this.createSpec(var6, var5);
      } else {
         EllipticCurve var7 = EC5Util.convertCurve(var3.getCurve(), var3.getSeed());
         this.ecSpec = EC5Util.convertSpec(var7, var3);
      }

      this.ecPublicKey = var2;
      this.configuration = var4;
   }

   public BCECPublicKey(String var1, ECPublicKeyParameters var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      this.ecPublicKey = var2;
      this.ecSpec = null;
      this.configuration = var3;
   }

   public BCECPublicKey(ECPublicKey var1, ProviderConfiguration var2) {
      this.algorithm = var1.getAlgorithm();
      this.ecSpec = var1.getParams();
      this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, var1.getW()), EC5Util.getDomainParameters(var2, var1.getParams()));
      this.configuration = var2;
   }

   BCECPublicKey(String var1, SubjectPublicKeyInfo var2, ProviderConfiguration var3) {
      this.algorithm = var1;
      this.configuration = var3;
      this.populateFromPubKeyInfo(var2);
   }

   private ECParameterSpec createSpec(EllipticCurve var1, ECDomainParameters var2) {
      return new ECParameterSpec(var1, EC5Util.convertPoint(var2.getG()), var2.getN(), var2.getH().intValue());
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      X962Parameters var2 = X962Parameters.getInstance(var1.getAlgorithm().getParameters());
      ECCurve var3 = EC5Util.getCurve(this.configuration, var2);
      this.ecSpec = EC5Util.convertToSpec(var2, var3);
      ASN1BitString var4 = var1.getPublicKeyData();
      byte[] var5 = var4.getBytes();
      Object var6 = new DEROctetString(var5);
      if (var5[0] == 4 && var5[1] == var5.length - 2 && (var5[2] == 2 || var5[2] == 3)) {
         int var7 = new X9IntegerConverter().getByteLength(var3);
         if (var7 >= var5.length - 3) {
            try {
               var6 = (ASN1OctetString)ASN1Primitive.fromByteArray(var5);
            } catch (IOException var9) {
               throw new IllegalArgumentException("error recovering public key");
            }
         }
      }

      X9ECPoint var10 = new X9ECPoint(var3, (ASN1OctetString)var6);
      this.ecPublicKey = new ECPublicKeyParameters(var10.getPoint(), ECUtil.getDomainParameters(this.configuration, var2));
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
      boolean var1 = Properties.isOverrideSet("org.bouncycastle.ec.enable_pc");
      if (this.encoding == null || this.oldPcSet != var1) {
         boolean var2 = this.withCompression || var1;
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, ECUtils.getDomainParametersFromName(this.ecSpec, var2));
         byte[] var4 = this.ecPublicKey.getQ().getEncoded(var2);
         this.encoding = KeyUtil.getEncodedSubjectPublicKeyInfo(var3, var4);
         this.oldPcSet = var1;
      }

      return Arrays.clone(this.encoding);
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
      return this.ecSpec != null ? EC5Util.convertSpec(this.ecSpec) : this.configuration.getEcImplicitlyCa();
   }

   @Override
   public String toString() {
      return ECUtil.publicKeyToString("EC", this.ecPublicKey.getQ(), this.engineGetSpec());
   }

   @Override
   public void setPointFormat(String var1) {
      this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(var1);
      this.encoding = null;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof BCECPublicKey)) {
         if (var1 instanceof ECPublicKey) {
            ECPublicKey var3 = (ECPublicKey)var1;
            return Arrays.areEqual(this.getEncoded(), var3.getEncoded());
         } else {
            return false;
         }
      } else {
         BCECPublicKey var2 = (BCECPublicKey)var1;
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
      this.configuration = BouncyCastleProvider.CONFIGURATION;
      this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
