package org.bouncycastle.internal.asn1.iso;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface ISOIECObjectIdentifiers {
   ASN1ObjectIdentifier iso_encryption_algorithms = new ASN1ObjectIdentifier("1.0.10118");
   ASN1ObjectIdentifier hash_algorithms = iso_encryption_algorithms.branch("3.0");
   ASN1ObjectIdentifier ripemd160 = hash_algorithms.branch("49");
   ASN1ObjectIdentifier ripemd128 = hash_algorithms.branch("50");
   ASN1ObjectIdentifier whirlpool = hash_algorithms.branch("55");
   ASN1ObjectIdentifier is18033_2 = new ASN1ObjectIdentifier("1.0.18033.2");
   ASN1ObjectIdentifier id_ac_generic_hybrid = is18033_2.branch("1.2");
   ASN1ObjectIdentifier id_kem_rsa = is18033_2.branch("2.4");
}
