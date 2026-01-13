package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.mime.ConstantMimeContext;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeIOException;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.MimeParserListener;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.io.Streams;

public abstract class SMimeParserListener implements MimeParserListener {
   private DigestCalculator[] digestCalculators;
   private SMimeMultipartContext parent;

   @Override
   public MimeContext createContext(MimeParserContext var1, Headers var2) {
      if (var2.isMultipart()) {
         this.parent = new SMimeMultipartContext(var1, var2);
         this.digestCalculators = this.parent.getDigestCalculators();
         return this.parent;
      } else {
         return new ConstantMimeContext();
      }
   }

   @Override
   public void object(MimeParserContext var1, Headers var2, InputStream var3) throws IOException {
      try {
         if (var2.getContentType().equals("application/pkcs7-signature") || var2.getContentType().equals("application/x-pkcs7-signature")) {
            HashMap var8 = new HashMap();

            for (int var5 = 0; var5 != this.digestCalculators.length; var5++) {
               this.digestCalculators[var5].getOutputStream().close();
               var8.put(this.digestCalculators[var5].getAlgorithmIdentifier().getAlgorithm(), this.digestCalculators[var5].getDigest());
            }

            byte[] var9 = Streams.readAll(var3);
            CMSSignedData var6 = new CMSSignedData(var8, var9);
            this.signedData(var1, var2, var6.getCertificates(), var6.getCRLs(), var6.getAttributeCertificates(), var6.getSignerInfos());
         } else if (!var2.getContentType().equals("application/pkcs7-mime") && !var2.getContentType().equals("application/x-pkcs7-mime")) {
            this.content(var1, var2, var3);
         } else {
            CMSEnvelopedDataParser var4 = new CMSEnvelopedDataParser(var3);
            this.envelopedData(var1, var2, var4.getOriginatorInfo(), var4.getRecipientInfos());
            var4.close();
         }
      } catch (CMSException var7) {
         throw new MimeIOException("CMS failure: " + var7.getMessage(), var7);
      }
   }

   public void content(MimeParserContext var1, Headers var2, InputStream var3) throws IOException {
      throw new IllegalStateException("content handling not implemented");
   }

   public void signedData(MimeParserContext var1, Headers var2, Store var3, Store var4, Store var5, SignerInformationStore var6) throws IOException, CMSException {
      throw new IllegalStateException("signedData handling not implemented");
   }

   public void envelopedData(MimeParserContext var1, Headers var2, OriginatorInformation var3, RecipientInformationStore var4) throws IOException, CMSException {
      throw new IllegalStateException("envelopedData handling not implemented");
   }
}
