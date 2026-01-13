package org.bouncycastle.mime.smime;

import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class SMimeParserContext implements MimeParserContext {
   private final String defaultContentTransferEncoding;
   private final DigestCalculatorProvider digestCalculatorProvider;

   public SMimeParserContext(String var1, DigestCalculatorProvider var2) {
      this.defaultContentTransferEncoding = var1;
      this.digestCalculatorProvider = var2;
   }

   @Override
   public String getDefaultContentTransferEncoding() {
      return this.defaultContentTransferEncoding;
   }

   public DigestCalculatorProvider getDigestCalculatorProvider() {
      return this.digestCalculatorProvider;
   }
}
