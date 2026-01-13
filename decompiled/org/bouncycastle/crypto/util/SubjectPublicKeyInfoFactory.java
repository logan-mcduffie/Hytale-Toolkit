package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class SubjectPublicKeyInfoFactory {
   private static final byte tag_OctetString = 4;
   private static Set cryptoProOids = new HashSet(5);

   private SubjectPublicKeyInfoFactory() {
   }

   public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      if (var0 instanceof RSAKeyParameters) {
         RSAKeyParameters var20 = (RSAKeyParameters)var0;
         return new SubjectPublicKeyInfo(
            new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPublicKey(var20.getModulus(), var20.getExponent())
         );
      } else if (var0 instanceof DSAPublicKeyParameters) {
         DSAPublicKeyParameters var19 = (DSAPublicKeyParameters)var0;
         DSAParameter var23 = null;
         DSAParameters var27 = var19.getParameters();
         if (var27 != null) {
            var23 = new DSAParameter(var27.getP(), var27.getQ(), var27.getG());
         }

         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, var23), new ASN1Integer(var19.getY()));
      } else if (var0 instanceof ECPublicKeyParameters) {
         ECPublicKeyParameters var18 = (ECPublicKeyParameters)var0;
         ECDomainParameters var22 = var18.getParameters();
         X962Parameters var25;
         if (var22 == null) {
            var25 = new X962Parameters(DERNull.INSTANCE);
         } else {
            if (var22 instanceof ECGOST3410Parameters) {
               ECGOST3410Parameters var29 = (ECGOST3410Parameters)var22;
               BigInteger var5 = var18.getQ().getAffineXCoord().toBigInteger();
               BigInteger var6 = var18.getQ().getAffineYCoord().toBigInteger();
               GOST3410PublicKeyAlgParameters var26 = new GOST3410PublicKeyAlgParameters(var29.getPublicKeyParamSet(), var29.getDigestParamSet());
               short var7;
               byte var8;
               ASN1ObjectIdentifier var9;
               if (cryptoProOids.contains(var29.getPublicKeyParamSet())) {
                  var7 = 64;
                  var8 = 32;
                  var9 = CryptoProObjectIdentifiers.gostR3410_2001;
               } else {
                  boolean var10 = var5.bitLength() > 256;
                  if (var10) {
                     var7 = 128;
                     var8 = 64;
                     var9 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
                  } else {
                     var7 = 64;
                     var8 = 32;
                     var9 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
                  }
               }

               byte[] var30 = new byte[var7];
               extractBytes(var30, var7 / 2, 0, var5);
               extractBytes(var30, var7 / 2, var8, var6);

               try {
                  return new SubjectPublicKeyInfo(new AlgorithmIdentifier(var9, var26), new DEROctetString(var30));
               } catch (IOException var12) {
                  return null;
               }
            }

            if (var22 instanceof ECNamedDomainParameters) {
               var25 = new X962Parameters(((ECNamedDomainParameters)var22).getName());
            } else {
               X9ECParameters var4 = new X9ECParameters(var22.getCurve(), new X9ECPoint(var22.getG(), false), var22.getN(), var22.getH(), var22.getSeed());
               var25 = new X962Parameters(var4);
            }
         }

         byte[] var28 = var18.getQ().getEncoded(false);
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var25), var28);
      } else if (var0 instanceof X448PublicKeyParameters) {
         X448PublicKeyParameters var17 = (X448PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X448), var17.getEncoded());
      } else if (var0 instanceof X25519PublicKeyParameters) {
         X25519PublicKeyParameters var16 = (X25519PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_X25519), var16.getEncoded());
      } else if (var0 instanceof Ed448PublicKeyParameters) {
         Ed448PublicKeyParameters var15 = (Ed448PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448), var15.getEncoded());
      } else if (var0 instanceof Ed25519PublicKeyParameters) {
         Ed25519PublicKeyParameters var14 = (Ed25519PublicKeyParameters)var0;
         return new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), var14.getEncoded());
      } else if (var0 instanceof HSSPublicKeyParameters) {
         HSSPublicKeyParameters var13 = (HSSPublicKeyParameters)var0;
         byte[] var21 = Composer.compose().u32str(var13.getL()).bytes(var13.getLMSPublicKey()).build();
         AlgorithmIdentifier var24 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var24, Arrays.concatenate(new byte[]{4, (byte)var21.length}, var21));
      } else if (var0 instanceof LMSPublicKeyParameters) {
         LMSPublicKeyParameters var1 = (LMSPublicKeyParameters)var0;
         byte[] var2 = Composer.compose().u32str(1).bytes(var1).build();
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var3, Arrays.concatenate(new byte[]{4, (byte)var2.length}, var2));
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
