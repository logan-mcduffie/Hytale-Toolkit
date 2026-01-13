package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;

public interface PQCObjectIdentifiers {
   ASN1ObjectIdentifier rainbow = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.5.3.2");
   ASN1ObjectIdentifier rainbowWithSha1 = rainbow.branch("1");
   ASN1ObjectIdentifier rainbowWithSha224 = rainbow.branch("2");
   ASN1ObjectIdentifier rainbowWithSha256 = rainbow.branch("3");
   ASN1ObjectIdentifier rainbowWithSha384 = rainbow.branch("4");
   ASN1ObjectIdentifier rainbowWithSha512 = rainbow.branch("5");
   ASN1ObjectIdentifier gmss = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.3");
   ASN1ObjectIdentifier gmssWithSha1 = gmss.branch("1");
   ASN1ObjectIdentifier gmssWithSha224 = gmss.branch("2");
   ASN1ObjectIdentifier gmssWithSha256 = gmss.branch("3");
   ASN1ObjectIdentifier gmssWithSha384 = gmss.branch("4");
   ASN1ObjectIdentifier gmssWithSha512 = gmss.branch("5");
   ASN1ObjectIdentifier mcEliece = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.4.1");
   ASN1ObjectIdentifier mcElieceCca2 = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.4.2");
   ASN1ObjectIdentifier mcElieceFujisaki = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.4.2.1");
   ASN1ObjectIdentifier mcEliecePointcheval = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.4.2.2");
   ASN1ObjectIdentifier mcElieceKobara_Imai = new ASN1ObjectIdentifier("1.3.6.1.4.1.8301.3.1.3.4.2.3");
   ASN1ObjectIdentifier sphincs256 = BCObjectIdentifiers.sphincs256;
   ASN1ObjectIdentifier sphincs256_with_BLAKE512 = BCObjectIdentifiers.sphincs256_with_BLAKE512;
   ASN1ObjectIdentifier sphincs256_with_SHA512 = BCObjectIdentifiers.sphincs256_with_SHA512;
   ASN1ObjectIdentifier sphincs256_with_SHA3_512 = BCObjectIdentifiers.sphincs256_with_SHA3_512;
   ASN1ObjectIdentifier newHope = BCObjectIdentifiers.newHope;
   ASN1ObjectIdentifier xmss = BCObjectIdentifiers.xmss;
   ASN1ObjectIdentifier xmss_SHA256ph = BCObjectIdentifiers.xmss_SHA256ph;
   ASN1ObjectIdentifier xmss_SHA512ph = BCObjectIdentifiers.xmss_SHA512ph;
   ASN1ObjectIdentifier xmss_SHAKE128ph = BCObjectIdentifiers.xmss_SHAKE128ph;
   ASN1ObjectIdentifier xmss_SHAKE256ph = BCObjectIdentifiers.xmss_SHAKE256ph;
   ASN1ObjectIdentifier xmss_SHA256 = BCObjectIdentifiers.xmss_SHA256;
   ASN1ObjectIdentifier xmss_SHA512 = BCObjectIdentifiers.xmss_SHA512;
   ASN1ObjectIdentifier xmss_SHAKE128 = BCObjectIdentifiers.xmss_SHAKE128;
   ASN1ObjectIdentifier xmss_SHAKE256 = BCObjectIdentifiers.xmss_SHAKE256;
   ASN1ObjectIdentifier xmss_mt = BCObjectIdentifiers.xmss_mt;
   ASN1ObjectIdentifier xmss_mt_SHA256ph = BCObjectIdentifiers.xmss_mt_SHA256ph;
   ASN1ObjectIdentifier xmss_mt_SHA512ph = BCObjectIdentifiers.xmss_mt_SHA512ph;
   ASN1ObjectIdentifier xmss_mt_SHAKE128ph = BCObjectIdentifiers.xmss_mt_SHAKE128ph;
   ASN1ObjectIdentifier xmss_mt_SHAKE256ph = BCObjectIdentifiers.xmss_mt_SHAKE256ph;
   ASN1ObjectIdentifier xmss_mt_SHA256 = BCObjectIdentifiers.xmss_mt_SHA256;
   ASN1ObjectIdentifier xmss_mt_SHA512 = BCObjectIdentifiers.xmss_mt_SHA512;
   ASN1ObjectIdentifier xmss_mt_SHAKE128 = BCObjectIdentifiers.xmss_mt_SHAKE128;
   ASN1ObjectIdentifier xmss_mt_SHAKE256 = BCObjectIdentifiers.xmss_mt_SHAKE256;
   @Deprecated
   ASN1ObjectIdentifier xmss_with_SHA256 = xmss_SHA256ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_with_SHA512 = xmss_SHA512ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_with_SHAKE128 = xmss_SHAKE128ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_with_SHAKE256 = xmss_SHAKE256ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_mt_with_SHA256 = xmss_mt_SHA256ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_mt_with_SHA512 = xmss_mt_SHA512ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_mt_with_SHAKE128 = xmss_mt_SHAKE128ph;
   @Deprecated
   ASN1ObjectIdentifier xmss_mt_with_SHAKE256 = xmss_mt_SHAKE256ph;
   ASN1ObjectIdentifier qTESLA = BCObjectIdentifiers.qTESLA;
   ASN1ObjectIdentifier qTESLA_p_I = BCObjectIdentifiers.qTESLA_p_I;
   ASN1ObjectIdentifier qTESLA_p_III = BCObjectIdentifiers.qTESLA_p_III;
   ASN1ObjectIdentifier id_Dilithium3_RSA_PKCS15_SHA256 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.1");
   ASN1ObjectIdentifier id_Dilithium3_ECDSA_P256_SHA256 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.2");
   ASN1ObjectIdentifier id_Dilithium3_ECDSA_brainpoolP256r1_SHA256 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.3");
   ASN1ObjectIdentifier id_Dilithium3_Ed25519 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.4");
   ASN1ObjectIdentifier id_Dilithium5_ECDSA_P384_SHA384 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.5");
   ASN1ObjectIdentifier id_Dilithium5_ECDSA_brainpoolP384r1_SHA384 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.6");
   ASN1ObjectIdentifier id_Dilithium5_Ed448 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.7");
   ASN1ObjectIdentifier id_Falcon512_ECDSA_P256_SHA256 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.8");
   ASN1ObjectIdentifier id_Falcon512_ECDSA_brainpoolP256r1_SHA256 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.9");
   ASN1ObjectIdentifier id_Falcon512_Ed25519 = new ASN1ObjectIdentifier("2.16.840.1.114027.80.5.1.10");
}
