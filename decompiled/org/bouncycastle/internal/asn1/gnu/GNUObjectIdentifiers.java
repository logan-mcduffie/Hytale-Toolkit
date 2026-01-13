package org.bouncycastle.internal.asn1.gnu;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface GNUObjectIdentifiers {
   ASN1ObjectIdentifier GNU = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.1");
   ASN1ObjectIdentifier GnuPG = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.2");
   ASN1ObjectIdentifier notation = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.2.1");
   ASN1ObjectIdentifier pkaAddress = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.2.1.1");
   ASN1ObjectIdentifier GnuRadar = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.3");
   ASN1ObjectIdentifier digestAlgorithm = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.12");
   ASN1ObjectIdentifier Tiger_192 = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.12.2");
   ASN1ObjectIdentifier encryptionAlgorithm = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13");
   ASN1ObjectIdentifier Serpent = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2");
   ASN1ObjectIdentifier Serpent_128_ECB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.1");
   ASN1ObjectIdentifier Serpent_128_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.2");
   ASN1ObjectIdentifier Serpent_128_OFB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.3");
   ASN1ObjectIdentifier Serpent_128_CFB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.4");
   ASN1ObjectIdentifier Serpent_192_ECB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.21");
   ASN1ObjectIdentifier Serpent_192_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.22");
   ASN1ObjectIdentifier Serpent_192_OFB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.23");
   ASN1ObjectIdentifier Serpent_192_CFB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.24");
   ASN1ObjectIdentifier Serpent_256_ECB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.41");
   ASN1ObjectIdentifier Serpent_256_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.42");
   ASN1ObjectIdentifier Serpent_256_OFB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.43");
   ASN1ObjectIdentifier Serpent_256_CFB = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.13.2.44");
   ASN1ObjectIdentifier CRC = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.14");
   ASN1ObjectIdentifier CRC32 = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.14.1");
   ASN1ObjectIdentifier ellipticCurve = new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.15");
   ASN1ObjectIdentifier Ed25519 = ellipticCurve.branch("1");
}
