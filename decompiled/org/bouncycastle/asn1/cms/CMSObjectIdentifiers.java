package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

public interface CMSObjectIdentifiers {
   ASN1ObjectIdentifier data = PKCSObjectIdentifiers.data;
   ASN1ObjectIdentifier signedData = PKCSObjectIdentifiers.signedData;
   ASN1ObjectIdentifier envelopedData = PKCSObjectIdentifiers.envelopedData;
   ASN1ObjectIdentifier signedAndEnvelopedData = PKCSObjectIdentifiers.signedAndEnvelopedData;
   ASN1ObjectIdentifier digestedData = PKCSObjectIdentifiers.digestedData;
   ASN1ObjectIdentifier encryptedData = PKCSObjectIdentifiers.encryptedData;
   ASN1ObjectIdentifier authenticatedData = PKCSObjectIdentifiers.id_ct_authData;
   ASN1ObjectIdentifier compressedData = PKCSObjectIdentifiers.id_ct_compressedData;
   ASN1ObjectIdentifier authEnvelopedData = PKCSObjectIdentifiers.id_ct_authEnvelopedData;
   ASN1ObjectIdentifier timestampedData = PKCSObjectIdentifiers.id_ct_timestampedData;
   ASN1ObjectIdentifier zlibCompress = PKCSObjectIdentifiers.id_alg_zlibCompress;
   ASN1ObjectIdentifier id_ri = X509ObjectIdentifiers.id_pkix.branch("16");
   ASN1ObjectIdentifier id_ri_ocsp_response = id_ri.branch("2");
   ASN1ObjectIdentifier id_ri_scvp = id_ri.branch("4");
   ASN1ObjectIdentifier id_alg = X509ObjectIdentifiers.pkix_algorithms;
   ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE128 = X509ObjectIdentifiers.id_rsassa_pss_shake128;
   ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE256 = X509ObjectIdentifiers.id_rsassa_pss_shake256;
   ASN1ObjectIdentifier id_ecdsa_with_shake128 = X509ObjectIdentifiers.id_ecdsa_with_shake128;
   ASN1ObjectIdentifier id_ecdsa_with_shake256 = X509ObjectIdentifiers.id_ecdsa_with_shake256;
   ASN1ObjectIdentifier id_ori = PKCSObjectIdentifiers.id_smime.branch("13");
   ASN1ObjectIdentifier id_ori_kem = id_ori.branch("3");
   ASN1ObjectIdentifier id_alg_cek_hkdf_sha256 = PKCSObjectIdentifiers.smime_alg.branch("31");
}
