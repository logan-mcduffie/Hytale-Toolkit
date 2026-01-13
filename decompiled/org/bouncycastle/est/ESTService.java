package org.bouncycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cmc.CMCException;
import org.bouncycastle.cmc.SimplePKIResponse;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.ConstantMimeContext;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.MimeParserListener;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

public class ESTService {
   protected static final String CACERTS = "/cacerts";
   protected static final String SIMPLE_ENROLL = "/simpleenroll";
   protected static final String SIMPLE_REENROLL = "/simplereenroll";
   protected static final String FULLCMC = "/fullcmc";
   protected static final String SERVERGEN = "/serverkeygen";
   protected static final String CSRATTRS = "/csrattrs";
   protected static final Set<String> illegalParts = new HashSet<>();
   private final String server;
   private final ESTClientProvider clientProvider;
   private static final Pattern pathInValid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;:=]+");

   ESTService(String var1, String var2, ESTClientProvider var3) {
      var1 = this.verifyServer(var1);
      if (var2 != null) {
         var2 = this.verifyLabel(var2);
         this.server = "https://" + var1 + "/.well-known/est/" + var2;
      } else {
         this.server = "https://" + var1 + "/.well-known/est";
      }

      this.clientProvider = var3;
   }

   public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> var0) {
      return storeToArray(var0, null);
   }

   public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> var0, Selector<X509CertificateHolder> var1) {
      Collection var2 = var0.getMatches(var1);
      return var2.toArray(new X509CertificateHolder[var2.size()]);
   }

   public CACertsResponse getCACerts() throws ESTException {
      ESTResponse var1 = null;
      Object var2 = null;
      CACertsResponse var3 = null;
      Object var4 = null;
      boolean var5 = false;

      try {
         var4 = new URL(this.server + "/cacerts");
         ESTClient var6 = this.clientProvider.makeClient();
         ESTRequest var7 = new ESTRequestBuilder("GET", (URL)var4).withClient(var6).build();
         var1 = var6.doRequest(var7);
         Store var8 = null;
         Store var9 = null;
         if (var1.getStatusCode() == 200) {
            String var10 = var1.getHeaders().getFirstValue("Content-Type");
            if (var10 == null || !var10.startsWith("application/pkcs7-mime")) {
               String var27 = var10 != null ? " got " + var10 : " but was not present.";
               throw new ESTException(
                  "Response : " + var4.toString() + "Expecting application/pkcs7-mime " + var27, null, var1.getStatusCode(), var1.getInputStream()
               );
            }

            try {
               ASN1InputStream var11 = this.getASN1InputStream(var1.getInputStream(), var1.getContentLength());
               SimplePKIResponse var12 = new SimplePKIResponse(ContentInfo.getInstance(var11.readObject()));
               var8 = var12.getCertificates();
               var9 = var12.getCRLs();
            } catch (Throwable var21) {
               throw new ESTException("Decoding CACerts: " + var4.toString() + " " + var21.getMessage(), var21, var1.getStatusCode(), var1.getInputStream());
            }
         } else if (var1.getStatusCode() != 204) {
            throw new ESTException("Get CACerts: " + var4.toString(), null, var1.getStatusCode(), var1.getInputStream());
         }

         var3 = new CACertsResponse(var8, var9, var7, var1.getSource(), this.clientProvider.isTrusted());
      } catch (Throwable var22) {
         var5 = true;
         if (var22 instanceof ESTException) {
            throw (ESTException)var22;
         }

         throw new ESTException(var22.getMessage(), var22);
      } finally {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Exception var20) {
            }
         }
      }

      if (var2 != null) {
         if (var2 instanceof ESTException) {
            throw (ESTException)var2;
         } else {
            throw new ESTException("Get CACerts: " + var4.toString(), (Throwable)var2, var1.getStatusCode(), null);
         }
      } else {
         return var3;
      }
   }

   private ASN1InputStream getASN1InputStream(InputStream var1, Long var2) {
      if (var2 == null) {
         return new ASN1InputStream(var1);
      } else {
         return var2.intValue() == var2 ? new ASN1InputStream(var1, var2.intValue()) : new ASN1InputStream(var1);
      }
   }

   public EnrollmentResponse simpleEnroll(EnrollmentResponse var1) throws Exception {
      if (!this.clientProvider.isTrusted()) {
         throw new IllegalStateException("No trust anchors.");
      } else {
         ESTResponse var2 = null;

         EnrollmentResponse var4;
         try {
            ESTClient var3 = this.clientProvider.makeClient();
            var2 = var3.doRequest(new ESTRequestBuilder(var1.getRequestToRetry()).withClient(var3).build());
            var4 = this.handleEnrollResponse(var2);
         } catch (Throwable var8) {
            if (var8 instanceof ESTException) {
               throw (ESTException)var8;
            }

            throw new ESTException(var8.getMessage(), var8);
         } finally {
            if (var2 != null) {
               var2.close();
            }
         }

         return var4;
      }
   }

   protected EnrollmentResponse enroll(boolean var1, PKCS10CertificationRequest var2, ESTAuth var3, boolean var4) throws IOException {
      if (!this.clientProvider.isTrusted()) {
         throw new IllegalStateException("No trust anchors.");
      } else {
         ESTResponse var5 = null;

         EnrollmentResponse var10;
         try {
            byte[] var6 = this.annotateRequest(var2.getEncoded()).getBytes();
            URL var7 = new URL(this.server + (var4 ? "/serverkeygen" : (var1 ? "/simplereenroll" : "/simpleenroll")));
            ESTClient var8 = this.clientProvider.makeClient();
            ESTRequestBuilder var9 = new ESTRequestBuilder("POST", var7).withData(var6).withClient(var8);
            var9.addHeader("Content-Type", "application/pkcs10");
            var9.addHeader("Content-Length", "" + var6.length);
            var9.addHeader("Content-Transfer-Encoding", "base64");
            if (var3 != null) {
               var3.applyAuth(var9);
            }

            var5 = var8.doRequest(var9.build());
            var10 = this.handleEnrollResponse(var5);
         } catch (Throwable var14) {
            if (var14 instanceof ESTException) {
               throw (ESTException)var14;
            }

            throw new ESTException(var14.getMessage(), var14);
         } finally {
            if (var5 != null) {
               var5.close();
            }
         }

         return var10;
      }
   }

   public EnrollmentResponse simpleEnroll(boolean var1, PKCS10CertificationRequest var2, ESTAuth var3) throws IOException {
      return this.enroll(var1, var2, var3, false);
   }

   public EnrollmentResponse simpleEnrollWithServersideCreation(PKCS10CertificationRequest var1, ESTAuth var2) throws IOException {
      return this.enroll(false, var1, var2, true);
   }

   public EnrollmentResponse enrollPop(boolean var1, final PKCS10CertificationRequestBuilder var2, final ContentSigner var3, ESTAuth var4, boolean var5) throws IOException {
      if (!this.clientProvider.isTrusted()) {
         throw new IllegalStateException("No trust anchors.");
      } else {
         ESTResponse var6 = null;

         EnrollmentResponse var10;
         try {
            URL var7 = new URL(this.server + (var1 ? "/simplereenroll" : "/simpleenroll"));
            ESTClient var8 = this.clientProvider.makeClient();
            ESTRequestBuilder var9 = new ESTRequestBuilder("POST", var7).withClient(var8).withConnectionListener(new ESTSourceConnectionListener() {
               @Override
               public ESTRequest onConnection(Source var1, ESTRequest var2x) throws IOException {
                  if (var1 instanceof TLSUniqueProvider && ((TLSUniqueProvider)var1).isTLSUniqueAvailable()) {
                     PKCS10CertificationRequestBuilder var3x = new PKCS10CertificationRequestBuilder(var2);
                     ByteArrayOutputStream var4x = new ByteArrayOutputStream();
                     byte[] var5x = ((TLSUniqueProvider)var1).getTLSUnique();
                     var3x.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, new DERPrintableString(Base64.toBase64String(var5x)));
                     var4x.write(ESTService.this.annotateRequest(var3x.build(var3).getEncoded()).getBytes());
                     var4x.flush();
                     ESTRequestBuilder var6x = new ESTRequestBuilder(var2x).withData(var4x.toByteArray());
                     var6x.setHeader("Content-Type", "application/pkcs10");
                     var6x.setHeader("Content-Transfer-Encoding", "base64");
                     var6x.setHeader("Content-Length", Long.toString(var4x.size()));
                     return var6x.build();
                  } else {
                     throw new IOException("Source does not supply TLS unique.");
                  }
               }
            });
            if (var4 != null) {
               var4.applyAuth(var9);
            }

            var6 = var8.doRequest(var9.build());
            var10 = this.handleEnrollResponse(var6);
         } catch (Throwable var14) {
            if (var14 instanceof ESTException) {
               throw (ESTException)var14;
            }

            throw new ESTException(var14.getMessage(), var14);
         } finally {
            if (var6 != null) {
               var6.close();
            }
         }

         return var10;
      }
   }

   public EnrollmentResponse simpleEnrollPoP(boolean var1, PKCS10CertificationRequestBuilder var2, ContentSigner var3, ESTAuth var4) throws IOException {
      return this.enrollPop(var1, var2, var3, var4, false);
   }

   public EnrollmentResponse simpleEnrollPopWithServersideCreation(PKCS10CertificationRequestBuilder var1, ContentSigner var2, ESTAuth var3) throws IOException {
      return this.enrollPop(false, var1, var2, var3, true);
   }

   protected EnrollmentResponse handleEnrollResponse(ESTResponse var1) throws IOException {
      ESTRequest var2 = var1.getOriginalRequest();
      Object var3 = null;
      if (var1.getStatusCode() == 202) {
         String var15 = var1.getHeader("Retry-After");
         if (var15 == null) {
            throw new ESTException("Got Status 202 but not Retry-After header from: " + var2.getURL().toString());
         } else {
            long var18 = -1L;

            try {
               var18 = System.currentTimeMillis() + Long.parseLong(var15) * 1000L;
            } catch (NumberFormatException var10) {
               try {
                  SimpleDateFormat var8 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                  var8.setTimeZone(TimeZone.getTimeZone("GMT"));
                  var18 = var8.parse(var15).getTime();
               } catch (Exception var9) {
                  throw new ESTException(
                     "Unable to parse Retry-After header:" + var2.getURL().toString() + " " + var9.getMessage(),
                     null,
                     var1.getStatusCode(),
                     var1.getInputStream()
                  );
               }
            }

            return new EnrollmentResponse(null, var18, var2, var1.getSource());
         }
      } else if (var1.getStatusCode() == 200 && var1.getHeaderOrEmpty("content-type").contains("multipart/mixed")) {
         Headers var14 = new Headers(var1.getHeaderOrEmpty("content-type"), "base64");
         BasicMimeParser var17 = new BasicMimeParser(var14, var1.getInputStream());
         final Object[] var6 = new Object[2];
         var17.parse(new MimeParserListener() {
            @Override
            public MimeContext createContext(MimeParserContext var1, Headers var2x) {
               return ConstantMimeContext.Instance;
            }

            @Override
            public void object(MimeParserContext var1, Headers var2x, InputStream var3x) throws IOException {
               if (var2x.getContentType().contains("application/pkcs8")) {
                  ASN1InputStream var4 = new ASN1InputStream(var3x);
                  var6[0] = PrivateKeyInfo.getInstance(var4.readObject());
                  if (var4.readObject() != null) {
                     throw new ESTException("Unexpected ASN1 object after private key info");
                  }
               } else if (var2x.getContentType().contains("application/pkcs7-mime")) {
                  ASN1InputStream var7 = new ASN1InputStream(var3x);

                  try {
                     var6[1] = new SimplePKIResponse(ContentInfo.getInstance(var7.readObject()));
                  } catch (CMCException var6x) {
                     throw new IOException(var6x.getMessage());
                  }

                  if (var7.readObject() != null) {
                     throw new ESTException("Unexpected ASN1 object after reading certificates");
                  }
               }
            }
         });
         if (var6[0] != null && var6[1] != null) {
            var3 = ((SimplePKIResponse)var6[1]).getCertificates();
            return new EnrollmentResponse((Store<X509CertificateHolder>)var3, -1L, null, var1.getSource(), PrivateKeyInfo.getInstance(var6[0]));
         } else {
            throw new ESTException("received neither private key info and certificates");
         }
      } else if (var1.getStatusCode() == 200) {
         ASN1InputStream var4 = new ASN1InputStream(var1.getInputStream());
         SimplePKIResponse var5 = null;

         try {
            var5 = new SimplePKIResponse(ContentInfo.getInstance(var4.readObject()));
         } catch (CMCException var11) {
            throw new ESTException(var11.getMessage(), var11.getCause());
         }

         var3 = var5.getCertificates();
         return new EnrollmentResponse((Store<X509CertificateHolder>)var3, -1L, null, var1.getSource());
      } else {
         throw new ESTException("Simple Enroll: " + var2.getURL().toString(), null, var1.getStatusCode(), var1.getInputStream());
      }
   }

   public CSRRequestResponse getCSRAttributes() throws ESTException {
      if (!this.clientProvider.isTrusted()) {
         throw new IllegalStateException("No trust anchors.");
      } else {
         ESTResponse var1 = null;
         Object var2 = null;
         Object var3 = null;
         Object var4 = null;

         try {
            var4 = new URL(this.server + "/csrattrs");
            ESTClient var5 = this.clientProvider.makeClient();
            ESTRequest var6 = new ESTRequestBuilder("GET", (URL)var4).withClient(var5).build();
            var1 = var5.doRequest(var6);
            switch (var1.getStatusCode()) {
               case 200:
                  try {
                     ASN1InputStream var7 = this.getASN1InputStream(var1.getInputStream(), var1.getContentLength());
                     ASN1Sequence var8 = ASN1Sequence.getInstance(var7.readObject());
                     var2 = new CSRAttributesResponse(CsrAttrs.getInstance(var8));
                     break;
                  } catch (Throwable var17) {
                     throw new ESTException(
                        "Decoding CACerts: " + var4.toString() + " " + var17.getMessage(), var17, var1.getStatusCode(), var1.getInputStream()
                     );
                  }
               case 204:
                  var2 = null;
                  break;
               case 404:
                  var2 = null;
                  break;
               default:
                  throw new ESTException("CSR Attribute request: " + var6.getURL().toString(), null, var1.getStatusCode(), var1.getInputStream());
            }
         } catch (Throwable var18) {
            if (var18 instanceof ESTException) {
               throw (ESTException)var18;
            }

            throw new ESTException(var18.getMessage(), var18);
         } finally {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Exception var16) {
               }
            }
         }

         if (var3 != null) {
            if (var3 instanceof ESTException) {
               throw (ESTException)var3;
            } else {
               throw new ESTException(var3.getMessage(), (Throwable)var3, var1.getStatusCode(), null);
            }
         } else {
            return new CSRRequestResponse((CSRAttributesResponse)var2, var1.getSource());
         }
      }
   }

   private String annotateRequest(byte[] var1) {
      int var2 = 0;
      StringWriter var3 = new StringWriter();
      PrintWriter var4 = new PrintWriter(var3);

      do {
         if (var2 + 48 < var1.length) {
            var4.print(Base64.toBase64String(var1, var2, 48));
            var2 += 48;
         } else {
            var4.print(Base64.toBase64String(var1, var2, var1.length - var2));
            var2 = var1.length;
         }

         var4.print('\n');
      } while (var2 < var1.length);

      var4.flush();
      return var3.toString();
   }

   private String verifyLabel(String var1) {
      while (var1.endsWith("/") && var1.length() > 0) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      while (var1.startsWith("/") && var1.length() > 0) {
         var1 = var1.substring(1);
      }

      if (var1.length() == 0) {
         throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
      } else if (!pathInValid.matcher(var1).matches()) {
         throw new IllegalArgumentException("Server path " + var1 + " contains invalid characters");
      } else if (illegalParts.contains(var1)) {
         throw new IllegalArgumentException("Label " + var1 + " is a reserved path segment.");
      } else {
         return var1;
      }
   }

   private String verifyServer(String var1) {
      try {
         while (var1.endsWith("/") && var1.length() > 0) {
            var1 = var1.substring(0, var1.length() - 1);
         }

         if (var1.contains("://")) {
            throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
         } else {
            URL var2 = new URL("https://" + var1);
            if (var2.getPath().length() != 0 && !var2.getPath().equals("/")) {
               throw new IllegalArgumentException(
                  "Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily."
               );
            } else {
               return var1;
            }
         }
      } catch (Exception var3) {
         if (var3 instanceof IllegalArgumentException) {
            throw (IllegalArgumentException)var3;
         } else {
            throw new IllegalArgumentException("Scheme and host is invalid: " + var3.getMessage(), var3);
         }
      }
   }

   static {
      illegalParts.add("/cacerts".substring(1));
      illegalParts.add("/simpleenroll".substring(1));
      illegalParts.add("/simplereenroll".substring(1));
      illegalParts.add("/fullcmc".substring(1));
      illegalParts.add("/serverkeygen".substring(1));
      illegalParts.add("/csrattrs".substring(1));
   }
}
