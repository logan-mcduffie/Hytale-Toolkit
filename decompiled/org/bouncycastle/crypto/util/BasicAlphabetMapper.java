package org.bouncycastle.crypto.util;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AlphabetMapper;

public class BasicAlphabetMapper implements AlphabetMapper {
   private Map<Character, Integer> indexMap = new HashMap<>();
   private Map<Integer, Character> charMap = new HashMap<>();

   public BasicAlphabetMapper(String var1) {
      this(var1.toCharArray());
   }

   public BasicAlphabetMapper(char[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         if (this.indexMap.containsKey(var1[var2])) {
            throw new IllegalArgumentException("duplicate key detected in alphabet: " + var1[var2]);
         }

         this.indexMap.put(var1[var2], var2);
         this.charMap.put(var2, var1[var2]);
      }
   }

   @Override
   public int getRadix() {
      return this.indexMap.size();
   }

   @Override
   public byte[] convertToIndexes(char[] var1) {
      byte[] var2;
      if (this.indexMap.size() <= 256) {
         var2 = new byte[var1.length];

         for (int var3 = 0; var3 != var1.length; var3++) {
            var2[var3] = this.indexMap.get(var1[var3]).byteValue();
         }
      } else {
         var2 = new byte[var1.length * 2];

         for (int var5 = 0; var5 != var1.length; var5++) {
            int var4 = this.indexMap.get(var1[var5]);
            var2[var5 * 2] = (byte)(var4 >> 8 & 0xFF);
            var2[var5 * 2 + 1] = (byte)(var4 & 0xFF);
         }
      }

      return var2;
   }

   @Override
   public char[] convertToChars(byte[] var1) {
      char[] var2;
      if (this.charMap.size() <= 256) {
         var2 = new char[var1.length];

         for (int var3 = 0; var3 != var1.length; var3++) {
            var2[var3] = this.charMap.get(var1[var3] & 255);
         }
      } else {
         if ((var1.length & 1) != 0) {
            throw new IllegalArgumentException("two byte radix and input string odd length");
         }

         var2 = new char[var1.length / 2];

         for (byte var4 = 0; var4 != var1.length; var4 += 2) {
            var2[var4 / 2] = this.charMap.get(var1[var4] << 8 & 0xFF00 | var1[var4 + 1] & 255);
         }
      }

      return var2;
   }
}
