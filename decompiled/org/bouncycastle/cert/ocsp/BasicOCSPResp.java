package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Encodable;

public class BasicOCSPResp implements Encodable {
   private BasicOCSPResponse resp;
   private ResponseData data;
   private Extensions extensions;

   public BasicOCSPResp(BasicOCSPResponse var1) {
      this.resp = var1;
      this.data = var1.getTbsResponseData();
      this.extensions = Extensions.getInstance(var1.getTbsResponseData().getResponseExtensions());
   }

   public byte[] getTBSResponseData() {
      try {
         return this.resp.getTbsResponseData().getEncoded("DER");
      } catch (IOException var2) {
         return null;
      }
   }

   public AlgorithmIdentifier getSignatureAlgorithmID() {
      return this.resp.getSignatureAlgorithm();
   }

   public int getVersion() {
      return this.data.getVersion().intValueExact() + 1;
   }

   public RespID getResponderId() {
      return new RespID(this.data.getResponderID());
   }

   public Date getProducedAt() {
      return OCSPUtils.extractDate(this.data.getProducedAt());
   }

   public SingleResp[] getResponses() {
      ASN1Sequence var1 = this.data.getResponses();
      SingleResp[] var2 = new SingleResp[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = new SingleResp(SingleResponse.getInstance(var1.getObjectAt(var3)));
      }

      return var2;
   }

   public boolean hasExtensions() {
      return this.extensions != null;
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return this.extensions != null ? this.extensions.getExtension(var1) : null;
   }

   public List getExtensionOIDs() {
      return OCSPUtils.getExtensionOIDs(this.extensions);
   }

   public Set getCriticalExtensionOIDs() {
      return OCSPUtils.getCriticalExtensionOIDs(this.extensions);
   }

   public Set getNonCriticalExtensionOIDs() {
      return OCSPUtils.getNonCriticalExtensionOIDs(this.extensions);
   }

   public ASN1ObjectIdentifier getSignatureAlgOID() {
      return this.resp.getSignatureAlgorithm().getAlgorithm();
   }

   public byte[] getSignature() {
      return this.resp.getSignature().getOctets();
   }

   public X509CertificateHolder[] getCerts() {
      if (this.resp.getCerts() == null) {
         return OCSPUtils.EMPTY_CERTS;
      } else {
         ASN1Sequence var1 = this.resp.getCerts();
         if (var1 == null) {
            return OCSPUtils.EMPTY_CERTS;
         } else {
            X509CertificateHolder[] var2 = new X509CertificateHolder[var1.size()];

            for (int var3 = 0; var3 != var2.length; var3++) {
               var2[var3] = new X509CertificateHolder(Certificate.getInstance(var1.getObjectAt(var3)));
            }

            return var2;
         }
      }
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws OCSPException {
      try {
         ContentVerifier var2 = var1.get(this.resp.getSignatureAlgorithm());
         OutputStream var3 = var2.getOutputStream();
         var3.write(this.resp.getTbsResponseData().getEncoded("DER"));
         var3.close();
         return var2.verify(this.getSignature());
      } catch (Exception var4) {
         throw new OCSPException("exception processing sig: " + var4, var4);
      }
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.resp.getEncoded();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BasicOCSPResp)) {
         return false;
      } else {
         BasicOCSPResp var2 = (BasicOCSPResp)var1;
         return this.resp.equals(var2.resp);
      }
   }

   @Override
   public int hashCode() {
      return this.resp.hashCode();
   }
}
