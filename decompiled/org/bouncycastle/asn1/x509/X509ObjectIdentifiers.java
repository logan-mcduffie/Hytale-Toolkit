package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;

public interface X509ObjectIdentifiers {
   ASN1ObjectIdentifier attributeType = new ASN1ObjectIdentifier("2.5.4").intern();
   ASN1ObjectIdentifier commonName = attributeType.branch("3").intern();
   ASN1ObjectIdentifier countryName = attributeType.branch("6").intern();
   ASN1ObjectIdentifier localityName = attributeType.branch("7").intern();
   ASN1ObjectIdentifier stateOrProvinceName = attributeType.branch("8").intern();
   ASN1ObjectIdentifier organization = attributeType.branch("10").intern();
   ASN1ObjectIdentifier organizationalUnitName = attributeType.branch("11").intern();
   ASN1ObjectIdentifier id_at_telephoneNumber = attributeType.branch("20").intern();
   ASN1ObjectIdentifier id_at_name = attributeType.branch("41").intern();
   ASN1ObjectIdentifier id_at_organizationIdentifier = attributeType.branch("97").intern();
   ASN1ObjectIdentifier id_SHA1 = new ASN1ObjectIdentifier("1.3.14.3.2.26").intern();
   ASN1ObjectIdentifier ripemd160 = new ASN1ObjectIdentifier("1.3.36.3.2.1").intern();
   ASN1ObjectIdentifier ripemd160WithRSAEncryption = new ASN1ObjectIdentifier("1.3.36.3.3.1.2").intern();
   ASN1ObjectIdentifier id_ea_rsa = new ASN1ObjectIdentifier("2.5.8.1.1").intern();
   ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
   ASN1ObjectIdentifier id_pe = id_pkix.branch("1");
   ASN1ObjectIdentifier pkix_algorithms = id_pkix.branch("6");
   ASN1ObjectIdentifier id_rsassa_pss_shake128 = pkix_algorithms.branch("30");
   ASN1ObjectIdentifier id_rsassa_pss_shake256 = pkix_algorithms.branch("31");
   ASN1ObjectIdentifier id_ecdsa_with_shake128 = pkix_algorithms.branch("32");
   ASN1ObjectIdentifier id_ecdsa_with_shake256 = pkix_algorithms.branch("33");
   ASN1ObjectIdentifier id_alg_noSignature = pkix_algorithms.branch("2");
   ASN1ObjectIdentifier id_alg_unsigned = pkix_algorithms.branch("36");
   ASN1ObjectIdentifier id_pda = id_pkix.branch("9");
   ASN1ObjectIdentifier id_ad = id_pkix.branch("48");
   ASN1ObjectIdentifier id_ad_caIssuers = id_ad.branch("2").intern();
   ASN1ObjectIdentifier id_ad_ocsp = id_ad.branch("1").intern();
   ASN1ObjectIdentifier ocspAccessMethod = id_ad_ocsp;
   ASN1ObjectIdentifier crlAccessMethod = id_ad_caIssuers;
   ASN1ObjectIdentifier id_ce = new ASN1ObjectIdentifier("2.5.29");
   /** @deprecated */
   ASN1ObjectIdentifier id_PasswordBasedMac = MiscObjectIdentifiers.entrust.branch("66.13");
}
