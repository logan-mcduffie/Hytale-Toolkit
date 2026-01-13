package org.bouncycastle.crypto.digests;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SkeinEngine implements Memoable {
   public static final int SKEIN_256 = 256;
   public static final int SKEIN_512 = 512;
   public static final int SKEIN_1024 = 1024;
   private static final int PARAM_TYPE_KEY = 0;
   private static final int PARAM_TYPE_CONFIG = 4;
   private static final int PARAM_TYPE_MESSAGE = 48;
   private static final int PARAM_TYPE_OUTPUT = 63;
   private static final Hashtable INITIAL_STATES = new Hashtable();
   final ThreefishEngine threefish;
   private final int outputSizeBytes;
   long[] chain;
   private long[] initialState;
   private byte[] key;
   private SkeinEngine.Parameter[] preMessageParameters;
   private SkeinEngine.Parameter[] postMessageParameters;
   private final SkeinEngine.UBI ubi;
   private final byte[] singleByte = new byte[1];

   private static void initialState(int var0, int var1, long[] var2) {
      INITIAL_STATES.put(variantIdentifier(var0 / 8, var1 / 8), var2);
   }

   private static Integer variantIdentifier(int var0, int var1) {
      return Integers.valueOf(var1 << 16 | var0);
   }

   public SkeinEngine(int var1, int var2) {
      if (var2 % 8 != 0) {
         throw new IllegalArgumentException("Output size must be a multiple of 8 bits. :" + var2);
      } else {
         this.outputSizeBytes = var2 / 8;
         this.threefish = new ThreefishEngine(var1);
         this.ubi = new SkeinEngine.UBI(this.threefish.getBlockSize());
      }
   }

   public SkeinEngine(SkeinEngine var1) {
      this(var1.getBlockSize() * 8, var1.getOutputSize() * 8);
      this.copyIn(var1);
   }

   private void copyIn(SkeinEngine var1) {
      this.ubi.reset(var1.ubi);
      this.chain = Arrays.clone(var1.chain, this.chain);
      this.initialState = Arrays.clone(var1.initialState, this.initialState);
      this.key = Arrays.clone(var1.key, this.key);
      this.preMessageParameters = clone(var1.preMessageParameters, this.preMessageParameters);
      this.postMessageParameters = clone(var1.postMessageParameters, this.postMessageParameters);
   }

   private static SkeinEngine.Parameter[] clone(SkeinEngine.Parameter[] var0, SkeinEngine.Parameter[] var1) {
      if (var0 == null) {
         return null;
      } else {
         if (var1 == null || var1.length != var0.length) {
            var1 = new SkeinEngine.Parameter[var0.length];
         }

         System.arraycopy(var0, 0, var1, 0, var1.length);
         return var1;
      }
   }

   @Override
   public Memoable copy() {
      return new SkeinEngine(this);
   }

   @Override
   public void reset(Memoable var1) {
      SkeinEngine var2 = (SkeinEngine)var1;
      if (this.getBlockSize() == var2.getBlockSize() && this.outputSizeBytes == var2.outputSizeBytes) {
         this.copyIn(var2);
      } else {
         throw new IllegalArgumentException("Incompatible parameters in provided SkeinEngine.");
      }
   }

   public int getOutputSize() {
      return this.outputSizeBytes;
   }

   public int getBlockSize() {
      return this.threefish.getBlockSize();
   }

   public void init(SkeinParameters var1) {
      this.chain = null;
      this.key = null;
      this.preMessageParameters = null;
      this.postMessageParameters = null;
      if (var1 != null) {
         byte[] var2 = var1.getKey();
         if (var2.length < 16) {
            throw new IllegalArgumentException("Skein key must be at least 128 bits.");
         }

         this.initParams(var1.getParameters());
      }

      this.createInitialState();
      this.ubiInit(48);
   }

   private void initParams(Hashtable var1) {
      Enumeration var2 = var1.keys();
      Vector var3 = new Vector();
      Vector var4 = new Vector();

      while (var2.hasMoreElements()) {
         Integer var5 = (Integer)var2.nextElement();
         byte[] var6 = (byte[])var1.get(var5);
         if (var5 == 0) {
            this.key = var6;
         } else if (var5 < 48) {
            var3.addElement(new SkeinEngine.Parameter(var5, var6));
         } else {
            var4.addElement(new SkeinEngine.Parameter(var5, var6));
         }
      }

      this.preMessageParameters = new SkeinEngine.Parameter[var3.size()];
      var3.copyInto(this.preMessageParameters);
      sort(this.preMessageParameters);
      this.postMessageParameters = new SkeinEngine.Parameter[var4.size()];
      var4.copyInto(this.postMessageParameters);
      sort(this.postMessageParameters);
   }

   private static void sort(SkeinEngine.Parameter[] var0) {
      if (var0 != null) {
         for (int var1 = 1; var1 < var0.length; var1++) {
            SkeinEngine.Parameter var2 = var0[var1];

            int var3;
            for (var3 = var1; var3 > 0 && var2.getType() < var0[var3 - 1].getType(); var3--) {
               var0[var3] = var0[var3 - 1];
            }

            var0[var3] = var2;
         }
      }
   }

   private void createInitialState() {
      long[] var1 = (long[])INITIAL_STATES.get(variantIdentifier(this.getBlockSize(), this.getOutputSize()));
      if (this.key == null && var1 != null) {
         this.chain = Arrays.clone(var1);
      } else {
         this.chain = new long[this.getBlockSize() / 8];
         if (this.key != null) {
            this.ubiComplete(0, this.key);
         }

         this.ubiComplete(4, new SkeinEngine.Configuration(this.outputSizeBytes * 8).getBytes());
      }

      if (this.preMessageParameters != null) {
         for (int var2 = 0; var2 < this.preMessageParameters.length; var2++) {
            SkeinEngine.Parameter var3 = this.preMessageParameters[var2];
            this.ubiComplete(var3.getType(), var3.getValue());
         }
      }

      this.initialState = Arrays.clone(this.chain);
   }

   public void reset() {
      System.arraycopy(this.initialState, 0, this.chain, 0, this.chain.length);
      this.ubiInit(48);
   }

   private void ubiComplete(int var1, byte[] var2) {
      this.ubiInit(var1);
      this.ubi.update(var2, 0, var2.length, this.chain);
      this.ubiFinal();
   }

   private void ubiInit(int var1) {
      this.ubi.reset(var1);
   }

   private void ubiFinal() {
      this.ubi.doFinal(this.chain);
   }

   private void checkInitialised() {
      if (this.ubi == null) {
         throw new IllegalArgumentException("Skein engine is not initialised.");
      }
   }

   public void update(byte var1) {
      this.singleByte[0] = var1;
      this.update(this.singleByte, 0, 1);
   }

   public void update(byte[] var1, int var2, int var3) {
      this.checkInitialised();
      this.ubi.update(var1, var2, var3, this.chain);
   }

   public int doFinal(byte[] var1, int var2) {
      this.checkInitialised();
      if (var1.length < var2 + this.outputSizeBytes) {
         throw new OutputLengthException("Output buffer is too short to hold output");
      } else {
         this.ubiFinal();
         if (this.postMessageParameters != null) {
            for (int var3 = 0; var3 < this.postMessageParameters.length; var3++) {
               SkeinEngine.Parameter var4 = this.postMessageParameters[var3];
               this.ubiComplete(var4.getType(), var4.getValue());
            }
         }

         int var7 = this.getBlockSize();
         int var8 = (this.outputSizeBytes + var7 - 1) / var7;

         for (int var5 = 0; var5 < var8; var5++) {
            int var6 = Math.min(var7, this.outputSizeBytes - var5 * var7);
            this.output(var5, var1, var2 + var5 * var7, var6);
         }

         this.reset();
         return this.outputSizeBytes;
      }
   }

   private void output(long var1, byte[] var3, int var4, int var5) {
      byte[] var6 = new byte[8];
      Pack.longToLittleEndian(var1, var6, 0);
      long[] var7 = new long[this.chain.length];
      this.ubiInit(63);
      this.ubi.update(var6, 0, var6.length, var7);
      this.ubi.doFinal(var7);
      int var8 = (var5 + 8 - 1) / 8;

      for (int var9 = 0; var9 < var8; var9++) {
         int var10 = Math.min(8, var5 - var9 * 8);
         if (var10 == 8) {
            Pack.longToLittleEndian(var7[var9], var3, var4 + var9 * 8);
         } else {
            Pack.longToLittleEndian(var7[var9], var6, 0);
            System.arraycopy(var6, 0, var3, var4 + var9 * 8, var10);
         }
      }
   }

   static {
      initialState(256, 128, new long[]{-2228972824489528736L, -8629553674646093540L, 1155188648486244218L, -3677226592081559102L});
      initialState(256, 160, new long[]{1450197650740764312L, 3081844928540042640L, -3136097061834271170L, 3301952811952417661L});
      initialState(256, 224, new long[]{-4176654842910610933L, -8688192972455077604L, -7364642305011795836L, 4056579644589979102L});
      initialState(256, 256, new long[]{-243853671043386295L, 3443677322885453875L, -5531612722399640561L, 7662005193972177513L});
      initialState(
         512,
         128,
         new long[]{
            -6288014694233956526L,
            2204638249859346602L,
            3502419045458743507L,
            -4829063503441264548L,
            983504137758028059L,
            1880512238245786339L,
            -6715892782214108542L,
            7602827311880509485L
         }
      );
      initialState(
         512,
         160,
         new long[]{
            2934123928682216849L,
            -4399710721982728305L,
            1684584802963255058L,
            5744138295201861711L,
            2444857010922934358L,
            -2807833639722848072L,
            -5121587834665610502L,
            118355523173251694L
         }
      );
      initialState(
         512,
         224,
         new long[]{
            -3688341020067007964L,
            -3772225436291745297L,
            -8300862168937575580L,
            4146387520469897396L,
            1106145742801415120L,
            7455425944880474941L,
            -7351063101234211863L,
            -7048981346965512457L
         }
      );
      initialState(
         512,
         384,
         new long[]{
            -6631894876634615969L,
            -5692838220127733084L,
            -7099962856338682626L,
            -2911352911530754598L,
            2000907093792408677L,
            9140007292425499655L,
            6093301768906360022L,
            2769176472213098488L
         }
      );
      initialState(
         512,
         512,
         new long[]{
            5261240102383538638L,
            978932832955457283L,
            -8083517948103779378L,
            -7339365279355032399L,
            6752626034097301424L,
            -1531723821829733388L,
            -7417126464950782685L,
            -5901786942805128141L
         }
      );
   }

   private static class Configuration {
      private byte[] bytes = new byte[32];

      public Configuration(long var1) {
         this.bytes[0] = 83;
         this.bytes[1] = 72;
         this.bytes[2] = 65;
         this.bytes[3] = 51;
         this.bytes[4] = 1;
         this.bytes[5] = 0;
         Pack.longToLittleEndian(var1, this.bytes, 8);
      }

      public byte[] getBytes() {
         return this.bytes;
      }
   }

   public static class Parameter {
      private int type;
      private byte[] value;

      public Parameter(int var1, byte[] var2) {
         this.type = var1;
         this.value = var2;
      }

      public int getType() {
         return this.type;
      }

      public byte[] getValue() {
         return this.value;
      }
   }

   private class UBI {
      private final SkeinEngine.UbiTweak tweak = new SkeinEngine.UbiTweak();
      private byte[] currentBlock;
      private int currentOffset;
      private long[] message;

      public UBI(int nullx) {
         this.currentBlock = new byte[nullx];
         this.message = new long[this.currentBlock.length / 8];
      }

      public void reset(SkeinEngine.UBI var1) {
         this.currentBlock = Arrays.clone(var1.currentBlock, this.currentBlock);
         this.currentOffset = var1.currentOffset;
         this.message = Arrays.clone(var1.message, this.message);
         this.tweak.reset(var1.tweak);
      }

      public void reset(int var1) {
         this.tweak.reset();
         this.tweak.setType(var1);
         this.currentOffset = 0;
      }

      public void update(byte[] var1, int var2, int var3, long[] var4) {
         int var5 = 0;

         while (var3 > var5) {
            if (this.currentOffset == this.currentBlock.length) {
               this.processBlock(var4);
               this.tweak.setFirst(false);
               this.currentOffset = 0;
            }

            int var6 = Math.min(var3 - var5, this.currentBlock.length - this.currentOffset);
            System.arraycopy(var1, var2 + var5, this.currentBlock, this.currentOffset, var6);
            var5 += var6;
            this.currentOffset += var6;
            this.tweak.advancePosition(var6);
         }
      }

      private void processBlock(long[] var1) {
         SkeinEngine.this.threefish.init(true, SkeinEngine.this.chain, this.tweak.getWords());
         Pack.littleEndianToLong(this.currentBlock, 0, this.message);
         SkeinEngine.this.threefish.processBlock(this.message, var1);

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] ^= this.message[var2];
         }
      }

      public void doFinal(long[] var1) {
         for (int var2 = this.currentOffset; var2 < this.currentBlock.length; var2++) {
            this.currentBlock[var2] = 0;
         }

         this.tweak.setFinal(true);
         this.processBlock(var1);
      }
   }

   private static class UbiTweak {
      private static final long LOW_RANGE = 9223372034707292160L;
      private static final long T1_FINAL = Long.MIN_VALUE;
      private static final long T1_FIRST = 4611686018427387904L;
      private long[] tweak = new long[2];
      private boolean extendedPosition;

      public UbiTweak() {
         this.reset();
      }

      public void reset(SkeinEngine.UbiTweak var1) {
         this.tweak = Arrays.clone(var1.tweak, this.tweak);
         this.extendedPosition = var1.extendedPosition;
      }

      public void reset() {
         this.tweak[0] = 0L;
         this.tweak[1] = 0L;
         this.extendedPosition = false;
         this.setFirst(true);
      }

      public void setType(int var1) {
         this.tweak[1] = this.tweak[1] & -274877906944L | (var1 & 63L) << 56;
      }

      public int getType() {
         return (int)(this.tweak[1] >>> 56 & 63L);
      }

      public void setFirst(boolean var1) {
         if (var1) {
            this.tweak[1] = this.tweak[1] | 4611686018427387904L;
         } else {
            this.tweak[1] = this.tweak[1] & -4611686018427387905L;
         }
      }

      public boolean isFirst() {
         return (this.tweak[1] & 4611686018427387904L) != 0L;
      }

      public void setFinal(boolean var1) {
         if (var1) {
            this.tweak[1] = this.tweak[1] | Long.MIN_VALUE;
         } else {
            this.tweak[1] = this.tweak[1] & Long.MAX_VALUE;
         }
      }

      public boolean isFinal() {
         return (this.tweak[1] & Long.MIN_VALUE) != 0L;
      }

      public void advancePosition(int var1) {
         if (this.extendedPosition) {
            long[] var2 = new long[]{this.tweak[0] & 4294967295L, this.tweak[0] >>> 32 & 4294967295L, this.tweak[1] & 4294967295L};
            long var3 = var1;

            for (int var5 = 0; var5 < var2.length; var5++) {
               var3 += var2[var5];
               var2[var5] = var3;
               var3 >>>= 32;
            }

            this.tweak[0] = (var2[1] & 4294967295L) << 32 | var2[0] & 4294967295L;
            this.tweak[1] = this.tweak[1] & -4294967296L | var2[2] & 4294967295L;
         } else {
            long var6 = this.tweak[0];
            var6 += var1;
            this.tweak[0] = var6;
            if (var6 > 9223372034707292160L) {
               this.extendedPosition = true;
            }
         }
      }

      public long[] getWords() {
         return this.tweak;
      }

      @Override
      public String toString() {
         return this.getType() + " first: " + this.isFirst() + ", final: " + this.isFinal();
      }
   }
}
