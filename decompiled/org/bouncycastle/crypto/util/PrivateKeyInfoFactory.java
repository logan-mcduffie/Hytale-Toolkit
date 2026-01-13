package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class PrivateKeyInfoFactory {
   private static Set cryptoProOids = new HashSet(5);

   private PrivateKeyInfoFactory() {
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      return createPrivateKeyInfo(var0, null);
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0, ASN1Set var1) throws IOException {
      if (var0 instanceof RSAKeyParameters) {
         RSAPrivateCrtKeyParameters var15 = (RSAPrivateCrtKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE),
            new RSAPrivateKey(
               var15.getModulus(), var15.getPublicExponent(), var15.getExponent(), var15.getP(), var15.getQ(), var15.getDP(), var15.getDQ(), var15.getQInv()
            ),
            var1
         );
      } else if (var0 instanceof DSAPrivateKeyParameters) {
         DSAPrivateKeyParameters var14 = (DSAPrivateKeyParameters)var0;
         DSAParameters var16 = var14.getParameters();
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(var16.getP(), var16.getQ(), var16.getG())),
            new ASN1Integer(var14.getX()),
            var1
         );
      } else if (var0 instanceof ECPrivateKeyParameters) {
         ECPrivateKeyParameters var13 = (ECPrivateKeyParameters)var0;
         ECDomainParameters var3 = var13.getParameters();
         X962Parameters var4;
         int var5;
         if (var3 == null) {
            var4 = new X962Parameters(DERNull.INSTANCE);
            var5 = var13.getD().bitLength();
         } else {
            if (var3 instanceof ECGOST3410Parameters) {
               GOST3410PublicKeyAlgParameters var18 = new GOST3410PublicKeyAlgParameters(
                  ((ECGOST3410Parameters)var3).getPublicKeyParamSet(),
                  ((ECGOST3410Parameters)var3).getDigestParamSet(),
                  ((ECGOST3410Parameters)var3).getEncryptionParamSet()
               );
               ASN1ObjectIdentifier var8;
               int var19;
               if (cryptoProOids.contains(var18.getPublicKeyParamSet())) {
                  var19 = 32;
                  var8 = CryptoProObjectIdentifiers.gostR3410_2001;
               } else {
                  boolean var9 = var13.getD().bitLength() > 256;
                  var8 = var9 ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                  var19 = var9 ? 64 : 32;
               }

               byte[] var20 = new byte[var19];
               extractBytes(var20, var19, 0, var13.getD());
               return new PrivateKeyInfo(new AlgorithmIdentifier(var8, var18), new DEROctetString(var20));
            }

            if (var3 instanceof ECNamedDomainParameters) {
               var4 = new X962Parameters(((ECNamedDomainParameters)var3).getName());
               var5 = var3.getN().bitLength();
            } else {
               X9ECParameters var6 = new X9ECParameters(var3.getCurve(), new X9ECPoint(var3.getG(), false), var3.getN(), var3.getH(), var3.getSeed());
               var4 = new X962Parameters(var6);
               var5 = var3.getN().bitLength();
            }
         }

         ECPoint var17 = new FixedPointCombMultiplier().multiply(var3.getG(), var13.getD());
         DERBitString var7 = new DERBitString(var17.getEncoded(false));
         return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var4), new ECPrivateKey(var5, var13.getD(), var7, var4), var1);
      } else if (var0 instanceof X448PrivateKeyParameters) {
         X448PrivateKeyParameters var12 = (X448PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), new DEROctetString(var12.getEncoded()), var1, var12.generatePublicKey().getEncoded()
         );
      } else if (var0 instanceof X25519PrivateKeyParameters) {
         X25519PrivateKeyParameters var11 = (X25519PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), new DEROctetString(var11.getEncoded()), var1, var11.generatePublicKey().getEncoded()
         );
      } else if (var0 instanceof Ed448PrivateKeyParameters) {
         Ed448PrivateKeyParameters var10 = (Ed448PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), new DEROctetString(var10.getEncoded()), var1, var10.generatePublicKey().getEncoded()
         );
      } else if (var0 instanceof Ed25519PrivateKeyParameters) {
         Ed25519PrivateKeyParameters var2 = (Ed25519PrivateKeyParameters)var0;
         return new PrivateKeyInfo(
            new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), new DEROctetString(var2.getEncoded()), var1, var2.generatePublicKey().getEncoded()
         );
      } else {
         throw new IOException("key parameters not recognized");
      }
   }

   private static void extractBytes(byte[] var0, int var1, int var2, BigInteger var3) {
      byte[] var4 = var3.toByteArray();
      if (var4.length < var1) {
         byte[] var5 = new byte[var1];
         System.arraycopy(var4, 0, var5, var5.length - var4.length, var4.length);
         var4 = var5;
      }

      for (int var6 = 0; var6 != var1; var6++) {
         var0[var2 + var6] = var4[var4.length - 1 - var6];
      }
   }

   static {
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA);
      cryptoProOids.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB);
   }
}
