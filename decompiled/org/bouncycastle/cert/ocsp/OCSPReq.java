package org.bouncycastle.cert.ocsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Exception;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;

public class OCSPReq {
   private static final X509CertificateHolder[] EMPTY_CERTS = new X509CertificateHolder[0];
   private OCSPRequest req;
   private Extensions extensions;

   public OCSPReq(OCSPRequest var1) {
      this.req = var1;
      this.extensions = var1.getTbsRequest().getRequestExtensions();
   }

   public OCSPReq(byte[] var1) throws IOException {
      this(new ASN1InputStream(var1));
   }

   private OCSPReq(ASN1InputStream var1) throws IOException {
      try {
         this.req = OCSPRequest.getInstance(var1.readObject());
         if (this.req == null) {
            throw new CertIOException("malformed request: no request data found");
         } else {
            this.extensions = this.req.getTbsRequest().getRequestExtensions();
         }
      } catch (IllegalArgumentException var3) {
         throw new CertIOException("malformed request: " + var3.getMessage(), var3);
      } catch (ClassCastException var4) {
         throw new CertIOException("malformed request: " + var4.getMessage(), var4);
      } catch (ASN1Exception var5) {
         throw new CertIOException("malformed request: " + var5.getMessage(), var5);
      }
   }

   public int getVersionNumber() {
      return this.req.getTbsRequest().getVersion().intValueExact() + 1;
   }

   public GeneralName getRequestorName() {
      return GeneralName.getInstance(this.req.getTbsRequest().getRequestorName());
   }

   public Req[] getRequestList() {
      ASN1Sequence var1 = this.req.getTbsRequest().getRequestList();
      Req[] var2 = new Req[var1.size()];

      for (int var3 = 0; var3 != var2.length; var3++) {
         var2[var3] = new Req(Request.getInstance(var1.getObjectAt(var3)));
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
      return !this.isSigned() ? null : this.req.getOptionalSignature().getSignatureAlgorithm().getAlgorithm();
   }

   public byte[] getSignature() {
      return !this.isSigned() ? null : this.req.getOptionalSignature().getSignature().getOctets();
   }

   public X509CertificateHolder[] getCerts() {
      if (this.req.getOptionalSignature() == null) {
         return EMPTY_CERTS;
      } else {
         ASN1Sequence var1 = this.req.getOptionalSignature().getCerts();
         if (var1 == null) {
            return EMPTY_CERTS;
         } else {
            X509CertificateHolder[] var2 = new X509CertificateHolder[var1.size()];

            for (int var3 = 0; var3 != var2.length; var3++) {
               var2[var3] = new X509CertificateHolder(Certificate.getInstance(var1.getObjectAt(var3)));
            }

            return var2;
         }
      }
   }

   public boolean isSigned() {
      return this.req.getOptionalSignature() != null;
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws OCSPException {
      if (!this.isSigned()) {
         throw new OCSPException("attempt to verify signature on unsigned object");
      } else {
         try {
            ContentVerifier var2 = var1.get(this.req.getOptionalSignature().getSignatureAlgorithm());
            OutputStream var3 = var2.getOutputStream();
            var3.write(this.req.getTbsRequest().getEncoded("DER"));
            return var2.verify(this.getSignature());
         } catch (Exception var4) {
            throw new OCSPException("exception processing signature: " + var4, var4);
         }
      }
   }

   public byte[] getEncoded() throws IOException {
      return this.req.getEncoded();
   }
}
