package org.bouncycastle.openssl;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;

public class PEMParser extends PemReader {
   public static final String TYPE_CERTIFICATE_REQUEST = "CERTIFICATE REQUEST";
   public static final String TYPE_NEW_CERTIFICATE_REQUEST = "NEW CERTIFICATE REQUEST";
   public static final String TYPE_CERTIFICATE = "CERTIFICATE";
   public static final String TYPE_TRUSTED_CERTIFICATE = "TRUSTED CERTIFICATE";
   public static final String TYPE_X509_CERTIFICATE = "X509 CERTIFICATE";
   public static final String TYPE_X509_CRL = "X509 CRL";
   public static final String TYPE_PKCS7 = "PKCS7";
   public static final String TYPE_CMS = "CMS";
   public static final String TYPE_ATTRIBUTE_CERTIFICATE = "ATTRIBUTE CERTIFICATE";
   public static final String TYPE_EC_PARAMETERS = "EC PARAMETERS";
   public static final String TYPE_PUBLIC_KEY = "PUBLIC KEY";
   public static final String TYPE_RSA_PUBLIC_KEY = "RSA PUBLIC KEY";
   public static final String TYPE_RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
   public static final String TYPE_DSA_PRIVATE_KEY = "DSA PRIVATE KEY";
   public static final String TYPE_EC_PRIVATE_KEY = "EC PRIVATE KEY";
   public static final String TYPE_ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
   public static final String TYPE_PRIVATE_KEY = "PRIVATE KEY";
   protected final Map parsers = new HashMap();

   public PEMParser(Reader var1) {
      super(var1);
      this.parsers.put("CERTIFICATE REQUEST", new PEMParser.PKCS10CertificationRequestParser());
      this.parsers.put("NEW CERTIFICATE REQUEST", new PEMParser.PKCS10CertificationRequestParser());
      this.parsers.put("CERTIFICATE", new PEMParser.X509CertificateParser());
      this.parsers.put("TRUSTED CERTIFICATE", new PEMParser.X509TrustedCertificateParser());
      this.parsers.put("X509 CERTIFICATE", new PEMParser.X509CertificateParser());
      this.parsers.put("X509 CRL", new PEMParser.X509CRLParser());
      this.parsers.put("PKCS7", new PEMParser.PKCS7Parser());
      this.parsers.put("CMS", new PEMParser.PKCS7Parser());
      this.parsers.put("ATTRIBUTE CERTIFICATE", new PEMParser.X509AttributeCertificateParser());
      this.parsers.put("EC PARAMETERS", new PEMParser.ECCurveParamsParser());
      this.parsers.put("PUBLIC KEY", new PEMParser.PublicKeyParser());
      this.parsers.put("RSA PUBLIC KEY", new PEMParser.RSAPublicKeyParser());
      this.parsers.put("RSA PRIVATE KEY", new PEMParser.KeyPairParser(new PEMParser.RSAKeyPairParser()));
      this.parsers.put("DSA PRIVATE KEY", new PEMParser.KeyPairParser(new PEMParser.DSAKeyPairParser()));
      this.parsers.put("EC PRIVATE KEY", new PEMParser.KeyPairParser(new PEMParser.ECDSAKeyPairParser()));
      this.parsers.put("ENCRYPTED PRIVATE KEY", new PEMParser.EncryptedPrivateKeyParser());
      this.parsers.put("PRIVATE KEY", new PEMParser.PrivateKeyParser());
   }

