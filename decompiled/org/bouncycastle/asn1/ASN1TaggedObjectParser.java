package org.bouncycastle.asn1;

import java.io.IOException;

public interface ASN1TaggedObjectParser extends ASN1Encodable, InMemoryRepresentable {
   int getTagClass();

   int getTagNo();

   boolean hasContextTag();

   boolean hasContextTag(int var1);

   boolean hasTag(int var1, int var2);

   boolean hasTagClass(int var1);

   ASN1Encodable parseBaseUniversal(boolean var1, int var2) throws IOException;

   ASN1Encodable parseExplicitBaseObject() throws IOException;

   ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException;

   ASN1TaggedObjectParser parseImplicitBaseTagged(int var1, int var2) throws IOException;
}
