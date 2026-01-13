package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;

public class CMSAbsentContent implements CMSTypedData, CMSReadable {
   private final ASN1ObjectIdentifier type;

   public CMSAbsentContent() {
      this(CMSObjectIdentifiers.data);
   }

   public CMSAbsentContent(ASN1ObjectIdentifier var1) {
      this.type = var1;
   }

   @Override
   public InputStream getInputStream() {
      return null;
   }

   @Override
   public void write(OutputStream var1) throws IOException, CMSException {
   }

   @Override
   public Object getContent() {
      return null;
   }

   @Override
   public ASN1ObjectIdentifier getContentType() {
      return this.type;
   }
}
