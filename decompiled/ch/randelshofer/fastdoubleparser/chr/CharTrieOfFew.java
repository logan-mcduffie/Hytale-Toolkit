package ch.randelshofer.fastdoubleparser.chr;

import java.util.Set;

final class CharTrieOfFew implements CharTrie {
   private CharTrieNode root = new CharTrieNode();

   public CharTrieOfFew(Set<String> set) {
      for (String str : set) {
         if (!str.isEmpty()) {
            this.add(str);
         }
      }
   }

   private void add(String str) {
      CharTrieNode node = this.root;

      for (int i = 0; i < str.length(); i++) {
         node = node.insert(str.charAt(i));
      }

      node.setEnd();
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
}
