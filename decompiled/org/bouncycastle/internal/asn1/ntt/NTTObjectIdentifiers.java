package org.bouncycastle.internal.asn1.ntt;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface NTTObjectIdentifiers {
   ASN1ObjectIdentifier id_camellia128_cbc = new ASN1ObjectIdentifier("1.2.392.200011.61.1.1.1.2");
   ASN1ObjectIdentifier id_camellia192_cbc = new ASN1ObjectIdentifier("1.2.392.200011.61.1.1.1.3");
   ASN1ObjectIdentifier id_camellia256_cbc = new ASN1ObjectIdentifier("1.2.392.200011.61.1.1.1.4");
   ASN1ObjectIdentifier id_camellia128_wrap = new ASN1ObjectIdentifier("1.2.392.200011.61.1.1.3.2");
   ASN1ObjectIdentifier id_camellia192_wrap = new ASN1ObjectIdentifier("1.2.392.200011.61.1.1.3.3");
   ASN1ObjectIdentifier id_camellia256_wrap = new ASN1ObjectIdentifier("1.2.392.200011.61.1.1.3.4");
}
