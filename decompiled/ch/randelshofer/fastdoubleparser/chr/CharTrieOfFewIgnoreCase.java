package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

final class CharTrieOfFewIgnoreCase implements CharTrie {
   private CharTrieNode root = new CharTrieNode();

   public CharTrieOfFewIgnoreCase(Set<String> set) {
      for (String str : set) {
         if (!str.isEmpty()) {
            this.add(str);
         }
      }
   }

   private void add(String str) {
      CharTrieNode upperNode = this.root;
      CharTrieNode lowerNode = this.root;
      String upperStr = str.toUpperCase();
      String lowerStr = upperStr.toLowerCase();

      for (int i = 0; i < str.length(); i++) {
         char upper = upperStr.charAt(i);
         char lower = lowerStr.charAt(i);
         upperNode = upperNode.insert(upper);
         lowerNode = lowerNode.insert(lower, upperNode);
      }

      upperNode.setEnd();
   }

   @Override
   public int match(char[] str, int startIndex, int endIndex) {
      CharTrieNode node = this.root;
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

   @Override
   public int match(CharSequence str, int startIndex, int endIndex) {
      CharTrieNode node = this.root;
      int longestMatch = startIndex;

      for (int i = startIndex; i < endIndex; i++) {
         node = node.get(str.charAt(i));
         if (node == null) {
            break;
         }

         longestMatch = node.isEnd() ? i + 1 : longestMatch;
      }

      return longestMatch - startIndex;
   }
}
