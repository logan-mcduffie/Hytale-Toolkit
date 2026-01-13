package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.pqc.asn1.CMCEPublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.pqc.crypto.cmce.CMCEPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

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
         if (var3 != null) {
            return var3.getPublicKeyParameters(var0, var1);
         } else {
            throw new IOException("algorithm identifier in public key not recognised: " + var2.getAlgorithm());
         }
      }
   }

   static {
      converters.put(PQCObjectIdentifiers.sphincs256, new PublicKeyFactory.SPHINCSConverter());
      converters.put(PQCObjectIdentifiers.newHope, new PublicKeyFactory.NHConverter());
      converters.put(PQCObjectIdentifiers.xmss, new PublicKeyFactory.XMSSConverter());
      converters.put(PQCObjectIdentifiers.xmss_mt, new PublicKeyFactory.XMSSMTConverter());
      converters.put(IsaraObjectIdentifiers.id_alg_xmss, new PublicKeyFactory.XMSSConverter());
      converters.put(IsaraObjectIdentifiers.id_alg_xmssmt, new PublicKeyFactory.XMSSMTConverter());
      converters.put(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new PublicKeyFactory.LMSConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_128s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_128f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_192s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_192f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_256s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_256f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128s, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_128f, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_128s, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_128f, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192s, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_192f, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_192s, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_192f, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256s, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_sha2_256f, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_256s, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.sphincsPlus_shake_256f, new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(new ASN1ObjectIdentifier("1.3.9999.6.4.10"), new PublicKeyFactory.SPHINCSPlusConverter());
      converters.put(BCObjectIdentifiers.mceliece348864_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece348864f_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece460896_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece460896f_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece6688128_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece6688128f_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece6960119_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece6960119f_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece8192128_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.mceliece8192128f_r3, new PublicKeyFactory.CMCEConverter());
      converters.put(BCObjectIdentifiers.frodokem640aes, new PublicKeyFactory.FrodoConverter());
      converters.put(BCObjectIdentifiers.frodokem640shake, new PublicKeyFactory.FrodoConverter());
      converters.put(BCObjectIdentifiers.frodokem976aes, new PublicKeyFactory.FrodoConverter());
      converters.put(BCObjectIdentifiers.frodokem976shake, new PublicKeyFactory.FrodoConverter());
      converters.put(BCObjectIdentifiers.frodokem1344aes, new PublicKeyFactory.FrodoConverter());
      converters.put(BCObjectIdentifiers.frodokem1344shake, new PublicKeyFactory.FrodoConverter());
      converters.put(BCObjectIdentifiers.lightsaberkem128r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.saberkem128r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.firesaberkem128r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.lightsaberkem192r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.saberkem192r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.firesaberkem192r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.lightsaberkem256r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.saberkem256r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.firesaberkem256r3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.ulightsaberkemr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.usaberkemr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.ufiresaberkemr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.lightsaberkem90sr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.saberkem90sr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.firesaberkem90sr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.ulightsaberkem90sr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.usaberkem90sr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.ufiresaberkem90sr3, new PublicKeyFactory.SABERConverter());
      converters.put(BCObjectIdentifiers.picnicl1fs, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl1ur, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl3fs, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl3ur, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl5fs, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl5ur, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnic3l1, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnic3l3, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnic3l5, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl1full, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl3full, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.picnicl5full, new PublicKeyFactory.PicnicConverter());
      converters.put(BCObjectIdentifiers.ntruhps2048509, new PublicKeyFactory.NtruConverter());
      converters.put(BCObjectIdentifiers.ntruhps2048677, new PublicKeyFactory.NtruConverter());
      converters.put(BCObjectIdentifiers.ntruhps4096821, new PublicKeyFactory.NtruConverter());
      converters.put(BCObjectIdentifiers.ntruhps40961229, new PublicKeyFactory.NtruConverter());
      converters.put(BCObjectIdentifiers.ntruhrss701, new PublicKeyFactory.NtruConverter());
      converters.put(BCObjectIdentifiers.ntruhrss1373, new PublicKeyFactory.NtruConverter());
      converters.put(BCObjectIdentifiers.falcon_512, new PublicKeyFactory.FalconConverter());
      converters.put(BCObjectIdentifiers.falcon_1024, new PublicKeyFactory.FalconConverter());
      converters.put(BCObjectIdentifiers.old_falcon_512, new PublicKeyFactory.FalconConverter());
      converters.put(BCObjectIdentifiers.old_falcon_1024, new PublicKeyFactory.FalconConverter());
      converters.put(NISTObjectIdentifiers.id_alg_ml_kem_512, new PublicKeyFactory.MLKEMConverter());
      converters.put(NISTObjectIdentifiers.id_alg_ml_kem_768, new PublicKeyFactory.MLKEMConverter());
      converters.put(NISTObjectIdentifiers.id_alg_ml_kem_1024, new PublicKeyFactory.MLKEMConverter());
      converters.put(BCObjectIdentifiers.kyber512_aes, new PublicKeyFactory.MLKEMConverter());
      converters.put(BCObjectIdentifiers.kyber768_aes, new PublicKeyFactory.MLKEMConverter());
      converters.put(BCObjectIdentifiers.kyber1024_aes, new PublicKeyFactory.MLKEMConverter());
      converters.put(BCObjectIdentifiers.ntrulpr653, new PublicKeyFactory.NTRULPrimeConverter());
      converters.put(BCObjectIdentifiers.ntrulpr761, new PublicKeyFactory.NTRULPrimeConverter());
      converters.put(BCObjectIdentifiers.ntrulpr857, new PublicKeyFactory.NTRULPrimeConverter());
      converters.put(BCObjectIdentifiers.ntrulpr953, new PublicKeyFactory.NTRULPrimeConverter());
      converters.put(BCObjectIdentifiers.ntrulpr1013, new PublicKeyFactory.NTRULPrimeConverter());
      converters.put(BCObjectIdentifiers.ntrulpr1277, new PublicKeyFactory.NTRULPrimeConverter());
      converters.put(BCObjectIdentifiers.sntrup653, new PublicKeyFactory.SNTRUPrimeConverter());
      converters.put(BCObjectIdentifiers.sntrup761, new PublicKeyFactory.SNTRUPrimeConverter());
      converters.put(BCObjectIdentifiers.sntrup857, new PublicKeyFactory.SNTRUPrimeConverter());
      converters.put(BCObjectIdentifiers.sntrup953, new PublicKeyFactory.SNTRUPrimeConverter());
      converters.put(BCObjectIdentifiers.sntrup1013, new PublicKeyFactory.SNTRUPrimeConverter());
      converters.put(BCObjectIdentifiers.sntrup1277, new PublicKeyFactory.SNTRUPrimeConverter());
      converters.put(NISTObjectIdentifiers.id_ml_dsa_44, new PublicKeyFactory.MLDSAConverter());
      converters.put(NISTObjectIdentifiers.id_ml_dsa_65, new PublicKeyFactory.MLDSAConverter());
      converters.put(NISTObjectIdentifiers.id_ml_dsa_87, new PublicKeyFactory.MLDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, new PublicKeyFactory.MLDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, new PublicKeyFactory.MLDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, new PublicKeyFactory.MLDSAConverter());
      converters.put(BCObjectIdentifiers.dilithium2, new PublicKeyFactory.DilithiumConverter());
      converters.put(BCObjectIdentifiers.dilithium3, new PublicKeyFactory.DilithiumConverter());
      converters.put(BCObjectIdentifiers.dilithium5, new PublicKeyFactory.DilithiumConverter());
      converters.put(BCObjectIdentifiers.dilithium2_aes, new PublicKeyFactory.DilithiumConverter());
      converters.put(BCObjectIdentifiers.dilithium3_aes, new PublicKeyFactory.DilithiumConverter());
      converters.put(BCObjectIdentifiers.dilithium5_aes, new PublicKeyFactory.DilithiumConverter());
      converters.put(BCObjectIdentifiers.bike128, new PublicKeyFactory.BIKEConverter());
      converters.put(BCObjectIdentifiers.bike192, new PublicKeyFactory.BIKEConverter());
      converters.put(BCObjectIdentifiers.bike256, new PublicKeyFactory.BIKEConverter());
      converters.put(BCObjectIdentifiers.hqc128, new PublicKeyFactory.HQCConverter());
      converters.put(BCObjectIdentifiers.hqc192, new PublicKeyFactory.HQCConverter());
      converters.put(BCObjectIdentifiers.hqc256, new PublicKeyFactory.HQCConverter());
      converters.put(BCObjectIdentifiers.rainbow_III_classic, new PublicKeyFactory.RainbowConverter());
      converters.put(BCObjectIdentifiers.rainbow_III_circumzenithal, new PublicKeyFactory.RainbowConverter());
      converters.put(BCObjectIdentifiers.rainbow_III_compressed, new PublicKeyFactory.RainbowConverter());
      converters.put(BCObjectIdentifiers.rainbow_V_classic, new PublicKeyFactory.RainbowConverter());
      converters.put(BCObjectIdentifiers.rainbow_V_circumzenithal, new PublicKeyFactory.RainbowConverter());
      converters.put(BCObjectIdentifiers.rainbow_V_compressed, new PublicKeyFactory.RainbowConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_128s, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_128f, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_192s, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_192f, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_256s, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_sha2_256f, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_128s, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_128f, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_192s, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_192f, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_256s, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_slh_dsa_shake_256f, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256, new PublicKeyFactory.SLHDSAConverter());
      converters.put(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256, new PublicKeyFactory.SLHDSAConverter());
      converters.put(BCObjectIdentifiers.mayo1, new PublicKeyFactory.MayoConverter());
      converters.put(BCObjectIdentifiers.mayo2, new PublicKeyFactory.MayoConverter());
      converters.put(BCObjectIdentifiers.mayo3, new PublicKeyFactory.MayoConverter());
      converters.put(BCObjectIdentifiers.mayo5, new PublicKeyFactory.MayoConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_4_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_4_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_4_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_4_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_5_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_5_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_5_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_24_5_5_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_25_8_3_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_25_8_3_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_25_8_3_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_25_8_3_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_29_6_5_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_29_6_5_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_29_6_5_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_29_6_5_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_8_4_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_8_4_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_8_4_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_8_4_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_17_2_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_17_2_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_17_2_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_37_17_2_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_49_11_3_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_49_11_3_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_49_11_3_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_49_11_3_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_56_25_2_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_56_25_2_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_56_25_2_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_56_25_2_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_60_10_4_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_60_10_4_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_60_10_4_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_60_10_4_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_66_15_3_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_66_15_3_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_66_15_3_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_66_15_3_shake_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_75_33_2_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_75_33_2_ssk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_75_33_2_shake_esk, new PublicKeyFactory.SnovaConverter());
      converters.put(BCObjectIdentifiers.snova_75_33_2_shake_ssk, new PublicKeyFactory.SnovaConverter());
   }

   private static class BIKEConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private BIKEConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         try {
            byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
            BIKEParameters var7 = Utils.bikeParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new BIKEPublicKeyParameters(var7, var3);
         } catch (Exception var6) {
            byte[] var4 = var1.getPublicKeyData().getOctets();
            BIKEParameters var5 = Utils.bikeParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new BIKEPublicKeyParameters(var5, var4);
         }
      }
   }

   private static class CMCEConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private CMCEConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         try {
            byte[] var3 = CMCEPublicKey.getInstance(var1.parsePublicKey()).getT();
            CMCEParameters var7 = Utils.mcElieceParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new CMCEPublicKeyParameters(var7, var3);
         } catch (Exception var6) {
            byte[] var4 = var1.getPublicKeyData().getOctets();
            CMCEParameters var5 = Utils.mcElieceParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new CMCEPublicKeyParameters(var5, var4);
         }
      }
   }

   static class DilithiumConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         DilithiumParameters var3 = Utils.dilithiumParamsLookup(var1.getAlgorithm().getAlgorithm());
         return getPublicKeyParams(var3, var1.getPublicKeyData());
      }

      static DilithiumPublicKeyParameters getPublicKeyParams(DilithiumParameters var0, ASN1BitString var1) {
         try {
            ASN1Primitive var2 = ASN1Primitive.fromByteArray(var1.getOctets());
            if (var2 instanceof ASN1Sequence) {
               ASN1Sequence var5 = ASN1Sequence.getInstance(var2);
               return new DilithiumPublicKeyParameters(
                  var0, ASN1OctetString.getInstance(var5.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(var5.getObjectAt(1)).getOctets()
               );
            } else {
               byte[] var3 = ASN1OctetString.getInstance(var2).getOctets();
               return new DilithiumPublicKeyParameters(var0, var3);
            }
         } catch (Exception var4) {
            return new DilithiumPublicKeyParameters(var0, var1.getOctets());
         }
      }
   }

   private static class FalconConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private FalconConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = var1.getPublicKeyData().getOctets();
         FalconParameters var4 = Utils.falconParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new FalconPublicKeyParameters(var4, Arrays.copyOfRange(var3, 1, var3.length));
      }
   }

   private static class FrodoConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private FrodoConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         FrodoParameters var4 = Utils.frodoParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new FrodoPublicKeyParameters(var4, var3);
      }
   }

   private static class HQCConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private HQCConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         try {
            byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
            HQCParameters var7 = Utils.hqcParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new HQCPublicKeyParameters(var7, var3);
         } catch (Exception var6) {
            byte[] var4 = var1.getPublicKeyData().getOctets();
            HQCParameters var5 = Utils.hqcParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new HQCPublicKeyParameters(var5, var4);
         }
      }
   }

   private static class LMSConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private LMSConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = var1.getPublicKeyData().getOctets();
         ASN1OctetString var4 = (ASN1OctetString)Utils.parseData(var3);
         return var4 != null ? this.getLmsKeyParameters(var4.getOctets()) : this.getLmsKeyParameters(var3);
      }

      private LMSKeyParameters getLmsKeyParameters(byte[] var1) throws IOException {
         return HSSPublicKeyParameters.getInstance(var1);
      }
   }

   static class MLDSAConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         MLDSAParameters var3 = Utils.mldsaParamsLookup(var1.getAlgorithm().getAlgorithm());
         return getPublicKeyParams(var3, var1.getPublicKeyData());
      }

      static MLDSAPublicKeyParameters getPublicKeyParams(MLDSAParameters var0, ASN1BitString var1) {
         try {
            ASN1Primitive var2 = ASN1Primitive.fromByteArray(var1.getOctets());
            if (var2 instanceof ASN1Sequence) {
               ASN1Sequence var5 = ASN1Sequence.getInstance(var2);
               return new MLDSAPublicKeyParameters(
                  var0, ASN1OctetString.getInstance(var5.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(var5.getObjectAt(1)).getOctets()
               );
            } else {
               byte[] var3 = ASN1OctetString.getInstance(var2).getOctets();
               return new MLDSAPublicKeyParameters(var0, var3);
            }
         } catch (Exception var4) {
            return new MLDSAPublicKeyParameters(var0, var1.getOctets());
         }
      }
   }

   static class MLKEMConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         MLKEMParameters var3 = Utils.mlkemParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new MLKEMPublicKeyParameters(var3, var1.getPublicKeyData().getOctets());
      }

      static MLKEMPublicKeyParameters getPublicKeyParams(MLKEMParameters var0, ASN1BitString var1) {
         try {
            ASN1Primitive var2 = ASN1Primitive.fromByteArray(var1.getOctets());
            if (var2 instanceof ASN1Sequence) {
               ASN1Sequence var5 = ASN1Sequence.getInstance(var2);
               return new MLKEMPublicKeyParameters(
                  var0, ASN1OctetString.getInstance(var5.getObjectAt(0)).getOctets(), ASN1OctetString.getInstance(var5.getObjectAt(1)).getOctets()
               );
            } else {
               byte[] var3 = ASN1OctetString.getInstance(var2).getOctets();
               return new MLKEMPublicKeyParameters(var0, var3);
            }
         } catch (Exception var4) {
            return new MLKEMPublicKeyParameters(var0, var1.getOctets());
         }
      }
   }

   private static class MayoConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private MayoConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         MayoParameters var4 = Utils.mayoParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new MayoPublicKeyParameters(var4, var3);
      }
   }

   private static class NHConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private NHConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         return new NHPublicKeyParameters(var1.getPublicKeyData().getBytes());
      }
   }

   private static class NTRULPrimeConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private NTRULPrimeConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         NTRULPRimeParameters var4 = Utils.ntrulprimeParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new NTRULPRimePublicKeyParameters(var4, var3);
      }
   }

   private static class NtruConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private NtruConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = var1.getPublicKeyData().getOctets();
         ASN1OctetString var4 = Utils.parseOctetData(var3);
         return var4 != null ? this.getNtruPublicKeyParameters(var1, var4.getOctets()) : this.getNtruPublicKeyParameters(var1, var3);
      }

      private NTRUPublicKeyParameters getNtruPublicKeyParameters(SubjectPublicKeyInfo var1, byte[] var2) {
         NTRUParameters var3 = Utils.ntruParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new NTRUPublicKeyParameters(var3, var2);
      }
   }

   private static class PicnicConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private PicnicConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         PicnicParameters var4 = Utils.picnicParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new PicnicPublicKeyParameters(var4, var3);
      }
   }

   private static class RainbowConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private RainbowConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         RainbowParameters var4 = Utils.rainbowParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new RainbowPublicKeyParameters(var4, var3);
      }
   }

   private static class SABERConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private SABERConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(ASN1Sequence.getInstance(var1.parsePublicKey()).getObjectAt(0)).getOctets();
         SABERParameters var4 = Utils.saberParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new SABERPublicKeyParameters(var4, var3);
      }
   }

   private static class SLHDSAConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private SLHDSAConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         try {
            byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
            SLHDSAParameters var7 = Utils.slhdsaParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new SLHDSAPublicKeyParameters(var7, Arrays.copyOfRange(var3, 4, var3.length));
         } catch (Exception var6) {
            byte[] var4 = var1.getPublicKeyData().getOctets();
            SLHDSAParameters var5 = Utils.slhdsaParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new SLHDSAPublicKeyParameters(var5, var4);
         }
      }
   }

   private static class SNTRUPrimeConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private SNTRUPrimeConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         SNTRUPrimeParameters var4 = Utils.sntruprimeParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new SNTRUPrimePublicKeyParameters(var4, var3);
      }
   }

   private static class SPHINCSConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private SPHINCSConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         return new SPHINCSPublicKeyParameters(
            var1.getPublicKeyData().getBytes(), Utils.sphincs256LookupTreeAlgName(SPHINCS256KeyParams.getInstance(var1.getAlgorithm().getParameters()))
         );
      }
   }

   private static class SPHINCSPlusConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private SPHINCSPlusConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         try {
            byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
            SPHINCSPlusParameters var7 = Utils.sphincsPlusParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new SPHINCSPlusPublicKeyParameters(var7, Arrays.copyOfRange(var3, 4, var3.length));
         } catch (Exception var6) {
            byte[] var4 = var1.getPublicKeyData().getOctets();
            SPHINCSPlusParameters var5 = Utils.sphincsPlusParamsLookup(var1.getAlgorithm().getAlgorithm());
            return new SPHINCSPlusPublicKeyParameters(var5, var4);
         }
      }
   }

   private static class SnovaConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private SnovaConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         byte[] var3 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
         SnovaParameters var4 = Utils.snovaParamsLookup(var1.getAlgorithm().getAlgorithm());
         return new SnovaPublicKeyParameters(var4, var3);
      }
   }

   private abstract static class SubjectPublicKeyInfoConverter {
      private SubjectPublicKeyInfoConverter() {
      }

      abstract AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException;
   }

   private static class XMSSConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private XMSSConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         XMSSKeyParams var3 = XMSSKeyParams.getInstance(var1.getAlgorithm().getParameters());
         if (var3 != null) {
            ASN1ObjectIdentifier var6 = var3.getTreeDigest().getAlgorithm();
            XMSSPublicKey var5 = XMSSPublicKey.getInstance(var1.parsePublicKey());
            return new XMSSPublicKeyParameters.Builder(new XMSSParameters(var3.getHeight(), Utils.getDigest(var6)))
               .withPublicSeed(var5.getPublicSeed())
               .withRoot(var5.getRoot())
               .build();
         } else {
            byte[] var4 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
            return new XMSSPublicKeyParameters.Builder(XMSSParameters.lookupByOID(Pack.bigEndianToInt(var4, 0))).withPublicKey(var4).build();
         }
      }
   }

   private static class XMSSMTConverter extends PublicKeyFactory.SubjectPublicKeyInfoConverter {
      private XMSSMTConverter() {
      }

      @Override
      AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo var1, Object var2) throws IOException {
         XMSSMTKeyParams var3 = XMSSMTKeyParams.getInstance(var1.getAlgorithm().getParameters());
         if (var3 != null) {
            ASN1ObjectIdentifier var6 = var3.getTreeDigest().getAlgorithm();
            XMSSPublicKey var5 = XMSSPublicKey.getInstance(var1.parsePublicKey());
            return new XMSSMTPublicKeyParameters.Builder(new XMSSMTParameters(var3.getHeight(), var3.getLayers(), Utils.getDigest(var6)))
               .withPublicSeed(var5.getPublicSeed())
               .withRoot(var5.getRoot())
               .build();
         } else {
            byte[] var4 = ASN1OctetString.getInstance(var1.parsePublicKey()).getOctets();
            return new XMSSMTPublicKeyParameters.Builder(XMSSMTParameters.lookupByOID(Pack.bigEndianToInt(var4, 0))).withPublicKey(var4).build();
         }
      }
   }
}
