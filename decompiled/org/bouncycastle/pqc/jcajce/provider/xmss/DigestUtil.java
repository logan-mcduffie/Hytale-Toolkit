package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

class DigestUtil {
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

   static ASN1ObjectIdentifier getDigestOID(String var0) {
      if (var0.equals("SHA-256")) {
         return NISTObjectIdentifiers.id_sha256;
      } else if (var0.equals("SHA-512")) {
         return NISTObjectIdentifiers.id_sha512;
      } else if (var0.equals("SHAKE128")) {
         return NISTObjectIdentifiers.id_shake128;
      } else if (var0.equals("SHAKE256")) {
         return NISTObjectIdentifiers.id_shake256;
      } else {
         throw new IllegalArgumentException("unrecognized digest: " + var0);
      }
   }

   public static byte[] getDigestResult(Digest var0) {
      byte[] var1 = new byte[var0.getDigestSize()];
      var0.doFinal(var1, 0);
      return var1;
   }

   public static String getXMSSDigestName(ASN1ObjectIdentifier var0) {
      if (var0.equals(NISTObjectIdentifiers.id_sha256)) {
         return "SHA256";
      } else if (var0.equals(NISTObjectIdentifiers.id_sha512)) {
         return "SHA512";
      } else if (var0.equals(NISTObjectIdentifiers.id_shake128)) {
         return "SHAKE128";
      } else if (var0.equals(NISTObjectIdentifiers.id_shake256)) {
         return "SHAKE256";
      } else {
         throw new IllegalArgumentException("unrecognized digest OID: " + var0);
      }
   }

   static class DoubleDigest implements Digest {
      private SHAKEDigest digest;

      DoubleDigest(SHAKEDigest var1) {
         this.digest = var1;
      }

      @Override
      public String getAlgorithmName() {
         return this.digest.getAlgorithmName() + "/" + this.digest.getDigestSize() * 2 * 8;
      }

      @Override
      public int getDigestSize() {
         return this.digest.getDigestSize() * 2;
      }

      @Override
      public void update(byte var1) {
         this.digest.update(var1);
      }

      @Override
      public void update(byte[] var1, int var2, int var3) {
         this.digest.update(var1, var2, var3);
      }

      @Override
      public int doFinal(byte[] var1, int var2) {
         return this.digest.doFinal(var1, var2, this.getDigestSize());
      }

      @Override
      public void reset() {
         this.digest.reset();
      }
   }
}
