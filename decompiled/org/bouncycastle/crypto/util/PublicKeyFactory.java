package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.DHPublicKey;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.ElGamalParameter;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class PublicKeyFactory {
   private static Map converters = new HashMap();

   public static AsymmetricKeyParameter createKey(byte[] var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("keyInfoData array null");
      } else if (var0.length == 0) {
         throw new IllegalArgumentException("keyInfoData array empty");
      } else {
         return createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(var0)));
      }
   }

   public static AsymmetricKeyParameter createKey(InputStream var0) throws IOException {
      return createKey(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(var0).readObject()));
   }

   public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("keyInfo argument null");
      } else {
         return createKey(var0, null);
      }
   }

   public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo var0, Object var1) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("keyInfo argument null");
      } else {
         AlgorithmIdentifier var2 = var0.getAlgorithm();
         PublicKeyFactory.SubjectPublicKeyInfoConverter var3 = (PublicKeyFactory.SubjectPublicKeyInfoConverter)converters.get(var2.getAlgorithm());
         if (null == var3) {
            throw new IOException("algorithm identifier in public key not recognised: " + var2.getAlgorithm());
         } else {
            return var3.getPublicKeyParameters(var0, var1);
         }
      }
   }

   private static byte[] getRawKey(SubjectPublicKeyInfo var0, Object var1) {
      return var0.getPublicKeyData().getOctets();
   }

   static {
      converters.put(PKCSObjectIdentifiers.rsaEncryption, new PublicKeyFactory.RSAConverter());
      converters.put(PKCSObjectIdentifiers.id_RSASSA_PSS, new PublicKeyFactory.RSAConverter());
      converters.put(X509ObjectIdentifiers.id_ea_rsa, new PublicKeyFactory.RSAConverter());
      converters.put(X9ObjectIdentifiers.dhpublicnumber, new PublicKeyFactory.DHPublicNumberConverter());
      converters.put(PKCSObjectIdentifiers.dhKeyAgreement, new PublicKeyFactory.DHAgreementConverter());
      converters.put(X9ObjectIdentifiers.id_dsa, new PublicKeyFactory.DSAConverter());
      converters.put(OIWObjectIdentifiers.dsaWithSHA1, new PublicKeyFactory.DSAConverter());
      converters.put(OIWObjectIdentifiers.elGamalAlgorithm, new PublicKeyFactory.ElGamalConverter());
      converters.put(X9ObjectIdentifiers.id_ecPublicKey, new PublicKeyFactory.ECConverter());
      converters.put(CryptoProObjectIdentifiers.gostR3410_2001, new PublicKeyFactory.GOST3410_2001Converter());
      converters.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, new PublicKeyFactory.GOST3410_2012Converter());
      converters.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, new PublicKeyFactory.GOST3410_2012Converter());
      converters.put(UAObjectIdentifiers.dstu4145be, new PublicKeyFactory.DSTUConverter());
      converters.put(UAObjectIdentifiers.dstu4145le, new PublicKeyFactory.DSTUConverter());
      converters.put(EdECObjectIdentifiers.id_X25519, new PublicKeyFactory.X25519Converter());
      converters.put(EdECObjectIdentifiers.id_X448, new PublicKeyFactory.X448Converter());
      converters.put(EdECObjectIdentifiers.id_Ed25519, new PublicKeyFactory.Ed25519Converter());
      converters.put(EdECObjectIdentifiers.id_Ed448, new PublicKeyFactory.Ed448Converter());
   }

   private static class DHAgreementConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private DHAgreementConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         DHParameter var3 = DHParameter.getInstance(var1.getAlgorithm().getParameters());
         ASN1Integer var4 = (ASN1Integer)var1.parsePublicKey();
         BigInteger var5 = var3.getL();
         int var6 = var5 == null ? 0 : var5.intValue();
         DHParameters var7 = new DHParameters(var3.getP(), var3.getG(), null, var6);
         return new DHPublicKeyParameters(var4.getValue(), var7);
      }
   }

   private static class DHPublicNumberConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private DHPublicNumberConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         DHPublicKey var3 = DHPublicKey.getInstance(var1.parsePublicKey());
         BigInteger var4 = var3.getY();
         DomainParameters var5 = DomainParameters.getInstance(var1.getAlgorithm().getParameters());
         BigInteger var6 = var5.getP();
         BigInteger var7 = var5.getG();
         BigInteger var8 = var5.getQ();
         BigInteger var9 = null;
         if (var5.getJ() != null) {
            var9 = var5.getJ();
         }

         DHValidationParameters var10 = null;
         ValidationParams var11 = var5.getValidationParams();
         if (var11 != null) {
            byte[] var12 = var11.getSeed();
            BigInteger var13 = var11.getPgenCounter();
            var10 = new DHValidationParameters(var12, var13.intValue());
         }

         return new DHPublicKeyParameters(var4, new DHParameters(var6, var7, var8, var9, var10));
      }
   }

   private static class DSAConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private DSAConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         ASN1Integer var3 = (ASN1Integer)var1.parsePublicKey();
         ASN1Encodable var4 = var1.getAlgorithm().getParameters();
         DSAParameters var5 = null;
         if (var4 != null) {
            DSAParameter var6 = DSAParameter.getInstance(var4.toASN1Primitive());
            var5 = new DSAParameters(var6.getP(), var6.getQ(), var6.getG());
         }

         return new DSAPublicKeyParameters(var3.getValue(), var5);
      }
   }

   private static class DSTUConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private DSTUConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         AlgorithmIdentifier var3 = var1.getAlgorithm();
         ASN1ObjectIdentifier var4 = var3.getAlgorithm();
         DSTU4145Params var5 = DSTU4145Params.getInstance(var3.getParameters());

         ASN1OctetString var6;
         try {
            var6 = (ASN1OctetString)var1.parsePublicKey();
         } catch (IOException var16) {
            throw new IllegalArgumentException("error recovering DSTU public key");
         }

         byte[] var7 = Arrays.clone(var6.getOctets());
         if (var4.equals(UAObjectIdentifiers.dstu4145le)) {
            this.reverseBytes(var7);
         }

         ECDomainParameters var8;
         if (var5.isNamedCurve()) {
            var8 = DSTU4145NamedCurves.getByOID(var5.getNamedCurve());
         } else {
            DSTU4145ECBinary var9 = var5.getECBinary();
            byte[] var10 = var9.getB();
            if (var4.equals(UAObjectIdentifiers.dstu4145le)) {
               this.reverseBytes(var10);
            }

            BigInteger var11 = new BigInteger(1, var10);
            DSTU4145BinaryField var12 = var9.getField();
            ECCurve.F2m var13 = new ECCurve.F2m(var12.getM(), var12.getK1(), var12.getK2(), var12.getK3(), var9.getA(), var11, null, null);
            byte[] var14 = var9.getG();
            if (var4.equals(UAObjectIdentifiers.dstu4145le)) {
               this.reverseBytes(var14);
            }

            ECPoint var15 = DSTU4145PointEncoder.decodePoint(var13, var14);
            var8 = new ECDomainParameters(var13, var15, var9.getN());
         }

         ECPoint var17 = DSTU4145PointEncoder.decodePoint(var8.getCurve(), var7);
         return new ECPublicKeyParameters(var17, var8);
      }

      private void reverseBytes(byte[] var1) {
         for (int var3 = 0; var3 < var1.length / 2; var3++) {
            byte var2 = var1[var3];
            var1[var3] = var1[var1.length - 1 - var3];
            var1[var1.length - 1 - var3] = var2;
         }
      }
   }

   private static class ECConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private ECConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         X962Parameters var3 = X962Parameters.getInstance(var1.getAlgorithm().getParameters());
         Object var4;
         if (var3.isNamedCurve()) {
            ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var3.getParameters();
            var4 = ECNamedDomainParameters.lookup(var5);
         } else if (var3.isImplicitlyCA()) {
            var4 = (ECDomainParameters)var2;
         } else {
            X9ECParameters var11 = X9ECParameters.getInstance(var3.getParameters());
            var4 = new ECDomainParameters(var11);
         }

         ASN1BitString var12 = var1.getPublicKeyData();
         byte[] var6 = var12.getBytes();
         Object var7 = new DEROctetString(var6);
         if (var6[0] == 4 && var6[1] == var6.length - 2 && (var6[2] == 2 || var6[2] == 3)) {
            int var8 = new X9IntegerConverter().getByteLength(((ECDomainParameters)var4).getCurve());
            if (var8 >= var6.length - 3) {
               try {
                  var7 = (ASN1OctetString)ASN1Primitive.fromByteArray(var6);
               } catch (IOException var10) {
                  throw new IllegalArgumentException("error recovering public key");
               }
            }
         }

         X9ECPoint var13 = new X9ECPoint(((ECDomainParameters)var4).getCurve(), (ASN1OctetString)var7);
         return new ECPublicKeyParameters(var13.getPoint(), (ECDomainParameters)var4);
      }
   }

   private static class Ed25519Converter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private Ed25519Converter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         return new Ed25519PublicKeyParameters(PublicKeyFactory.getRawKey(var1, var2));
      }
   }

   private static class Ed448Converter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private Ed448Converter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         return new Ed448PublicKeyParameters(PublicKeyFactory.getRawKey(var1, var2));
      }
   }

   private static class ElGamalConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private ElGamalConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         ElGamalParameter var3 = ElGamalParameter.getInstance(var1.getAlgorithm().getParameters());
         ASN1Integer var4 = (ASN1Integer)var1.parsePublicKey();
         return new ElGamalPublicKeyParameters(var4.getValue(), new ElGamalParameters(var3.getP(), var3.getG()));
      }
   }

   private static class GOST3410_2001Converter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private GOST3410_2001Converter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         AlgorithmIdentifier var3 = var1.getAlgorithm();
         GOST3410PublicKeyAlgParameters var4 = GOST3410PublicKeyAlgParameters.getInstance(var3.getParameters());
         ASN1ObjectIdentifier var5 = var4.getPublicKeyParamSet();
         ECGOST3410Parameters var6 = new ECGOST3410Parameters(
            new ECNamedDomainParameters(var5, ECGOST3410NamedCurves.getByOIDX9(var5)), var5, var4.getDigestParamSet(), var4.getEncryptionParamSet()
         );

         ASN1OctetString var7;
         try {
            var7 = (ASN1OctetString)var1.parsePublicKey();
         } catch (IOException var13) {
            throw new IllegalArgumentException("error recovering GOST3410_2001 public key");
         }

         byte var8 = 32;
         int var9 = 2 * var8;
         byte[] var10 = var7.getOctets();
         if (var10.length != var9) {
            throw new IllegalArgumentException("invalid length for GOST3410_2001 public key");
         } else {
            byte[] var11 = new byte[1 + var9];
            var11[0] = 4;

            for (int var12 = 1; var12 <= var8; var12++) {
               var11[var12] = var10[var8 - var12];
               var11[var12 + var8] = var10[var9 - var12];
            }

            ECPoint var14 = var6.getCurve().decodePoint(var11);
            return new ECPublicKeyParameters(var14, var6);
         }
      }
   }

   private static class GOST3410_2012Converter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private GOST3410_2012Converter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         AlgorithmIdentifier var3 = var1.getAlgorithm();
         ASN1ObjectIdentifier var4 = var3.getAlgorithm();
         GOST3410PublicKeyAlgParameters var5 = GOST3410PublicKeyAlgParameters.getInstance(var3.getParameters());
         ASN1ObjectIdentifier var6 = var5.getPublicKeyParamSet();
         ECGOST3410Parameters var7 = new ECGOST3410Parameters(
            new ECNamedDomainParameters(var6, ECGOST3410NamedCurves.getByOIDX9(var6)), var6, var5.getDigestParamSet(), var5.getEncryptionParamSet()
         );

         ASN1OctetString var8;
         try {
            var8 = (ASN1OctetString)var1.parsePublicKey();
         } catch (IOException var14) {
            throw new IllegalArgumentException("error recovering GOST3410_2012 public key");
         }

         byte var9 = 32;
         if (var4.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)) {
            var9 = 64;
         }

         int var10 = 2 * var9;
         byte[] var11 = var8.getOctets();
         if (var11.length != var10) {
            throw new IllegalArgumentException("invalid length for GOST3410_2012 public key");
         } else {
            byte[] var12 = new byte[1 + var10];
            var12[0] = 4;

            for (int var13 = 1; var13 <= var9; var13++) {
               var12[var13] = var11[var9 - var13];
               var12[var13 + var9] = var11[var10 - var13];
            }

            ECPoint var15 = var7.getCurve().decodePoint(var12);
            return new ECPublicKeyParameters(var15, var7);
         }
      }
   }

   private static class RSAConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private RSAConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         RSAPublicKey var3 = RSAPublicKey.getInstance(var1.parsePublicKey());
         return new RSAKeyParameters(false, var3.getModulus(), var3.getPublicExponent());
      }
   }

   private abstract static class SubjectPublicKeyInfoConverter {
      private SubjectPublicKeyInfoConverter() {
      }

      abstract AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException;
   }

   private static class X25519Converter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private X25519Converter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         return new X25519PublicKeyParameters(PublicKeyFactory.getRawKey(var1, var2));
      }
   }

   private static class X448Converter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private X448Converter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) {
         return new X448PublicKeyParameters(PublicKeyFactory.getRawKey(var1, var2));
      }
   }
}
