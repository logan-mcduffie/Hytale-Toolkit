package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class ChaCha20Poly1305 implements AEADCipher {
   private static final int BUF_SIZE = 64;
   private static final int KEY_SIZE = 32;
   private static final int NONCE_SIZE = 12;
   private static final int MAC_SIZE = 16;
   private static final byte[] ZEROES = new byte[15];
   private static final long AAD_LIMIT = -1L;
   private static final long DATA_LIMIT = 274877906880L;
   private final ChaCha7539Engine chacha20;
   private final Mac poly1305;
   private final byte[] key = new byte[32];
   private final byte[] nonce = new byte[12];
   private final byte[] buf = new byte[80];
   private final byte[] mac = new byte[16];
   private byte[] initialAAD;
   private long aadCount;
   private long dataCount;
   private int state = 0;
   private int bufPos;

   public ChaCha20Poly1305() {
      this(new Poly1305());
   }

   public ChaCha20Poly1305(Mac var1) {
      if (null == var1) {
         throw new NullPointerException("'poly1305' cannot be null");
      } else if (16 != var1.getMacSize()) {
         throw new IllegalArgumentException("'poly1305' must be a 128-bit MAC");
      } else {
         this.chacha20 = new ChaCha7539Engine();
         this.poly1305 = var1;
      }
   }

   @Override
   public String getAlgorithmName() {
      return "ChaCha20Poly1305";
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      KeyParameter var3;
      byte[] var4;
      ParametersWithIV var5;
      if (var2 instanceof AEADParameters) {
         AEADParameters var6 = (AEADParameters)var2;
         int var7 = var6.getMacSize();
         if (128 != var7) {
            throw new IllegalArgumentException("Invalid value for MAC size: " + var7);
         }

         var3 = var6.getKey();
         var4 = var6.getNonce();
         var5 = new ParametersWithIV(var3, var4);
         this.initialAAD = var6.getAssociatedText();
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameters passed to ChaCha20Poly1305");
         }

         ParametersWithIV var8 = (ParametersWithIV)var2;
         var3 = (KeyParameter)var8.getParameters();
         var4 = var8.getIV();
         var5 = var8;
         this.initialAAD = null;
      }

      if (null == var3) {
         if (0 == this.state) {
            throw new IllegalArgumentException("Key must be specified in initial init");
         }
      } else if (32 != var3.getKeyLength()) {
         throw new IllegalArgumentException("Key must be 256 bits");
      }

      if (null != var4 && 12 == var4.length) {
         if (0 == this.state || !var1 || !Arrays.areEqual(this.nonce, var4) || null != var3 && !Arrays.areEqual(this.key, var3.getKey())) {
            if (null != var3) {
               var3.copyTo(this.key, 0, 32);
            }

            System.arraycopy(var4, 0, this.nonce, 0, 12);
            this.chacha20.init(true, var5);
            this.state = var1 ? 1 : 5;
            this.reset(true, false);
         } else {
            throw new IllegalArgumentException("cannot reuse nonce for ChaCha20Poly1305 encryption");
         }
      } else {
         throw new IllegalArgumentException("Nonce must be 96 bits");
      }
   }

   @Override
   public int getOutputSize(int var1) {
      int var2 = Math.max(0, var1) + this.bufPos;
      switch (this.state) {
         case 1:
         case 2:
         case 3:
            return var2 + 16;
         case 4:
         default:
            throw new IllegalStateException();
         case 5:
         case 6:
         case 7:
            return Math.max(0, var2 - 16);
      }
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      int var2 = Math.max(0, var1) + this.bufPos;
      switch (this.state) {
         case 4:
         default:
            throw new IllegalStateException();
         case 5:
         case 6:
         case 7:
            var2 = Math.max(0, var2 - 16);
         case 1:
         case 2:
         case 3:
            return var2 - var2 % 64;
      }
   }

   @Override
   public void processAADByte(byte var1) {
      this.checkAAD();
      this.aadCount = this.incrementCount(this.aadCount, 1, -1L);
      this.poly1305.update(var1);
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      if (null == var1) {
         throw new NullPointerException("'in' cannot be null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("'inOff' cannot be negative");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("'len' cannot be negative");
      } else if (var2 > var1.length - var3) {
         throw new DataLengthException("Input buffer too short");
      } else {
         this.checkAAD();
         if (var3 > 0) {
            this.aadCount = this.incrementCount(this.aadCount, var3, -1L);
            this.poly1305.update(var1, var2, var3);
         }
      }
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
      this.checkData();
      switch (this.state) {
         case 3:
            this.buf[this.bufPos] = var1;
            if (++this.bufPos == 64) {
               this.processData(this.buf, 0, 64, var2, var3);
               this.poly1305.update(var2, var3, 64);
               this.bufPos = 0;
               return 64;
            }

            return 0;
         case 7:
            this.buf[this.bufPos] = var1;
            if (++this.bufPos == this.buf.length) {
               this.poly1305.update(this.buf, 0, 64);
               this.processData(this.buf, 0, 64, var2, var3);
               System.arraycopy(this.buf, 64, this.buf, 0, 16);
               this.bufPos = 16;
               return 64;
            }

            return 0;
         default:
            throw new IllegalStateException();
      }
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      if (null == var1) {
         throw new NullPointerException("'in' cannot be null");
      } else {
         if (null == var4) {
         }

         if (var2 < 0) {
            throw new IllegalArgumentException("'inOff' cannot be negative");
         } else if (var3 < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
         } else if (var2 > var1.length - var3) {
            throw new DataLengthException("Input buffer too short");
         } else if (var5 < 0) {
            throw new IllegalArgumentException("'outOff' cannot be negative");
         } else {
            if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, this.getUpdateOutputSize(var3))) {
               var1 = new byte[var3];
               System.arraycopy(var4, var2, var1, 0, var3);
               var2 = 0;
            }

            this.checkData();
            byte var6 = 0;
            switch (this.state) {
               case 3:
                  if (this.bufPos != 0) {
                     while (var3 > 0) {
                        var3--;
                        this.buf[this.bufPos] = var1[var2++];
                        if (++this.bufPos == 64) {
                           this.processData(this.buf, 0, 64, var4, var5);
                           this.poly1305.update(var4, var5, 64);
                           this.bufPos = 0;
                           var6 = 64;
                           break;
                        }
                     }
                  }

                  while (var3 >= 64) {
                     this.processData(var1, var2, 64, var4, var5 + var6);
                     this.poly1305.update(var4, var5 + var6, 64);
                     var2 += 64;
                     var3 -= 64;
                     var6 += 64;
                  }

                  if (var3 > 0) {
                     System.arraycopy(var1, var2, this.buf, 0, var3);
                     this.bufPos = var3;
                  }
                  break;
               case 7:
                  for (int var7 = 0; var7 < var3; var7++) {
                     this.buf[this.bufPos] = var1[var2 + var7];
                     if (++this.bufPos == this.buf.length) {
                        this.poly1305.update(this.buf, 0, 64);
                        this.processData(this.buf, 0, 64, var4, var5 + var6);
                        System.arraycopy(this.buf, 64, this.buf, 0, 16);
                        this.bufPos = 16;
                        var6 += 64;
                     }
                  }
                  break;
               default:
                  throw new IllegalStateException();
            }

            return var6;
         }
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      if (null == var1) {
         throw new NullPointerException("'out' cannot be null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("'outOff' cannot be negative");
      } else {
         this.checkData();
         Arrays.clear(this.mac);
         int var3 = 0;
         switch (this.state) {
            case 3:
               var3 = this.bufPos + 16;
               if (var2 > var1.length - var3) {
                  throw new OutputLengthException("Output buffer too short");
               }

               if (this.bufPos > 0) {
                  this.processData(this.buf, 0, this.bufPos, var1, var2);
                  this.poly1305.update(var1, var2, this.bufPos);
               }

               this.finishData(4);
               System.arraycopy(this.mac, 0, var1, var2 + this.bufPos, 16);
               break;
            case 7:
               if (this.bufPos < 16) {
                  throw new InvalidCipherTextException("data too short");
               }

               var3 = this.bufPos - 16;
               if (var2 > var1.length - var3) {
                  throw new OutputLengthException("Output buffer too short");
               }

               if (var3 > 0) {
                  this.poly1305.update(this.buf, 0, var3);
                  this.processData(this.buf, 0, var3, var1, var2);
               }

               this.finishData(8);
               if (!Arrays.constantTimeAreEqual(16, this.mac, 0, this.buf, var3)) {
                  throw new InvalidCipherTextException("mac check in ChaCha20Poly1305 failed");
               }
               break;
            default:
               throw new IllegalStateException();
         }

         this.reset(false, true);
         return var3;
      }
   }

   @Override
   public byte[] getMac() {
      return Arrays.clone(this.mac);
   }

   @Override
   public void reset() {
      this.reset(true, true);
   }

   private void checkAAD() {
      switch (this.state) {
         case 1:
            this.state = 2;
         case 2:
         case 6:
            break;
         case 3:
         default:
            throw new IllegalStateException();
         case 4:
            throw new IllegalStateException("ChaCha20Poly1305 cannot be reused for encryption");
         case 5:
            this.state = 6;
      }
   }

   private void checkData() {
      switch (this.state) {
         case 1:
         case 2:
            this.finishAAD(3);
         case 3:
         case 7:
            break;
         case 4:
            throw new IllegalStateException("ChaCha20Poly1305 cannot be reused for encryption");
         case 5:
         case 6:
            this.finishAAD(7);
            break;
         default:
            throw new IllegalStateException();
      }
   }

   private void finishAAD(int var1) {
      this.padMAC(this.aadCount);
      this.state = var1;
   }

   private void finishData(int var1) {
      this.padMAC(this.dataCount);
      byte[] var2 = new byte[16];
      Pack.longToLittleEndian(this.aadCount, var2, 0);
      Pack.longToLittleEndian(this.dataCount, var2, 8);
      this.poly1305.update(var2, 0, 16);
      this.poly1305.doFinal(this.mac, 0);
      this.state = var1;
   }

   private long incrementCount(long var1, int var3, long var4) {
      if (var1 + Long.MIN_VALUE > var4 - var3 + Long.MIN_VALUE) {
         throw new IllegalStateException("Limit exceeded");
      } else {
         return var1 + var3;
      }
   }

   private void initMAC() {
      byte[] var1 = new byte[64];

      try {
         this.chacha20.processBytes(var1, 0, 64, var1, 0);
         this.poly1305.init(new KeyParameter(var1, 0, 32));
      } finally {
         Arrays.clear(var1);
      }
   }

   private void padMAC(long var1) {
      int var3 = (int)var1 & 15;
      if (0 != var3) {
         this.poly1305.update(ZEROES, 0, 16 - var3);
      }
   }

   private void processData(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (var5 > var4.length - var3) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         this.chacha20.processBytes(var1, var2, var3, var4, var5);
         this.dataCount = this.incrementCount(this.dataCount, var3, 274877906880L);
      }
   }

   private void reset(boolean var1, boolean var2) {
      Arrays.clear(this.buf);
      if (var1) {
         Arrays.clear(this.mac);
      }

      this.aadCount = 0L;
      this.dataCount = 0L;
      this.bufPos = 0;
      switch (this.state) {
         case 2:
         case 3:
         case 4:
            this.state = 4;
            return;
         case 6:
         case 7:
         case 8:
            this.state = 5;
         case 1:
         case 5:
            if (var2) {
               this.chacha20.reset();
            }

            this.initMAC();
            if (null != this.initialAAD) {
               this.processAADBytes(this.initialAAD, 0, this.initialAAD.length);
            }

            return;
         default:
            throw new IllegalStateException();
      }
   }

   private static final class State {
      static final int UNINITIALIZED = 0;
      static final int ENC_INIT = 1;
      static final int ENC_AAD = 2;
      static final int ENC_DATA = 3;
      static final int ENC_FINAL = 4;
      static final int DEC_INIT = 5;
      static final int DEC_AAD = 6;
      static final int DEC_DATA = 7;
      static final int DEC_FINAL = 8;
   }
}
