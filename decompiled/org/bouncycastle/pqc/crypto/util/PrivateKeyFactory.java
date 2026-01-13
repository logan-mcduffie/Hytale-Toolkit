package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.CMCEPrivateKey;
import org.bouncycastle.pqc.asn1.FalconPrivateKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.SPHINCSPLUSPrivateKey;
import org.bouncycastle.pqc.asn1.SPHINCSPLUSPublicKey;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class PrivateKeyFactory {
   public static AsymmetricKeyParameter createKey(byte[] var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("privateKeyInfoData array null");
      } else if (var0.length == 0) {
         throw new IllegalArgumentException("privateKeyInfoData array empty");
      } else {
         return createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var0)));
      }
   }

   public static AsymmetricKeyParameter createKey(InputStream var0) throws IOException {
      return createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(var0).readObject()));
   }

   public static AsymmetricKeyParameter createKey(PrivateKeyInfo var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("keyInfo array null");
      } else {
         AlgorithmIdentifier var1 = var0.getPrivateKeyAlgorithm();
         ASN1ObjectIdentifier var2 = var1.getAlgorithm();
         if (var2.equals(PQCObjectIdentifiers.sphincs256)) {
            return new SPHINCSPrivateKeyParameters(
               ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets(),
               Utils.sphincs256LookupTreeAlgName(SPHINCS256KeyParams.getInstance(var1.getParameters()))
            );
         } else if (var2.equals(PQCObjectIdentifiers.newHope)) {
            return new NHPrivateKeyParameters(convert(ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets()));
         } else if (var2.equals(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig)) {
            ASN1OctetString var32 = parseOctetString(var0.getPrivateKey(), 64);
            byte[] var53 = var32.getOctets();
            ASN1BitString var61 = var0.getPublicKeyData();
            if (var61 != null) {
               byte[] var69 = var61.getOctets();
               return HSSPrivateKeyParameters.getInstance(Arrays.copyOfRange(var53, 4, var53.length), var69);
            } else {
               return HSSPrivateKeyParameters.getInstance(Arrays.copyOfRange(var53, 4, var53.length));
            }
         } else if (var2.on(BCObjectIdentifiers.sphincsPlus) || var2.on(BCObjectIdentifiers.sphincsPlus_interop)) {
            SPHINCSPlusParameters var31 = Utils.sphincsPlusParamsLookup(var2);
            ASN1Encodable var52 = var0.parsePrivateKey();
            if (var52 instanceof ASN1Sequence) {
               SPHINCSPLUSPrivateKey var60 = SPHINCSPLUSPrivateKey.getInstance(var52);
               SPHINCSPLUSPublicKey var68 = var60.getPublicKey();
               return new SPHINCSPlusPrivateKeyParameters(var31, var60.getSkseed(), var60.getSkprf(), var68.getPkseed(), var68.getPkroot());
            } else {
               return new SPHINCSPlusPrivateKeyParameters(var31, ASN1OctetString.getInstance(var52).getOctets());
            }
         } else if (Utils.slhdsaParams.containsKey(var2)) {
            SLHDSAParameters var30 = Utils.slhdsaParamsLookup(var2);
            ASN1OctetString var51 = parseOctetString(var0.getPrivateKey(), var30.getN() * 4);
            return new SLHDSAPrivateKeyParameters(var30, var51.getOctets());
         } else if (var2.on(BCObjectIdentifiers.picnic)) {
            byte[] var29 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            PicnicParameters var50 = Utils.picnicParamsLookup(var2);
            return new PicnicPrivateKeyParameters(var50, var29);
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_mceliece)) {
            CMCEPrivateKey var28 = CMCEPrivateKey.getInstance(var0.parsePrivateKey());
            CMCEParameters var49 = Utils.mcElieceParamsLookup(var2);
            return new CMCEPrivateKeyParameters(var49, var28.getDelta(), var28.getC(), var28.getG(), var28.getAlpha(), var28.getS());
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_frodo)) {
            byte[] var27 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            FrodoParameters var48 = Utils.frodoParamsLookup(var2);
            return new FrodoPrivateKeyParameters(var48, var27);
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_saber)) {
            byte[] var26 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            SABERParameters var47 = Utils.saberParamsLookup(var2);
            return new SABERPrivateKeyParameters(var47, var26);
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_ntru)) {
            byte[] var25 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            NTRUParameters var46 = Utils.ntruParamsLookup(var2);
            return new NTRUPrivateKeyParameters(var46, var25);
         } else if (var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_512)
            || var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_768)
            || var2.equals(NISTObjectIdentifiers.id_alg_ml_kem_1024)) {
            ASN1Primitive var24 = parsePrimitiveString(var0.getPrivateKey(), 64);
            MLKEMParameters var45 = Utils.mlkemParamsLookup(var2);
            MLKEMPublicKeyParameters var59 = null;
            if (var0.getPublicKeyData() != null) {
               var59 = PublicKeyFactory.MLKEMConverter.getPublicKeyParams(var45, var0.getPublicKeyData());
            }

            if (var24 instanceof ASN1OctetString) {
               return new MLKEMPrivateKeyParameters(var45, ((ASN1OctetString)var24).getOctets(), var59);
            } else if (var24 instanceof ASN1Sequence) {
               ASN1Sequence var67 = (ASN1Sequence)var24;
               byte[] var74 = ASN1OctetString.getInstance(var67.getObjectAt(0)).getOctets();
               byte[] var75 = ASN1OctetString.getInstance(var67.getObjectAt(1)).getOctets();
               MLKEMPrivateKeyParameters var76 = new MLKEMPrivateKeyParameters(var45, var74, var59);
               if (!Arrays.constantTimeAreEqual(var76.getEncoded(), var75)) {
                  throw new IllegalArgumentException("inconsistent " + var45.getName() + " private key");
               } else {
                  return var76;
               }
            } else {
               throw new IllegalArgumentException("invalid " + var45.getName() + " private key");
            }
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_ntrulprime)) {
            ASN1Sequence var23 = ASN1Sequence.getInstance(var0.parsePrivateKey());
            NTRULPRimeParameters var44 = Utils.ntrulprimeParamsLookup(var2);
            return new NTRULPRimePrivateKeyParameters(
               var44,
               ASN1OctetString.getInstance(var23.getObjectAt(0)).getOctets(),
               ASN1OctetString.getInstance(var23.getObjectAt(1)).getOctets(),
               ASN1OctetString.getInstance(var23.getObjectAt(2)).getOctets(),
               ASN1OctetString.getInstance(var23.getObjectAt(3)).getOctets()
            );
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_sntruprime)) {
            ASN1Sequence var22 = ASN1Sequence.getInstance(var0.parsePrivateKey());
            SNTRUPrimeParameters var43 = Utils.sntruprimeParamsLookup(var2);
            return new SNTRUPrimePrivateKeyParameters(
               var43,
               ASN1OctetString.getInstance(var22.getObjectAt(0)).getOctets(),
               ASN1OctetString.getInstance(var22.getObjectAt(1)).getOctets(),
               ASN1OctetString.getInstance(var22.getObjectAt(2)).getOctets(),
               ASN1OctetString.getInstance(var22.getObjectAt(3)).getOctets(),
               ASN1OctetString.getInstance(var22.getObjectAt(4)).getOctets()
            );
         } else if (Utils.mldsaParams.containsKey(var2)) {
            ASN1Primitive var21 = parsePrimitiveString(var0.getPrivateKey(), 32);
            MLDSAParameters var42 = Utils.mldsaParamsLookup(var2);
            MLDSAPublicKeyParameters var58 = null;
            if (var0.getPublicKeyData() != null) {
               var58 = PublicKeyFactory.MLDSAConverter.getPublicKeyParams(var42, var0.getPublicKeyData());
            }

            if (var21 instanceof ASN1OctetString) {
               return new MLDSAPrivateKeyParameters(var42, ((ASN1OctetString)var21).getOctets(), var58);
            } else if (var21 instanceof ASN1Sequence) {
               ASN1Sequence var66 = (ASN1Sequence)var21;
               byte[] var73 = ASN1OctetString.getInstance(var66.getObjectAt(0)).getOctets();
               byte[] var8 = ASN1OctetString.getInstance(var66.getObjectAt(1)).getOctets();
               MLDSAPrivateKeyParameters var9 = new MLDSAPrivateKeyParameters(var42, var73, var58);
               if (!Arrays.constantTimeAreEqual(var9.getEncoded(), var8)) {
                  throw new IllegalArgumentException("inconsistent " + var42.getName() + " private key");
               } else {
                  return var9;
               }
            } else {
               throw new IllegalArgumentException("invalid " + var42.getName() + " private key");
            }
         } else if (var2.equals(BCObjectIdentifiers.dilithium2) || var2.equals(BCObjectIdentifiers.dilithium3) || var2.equals(BCObjectIdentifiers.dilithium5)) {
            ASN1Encodable var20 = var0.parsePrivateKey();
            DilithiumParameters var41 = Utils.dilithiumParamsLookup(var2);
            if (var20 instanceof ASN1Sequence) {
               ASN1Sequence var57 = ASN1Sequence.getInstance(var20);
               int var65 = ASN1Integer.getInstance(var57.getObjectAt(0)).intValueExact();
               if (var65 != 0) {
                  throw new IOException("unknown private key version: " + var65);
               } else if (var0.getPublicKeyData() != null) {
                  DilithiumPublicKeyParameters var72 = PublicKeyFactory.DilithiumConverter.getPublicKeyParams(var41, var0.getPublicKeyData());
                  return new DilithiumPrivateKeyParameters(
                     var41,
                     ASN1BitString.getInstance(var57.getObjectAt(1)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(2)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(3)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(4)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(5)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(6)).getOctets(),
                     var72.getT1()
                  );
               } else {
                  return new DilithiumPrivateKeyParameters(
                     var41,
                     ASN1BitString.getInstance(var57.getObjectAt(1)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(2)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(3)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(4)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(5)).getOctets(),
                     ASN1BitString.getInstance(var57.getObjectAt(6)).getOctets(),
                     null
                  );
               }
            } else if (var20 instanceof DEROctetString) {
               byte[] var56 = ASN1OctetString.getInstance(var20).getOctets();
               if (var0.getPublicKeyData() != null) {
                  DilithiumPublicKeyParameters var64 = PublicKeyFactory.DilithiumConverter.getPublicKeyParams(var41, var0.getPublicKeyData());
                  return new DilithiumPrivateKeyParameters(var41, var56, var64);
               } else {
                  return new DilithiumPrivateKeyParameters(var41, var56, null);
               }
            } else {
               throw new IOException("not supported");
            }
         } else if (var2.equals(BCObjectIdentifiers.falcon_512) || var2.equals(BCObjectIdentifiers.falcon_1024)) {
            FalconPrivateKey var19 = FalconPrivateKey.getInstance(var0.parsePrivateKey());
            FalconParameters var40 = Utils.falconParamsLookup(var2);
            return new FalconPrivateKeyParameters(var40, var19.getf(), var19.getG(), var19.getF(), var19.getPublicKey().getH());
         } else if (var2.equals(BCObjectIdentifiers.old_falcon_512) || var2.equals(BCObjectIdentifiers.old_falcon_1024)) {
            FalconPrivateKey var18 = FalconPrivateKey.getInstance(var0.parsePrivateKey());
            FalconParameters var39 = Utils.falconParamsLookup(var2);
            return new FalconPrivateKeyParameters(var39, var18.getf(), var18.getG(), var18.getF(), var18.getPublicKey().getH());
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_bike)) {
            byte[] var17 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            BIKEParameters var38 = Utils.bikeParamsLookup(var2);
            byte[] var55 = Arrays.copyOfRange(var17, 0, var38.getRByte());
            byte[] var63 = Arrays.copyOfRange(var17, var38.getRByte(), 2 * var38.getRByte());
            byte[] var71 = Arrays.copyOfRange(var17, 2 * var38.getRByte(), var17.length);
            return new BIKEPrivateKeyParameters(var38, var55, var63, var71);
         } else if (var2.on(BCObjectIdentifiers.pqc_kem_hqc)) {
            byte[] var16 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            HQCParameters var37 = Utils.hqcParamsLookup(var2);
            return new HQCPrivateKeyParameters(var37, var16);
         } else if (var2.on(BCObjectIdentifiers.rainbow)) {
            byte[] var15 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            RainbowParameters var36 = Utils.rainbowParamsLookup(var2);
            return new RainbowPrivateKeyParameters(var36, var15);
         } else if (var2.equals(PQCObjectIdentifiers.xmss)) {
            XMSSKeyParams var14 = XMSSKeyParams.getInstance(var1.getParameters());
            ASN1ObjectIdentifier var35 = var14.getTreeDigest().getAlgorithm();
            XMSSPrivateKey var54 = XMSSPrivateKey.getInstance(var0.parsePrivateKey());

            try {
               XMSSPrivateKeyParameters.Builder var62 = new XMSSPrivateKeyParameters.Builder(new XMSSParameters(var14.getHeight(), Utils.getDigest(var35)))
                  .withIndex(var54.getIndex())
                  .withSecretKeySeed(var54.getSecretKeySeed())
                  .withSecretKeyPRF(var54.getSecretKeyPRF())
                  .withPublicSeed(var54.getPublicSeed())
                  .withRoot(var54.getRoot());
               if (var54.getVersion() != 0) {
                  var62.withMaxIndex(var54.getMaxIndex());
               }

               if (var54.getBdsState() != null) {
                  BDS var70 = (BDS)XMSSUtil.deserialize(var54.getBdsState(), BDS.class);
                  var62.withBDSState(var70.withWOTSDigest(var35));
               }

               return var62.build();
            } catch (ClassNotFoundException var10) {
               throw new IOException("ClassNotFoundException processing BDS state: " + var10.getMessage());
            }
         } else if (var2.equals(PQCObjectIdentifiers.xmss_mt)) {
            XMSSMTKeyParams var13 = XMSSMTKeyParams.getInstance(var1.getParameters());
            ASN1ObjectIdentifier var34 = var13.getTreeDigest().getAlgorithm();

            try {
               XMSSMTPrivateKey var5 = XMSSMTPrivateKey.getInstance(var0.parsePrivateKey());
               XMSSMTPrivateKeyParameters.Builder var6 = new XMSSMTPrivateKeyParameters.Builder(
                     new XMSSMTParameters(var13.getHeight(), var13.getLayers(), Utils.getDigest(var34))
                  )
                  .withIndex(var5.getIndex())
                  .withSecretKeySeed(var5.getSecretKeySeed())
                  .withSecretKeyPRF(var5.getSecretKeyPRF())
                  .withPublicSeed(var5.getPublicSeed())
                  .withRoot(var5.getRoot());
               if (var5.getVersion() != 0) {
                  var6.withMaxIndex(var5.getMaxIndex());
               }

               if (var5.getBdsState() != null) {
                  BDSStateMap var7 = (BDSStateMap)XMSSUtil.deserialize(var5.getBdsState(), BDSStateMap.class);
                  var6.withBDSState(var7.withWOTSDigest(var34));
               }

               return var6.build();
            } catch (ClassNotFoundException var11) {
               throw new IOException("ClassNotFoundException processing BDS state: " + var11.getMessage());
            }
         } else if (var2.on(BCObjectIdentifiers.mayo)) {
            byte[] var12 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            MayoParameters var33 = Utils.mayoParamsLookup(var2);
            return new MayoPrivateKeyParameters(var33, var12);
         } else if (var2.on(BCObjectIdentifiers.snova)) {
            byte[] var3 = ASN1OctetString.getInstance(var0.parsePrivateKey()).getOctets();
            SnovaParameters var4 = Utils.snovaParamsLookup(var2);
            return new SnovaPrivateKeyParameters(var4, var3);
         } else {
            throw new RuntimeException("algorithm identifier in private key not recognised");
         }
      }
   }

   private static ASN1OctetString parseOctetString(ASN1OctetString var0, int var1) throws IOException {
      byte[] var2 = var0.getOctets();
      if (var2.length == var1) {
         return var0;
      } else {
         ASN1OctetString var3 = Utils.parseOctetData(var2);
         return var3 != null ? ASN1OctetString.getInstance(var3) : var0;
      }
   }

   private static ASN1Primitive parsePrimitiveString(ASN1OctetString var0, int var1) throws IOException {
      byte[] var2 = var0.getOctets();
      if (var2.length == var1) {
         return var0;
      } else {
         ASN1Primitive var3 = Utils.parseData(var2);
         if (var3 instanceof ASN1OctetString) {
            return ASN1OctetString.getInstance(var3);
         } else {
            return (ASN1Primitive)(var3 instanceof ASN1Sequence ? ASN1Sequence.getInstance(var3) : var0);
         }
      }
   }

   private static short[] convert(byte[] var0) {
      short[] var1 = new short[var0.length / 2];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = Pack.littleEndianToShort(var0, var2 * 2);
      }

      return var1;
   }
}
