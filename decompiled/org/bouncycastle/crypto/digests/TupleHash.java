package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class TupleHash implements Xof, SavableDigest {
   private static final byte[] N_TUPLE_HASH = Strings.toByteArray("TupleHash");
   private final CSHAKEDigest cshake;
   private int bitLength;
   private int outputLength;
   private boolean firstOutput;

   public TupleHash(int var1, byte[] var2) {
      this(var1, var2, var1 * 2);
   }

   public TupleHash(int var1, byte[] var2, int var3) {
      this.cshake = new CSHAKEDigest(var1, N_TUPLE_HASH, var2);
      this.bitLength = var1;
      this.outputLength = (var3 + 7) / 8;
      this.reset();
   }

   public TupleHash(TupleHash var1) {
      this.cshake = new CSHAKEDigest(var1.cshake);
      this.bitLength = var1.bitLength;
      this.outputLength = var1.outputLength;
      this.firstOutput = var1.firstOutput;
   }

   public TupleHash(byte[] var1) {
      this.cshake = new CSHAKEDigest(Arrays.copyOfRange(var1, 0, var1.length - 9));
      this.bitLength = Pack.bigEndianToInt(var1, var1.length - 9);
      this.outputLength = Pack.bigEndianToInt(var1, var1.length - 5);
      this.firstOutput = var1[var1.length - 1] != 0;
   }

   private void copyIn(TupleHash var1) {
      this.cshake.reset(var1.cshake);
      this.bitLength = this.cshake.fixedOutputLength;
      this.outputLength = this.bitLength * 2 / 8;
      this.firstOutput = var1.firstOutput;
   }

   @Override
   public String getAlgorithmName() {
      return "TupleHash" + this.cshake.getAlgorithmName().substring(6);
   }

   @Override
   public int getByteLength() {
      return this.cshake.getByteLength();
   }

   @Override
   public int getDigestSize() {
      return this.outputLength;
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      byte[] var2 = XofUtils.encode(var1);
      this.cshake.update(var2, 0, var2.length);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      byte[] var4 = XofUtils.encode(var1, var2, var3);
      this.cshake.update(var4, 0, var4.length);
   }

   private void wrapUp(int var1) {
      byte[] var2 = XofUtils.rightEncode(var1 * 8L);
      this.cshake.update(var2, 0, var2.length);
      this.firstOutput = false;
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      if (this.firstOutput) {
         this.wrapUp(this.getDigestSize());
      }

      int var3 = this.cshake.doFinal(var1, var2, this.getDigestSize());
      this.reset();
      return var3;
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      if (this.firstOutput) {
         this.wrapUp(this.getDigestSize());
      }

      int var4 = this.cshake.doFinal(var1, var2, var3);
      this.reset();
      return var4;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (this.firstOutput) {
         this.wrapUp(0);
      }

      return this.cshake.doOutput(var1, var2, var3);
   }

   @Override
   public void reset() {
      this.cshake.reset();
      this.firstOutput = true;
   }

   @Override
   public byte[] getEncodedState() {
      byte[] var1 = this.cshake.getEncodedState();
      byte[] var2 = new byte[9];
      Pack.intToBigEndian(this.bitLength, var2, 0);
      Pack.intToBigEndian(this.outputLength, var2, 4);
      var2[8] = (byte)(this.firstOutput ? 1 : 0);
      return Arrays.concatenate(var1, var2);
   }

   @Override
   public Memoable copy() {
      return new TupleHash(this);
   }

   @Override
   public void reset(Memoable var1) {
      this.copyIn((TupleHash)var1);
   }
}