   public Object readObject() throws IOException {
      PemObject var1 = this.readPemObject();
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.getType();
         Object var3 = this.parsers.get(var2);
         if (var3 == null) {
            throw new IOException("unrecognised object: " + var2);
         } else {
            return ((PemObjectParser)var3).parseObject(var1);
         }
      }
   }

   public Set<String> getSupportedTypes() {
      return Collections.unmodifiableSet(this.parsers.keySet());
   }

   private static class DSAKeyPairParser implements PEMKeyPairParser {
      private DSAKeyPairParser() {
      }

      @Override
      public PEMKeyPair parse(byte[] var1) throws IOException {
         try {
            ASN1Sequence var2 = ASN1Sequence.getInstance(var1);
            if (var2.size() != 6) {
               throw new PEMException("malformed sequence in DSA private key");
            } else {
               ASN1Integer var3 = ASN1Integer.getInstance(var2.getObjectAt(1));
               ASN1Integer var4 = ASN1Integer.getInstance(var2.getObjectAt(2));
               ASN1Integer var5 = ASN1Integer.getInstance(var2.getObjectAt(3));
               ASN1Integer var6 = ASN1Integer.getInstance(var2.getObjectAt(4));
               ASN1Integer var7 = ASN1Integer.getInstance(var2.getObjectAt(5));
               return new PEMKeyPair(
                  new SubjectPublicKeyInfo(
                     new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(var3.getValue(), var4.getValue(), var5.getValue())), var6
                  ),
                  new PrivateKeyInfo(
                     new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(var3.getValue(), var4.getValue(), var5.getValue())), var7
                  )
               );
            }
         } catch (IOException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new PEMException("problem creating DSA private key: " + var9.toString(), var9);
         }
      }
   }

   private static class ECCurveParamsParser implements PemObjectParser {
      private ECCurveParamsParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            ASN1Primitive var2 = ASN1Primitive.fromByteArray(var1.getContent());
            if (var2 instanceof ASN1ObjectIdentifier) {
               return var2;
            } else {
               return var2 instanceof ASN1Sequence ? X9ECParameters.getInstance(var2) : null;
            }
         } catch (IOException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new PEMException("exception extracting EC named curve: " + var4.toString());
         }
      }
   }

   private static class ECDSAKeyPairParser implements PEMKeyPairParser {
      private ECDSAKeyPairParser() {
      }

      @Override
      public PEMKeyPair parse(byte[] var1) throws IOException {
         try {
            ASN1Sequence var2 = ASN1Sequence.getInstance(var1);
            ECPrivateKey var3 = ECPrivateKey.getInstance(var2);
            AlgorithmIdentifier var4 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var3.getParametersObject());
            PrivateKeyInfo var5 = new PrivateKeyInfo(var4, var3);
            ASN1BitString var6 = var3.getPublicKey();
            SubjectPublicKeyInfo var7 = null;
            if (var6 != null) {
               var7 = new SubjectPublicKeyInfo(var4, var6.getBytes());
            }

            return new PEMKeyPair(var7, var5);
         } catch (IOException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new PEMException("problem creating EC private key: " + var9.toString(), var9);
         }
      }
   }

   private static class EncryptedPrivateKeyParser implements PemObjectParser {
      public EncryptedPrivateKeyParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(var1.getContent()));
         } catch (Exception var3) {
            throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + var3.toString(), var3);
         }
      }
   }

   private static class KeyPairParser implements PemObjectParser {
      private final PEMKeyPairParser pemKeyPairParser;

      public KeyPairParser(PEMKeyPairParser var1) {
         this.pemKeyPairParser = var1;
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         boolean var2 = false;
         String var3 = null;

         for (PemHeader var6 : var1.getHeaders()) {
            if (var6.getName().equals("Proc-Type") && var6.getValue().equals("4,ENCRYPTED")) {
               var2 = true;
            } else if (var6.getName().equals("DEK-Info")) {
               var3 = var6.getValue();
            }
         }

         byte[] var11 = var1.getContent();

         try {
            if (var2) {
               StringTokenizer var12 = new StringTokenizer(var3, ",");
               String var7 = var12.nextToken();
               byte[] var8 = Hex.decode(var12.nextToken());
               return new PEMEncryptedKeyPair(var7, var8, var11, this.pemKeyPairParser);
            } else {
               return this.pemKeyPairParser.parse(var11);
            }
         } catch (IOException var9) {
            if (var2) {
               throw new PEMException("exception decoding - please check password and data.", var9);
            } else {
               throw new PEMException(var9.getMessage(), var9);
            }
         } catch (IllegalArgumentException var10) {
            if (var2) {
               throw new PEMException("exception decoding - please check password and data.", var10);
            } else {
               throw new PEMException(var10.getMessage(), var10);
            }
         }
      }
   }

   private static class PKCS10CertificationRequestParser implements PemObjectParser {
      private PKCS10CertificationRequestParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return new PKCS10CertificationRequest(var1.getContent());
         } catch (Exception var3) {
            throw new PEMException("problem parsing certrequest: " + var3.toString(), var3);
         }
      }
   }

   private static class PKCS7Parser implements PemObjectParser {
      private PKCS7Parser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return ContentInfo.getInstance(var1.getContent());
         } catch (Exception var3) {
            throw new PEMException("problem parsing PKCS7 object: " + var3.toString(), var3);
         }
      }
   }

   private static class PrivateKeyParser implements PemObjectParser {
      public PrivateKeyParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return PrivateKeyInfo.getInstance(var1.getContent());
         } catch (Exception var3) {
            throw new PEMException("problem parsing PRIVATE KEY: " + var3.toString(), var3);
         }
      }
   }

   private static class PublicKeyParser implements PemObjectParser {
      public PublicKeyParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         return SubjectPublicKeyInfo.getInstance(var1.getContent());
      }
   }

   private static class RSAKeyPairParser implements PEMKeyPairParser {
      private RSAKeyPairParser() {
      }

      @Override
      public PEMKeyPair parse(byte[] var1) throws IOException {
         try {
            ASN1Sequence var2 = ASN1Sequence.getInstance(var1);
            if (var2.size() != 9) {
               throw new PEMException("malformed sequence in RSA private key");
            } else {
               RSAPrivateKey var3 = RSAPrivateKey.getInstance(var2);
               RSAPublicKey var4 = new RSAPublicKey(var3.getModulus(), var3.getPublicExponent());
               AlgorithmIdentifier var5 = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
               return new PEMKeyPair(new SubjectPublicKeyInfo(var5, var4), new PrivateKeyInfo(var5, var3));
            }
         } catch (IOException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new PEMException("problem creating RSA private key: " + var7.toString(), var7);
         }
      }
   }

   private static class RSAPublicKeyParser implements PemObjectParser {
      public RSAPublicKeyParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            AlgorithmIdentifier var2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
            RSAPublicKey var3 = RSAPublicKey.getInstance(var1.getContent());
            return new SubjectPublicKeyInfo(var2, var3);
         } catch (IOException var4) {
            throw var4;
         } catch (Exception var5) {
            throw new PEMException("problem extracting key: " + var5.toString(), var5);
         }
      }
   }

   private static class X509AttributeCertificateParser implements PemObjectParser {
      private X509AttributeCertificateParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         return new X509AttributeCertificateHolder(var1.getContent());
      }
   }

   private static class X509CRLParser implements PemObjectParser {
      private X509CRLParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return new X509CRLHolder(var1.getContent());
         } catch (Exception var3) {
            throw new PEMException("problem parsing cert: " + var3.toString(), var3);
         }
      }
   }

   private static class X509CertificateParser implements PemObjectParser {
      private X509CertificateParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return new X509CertificateHolder(var1.getContent());
         } catch (Exception var3) {
            throw new PEMException("problem parsing cert: " + var3.toString(), var3);
         }
      }
   }

   private static class X509TrustedCertificateParser implements PemObjectParser {
      private X509TrustedCertificateParser() {
      }

      @Override
      public Object parseObject(PemObject var1) throws IOException {
         try {
            return new X509TrustedCertificateBlock(var1.getContent());
         } catch (Exception var3) {
            throw new PEMException("problem parsing cert: " + var3.toString(), var3);
         }
      }
   }
}
