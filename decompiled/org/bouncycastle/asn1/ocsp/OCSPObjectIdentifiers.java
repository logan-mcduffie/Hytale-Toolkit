package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

public interface OCSPObjectIdentifiers {
   ASN1ObjectIdentifier id_pkix_ocsp = X509ObjectIdentifiers.id_ad_ocsp;
   ASN1ObjectIdentifier id_pkix_ocsp_basic = id_pkix_ocsp.branch("1");
   ASN1ObjectIdentifier id_pkix_ocsp_nonce = id_pkix_ocsp.branch("2");
   ASN1ObjectIdentifier id_pkix_ocsp_crl = id_pkix_ocsp.branch("3");
   ASN1ObjectIdentifier id_pkix_ocsp_response = id_pkix_ocsp.branch("4");
   ASN1ObjectIdentifier id_pkix_ocsp_nocheck = id_pkix_ocsp.branch("5");
   ASN1ObjectIdentifier id_pkix_ocsp_archive_cutoff = id_pkix_ocsp.branch("6");
   ASN1ObjectIdentifier id_pkix_ocsp_service_locator = id_pkix_ocsp.branch("7");
   ASN1ObjectIdentifier id_pkix_ocsp_pref_sig_algs = id_pkix_ocsp.branch("8");
   ASN1ObjectIdentifier id_pkix_ocsp_extended_revoke = id_pkix_ocsp.branch("9");
}
