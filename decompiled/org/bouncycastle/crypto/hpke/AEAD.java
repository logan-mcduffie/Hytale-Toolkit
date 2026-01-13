package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.ChaCha20Poly1305;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Pack;

public class AEAD {
   private final short aeadId;
   private final byte[] key;
   private final byte[] baseNonce;
   private long seq = 0L;
   private AEADCipher cipher;

   public AEAD(short var1, byte[] var2, byte[] var3) {
      this.key = var2;
      this.baseNonce = var3;
      this.aeadId = var1;
      this.seq = 0L;
      switch (var1) {
         case -1:
         case 0:
         default:
            break;
         case 1:
         case 2:
            this.cipher = GCMBlockCipher.newInstance(AESEngine.newInstance());
            break;
         case 3:
            this.cipher = new ChaCha20Poly1305();
      }
   }

   public byte[] seal(byte[] var1, byte[] var2) throws InvalidCipherTextException {
      return this.process(true, var1, var2, 0, var2.length);
   }

   public byte[] seal(byte[] var1, byte[] var2, int var3, int var4) throws InvalidCipherTextException {
      Arrays.validateSegment(var2, var3, var4);
      return this.process(true, var1, var2, var3, var4);
   }

   public byte[] open(byte[] var1, byte[] var2) throws InvalidCipherTextException {
      return this.process(false, var1, var2, 0, var2.length);
   }

   public byte[] open(byte[] var1, byte[] var2, int var3, int var4) throws InvalidCipherTextException {
      Arrays.validateSegment(var2, var3, var4);
      return this.process(false, var1, var2, var3, var4);
   }

   private byte[] computeNonce() {
      byte[] var1 = Pack.longToBigEndian(this.seq++);
      byte[] var2 = Arrays.clone(this.baseNonce);
      Bytes.xorTo(8, var1, 0, var2, var2.length - 8);
      return var2;
   }

   private byte[] process(boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws InvalidCipherTextException {
      switch (this.aeadId) {
         case -1:
         case 0:
         default:
            throw new IllegalStateException("Export only mode, cannot be used to seal/open");
         case 1:
         case 2:
         case 3:
            ParametersWithIV var6 = new ParametersWithIV(new KeyParameter(this.key), this.computeNonce());
            this.cipher.init(var1, var6);
            this.cipher.processAADBytes(var2, 0, var2.length);
            byte[] var7 = new byte[this.cipher.getOutputSize(var5)];
            int var8 = this.cipher.processBytes(var3, var4, var5, var7, 0);
            var8 += this.cipher.doFinal(var7, var8);
            if (var8 != var7.length) {
               throw new IllegalStateException();
            } else {
               return var7;
            }
      }
   }
}
