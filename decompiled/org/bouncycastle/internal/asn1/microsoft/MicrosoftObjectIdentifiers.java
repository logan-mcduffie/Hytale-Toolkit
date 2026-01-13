package org.bouncycastle.internal.asn1.microsoft;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface MicrosoftObjectIdentifiers {
   ASN1ObjectIdentifier microsoft = new ASN1ObjectIdentifier("1.3.6.1.4.1.311");
   ASN1ObjectIdentifier microsoftCertTemplateV1 = microsoft.branch("20.2");
   ASN1ObjectIdentifier microsoftCaVersion = microsoft.branch("21.1");
   ASN1ObjectIdentifier microsoftPrevCaCertHash = microsoft.branch("21.2");
   ASN1ObjectIdentifier microsoftCrlNextPublish = microsoft.branch("21.4");
   ASN1ObjectIdentifier microsoftCertTemplateV2 = microsoft.branch("21.7");
   ASN1ObjectIdentifier microsoftAppPolicies = microsoft.branch("21.10");
}
