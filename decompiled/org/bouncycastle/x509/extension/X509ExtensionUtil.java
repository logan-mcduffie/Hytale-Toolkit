package org.bouncycastle.x509.extension;

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.util.Integers;

/** @deprecated */
public class X509ExtensionUtil {
   /** @deprecated */
   public static ASN1Primitive fromExtensionValue(byte[] var0) throws IOException {
      ASN1OctetString var1 = (ASN1OctetString)ASN1Primitive.fromByteArray(var0);
      return ASN1Primitive.fromByteArray(var1.getOctets());
   }

   /** @deprecated */
   public static Collection getIssuerAlternativeNames(X509Certificate var0) throws CertificateParsingException {
      byte[] var1 = var0.getExtensionValue(Extension.issuerAlternativeName.getId());
      return getAlternativeNames(var1);
   }

   /** @deprecated */
   public static Collection getSubjectAlternativeNames(X509Certificate var0) throws CertificateParsingException {
      byte[] var1 = var0.getExtensionValue(Extension.subjectAlternativeName.getId());
      return getAlternativeNames(var1);
   }

   private static Collection getAlternativeNames(byte[] var0) throws CertificateParsingException {
      if (var0 == null) {
         return Collections.EMPTY_LIST;
      } else {
         try {
            ArrayList var1 = new ArrayList();
            Enumeration var2 = DERSequence.getInstance(fromExtensionValue(var0)).getObjects();

            while (var2.hasMoreElements()) {
               GeneralName var3 = GeneralName.getInstance(var2.nextElement());
               ArrayList var4 = new ArrayList();
               var4.add(Integers.valueOf(var3.getTagNo()));
               switch (var3.getTagNo()) {
                  case 0:
                  case 3:
                  case 5:
                     var4.add(var3.getName().toASN1Primitive());
                     break;
                  case 1:
                  case 2:
                  case 6:
                     var4.add(((ASN1String)var3.getName()).getString());
                     break;
                  case 4:
                     var4.add(X500Name.getInstance(var3.getName()).toString());
                     break;
                  case 7:
                     var4.add(DEROctetString.getInstance(var3.getName()).getOctets());
                     break;
                  case 8:
                     var4.add(ASN1ObjectIdentifier.getInstance(var3.getName()).getId());
                     break;
                  default:
                     throw new IOException("Bad tag number: " + var3.getTagNo());
               }

               var1.add(var4);
            }

            return Collections.unmodifiableCollection(var1);
         } catch (Exception var5) {
            throw new CertificateParsingException(var5.getMessage());
         }
      }
   }
}
