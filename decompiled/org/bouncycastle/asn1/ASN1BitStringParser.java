package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

public interface ASN1BitStringParser extends ASN1Encodable, InMemoryRepresentable {
   InputStream getBitStream() throws IOException;

   InputStream getOctetStream() throws IOException;

   int getPadBits();
}
