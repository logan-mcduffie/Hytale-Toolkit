package ch.randelshofer.fastdoubleparser.chr;

import java.util.Collection;

final class CharToIntMap implements CharDigitSet, CharSet {
   private CharToIntMap.Node[] table;

   public CharToIntMap(Collection<Character> chars) {
      this(chars.size());
      int i = 0;

      for (char ch : chars) {
         this.put(ch, i++);
      }
   }

   @Override
   public boolean containsKey(char key) {
      return this.getOrDefault(key, -1) >= 0;
   }

   @Override
   public int toDigit(char ch) {
      return this.getOrDefault(ch, 10);
   }

   public CharToIntMap(int maxSize) {
      int n = (-1 >>> Integer.numberOfLeadingZeros(maxSize * 2)) + 1;
      this.table = new CharToIntMap.Node[n];
   }

   public void put(char key, int value) {
      int index = this.getIndex(key);
      CharToIntMap.Node found = this.table[index];
      if (found == null) {
         this.table[index] = new CharToIntMap.Node(key, value);
      } else {
         while (found.next != null && found.key != key) {
            found = found.next;
         }

         if (found.key == key) {
            found.value = value;
         } else {
            found.next = new CharToIntMap.Node(key, value);
         }
      }
   }

   private int getIndex(char key) {
      return key & this.table.length - 1;
   }

   public int getOrDefault(char key, int defaultValue) {
      int index = this.getIndex(key);

      for (CharToIntMap.Node found = this.table[index]; found != null; found = found.next) {
         if (found.key == key) {
            return found.value;
         }
      }

      return defaultValue;
   }

   private static class Node {
      char key;
      int value;
      CharToIntMap.Node next;

      public Node(char key, int value) {
         this.key = key;
         this.value = value;
      }
   }
}
