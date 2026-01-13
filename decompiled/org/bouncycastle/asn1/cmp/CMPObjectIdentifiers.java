package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

public interface CMPObjectIdentifiers {
   ASN1ObjectIdentifier passwordBasedMac = CRMFObjectIdentifiers.passwordBasedMac;
   ASN1ObjectIdentifier kemBasedMac = MiscObjectIdentifiers.entrust.branch("66.16");
   ASN1ObjectIdentifier dhBasedMac = MiscObjectIdentifiers.entrust.branch("66.30");
   ASN1ObjectIdentifier id_it = X509ObjectIdentifiers.id_pkix.branch("4");
   ASN1ObjectIdentifier it_caProtEncCert = id_it.branch("1");
   ASN1ObjectIdentifier it_signKeyPairTypes = id_it.branch("2");
   ASN1ObjectIdentifier it_encKeyPairTypes = id_it.branch("3");
   ASN1ObjectIdentifier it_preferredSymAlg = id_it.branch("4");
   ASN1ObjectIdentifier it_caKeyUpdateInfo = id_it.branch("5");
   ASN1ObjectIdentifier it_currentCRL = id_it.branch("6");
   ASN1ObjectIdentifier it_unsupportedOIDs = id_it.branch("7");
   ASN1ObjectIdentifier it_keyPairParamReq = id_it.branch("10");
   ASN1ObjectIdentifier it_keyPairParamRep = id_it.branch("11");
   ASN1ObjectIdentifier it_revPassphrase = id_it.branch("12");
   ASN1ObjectIdentifier it_implicitConfirm = id_it.branch("13");
   ASN1ObjectIdentifier it_confirmWaitTime = id_it.branch("14");
   ASN1ObjectIdentifier it_origPKIMessage = id_it.branch("15");
   ASN1ObjectIdentifier it_suppLangTags = id_it.branch("16");
   ASN1ObjectIdentifier id_it_caCerts = id_it.branch("17");
   ASN1ObjectIdentifier id_it_rootCaKeyUpdate = id_it.branch("18");
   ASN1ObjectIdentifier id_it_certReqTemplate = id_it.branch("19");
   ASN1ObjectIdentifier id_it_rootCaCert = id_it.branch("20");
   ASN1ObjectIdentifier id_it_certProfile = id_it.branch("21");
   ASN1ObjectIdentifier id_it_crlStatusList = id_it.branch("22");
   ASN1ObjectIdentifier id_it_crls = id_it.branch("23");
   ASN1ObjectIdentifier id_pkip = CRMFObjectIdentifiers.id_pkip;
   ASN1ObjectIdentifier id_regCtrl = CRMFObjectIdentifiers.id_regCtrl;
   ASN1ObjectIdentifier id_regInfo = CRMFObjectIdentifiers.id_regInfo;
   ASN1ObjectIdentifier regCtrl_regToken = CRMFObjectIdentifiers.id_regCtrl_regToken;
   ASN1ObjectIdentifier regCtrl_authenticator = CRMFObjectIdentifiers.id_regCtrl_authenticator;
   ASN1ObjectIdentifier regCtrl_pkiPublicationInfo = CRMFObjectIdentifiers.id_regCtrl_pkiPublicationInfo;
   ASN1ObjectIdentifier regCtrl_pkiArchiveOptions = CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions;
   ASN1ObjectIdentifier regCtrl_oldCertID = CRMFObjectIdentifiers.id_regCtrl_oldCertID;
   ASN1ObjectIdentifier regCtrl_protocolEncrKey = CRMFObjectIdentifiers.id_regCtrl_protocolEncrKey;
   ASN1ObjectIdentifier regCtrl_altCertTemplate = id_regCtrl.branch("7");
   ASN1ObjectIdentifier id_regCtrl_algId = id_regCtrl.branch("11");
   ASN1ObjectIdentifier id_regCtrl_rsaKeyLen = id_regCtrl.branch("12");
   ASN1ObjectIdentifier regInfo_utf8Pairs = CRMFObjectIdentifiers.id_regInfo_utf8Pairs;
   ASN1ObjectIdentifier regInfo_certReq = CRMFObjectIdentifiers.id_regInfo_certReq;
   ASN1ObjectIdentifier ct_encKeyWithID = CRMFObjectIdentifiers.id_ct_encKeyWithID;
}
