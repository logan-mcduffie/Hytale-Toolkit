package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;
import org.bouncycastle.asn1.ASN1Util;

public class ContentInfoParser {
   private ASN1ObjectIdentifier contentType;
   private ASN1TaggedObjectParser content;

   public ContentInfoParser(ASN1SequenceParser var1) throws IOException {
      this.contentType = (ASN1ObjectIdentifier)var1.readObject();
      this.content = (ASN1TaggedObjectParser)var1.readObject();
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentType;
   }

   public ASN1Encodable getContent(int var1) throws IOException {
      return this.content != null ? ASN1Util.parseExplicitContextBaseObject(this.content, 0) : null;
   }
}
