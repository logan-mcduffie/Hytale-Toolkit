package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class DSTU7564Mac implements Mac {
   private static final int BITS_IN_BYTE = 8;
   private DSTU7564Digest engine;
   private int macSize;
   private byte[] paddedKey;
   private byte[] invertedKey;
   private long inputLength;

   public DSTU7564Mac(int var1) {
      this.engine = new DSTU7564Digest(var1);
      this.macSize = var1 / 8;
      this.paddedKey = null;
      this.invertedKey = null;
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      this.paddedKey = null;
      this.reset();
      if (!(var1 instanceof KeyParameter)) {
         throw new IllegalArgumentException("Bad parameter passed");
      } else {
         byte[] var2 = ((KeyParameter)var1).getKey();
         this.invertedKey = new byte[var2.length];
         this.paddedKey = this.padKey(var2);

         for (int var3 = 0; var3 < this.invertedKey.length; var3++) {
            this.invertedKey[var3] = (byte)(~var2[var3]);
         }

         this.engine.update(this.paddedKey, 0, this.paddedKey.length);
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DSTU7564Mac";
   }

   @Override
   public int getMacSize() {
      return this.macSize;
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      this.engine.update(var1);
      this.inputLength++;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      if (var1.length - var2 < var3) {
         throw new DataLengthException("Input buffer too short");
      } else if (this.paddedKey == null) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else {
         this.engine.update(var1, var2, var3);
         this.inputLength += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      if (this.paddedKey == null) {
         throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
      } else if (var1.length - var2 < this.macSize) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         this.pad();
         this.engine.update(this.invertedKey, 0, this.invertedKey.length);
         this.inputLength = 0L;
         int var3 = this.engine.doFinal(var1, var2);
         this.reset();
         return var3;
      }
   }

   @Override
   public void reset() {
      this.inputLength = 0L;
      this.engine.reset();
      if (this.paddedKey != null) {
         this.engine.update(this.paddedKey, 0, this.paddedKey.length);
      }
   }

   private void pad() {
      int var1 = this.engine.getByteLength() - (int)(this.inputLength % this.engine.getByteLength());
      if (var1 < 13) {
         var1 += this.engine.getByteLength();
      }

      byte[] var2 = new byte[var1];
      var2[0] = -128;
      Pack.longToLittleEndian(this.inputLength * 8L, var2, var2.length - 12);
      this.engine.update(var2, 0, var2.length);
   }

   private byte[] padKey(byte[] var1) {
      int var2 = (var1.length + this.engine.getByteLength() - 1) / this.engine.getByteLength() * this.engine.getByteLength();
      int var3 = var2 - var1.length;
      if (var3 < 13) {
         var2 += this.engine.getByteLength();
      }

      byte[] var4 = new byte[var2];
      System.arraycopy(var1, 0, var4, 0, var1.length);
      var4[var1.length] = -128;
      Pack.intToLittleEndian(var1.length * 8, var4, var4.length - 12);
      return var4;
   }
}
