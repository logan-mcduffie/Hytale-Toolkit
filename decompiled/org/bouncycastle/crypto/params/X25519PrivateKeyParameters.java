package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class X25519PrivateKeyParameters extends AsymmetricKeyParameter {
   public static final int KEY_SIZE = 32;
   public static final int SECRET_SIZE = 32;
   private final byte[] data = new byte[32];

   public X25519PrivateKeyParameters(SecureRandom var1) {
      super(true);
      X25519.generatePrivateKey(var1, this.data);
   }

   public X25519PrivateKeyParameters(byte[] var1) {
      this(validate(var1), 0);
   }

   public X25519PrivateKeyParameters(byte[] var1, int var2) {
      super(true);
      System.arraycopy(var1, var2, this.data, 0, 32);
   }

   public X25519PrivateKeyParameters(InputStream var1) throws IOException {
      super(true);
      if (32 != Streams.readFully(var1, this.data)) {
         throw new EOFException("EOF encountered in middle of X25519 private key");
      }
   }

   public void encode(byte[] var1, int var2) {
      System.arraycopy(this.data, 0, var1, var2, 32);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.data);
   }

   public X25519PublicKeyParameters generatePublicKey() {
      byte[] var1 = new byte[32];
      X25519.generatePublicKey(this.data, 0, var1, 0);
      return new X25519PublicKeyParameters(var1, 0);
   }

   public void generateSecret(X25519PublicKeyParameters var1, byte[] var2, int var3) {
      byte[] var4 = new byte[32];
      var1.encode(var4, 0);
      if (!X25519.calculateAgreement(this.data, 0, var4, 0, var2, var3)) {
         throw new IllegalStateException("X25519 agreement failed");
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
