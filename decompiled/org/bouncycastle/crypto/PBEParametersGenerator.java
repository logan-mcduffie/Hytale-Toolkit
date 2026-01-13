package org.bouncycastle.crypto;

import org.bouncycastle.util.Strings;

public abstract class PBEParametersGenerator {
   protected byte[] password;
   protected byte[] salt;
   protected int iterationCount;

   protected PBEParametersGenerator() {
   }

   public void init(byte[] var1, byte[] var2, int var3) {
      this.password = var1;
      this.salt = var2;
      this.iterationCount = var3;
   }

   public byte[] getPassword() {
      return this.password;
   }

   public byte[] getSalt() {
      return this.salt;
   }

   public int getIterationCount() {
      return this.iterationCount;
   }

   public abstract CipherParameters generateDerivedParameters(int var1);

   public abstract CipherParameters generateDerivedParameters(int var1, int var2);

   public abstract CipherParameters generateDerivedMacParameters(int var1);

   public static byte[] PKCS5PasswordToBytes(char[] var0) {
      if (var0 == null) {
         return new byte[0];
      } else {
         byte[] var1 = new byte[var0.length];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = (byte)var0[var2];
         }

         return var1;
      }
   }

   public static byte[] PKCS5PasswordToUTF8Bytes(char[] var0) {
      return var0 != null ? Strings.toUTF8ByteArray(var0) : new byte[0];
   }

   public static byte[] PKCS12PasswordToBytes(char[] var0) {
      if (var0 != null && var0.length > 0) {
         byte[] var1 = new byte[(var0.length + 1) * 2];

         for (int var2 = 0; var2 != var0.length; var2++) {
            var1[var2 * 2] = (byte)(var0[var2] >>> '\b');
            var1[var2 * 2 + 1] = (byte)var0[var2];
         }

         return var1;
      } else {
         return new byte[0];
      }
   }
}
