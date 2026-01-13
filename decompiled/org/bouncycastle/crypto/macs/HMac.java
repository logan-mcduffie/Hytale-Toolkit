package org.bouncycastle.crypto.macs;

import java.util.Hashtable;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;

public class HMac implements Mac {
   private static final byte IPAD = 54;
   private static final byte OPAD = 92;
   private Digest digest;
   private int digestSize;
   private int blockLength;
   private Memoable ipadState;
   private Memoable opadState;
   private byte[] inputPad;
   private byte[] outputBuf;
   private static Hashtable blockLengths = new Hashtable();

   private static int getByteLength(Digest var0) {
      if (var0 instanceof ExtendedDigest) {
         return ((ExtendedDigest)var0).getByteLength();
      } else {
         Integer var1 = (Integer)blockLengths.get(var0.getAlgorithmName());
         if (var1 == null) {
            throw new IllegalArgumentException("unknown digest passed: " + var0.getAlgorithmName());
         } else {
            return var1;
         }
      }
   }

   public HMac(Digest var1) {
      this(var1, getByteLength(var1));
   }

   private HMac(Digest var1, int var2) {
      this.digest = var1;
      this.digestSize = var1.getDigestSize();
      this.blockLength = var2;
      this.inputPad = new byte[this.blockLength];
      this.outputBuf = new byte[this.blockLength + this.digestSize];
   }

   @Override
   public String getAlgorithmName() {
      return this.digest.getAlgorithmName() + "/HMAC";
   }

   public Digest getUnderlyingDigest() {
      return this.digest;
   }

   @Override
   public void init(CipherParameters var1) {
      this.digest.reset();
      byte[] var2 = ((KeyParameter)var1).getKey();
      int var3 = var2.length;
      if (var3 > this.blockLength) {
         this.digest.update(var2, 0, var3);
         this.digest.doFinal(this.inputPad, 0);
         var3 = this.digestSize;
      } else {
         System.arraycopy(var2, 0, this.inputPad, 0, var3);
      }

      for (int var4 = var3; var4 < this.inputPad.length; var4++) {
         this.inputPad[var4] = 0;
      }

      System.arraycopy(this.inputPad, 0, this.outputBuf, 0, this.blockLength);
      xorPad(this.inputPad, this.blockLength, (byte)54);
      xorPad(this.outputBuf, this.blockLength, (byte)92);
      if (this.digest instanceof Memoable) {
         this.opadState = ((Memoable)this.digest).copy();
         ((Digest)this.opadState).update(this.outputBuf, 0, this.blockLength);
      }

      this.digest.update(this.inputPad, 0, this.inputPad.length);
      if (this.digest instanceof Memoable) {
         this.ipadState = ((Memoable)this.digest).copy();
      }
   }

   @Override
   public int getMacSize() {
      return this.digestSize;
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
      this.digest.doFinal(this.outputBuf, this.blockLength);
      if (this.opadState != null) {
         ((Memoable)this.digest).reset(this.opadState);
         this.digest.update(this.outputBuf, this.blockLength, this.digest.getDigestSize());
      } else {
         this.digest.update(this.outputBuf, 0, this.outputBuf.length);
      }

      int var3 = this.digest.doFinal(var1, var2);

      for (int var4 = this.blockLength; var4 < this.outputBuf.length; var4++) {
         this.outputBuf[var4] = 0;
      }

      if (this.ipadState != null) {
         ((Memoable)this.digest).reset(this.ipadState);
      } else {
         this.digest.update(this.inputPad, 0, this.inputPad.length);
      }

      return var3;
   }

   @Override
   public void reset() {
      if (this.ipadState != null) {
         ((Memoable)this.digest).reset(this.ipadState);
      } else {
         this.digest.reset();
         this.digest.update(this.inputPad, 0, this.inputPad.length);
      }
   }

   private static void xorPad(byte[] var0, int var1, byte var2) {
      for (int var3 = 0; var3 < var1; var3++) {
         var0[var3] ^= var2;
      }
   }

   static {
      blockLengths.put("GOST3411", Integers.valueOf(32));
      blockLengths.put("MD2", Integers.valueOf(16));
      blockLengths.put("MD4", Integers.valueOf(64));
      blockLengths.put("MD5", Integers.valueOf(64));
      blockLengths.put("RIPEMD128", Integers.valueOf(64));
      blockLengths.put("RIPEMD160", Integers.valueOf(64));
      blockLengths.put("SHA-1", Integers.valueOf(64));
      blockLengths.put("SHA-224", Integers.valueOf(64));
      blockLengths.put("SHA-256", Integers.valueOf(64));
      blockLengths.put("SHA-384", Integers.valueOf(128));
      blockLengths.put("SHA-512", Integers.valueOf(128));
      blockLengths.put("Tiger", Integers.valueOf(64));
      blockLengths.put("Whirlpool", Integers.valueOf(64));
   }
}
