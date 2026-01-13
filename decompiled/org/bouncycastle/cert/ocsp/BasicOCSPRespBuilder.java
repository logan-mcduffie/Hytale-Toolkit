package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.RevokedInfo;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;

public class BasicOCSPRespBuilder {
   private List list = new ArrayList();
   private Extensions responseExtensions = null;
   private RespID responderID;

   public BasicOCSPRespBuilder(RespID var1) {
      this.responderID = var1;
   }

   public BasicOCSPRespBuilder(SubjectPublicKeyInfo var1, DigestCalculator var2) throws OCSPException {
      this.responderID = new RespID(var1, var2);
   }

   public BasicOCSPRespBuilder addResponse(CertificateID var1, CertificateStatus var2) {
      this.addResponse(var1, var2, new Date(), null, null);
      return this;
   }

   public BasicOCSPRespBuilder addResponse(CertificateID var1, CertificateStatus var2, Extensions var3) {
      this.addResponse(var1, var2, new Date(), null, var3);
      return this;
   }

   public BasicOCSPRespBuilder addResponse(CertificateID var1, CertificateStatus var2, Date var3, Extensions var4) {
      this.addResponse(var1, var2, new Date(), var3, var4);
      return this;
   }

   public BasicOCSPRespBuilder addResponse(CertificateID var1, CertificateStatus var2, Date var3, Date var4) {
      this.addResponse(var1, var2, var3, var4, null);
      return this;
   }

   public BasicOCSPRespBuilder addResponse(CertificateID var1, CertificateStatus var2, Date var3, Date var4, Extensions var5) {
      this.list.add(new BasicOCSPRespBuilder.ResponseObject(var1, var2, var3, var4, var5));
      return this;
   }

   public BasicOCSPRespBuilder setResponseExtensions(Extensions var1) {
      this.responseExtensions = var1;
      return this;
   }

   public BasicOCSPResp build(ContentSigner var1, X509CertificateHolder[] var2, Date var3) throws OCSPException {
      Iterator var4 = this.list.iterator();
      ASN1EncodableVector var5 = new ASN1EncodableVector();

      while (var4.hasNext()) {
         try {
            var5.add(((BasicOCSPRespBuilder.ResponseObject)var4.next()).toResponse());
         } catch (Exception var13) {
            throw new OCSPException("exception creating Request", var13);
         }
      }

      ResponseData var6 = new ResponseData(this.responderID.toASN1Primitive(), new ASN1GeneralizedTime(var3), new DERSequence(var5), this.responseExtensions);

      DERBitString var7;
      try {
         OutputStream var8 = var1.getOutputStream();
         var8.write(var6.getEncoded("DER"));
         var8.close();
         var7 = new DERBitString(var1.getSignature());
      } catch (Exception var12) {
         throw new OCSPException("exception processing TBSRequest: " + var12.getMessage(), var12);
      }

      AlgorithmIdentifier var14 = var1.getAlgorithmIdentifier();
      DERSequence var9 = null;
      if (var2 != null && var2.length > 0) {
         ASN1EncodableVector var10 = new ASN1EncodableVector();

         for (int var11 = 0; var11 != var2.length; var11++) {
            var10.add(var2[var11].toASN1Structure());
         }

         var9 = new DERSequence(var10);
      }

      return new BasicOCSPResp(new BasicOCSPResponse(var6, var14, var7, var9));
   }

   private static class ResponseObject {
      CertificateID certId;
      CertStatus certStatus;
      ASN1GeneralizedTime thisUpdate;
      ASN1GeneralizedTime nextUpdate;
      Extensions extensions;

      ResponseObject(CertificateID var1, CertificateStatus var2, Date var3, Date var4, Extensions var5) {
         this.certId = var1;
         if (var2 == null) {
            this.certStatus = new CertStatus();
         } else if (var2 instanceof UnknownStatus) {
            this.certStatus = new CertStatus(2, DERNull.INSTANCE);
         } else {
            RevokedStatus var6 = (RevokedStatus)var2;
            if (var6.hasRevocationReason()) {
               this.certStatus = new CertStatus(
                  new RevokedInfo(new ASN1GeneralizedTime(var6.getRevocationTime()), CRLReason.lookup(var6.getRevocationReason()))
               );
            } else {
               this.certStatus = new CertStatus(new RevokedInfo(new ASN1GeneralizedTime(var6.getRevocationTime()), null));
            }
         }

         this.thisUpdate = new DERGeneralizedTime(var3);
         if (var4 != null) {
            this.nextUpdate = new DERGeneralizedTime(var4);
         } else {
            this.nextUpdate = null;
         }

         this.extensions = var5;
      }

      SingleResponse toResponse() throws Exception {
         return new SingleResponse(this.certId.toASN1Primitive(), this.certStatus, this.thisUpdate, this.nextUpdate, this.extensions);
      }
   }
}
