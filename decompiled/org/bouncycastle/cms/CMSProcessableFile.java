package org.bouncycastle.cms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.util.io.Streams;

public class CMSProcessableFile implements CMSTypedData, CMSReadable {
   private static final int DEFAULT_BUF_SIZE = 32768;
   private final ASN1ObjectIdentifier type;
   private final File file;
   private final int bufSize;

   public CMSProcessableFile(File var1) {
      this(var1, 32768);
   }

   public CMSProcessableFile(File var1, int var2) {
      this(CMSObjectIdentifiers.data, var1, var2);
   }

   public CMSProcessableFile(ASN1ObjectIdentifier var1, File var2, int var3) {
      this.type = var1;
      this.file = var2;
      this.bufSize = var3;
   }

   @Override
   public InputStream getInputStream() throws IOException, CMSException {
      return new BufferedInputStream(new FileInputStream(this.file), this.bufSize);
   }

   @Override
   public void write(OutputStream var1) throws IOException, CMSException {
      FileInputStream var2 = new FileInputStream(this.file);
      Streams.pipeAll(var2, var1, this.bufSize);
      var2.close();
   }

   @Override
   public Object getContent() {
      return this.file;
   }

   @Override
   public ASN1ObjectIdentifier getContentType() {
      return this.type;
   }
}
