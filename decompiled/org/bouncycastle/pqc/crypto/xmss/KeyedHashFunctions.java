package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;

final class KeyedHashFunctions {
   private final Digest digest;
   private final int digestSize;

   protected KeyedHashFunctions(ASN1ObjectIdentifier var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("digest == null");
      } else {
         this.digest = DigestUtil.getDigest(var1);
         this.digestSize = var2;
      }
   }

   private byte[] coreDigest(int var1, byte[] var2, byte[] var3) {
      byte[] var4 = XMSSUtil.toBytesBigEndian(var1, this.digestSize);
      this.digest.update(var4, 0, var4.length);
      this.digest.update(var2, 0, var2.length);
      this.digest.update(var3, 0, var3.length);
      byte[] var5 = new byte[this.digestSize];
      if (this.digest instanceof Xof) {
         ((Xof)this.digest).doFinal(var5, 0, this.digestSize);
      } else {
         this.digest.doFinal(var5, 0);
      }

      return var5;
   }

   protected byte[] F(byte[] var1, byte[] var2) {
      if (var1.length != this.digestSize) {
         throw new IllegalArgumentException("wrong key length");
      } else if (var2.length != this.digestSize) {
         throw new IllegalArgumentException("wrong in length");
      } else {
         return this.coreDigest(0, var1, var2);
      }
   }

   protected byte[] H(byte[] var1, byte[] var2) {
      if (var1.length != this.digestSize) {
         throw new IllegalArgumentException("wrong key length");
      } else if (var2.length != 2 * this.digestSize) {
         throw new IllegalArgumentException("wrong in length");
      } else {
         return this.coreDigest(1, var1, var2);
      }
   }

   protected byte[] HMsg(byte[] var1, byte[] var2) {
      if (var1.length != 3 * this.digestSize) {
         throw new IllegalArgumentException("wrong key length");
      } else {
         return this.coreDigest(2, var1, var2);
      }
   }

   protected byte[] PRF(byte[] var1, byte[] var2) {
      if (var1.length != this.digestSize) {
         throw new IllegalArgumentException("wrong key length");
      } else if (var2.length != 32) {
         throw new IllegalArgumentException("wrong address length");
      } else {
         return this.coreDigest(3, var1, var2);
      }
   }
}
