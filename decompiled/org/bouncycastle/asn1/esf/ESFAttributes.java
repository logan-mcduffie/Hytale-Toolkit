package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public interface ESFAttributes {
   ASN1ObjectIdentifier sigPolicyId = PKCSObjectIdentifiers.id_aa_ets_sigPolicyId;
   ASN1ObjectIdentifier commitmentType = PKCSObjectIdentifiers.id_aa_ets_commitmentType;
   ASN1ObjectIdentifier signerLocation = PKCSObjectIdentifiers.id_aa_ets_signerLocation;
   ASN1ObjectIdentifier signerAttr = PKCSObjectIdentifiers.id_aa_ets_signerAttr;
   ASN1ObjectIdentifier otherSigCert = PKCSObjectIdentifiers.id_aa_ets_otherSigCert;
   ASN1ObjectIdentifier contentTimestamp = PKCSObjectIdentifiers.id_aa_ets_contentTimestamp;
   ASN1ObjectIdentifier certificateRefs = PKCSObjectIdentifiers.id_aa_ets_certificateRefs;
   ASN1ObjectIdentifier revocationRefs = PKCSObjectIdentifiers.id_aa_ets_revocationRefs;
   ASN1ObjectIdentifier certValues = PKCSObjectIdentifiers.id_aa_ets_certValues;
   ASN1ObjectIdentifier revocationValues = PKCSObjectIdentifiers.id_aa_ets_revocationValues;
   ASN1ObjectIdentifier escTimeStamp = PKCSObjectIdentifiers.id_aa_ets_escTimeStamp;
   ASN1ObjectIdentifier certCRLTimestamp = PKCSObjectIdentifiers.id_aa_ets_certCRLTimestamp;
   ASN1ObjectIdentifier archiveTimestamp = PKCSObjectIdentifiers.id_aa_ets_archiveTimestamp;
   ASN1ObjectIdentifier archiveTimestampV2 = PKCSObjectIdentifiers.id_aa.branch("48");
}
