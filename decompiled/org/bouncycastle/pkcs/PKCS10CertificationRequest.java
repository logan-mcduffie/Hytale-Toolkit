package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.util.Exceptions;

public class PKCS10CertificationRequest {
   private static Attribute[] EMPTY_ARRAY = new Attribute[0];
   private final CertificationRequest certificationRequest;
   private final boolean isAltRequest;
   private final AlgorithmIdentifier altSignature;
   private final SubjectPublicKeyInfo altPublicKey;
   private final ASN1BitString altSignatureValue;

   private static CertificationRequest parseBytes(byte[] var0) throws IOException {
      try {
         CertificationRequest var1 = CertificationRequest.getInstance(ASN1Primitive.fromByteArray(var0));
         if (var1 == null) {
            throw new PKCSIOException("empty data passed to constructor");
         } else {
            return var1;
         }
      } catch (ClassCastException var2) {
         throw new PKCSIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new PKCSIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   private static ASN1Encodable getSingleValue(Attribute var0) {
      ASN1Encodable[] var1 = var0.getAttributeValues();
      if (var1.length != 1) {
         throw new IllegalArgumentException("single value attribute value not size of 1");
      } else {
         return var1[0];
      }
   }

   public PKCS10CertificationRequest(CertificationRequest var1) {
      if (var1 == null) {
         throw new NullPointerException("certificationRequest cannot be null");
      } else {
         this.certificationRequest = var1;
         ASN1Set var2 = var1.getCertificationRequestInfo().getAttributes();
         AlgorithmIdentifier var3 = null;
         SubjectPublicKeyInfo var4 = null;
         ASN1BitString var5 = null;
         if (var2 != null) {
            Enumeration var6 = var2.getObjects();

            while (var6.hasMoreElements()) {
               Attribute var7 = Attribute.getInstance(var6.nextElement());
               if (Extension.altSignatureAlgorithm.equals(var7.getAttrType())) {
                  var3 = AlgorithmIdentifier.getInstance(getSingleValue(var7));
               }

               if (Extension.subjectAltPublicKeyInfo.equals(var7.getAttrType())) {
                  var4 = SubjectPublicKeyInfo.getInstance(getSingleValue(var7));
               }

               if (Extension.altSignatureValue.equals(var7.getAttrType())) {
                  var5 = ASN1BitString.getInstance(getSingleValue(var7));
               }
            }
         }

         this.isAltRequest = var3 != null | var4 != null | var5 != null;
         if (this.isAltRequest && !(var3 != null & var4 != null & var5 != null)) {
            throw new IllegalArgumentException("invalid alternate public key details found");
         } else {
            this.altSignature = var3;
            this.altPublicKey = var4;
            this.altSignatureValue = var5;
         }
      }
   }

   public PKCS10CertificationRequest(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public CertificationRequest toASN1Structure() {
      return this.certificationRequest;
   }

   public X500Name getSubject() {
      return X500Name.getInstance(this.certificationRequest.getCertificationRequestInfo().getSubject());
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.certificationRequest.getSignatureAlgorithm();
   }

   public byte[] getSignature() {
      return this.certificationRequest.getSignature().getOctets();
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();
   }

   public Attribute[] getAttributes() {
      ASN1Set var1 = this.certificationRequest.getCertificationRequestInfo().getAttributes();
      if (var1 == null) {
         return EMPTY_ARRAY;
      } else {
         Attribute[] var2 = new Attribute[var1.size()];

         for (int var3 = 0; var3 != var1.size(); var3++) {
            var2[var3] = Attribute.getInstance(var1.getObjectAt(var3));
         }

         return var2;
      }
   }

   public Attribute[] getAttributes(ASN1ObjectIdentifier var1) {
      ASN1Set var2 = this.certificationRequest.getCertificationRequestInfo().getAttributes();
      if (var2 == null) {
         return EMPTY_ARRAY;
      } else {
         ArrayList var3 = new ArrayList();

         for (int var4 = 0; var4 != var2.size(); var4++) {
            Attribute var5 = Attribute.getInstance(var2.getObjectAt(var4));
            if (var5.getAttrType().equals(var1)) {
               var3.add(var5);
            }
         }

         return var3.size() == 0 ? EMPTY_ARRAY : var3.toArray(new Attribute[var3.size()]);
      }
   }

   public byte[] getEncoded() throws IOException {
      return this.certificationRequest.getEncoded();
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws PKCSException {
      CertificationRequestInfo var2 = this.certificationRequest.getCertificationRequestInfo();

      ContentVerifier var3;
      try {
         var3 = var1.get(this.certificationRequest.getSignatureAlgorithm());
         OutputStream var4 = var3.getOutputStream();
         var4.write(var2.getEncoded("DER"));
         var4.close();
      } catch (Exception var5) {
         throw new PKCSException("unable to process signature: " + var5.getMessage(), var5);
      }

      return var3.verify(this.getSignature());
   }

   public boolean hasAltPublicKey() {
      return this.isAltRequest;
   }

   public boolean isAltSignatureValid(ContentVerifierProvider var1) throws PKCSException {
      if (!this.isAltRequest) {
         throw new IllegalStateException("no alternate public key present");
      } else {
         CertificationRequestInfo var2 = this.certificationRequest.getCertificationRequestInfo();
         ASN1Set var3 = var2.getAttributes();
         ASN1EncodableVector var4 = new ASN1EncodableVector();
         Enumeration var5 = var3.getObjects();

         while (var5.hasMoreElements()) {
            Attribute var6 = Attribute.getInstance(var5.nextElement());
            if (!Extension.altSignatureValue.equals(var6.getAttrType())) {
               var4.add(var6);
            }
         }

         var2 = new CertificationRequestInfo(var2.getSubject(), var2.getSubjectPublicKeyInfo(), new DERSet(var4));

         try {
            var9 = var1.get(this.altSignature);
            OutputStream var10 = var9.getOutputStream();
            var10.write(var2.getEncoded("DER"));
            var10.close();
         } catch (Exception var7) {
            throw new PKCSException("unable to process signature: " + var7.getMessage(), var7);
         }

         return var9.verify(this.altSignatureValue.getOctets());
      }
   }

   public Extensions getRequestedExtensions() {
      Attribute[] var1 = this.getAttributes();

      for (int var2 = 0; var2 != var1.length; var2++) {
         Attribute var3 = var1[var2];
         if (PKCSObjectIdentifiers.pkcs_9_at_extensionRequest.equals(var3.getAttrType())) {
            ExtensionsGenerator var4 = new ExtensionsGenerator();
            ASN1Set var5 = var3.getAttrValues();
            if (var5 != null && var5.size() != 0) {
               ASN1Sequence var6 = ASN1Sequence.getInstance(var5.getObjectAt(0));

               try {
                  Enumeration var7 = var6.getObjects();

                  while (var7.hasMoreElements()) {
                     ASN1Sequence var8 = ASN1Sequence.getInstance(var7.nextElement());
                     boolean var9 = var8.size() == 3 && ASN1Boolean.getInstance(var8.getObjectAt(1)).isTrue();
                     if (var8.size() == 2) {
                        var4.addExtension(
                           ASN1ObjectIdentifier.getInstance(var8.getObjectAt(0)), false, ASN1OctetString.getInstance(var8.getObjectAt(1)).getOctets()
                        );
                     } else {
                        if (var8.size() != 3) {
                           throw new IllegalStateException("incorrect sequence size of Extension get " + var8.size() + " expected 2 or three");
                        }

                        var4.addExtension(
                           ASN1ObjectIdentifier.getInstance(var8.getObjectAt(0)), var9, ASN1OctetString.getInstance(var8.getObjectAt(2)).getOctets()
                        );
                     }
                  }
               } catch (IllegalArgumentException var10) {
                  throw Exceptions.illegalStateException("asn1 processing issue: " + var10.getMessage(), var10);
               }

               return var4.generate();
            }

            throw new IllegalStateException("pkcs_9_at_extensionRequest present but has no value");
         }
      }

      return null;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PKCS10CertificationRequest)) {
         return false;
      } else {
         PKCS10CertificationRequest var2 = (PKCS10CertificationRequest)var1;
         return this.toASN1Structure().equals(var2.toASN1Structure());
      }
   }

   @Override
   public int hashCode() {
      return this.toASN1Structure().hashCode();
   }
}
