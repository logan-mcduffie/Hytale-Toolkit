package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

class DigestUtil {
   static Digest getDigest(LMOtsParameters var0) {
      return createDigest(var0.getDigestOID(), var0.getN());
   }

   static Digest getDigest(LMSigParameters var0) {
      return createDigest(var0.getDigestOID(), var0.getM());
   }

   private static Digest createDigest(ASN1ObjectIdentifier var0, int var1) {
      Digest var2 = createDigest(var0);
      return (Digest)(!NISTObjectIdentifiers.id_shake256_len.equals(var0) && var2.getDigestSize() == var1 ? var2 : new DigestUtil.WrapperDigest(var2, var1));
   }

   private static Digest createDigest(ASN1ObjectIdentifier var0) {
      if (var0.equals(NISTObjectIdentifiers.id_sha256)) {
         return new SHA256Digest();
      } else if (var0.equals(NISTObjectIdentifiers.id_shake256_len)) {
         return new SHAKEDigest(256);
      } else {
         throw new IllegalArgumentException("unrecognized digest OID: " + var0);
      }
   }

   static class WrapperDigest implements Digest {
      private final Digest digest;
      private final int length;

      WrapperDigest(Digest var1, int var2) {
         this.digest = var1;
         this.length = var2;
      }

      @Override
      public String getAlgorithmName() {
         return this.digest.getAlgorithmName() + "/" + this.length * 8;
      }

      @Override
      public int getDigestSize() {
         return this.length;
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
         byte[] var3 = new byte[this.digest.getDigestSize()];
         this.digest.doFinal(var3, 0);
         System.arraycopy(var3, 0, var1, var2, this.length);
         return this.length;
      }

      @Override
      public void reset() {
         this.digest.reset();
      }
   }
}
