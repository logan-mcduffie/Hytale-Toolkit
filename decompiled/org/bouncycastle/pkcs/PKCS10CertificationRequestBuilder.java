package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;

public class PKCS10CertificationRequestBuilder {
   private SubjectPublicKeyInfo publicKeyInfo;
   private X500Name subject;
   private List attributes = new ArrayList();
   private boolean leaveOffEmpty = false;

   public PKCS10CertificationRequestBuilder(PKCS10CertificationRequestBuilder var1) {
      this.publicKeyInfo = var1.publicKeyInfo;
      this.subject = var1.subject;
      this.leaveOffEmpty = var1.leaveOffEmpty;
      this.attributes = new ArrayList(var1.attributes);
   }

   public PKCS10CertificationRequestBuilder(X500Name var1, SubjectPublicKeyInfo var2) {
      this.subject = var1;
      this.publicKeyInfo = var2;
   }

   public PKCS10CertificationRequestBuilder setAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      Iterator var3 = this.attributes.iterator();

      while (var3.hasNext()) {
         if (((Attribute)var3.next()).getAttrType().equals(var1)) {
            throw new IllegalStateException("Attribute " + var1.toString() + " is already set");
         }
      }

      this.addAttribute(var1, var2);
      return this;
   }

   public PKCS10CertificationRequestBuilder setAttribute(ASN1ObjectIdentifier var1, ASN1Encodable[] var2) {
      Iterator var3 = this.attributes.iterator();

      while (var3.hasNext()) {
         if (((Attribute)var3.next()).getAttrType().equals(var1)) {
            throw new IllegalStateException("Attribute " + var1.toString() + " is already set");
         }
      }

      this.addAttribute(var1, var2);
      return this;
   }

   public PKCS10CertificationRequestBuilder addAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.attributes.add(new Attribute(var1, new DERSet(var2)));
      return this;
   }

   public PKCS10CertificationRequestBuilder addAttribute(ASN1ObjectIdentifier var1, ASN1Encodable[] var2) {
      this.attributes.add(new Attribute(var1, new DERSet(var2)));
      return this;
   }

   public PKCS10CertificationRequestBuilder setLeaveOffEmptyAttributes(boolean var1) {
      this.leaveOffEmpty = var1;
      return this;
   }

   public PKCS10CertificationRequest build(ContentSigner var1) {
      CertificationRequestInfo var2;
      if (this.attributes.isEmpty()) {
         if (this.leaveOffEmpty) {
            var2 = new CertificationRequestInfo(this.subject, this.publicKeyInfo, null);
         } else {
            var2 = new CertificationRequestInfo(this.subject, this.publicKeyInfo, new DERSet());
         }
      } else {
         ASN1EncodableVector var3 = new ASN1EncodableVector();
         Iterator var4 = this.attributes.iterator();

         while (var4.hasNext()) {
            var3.add(Attribute.getInstance(var4.next()));
         }

         var2 = new CertificationRequestInfo(this.subject, this.publicKeyInfo, new DERSet(var3));
      }

      try {
         OutputStream var6 = var1.getOutputStream();
         var6.write(var2.getEncoded("DER"));
         var6.close();
         return new PKCS10CertificationRequest(new CertificationRequest(var2, var1.getAlgorithmIdentifier(), new DERBitString(var1.getSignature())));
      } catch (IOException var5) {
         throw new IllegalStateException("cannot produce certification request signature");
      }
   }

   public PKCS10CertificationRequest build(ContentSigner var1, SubjectPublicKeyInfo var2, ContentSigner var3) {
      ASN1EncodableVector var5 = new ASN1EncodableVector();
      Iterator var6 = this.attributes.iterator();

      while (var6.hasNext()) {
         var5.add(Attribute.getInstance(var6.next()));
      }

      var5.add(new Attribute(Extension.subjectAltPublicKeyInfo, new DERSet(var2)));
      var5.add(new Attribute(Extension.altSignatureAlgorithm, new DERSet(var3.getAlgorithmIdentifier())));
      CertificationRequestInfo var4 = new CertificationRequestInfo(this.subject, this.publicKeyInfo, new DERSet(var5));

      try {
         OutputStream var10 = var3.getOutputStream();
         var10.write(var4.getEncoded("DER"));
         var10.close();
         var5.add(new Attribute(Extension.altSignatureValue, new DERSet(new DERBitString(var3.getSignature()))));
         var4 = new CertificationRequestInfo(this.subject, this.publicKeyInfo, new DERSet(var5));
      } catch (IOException var8) {
         throw new IllegalStateException("cannot produce certification request signature");
      }

      try {
         OutputStream var11 = var1.getOutputStream();
         var11.write(var4.getEncoded("DER"));
         var11.close();
         return new PKCS10CertificationRequest(new CertificationRequest(var4, var1.getAlgorithmIdentifier(), new DERBitString(var1.getSignature())));
      } catch (IOException var7) {
         throw new IllegalStateException("cannot produce certification request signature");
      }
   }
}
