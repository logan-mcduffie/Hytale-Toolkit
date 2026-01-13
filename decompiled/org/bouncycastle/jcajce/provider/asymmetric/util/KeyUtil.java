package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class KeyUtil {
   public static byte[] getEncodedSubjectPublicKeyInfo(AlgorithmIdentifier var0, ASN1Encodable var1) {
      try {
         return getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(var0, var1));
      } catch (Exception var3) {
         return null;
      }
   }

   public static byte[] getEncodedSubjectPublicKeyInfo(AlgorithmIdentifier var0, byte[] var1) {
      try {
         return getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(var0, var1));
      } catch (Exception var3) {
         return null;
      }
   }

   public static byte[] getEncodedSubjectPublicKeyInfo(SubjectPublicKeyInfo var0) {
      try {
         return var0.getEncoded("DER");
      } catch (Exception var2) {
         return null;
      }
   }

   public static byte[] getEncodedPrivateKeyInfo(AlgorithmIdentifier var0, ASN1Encodable var1) {
      try {
         PrivateKeyInfo var2 = new PrivateKeyInfo(var0, var1.toASN1Primitive());
         return getEncodedPrivateKeyInfo(var2);
      } catch (Exception var3) {
         return null;
      }
   }

   public static byte[] getEncodedPrivateKeyInfo(PrivateKeyInfo var0) {
      try {
         return var0.getEncoded("DER");
      } catch (Exception var2) {
         return null;
      }
   }
}
