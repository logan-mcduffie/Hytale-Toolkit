package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;

final class Bzip2HuffmanStageEncoder {
   private static final int HUFFMAN_HIGH_SYMBOL_COST = 15;
   private final Bzip2BitWriter writer;
   private final char[] mtfBlock;
   private final int mtfLength;
   private final int mtfAlphabetSize;
   private final int[] mtfSymbolFrequencies;
   private final int[][] huffmanCodeLengths;
   private final int[][] huffmanMergedCodeSymbols;
   private final byte[] selectors;

   Bzip2HuffmanStageEncoder(Bzip2BitWriter writer, char[] mtfBlock, int mtfLength, int mtfAlphabetSize, int[] mtfSymbolFrequencies) {
      this.writer = writer;
      this.mtfBlock = mtfBlock;
      this.mtfLength = mtfLength;
      this.mtfAlphabetSize = mtfAlphabetSize;
      this.mtfSymbolFrequencies = mtfSymbolFrequencies;
      int totalTables = selectTableCount(mtfLength);
      this.huffmanCodeLengths = new int[totalTables][mtfAlphabetSize];
      this.huffmanMergedCodeSymbols = new int[totalTables][mtfAlphabetSize];
      this.selectors = new byte[(mtfLength + 50 - 1) / 50];
   }

   private static int selectTableCount(int mtfLength) {
      if (mtfLength >= 2400) {
         return 6;
      } else if (mtfLength >= 1200) {
         return 5;
      } else if (mtfLength >= 600) {
         return 4;
      } else {
         return mtfLength >= 200 ? 3 : 2;
      }
   }

   private static void generateHuffmanCodeLengths(int alphabetSize, int[] symbolFrequencies, int[] codeLengths) {
      int[] mergedFrequenciesAndIndices = new int[alphabetSize];
      int[] sortedFrequencies = new int[alphabetSize];

      for (int i = 0; i < alphabetSize; i++) {
         mergedFrequenciesAndIndices[i] = symbolFrequencies[i] << 9 | i;
      }

      Arrays.sort(mergedFrequenciesAndIndices);

      for (int i = 0; i < alphabetSize; i++) {
         sortedFrequencies[i] = mergedFrequenciesAndIndices[i] >>> 9;
      }

      Bzip2HuffmanAllocator.allocateHuffmanCodeLengths(sortedFrequencies, 20);

      for (int i = 0; i < alphabetSize; i++) {
         codeLengths[mergedFrequenciesAndIndices[i] & 511] = sortedFrequencies[i];
      }
   }

   private void generateHuffmanOptimisationSeeds() {
      int[][] huffmanCodeLengths = this.huffmanCodeLengths;
      int[] mtfSymbolFrequencies = this.mtfSymbolFrequencies;
      int mtfAlphabetSize = this.mtfAlphabetSize;
      int totalTables = huffmanCodeLengths.length;
      int remainingLength = this.mtfLength;
      int lowCostEnd = -1;

      for (int i = 0; i < totalTables; i++) {
         int targetCumulativeFrequency = remainingLength / (totalTables - i);
         int lowCostStart = lowCostEnd + 1;
         int actualCumulativeFrequency = 0;

         while (actualCumulativeFrequency < targetCumulativeFrequency && lowCostEnd < mtfAlphabetSize - 1) {
            actualCumulativeFrequency += mtfSymbolFrequencies[++lowCostEnd];
         }

         if (lowCostEnd > lowCostStart && i != 0 && i != totalTables - 1 && (totalTables - i & 1) == 0) {
            actualCumulativeFrequency -= mtfSymbolFrequencies[lowCostEnd--];
         }

         int[] tableCodeLengths = huffmanCodeLengths[i];

         for (int j = 0; j < mtfAlphabetSize; j++) {
            if (j < lowCostStart || j > lowCostEnd) {
               tableCodeLengths[j] = 15;
            }
         }

         remainingLength -= actualCumulativeFrequency;
      }
   }

