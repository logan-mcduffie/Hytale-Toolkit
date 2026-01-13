package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class CMSProcessableByteArray implements CMSTypedData, CMSReadable {
   private final ASN1ObjectIdentifier type;
   private final byte[] bytes;

   public CMSProcessableByteArray(byte[] var1) {
      this(CMSObjectIdentifiers.data, var1);
   }

   public CMSProcessableByteArray(ASN1ObjectIdentifier var1, byte[] var2) {
      this.type = var1;
      this.bytes = var2;
   }

   @Override
   public InputStream getInputStream() {
      return new ByteArrayInputStream(this.bytes);
   }

   @Override
   public void write(OutputStream var1) throws IOException, CMSException {
      var1.write(this.bytes);
   }

   @Override
   public Object getContent() {
      return Arrays.clone(this.bytes);
   }

   @Override
   public ASN1ObjectIdentifier getContentType() {
      return this.type;
   }
}
