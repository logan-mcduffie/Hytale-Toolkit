package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public final class X448PrivateKeyParameters extends AsymmetricKeyParameter {
   public static final int KEY_SIZE = 56;
   public static final int SECRET_SIZE = 56;
   private final byte[] data = new byte[56];

   public X448PrivateKeyParameters(SecureRandom var1) {
      super(true);
      X448.generatePrivateKey(var1, this.data);
   }

   public X448PrivateKeyParameters(byte[] var1) {
      this(validate(var1), 0);
   }

   public X448PrivateKeyParameters(byte[] var1, int var2) {
      super(true);
      System.arraycopy(var1, var2, this.data, 0, 56);
   }

   public X448PrivateKeyParameters(InputStream var1) throws IOException {
      super(true);
      if (56 != Streams.readFully(var1, this.data)) {
         throw new EOFException("EOF encountered in middle of X448 private key");
      }
   }

   public void encode(byte[] var1, int var2) {
      System.arraycopy(this.data, 0, var1, var2, 56);
   }

   public byte[] getEncoded() {
      return Arrays.clone(this.data);
   }

   public X448PublicKeyParameters generatePublicKey() {
      byte[] var1 = new byte[56];
      X448.generatePublicKey(this.data, 0, var1, 0);
      return new X448PublicKeyParameters(var1, 0);
   }

   public void generateSecret(X448PublicKeyParameters var1, byte[] var2, int var3) {
      byte[] var4 = new byte[56];
      var1.encode(var4, 0);
      if (!X448.calculateAgreement(this.data, 0, var4, 0, var2, var3)) {
         throw new IllegalStateException("X448 agreement failed");
      }
   }

   private static byte[] validate(byte[] var0) {
      if (var0.length != 56) {
         throw new IllegalArgumentException("'buf' must have length 56");
      } else {
         return var0;
      }
   }
}
