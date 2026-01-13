package io.netty.buffer.search;

import io.netty.util.internal.PlatformDependent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public class AhoCorasicSearchProcessorFactory extends AbstractMultiSearchProcessorFactory {
   private final int[] jumpTable;
   private final int[] matchForNeedleId;
   static final int BITS_PER_SYMBOL = 8;
   static final int ALPHABET_SIZE = 256;

   AhoCorasicSearchProcessorFactory(byte[]... needles) {
      for (byte[] needle : needles) {
         if (needle.length == 0) {
            throw new IllegalArgumentException("Needle must be non empty");
         }
      }

      AhoCorasicSearchProcessorFactory.Context context = buildTrie(needles);
      this.jumpTable = context.jumpTable;
      this.matchForNeedleId = context.matchForNeedleId;
      this.linkSuffixes();

      for (int i = 0; i < this.jumpTable.length; i++) {
         if (this.matchForNeedleId[this.jumpTable[i] >> 8] >= 0) {
            this.jumpTable[i] = -this.jumpTable[i];
         }
      }
   }

   private static AhoCorasicSearchProcessorFactory.Context buildTrie(byte[][] needles) {
      ArrayList<Integer> jumpTableBuilder = new ArrayList<>(256);

      for (int i = 0; i < 256; i++) {
         jumpTableBuilder.add(-1);
      }

      ArrayList<Integer> matchForBuilder = new ArrayList<>();
      matchForBuilder.add(-1);

      for (int needleId = 0; needleId < needles.length; needleId++) {
         byte[] needle = needles[needleId];
         int currentPosition = 0;

         for (byte ch0 : needle) {
            int ch = ch0 & 255;
            int next = currentPosition + ch;
            if (jumpTableBuilder.get(next) == -1) {
               jumpTableBuilder.set(next, jumpTableBuilder.size());

               for (int i = 0; i < 256; i++) {
                  jumpTableBuilder.add(-1);
               }

               matchForBuilder.add(-1);
            }

            currentPosition = jumpTableBuilder.get(next);
         }

         matchForBuilder.set(currentPosition >> 8, needleId);
      }

      AhoCorasicSearchProcessorFactory.Context context = new AhoCorasicSearchProcessorFactory.Context();
      context.jumpTable = new int[jumpTableBuilder.size()];

      for (int i = 0; i < jumpTableBuilder.size(); i++) {
         context.jumpTable[i] = jumpTableBuilder.get(i);
      }

      context.matchForNeedleId = new int[matchForBuilder.size()];

      for (int i = 0; i < matchForBuilder.size(); i++) {
         context.matchForNeedleId[i] = matchForBuilder.get(i);
      }

      return context;
   }

   private void linkSuffixes() {
      Queue<Integer> queue = new ArrayDeque<>();
      queue.add(0);
      int[] suffixLinks = new int[this.matchForNeedleId.length];
      Arrays.fill(suffixLinks, -1);

      while (!queue.isEmpty()) {
         int v = queue.remove();
         int vPosition = v >> 8;
         int u = suffixLinks[vPosition] == -1 ? 0 : suffixLinks[vPosition];
         if (this.matchForNeedleId[vPosition] == -1) {
            this.matchForNeedleId[vPosition] = this.matchForNeedleId[u >> 8];
         }

         for (int ch = 0; ch < 256; ch++) {
            int vIndex = v | ch;
            int uIndex = u | ch;
            int jumpV = this.jumpTable[vIndex];
            int jumpU = this.jumpTable[uIndex];
            if (jumpV != -1) {
               suffixLinks[jumpV >> 8] = v > 0 && jumpU != -1 ? jumpU : 0;
               queue.add(jumpV);
            } else {
               this.jumpTable[vIndex] = jumpU != -1 ? jumpU : 0;
            }
         }
      }
   }

   public AhoCorasicSearchProcessorFactory.Processor newSearchProcessor() {
      return new AhoCorasicSearchProcessorFactory.Processor(this.jumpTable, this.matchForNeedleId);
   }

   private static class Context {
      int[] jumpTable;
      int[] matchForNeedleId;

      private Context() {
      }
   }

   public static class Processor implements MultiSearchProcessor {
      private final int[] jumpTable;
      private final int[] matchForNeedleId;
      private long currentPosition;

      Processor(int[] jumpTable, int[] matchForNeedleId) {
         this.jumpTable = jumpTable;
         this.matchForNeedleId = matchForNeedleId;
      }

      @Override
      public boolean process(byte value) {
         this.currentPosition = PlatformDependent.getInt(this.jumpTable, this.currentPosition | value & 255L);
         if (this.currentPosition < 0L) {
            this.currentPosition = -this.currentPosition;
            return false;
         } else {
            return true;
         }
      }

      @Override
      public int getFoundNeedleId() {
         return this.matchForNeedleId[(int)this.currentPosition >> 8];
      }

      @Override
      public void reset() {
         this.currentPosition = 0L;
      }
   }
}
