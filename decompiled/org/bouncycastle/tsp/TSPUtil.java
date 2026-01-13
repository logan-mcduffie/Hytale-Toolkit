package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

public class TSPUtil {
   private static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
   private static final Map digestLengths = new HashMap();
   private static final Map digestNames = new HashMap();

   public static Collection getSignatureTimestamps(SignerInformation var0, DigestCalculatorProvider var1) throws TSPValidationException {
      ArrayList var2 = new ArrayList();
      AttributeTable var3 = var0.getUnsignedAttributes();
      if (var3 != null) {
         ASN1EncodableVector var4 = var3.getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);

         for (int var5 = 0; var5 < var4.size(); var5++) {
            Attribute var6 = (Attribute)var4.get(var5);
            ASN1Set var7 = var6.getAttrValues();

            for (int var8 = 0; var8 < var7.size(); var8++) {
               try {
                  ContentInfo var9 = ContentInfo.getInstance(var7.getObjectAt(var8));
                  TimeStampToken var10 = new TimeStampToken(var9);
                  TimeStampTokenInfo var11 = var10.getTimeStampInfo();
                  DigestCalculator var12 = var1.get(var11.getHashAlgorithm());
                  OutputStream var13 = var12.getOutputStream();
                  var13.write(var0.getSignature());
                  var13.close();
                  byte[] var14 = var12.getDigest();
                  if (!Arrays.constantTimeAreEqual(var14, var11.getMessageImprintDigest())) {
                     throw new TSPValidationException("Incorrect digest in message imprint");
                  }

                  var2.add(var10);
               } catch (OperatorCreationException var15) {
                  throw new TSPValidationException("Unknown hash algorithm specified in timestamp");
               } catch (Exception var16) {
                  throw new TSPValidationException("Timestamp could not be parsed");
               }
            }
         }
      }

      return var2;
   }

   public static void validateCertificate(X509CertificateHolder var0) throws TSPValidationException {
      if (var0.toASN1Structure().getVersionNumber() != 3) {
         throw new IllegalArgumentException("Certificate must have an ExtendedKeyUsage extension.");
      } else {
         Extension var1 = var0.getExtension(Extension.extendedKeyUsage);
         if (var1 == null) {
            throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension.");
         } else if (!var1.isCritical()) {
            throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension marked as critical.");
         } else {
            ExtendedKeyUsage var2 = ExtendedKeyUsage.getInstance(var1.getParsedValue());
            if (!var2.hasKeyPurposeId(KeyPurposeId.id_kp_timeStamping) || var2.size() != 1) {
               throw new TSPValidationException("ExtendedKeyUsage not solely time stamping.");
            }
         }
      }
   }

   static int getDigestLength(String var0) throws TSPException {
      Integer var1 = (Integer)digestLengths.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new TSPException("digest algorithm cannot be found.");
      }
   }

   static List getExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_LIST : Collections.unmodifiableList(java.util.Arrays.asList(var0.getExtensionOIDs()));
   }

   static void addExtension(ExtensionsGenerator var0, ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws TSPIOException {
      try {
         var0.addExtension(var1, var2, var3);
      } catch (IOException var5) {
         throw new TSPIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }

   static {
      digestLengths.put(PKCSObjectIdentifiers.md5.getId(), Integers.valueOf(16));
      digestLengths.put(OIWObjectIdentifiers.idSHA1.getId(), Integers.valueOf(20));
      digestLengths.put(NISTObjectIdentifiers.id_sha224.getId(), Integers.valueOf(28));
      digestLengths.put(NISTObjectIdentifiers.id_sha256.getId(), Integers.valueOf(32));
      digestLengths.put(NISTObjectIdentifiers.id_sha384.getId(), Integers.valueOf(48));
      digestLengths.put(NISTObjectIdentifiers.id_sha512.getId(), Integers.valueOf(64));
      digestLengths.put(NISTObjectIdentifiers.id_sha3_224.getId(), Integers.valueOf(28));
      digestLengths.put(NISTObjectIdentifiers.id_sha3_256.getId(), Integers.valueOf(32));
      digestLengths.put(NISTObjectIdentifiers.id_sha3_384.getId(), Integers.valueOf(48));
      digestLengths.put(NISTObjectIdentifiers.id_sha3_512.getId(), Integers.valueOf(64));
      digestLengths.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), Integers.valueOf(16));
      digestLengths.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), Integers.valueOf(20));
      digestLengths.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), Integers.valueOf(32));
      digestLengths.put(CryptoProObjectIdentifiers.gostR3411.getId(), Integers.valueOf(32));
      digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), Integers.valueOf(32));
      digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), Integers.valueOf(64));
      digestLengths.put(GMObjectIdentifiers.sm3.getId(), Integers.valueOf(32));
      digestNames.put(PKCSObjectIdentifiers.md5.getId(), "MD5");
      digestNames.put(OIWObjectIdentifiers.idSHA1.getId(), "SHA1");
      digestNames.put(NISTObjectIdentifiers.id_sha224.getId(), "SHA224");
      digestNames.put(NISTObjectIdentifiers.id_sha256.getId(), "SHA256");
      digestNames.put(NISTObjectIdentifiers.id_sha384.getId(), "SHA384");
      digestNames.put(NISTObjectIdentifiers.id_sha512.getId(), "SHA512");
      digestNames.put(NISTObjectIdentifiers.id_sha3_224.getId(), "SHA3-224");
      digestNames.put(NISTObjectIdentifiers.id_sha3_256.getId(), "SHA3-256");
      digestNames.put(NISTObjectIdentifiers.id_sha3_384.getId(), "SHA3-384");
      digestNames.put(NISTObjectIdentifiers.id_sha3_512.getId(), "SHA3-512");
      digestNames.put(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId(), "SHA1");
      digestNames.put(PKCSObjectIdentifiers.sha224WithRSAEncryption.getId(), "SHA224");
      digestNames.put(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(), "SHA256");
      digestNames.put(PKCSObjectIdentifiers.sha384WithRSAEncryption.getId(), "SHA384");
      digestNames.put(PKCSObjectIdentifiers.sha512WithRSAEncryption.getId(), "SHA512");
      digestNames.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
      digestNames.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
      digestNames.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
      digestNames.put(CryptoProObjectIdentifiers.gostR3411.getId(), "GOST3411");
      digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), "GOST3411-2012-256");
      digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), "GOST3411-2012-512");
      digestNames.put(GMObjectIdentifiers.sm3.getId(), "SM3");
   }
}
