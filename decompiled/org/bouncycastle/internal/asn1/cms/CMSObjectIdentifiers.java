package org.bouncycastle.internal.asn1.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

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
   ASN1ObjectIdentifier id_ri = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.16");
   ASN1ObjectIdentifier id_ri_ocsp_response = id_ri.branch("2");
   ASN1ObjectIdentifier id_ri_scvp = id_ri.branch("4");
   ASN1ObjectIdentifier id_alg = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.6");
   ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE128 = id_alg.branch("30");
   ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE256 = id_alg.branch("31");
   ASN1ObjectIdentifier id_ecdsa_with_shake128 = id_alg.branch("32");
   ASN1ObjectIdentifier id_ecdsa_with_shake256 = id_alg.branch("33");
}
