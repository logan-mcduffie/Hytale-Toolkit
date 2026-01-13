package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeParser;
import org.bouncycastle.mime.MimeParserProvider;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class SMimeParserProvider implements MimeParserProvider {
   private final String defaultContentTransferEncoding;
   private final DigestCalculatorProvider digestCalculatorProvider;

   public SMimeParserProvider(String var1, DigestCalculatorProvider var2) {
      this.defaultContentTransferEncoding = var1;
      this.digestCalculatorProvider = var2;
   }

   @Override
   public MimeParser createParser(InputStream var1) throws IOException {
      return new BasicMimeParser(new SMimeParserContext(this.defaultContentTransferEncoding, this.digestCalculatorProvider), SMimeUtils.autoBuffer(var1));
   }

   @Override
   public MimeParser createParser(Headers var1, InputStream var2) throws IOException {
      return new BasicMimeParser(new SMimeParserContext(this.defaultContentTransferEncoding, this.digestCalculatorProvider), var1, SMimeUtils.autoBuffer(var2));
   }
}
