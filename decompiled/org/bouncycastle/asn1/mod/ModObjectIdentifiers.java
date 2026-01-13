package org.bouncycastle.asn1.mod;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface ModObjectIdentifiers {
   ASN1ObjectIdentifier id_mod = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.0");
   ASN1ObjectIdentifier id_mod_algorithmInformation_02 = id_mod.branch("58");
}
