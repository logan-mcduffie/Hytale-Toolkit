package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.asn1.CMCEPrivateKey;
import org.bouncycastle.pqc.asn1.CMCEPublicKey;
import org.bouncycastle.pqc.asn1.FalconPrivateKey;
import org.bouncycastle.pqc.asn1.FalconPublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPrivateKey;
import org.bouncycastle.pqc.asn1.XMSSPrivateKey;
import org.bouncycastle.pqc.crypto.bike.BIKEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.BDSStateMap;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSUtil;
import org.bouncycastle.util.Pack;

public class PrivateKeyInfoFactory {
   private PrivateKeyInfoFactory() {
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      return createPrivateKeyInfo(var0, null);
   }

   public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter var0, ASN1Set var1) throws IOException {
      if (var0 instanceof SPHINCSPrivateKeyParameters) {
         SPHINCSPrivateKeyParameters var29 = (SPHINCSPrivateKeyParameters)var0;
         AlgorithmIdentifier var52 = new AlgorithmIdentifier(
            PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(var29.getTreeDigest()))
         );
         return new PrivateKeyInfo(var52, new DEROctetString(var29.getKeyData()));
      } else if (!(var0 instanceof NHPrivateKeyParameters)) {
         if (var0 instanceof LMSPrivateKeyParameters) {
            LMSPrivateKeyParameters var28 = (LMSPrivateKeyParameters)var0;
            byte[] var51 = Composer.compose().u32str(1).bytes(var28).build();
            byte[] var68 = Composer.compose().u32str(1).bytes(var28.getPublicKey()).build();
            AlgorithmIdentifier var72 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new PrivateKeyInfo(var72, new DEROctetString(var51), var1, var68);
         } else if (var0 instanceof HSSPrivateKeyParameters) {
            HSSPrivateKeyParameters var27 = (HSSPrivateKeyParameters)var0;
            byte[] var50 = Composer.compose().u32str(var27.getL()).bytes(var27).build();
            byte[] var67 = Composer.compose().u32str(var27.getL()).bytes(var27.getPublicKey().getLMSPublicKey()).build();
            AlgorithmIdentifier var71 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
            return new PrivateKeyInfo(var71, new DEROctetString(var50), var1, var67);
         } else if (var0 instanceof SPHINCSPlusPrivateKeyParameters) {
            SPHINCSPlusPrivateKeyParameters var26 = (SPHINCSPlusPrivateKeyParameters)var0;
            AlgorithmIdentifier var49 = new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(var26.getParameters()));
            return new PrivateKeyInfo(var49, new DEROctetString(var26.getEncoded()), var1, var26.getPublicKey());
         } else if (var0 instanceof SLHDSAPrivateKeyParameters) {
            SLHDSAPrivateKeyParameters var25 = (SLHDSAPrivateKeyParameters)var0;
            AlgorithmIdentifier var48 = new AlgorithmIdentifier(Utils.slhdsaOidLookup(var25.getParameters()));
            return new PrivateKeyInfo(var48, var25.getEncoded(), var1);
         } else if (var0 instanceof PicnicPrivateKeyParameters) {
            PicnicPrivateKeyParameters var24 = (PicnicPrivateKeyParameters)var0;
            byte[] var47 = var24.getEncoded();
            AlgorithmIdentifier var66 = new AlgorithmIdentifier(Utils.picnicOidLookup(var24.getParameters()));
            return new PrivateKeyInfo(var66, new DEROctetString(var47), var1);
         } else if (var0 instanceof CMCEPrivateKeyParameters) {
            CMCEPrivateKeyParameters var23 = (CMCEPrivateKeyParameters)var0;
            AlgorithmIdentifier var46 = new AlgorithmIdentifier(Utils.mcElieceOidLookup(var23.getParameters()));
            CMCEPublicKey var65 = new CMCEPublicKey(var23.reconstructPublicKey());
            CMCEPrivateKey var70 = new CMCEPrivateKey(0, var23.getDelta(), var23.getC(), var23.getG(), var23.getAlpha(), var23.getS(), var65);
            return new PrivateKeyInfo(var46, var70, var1);
         } else if (var0 instanceof XMSSPrivateKeyParameters) {
            XMSSPrivateKeyParameters var22 = (XMSSPrivateKeyParameters)var0;
            AlgorithmIdentifier var45 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss, new XMSSKeyParams(var22.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(var22.getTreeDigest()))
            );
            return new PrivateKeyInfo(var45, xmssCreateKeyStructure(var22), var1);
         } else if (var0 instanceof XMSSMTPrivateKeyParameters) {
            XMSSMTPrivateKeyParameters var21 = (XMSSMTPrivateKeyParameters)var0;
            AlgorithmIdentifier var44 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss_mt,
               new XMSSMTKeyParams(var21.getParameters().getHeight(), var21.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(var21.getTreeDigest()))
            );
            return new PrivateKeyInfo(var44, xmssmtCreateKeyStructure(var21), var1);
         } else if (var0 instanceof FrodoPrivateKeyParameters) {
            FrodoPrivateKeyParameters var20 = (FrodoPrivateKeyParameters)var0;
            byte[] var43 = var20.getEncoded();
            AlgorithmIdentifier var64 = new AlgorithmIdentifier(Utils.frodoOidLookup(var20.getParameters()));
            return new PrivateKeyInfo(var64, new DEROctetString(var43), var1);
         } else if (var0 instanceof SABERPrivateKeyParameters) {
            SABERPrivateKeyParameters var19 = (SABERPrivateKeyParameters)var0;
            byte[] var42 = var19.getEncoded();
            AlgorithmIdentifier var63 = new AlgorithmIdentifier(Utils.saberOidLookup(var19.getParameters()));
            return new PrivateKeyInfo(var63, new DEROctetString(var42), var1);
         } else if (var0 instanceof NTRUPrivateKeyParameters) {
            NTRUPrivateKeyParameters var18 = (NTRUPrivateKeyParameters)var0;
            byte[] var41 = var18.getEncoded();
            AlgorithmIdentifier var62 = new AlgorithmIdentifier(Utils.ntruOidLookup(var18.getParameters()));
            return new PrivateKeyInfo(var62, new DEROctetString(var41), var1);
         } else if (var0 instanceof FalconPrivateKeyParameters) {
            FalconPrivateKeyParameters var17 = (FalconPrivateKeyParameters)var0;
            AlgorithmIdentifier var40 = new AlgorithmIdentifier(Utils.falconOidLookup(var17.getParameters()));
            FalconPublicKey var61 = new FalconPublicKey(var17.getPublicKey());
            FalconPrivateKey var69 = new FalconPrivateKey(0, var17.getSpolyf(), var17.getG(), var17.getSpolyF(), var61);
            return new PrivateKeyInfo(var40, var69, var1);
         } else if (var0 instanceof MLKEMPrivateKeyParameters) {
            MLKEMPrivateKeyParameters var16 = (MLKEMPrivateKeyParameters)var0;
            AlgorithmIdentifier var39 = new AlgorithmIdentifier(Utils.mlkemOidLookup(var16.getParameters()));
            if (var16.getPreferredFormat() == 1) {
               return new PrivateKeyInfo(var39, new DERTaggedObject(false, 0, new DEROctetString(var16.getSeed())), var1);
            } else {
               return var16.getPreferredFormat() == 2
                  ? new PrivateKeyInfo(var39, new DEROctetString(var16.getEncoded()), var1)
                  : new PrivateKeyInfo(var39, getBasicPQCEncoding(var16.getSeed(), var16.getEncoded()), var1);
            }
         } else if (var0 instanceof NTRULPRimePrivateKeyParameters) {
            NTRULPRimePrivateKeyParameters var15 = (NTRULPRimePrivateKeyParameters)var0;
            ASN1EncodableVector var38 = new ASN1EncodableVector();
            var38.add(new DEROctetString(var15.getEnca()));
            var38.add(new DEROctetString(var15.getPk()));
            var38.add(new DEROctetString(var15.getRho()));
            var38.add(new DEROctetString(var15.getHash()));
            AlgorithmIdentifier var60 = new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(var15.getParameters()));
            return new PrivateKeyInfo(var60, new DERSequence(var38), var1);
         } else if (var0 instanceof SNTRUPrimePrivateKeyParameters) {
            SNTRUPrimePrivateKeyParameters var14 = (SNTRUPrimePrivateKeyParameters)var0;
            ASN1EncodableVector var37 = new ASN1EncodableVector();
            var37.add(new DEROctetString(var14.getF()));
            var37.add(new DEROctetString(var14.getGinv()));
            var37.add(new DEROctetString(var14.getPk()));
            var37.add(new DEROctetString(var14.getRho()));
            var37.add(new DEROctetString(var14.getHash()));
            AlgorithmIdentifier var59 = new AlgorithmIdentifier(Utils.sntruprimeOidLookup(var14.getParameters()));
            return new PrivateKeyInfo(var59, new DERSequence(var37), var1);
         } else if (var0 instanceof MLDSAPrivateKeyParameters) {
            MLDSAPrivateKeyParameters var13 = (MLDSAPrivateKeyParameters)var0;
            AlgorithmIdentifier var36 = new AlgorithmIdentifier(Utils.mldsaOidLookup(var13.getParameters()));
            if (var13.getPreferredFormat() == 1) {
               return new PrivateKeyInfo(var36, new DERTaggedObject(false, 0, new DEROctetString(var13.getSeed())), var1);
            } else {
               return var13.getPreferredFormat() == 2
                  ? new PrivateKeyInfo(var36, new DEROctetString(var13.getEncoded()), var1)
                  : new PrivateKeyInfo(var36, getBasicPQCEncoding(var13.getSeed(), var13.getEncoded()), var1);
            }
         } else if (var0 instanceof DilithiumPrivateKeyParameters) {
            DilithiumPrivateKeyParameters var12 = (DilithiumPrivateKeyParameters)var0;
            AlgorithmIdentifier var35 = new AlgorithmIdentifier(Utils.dilithiumOidLookup(var12.getParameters()));
            DilithiumPublicKeyParameters var58 = var12.getPublicKeyParameters();
            return new PrivateKeyInfo(var35, new DEROctetString(var12.getEncoded()), var1, var58.getEncoded());
         } else if (var0 instanceof BIKEPrivateKeyParameters) {
            BIKEPrivateKeyParameters var11 = (BIKEPrivateKeyParameters)var0;
            AlgorithmIdentifier var34 = new AlgorithmIdentifier(Utils.bikeOidLookup(var11.getParameters()));
            byte[] var57 = var11.getEncoded();
            return new PrivateKeyInfo(var34, new DEROctetString(var57), var1);
         } else if (var0 instanceof HQCPrivateKeyParameters) {
            HQCPrivateKeyParameters var10 = (HQCPrivateKeyParameters)var0;
            AlgorithmIdentifier var33 = new AlgorithmIdentifier(Utils.hqcOidLookup(var10.getParameters()));
            byte[] var56 = var10.getEncoded();
            return new PrivateKeyInfo(var33, new DEROctetString(var56), var1);
         } else if (var0 instanceof RainbowPrivateKeyParameters) {
            RainbowPrivateKeyParameters var9 = (RainbowPrivateKeyParameters)var0;
            AlgorithmIdentifier var32 = new AlgorithmIdentifier(Utils.rainbowOidLookup(var9.getParameters()));
            byte[] var55 = var9.getEncoded();
            return new PrivateKeyInfo(var32, new DEROctetString(var55), var1);
         } else if (var0 instanceof MayoPrivateKeyParameters) {
            MayoPrivateKeyParameters var8 = (MayoPrivateKeyParameters)var0;
            AlgorithmIdentifier var31 = new AlgorithmIdentifier(Utils.mayoOidLookup(var8.getParameters()));
            byte[] var54 = var8.getEncoded();
            return new PrivateKeyInfo(var31, new DEROctetString(var54), var1);
         } else if (var0 instanceof SnovaPrivateKeyParameters) {
            SnovaPrivateKeyParameters var7 = (SnovaPrivateKeyParameters)var0;
            AlgorithmIdentifier var30 = new AlgorithmIdentifier(Utils.snovaOidLookup(var7.getParameters()));
            byte[] var53 = var7.getEncoded();
            return new PrivateKeyInfo(var30, new DEROctetString(var53), var1);
         } else {
            throw new IOException("key parameters not recognized");
         }
      } else {
         NHPrivateKeyParameters var2 = (NHPrivateKeyParameters)var0;
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
         short[] var4 = var2.getSecData();
         byte[] var5 = new byte[var4.length * 2];

         for (int var6 = 0; var6 != var4.length; var6++) {
            Pack.shortToLittleEndian(var4[var6], var5, var6 * 2);
         }

         return new PrivateKeyInfo(var3, new DEROctetString(var5));
      }
   }

   private static XMSSPrivateKey xmssCreateKeyStructure(XMSSPrivateKeyParameters var0) throws IOException {
      byte[] var1 = var0.getEncoded();
      int var2 = var0.getParameters().getTreeDigestSize();
      int var3 = var0.getParameters().getHeight();
      byte var4 = 4;
      int var9 = 0;
      int var10 = (int)XMSSUtil.bytesToXBigEndian(var1, var9, var4);
      if (!XMSSUtil.isIndexValid(var3, var10)) {
         throw new IllegalArgumentException("index out of bounds");
      } else {
         var9 += var4;
         byte[] var11 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var12 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var13 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var14 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var15 = XMSSUtil.extractBytesAtOffset(var1, var9, var1.length - var9);
         Object var16 = null;

         try {
            var16 = (BDS)XMSSUtil.deserialize(var15, BDS.class);
         } catch (ClassNotFoundException var18) {
            throw new IOException("cannot parse BDS: " + var18.getMessage());
         }

         return ((BDS)var16).getMaxIndex() != (1 << var3) - 1
            ? new XMSSPrivateKey(var10, var11, var12, var13, var14, var15, ((BDS)var16).getMaxIndex())
            : new XMSSPrivateKey(var10, var11, var12, var13, var14, var15);
      }
   }

   private static ASN1Sequence getBasicPQCEncoding(byte[] var0, byte[] var1) {
      return new DERSequence(new DEROctetString(var0), new DEROctetString(var1));
   }

   private static XMSSMTPrivateKey xmssmtCreateKeyStructure(XMSSMTPrivateKeyParameters var0) throws IOException {
      byte[] var1 = var0.getEncoded();
      int var2 = var0.getParameters().getTreeDigestSize();
      int var3 = var0.getParameters().getHeight();
      int var4 = (var3 + 7) / 8;
      int var9 = 0;
      int var10 = (int)XMSSUtil.bytesToXBigEndian(var1, var9, var4);
      if (!XMSSUtil.isIndexValid(var3, var10)) {
         throw new IllegalArgumentException("index out of bounds");
      } else {
         var9 += var4;
         byte[] var11 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var12 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var13 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var14 = XMSSUtil.extractBytesAtOffset(var1, var9, var2);
         var9 += var2;
         byte[] var15 = XMSSUtil.extractBytesAtOffset(var1, var9, var1.length - var9);
         Object var16 = null;

         try {
            var16 = (BDSStateMap)XMSSUtil.deserialize(var15, BDSStateMap.class);
         } catch (ClassNotFoundException var18) {
            throw new IOException("cannot parse BDSStateMap: " + var18.getMessage());
         }

         return ((BDSStateMap)var16).getMaxIndex() != (1L << var3) - 1L
            ? new XMSSMTPrivateKey(var10, var11, var12, var13, var14, var15, ((BDSStateMap)var16).getMaxIndex())
            : new XMSSMTPrivateKey(var10, var11, var12, var13, var14, var15);
      }
   }
}
