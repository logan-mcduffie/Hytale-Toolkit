package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

public interface CRMFObjectIdentifiers {
   ASN1ObjectIdentifier passwordBasedMac = MiscObjectIdentifiers.entrust.branch("66.13");
   ASN1ObjectIdentifier id_pkix = X509ObjectIdentifiers.id_pkix;
   ASN1ObjectIdentifier id_pkip = id_pkix.branch("5");
   ASN1ObjectIdentifier id_regCtrl = id_pkip.branch("1");
   ASN1ObjectIdentifier id_regCtrl_regToken = id_regCtrl.branch("1");
   ASN1ObjectIdentifier id_regCtrl_authenticator = id_regCtrl.branch("2");
   ASN1ObjectIdentifier id_regCtrl_pkiPublicationInfo = id_regCtrl.branch("3");
   ASN1ObjectIdentifier id_regCtrl_pkiArchiveOptions = id_regCtrl.branch("4");
   ASN1ObjectIdentifier id_regCtrl_oldCertID = id_regCtrl.branch("5");
   ASN1ObjectIdentifier id_regCtrl_protocolEncrKey = id_regCtrl.branch("6");
   ASN1ObjectIdentifier id_regInfo = id_pkip.branch("2");
   ASN1ObjectIdentifier id_regInfo_utf8Pairs = id_regInfo.branch("1");
   ASN1ObjectIdentifier id_regInfo_certReq = id_regInfo.branch("2");
   ASN1ObjectIdentifier id_ct_encKeyWithID = PKCSObjectIdentifiers.id_ct.branch("21");
   ASN1ObjectIdentifier id_alg = X509ObjectIdentifiers.pkix_algorithms;
   ASN1ObjectIdentifier id_dh_sig_hmac_sha1 = id_alg.branch("3");
   ASN1ObjectIdentifier id_alg_dh_pop = id_alg.branch("4");
}
