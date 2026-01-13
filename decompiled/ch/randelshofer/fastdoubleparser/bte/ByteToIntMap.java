package ch.randelshofer.fastdoubleparser.bte;

import java.util.Collection;

final class ByteToIntMap implements ByteDigitSet, ByteSet {
   private ByteToIntMap.Node[] table;

   public ByteToIntMap(Collection<Character> chars) {
      this(chars.size());
      int i = 0;

      for (char ch : chars) {
         if (ch > 127) {
            throw new IllegalArgumentException("can not map to a single byte. ch=" + ch);
         }

         this.put((byte)ch, i++);
      }
   }

   @Override
   public boolean containsKey(byte b) {
      return this.getOrDefault(b, -1) >= 0;
   }

   @Override
   public int toDigit(byte ch) {
      return this.getOrDefault(ch, 10);
   }

   public ByteToIntMap(int maxSize) {
      int n = (-1 >>> Integer.numberOfLeadingZeros(maxSize * 2)) + 1;
      this.table = new ByteToIntMap.Node[n];
   }

   public void put(byte key, int value) {
      int index = this.getIndex(key);
      ByteToIntMap.Node found = this.table[index];
      if (found == null) {
         this.table[index] = new ByteToIntMap.Node(key, value);
      } else {
         while (found.next != null && found.key != key) {
            found = found.next;
         }

         if (found.key == key) {
            found.value = value;
         } else {
            found.next = new ByteToIntMap.Node(key, value);
         }
      }
   }

   private int getIndex(byte key) {
      return key & this.table.length - 1;
   }

   public int getOrDefault(byte key, int defaultValue) {
      int index = this.getIndex(key);

      for (ByteToIntMap.Node found = this.table[index]; found != null; found = found.next) {
         if (found.key == key) {
            return found.value;
         }
      }

      return defaultValue;
   }

   private static class Node {
      byte key;
      int value;
      ByteToIntMap.Node next;

      public Node(byte key, int value) {
         this.key = key;
         this.value = value;
      }
   }
}
