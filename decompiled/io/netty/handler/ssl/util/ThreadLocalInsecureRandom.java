package io.netty.handler.ssl.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

final class ThreadLocalInsecureRandom extends SecureRandom {
   private static final long serialVersionUID = -8209473337192526191L;
   private static final SecureRandom INSTANCE = new ThreadLocalInsecureRandom();

   static SecureRandom current() {
      return INSTANCE;
   }

   private ThreadLocalInsecureRandom() {
   }

   @Override
   public String getAlgorithm() {
      return "insecure";
   }

   @Override
   public void setSeed(byte[] seed) {
   }

   @Override
   public void setSeed(long seed) {
   }

   @Override
   public void nextBytes(byte[] bytes) {
      random().nextBytes(bytes);
   }

   @Override
   public byte[] generateSeed(int numBytes) {
      byte[] seed = new byte[numBytes];
      random().nextBytes(seed);
      return seed;
   }

   @Override
   public int nextInt() {
      return random().nextInt();
   }

   @Override
   public int nextInt(int n) {
      return random().nextInt(n);
   }

   @Override
   public boolean nextBoolean() {
      return random().nextBoolean();
   }

   @Override
   public long nextLong() {
      return random().nextLong();
   }

   @Override
   public float nextFloat() {
      return random().nextFloat();
   }

   @Override
   public double nextDouble() {
      return random().nextDouble();
   }

   @Override
   public double nextGaussian() {
      return random().nextGaussian();
   }

   private static Random random() {
      return ThreadLocalRandom.current();
   }
}
