package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.cms.ContentInfoParser;

public class CMSContentInfoParser {
   protected ContentInfoParser _contentInfo;
   protected InputStream _data;

   protected CMSContentInfoParser(InputStream var1) throws CMSException {
      this._data = var1;

      try {
         ASN1StreamParser var2 = new ASN1StreamParser(var1);
         ASN1SequenceParser var3 = (ASN1SequenceParser)var2.readObject();
         if (var3 == null) {
            throw new CMSException("No content found.");
         } else {
            this._contentInfo = new ContentInfoParser(var3);
         }
      } catch (IOException var4) {
         throw new CMSException("IOException reading content.", var4);
      } catch (ClassCastException var5) {
         throw new CMSException("Unexpected object reading content.", var5);
      }
   }

   public void close() throws IOException {
      this._data.close();
   }
}
