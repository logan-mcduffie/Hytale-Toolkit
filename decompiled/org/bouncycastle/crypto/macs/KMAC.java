package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.CSHAKEDigest;
import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.crypto.digests.XofUtils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class KMAC implements Mac, Xof, Memoable, EncodableDigest {
   private static final byte[] padding = new byte[100];
   private final CSHAKEDigest cshake;
   private int bitLength;
   private int outputLength;
   private byte[] key;
   private boolean initialised;
   private boolean firstOutput;

   public KMAC(int var1, byte[] var2) {
      this.cshake = new CSHAKEDigest(var1, Strings.toByteArray("KMAC"), var2);
      this.bitLength = var1;
      this.outputLength = var1 * 2 / 8;
   }

   public KMAC(KMAC var1) {
      this.cshake = new CSHAKEDigest(var1.cshake);
      this.bitLength = var1.bitLength;
      this.outputLength = var1.outputLength;
      this.key = var1.key;
      this.initialised = var1.initialised;
      this.firstOutput = var1.firstOutput;
   }

   public KMAC(byte[] var1) {
      this.key = new byte[var1[0] & 255];
      System.arraycopy(var1, 1, this.key, 0, this.key.length);
      this.cshake = new CSHAKEDigest(Arrays.copyOfRange(var1, 1 + this.key.length, var1.length - 10));
      this.bitLength = Pack.bigEndianToInt(var1, var1.length - 10);
      this.outputLength = Pack.bigEndianToInt(var1, var1.length - 6);
      this.initialised = var1[var1.length - 2] != 0;
      this.firstOutput = var1[var1.length - 1] != 0;
   }

   private void copyIn(KMAC var1) {
      this.cshake.reset(var1.cshake);
      this.bitLength = var1.bitLength;
      this.outputLength = var1.outputLength;
      this.initialised = var1.initialised;
      this.firstOutput = var1.firstOutput;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      KeyParameter var2 = (KeyParameter)var1;
      this.key = Arrays.clone(var2.getKey());
      if (this.key.length > 255) {
         throw new IllegalArgumentException("key length must be between 0 and 2040 bits");
      } else {
         this.initialised = true;
         this.reset();
      }
   }

   @Override
   public String getAlgorithmName() {
      return "KMAC" + this.cshake.getAlgorithmName().substring(6);
   }

   @Override
   public int getByteLength() {
      return this.cshake.getByteLength();
   }

   @Override
   public int getMacSize() {
      return this.outputLength;
   }

   @Override
   public int getDigestSize() {
      return this.outputLength;
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      if (!this.initialised) {
         throw new IllegalStateException("KMAC not initialized");
      } else {
         this.cshake.update(var1);
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      if (!this.initialised) {
         throw new IllegalStateException("KMAC not initialized");
      } else {
         this.cshake.update(var1, var2, var3);
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      if (this.firstOutput) {
         if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
         }

         byte[] var3 = XofUtils.rightEncode(this.getMacSize() * 8);
         this.cshake.update(var3, 0, var3.length);
      }

      int var4 = this.cshake.doFinal(var1, var2, this.getMacSize());
      this.reset();
      return var4;
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      if (this.firstOutput) {
         if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
         }

         byte[] var4 = XofUtils.rightEncode(var3 * 8);
         this.cshake.update(var4, 0, var4.length);
      }

      int var5 = this.cshake.doFinal(var1, var2, var3);
      this.reset();
      return var5;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (this.firstOutput) {
         if (!this.initialised) {
            throw new IllegalStateException("KMAC not initialized");
         }

         byte[] var4 = XofUtils.rightEncode(0L);
         this.cshake.update(var4, 0, var4.length);
         this.firstOutput = false;
      }

      return this.cshake.doOutput(var1, var2, var3);
   }

   @Override
   public void reset() {
      this.cshake.reset();
      if (this.key != null) {
         if (this.bitLength == 128) {
            this.bytePad(this.key, 168);
         } else {
            this.bytePad(this.key, 136);
         }
      }

      this.firstOutput = true;
   }

   private void bytePad(byte[] var1, int var2) {
      byte[] var3 = XofUtils.leftEncode(var2);
      this.update(var3, 0, var3.length);
      byte[] var4 = encode(var1);
      this.update(var4, 0, var4.length);
      int var5 = var2 - (var3.length + var4.length) % var2;
      if (var5 > 0 && var5 != var2) {
         while (var5 > padding.length) {
            this.update(padding, 0, padding.length);
            var5 -= padding.length;
         }

         this.update(padding, 0, var5);
      }
   }

   private static byte[] encode(byte[] var0) {
      return Arrays.concatenate(XofUtils.leftEncode(var0.length * 8), var0);
   }

   @Override
   public byte[] getEncodedState() {
      if (!this.initialised) {
         throw new IllegalStateException("KMAC not initialised");
      } else {
         byte[] var1 = this.cshake.getEncodedState();
         byte[] var2 = new byte[10];
         Pack.intToBigEndian(this.bitLength, var2, 0);
         Pack.intToBigEndian(this.outputLength, var2, 4);
         var2[8] = (byte)(this.initialised ? 1 : 0);
         var2[9] = (byte)(this.firstOutput ? 1 : 0);
         byte[] var3 = new byte[1 + this.key.length + var1.length + var2.length];
         var3[0] = (byte)this.key.length;
         System.arraycopy(this.key, 0, var3, 1, this.key.length);
         System.arraycopy(var1, 0, var3, 1 + this.key.length, var1.length);
         System.arraycopy(var2, 0, var3, 1 + this.key.length + var1.length, var2.length);
         return var3;
      }
   }

   @Override
   public Memoable copy() {
      return new KMAC(this);
   }

   @Override
   public void reset(Memoable var1) {
      this.copyIn((KMAC)var1);
   }
}
