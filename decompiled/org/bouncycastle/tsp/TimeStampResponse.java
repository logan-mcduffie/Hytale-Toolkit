package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.util.Arrays;

public class TimeStampResponse {
   private final TimeStampResp resp;
   private final TimeStampToken timeStampToken;

   private static TimeStampResp parseTimeStampResp(byte[] var0) throws IOException, TSPException {
      try {
         return TimeStampResp.getInstance(var0);
      } catch (IllegalArgumentException var2) {
         throw new TSPException("malformed timestamp response: " + var2, var2);
      } catch (ClassCastException var3) {
         throw new TSPException("malformed timestamp response: " + var3, var3);
      }
   }

   private static TimeStampResp parseTimeStampResp(InputStream var0) throws IOException, TSPException {
      try {
         return TimeStampResp.getInstance(new ASN1InputStream(var0).readObject());
      } catch (IllegalArgumentException var2) {
         throw new TSPException("malformed timestamp response: " + var2, var2);
      } catch (ClassCastException var3) {
         throw new TSPException("malformed timestamp response: " + var3, var3);
      }
   }

   public TimeStampResponse(TimeStampResp var1) throws TSPException, IOException {
      this.resp = var1;
      ContentInfo var2 = var1.getTimeStampToken();
      this.timeStampToken = var2 == null ? null : new TimeStampToken(var2);
   }

   public TimeStampResponse(byte[] var1) throws TSPException, IOException {
      this(parseTimeStampResp(var1));
   }

   public TimeStampResponse(InputStream var1) throws TSPException, IOException {
      this(parseTimeStampResp(var1));
   }

   TimeStampResponse(DLSequence var1) throws TSPException, IOException {
      try {
         this.resp = TimeStampResp.getInstance(var1);
         this.timeStampToken = new TimeStampToken(ContentInfo.getInstance(var1.getObjectAt(1)));
      } catch (IllegalArgumentException var3) {
         throw new TSPException("malformed timestamp response: " + var3, var3);
      } catch (ClassCastException var4) {
         throw new TSPException("malformed timestamp response: " + var4, var4);
      }
   }

   public int getStatus() {
      return this.resp.getStatus().getStatusObject().intValueExact();
   }

   public String getStatusString() {
      if (this.resp.getStatus().getStatusString() == null) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         PKIFreeText var2 = this.resp.getStatus().getStatusString();

         for (int var3 = 0; var3 != var2.size(); var3++) {
            var1.append(var2.getStringAtUTF8(var3).getString());
         }

         return var1.toString();
      }
   }

   public PKIFailureInfo getFailInfo() {
      return this.resp.getStatus().getFailInfo() != null ? new PKIFailureInfo(this.resp.getStatus().getFailInfo()) : null;
   }

   public TimeStampToken getTimeStampToken() {
      return this.timeStampToken;
   }

   public void validate(TimeStampRequest var1) throws TSPException {
      TimeStampToken var2 = this.getTimeStampToken();
      if (var2 != null) {
         TimeStampTokenInfo var3 = var2.getTimeStampInfo();
         if (var1.getNonce() != null && !var1.getNonce().equals(var3.getNonce())) {
            throw new TSPValidationException("response contains wrong nonce value.");
         }

         if (this.getStatus() != 0 && this.getStatus() != 1) {
            throw new TSPValidationException("time stamp token found in failed request.");
         }

         if (!var3.getMessageImprintAlgOID().equals(var1.getMessageImprintAlgOID())) {
            throw new TSPValidationException("response for different message imprint algorithm.");
         }

         if (!Arrays.constantTimeAreEqual(var1.getMessageImprintDigest(), var3.getMessageImprintDigest())) {
            throw new TSPValidationException("response for different message imprint digest.");
         }

         Attribute var4 = var2.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificate);
         Attribute var5 = var2.getSignedAttributes().get(PKCSObjectIdentifiers.id_aa_signingCertificateV2);
         if (var4 == null && var5 == null) {
            throw new TSPValidationException("no signing certificate attribute present.");
         }

         if (var4 != null && var5 != null) {
         }

         if (var1.getReqPolicy() != null && !var1.getReqPolicy().equals(var3.getPolicy())) {
            throw new TSPValidationException("TSA policy wrong for request.");
         }
      } else if (this.getStatus() == 0 || this.getStatus() == 1) {
         throw new TSPValidationException("no time stamp token found and one expected.");
      }
   }

   public byte[] getEncoded() throws IOException {
      return this.resp.getEncoded();
   }

   public byte[] getEncoded(String var1) throws IOException {
      Object var2 = this.resp;
      if ("DL".equals(var1)) {
         var2 = this.timeStampToken == null
            ? new DLSequence(this.resp.getStatus())
            : new DLSequence(this.resp.getStatus(), this.timeStampToken.toCMSSignedData().toASN1Structure());
      }

      return ((ASN1Object)var2).getEncoded(var1);
   }
}
