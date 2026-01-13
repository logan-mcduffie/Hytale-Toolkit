package org.bouncycastle.cert.jcajce;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Integers;

public class JcaX509ExtensionUtils extends X509ExtensionUtils {
   public JcaX509ExtensionUtils() throws NoSuchAlgorithmException {
      super(new JcaX509ExtensionUtils.SHA1DigestCalculator(MessageDigest.getInstance("SHA1")));
   }

   public JcaX509ExtensionUtils(DigestCalculator var1) {
      super(var1);
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509Certificate var1) throws CertificateEncodingException {
      return super.createAuthorityKeyIdentifier(new JcaX509CertificateHolder(var1));
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey var1) {
      return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey var1, X500Principal var2, BigInteger var3) {
      return super.createAuthorityKeyIdentifier(
         SubjectPublicKeyInfo.getInstance(var1.getEncoded()), new GeneralNames(new GeneralName(X500Name.getInstance(var2.getEncoded()))), var3
      );
   }

   public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey var1, GeneralNames var2, BigInteger var3) {
      return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(var1.getEncoded()), var2, var3);
   }

   public SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey var1) {
      return super.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
   }

   public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(PublicKey var1) {
      return super.createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
   }

   public static ASN1Primitive parseExtensionValue(byte[] var0) throws IOException {
      return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(var0).getOctets());
   }

   public static Collection getIssuerAlternativeNames(X509Certificate var0) throws CertificateParsingException {
      byte[] var1 = var0.getExtensionValue(Extension.issuerAlternativeName.getId());
      return getAlternativeNames(var1);
   }

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
            Enumeration var2 = DERSequence.getInstance(parseExtensionValue(var0)).getObjects();

            while (var2.hasMoreElements()) {
               GeneralName var3 = GeneralName.getInstance(var2.nextElement());
               ArrayList var4 = new ArrayList();
               var4.add(Integers.valueOf(var3.getTagNo()));
               switch (var3.getTagNo()) {
                  case 0:
                  case 3:
                  case 5:
                     var4.add(var3.getEncoded());
                     break;
                  case 1:
                  case 2:
                  case 6:
                     var4.add(((ASN1String)var3.getName()).getString());
                     break;
                  case 4:
                     var4.add(X500Name.getInstance(RFC4519Style.INSTANCE, var3.getName()).toString());
                     break;
                  case 7:
                     byte[] var5 = DEROctetString.getInstance(var3.getName()).getOctets();

                     String var6;
                     try {
                        var6 = InetAddress.getByAddress(var5).getHostAddress();
                     } catch (UnknownHostException var8) {
                        continue;
                     }

                     var4.add(var6);
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
         } catch (Exception var9) {
            throw new CertificateParsingException(var9.getMessage());
         }
      }
   }

   private static class SHA1DigestCalculator implements DigestCalculator {
      private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      private MessageDigest digest;

      public SHA1DigestCalculator(MessageDigest var1) {
         this.digest = var1;
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
      }

      @Override
      public OutputStream getOutputStream() {
         return this.bOut;
      }

      @Override
      public byte[] getDigest() {
         byte[] var1 = this.digest.digest(this.bOut.toByteArray());
         this.bOut.reset();
         return var1;
      }
   }
}
