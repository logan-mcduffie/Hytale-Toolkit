package org.bouncycastle.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.operator.ContentVerifierProvider;

public class DeltaCertAttributeUtils {
   public static boolean isDeltaRequestSignatureValid(PKCS10CertificationRequest var0, ContentVerifierProvider var1) throws PKCSException {
      Attribute[] var2 = var0.getAttributes(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.2"));
      DeltaCertificateRequestAttributeValue var3 = new DeltaCertificateRequestAttributeValue(var2[0]);
      var2 = var0.getAttributes(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.3"));
      CertificationRequest var4 = var0.toASN1Structure();
      CertificationRequestInfo var5 = var4.getCertificationRequestInfo();
      ASN1EncodableVector var6 = new ASN1EncodableVector();
      var6.add(var5.getVersion());
      var6.add(var5.getSubject());
      var6.add(var5.getSubjectPublicKeyInfo());
      ASN1EncodableVector var7 = new ASN1EncodableVector();
      Enumeration var8 = var5.getAttributes().getObjects();

      while (var8.hasMoreElements()) {
         Attribute var9 = Attribute.getInstance(var8.nextElement());
         if (!var9.getAttrType().equals(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.3"))) {
            var7.add(var9);
         }
      }

      var6.add(new DERTaggedObject(false, 0, new DERSet(var7)));
      ASN1EncodableVector var11 = new ASN1EncodableVector();
      var11.add(new DERSequence(var6));
      var11.add(var3.getSignatureAlgorithm());
      var11.add(var2[0].getAttributeValues()[0]);
      PKCS10CertificationRequest var12 = new PKCS10CertificationRequest(CertificationRequest.getInstance(new DERSequence(var11)));
      return var12.isSignatureValid(var1);
   }
}
