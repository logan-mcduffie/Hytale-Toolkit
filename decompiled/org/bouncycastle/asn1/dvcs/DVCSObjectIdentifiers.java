package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface DVCSObjectIdentifiers {
   ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
   ASN1ObjectIdentifier id_smime = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16");
   ASN1ObjectIdentifier id_ad_dvcs = id_pkix.branch("48.4");
   ASN1ObjectIdentifier id_kp_dvcs = id_pkix.branch("3.10");
   ASN1ObjectIdentifier id_ct_DVCSRequestData = id_smime.branch("1.7");
   ASN1ObjectIdentifier id_ct_DVCSResponseData = id_smime.branch("1.8");
   ASN1ObjectIdentifier id_aa_dvcs_dvc = id_smime.branch("2.29");
}
