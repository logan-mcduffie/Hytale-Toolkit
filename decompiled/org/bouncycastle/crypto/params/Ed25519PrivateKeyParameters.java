package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PrivateKeyParameters extends AsymmetricKeyParameter {
   public static final int KEY_SIZE = 32;
   public static final int SIGNATURE_SIZE = 64;
   private final byte[] data = new byte[32];
   private Ed25519PublicKeyParameters cachedPublicKey;

   public Ed25519PrivateKeyParameters(SecureRandom var1) {
      super(true);
      Ed25519.generatePrivateKey(var1, this.data);
   }

   public Ed25519PrivateKeyParameters(byte[] var1) {
      this(validate(var1), 0);
   }

   public Ed25519PrivateKeyParameters(byte[] var1, int var2) {
      super(true);
      System.arraycopy(var1, var2, this.data, 0, 32);
   }

   public Ed25519PrivateKeyParameters(InputStream var1) throws IOException {
      super(true);
      if (32 != Streams.readFully(var1, this.data)) {
         throw new EOFException("EOF encountered in middle of Ed25519 private key");
      }
   }

   public void encode(byte[] var1, int var2) {
      System.arraycopy(this.data, 0, var1, var2, 32);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.data);
   }

   public Ed25519PublicKeyParameters generatePublicKey() {
      synchronized (this.data) {
         if (null == this.cachedPublicKey) {
            this.cachedPublicKey = new Ed25519PublicKeyParameters(Ed25519.generatePublicKey(this.data, 0));
         }

         return this.cachedPublicKey;
      }
   }

   /** @deprecated */
   public void sign(int var1, Ed25519PublicKeyParameters var2, byte[] var3, byte[] var4, int var5, int var6, byte[] var7, int var8) {
      this.sign(var1, var3, var4, var5, var6, var7, var8);
   }

   public void sign(int var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6, int var7) {
      Ed25519PublicKeyParameters var8 = this.generatePublicKey();
      byte[] var9 = new byte[32];
      var8.encode(var9, 0);
      switch (var1) {
         case 0:
            if (null != var2) {
               throw new IllegalArgumentException("ctx");
            }

            Ed25519.sign(this.data, 0, var9, 0, var3, var4, var5, var6, var7);
            break;
         case 1:
            if (null == var2) {
               throw new NullPointerException("'ctx' cannot be null");
            }

            if (var2.length > 255) {
               throw new IllegalArgumentException("ctx");
            }

            Ed25519.sign(this.data, 0, var9, 0, var2, var3, var4, var5, var6, var7);
            break;
         case 2:
            if (null == var2) {
               throw new NullPointerException("'ctx' cannot be null");
            }

            if (var2.length > 255) {
               throw new IllegalArgumentException("ctx");
            }

            if (64 != var5) {
               throw new IllegalArgumentException("msgLen");
            }

            Ed25519.signPrehash(this.data, 0, var9, 0, var2, var3, var4, var6, var7);
            break;
         default:
            throw new IllegalArgumentException("algorithm");
      }
   }

   private static byte[] validate(byte[] var0) {
      if (var0.length != 32) {
         throw new IllegalArgumentException("'buf' must have length 32");
      } else {
         return var0;
      }
   }
}
