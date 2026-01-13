package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.mime.CanonicalOutputStream;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeMultipartContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

public class SMimeMultipartContext implements MimeMultipartContext {
   private final SMimeParserContext parserContext;
   private DigestCalculator[] calculators;

   public SMimeMultipartContext(MimeParserContext var1, Headers var2) {
      this.parserContext = (SMimeParserContext)var1;
      this.calculators = this.createDigestCalculators(var2);
   }

   DigestCalculator[] getDigestCalculators() {
      return this.calculators;
   }

   OutputStream getDigestOutputStream() {
      if (this.calculators.length == 1) {
         return this.calculators[0].getOutputStream();
      } else {
         Object var1 = this.calculators[0].getOutputStream();

         for (int var2 = 1; var2 < this.calculators.length; var2++) {
            var1 = new TeeOutputStream(this.calculators[var2].getOutputStream(), (OutputStream)var1);
         }

         return (OutputStream)var1;
      }
   }

   private DigestCalculator[] createDigestCalculators(Headers var1) {
      try {
         Map var2 = var1.getContentTypeAttributes();
         String var3 = (String)var2.get("micalg");
         if (var3 == null) {
            throw new IllegalStateException("No micalg field on content-type header");
         } else {
            String[] var4 = var3.substring(var3.indexOf(61) + 1).split(",");
            DigestCalculator[] var5 = new DigestCalculator[var4.length];

            for (int var6 = 0; var6 < var4.length; var6++) {
               String var7 = SMimeUtils.lessQuotes(var4[var6]).trim();
               var5[var6] = this.parserContext.getDigestCalculatorProvider().get(new AlgorithmIdentifier(SMimeUtils.getDigestOID(var7)));
            }

            return var5;
         }
      } catch (OperatorCreationException var8) {
         return null;
      }
   }

   @Override
   public MimeContext createContext(final int var1) throws IOException {
      return new MimeContext() {
         @Override
         public InputStream applyContext(Headers var1x, InputStream var2) throws IOException {
            if (var1 == 0) {
               OutputStream var3 = SMimeMultipartContext.this.getDigestOutputStream();
               var1x.dumpHeaders(var3);
               var3.write(13);
               var3.write(10);
               return new TeeInputStream(var2, new CanonicalOutputStream(SMimeMultipartContext.this.parserContext, var1x, var3));
            } else {
               return var2;
            }
         }
      };
   }

   @Override
   public InputStream applyContext(Headers var1, InputStream var2) throws IOException {
      return var2;
   }
}
