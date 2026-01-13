package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class X25519PublicKeyParameters extends AsymmetricKeyParameter {
   public static final int KEY_SIZE = 32;
   private final byte[] data = new byte[32];

   public X25519PublicKeyParameters(byte[] var1) {
      this(validate(var1), 0);
   }

   public X25519PublicKeyParameters(byte[] var1, int var2) {
      super(false);
      System.arraycopy(var1, var2, this.data, 0, 32);
   }

   public X25519PublicKeyParameters(InputStream var1) throws IOException {
      super(false);
      if (32 != Streams.readFully(var1, this.data)) {
         throw new EOFException("EOF encountered in middle of X25519 public key");
      }
   }

   public void encode(byte[] var1, int var2) {
      System.arraycopy(this.data, 0, var1, var2, 32);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.data);
   }

   private static byte[] validate(byte[] var0) {
      if (var0.length != 32) {
         throw new IllegalArgumentException("'buf' must have length 32");
      } else {
         return var0;
      }
   }
}
