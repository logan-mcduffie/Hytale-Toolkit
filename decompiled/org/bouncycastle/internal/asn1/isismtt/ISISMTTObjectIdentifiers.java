package org.bouncycastle.internal.asn1.isismtt;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface ISISMTTObjectIdentifiers {
   ASN1ObjectIdentifier id_isismtt = new ASN1ObjectIdentifier("1.3.36.8");
   ASN1ObjectIdentifier id_isismtt_cp = id_isismtt.branch("1");
   ASN1ObjectIdentifier id_isismtt_cp_accredited = id_isismtt_cp.branch("1");
   ASN1ObjectIdentifier id_isismtt_at = id_isismtt.branch("3");
   ASN1ObjectIdentifier id_isismtt_at_dateOfCertGen = id_isismtt_at.branch("1");
   ASN1ObjectIdentifier id_isismtt_at_procuration = id_isismtt_at.branch("2");
   ASN1ObjectIdentifier id_isismtt_at_admission = id_isismtt_at.branch("3");
   ASN1ObjectIdentifier id_isismtt_at_monetaryLimit = id_isismtt_at.branch("4");
   ASN1ObjectIdentifier id_isismtt_at_declarationOfMajority = id_isismtt_at.branch("5");
   ASN1ObjectIdentifier id_isismtt_at_iCCSN = id_isismtt_at.branch("6");
   ASN1ObjectIdentifier id_isismtt_at_PKReference = id_isismtt_at.branch("7");
   ASN1ObjectIdentifier id_isismtt_at_restriction = id_isismtt_at.branch("8");
   ASN1ObjectIdentifier id_isismtt_at_retrieveIfAllowed = id_isismtt_at.branch("9");
   ASN1ObjectIdentifier id_isismtt_at_requestedCertificate = id_isismtt_at.branch("10");
   ASN1ObjectIdentifier id_isismtt_at_namingAuthorities = id_isismtt_at.branch("11");
   ASN1ObjectIdentifier id_isismtt_at_certInDirSince = id_isismtt_at.branch("12");
   ASN1ObjectIdentifier id_isismtt_at_certHash = id_isismtt_at.branch("13");
   ASN1ObjectIdentifier id_isismtt_at_nameAtBirth = id_isismtt_at.branch("14");
   ASN1ObjectIdentifier id_isismtt_at_additionalInformation = id_isismtt_at.branch("15");
   ASN1ObjectIdentifier id_isismtt_at_liabilityLimitationFlag = new ASN1ObjectIdentifier("0.2.262.1.10.12.0");
}