   private void optimiseSelectorsAndHuffmanTables(boolean storeSelectors) {
      char[] mtfBlock = this.mtfBlock;
      byte[] selectors = this.selectors;
      int[][] huffmanCodeLengths = this.huffmanCodeLengths;
      int mtfLength = this.mtfLength;
      int mtfAlphabetSize = this.mtfAlphabetSize;
      int totalTables = huffmanCodeLengths.length;
      int[][] tableFrequencies = new int[totalTables][mtfAlphabetSize];
      int selectorIndex = 0;
      int groupStart = 0;

      while (groupStart < mtfLength) {
         int groupEnd = Math.min(groupStart + 50, mtfLength) - 1;
         int[] cost = new int[totalTables];

         for (int i = groupStart; i <= groupEnd; i++) {
            int value = mtfBlock[i];

            for (int j = 0; j < totalTables; j++) {
               cost[j] += huffmanCodeLengths[j][value];
            }
         }

         byte bestTable = 0;
         int bestCost = cost[0];

         for (byte i = 1; i < totalTables; i++) {
            int tableCost = cost[i];
            if (tableCost < bestCost) {
               bestCost = tableCost;
               bestTable = i;
            }
         }

         int[] bestGroupFrequencies = tableFrequencies[bestTable];

         for (int ix = groupStart; ix <= groupEnd; ix++) {
            bestGroupFrequencies[mtfBlock[ix]]++;
         }

         if (storeSelectors) {
            selectors[selectorIndex++] = bestTable;
         }

         groupStart = groupEnd + 1;
      }

      for (int ix = 0; ix < totalTables; ix++) {
         generateHuffmanCodeLengths(mtfAlphabetSize, tableFrequencies[ix], huffmanCodeLengths[ix]);
      }
   }

   private void assignHuffmanCodeSymbols() {
      int[][] huffmanMergedCodeSymbols = this.huffmanMergedCodeSymbols;
      int[][] huffmanCodeLengths = this.huffmanCodeLengths;
      int mtfAlphabetSize = this.mtfAlphabetSize;
      int totalTables = huffmanCodeLengths.length;

      for (int i = 0; i < totalTables; i++) {
         int[] tableLengths = huffmanCodeLengths[i];
         int minimumLength = 32;
         int maximumLength = 0;

         for (int j = 0; j < mtfAlphabetSize; j++) {
            int length = tableLengths[j];
            if (length > maximumLength) {
               maximumLength = length;
            }

            if (length < minimumLength) {
               minimumLength = length;
            }
         }

         int code = 0;

         for (int j = minimumLength; j <= maximumLength; j++) {
            for (int k = 0; k < mtfAlphabetSize; k++) {
               if ((huffmanCodeLengths[i][k] & 0xFF) == j) {
                  huffmanMergedCodeSymbols[i][k] = j << 24 | code;
                  code++;
               }
            }

            code <<= 1;
         }
      }
   }

   private void writeSelectorsAndHuffmanTables(ByteBuf out) {
      Bzip2BitWriter writer = this.writer;
      byte[] selectors = this.selectors;
      int totalSelectors = selectors.length;
      int[][] huffmanCodeLengths = this.huffmanCodeLengths;
      int totalTables = huffmanCodeLengths.length;
      int mtfAlphabetSize = this.mtfAlphabetSize;
      writer.writeBits(out, 3, totalTables);
      writer.writeBits(out, 15, totalSelectors);
      Bzip2MoveToFrontTable selectorMTF = new Bzip2MoveToFrontTable();

      for (byte selector : selectors) {
         writer.writeUnary(out, selectorMTF.valueToFront(selector));
      }

      for (int[] tableLengths : huffmanCodeLengths) {
         int currentLength = tableLengths[0];
         writer.writeBits(out, 5, currentLength);

         for (int j = 0; j < mtfAlphabetSize; j++) {
            int codeLength = tableLengths[j];
            int value = currentLength < codeLength ? 2 : 3;
            int delta = Math.abs(codeLength - currentLength);

            while (delta-- > 0) {
               writer.writeBits(out, 2, value);
            }

            writer.writeBoolean(out, false);
            currentLength = codeLength;
         }
      }
   }

   private void writeBlockData(ByteBuf out) {
      Bzip2BitWriter writer = this.writer;
      int[][] huffmanMergedCodeSymbols = this.huffmanMergedCodeSymbols;
      byte[] selectors = this.selectors;
      int mtfLength = this.mtfLength;
      int selectorIndex = 0;
      int mtfIndex = 0;

      while (mtfIndex < mtfLength) {
         int groupEnd = Math.min(mtfIndex + 50, mtfLength) - 1;
         int[] tableMergedCodeSymbols = huffmanMergedCodeSymbols[selectors[selectorIndex++]];

         while (mtfIndex <= groupEnd) {
            int mergedCodeSymbol = tableMergedCodeSymbols[this.mtfBlock[mtfIndex++]];
            writer.writeBits(out, mergedCodeSymbol >>> 24, mergedCodeSymbol);
         }
      }
   }

   void encode(ByteBuf out) {
      this.generateHuffmanOptimisationSeeds();

      for (int i = 3; i >= 0; i--) {
         this.optimiseSelectorsAndHuffmanTables(i == 0);
      }

      this.assignHuffmanCodeSymbols();
      this.writeSelectorsAndHuffmanTables(out);
      this.writeBlockData(out);
   }
}
