package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeIOException;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Strings;

public class SMIMEEnvelopedWriter extends MimeWriter {
   private final CMSEnvelopedDataStreamGenerator envGen;
   private final OutputEncryptor outEnc;
   private final OutputStream mimeOut;
   private final String contentTransferEncoding;

   private SMIMEEnvelopedWriter(SMIMEEnvelopedWriter.Builder var1, OutputEncryptor var2, OutputStream var3) {
      super(new Headers(mapToLines(var1.headers), var1.contentTransferEncoding));
      this.envGen = var1.envGen;
      this.contentTransferEncoding = var1.contentTransferEncoding;
      this.outEnc = var2;
      this.mimeOut = var3;
   }

   @Override
   public OutputStream getContentStream() throws IOException {
      this.headers.dumpHeaders(this.mimeOut);
      this.mimeOut.write(Strings.toByteArray("\r\n"));

      try {
         Object var1 = this.mimeOut;
         if ("base64".equals(this.contentTransferEncoding)) {
            var1 = new Base64OutputStream((OutputStream)var1);
         }

         OutputStream var2 = this.envGen.open(SMimeUtils.createUnclosable((OutputStream)var1), this.outEnc);
         return new SMIMEEnvelopedWriter.ContentOutputStream(var2, (OutputStream)var1);
      } catch (CMSException var3) {
         throw new MimeIOException(var3.getMessage(), var3);
      }
   }

   public static class Builder {
      private static final String[] stdHeaders = new String[]{"Content-Type", "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
      private static final String[] stdValues = new String[]{
         "application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", "base64", "S/MIME Encrypted Message"
      };
      private final CMSEnvelopedDataStreamGenerator envGen = new CMSEnvelopedDataStreamGenerator();
      private final Map<String, String> headers = new LinkedHashMap<>();
      String contentTransferEncoding = "base64";

      public Builder() {
         for (int var1 = 0; var1 != stdHeaders.length; var1++) {
            this.headers.put(stdHeaders[var1], stdValues[var1]);
         }
      }

      public SMIMEEnvelopedWriter.Builder setBufferSize(int var1) {
         this.envGen.setBufferSize(var1);
         return this;
      }

      public SMIMEEnvelopedWriter.Builder setUnprotectedAttributeGenerator(CMSAttributeTableGenerator var1) {
         this.envGen.setUnprotectedAttributeGenerator(var1);
         return this;
      }

      public SMIMEEnvelopedWriter.Builder setOriginatorInfo(OriginatorInformation var1) {
         this.envGen.setOriginatorInfo(var1);
         return this;
      }

      public SMIMEEnvelopedWriter.Builder withHeader(String var1, String var2) {
         this.headers.put(var1, var2);
         return this;
      }

      public SMIMEEnvelopedWriter.Builder addRecipientInfoGenerator(RecipientInfoGenerator var1) {
         this.envGen.addRecipientInfoGenerator(var1);
         return this;
      }

      public SMIMEEnvelopedWriter build(OutputStream var1, OutputEncryptor var2) {
         return new SMIMEEnvelopedWriter(this, var2, SMimeUtils.autoBuffer(var1));
      }
   }

   private static class ContentOutputStream extends OutputStream {
      private final OutputStream main;
      private final OutputStream backing;

      ContentOutputStream(OutputStream var1, OutputStream var2) {
         this.main = var1;
         this.backing = var2;
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this.main.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.main.write(var1, var2, var3);
      }

      @Override
      public void write(int var1) throws IOException {
         this.main.write(var1);
      }

      @Override
      public void close() throws IOException {
         this.main.close();
         if (this.backing != null) {
            this.backing.close();
         }
      }
   }
}
