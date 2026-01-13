package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.util.Arrays;

public class Argon2Parameters {
   public static final int ARGON2_d = 0;
   public static final int ARGON2_i = 1;
   public static final int ARGON2_id = 2;
   public static final int ARGON2_VERSION_10 = 16;
   public static final int ARGON2_VERSION_13 = 19;
   private static final int DEFAULT_ITERATIONS = 3;
   private static final int DEFAULT_MEMORY_COST = 12;
   private static final int DEFAULT_LANES = 1;
   private static final int DEFAULT_TYPE = 1;
   private static final int DEFAULT_VERSION = 19;
   private final byte[] salt;
   private final byte[] secret;
   private final byte[] additional;
   private final int iterations;
   private final int memory;
   private final int lanes;
   private final int version;
   private final int type;
   private final CharToByteConverter converter;

   private Argon2Parameters(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6, int var7, int var8, CharToByteConverter var9) {
      this.salt = Arrays.clone(var2);
      this.secret = Arrays.clone(var3);
      this.additional = Arrays.clone(var4);
      this.iterations = var5;
      this.memory = var6;
      this.lanes = var7;
      this.version = var8;
      this.type = var1;
      this.converter = var9;
   }

   public byte[] getSalt() {
      return Arrays.clone(this.salt);
   }

   public byte[] getSecret() {
      return Arrays.clone(this.secret);
   }

   public byte[] getAdditional() {
      return Arrays.clone(this.additional);
   }

   public int getIterations() {
      return this.iterations;
   }

   public int getMemory() {
      return this.memory;
   }

   public int getLanes() {
      return this.lanes;
   }

   public int getVersion() {
      return this.version;
   }

   public int getType() {
      return this.type;
   }

   public CharToByteConverter getCharToByteConverter() {
      return this.converter;
   }

   public void clear() {
      Arrays.clear(this.salt);
      Arrays.clear(this.secret);
      Arrays.clear(this.additional);
   }

   public static class Builder {
      private byte[] salt;
      private byte[] secret;
      private byte[] additional;
      private int iterations;
      private int memory;
      private int lanes;
      private int version;
      private final int type;
      private CharToByteConverter converter = PasswordConverter.UTF8;

      public Builder() {
         this(1);
      }

      public Builder(int var1) {
         this.type = var1;
         this.lanes = 1;
         this.memory = 4096;
         this.iterations = 3;
         this.version = 19;
      }

      public Argon2Parameters.Builder withParallelism(int var1) {
         this.lanes = var1;
         return this;
      }

      public Argon2Parameters.Builder withSalt(byte[] var1) {
         this.salt = Arrays.clone(var1);
         return this;
      }

      public Argon2Parameters.Builder withSecret(byte[] var1) {
         this.secret = Arrays.clone(var1);
         return this;
      }

      public Argon2Parameters.Builder withAdditional(byte[] var1) {
         this.additional = Arrays.clone(var1);
         return this;
      }

      public Argon2Parameters.Builder withIterations(int var1) {
         this.iterations = var1;
         return this;
      }

      public Argon2Parameters.Builder withMemoryAsKB(int var1) {
         this.memory = var1;
         return this;
      }

      public Argon2Parameters.Builder withMemoryPowOfTwo(int var1) {
         this.memory = 1 << var1;
         return this;
      }

      public Argon2Parameters.Builder withVersion(int var1) {
         this.version = var1;
         return this;
      }

      public Argon2Parameters.Builder withCharToByteConverter(CharToByteConverter var1) {
         this.converter = var1;
         return this;
      }

      public Argon2Parameters build() {
         return new Argon2Parameters(this.type, this.salt, this.secret, this.additional, this.iterations, this.memory, this.lanes, this.version, this.converter);
      }

      public void clear() {
         Arrays.clear(this.salt);
         Arrays.clear(this.secret);
         Arrays.clear(this.additional);
      }
   }
}
