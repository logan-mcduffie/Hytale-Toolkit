package io.netty.handler.codec.compression;

final class Bzip2HuffmanStageDecoder {
   private final Bzip2BitReader reader;
   byte[] selectors;
   private final int[] minimumLengths;
   private final int[][] codeBases;
   private final int[][] codeLimits;
   private final int[][] codeSymbols;
   private int currentTable;
   private int groupIndex = -1;
   private int groupPosition = -1;
   final int totalTables;
   final int alphabetSize;
   final Bzip2MoveToFrontTable tableMTF = new Bzip2MoveToFrontTable();
   int currentSelector;
   final byte[][] tableCodeLengths;
   int currentGroup;
   int currentLength = -1;
   int currentAlpha;
   boolean modifyLength;

   Bzip2HuffmanStageDecoder(Bzip2BitReader reader, int totalTables, int alphabetSize) {
      this.reader = reader;
      this.totalTables = totalTables;
      this.alphabetSize = alphabetSize;
      this.minimumLengths = new int[totalTables];
      this.codeBases = new int[totalTables][25];
      this.codeLimits = new int[totalTables][24];
      this.codeSymbols = new int[totalTables][258];
      this.tableCodeLengths = new byte[totalTables][258];
   }

   void createHuffmanDecodingTables() {
      int alphabetSize = this.alphabetSize;

      for (int table = 0; table < this.tableCodeLengths.length; table++) {
         int[] tableBases = this.codeBases[table];
         int[] tableLimits = this.codeLimits[table];
         int[] tableSymbols = this.codeSymbols[table];
         byte[] codeLengths = this.tableCodeLengths[table];
         int minimumLength = 23;
         int maximumLength = 0;

         for (int i = 0; i < alphabetSize; i++) {
            byte currLength = codeLengths[i];
            maximumLength = Math.max(currLength, maximumLength);
            minimumLength = Math.min(currLength, minimumLength);
         }

         this.minimumLengths[table] = minimumLength;

         for (int i = 0; i < alphabetSize; i++) {
            tableBases[codeLengths[i] + 1]++;
         }

         int i = 1;

         for (int b = tableBases[0]; i < 25; i++) {
            b += tableBases[i];
            tableBases[i] = b;
         }

         i = minimumLength;

         for (int code = 0; i <= maximumLength; i++) {
            int var18 = code + (tableBases[i + 1] - tableBases[i]);
            tableBases[i] = code - tableBases[i];
            tableLimits[i] = var18 - 1;
            code = var18 << 1;
         }

         i = minimumLength;

         for (int codeIndex = 0; i <= maximumLength; i++) {
            for (int symbol = 0; symbol < alphabetSize; symbol++) {
               if (codeLengths[symbol] == i) {
                  tableSymbols[codeIndex++] = symbol;
               }
            }
         }
      }

      this.currentTable = this.selectors[0];
   }

   int nextSymbol() {
      if (++this.groupPosition % 50 == 0) {
         this.groupIndex++;
         if (this.groupIndex == this.selectors.length) {
            throw new DecompressionException("error decoding block");
         }

         this.currentTable = this.selectors[this.groupIndex] & 255;
      }

      Bzip2BitReader reader = this.reader;
      int currentTable = this.currentTable;
      int[] tableLimits = this.codeLimits[currentTable];
      int[] tableBases = this.codeBases[currentTable];
      int[] tableSymbols = this.codeSymbols[currentTable];
      int codeLength = this.minimumLengths[currentTable];

      for (int codeBits = reader.readBits(codeLength); codeLength <= 23; codeLength++) {
         if (codeBits <= tableLimits[codeLength]) {
            return tableSymbols[codeBits - tableBases[codeLength]];
         }

         codeBits = codeBits << 1 | reader.readBits(1);
      }

      throw new DecompressionException("a valid code was not recognised");
   }
}
