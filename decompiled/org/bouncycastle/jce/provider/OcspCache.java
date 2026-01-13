package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

class OcspCache {
   private static final int DEFAULT_TIMEOUT = 15000;
   private static final int DEFAULT_MAX_RESPONSE_SIZE = 32768;
   private static Map<URI, WeakReference<Map<CertID, OCSPResponse>>> cache = Collections.synchronizedMap(new WeakHashMap<>());

   static OCSPResponse getOcspResponse(
      CertID var0, PKIXCertRevocationCheckerParameters var1, URI var2, X509Certificate var3, List<Extension> var4, JcaJceHelper var5
   ) throws CertPathValidatorException {
      Map var6 = null;
      WeakReference var7 = cache.get(var2);
      if (var7 != null) {
         var6 = (Map)var7.get();
      }

      if (var6 != null) {
         OCSPResponse var8 = (OCSPResponse)var6.get(var0);
         if (var8 != null) {
            BasicOCSPResponse var9 = BasicOCSPResponse.getInstance(ASN1OctetString.getInstance(var8.getResponseBytes().getResponse()).getOctets());
            boolean var10 = isCertIDFoundAndCurrent(var9, var1.getValidDate(), var0);
            if (var10) {
               return var8;
            }

            var6.remove(var0);
         }
      }

      URL var28;
      try {
         var28 = var2.toURL();
      } catch (MalformedURLException var24) {
         throw new CertPathValidatorException("configuration error: " + var24.getMessage(), var24, var1.getCertPath(), var1.getIndex());
      }

      ASN1EncodableVector var29 = new ASN1EncodableVector();
      var29.add(new Request(var0, null));
      List var30 = var4;
      ASN1EncodableVector var11 = new ASN1EncodableVector();
      byte[] var12 = null;

      for (int var13 = 0; var13 != var30.size(); var13++) {
         Extension var14 = (Extension)var30.get(var13);
         ASN1ObjectIdentifier var15 = new ASN1ObjectIdentifier(var14.getId());
         DEROctetString var16 = new DEROctetString(var14.getValue());
         if (OCSPObjectIdentifiers.id_pkix_ocsp_nonce.equals(var15)) {
            var12 = Arrays.clone(var16.getOctets());
         }

         var11.add(new org.bouncycastle.asn1.x509.Extension(var15, var14.isCritical(), var16));
      }

      TBSRequest var31;
      if (var11.size() != 0) {
         var31 = new TBSRequest(null, new DERSequence(var29), Extensions.getInstance(new DERSequence(var11)));
      } else {
         var31 = new TBSRequest(null, new DERSequence(var29), (Extensions)null);
      }

      Object var32 = null;

      try {
         byte[] var33 = new OCSPRequest(var31, (Signature)var32).getEncoded();
         HttpURLConnection var34 = (HttpURLConnection)var28.openConnection();
         var34.setConnectTimeout(15000);
         var34.setReadTimeout(15000);
         var34.setDoOutput(true);
         var34.setDoInput(true);
         var34.setRequestMethod("POST");
         var34.setRequestProperty("Content-type", "application/ocsp-request");
         var34.setRequestProperty("Content-length", String.valueOf(var33.length));
         OutputStream var17 = var34.getOutputStream();
         var17.write(var33);
         var17.flush();
         InputStream var18 = var34.getInputStream();
         int var19 = var34.getContentLength();
         if (var19 < 0) {
            var19 = 32768;
         }

         OCSPResponse var20 = OCSPResponse.getInstance(Streams.readAllLimited(var18, var19));
         if (0 != var20.getResponseStatus().getIntValue()) {
            throw new CertPathValidatorException("OCSP responder failed: " + var20.getResponseStatus().getValue(), null, var1.getCertPath(), var1.getIndex());
         } else {
            boolean var21 = false;
            ResponseBytes var22 = ResponseBytes.getInstance(var20.getResponseBytes());
            if (var22.getResponseType().equals(OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
               BasicOCSPResponse var23 = BasicOCSPResponse.getInstance(var22.getResponse().getOctets());
               var21 = ProvOcspRevocationChecker.validatedOcspResponse(var23, var1, var12, var3, var5)
                  && isCertIDFoundAndCurrent(var23, var1.getValidDate(), var0);
            }

            if (!var21) {
               throw new CertPathValidatorException("OCSP response failed to validate", null, var1.getCertPath(), var1.getIndex());
            } else {
               var7 = cache.get(var2);
               if (var7 != null) {
                  var6 = (Map)var7.get();
               }

               if (var6 != null) {
                  var6.put(var0, var20);
               } else {
                  HashMap var26 = new HashMap();
                  var26.put(var0, var20);
                  cache.put(var2, new WeakReference<>(var26));
               }

               return var20;
            }
         }
      } catch (IOException var25) {
         throw new CertPathValidatorException("configuration error: " + var25.getMessage(), var25, var1.getCertPath(), var1.getIndex());
      }
   }

   private static boolean isCertIDFoundAndCurrent(BasicOCSPResponse var0, Date var1, CertID var2) {
      ResponseData var3 = ResponseData.getInstance(var0.getTbsResponseData());
      ASN1Sequence var4 = var3.getResponses();

      for (int var5 = 0; var5 != var4.size(); var5++) {
         SingleResponse var6 = SingleResponse.getInstance(var4.getObjectAt(var5));
         if (var2.equals(var6.getCertID())) {
            ASN1GeneralizedTime var7 = var6.getNextUpdate();

            try {
               if (var7 != null && var1.after(var7.getDate())) {
                  return false;
               }

               return true;
            } catch (ParseException var9) {
               return false;
            }
         }
      }

      return false;
   }
}
