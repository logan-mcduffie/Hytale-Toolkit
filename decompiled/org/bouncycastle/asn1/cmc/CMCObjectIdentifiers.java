package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface CMCObjectIdentifiers {
   ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
   ASN1ObjectIdentifier id_cmc = id_pkix.branch("7");
   ASN1ObjectIdentifier id_cct = id_pkix.branch("12");
   ASN1ObjectIdentifier id_kp = id_pkix.branch("3");
   ASN1ObjectIdentifier id_cmc_identityProof = id_cmc.branch("3");
   ASN1ObjectIdentifier id_cmc_dataReturn = id_cmc.branch("4");
   ASN1ObjectIdentifier id_cmc_regInfo = id_cmc.branch("18");
   ASN1ObjectIdentifier id_cmc_responseInfo = id_cmc.branch("19");
   ASN1ObjectIdentifier id_cmc_queryPending = id_cmc.branch("21");
   ASN1ObjectIdentifier id_cmc_popLinkRandom = id_cmc.branch("22");
   ASN1ObjectIdentifier id_cmc_popLinkWitness = id_cmc.branch("23");
   ASN1ObjectIdentifier id_cmc_identification = id_cmc.branch("2");
   ASN1ObjectIdentifier id_cmc_transactionId = id_cmc.branch("5");
   ASN1ObjectIdentifier id_cmc_senderNonce = id_cmc.branch("6");
   ASN1ObjectIdentifier id_cmc_recipientNonce = id_cmc.branch("7");
   ASN1ObjectIdentifier id_cct_PKIData = id_cct.branch("2");
   ASN1ObjectIdentifier id_cct_PKIResponse = id_cct.branch("3");
   ASN1ObjectIdentifier id_cmc_statusInfo = id_cmc.branch("1");
   ASN1ObjectIdentifier id_cmc_addExtensions = id_cmc.branch("8");
   ASN1ObjectIdentifier id_cmc_encryptedPOP = id_cmc.branch("9");
   ASN1ObjectIdentifier id_cmc_decryptedPOP = id_cmc.branch("10");
   ASN1ObjectIdentifier id_cmc_lraPOPWitness = id_cmc.branch("11");
   ASN1ObjectIdentifier id_cmc_getCert = id_cmc.branch("15");
   ASN1ObjectIdentifier id_cmc_getCRL = id_cmc.branch("16");
   ASN1ObjectIdentifier id_cmc_revokeRequest = id_cmc.branch("17");
   ASN1ObjectIdentifier id_cmc_confirmCertAcceptance = id_cmc.branch("24");
   ASN1ObjectIdentifier id_cmc_statusInfoV2 = id_cmc.branch("25");
   ASN1ObjectIdentifier id_cmc_trustedAnchors = id_cmc.branch("26");
   ASN1ObjectIdentifier id_cmc_authData = id_cmc.branch("27");
   ASN1ObjectIdentifier id_cmc_batchRequests = id_cmc.branch("28");
   ASN1ObjectIdentifier id_cmc_batchResponses = id_cmc.branch("29");
   ASN1ObjectIdentifier id_cmc_publishCert = id_cmc.branch("30");
   ASN1ObjectIdentifier id_cmc_modCertTemplate = id_cmc.branch("31");
   ASN1ObjectIdentifier id_cmc_controlProcessed = id_cmc.branch("32");
   ASN1ObjectIdentifier id_cmc_identityProofV2 = id_cmc.branch("34");
   ASN1ObjectIdentifier id_cmc_popLinkWitnessV2 = id_cmc.branch("33");
   ASN1ObjectIdentifier id_kp_cmcCA = id_kp.branch("27");
   ASN1ObjectIdentifier id_kp_cmcRA = id_kp.branch("28");
   ASN1ObjectIdentifier id_kp_cmKGA = id_kp.branch("32");
}
