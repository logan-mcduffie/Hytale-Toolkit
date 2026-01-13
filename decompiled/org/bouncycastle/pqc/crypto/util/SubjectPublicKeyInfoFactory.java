package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTPublicKey;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.Composer;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

public class SubjectPublicKeyInfoFactory {
   private SubjectPublicKeyInfoFactory() {
   }

   public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter var0) throws IOException {
      if (var0 instanceof SPHINCSPublicKeyParameters) {
         SPHINCSPublicKeyParameters var28 = (SPHINCSPublicKeyParameters)var0;
         AlgorithmIdentifier var51 = new AlgorithmIdentifier(
            PQCObjectIdentifiers.sphincs256, new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(var28.getTreeDigest()))
         );
         return new SubjectPublicKeyInfo(var51, var28.getKeyData());
      } else if (var0 instanceof NHPublicKeyParameters) {
         NHPublicKeyParameters var27 = (NHPublicKeyParameters)var0;
         AlgorithmIdentifier var50 = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
         return new SubjectPublicKeyInfo(var50, var27.getPubData());
      } else if (var0 instanceof LMSPublicKeyParameters) {
         LMSPublicKeyParameters var26 = (LMSPublicKeyParameters)var0;
         byte[] var49 = Composer.compose().u32str(1).bytes(var26).build();
         AlgorithmIdentifier var69 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var69, var49);
      } else if (var0 instanceof HSSPublicKeyParameters) {
         HSSPublicKeyParameters var25 = (HSSPublicKeyParameters)var0;
         byte[] var48 = Composer.compose().u32str(var25.getL()).bytes(var25.getLMSPublicKey()).build();
         AlgorithmIdentifier var68 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig);
         return new SubjectPublicKeyInfo(var68, var48);
      } else if (var0 instanceof SLHDSAPublicKeyParameters) {
         SLHDSAPublicKeyParameters var24 = (SLHDSAPublicKeyParameters)var0;
         byte[] var47 = var24.getEncoded();
         AlgorithmIdentifier var67 = new AlgorithmIdentifier(Utils.slhdsaOidLookup(var24.getParameters()));
         return new SubjectPublicKeyInfo(var67, var47);
      } else if (var0 instanceof SPHINCSPlusPublicKeyParameters) {
         SPHINCSPlusPublicKeyParameters var23 = (SPHINCSPlusPublicKeyParameters)var0;
         byte[] var46 = var23.getEncoded();
         AlgorithmIdentifier var66 = new AlgorithmIdentifier(Utils.sphincsPlusOidLookup(var23.getParameters()));
         return new SubjectPublicKeyInfo(var66, var46);
      } else if (var0 instanceof CMCEPublicKeyParameters) {
         CMCEPublicKeyParameters var22 = (CMCEPublicKeyParameters)var0;
         byte[] var45 = var22.getEncoded();
         AlgorithmIdentifier var65 = new AlgorithmIdentifier(Utils.mcElieceOidLookup(var22.getParameters()));
         return new SubjectPublicKeyInfo(var65, var45);
      } else if (var0 instanceof XMSSPublicKeyParameters) {
         XMSSPublicKeyParameters var21 = (XMSSPublicKeyParameters)var0;
         byte[] var44 = var21.getPublicSeed();
         byte[] var64 = var21.getRoot();
         byte[] var71 = var21.getEncoded();
         if (var71.length > var44.length + var64.length) {
            AlgorithmIdentifier var74 = new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmss);
            return new SubjectPublicKeyInfo(var74, new DEROctetString(var71));
         } else {
            AlgorithmIdentifier var73 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss, new XMSSKeyParams(var21.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(var21.getTreeDigest()))
            );
            return new SubjectPublicKeyInfo(var73, new XMSSPublicKey(var44, var64));
         }
      } else if (var0 instanceof XMSSMTPublicKeyParameters) {
         XMSSMTPublicKeyParameters var20 = (XMSSMTPublicKeyParameters)var0;
         byte[] var43 = var20.getPublicSeed();
         byte[] var63 = var20.getRoot();
         byte[] var70 = var20.getEncoded();
         if (var70.length > var43.length + var63.length) {
            AlgorithmIdentifier var72 = new AlgorithmIdentifier(IsaraObjectIdentifiers.id_alg_xmssmt);
            return new SubjectPublicKeyInfo(var72, new DEROctetString(var70));
         } else {
            AlgorithmIdentifier var5 = new AlgorithmIdentifier(
               PQCObjectIdentifiers.xmss_mt,
               new XMSSMTKeyParams(var20.getParameters().getHeight(), var20.getParameters().getLayers(), Utils.xmssLookupTreeAlgID(var20.getTreeDigest()))
            );
            return new SubjectPublicKeyInfo(var5, new XMSSMTPublicKey(var20.getPublicSeed(), var20.getRoot()));
         }
      } else if (var0 instanceof FrodoPublicKeyParameters) {
         FrodoPublicKeyParameters var19 = (FrodoPublicKeyParameters)var0;
         byte[] var42 = var19.getEncoded();
         AlgorithmIdentifier var62 = new AlgorithmIdentifier(Utils.frodoOidLookup(var19.getParameters()));
         return new SubjectPublicKeyInfo(var62, new DEROctetString(var42));
      } else if (var0 instanceof SABERPublicKeyParameters) {
         SABERPublicKeyParameters var18 = (SABERPublicKeyParameters)var0;
         byte[] var41 = var18.getEncoded();
         AlgorithmIdentifier var61 = new AlgorithmIdentifier(Utils.saberOidLookup(var18.getParameters()));
         return new SubjectPublicKeyInfo(var61, new DERSequence(new DEROctetString(var41)));
      } else if (var0 instanceof PicnicPublicKeyParameters) {
         PicnicPublicKeyParameters var17 = (PicnicPublicKeyParameters)var0;
         byte[] var40 = var17.getEncoded();
         AlgorithmIdentifier var60 = new AlgorithmIdentifier(Utils.picnicOidLookup(var17.getParameters()));
         return new SubjectPublicKeyInfo(var60, new DEROctetString(var40));
      } else if (var0 instanceof NTRUPublicKeyParameters) {
         NTRUPublicKeyParameters var16 = (NTRUPublicKeyParameters)var0;
         byte[] var39 = var16.getEncoded();
         AlgorithmIdentifier var59 = new AlgorithmIdentifier(Utils.ntruOidLookup(var16.getParameters()));
         return new SubjectPublicKeyInfo(var59, var39);
      } else if (var0 instanceof FalconPublicKeyParameters) {
         FalconPublicKeyParameters var15 = (FalconPublicKeyParameters)var0;
         byte[] var38 = var15.getH();
         AlgorithmIdentifier var58 = new AlgorithmIdentifier(Utils.falconOidLookup(var15.getParameters()));
         byte[] var4 = new byte[var38.length + 1];
         var4[0] = (byte)(0 + var15.getParameters().getLogN());
         System.arraycopy(var38, 0, var4, 1, var38.length);
         return new SubjectPublicKeyInfo(var58, var4);
      } else if (var0 instanceof MLKEMPublicKeyParameters) {
         MLKEMPublicKeyParameters var14 = (MLKEMPublicKeyParameters)var0;
         AlgorithmIdentifier var37 = new AlgorithmIdentifier(Utils.mlkemOidLookup(var14.getParameters()));
         return new SubjectPublicKeyInfo(var37, var14.getEncoded());
      } else if (var0 instanceof NTRULPRimePublicKeyParameters) {
         NTRULPRimePublicKeyParameters var13 = (NTRULPRimePublicKeyParameters)var0;
         byte[] var36 = var13.getEncoded();
         AlgorithmIdentifier var57 = new AlgorithmIdentifier(Utils.ntrulprimeOidLookup(var13.getParameters()));
         return new SubjectPublicKeyInfo(var57, new DEROctetString(var36));
      } else if (var0 instanceof SNTRUPrimePublicKeyParameters) {
         SNTRUPrimePublicKeyParameters var12 = (SNTRUPrimePublicKeyParameters)var0;
         byte[] var35 = var12.getEncoded();
         AlgorithmIdentifier var56 = new AlgorithmIdentifier(Utils.sntruprimeOidLookup(var12.getParameters()));
         return new SubjectPublicKeyInfo(var56, new DEROctetString(var35));
      } else if (var0 instanceof DilithiumPublicKeyParameters) {
         DilithiumPublicKeyParameters var11 = (DilithiumPublicKeyParameters)var0;
         AlgorithmIdentifier var34 = new AlgorithmIdentifier(Utils.dilithiumOidLookup(var11.getParameters()));
         return new SubjectPublicKeyInfo(var34, var11.getEncoded());
      } else if (var0 instanceof MLDSAPublicKeyParameters) {
         MLDSAPublicKeyParameters var10 = (MLDSAPublicKeyParameters)var0;
         AlgorithmIdentifier var33 = new AlgorithmIdentifier(Utils.mldsaOidLookup(var10.getParameters()));
         return new SubjectPublicKeyInfo(var33, var10.getEncoded());
      } else if (var0 instanceof BIKEPublicKeyParameters) {
         BIKEPublicKeyParameters var9 = (BIKEPublicKeyParameters)var0;
         byte[] var32 = var9.getEncoded();
         AlgorithmIdentifier var55 = new AlgorithmIdentifier(Utils.bikeOidLookup(var9.getParameters()));
         return new SubjectPublicKeyInfo(var55, var32);
      } else if (var0 instanceof HQCPublicKeyParameters) {
         HQCPublicKeyParameters var8 = (HQCPublicKeyParameters)var0;
         byte[] var31 = var8.getEncoded();
         AlgorithmIdentifier var54 = new AlgorithmIdentifier(Utils.hqcOidLookup(var8.getParameters()));
         return new SubjectPublicKeyInfo(var54, var31);
      } else if (var0 instanceof RainbowPublicKeyParameters) {
         RainbowPublicKeyParameters var7 = (RainbowPublicKeyParameters)var0;
         byte[] var30 = var7.getEncoded();
         AlgorithmIdentifier var53 = new AlgorithmIdentifier(Utils.rainbowOidLookup(var7.getParameters()));
         return new SubjectPublicKeyInfo(var53, new DEROctetString(var30));
      } else if (var0 instanceof MayoPublicKeyParameters) {
         MayoPublicKeyParameters var6 = (MayoPublicKeyParameters)var0;
         byte[] var29 = var6.getEncoded();
         AlgorithmIdentifier var52 = new AlgorithmIdentifier(Utils.mayoOidLookup(var6.getParameters()));
         return new SubjectPublicKeyInfo(var52, new DEROctetString(var29));
      } else if (var0 instanceof SnovaPublicKeyParameters) {
         SnovaPublicKeyParameters var1 = (SnovaPublicKeyParameters)var0;
         byte[] var2 = var1.getEncoded();
         AlgorithmIdentifier var3 = new AlgorithmIdentifier(Utils.snovaOidLookup(var1.getParameters()));
         return new SubjectPublicKeyInfo(var3, new DEROctetString(var2));
      } else {
         throw new IOException("key parameters not recognized");
      }
   }
}
