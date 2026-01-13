package org.bouncycastle.pqc.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KEMKDFSpec;
import org.bouncycastle.util.Arrays;

public class KdfUtil {
   public static byte[] makeKeyBytes(KEMKDFSpec var0, byte[] var1) {
      byte[] var2;
      try {
         if (var0 == null) {
            var2 = new byte[var1.length];
            System.arraycopy(var1, 0, var2, 0, var2.length);
         } else {
            var2 = makeKeyBytes(var0.getKdfAlgorithm(), var1, var0.getOtherInfo(), var0.getKeySize());
         }
      } finally {
         Arrays.clear(var1);
      }

      return var2;
   }

   static byte[] makeKeyBytes(AlgorithmIdentifier var0, byte[] var1, byte[] var2, int var3) {
      byte[] var4 = new byte[(var3 + 7) / 8];
      if (var0 == null) {
         System.arraycopy(var1, 0, var4, 0, var4.length);
      } else if (X9ObjectIdentifiers.id_kdf_kdf2.equals(var0.getAlgorithm())) {
         AlgorithmIdentifier var5 = AlgorithmIdentifier.getInstance(var0.getParameters());
         KDF2BytesGenerator var6 = new KDF2BytesGenerator(getDigest(var5.getAlgorithm()));
         var6.init(new KDFParameters(var1, var2));
         var6.generateBytes(var4, 0, var4.length);
      } else if (X9ObjectIdentifiers.id_kdf_kdf3.equals(var0.getAlgorithm())) {
         AlgorithmIdentifier var7 = AlgorithmIdentifier.getInstance(var0.getParameters());
         ConcatenationKDFGenerator var14 = new ConcatenationKDFGenerator(getDigest(var7.getAlgorithm()));
         var14.init(new KDFParameters(var1, var2));
         var14.generateBytes(var4, 0, var4.length);
      } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha256.equals(var0.getAlgorithm())) {
         if (var0.getParameters() != null) {
            throw new IllegalStateException("HDKF parameter support not added");
         }

         HKDFBytesGenerator var8 = new HKDFBytesGenerator(new SHA256Digest());
         var8.init(new HKDFParameters(var1, null, var2));
         var8.generateBytes(var4, 0, var4.length);
      } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha384.equals(var0.getAlgorithm())) {
         if (var0.getParameters() != null) {
            throw new IllegalStateException("HDKF parameter support not added");
         }

         HKDFBytesGenerator var9 = new HKDFBytesGenerator(new SHA384Digest());
         var9.init(new HKDFParameters(var1, null, var2));
         var9.generateBytes(var4, 0, var4.length);
      } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha512.equals(var0.getAlgorithm())) {
         if (var0.getParameters() != null) {
            throw new IllegalStateException("HDKF parameter support not added");
         }

         HKDFBytesGenerator var10 = new HKDFBytesGenerator(new SHA512Digest());
         var10.init(new HKDFParameters(var1, null, var2));
         var10.generateBytes(var4, 0, var4.length);
      } else if (NISTObjectIdentifiers.id_Kmac128.equals(var0.getAlgorithm())) {
         byte[] var11 = new byte[0];
         if (var0.getParameters() != null) {
            var11 = ASN1OctetString.getInstance(var0.getParameters()).getOctets();
         }

         KMAC var15 = new KMAC(128, var11);
         var15.init(new KeyParameter(var1, 0, var1.length));
         var15.update(var2, 0, var2.length);
         var15.doFinal(var4, 0, var4.length);
      } else if (NISTObjectIdentifiers.id_Kmac256.equals(var0.getAlgorithm())) {
         byte[] var12 = new byte[0];
         if (var0.getParameters() != null) {
            var12 = ASN1OctetString.getInstance(var0.getParameters()).getOctets();
         }

         KMAC var16 = new KMAC(256, var12);
         var16.init(new KeyParameter(var1, 0, var1.length));
         var16.update(var2, 0, var2.length);
         var16.doFinal(var4, 0, var4.length);
      } else {
         if (!NISTObjectIdentifiers.id_shake256.equals(var0.getAlgorithm())) {
            throw new IllegalArgumentException("Unrecognized KDF: " + var0.getAlgorithm());
         }

         SHAKEDigest var13 = new SHAKEDigest(256);
         var13.update(var1, 0, var1.length);
         var13.update(var2, 0, var2.length);
         var13.doFinal(var4, 0, var4.length);
      }

      return var4;
   }

   static Digest getDigest(ASN1ObjectIdentifier var0) {
      if (var0.equals(NISTObjectIdentifiers.id_sha256)) {
         return new SHA256Digest();
      } else if (var0.equals(NISTObjectIdentifiers.id_sha512)) {
         return new SHA512Digest();
      } else if (var0.equals(NISTObjectIdentifiers.id_shake128)) {
         return new SHAKEDigest(128);
      } else if (var0.equals(NISTObjectIdentifiers.id_shake256)) {
         return new SHAKEDigest(256);
      } else {
         throw new IllegalArgumentException("unrecognized digest OID: " + var0);
      }
   }
}
