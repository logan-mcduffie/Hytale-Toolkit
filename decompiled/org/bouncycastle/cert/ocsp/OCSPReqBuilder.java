package org.bouncycastle.cert.ocsp;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;

public class OCSPReqBuilder {
   private List list = new ArrayList();
   private GeneralName requestorName = null;
   private Extensions requestExtensions = null;

   public OCSPReqBuilder addRequest(CertificateID var1) {
      this.list.add(new OCSPReqBuilder.RequestObject(var1, null));
      return this;
   }

   public OCSPReqBuilder addRequest(CertificateID var1, Extensions var2) {
      this.list.add(new OCSPReqBuilder.RequestObject(var1, var2));
      return this;
   }

   public OCSPReqBuilder setRequestorName(X500Name var1) {
      this.requestorName = new GeneralName(4, var1);
      return this;
   }

   public OCSPReqBuilder setRequestorName(GeneralName var1) {
      this.requestorName = var1;
      return this;
   }

   public OCSPReqBuilder setRequestExtensions(Extensions var1) {
      this.requestExtensions = var1;
      return this;
   }

   private OCSPReq generateRequest(ContentSigner var1, X509CertificateHolder[] var2) throws OCSPException {
      Iterator var3 = this.list.iterator();
      ASN1EncodableVector var4 = new ASN1EncodableVector();

      while (var3.hasNext()) {
         try {
            var4.add(((OCSPReqBuilder.RequestObject)var3.next()).toRequest());
         } catch (Exception var12) {
            throw new OCSPException("exception creating Request", var12);
         }
      }

      TBSRequest var5 = new TBSRequest(this.requestorName, new DERSequence(var4), this.requestExtensions);
      Signature var6 = null;
      if (var1 != null) {
         if (this.requestorName == null) {
            throw new OCSPException("requestorName must be specified if request is signed.");
         }

         try {
            OutputStream var7 = var1.getOutputStream();
            var7.write(var5.getEncoded("DER"));
            var7.close();
         } catch (Exception var11) {
            throw new OCSPException("exception processing TBSRequest: " + var11, var11);
         }

         DERBitString var13 = new DERBitString(var1.getSignature());
         AlgorithmIdentifier var8 = var1.getAlgorithmIdentifier();
         if (var2 != null && var2.length > 0) {
            ASN1EncodableVector var9 = new ASN1EncodableVector();

            for (int var10 = 0; var10 != var2.length; var10++) {
               var9.add(var2[var10].toASN1Structure());
            }

            var6 = new Signature(var8, var13, new DERSequence(var9));
         } else {
            var6 = new Signature(var8, var13);
         }
      }

      return new OCSPReq(new OCSPRequest(var5, var6));
   }

   public OCSPReq build() throws OCSPException {
      return this.generateRequest(null, null);
   }

   public OCSPReq build(ContentSigner var1, X509CertificateHolder[] var2) throws OCSPException, IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("no signer specified");
      } else {
         return this.generateRequest(var1, var2);
      }
   }

   private static class RequestObject {
      CertificateID certId;
      Extensions extensions;

      RequestObject(CertificateID var1, Extensions var2) {
         this.certId = var1;
         this.extensions = var2;
      }

      Request toRequest() throws Exception {
         return new Request(this.certId.toASN1Primitive(), this.extensions);
      }
   }
}
