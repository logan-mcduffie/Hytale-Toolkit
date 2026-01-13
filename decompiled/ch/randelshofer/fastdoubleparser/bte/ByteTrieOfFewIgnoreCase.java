package ch.randelshofer.fastdoubleparser.bte;

import java.nio.charset.StandardCharsets;
import java.util.Set;

final class ByteTrieOfFewIgnoreCase implements ByteTrie {
   private ByteTrieNode root = new ByteTrieNode();

   public ByteTrieOfFewIgnoreCase(Set<String> set) {
      for (String str : set) {
         if (!str.isEmpty()) {
            this.add(str);
         }
      }
   }

   private void add(String str) {
      ByteTrieNode upperNode = this.root;
      ByteTrieNode lowerNode = this.root;
      String upperStr = str.toUpperCase();
      String lowerStr = upperStr.toLowerCase();

      for (int i = 0; i < str.length(); i++) {
         byte[] upper = upperStr.substring(i, i + 1).getBytes(StandardCharsets.UTF_8);
         byte[] lower = lowerStr.substring(i, i + 1).getBytes(StandardCharsets.UTF_8);

         for (int u = 0; u < upper.length; u++) {
            upperNode = upperNode.insert(upper[u]);
         }

         for (int l = 0; l < upper.length - 1; l++) {
            lowerNode = lowerNode.insert(lower[l]);
         }

         lowerNode = lowerNode.insert(lower[lower.length - 1], upperNode);
      }

      upperNode.setEnd();
   }

   @Override
   public int match(byte[] str, int startIndex, int endIndex) {
      ByteTrieNode node = this.root;
      int longestMatch = startIndex;

      for (int i = startIndex; i < endIndex; i++) {
         node = node.get(str[i]);
         if (node == null) {
            break;
         }

         longestMatch = node.isEnd() ? i + 1 : longestMatch;
      }

      return longestMatch - startIndex;
   }
}
