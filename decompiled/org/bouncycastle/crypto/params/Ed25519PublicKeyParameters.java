package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PublicKeyParameters extends AsymmetricKeyParameter {
   public static final int KEY_SIZE = 32;
   private final Ed25519.PublicPoint publicPoint;

   public Ed25519PublicKeyParameters(byte[] var1) {
      this(validate(var1), 0);
   }

   public Ed25519PublicKeyParameters(byte[] var1, int var2) {
      super(false);
      this.publicPoint = parse(var1, var2);
   }

   public Ed25519PublicKeyParameters(InputStream var1) throws IOException {
      super(false);
      byte[] var2 = new byte[32];
      if (32 != Streams.readFully(var1, var2)) {
         throw new EOFException("EOF encountered in middle of Ed25519 public key");
      } else {
         this.publicPoint = parse(var2, 0);
      }
   }

   public Ed25519PublicKeyParameters(Ed25519.PublicPoint var1) {
      super(false);
      if (var1 == null) {
         throw new NullPointerException("'publicPoint' cannot be null");
      } else {
         this.publicPoint = var1;
      }
   }

   public void encode(byte[] var1, int var2) {
      Ed25519.encodePublicPoint(this.publicPoint, var1, var2);
   }

   public byte[] getEncoded() {
      byte[] var1 = new byte[32];
      this.encode(var1, 0);
      return var1;
   }

   public boolean verify(int var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6, int var7) {
      switch (var1) {
         case 0:
            if (null != var2) {
               throw new IllegalArgumentException("ctx");
            }

            return Ed25519.verify(var6, var7, this.publicPoint, var3, var4, var5);
         case 1:
            if (null == var2) {
               throw new NullPointerException("'ctx' cannot be null");
            } else {
               if (var2.length > 255) {
                  throw new IllegalArgumentException("ctx");
               }

               return Ed25519.verify(var6, var7, this.publicPoint, var2, var3, var4, var5);
            }
         case 2:
            if (null == var2) {
               throw new NullPointerException("'ctx' cannot be null");
            } else if (var2.length > 255) {
               throw new IllegalArgumentException("ctx");
            } else {
               if (64 != var5) {
                  throw new IllegalArgumentException("msgLen");
               }

               return Ed25519.verifyPrehash(var6, var7, this.publicPoint, var2, var3, var4);
            }
         default:
            throw new IllegalArgumentException("algorithm");
      }
   }

   private static Ed25519.PublicPoint parse(byte[] var0, int var1) {
      Ed25519.PublicPoint var2 = Ed25519.validatePublicKeyPartialExport(var0, var1);
      if (var2 == null) {
         throw new IllegalArgumentException("invalid public key");
      } else {
         return var2;
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
